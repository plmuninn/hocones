package pl.muninn.hocones.meta

import cats.effect.SyncIO
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import pl.muninn.hocones.common.implicits
import pl.muninn.hocones.meta.BuildInfo.version
import pl.muninn.hocones.meta.error.MetaError
import pl.muninn.hocones.meta.model.{MetaInformation, MetaValue}
import pl.muninn.hocones.parser.HoconResult
import pl.muninn.hocones.parser.entity._
import pl.muninn.hocones.parser.ops.HoconOps._

/**
  * Parser is decomposing structure of configuration with paths like:
  *
  *  Map( pl.muninn.hocones.value -> 1, pl.muninn.test.value -> 2, pl.muninn.hocones.test.value -> 3)
  *
  *  to
  *
  *  Map(
  *    pl.muninn -> Map(
  *       hocones -> Map(
  *         value -> 1
  *       ),
  *       hocones.test -> Map(
  *         value -> 3
  *       ),
  *       test -> Map(
  *         value -> 2
  *       )
  *    )
  *  )
  *
  */
object MetaParser {

  import pl.muninn.hocones.common.implicits._
  import pl.muninn.hocones.meta.codecs._

  private[MetaParser] object InternalMetaParser {

    implicit class PathResultFunctions(pair: (Path, HoconResultValue)) {
      lazy val (path, value) = pair

      def isOrphan: Boolean = path.isOrphan
    }
  }

  import InternalMetaParser._

  /**
    * Method generate roots for structure. For example:
    *  Map( pl.muninn.hocones.value -> 1)
    *
    *  Root will be:
    *
    *  `pl.muninn`
    *
    *  But for:
    *
    *  Map(pl.muninn.hocones.value -> 1, pl.muninn.value -> 2)
    *
    *  Possible root is only `pl`
    *
    */
  private[meta] def generateRoots(hocones: HoconResult): Seq[String] = {
    val paths: Iterable[implicits.Path] = hocones.results.asMap.filterNot(_.isOrphan).keys

    if (paths.isEmpty) Nil
    else // Iterate trough whole list of packages
      paths.foldLeft(Seq.empty[String]) {
        case (acc, path) =>
          val root: Path = path.packageName.dropRight(1)
          val rootChunks = root.splitPath

          // Function check if path passed to it is in same namespace as actually processed path
          // They are the same if first segment is the same
          def inSameNamespace(root: Path): Boolean = {
            val rootHead = root.splitPath.head

            rootChunks.head == rootHead
          }

          // If there is no roots or root not exists in list, just add it
          if (acc.isEmpty || !acc.exists(inSameNamespace(_))) acc :+ root
          else {
            // If exists, we still need to find if this root should not be the new root in this namespace
            // because its for example shorter

            // We are creating list of similar roots to root that we are processing
            val similar: Seq[(String, Int)] = acc.zipWithIndex.filter {
              case (rootPath, _) => inSameNamespace(rootPath)
            }

            // We are mapping each similar root
            val clean: Seq[(Int, String)] =
              similar.map {
                case (rootPath, index) =>
                  val root: Path = rootPath
                  index -> root.splitPath // And we are filtering parts of similar root
                    .filter(part => rootChunks.contains(part)) // Is their contained in root we are actually processing
                    .mkString(".") // Thanks of that, we should achieve most common part of in root
              }

            // We must update our accumulator
            val updated = clean.foldLeft(acc) {
              case (accumulator, (index, rootPath)) => accumulator.updated(index, rootPath)
            }

            // There is chance after process of filtering we will end with empty roots or duplicates - we need to clean them
            val withoutDuplicates = updated.filter(_.nonEmpty).distinct

            withoutDuplicates
          }
      }
  }

  /**
    * This method is generating from list of roots, structure Map(Root, Map(SubPackage -> Seq[MetaValue])). For example:
    *
    * For result:
    *
    *   Map( pl.muninn.hocones.value -> 1)
    *
    * and roots:
    *
    *   Seq( pl.muninn )
    *
    * We should get :
    *
    *   Map(pl.muninn -> Map(hocones -> Seq( MetaNumber(1) ))
    *
    */
  private[meta] def generateMetaValues(
    roots: Seq[String],
    hocones: HoconResult
  ): Map[String, Map[String, Seq[MetaValue]]] = {
    // Create empty accumulator with roots
    val result =
      roots.map(path => path -> Map.empty[String, Seq[MetaValue]]).toMap

    // Get only results that are not orphans
    val withoutOrphans: Map[implicits.Path, HoconResultValue] = hocones.results.asMap.filterNot(_.isOrphan)

    withoutOrphans.foldLeft(result) {
      case (rootsList, (path, value)) =>
        // Fist we are looking for root that is part of path we are processing
        rootsList.find {
          case (key, _) => path.startsWith(key) // Path needs to start with root to be in same namespace
        } match {
          case Some((root: String, subPackages: Map[String, Seq[MetaValue]])) => // When we found root for this path
            val subPackageAccumulator: Map[String, Seq[MetaValue]] =
              subPackages
                .find { // We are checking if path should be added to existing subpackage or part of it should become subpackage
                  case (subKey, _) =>
                    val actualPath = root + "." + subKey // We are creating actual Path - from root.subpackage
                    // We are removing actualPath from path to see how much left
                    val cleared: Path = path.replace(s"$actualPath.", "")
                    // If there left only 1 element - tha means only name of left and it should be added to existing package
                    cleared.splitPath.length == 1
                } match {
                case Some((subPackage: String, metaValueSeq: Seq[MetaValue])) => // Add to existing package
                  subPackages + (subPackage -> (metaValueSeq :+ value.encodeTo[MetaValue]))
                case _ => // Create new subpackage name
                  // To create package name, we are getting only path without name and we are removing root from it
                  val packageName = path.packageName.replace(s"$root.", "")
                  subPackages + (packageName -> Seq(value.encodeTo[MetaValue]))
              }

            rootsList + (root -> subPackageAccumulator) // Update Map
          case _ =>
            // If we didn't found a root, that means generating roots or differencing orphans failed and
            // value shouldn't be here
            throw MetaError("Something went wrong, not root found")
        }
    }

  }

  /**
    * Creates list of "orphans" MetaValues
    *
    * Orphan is a path where there is no root because its too short
    */
  private[meta] def orphans(hocones: HoconResult): Seq[MetaValue] =
    hocones.results
      .filter(_.path.isOrphan)
      .map(_.encodeTo[MetaValue])
      .toList

  private[meta] def roots(hocones: HoconResult): SyncIO[Map[String, Map[String, Seq[MetaValue]]]] =
    for {
      logger <- Slf4jLogger.create[SyncIO]
      rootsKeys = generateRoots(hocones)
      _ <- logger.debug(s"Root keys: ${rootsKeys.mkString(";\n")}")
      generatedMetaValues = generateMetaValues(rootsKeys, hocones)
      filteredMetaValues = generatedMetaValues.filterNot {
        case (_, value) => value.isEmpty
      }
    } yield filteredMetaValues

  def generate(hocones: HoconResult): SyncIO[MetaInformation] =
    for {
      logger <- Slf4jLogger.create[SyncIO]
      rootsResult <- roots(hocones)
      _ <- logger.debug("Roots with results:")
      _ <- logger.debug(pprint.tokenize(rootsResult).mkString(""))
      orphansResult = orphans(hocones)
      _ <- logger.debug("Orphans:")
      _ <- logger.debug(orphansResult.toString())
    } yield MetaInformation.apply(version, rootsResult, orphansResult)
}
