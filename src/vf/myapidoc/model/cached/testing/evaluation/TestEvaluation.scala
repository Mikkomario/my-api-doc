package vf.myapidoc.model.cached.testing.evaluation

import utopia.flow.generic.model.immutable.Value
import vf.myapidoc.controller.testing.TestContext

/**
 * Common trait for evaluation functions used in tests
 * @author Mikko Hilpinen
 * @since 29.10.2026, v0.1
 */
trait TestEvaluation
{
	// ABSTRACT -----------------------
	
	/**
	 * Executes this evaluation
	 * @param context Implicit test context
	 * @return Evaluation result as a value
	 */
	def run()(implicit context: TestContext): Value
	
	
	// OTHER    -----------------------
	
	/**
	 * @param property Targeted property name
	 * @return Access to this result's specified property
	 */
	def get(property: String) = ReadProperty(this, property)
}
