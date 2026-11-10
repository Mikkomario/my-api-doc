package vf.myapidoc.model.cached.testing.evaluation

import utopia.flow.generic.model.immutable.Value
import utopia.flow.view.mutable.Pointer
import vf.myapidoc.controller.testing.TestContext

/**
 * Iterates over a collection, finding a value that satisfies a predicate
 * @author Mikko Hilpinen
 * @since 10.11.2026, v0.1
 */
case class FindFunction(getColl: TestEvaluation, predicate: TestEvaluation, evaluatedItemPointer: Pointer[Value])
	extends TestEvaluation
{
	override def sensitive(implicit context: TestContext): Boolean = getColl.sensitive || predicate.sensitive
	
	override def run()(implicit context: TestContext): Value = {
		val values = getColl.run().getVector
		val result = values.find { value =>
			evaluatedItemPointer.value = value
			predicate.run().getBoolean
		}
		if (sensitive)
			context.log(s"Iterated over `$getColl` ${
				if (result.isDefined) "and found" else "but didn't find" } a value that satisfied `$predicate`")
		else
			result match {
				case Some(value) => context.log(s"Targeting $value from ${ values.size } values")
				case None =>
					context.log(s"Didn't find a value that satisfied `$predicate`. Input size was ${ values.size }.")
			}
		
		result.getOrElse(Value.empty)
	}
}