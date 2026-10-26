package vf.myapidoc.database.storable.schema

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.{DeprecatesAfterDefined, HasIdProperty}
import utopia.vault.nosql.storable.StorableFactory
import utopia.vault.store.{FromIdFactory, HasId}
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.model.factory.schema.PossiblePropertyValueFactory
import vf.myapidoc.model.partial.schema.PossiblePropertyValueData
import vf.myapidoc.model.stored.schema.PossiblePropertyValue

import java.time.Instant

/**
  * Used for constructing PossiblePropertyValueDbModel instances and for inserting possible 
  * property values to the database
  * @author Mikko Hilpinen
  * @since 26.10.2026, v0.1
  */
object PossiblePropertyValueDbModel 
	extends StorableFactory[PossiblePropertyValueDbModel, PossiblePropertyValue, PossiblePropertyValueData] 
		with FromIdFactory[Int, PossiblePropertyValueDbModel] with HasIdProperty 
		with PossiblePropertyValueFactory[PossiblePropertyValueDbModel] with DeprecatesAfterDefined
{
	// ATTRIBUTES	--------------------
	
	override val id = DbPropertyDeclaration("id", index)
	
	/**
	  * Database property used for interacting with prop ids
	  */
	lazy val propId = property("propId")
	
	/**
	  * Database property used for interacting with values
	  */
	lazy val value = property("value")
	
	/**
	  * Database property used for interacting with descriptions
	  */
	lazy val description = property("description")
	
	/**
	  * Database property used for interacting with creation times
	  */
	lazy val created = property("created")
	
	/**
	  * Database property used for interacting with deprecation times
	  */
	lazy val deprecatedAfter = property("deprecatedAfter")
	
	override val deprecationColumn = deprecatedAfter
	
	
	// IMPLEMENTED	--------------------
	
	override def table = MyApiDocTables.possiblePropertyValue
	
	override def apply(data: PossiblePropertyValueData): PossiblePropertyValueDbModel = 
		apply(None, Some(data.propId), data.value, data.description, Some(data.created), data.deprecatedAfter)
	
	override def withCreated(created: Instant) = apply(created = Some(created))
	
	override def withDeprecatedAfter(deprecatedAfter: Instant) = apply(deprecatedAfter = 
		Some(deprecatedAfter))
	
	override def withDescription(description: String) = apply(description = description)
	
	override def withId(id: Int) = apply(id = Some(id))
	
	override def withPropId(propId: Int) = apply(propId = Some(propId))
	
	override def withValue(value: String) = apply(value = value)
	
	override protected def complete(id: Value, data: PossiblePropertyValueData) = 
		PossiblePropertyValue(id.getInt, data)
}

/**
  * Used for interacting with PossiblePropertyValues in the database
  * @param id possible property value database id
  * @author Mikko Hilpinen
  * @since 26.10.2026, v0.1
  */
case class PossiblePropertyValueDbModel(id: Option[Int] = None, propId: Option[Int] = None, 
	value: String = "", description: String = "", created: Option[Instant] = None, 
	deprecatedAfter: Option[Instant] = None) 
	extends Storable with HasId[Option[Int]] with FromIdFactory[Int, PossiblePropertyValueDbModel] 
		with PossiblePropertyValueFactory[PossiblePropertyValueDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties: Seq[(String, Value)] = 
		Vector(PossiblePropertyValueDbModel.id.name -> id, 
			PossiblePropertyValueDbModel.propId.name -> propId, 
			PossiblePropertyValueDbModel.value.name -> value, 
			PossiblePropertyValueDbModel.description.name -> description, 
			PossiblePropertyValueDbModel.created.name -> created, 
			PossiblePropertyValueDbModel.deprecatedAfter.name -> deprecatedAfter)
	
	
	// IMPLEMENTED	--------------------
	
	override def table = PossiblePropertyValueDbModel.table
	
	override def withCreated(created: Instant) = copy(created = Some(created))
	
	override def withDeprecatedAfter(deprecatedAfter: Instant) = copy(deprecatedAfter = Some(deprecatedAfter))
	
	override def withDescription(description: String) = copy(description = description)
	
	override def withId(id: Int) = copy(id = Some(id))
	
	override def withPropId(propId: Int) = copy(propId = Some(propId))
	
	override def withValue(value: String) = copy(value = value)
}

