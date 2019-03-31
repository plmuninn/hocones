package pl.muninn.hocones.md.document.renderer

import cats.implicits._
import pl.muninn.scalamdtag._
import pl.muninn.scalamdtag.tags.Markdown
import pl.muninn.hocones.meta.document.model.Document
import pl.muninn.hocones.parser.entity.simple.{EnvironmentValue, NotResolvedRef, ResolvedRef}

trait CommonRenderingOps {

  protected def title(document: Document): Markdown = h2(document.path)

  protected def name(document: Document): Markdown =
    frag(b("Name:"), document.name)

  protected def description(document: Document): Option[Markdown] =
    document.description.map(value => frag(b("Description:"), value))

  protected def packageName(document: Document): Markdown =
    frag(b("Package:"), document.packageName)

  protected def from(document: Document): Option[Markdown] =
    document.from.map(from => frag(b("From file:"), from))

  protected def typeName(document: Document): Markdown =
    frag(b("Type of configuration:"), document.typeName.toString)

  protected def details(document: Document): Option[Markdown] =
    if (document.details.isEmpty) None
    else
      Some {
        frag(
          b("Details:"),
          ul(
            document.details.map {
              case (key, value) => frag(b(s"$key:"), value)
            }
          )
        )
      }

  protected def template(document: Document)(body: Markdown): Markdown = {
    val titleMarkdown: Markdown = title(document)
    val nameMarkdown: Markdown = name(document) + br
    val typeMarkdown: Markdown = typeName(document)

    val fromFileMarkdownMaybe: Option[Markdown] = from(document).map(_ + br)
    val descriptionMarkdownMaybe: Option[Markdown] = description(document).map(_ + br)
    val bodyMarkdown: Markdown = body + br
    val packageMarkdown: Markdown = packageName(document) + br
    val detailsMaybeMarkdown: Option[Markdown] = details(document)

    p(
      List(
        titleMarkdown.pure[Option],
        nameMarkdown.pure[Option],
        fromFileMarkdownMaybe
          .orElse(descriptionMarkdownMaybe)
          .map(_ => typeMarkdown + br)
          .getOrElse(typeMarkdown)
          .pure[Option],
        fromFileMarkdownMaybe,
        descriptionMarkdownMaybe,
        bodyMarkdown.pure[Option],
        packageMarkdown.pure[Option],
        detailsMaybeMarkdown,
      ).flatten
    )
  }
}

object CommonRenderingOps {

  def environmentTable(values: Iterable[EnvironmentValue]): Option[Markdown] =
    if (values.isEmpty) None
    else
      Some {
        frag(
          b("Environment values:"),
          table(
            ("Name", "Is optional"),
            values.map { environmentValue =>
              (environmentValue.name, environmentValue.isOptional.toString)
            }
          )
        )
      }

  def references(values: Iterable[ResolvedRef]): Option[Markdown] =
    if (values.isEmpty) None
    else
      Some {
        frag(
          b("Reference values:"),
          table(
            ("Reference to", "Is optional"),
            values.map { resolvedRef =>
              (resolvedRef.reference.name, resolvedRef.reference.isOptional.toString)
            }
          )
        )
      }

  def unresolvedReferences(values: Iterable[NotResolvedRef]): Option[Markdown] =
    if (values.isEmpty) None
    else
      Some {
        frag(
          b("Not resolved reference values:"),
          table(
            ("Reference to", "Is optional"),
            values.map { reference =>
              (reference.name, reference.isOptional.toString)
            }
          )
        )
      }
}
