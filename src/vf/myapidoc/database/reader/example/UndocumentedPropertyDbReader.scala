package vf.myapidoc.database.reader.example

import utopia.bunnymunch.jawn.JsonBunny
import utopia.flow.generic.model.immutable.Model
import utopia.vault.model.template.HasTableAsTarget
import utopia.vault.nosql.read.DbRowReader
import utopia.vault.nosql.read.parse.ParseTableModel
import vf.myapidoc.database.storable.example.UndocumentedPropertyDbModel
import vf.myapidoc.model.partial.example.UndocumentedPropertyData
import vf.myapidoc.model.stored.example.UndocumentedProperty

import scala.util.Success

/**
  * Used for reading undocumented property data from the DB
  * @author Mikko Hilpinen
  * @since 12.11.2026, v0.1
  */
object UndocumentedPropertyDbReader 
	extends DbRowReader[UndocumentedProperty] with ParseTableModel[UndocumentedProperty] with HasTableAsTarget
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	val model = UndocumentedPropertyDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def table = model.table
	
	override def fromValid(valid: Model) = 
		Success(UndocumentedProperty(valid(this.model.id.name).getInt, 
			UndocumentedPropertyData(responseId = valid(this.model.responseId.name).getInt, 
			modelTypeId = valid(this.model.modelTypeId.name).getInt, 
			name = valid(this.model.name.name).getString, 
			value = valid(this.model.value.name).mapIfNotEmpty { v => JsonBunny.sureMunch(v.getString) })))
}

