package pl.onewebpro.hocones.md.document.renderer

import pl.muninn.scalamdtag.tags.Markdown
import pl.onewebpro.hocones.meta.document.model.Document

trait CommonRenderingOps {

  import pl.muninn.scalamdtag._

  private val empty = text("")

  protected def title(document: Document[_]): Markdown = h2(document.path)

  protected def name(document: Document[_]): Markdown =
    frag(b("Name:"), document.name)

  protected def description(document: Document[_]): Option[Markdown] =
    document.description.map(value => p(b("Description:"), value))

  protected def packageName(document: Document[_]): Markdown =
    frag(b("Package:"), document.packageName)

  protected def from(document: Document[_]): Option[Markdown] =
    document.from.map(from => p(b("From file:"), from))

  protected def typeName(document: Document[_]): Markdown =
    frag(b("Type of configuration:"), document.typeName.toString)

  protected def details(document: Document[_]): Option[Markdown] =
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

  protected def template(document: Document[_])(body: Markdown): Markdown =
    p(
      title(document),
      name(document),
      br,
      typeName(document),
      br,
      from(document).map(value => frag(value, br)).getOrElse(empty),
      description(document).map(value => frag(value, br)).getOrElse(empty),
      body,
      br,
      packageName(document),
      br,
      details(document).map(value => frag(value, br)).getOrElse(empty)
    )
}
