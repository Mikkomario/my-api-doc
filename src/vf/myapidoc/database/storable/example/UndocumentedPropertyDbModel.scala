package vf.myapidoc.database.storable.example

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.HasIdProperty
import utopia.vault.nosql.storable.StorableFactory
import utopia.vault.store.{FromIdFactory, HasId}
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.model.factory.example.UndocumentedPropertyFactory
import vf.myapidoc.model.partial.example.UndocumentedPropertyData
import vf.myapidoc.model.stored.example.UndocumentedProperty

/**
  * Used for constructing UndocumentedPropertyDbModel instances and for inserting undocumented 
  * properties to the database
  * @author Mikko Hilpinen
  * @since 12.11.2026, v0.1
  */
object UndocumentedPropertyDbModel 
	extends StorableFactory[UndocumentedPropertyDbModel, UndocumentedProperty, UndocumentedPropertyData] 
		with FromIdFactory[Int, UndocumentedPropertyDbModel] with HasIdProperty 
		with UndocumentedPropertyFactory[UndocumentedPropertyDbModel]
{
	// ATTRIBUTES	--------------------
	
	override val id = DbPropertyDeclaration("id", index)
	
	/**
	  * Database property used for interacting with response ids
	  */
	lazy val responseId = property("responseId")
	
	/**
	  * Database property used for interacting with model type ids
	  */
	lazy val modelTypeId = property("modelTypeId")
	
	/**
	  * Database property used for interacting with names
	  */
	lazy val name = property("name")
	
	/**
	  * Database property used for interacting with values
	  */
	lazy val value = property("value")
	
	
	// IMPLEMENTED	--------------------
	
	override def table = MyApiDocTables.undocumentedProperty
	
	override def apply(data: UndocumentedPropertyData): UndocumentedPropertyDbModel = 
		apply(None, Some(data.responseId), Some(data.modelTypeId), data.name, data.value)
	
	override def withId(id: Int) = apply(id = Some(id))
	
	override def withModelTypeId(modelTypeId: Int) = apply(modelTypeId = Some(modelTypeId))
	
	override def withName(name: String) = apply(name = name)
	
	override def withResponseId(responseId: Int) = apply(responseId = Some(responseId))
	
	override def withValue(value: Value) = apply(value = value)
	
	override protected def complete(id: Value, data: UndocumentedPropertyData) = 
		UndocumentedProperty(id.getInt, data)
}

/**
  * Used for interacting with UndocumentedProperties in the database
  * @param id undocumented property database id
  * @author Mikko Hilpinen
  * @since 12.11.2026, v0.1
  */
case class UndocumentedPropertyDbModel(id: Option[Int] = None, responseId: Option[Int] = None, 
	modelTypeId: Option[Int] = None, name: String = "", value: Value = Value.empty) 
	extends Storable with HasId[Option[Int]] with FromIdFactory[Int, UndocumentedPropertyDbModel] 
		with UndocumentedPropertyFactory[UndocumentedPropertyDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties: Seq[(String, Value)] = 
		Vector(UndocumentedPropertyDbModel.id.name -> id, 
			UndocumentedPropertyDbModel.responseId.name -> responseId, 
			UndocumentedPropertyDbModel.modelTypeId.name -> modelTypeId, 
			UndocumentedPropertyDbModel.name.name -> name, 
			UndocumentedPropertyDbModel.value.name -> value.mapIfNotEmpty { _.toJson })
	
	
	// IMPLEMENTED	--------------------
	
	override def table = UndocumentedPropertyDbModel.table
	
	override def withId(id: Int) = copy(id = Some(id))
	
	override def withModelTypeId(modelTypeId: Int) = copy(modelTypeId = Some(modelTypeId))
	
	override def withName(name: String) = copy(name = name)
	
	override def withResponseId(responseId: Int) = copy(responseId = Some(responseId))
	
	override def withValue(value: Value) = copy(value = value)
}

