package vf.myapidoc.database.reader.schema

import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
import vf.myapidoc.database.reader.api.VersionedDocElementDbReaderLike
import vf.myapidoc.database.storable.schema.AlternativePropertyTypeDbModel
import vf.myapidoc.model.enumeration.PropertyType
import vf.myapidoc.model.partial.schema.AlternativePropertyTypeData
import vf.myapidoc.model.stored.schema.AlternativePropertyType

/**
  * Used for reading alternative property type data from the DB
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
object AlternativePropertyTypeDbReader extends VersionedDocElementDbReaderLike[AlternativePropertyType]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	override val dbProps = AlternativePropertyTypeDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def table = dbProps.table
	
	/**
	  * @param model            Model from which additional data may be read
	  * @param id               ID to assign to the read/parsed versioned doc element
	  * @param originalUpdateId original update id to assign to the new versioned doc element
	  * @param removingUpdateId removing update id to assign to the new versioned doc element
	  */
	override protected def apply(model: HasProperties, id: Int, originalUpdateId: Int, removingUpdateId: Option[Int]) =
		AlternativePropertyType(id, AlternativePropertyTypeData(originalUpdateId = originalUpdateId, 
			propId = model(dbProps.propId.name).getInt, removingUpdateId = removingUpdateId, 
			altType = PropertyType.fromValue(model(dbProps.altType.name))))
}

