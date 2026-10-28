package vf.myapidoc.database.reader.api

import utopia.flow.generic.model.immutable.Model
import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
import utopia.vault.model.template.HasTableAsTarget
import utopia.vault.nosql.read.DbRowReader
import utopia.vault.nosql.read.parse.ParseTableModel
import vf.myapidoc.database.props.api.VersionedDocElementDbProps

import scala.util.Success

/**
  * Common trait for factories that parse versioned doc element data from database-originated models
  * @tparam A Type of read instances
  * @author Mikko Hilpinen
  * @since 28.10.2026, v0.1
  */
trait VersionedDocElementDbReaderLike[+A] extends DbRowReader[A] with ParseTableModel[A] with HasTableAsTarget
{
	// ABSTRACT	--------------------
	
	/**
	  * Database properties used when parsing column data
	  */
	def dbProps: VersionedDocElementDbProps
	
	/**
	  * @param model            Model from which additional data may be read
	  * @param id               ID to assign to the read/parsed versioned doc element
	  * @param originalUpdateId original update id to assign to the new versioned doc element
	  * @param removingUpdateId removing update id to assign to the new versioned doc element
	  * @return A versioned doc element with the specified data
	  */
	protected def apply(model: HasProperties, id: Int, originalUpdateId: Int, 
		removingUpdateId: Option[Int]): A
	
	
	// IMPLEMENTED	--------------------
	
	override def fromValid(valid: Model) = 
		Success(apply(valid, valid(dbProps.id.name).getInt, 
			originalUpdateId = valid(dbProps.originalUpdateId.name).getInt, 
			removingUpdateId = valid(dbProps.removingUpdateId.name).int))
}

