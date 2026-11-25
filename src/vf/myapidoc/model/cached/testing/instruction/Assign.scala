package vf.myapidoc.model.cached.testing.instruction

import vf.myapidoc.controller.testing.TestContext
import vf.myapidoc.model.cached.testing.evaluation.TestEvaluation

/**
 * A test instruction that assigns a value to the context
 * @param key Context variable where the information will be stored
 * @param getValue A function for acquiring the value to store
 * @author Mikko Hilpinen
 * @since 29.10.2026, v0.1
 */
case class Assign(key: String, getValue: TestEvaluation) extends TestInstruction
{
	override def toString = s"$key = $getValue"
	
	override def execute()(implicit context: TestContext) = {
		// Acquires the value to assign
		val value = getValue.run()
		// May mark the key as sensitive, depending on how the value was acquired
		if (getValue.sensitive)
			context.markSensitive(key)
		// Stores the value
		context(key) = value
		
		// Logs
		val valueStr = {
			if (context.isSensitive(key))
				"<omitted>"
			else {
				val str = value.toJson
				if (str.length > 27)
					s"${str.take(12)}...${str.takeRight(12)}"
				else
					str
			}
		}
		context.log(s"$key = $getValue = $valueStr")
	}
}
