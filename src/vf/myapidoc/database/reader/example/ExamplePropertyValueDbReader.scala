package vf.myapidoc.database.reader.example

import utopia.bunnymunch.jawn.JsonBunny
import utopia.flow.generic.model.immutable.Model
import utopia.vault.model.template.HasTableAsTarget
import utopia.vault.nosql.read.DbRowReader
import utopia.vault.nosql.read.parse.ParseTableModel
import vf.myapidoc.database.storable.example.ExamplePropertyValueDbModel
import vf.myapidoc.model.partial.example.ExamplePropertyValueData
import vf.myapidoc.model.stored.example.ExamplePropertyValue

import scala.util.Success

/**
  * Used for reading example property value data from the DB
  * @author Mikko Hilpinen
  * @since 30.10.2026, v0.1
  */
object ExamplePropertyValueDbReader 
	extends DbRowReader[ExamplePropertyValue] with ParseTableModel[ExamplePropertyValue] with HasTableAsTarget
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	val model = ExamplePropertyValueDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def table = model.table
	
	override def fromValid(valid: Model) = 
		Success(ExamplePropertyValue(valid(this.model.id.name).getInt, 
			ExamplePropertyValueData(responseId = valid(this.model.responseId.name).getInt, 
			propId = valid(this.model.propId.name).getInt, 
			value = valid(this.model.value.name).mapIfNotEmpty { v => JsonBunny.sureMunch(v.getString) })))
}

