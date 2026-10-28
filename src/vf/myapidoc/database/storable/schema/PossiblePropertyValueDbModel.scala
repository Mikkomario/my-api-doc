package vf.myapidoc.database.storable.schema

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.{DeprecatesAfterDefined, HasIdProperty}
import utopia.vault.nosql.storable.StorableFactory
import utopia.vault.store.{FromIdFactory, HasId}
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.database.props.api.VersionedDocElementDbProps
import vf.myapidoc.database.storable.api.{VersionedDocElementDbModel, VersionedDocElementDbModelFactoryLike, VersionedDocElementDbModelLike}
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
	extends VersionedDocElementDbModelFactoryLike[PossiblePropertyValueDbModel, PossiblePropertyValue, PossiblePropertyValueData] 
		with PossiblePropertyValueFactory[PossiblePropertyValueDbModel] with VersionedDocElementDbProps
{
	// ATTRIBUTES	--------------------
	
	override val id = DbPropertyDeclaration("id", index)
	/**
	  * Database property used for interacting with original update ids
	  */
	override lazy val originalUpdateId = property("originalUpdateId")
	/**
	  * Database property used for interacting with prop ids
	  */
	lazy val propId = property("propId")
	/**
	  * Database property used for interacting with values
	  */
	lazy val value = property("value")
	/**
	  * Database property used for interacting with removing update ids
	  */
	override lazy val removingUpdateId = property("removingUpdateId")
	/**
	  * Database property used for interacting with descriptions
	  */
	lazy val description = property("description")
	
	
	// IMPLEMENTED	--------------------
	
	override def table = MyApiDocTables.possiblePropertyValue
	
	override def apply(data: PossiblePropertyValueData): PossiblePropertyValueDbModel = 
		apply(None, Some(data.originalUpdateId), Some(data.propId), data.value, data.removingUpdateId, 
			data.description)
	
	override def withDescription(description: String) = apply(description = description)
	override def withId(id: Int) = apply(id = Some(id))
	override def withOriginalUpdateId(originalUpdateId: Int) = apply(originalUpdateId = Some(originalUpdateId))
	override def withPropId(propId: Int) = apply(propId = Some(propId))
	override def withRemovingUpdateId(removingUpdateId: Int) = apply(removingUpdateId = Some(removingUpdateId))
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
case class PossiblePropertyValueDbModel(id: Option[Int] = None, originalUpdateId: Option[Int] = None,
                                        propId: Option[Int] = None, value: String = "",
                                        removingUpdateId: Option[Int] = None, description: String = "")
	extends VersionedDocElementDbModel with VersionedDocElementDbModelLike[PossiblePropertyValueDbModel] 
		with PossiblePropertyValueFactory[PossiblePropertyValueDbModel] with Storable with HasId[Option[Int]] 
		with FromIdFactory[Int, PossiblePropertyValueDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties: Seq[(String, Value)] = 
		super[VersionedDocElementDbModelLike].valueProperties ++ Vector(PossiblePropertyValueDbModel.propId.name -> propId, 
			PossiblePropertyValueDbModel.value.name -> value, 
			PossiblePropertyValueDbModel.description.name -> description)
	
	
	// IMPLEMENTED	--------------------
	
	override def dbProps = PossiblePropertyValueDbModel
	
	override def table = PossiblePropertyValueDbModel.table
	
	/**
	  * @param id               ID to assign to the new model (default = currently assigned id)
	  * @param originalUpdateId original update id to assign to the new model (default = currently 
	  *                         assigned value)
	  * @param removingUpdateId removing update id to assign to the new model (default = currently 
	  *                         assigned value)
	  */
	override def copyVersionedDocElement(id: Option[Int] = id, 
		originalUpdateId: Option[Int] = originalUpdateId, removingUpdateId: Option[Int] = removingUpdateId) = 
		copy(id = id, originalUpdateId = originalUpdateId, removingUpdateId = removingUpdateId)
	
	override def withDescription(description: String) = copy(description = description)
	override def withId(id: Int) = copy(id = Some(id))
	override def withPropId(propId: Int) = copy(propId = Some(propId))
	override def withValue(value: String) = copy(value = value)
}

