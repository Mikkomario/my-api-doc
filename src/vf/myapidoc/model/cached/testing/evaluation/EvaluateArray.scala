package vf.myapidoc.model.cached.testing.evaluation

import utopia.flow.generic.model.immutable.Value
import utopia.flow.generic.casting.ValueConversions._
import vf.myapidoc.controller.testing.TestContext

/**
 * Evaluates multiple values at once
 * @author Mikko Hilpinen
 * @since 29.10.2026, v0.1
 */
case class EvaluateArray(elements: Seq[TestEvaluation]) extends TestEvaluation
{
	override def toString = s"[${ elements.iterator.map { e => s"`$e`" }.mkString(", ") }]"
	
	override def run()(implicit context: TestContext): Value = elements.map { _.run() }
}
