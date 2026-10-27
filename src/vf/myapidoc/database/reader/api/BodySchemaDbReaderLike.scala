package vf.myapidoc.database.reader.api

import utopia.flow.generic.model.immutable.Model
import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
import utopia.vault.model.template.HasTableAsTarget
import utopia.vault.nosql.read.DbRowReader
import utopia.vault.nosql.read.parse.ParseTableModel
import vf.myapidoc.database.props.api.BodySchemaDbProps
import vf.myapidoc.model.enumeration.PropertyType

import java.time.Instant
import scala.util.Success

/**
  * Common trait for factories which parse body schema data from database-originated models
  * @tparam A Type of read instances
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
trait BodySchemaDbReaderLike[+A] extends DbRowReader[A] with ParseTableModel[A] with HasTableAsTarget
{
	// ABSTRACT	--------------------
	
	/**
	  * Database properties used when parsing column data
	  */
	def dbProps: BodySchemaDbProps
	
	/**
	  * @param model           Model from which additional data may be read
	  * @param id              Id to assign to the read/parsed body schema
	  * @param parentId        parent id to assign to the new body schema
	  * @param dataType        data type to assign to the new body schema
	  * @param modelTypeId     model type id to assign to the new body schema
	  * @param created         created to assign to the new body schema
	  * @param deprecatedAfter deprecated after to assign to the new body schema
	  * @param isArray         is array to assign to the new body schema
	  * @return A body schema with the specified data
	  */
	protected def apply(model: HasProperties, id: Int, parentId: Int, dataType: PropertyType, 
		modelTypeId: Option[Int], created: Instant, deprecatedAfter: Option[Instant], isArray: Boolean): A
	
	
	// IMPLEMENTED	--------------------
	
	override def fromValid(valid: Model) = Success(apply(valid, valid(dbProps.id.name).getInt,
		parentId = valid(dbProps.parentId.name).getInt,
		dataType = PropertyType.fromValue(valid(dbProps.dataType.name)),
		modelTypeId = valid(dbProps.modelTypeId.name).int,
		created = valid(dbProps.created.name).getInstant,
		deprecatedAfter = valid(dbProps.deprecatedAfter.name).instant,
		isArray = valid(dbProps.isArray.name).getBoolean))
}

