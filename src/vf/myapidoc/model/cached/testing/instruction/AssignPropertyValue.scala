package vf.myapidoc.model.cached.testing.instruction

import vf.myapidoc.controller.testing.TestContext
import vf.myapidoc.model.cached.testing.evaluation.TestEvaluation

/**
 * Assigns a deeply nested value to the test context
 * @author Mikko Hilpinen
 * @since 30.10.2026, v0.1
 */
case class AssignPropertyValue(path: Iterable[String], getValue: TestEvaluation) extends TestInstruction
{
	override def toString = s"${ path.mkString(".") } = `$getValue`"
	
	override def execute()(implicit context: TestContext) = {
		// Acquires the value
		val value = getValue.run()
		// May mark the key as sensitive, depending on how the value was acquired
		if (getValue.sensitive)
			context.markSensitive(path.last)
		// Stores the value
		context.update(path, value)
		
		// Logs
		val valueStr = {
			if (path.exists(context.isSensitive))
				"<omitted>"
			else
				value.toJson
		}
		context.log(s"${ path.mkString(".") } = `$getValue` = $valueStr")
	}
}
