package pl.onewebpro.hocon.utils.parser.entity.simple

import pl.onewebpro.hocon.utils.parser.entity.Result

case class ResolvedRef(result: Result, reference: NotResolvedRef) extends ReferenceTypeValue
