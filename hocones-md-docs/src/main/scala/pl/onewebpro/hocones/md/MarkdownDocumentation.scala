package pl.onewebpro.hocones.md
import cats.effect.SyncIO
import pl.onewebpro.hocones.meta.document.model.Documentation

object MarkdownDocumentation {
  def fromDocumentation(documentation: Documentation): SyncIO[String] = SyncIO("")
}
