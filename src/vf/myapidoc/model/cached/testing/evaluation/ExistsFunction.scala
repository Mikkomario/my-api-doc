package vf.myapidoc.model.cached.testing.evaluation

import utopia.flow.generic.model.immutable.Value
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.view.mutable.Pointer
import vf.myapidoc.controller.testing.TestContext

/**
 * A function that iterates over a collection or a value, looking whether it satisfies a condition
 * @author Mikko Hilpinen
 * @since 10.11.2026, v0.1
 */
case class ExistsFunction(getColl: TestEvaluation, predicate: TestEvaluation, evaluatedItemPointer: Pointer[Value])
	extends TestEvaluation
{
	override def sensitive(implicit context: TestContext): Boolean = getColl.sensitive || predicate.sensitive
	
	override def run()(implicit context: TestContext): Value = {
		val values = getColl.run().getVector
		lazy val _sensitive = sensitive
		val result = values.exists { value =>
			evaluatedItemPointer.value = value
			val result = predicate.run().getBoolean
			
			if (result) {
				if (_sensitive)
					context.log(s"Found a value that satisfied `$predicate`")
				else
					context.log(s"$value satisfies `$predicate`")
			}
			
			result
		}
		
		if (!result)
			context.log(s"None of the ${ values.size } values satisfied `$predicate`")
		
		result
	}
}
