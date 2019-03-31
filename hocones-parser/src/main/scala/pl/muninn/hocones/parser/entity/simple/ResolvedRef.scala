package pl.muninn.hocones.parser.entity.simple

import pl.muninn.hocones.parser.entity.Result

case class ResolvedRef(result: Result, reference: NotResolvedRef) extends ReferenceTypeValue
