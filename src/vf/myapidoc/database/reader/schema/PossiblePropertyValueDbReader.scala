package vf.myapidoc.database.reader.schema

import utopia.flow.generic.model.immutable.Model
import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
import utopia.vault.model.template.HasTableAsTarget
import utopia.vault.nosql.read.DbRowReader
import utopia.vault.nosql.read.parse.ParseTableModel
import vf.myapidoc.database.reader.api.VersionedDocElementDbReaderLike
import vf.myapidoc.database.storable.schema.PossiblePropertyValueDbModel
import vf.myapidoc.model.partial.schema.PossiblePropertyValueData
import vf.myapidoc.model.stored.schema.PossiblePropertyValue

import scala.util.Success

/**
  * Used for reading possible property value data from the DB
  * @author Mikko Hilpinen
  * @since 26.10.2026, v0.1
  */
object PossiblePropertyValueDbReader extends VersionedDocElementDbReaderLike[PossiblePropertyValue]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	override val dbProps = PossiblePropertyValueDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def table = dbProps.table
	
	/**
	  * @param model            Model from which additional data may be read
	  * @param id               ID to assign to the read/parsed versioned doc element
	  * @param originalUpdateId original update id to assign to the new versioned doc element
	  * @param removingUpdateId removing update id to assign to the new versioned doc element
	  */
	override protected def apply(model: HasProperties, id: Int, originalUpdateId: Int, removingUpdateId: Option[Int]) =
		PossiblePropertyValue(id, PossiblePropertyValueData(originalUpdateId = originalUpdateId, 
			propId = model(dbProps.propId.name).getInt, value = model(dbProps.value.name).getString, 
			removingUpdateId = removingUpdateId, description = model(dbProps.description.name).getString))
}

