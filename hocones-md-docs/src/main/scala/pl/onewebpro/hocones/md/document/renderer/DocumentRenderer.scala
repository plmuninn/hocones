package pl.onewebpro.hocones.md.document.renderer
import pl.onewebpro.hocones.md.document.DocumentToMarkdown
import pl.onewebpro.hocones.meta.document.model.{ArrayDocument, ConcatenationDocument, Document, EnvironmentDocument, MergedValuesDocument, ObjectDocument, ReferenceValueDocument, ResolvedReferenceDocument, ValueDocument}

object DocumentRenderer {

  lazy val renderer: DocumentToMarkdown[Document[_]] = {
    case array: ArrayDocument                 => ArrayDocumentRenderer.renderer.toMd(array)
    case concatenation: ConcatenationDocument => ConcatenationDocumentRenderer.renderer.toMd(concatenation)
    case environment: EnvironmentDocument     => EnvironmentDocumentRenderer.renderer.toMd(environment)
    case merged: MergedValuesDocument         => MergedValuesDocumentRenderer.renderer.toMd(merged)
    case objectDocument: ObjectDocument       => ObjectDocumentRenderer.renderer.toMd(objectDocument)
    case referenceValueDocument: ReferenceValueDocument =>
      ReferenceValueDocumentRenderer.renderer.toMd(referenceValueDocument)
    case resolvedReferenceDocument: ResolvedReferenceDocument =>
      ResolvedReferenceDocumentRenderer.renderer.toMd(resolvedReferenceDocument)
    case valueDocument: ValueDocument => ValueDocumentRenderer.renderer.toMd(valueDocument)
  }
}
