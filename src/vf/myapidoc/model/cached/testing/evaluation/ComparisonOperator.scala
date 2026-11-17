package vf.myapidoc.model.cached.testing.evaluation

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.collection.immutable.Pair
import utopia.flow.generic.model.immutable.Value
import utopia.flow.generic.model.mutable.DataType.DoubleType
import utopia.flow.operator.enumeration.End
import utopia.flow.operator.enumeration.End.{First, Last}
import utopia.flow.util.UncertainBoolean

/**
 * An operator for comparing two values
 * @author Mikko Hilpinen
 * @since 29.10.2026, v0.1
 */
sealed trait ComparisonOperator
{
	// ABSTRACT     -----------------------
	
	/**
	 * @return Code that matches this operator (e.g. "==")
	 */
	def code: String
	
	/**
	 * @param values Values to test
	 * @return Whether this comparison holds true for those values. May be uncertain.
	 */
	def apply(values: Pair[Value]): UncertainBoolean
	
	
	// IMPLEMENTED  ----------------------
	
	override def toString: String = code
}

object ComparisonOperator
{
	// ATTRIBUTES   ----------------------
	
	val values = Vector[ComparisonOperator](Equality, Inequality, Smaller, SmallerOrEqual, Larger, LargerOrEqual)
	
	
	// OTHER    --------------------------
	
	def apply(code: String) = values.find { _.code == code }
	
	
	// NESTED   --------------------------
	
	sealed abstract class SmallerLike(smallerEnd: End = First, reverse: Boolean = false) extends ComparisonOperator
	{
		// IMPLEMENTED  ------------------
		
		override def apply(values: Pair[Value]): UncertainBoolean = {
			// Casts the values to the same type
			val sameTypeValues = {
				if (values.isSymmetricBy { _.dataType })
					Some(values)
				else {
					val originTypes = values
						.flatMap { v => v.dataType.conversionCostTo(DoubleType).map { v.dataType -> _ } }
						.sortBy { _._2 }.map { _._1 }
					originTypes.findMap { t => values.findForBoth { _.castTo(t) } }
				}
			}
			// Casts both values to doubles
			sameTypeValues.flatMap { _.findForBoth { _.double } } match {
				case Some(values) =>
					// Compares the double values and yields the result
					val evalResult = values(smallerEnd) < values(smallerEnd.opposite)
					if (reverse) !evalResult else evalResult
					
				case None => UncertainBoolean
			}
		}
	}
	
	
	// VALUES   --------------------------
	
	case object Equality extends ComparisonOperator
	{
		override val code: String = "=="
		
		override def apply(values: Pair[Value]): UncertainBoolean = values.isSymmetricWith(Value.convertToEqual)
	}
	case object Inequality extends ComparisonOperator
	{
		override val code: String = "!="
		
		override def apply(values: Pair[Value]): UncertainBoolean = values.isAsymmetricWith(Value.convertToEqual)
	}
	
	case object Smaller extends SmallerLike()
	{
		override val code: String = "<"
	}
	case object SmallerOrEqual extends SmallerLike(smallerEnd = Last, reverse = true)
	{
		override val code: String = "<="
	}
	case object Larger extends SmallerLike(smallerEnd = Last)
	{
		override val code: String = ">"
	}
	case object LargerOrEqual extends SmallerLike(reverse = true)
	{
		override val code: String = ">="
	}
}
