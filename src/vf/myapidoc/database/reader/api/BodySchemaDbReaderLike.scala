package vf.myapidoc.database.reader.api

import utopia.flow.generic.model.immutable.Model
import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
import utopia.vault.model.template.HasTableAsTarget
import utopia.vault.nosql.read.DbRowReader
import utopia.vault.nosql.read.parse.ParseTableModel
import vf.myapidoc.database.props.api.BodySchemaDbProps
import vf.myapidoc.model.enumeration.PropertyType

import scala.util.Success

import java.time.Instant

/**
  * Common trait for factories that parse body schema data from database-originated models
  * @tparam A Type of read instances
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
trait BodySchemaDbReaderLike[+A] extends VersionedDocElementDbReaderLike[A]
{
	// ABSTRACT	--------------------
	
	/**
	  * Database properties used when parsing column data
	  */
	override def dbProps: BodySchemaDbProps
	
	/**
	  * @param model            Model from which additional data may be read
	  * @param id               ID to assign to the read/parsed body schema
	  * @param originalUpdateId original update id to assign to the new body schema
	  * @param parentId         parent id to assign to the new body schema
	  * @param removingUpdateId removing update id to assign to the new body schema
	  * @param dataType         data type to assign to the new body schema
	  * @param modelTypeId      model type id to assign to the new body schema
	  * @param isArray          is array to assign to the new body schema
	  * @return A body schema with the specified data
	  */
	protected def apply(model: HasProperties, id: Int, originalUpdateId: Int, parentId: Int,
	                    removingUpdateId: Option[Int], dataType: PropertyType, modelTypeId: Option[Int],
	                    isArray: Boolean): A
	
	
	// IMPLEMENTED	--------------------
	
	/**
	  * @param model            Model from which additional data may be read
	  * @param id               ID to assign to the read/parsed versioned doc element
	  * @param originalUpdateId original update id to assign to the new versioned doc element
	  * @param removingUpdateId removing update id to assign to the new versioned doc element
	  */
	override protected def apply(model: HasProperties, id: Int, originalUpdateId: Int, removingUpdateId: Option[Int]) =
		apply(model, id, originalUpdateId = originalUpdateId, parentId = model(dbProps.parentId.name).getInt, 
			removingUpdateId = removingUpdateId, 
			dataType = PropertyType.fromValue(model(dbProps.dataType.name)), 
			modelTypeId = model(dbProps.modelTypeId.name).int, 
			isArray = model(dbProps.isArray.name).getBoolean)
}

