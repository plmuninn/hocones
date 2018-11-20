package pl.onewebpro.hocones.md.document.md

import net.steppschuh.markdowngenerator.MarkdownSerializable
import pl.onewebpro.hocones.md.document.model._

object implicits {

  implicit class DocumentToMd[T <: Document[_]](document: T) {
    def toMd(implicit dToMD: DocumentToMdGenerator[T]): MarkdownSerializable = dToMD.toMd(document)
  }

  implicit val markdownArray: DocumentToMdGenerator[ArrayDocument] = new ArrayDocumentMarkdown()
  implicit val markdownConcatenation: DocumentToMdGenerator[ConcatenationDocument] = new ConcatenationDocumentMarkdown()
  implicit val markdownEnvironment: DocumentToMdGenerator[EnvironmentDocument] = new EnvironmentDocumentMarkdown()
  implicit val markdownMerged: DocumentToMdGenerator[MergedValuesDocument] = new MergedValuesDocumentMarkdown()
  implicit val markdownObject: DocumentToMdGenerator[ObjectDocument] = new ObjectDocumentMarkdown()
  implicit val markdownReference: DocumentToMdGenerator[ReferenceValueDocument] = new ReferenceValueDocumentMarkdown()
  implicit val markdownResolvedReference: DocumentToMdGenerator[ResolvedReferenceDocument] = new ResolvedReferenceDocumentMarkdown()
  implicit val markdownValue: DocumentToMdGenerator[ValueDocument] = new ValueDocumentMarkdown()

}
