package vf.myapidoc.model.cached.testing.evaluation

import utopia.flow.collection.immutable.Pair
import utopia.flow.generic.model.immutable.Value
import utopia.flow.operator.enumeration.End
import utopia.flow.operator.enumeration.End.{First, Last}

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
	 * @return Whether this comparison holds true for those values
	 */
	def apply(values: Pair[Value]): Boolean
	
	
	// IMPLEMENTED  ----------------------
	
	override def toString: String = code
}

object ComparisonOperator
{
	// ATTRIBUTES   ----------------------
	
	val values = Vector[ComparisonOperator](Equality, Smaller, SmallerOrEqual, Larger, LargerOrEqual)
	
	
	// OTHER    --------------------------
	
	def apply(code: String) = values.find { _.code == code }
	
	
	// NESTED   --------------------------
	
	sealed abstract class SmallerLike(smallerEnd: End = First, reverse: Boolean = false) extends ComparisonOperator
	{
		// IMPLEMENTED  ------------------
		
		override def apply(values: Pair[Value]): Boolean = {
			val doubleValues = values.map { _.getDouble }
			val evalResult = doubleValues(smallerEnd) < doubleValues(smallerEnd.opposite)
			if (reverse) !evalResult else evalResult
		}
	}
	
	
	// VALUES   --------------------------
	
	case object Equality extends ComparisonOperator
	{
		override val code: String = "=="
		
		override def apply(values: Pair[Value]): Boolean = values.isSymmetricWith(Value.convertToEqual)
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
