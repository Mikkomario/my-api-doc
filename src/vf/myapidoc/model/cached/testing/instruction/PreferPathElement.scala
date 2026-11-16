package vf.myapidoc.model.cached.testing.instruction

import utopia.flow.generic.model.immutable.Model
import utopia.flow.generic.casting.ValueConversions._
import vf.myapidoc.controller.testing.TestContext
import vf.myapidoc.model.cached.testing.evaluation.TestEvaluation

/**
 * Marks a path element as preferred
 * @param getElem An evaluation that resolves into the preferred path element (string)
 * @author Mikko Hilpinen
 * @since 16.11.2026, v0.1
 */
case class PreferPathElement(getElem: TestEvaluation) extends TestInstruction
{
	// IMPLEMENTED  -----------------------
	
	override def toString: String = s"prefer `$getElem`"
	
	override def execute()(implicit context: TestContext): Unit = {
		getElem.run().string match {
			case Some(elem) =>
				context.preferPathElement(elem)
				if (!getElem.sensitive)
					context.log(s"Prefers path element: \"$elem\"")
			case None =>
				context.scribe.in("prefer.element.resolve").warning("No preferred element was found",
					Model.from("eval" -> getElem.toString))
		}
	}
}
