package vf.myapidoc.database.storable.schema

import utopia.flow.collection.immutable.Pair
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.{DeprecatesAfterDefined, HasIdProperty}
import utopia.vault.nosql.storable.StorableFactory
import utopia.vault.store.{FromIdFactory, HasId}
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.database.props.api.VersionedDocElementDbProps
import vf.myapidoc.database.storable.api.{VersionedDocElementDbModel, VersionedDocElementDbModelFactoryLike, VersionedDocElementDbModelLike}
import vf.myapidoc.model.enumeration.PropertyType
import vf.myapidoc.model.factory.schema.AlternativePropertyTypeFactory
import vf.myapidoc.model.partial.schema.AlternativePropertyTypeData
import vf.myapidoc.model.stored.schema.AlternativePropertyType

import java.time.Instant

/**
  * Used for constructing AlternativePropertyTypeDbModel instances and for inserting alternative 
  * property types to the database
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
object AlternativePropertyTypeDbModel 
	extends VersionedDocElementDbModelFactoryLike[AlternativePropertyTypeDbModel, AlternativePropertyType, AlternativePropertyTypeData] 
		with AlternativePropertyTypeFactory[AlternativePropertyTypeDbModel] with VersionedDocElementDbProps
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
	  * Database property used for interacting with removing update ids
	  */
	override lazy val removingUpdateId = property("removingUpdateId")
	/**
	  * Database property used for interacting with alt types
	  */
	lazy val altType = property("altTypeId")
	
	
	// IMPLEMENTED	--------------------
	
	override def table = MyApiDocTables.alternativePropertyType
	
	override def apply(data: AlternativePropertyTypeData): AlternativePropertyTypeDbModel = 
		apply(None, Some(data.originalUpdateId), Some(data.propId), data.removingUpdateId, Some(data.altType))
	
	override def withAltType(altType: PropertyType) = apply(altType = Some(altType))
	override def withId(id: Int) = apply(id = Some(id))
	override def withOriginalUpdateId(originalUpdateId: Int) = apply(originalUpdateId = 
		Some(originalUpdateId))
	override def withPropId(propId: Int) = apply(propId = Some(propId))
	override def withRemovingUpdateId(removingUpdateId: Int) = apply(removingUpdateId = 
		Some(removingUpdateId))
	
	override protected def complete(id: Value, data: AlternativePropertyTypeData) = 
		AlternativePropertyType(id.getInt, data)
}

/**
  * Used for interacting with AlternativePropertyTypes in the database
  * @param id alternative property type database id
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AlternativePropertyTypeDbModel(id: Option[Int] = None, originalUpdateId: Option[Int] = None,
                                          propId: Option[Int] = None, removingUpdateId: Option[Int] = None,
                                          altType: Option[PropertyType] = None)
	extends VersionedDocElementDbModel with VersionedDocElementDbModelLike[AlternativePropertyTypeDbModel] 
		with AlternativePropertyTypeFactory[AlternativePropertyTypeDbModel] with Storable 
		with HasId[Option[Int]] with FromIdFactory[Int, AlternativePropertyTypeDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties: Seq[(String, Value)] = super[VersionedDocElementDbModelLike].valueProperties ++
		Pair(AlternativePropertyTypeDbModel.propId.name -> propId,
			AlternativePropertyTypeDbModel.altType.name -> altType.map[Value] { e => e.id }.getOrElse(Value.empty))
	
	
	// IMPLEMENTED	--------------------
	
	override def dbProps = AlternativePropertyTypeDbModel
	
	override def table = AlternativePropertyTypeDbModel.table
	
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
	
	override def withAltType(altType: PropertyType) = copy(altType = Some(altType))
	override def withId(id: Int) = copy(id = Some(id))
	override def withPropId(propId: Int) = copy(propId = Some(propId))
}

