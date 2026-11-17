package vf.myapidoc.model.cached.testing.evaluation

import utopia.flow.collection.immutable.Pair
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.{Model, Value}
import vf.myapidoc.controller.testing.TestContext
import vf.myapidoc.model.cached.testing.evaluation.ComparisonOperator.Equality

/**
 * Compares two values with each other
 * @author Mikko Hilpinen
 * @since 29.10.2026, v0.1
 */
case class Compare(line: Int, values: Pair[TestEvaluation], operator: ComparisonOperator = Equality)
	extends TestEvaluation
{
	// IMPLEMENTED  -------------------
	
	override def toString = s"${ values.first } $operator ${ values.second }"
	
	override def sensitive(implicit context: TestContext): Boolean = values.exists { _.sensitive }
	
	override def run()(implicit context: TestContext): Value = {
		val inputValues = values.map { _.run() }
		val result = operator(inputValues)
		
		// Logs if the result is ambiguous
		if (result.isUncertain)
			context.scribe.in("Compare.uncertain").variant("operator", operator.code).apply(
				"Comparison result is ambiguous",
				Model.from("line" -> line, "sources" -> values.map { _.toString },
					"values" -> (if (sensitive) "<omitted>" else inputValues)))
		
		result
	}
}
