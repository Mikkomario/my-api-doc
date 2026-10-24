package vf.myapidoc.database.storable.schema

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.{DeprecatesAfterDefined, HasIdProperty}
import utopia.vault.nosql.storable.StorableFactory
import utopia.vault.store.{FromIdFactory, HasId}
import vf.myapidoc.database.MyApiDocTables
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
	extends StorableFactory[AlternativePropertyTypeDbModel, AlternativePropertyType, AlternativePropertyTypeData] 
		with FromIdFactory[Int, AlternativePropertyTypeDbModel] with HasIdProperty 
		with AlternativePropertyTypeFactory[AlternativePropertyTypeDbModel] with DeprecatesAfterDefined
{
	// ATTRIBUTES	--------------------
	
	override val id = DbPropertyDeclaration("id", index)
	
	/**
	  * Database property used for interacting with prop ids
	  */
	lazy val propId = property("propId")
	
	/**
	  * Database property used for interacting with alt types
	  */
	lazy val altType = property("altTypeId")
	
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
	
	override def table = MyApiDocTables.alternativePropertyType
	
	override def apply(data: AlternativePropertyTypeData): AlternativePropertyTypeDbModel = 
		apply(None, Some(data.propId), Some(data.altType), Some(data.created), data.deprecatedAfter)
	
	override def withAltType(altType: PropertyType) = apply(altType = Some(altType))
	
	override def withCreated(created: Instant) = apply(created = Some(created))
	
	override def withDeprecatedAfter(deprecatedAfter: Instant) = apply(deprecatedAfter = 
		Some(deprecatedAfter))
	
	override def withId(id: Int) = apply(id = Some(id))
	
	override def withPropId(propId: Int) = apply(propId = Some(propId))
	
	override protected def complete(id: Value, data: AlternativePropertyTypeData) = 
		AlternativePropertyType(id.getInt, data)
}

/**
  * Used for interacting with AlternativePropertyTypes in the database
  * @param id alternative property type database id
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AlternativePropertyTypeDbModel(id: Option[Int] = None, propId: Option[Int] = None, 
	altType: Option[PropertyType] = None, created: Option[Instant] = None, 
	deprecatedAfter: Option[Instant] = None) 
	extends Storable with HasId[Option[Int]] with FromIdFactory[Int, AlternativePropertyTypeDbModel] 
		with AlternativePropertyTypeFactory[AlternativePropertyTypeDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties: Seq[(String, Value)] = 
		Vector(AlternativePropertyTypeDbModel.id.name -> id, 
			AlternativePropertyTypeDbModel.propId.name -> propId, 
			AlternativePropertyTypeDbModel.altType.name -> altType.map[Value] { e => e.id }.getOrElse(Value.empty), 
			AlternativePropertyTypeDbModel.created.name -> created, 
			AlternativePropertyTypeDbModel.deprecatedAfter.name -> deprecatedAfter)
	
	
	// IMPLEMENTED	--------------------
	
	override def table = AlternativePropertyTypeDbModel.table
	
	override def withAltType(altType: PropertyType) = copy(altType = Some(altType))
	
	override def withCreated(created: Instant) = copy(created = Some(created))
	
	override def withDeprecatedAfter(deprecatedAfter: Instant) = copy(deprecatedAfter = Some(deprecatedAfter))
	
	override def withId(id: Int) = copy(id = Some(id))
	
	override def withPropId(propId: Int) = copy(propId = Some(propId))
}

