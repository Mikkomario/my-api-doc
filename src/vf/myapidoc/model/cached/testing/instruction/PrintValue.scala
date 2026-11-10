package vf.myapidoc.model.cached.testing.instruction

import utopia.flow.generic.model.immutable.Value
import vf.myapidoc.controller.testing.TestContext
import vf.myapidoc.model.cached.testing.evaluation.TestEvaluation

/**
 * Prints a value to the console and test logs
 * @author Mikko Hilpinen
 * @since 10.11.2026, v0.1
 */
case class PrintValue(getValue: TestEvaluation) extends TestInstruction
{
	override def execute()(implicit context: TestContext): Unit = {
		val value = valueToString(getValue.run(), indentation = 0, indentFirstLine = false)
		println(value)
		
		// Sensitive values are not included in the persisting logs
		if (!getValue.sensitive)
			context.log(s"Printed: $value")
	}
	
	private def valueToString(value: Value, indentation: Int, indentFirstLine: Boolean): String = {
		val indentationStr = "\t" * indentation
		val firstLineIndentation = if (indentFirstLine) indentationStr else ""
		value.model match {
			case Some(model) =>
				s"$firstLineIndentation{\n${
					model.propertiesIterator
						.map { prop =>
							s"$indentationStr\t\"${ prop.name }\": ${
								valueToString(prop.value, indentation + 1, indentFirstLine = false) }"
						}
						.mkString(",\n")
				}\n$indentationStr}"
			case None =>
				value.vector.filter { _.size > 1 } match {
					case Some(values) =>
						s"$firstLineIndentation[\n${
							values.map { valueToString(_, indentation, indentFirstLine = true) }.mkString(",\n")
						}\n$indentationStr]"
					
					case None => s"$firstLineIndentation${ value.toJson }"
				}
		}
	}
}