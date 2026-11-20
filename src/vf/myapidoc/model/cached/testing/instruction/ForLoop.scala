package vf.myapidoc.model.cached.testing.instruction

import utopia.flow.generic.model.immutable.Value
import utopia.flow.util.EitherExtensions._
import utopia.flow.view.mutable.Pointer
import vf.myapidoc.controller.testing.TestContext
import vf.myapidoc.model.cached.testing.evaluation.TestEvaluation

/**
 * Iterates over a collection, executing specific instructions for each entry
 * @param getColl An evaluation that yields the collection to iterate
 * @param itemRef Reference to the iterated item. Either:
 *                - Left: A variable name
 *                - Right: A pointer to update
 * @param code Code executed for each collection value
 * @author Mikko Hilpinen
 * @since 19.11.2026, v0.1
 */
case class ForLoop(getColl: TestEvaluation, itemRef: Either[String, Pointer[Value]],
                   code: TestInstruction)
	extends TestInstruction
{
	// IMPLEMENTED  ---------------------------
	
	override def toString: String = s"for $getColl: $code"
	
	override def execute()(implicit context: TestContext): Unit = {
		getColl.run().vector match {
			case Some(coll) =>
				val count = coll.size
				if (count == 0)
					context.log(s"$getColl = empty => Skipping the for loop")
				else {
					val sensitive = getColl.sensitive || itemRef.leftOption.exists(context.isSensitive)
					coll.iterator.zipWithIndex.foreach { case (value, index) =>
						// Updates the iterated value reference
						val varStr = itemRef match {
							case Left(variableName) =>
								context(variableName) = value
								s"`$variableName` = "
							case Right(pointer) =>
								pointer.value = value
								""
						}
						val valueStr = if (sensitive) "<omitted>" else value.toJson
						context.log(s"${ index + 1 }/$count: $varStr$valueStr")
						code.execute()
					}
				}
			case None => context.log(s"$getColl = null => Skipping the for loop")
		}
	}
}