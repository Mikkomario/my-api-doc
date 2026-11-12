package vf.myapidoc.model.factory.example

import utopia.flow.generic.model.immutable.Value
import utopia.flow.util.Mutate
import utopia.vault.store.CanCopyData

/**
  * Common trait for classes that implement ExamplePropertyValueFactory by wrapping another such instance
  * @tparam Data Type of the wrapped data
  * @tparam Repr Type of the implementing class
  * @author Mikko Hilpinen
  * @since 30.10.2026, v0.1
  */
trait ExamplePropertyValueFactoryWrapper[Data <: ExamplePropertyValueFactory[Data], +Repr]
	extends ExamplePropertyValueFactory[Repr] with CanCopyData[Data, Repr]
{
	// IMPLEMENTED	--------------------
	
	override def withMatchedDocumentation(matchedDocumentation: Boolean) = 
		mapData { _.withMatchedDocumentation(matchedDocumentation) }
	override def withPropId(propId: Int) = mapData { _.withPropId(propId) }
	override def withResponseId(responseId: Int) = mapData { _.withResponseId(responseId) }
	override def withValue(value: Value) = mapData { _.withValue(value) }
}

