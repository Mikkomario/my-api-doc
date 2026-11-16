package vf.myapidoc.database.reader.api

import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
import vf.myapidoc.database.props.api.VersionedDocElementDbProps
import vf.myapidoc.model.partial.api.VersionedDocElementData
import vf.myapidoc.model.stored.api.VersionedDocElement

/**
  * Used for pulling versioned doc elements from the database
  * @param dbProps Database properties used when reading column data
  * @since 15.11.2026
  */
case class VersionedDocElementDbReader(dbProps: VersionedDocElementDbProps) 
	extends VersionedDocElementDbReaderLike[VersionedDocElement]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param model            Model from which additional data may be read
	  * @param id               ID to assign to the read/parsed versioned doc element
	  * @param originalUpdateId original update id to assign to the new versioned doc element
	  * @param removingUpdateId removing update id to assign to the new versioned doc element
	  */
	override protected def apply(model: HasProperties, id: Int, originalUpdateId: Int, 
		removingUpdateId: Option[Int]) = 
		VersionedDocElement(id, VersionedDocElementData(originalUpdateId, removingUpdateId))
}

