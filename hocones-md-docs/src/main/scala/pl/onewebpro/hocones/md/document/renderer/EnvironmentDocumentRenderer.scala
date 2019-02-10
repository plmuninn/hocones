package pl.onewebpro.hocones.md.document.renderer

import pl.onewebpro.hocones.md.document.DocumentToMarkdown
import pl.onewebpro.hocones.meta.document.model.EnvironmentDocument

object EnvironmentDocumentRenderer {

  import pl.muninn.scalamdtag._

  lazy val renderer: DocumentToMarkdown[EnvironmentDocument] = { document =>
    frag(
      frag(b("Environment name:"), document.environmentName),br,
      frag(b("Is optional:"), document.isOptional.toString)
    )
  }
}
