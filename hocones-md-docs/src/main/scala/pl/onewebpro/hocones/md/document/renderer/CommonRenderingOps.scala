package pl.onewebpro.hocones.md.document.renderer

import pl.muninn.scalamdtag._
import pl.muninn.scalamdtag.tags.Markdown
import pl.onewebpro.hocones.meta.document.model.Document
import pl.onewebpro.hocones.parser.entity.simple.{EnvironmentValue, NotResolvedRef, ResolvedRef}

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
        p(
          b("Details:"),
          ul(
            document.details.map {
              case (key, value) => frag(b(s"$key:"), value)
            }
          )
        )
      }

  protected def template(document: Document)(body: Markdown): Markdown =
    p(
      title(document),
      name(document) + br,
      typeName(document),
      from(document).map(br + _ + br).getOrElse(CommonRenderingOps.empty),
      description(document).map(br + _ + br).getOrElse(CommonRenderingOps.empty),
      body + br,
      packageName(document) + br,
      details(document).getOrElse(CommonRenderingOps.empty)
    )
}

object CommonRenderingOps {

  val empty: Markdown = text("")

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
