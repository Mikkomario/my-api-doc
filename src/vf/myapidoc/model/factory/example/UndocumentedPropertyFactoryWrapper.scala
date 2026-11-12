package vf.myapidoc.model.factory.example

import utopia.flow.generic.model.immutable.Value
import utopia.vault.store.CanCopyData

/**
  * Common trait for classes that implement UndocumentedPropertyFactory by wrapping another such 
  * instance
  * @tparam Data Type of the wrapped data
  * @tparam Repr Type of the implementing class
  * @author Mikko Hilpinen
  * @since 12.11.2026, v0.1
  */
trait UndocumentedPropertyFactoryWrapper[Data <: UndocumentedPropertyFactory[Data], +Repr] 
	extends UndocumentedPropertyFactory[Repr] with CanCopyData[Data, Repr]
{
	// IMPLEMENTED	--------------------
	
	override def withModelTypeId(modelTypeId: Int) = mapData { _.withModelTypeId(modelTypeId) }
	
	override def withName(name: String) = mapData { _.withName(name) }
	
	override def withResponseId(responseId: Int) = mapData { _.withResponseId(responseId) }
	
	override def withValue(value: Value) = mapData { _.withValue(value) }
}

