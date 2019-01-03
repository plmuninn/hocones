package pl.onewebpro.hocones.parser.entity.simple

import pl.onewebpro.hocones.parser.entity.Result

case class ResolvedRef(result: Result, reference: NotResolvedRef) extends ReferenceTypeValue
