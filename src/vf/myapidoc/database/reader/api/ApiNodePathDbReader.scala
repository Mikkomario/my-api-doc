package vf.myapidoc.database.reader.api

import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
import vf.myapidoc.database.storable.api.ApiNodePathDbModel
import vf.myapidoc.model.partial.api.ApiNodePathData
import vf.myapidoc.model.stored.api.ApiNodePath

/**
  * Used for reading api node path data from the DB
  * @author Mikko Hilpinen
  * @since 15.11.2026, v0.1
  */
object ApiNodePathDbReader extends VersionedDocElementDbReaderLike[ApiNodePath]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	override val dbProps = ApiNodePathDbModel
	
	
	// IMPLEMENTED	--------------------
	
	/**
	  * @param model            Model from which additional data may be read
	  * @param id               ID to assign to the read/parsed versioned doc element
	  * @param originalUpdateId original update id to assign to the new versioned doc element
	  * @param removingUpdateId removing update id to assign to the new versioned doc element
	  */
	override protected def apply(model: HasProperties, id: Int, originalUpdateId: Int, 
		removingUpdateId: Option[Int]) = 
		ApiNodePath(id, ApiNodePathData(originalUpdateId = originalUpdateId, 
			nodeId = model(dbProps.nodeId.name).getInt, relative = model(dbProps.relative.name).getString, 
			removingUpdateId = removingUpdateId, 
			paramsModelSchemaId = model(dbProps.paramsModelSchemaId.name).int))
}

