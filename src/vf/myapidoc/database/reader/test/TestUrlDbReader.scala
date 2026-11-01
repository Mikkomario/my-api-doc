package vf.myapidoc.database.reader.test

import utopia.flow.generic.model.immutable.Model
import utopia.vault.model.template.HasTableAsTarget
import utopia.vault.nosql.read.DbRowReader
import utopia.vault.nosql.read.parse.ParseTableModel
import vf.myapidoc.database.storable.test.TestUrlDbModel
import vf.myapidoc.model.partial.test.TestUrlData
import vf.myapidoc.model.stored.test.TestUrl

import scala.util.Success

/**
  * Used for reading test url data from the DB
  * @author Mikko Hilpinen
  * @since 01.11.2026, v0.1
  */
object TestUrlDbReader extends DbRowReader[TestUrl] with ParseTableModel[TestUrl] with HasTableAsTarget
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	val model = TestUrlDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def table = model.table
	
	override def fromValid(valid: Model) = 
		Success(TestUrl(valid(this.model.id.name).getInt, 
			TestUrlData(apiId = valid(this.model.apiId.name).getInt, 
			rootUrl = valid(this.model.rootUrl.name).getString, 
			created = valid(this.model.created.name).getInstant)))
}

