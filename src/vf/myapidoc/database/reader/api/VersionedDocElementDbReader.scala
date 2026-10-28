package vf.myapidoc.database.reader.api

import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
import vf.myapidoc.database.props.api.VersionedDocElementDbProps
import vf.myapidoc.model.partial.api.VersionedDocElementData
import vf.myapidoc.model.stored.api.VersionedDocElement

object VersionedDocElementDbReader
{
	// OTHER	--------------------
	
	/**
	  * @param dbProps Database properties used when reading column data
	  * @return A factory used for parsing versioned doc elements from database model data
	  */
	def apply(dbProps: VersionedDocElementDbProps): VersionedDocElementDbReader = 
		_VersionedDocElementDbReader(dbProps)
	
	
	// NESTED	--------------------
	
	/**
	  * @param dbProps Database properties used when reading column data
	  */
	private case class _VersionedDocElementDbReader(dbProps: VersionedDocElementDbProps) 
		extends VersionedDocElementDbReader
	{
		// IMPLEMENTED	--------------------
		
		override def table = dbProps.table
		
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
}

/**
  * Common trait for factories that parse versioned doc element data from database-originated 
  * models
  * @author Mikko Hilpinen
  * @since 28.10.2026, v0.1
  */
trait VersionedDocElementDbReader extends VersionedDocElementDbReaderLike[VersionedDocElement]

