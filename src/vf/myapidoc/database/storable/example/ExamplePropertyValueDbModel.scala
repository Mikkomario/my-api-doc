package vf.myapidoc.database.storable.example

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.HasIdProperty
import utopia.vault.nosql.storable.StorableFactory
import utopia.vault.store.{FromIdFactory, HasId}
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.model.factory.example.ExamplePropertyValueFactory
import vf.myapidoc.model.partial.example.ExamplePropertyValueData
import vf.myapidoc.model.stored.example.ExamplePropertyValue

/**
  * Used for constructing ExamplePropertyValueDbModel instances and for inserting example 
  * property values to the database
  * @author Mikko Hilpinen
  * @since 30.10.2026, v0.1
  */
object ExamplePropertyValueDbModel 
	extends StorableFactory[ExamplePropertyValueDbModel, ExamplePropertyValue, ExamplePropertyValueData] 
		with FromIdFactory[Int, ExamplePropertyValueDbModel] with HasIdProperty 
		with ExamplePropertyValueFactory[ExamplePropertyValueDbModel]
{
	// ATTRIBUTES	--------------------
	
	override val id = DbPropertyDeclaration("id", index)
	
	/**
	  * Database property used for interacting with response ids
	  */
	lazy val responseId = property("responseId")
	
	/**
	  * Database property used for interacting with prop ids
	  */
	lazy val propId = property("propId")
	
	/**
	  * Database property used for interacting with values
	  */
	lazy val value = property("value")
	
	
	// IMPLEMENTED	--------------------
	
	override def table = MyApiDocTables.examplePropertyValue
	
	override def apply(data: ExamplePropertyValueData): ExamplePropertyValueDbModel = 
		apply(None, Some(data.responseId), Some(data.propId), data.value)
	
	override def withId(id: Int) = apply(id = Some(id))
	
	override def withPropId(propId: Int) = apply(propId = Some(propId))
	
	override def withResponseId(responseId: Int) = apply(responseId = Some(responseId))
	
	override def withValue(value: Value) = apply(value = value)
	
	override protected def complete(id: Value, data: ExamplePropertyValueData) = 
		ExamplePropertyValue(id.getInt, data)
}

/**
  * Used for interacting with ExamplePropertyValues in the database
  * @param id example property value database id
  * @author Mikko Hilpinen
  * @since 30.10.2026, v0.1
  */
case class ExamplePropertyValueDbModel(id: Option[Int] = None, responseId: Option[Int] = None, 
	propId: Option[Int] = None, value: Value = Value.empty) 
	extends Storable with HasId[Option[Int]] with FromIdFactory[Int, ExamplePropertyValueDbModel] 
		with ExamplePropertyValueFactory[ExamplePropertyValueDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties: Seq[(String, Value)] = 
		Vector(ExamplePropertyValueDbModel.id.name -> id, 
			ExamplePropertyValueDbModel.responseId.name -> responseId, 
			ExamplePropertyValueDbModel.propId.name -> propId, 
			ExamplePropertyValueDbModel.value.name -> value.mapIfNotEmpty { _.toJson })
	
	
	// IMPLEMENTED	--------------------
	
	override def table = ExamplePropertyValueDbModel.table
	
	override def withId(id: Int) = copy(id = Some(id))
	
	override def withPropId(propId: Int) = copy(propId = Some(propId))
	
	override def withResponseId(responseId: Int) = copy(responseId = Some(responseId))
	
	override def withValue(value: Value) = copy(value = value)
}

