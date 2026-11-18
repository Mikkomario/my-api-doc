package vf.myapidoc.model.cached.testing.evaluation

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.flow.generic.model.mutable.DataType.{ModelType, PairType, StringType, VectorType}
import vf.myapidoc.controller.testing.TestContext

/**
 * Evaluates the value of a value's property
 * @author Mikko Hilpinen
 * @since 29.10.2026, v0.1
 */
case class ReadProperty(from: TestEvaluation, key: String) extends TestEvaluation
{
	// IMPLEMENTED	--------------------
	
	override def toString = s"$from.$key"
	
	override def sensitive(implicit context: TestContext): Boolean = from.sensitive || context.isSensitive(key)
	
	override def run()(implicit context: TestContext): Value = {
		val origin = from.run()
		// By default, looks for a model property value
		val default = origin(key)
		if (default.nonEmpty)
			default
		// Case: No property value specified => Checks for a custom key
		else
			key.toLowerCase match {
				case "isempty" | "empty" => valueIsEmpty(origin)
				case "nonempty" | "isdefined" | "defined" => !valueIsEmpty(origin)
				case "size" | "length" => origin.getVector.size
				case "head" | "first" => origin.getVector.headOption
				case other =>
					other.int match {
						case Some(index) => origin.getVector.lift(index).getOrElse(Value.empty)
						case None => default
					}
			}
	}
	
	
	// OTHER    --------------------------
	
	private def valueIsEmpty(value: Value) = value.isEmpty || (value.dataType match {
		case StringType => value.getString.isEmpty
		case VectorType => value.getVector.isEmpty
		case PairType => value.getPair.isEmpty
		case ModelType => !value.getModel.nonEmptyPropertiesIterator.hasNext
		case _ => false
	})
}
