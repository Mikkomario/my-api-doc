package vf.myapidoc.database.reader.test

import utopia.flow.generic.model.immutable.Model
import utopia.flow.parse.file.FileExtensions._
import utopia.vault.model.template.HasTableAsTarget
import utopia.vault.nosql.read.DbRowReader
import utopia.vault.nosql.read.parse.ParseTableModel
import vf.myapidoc.database.storable.test.TestsFileDbModel
import vf.myapidoc.model.partial.test.TestsFileData
import vf.myapidoc.model.stored.test.TestsFile

import scala.util.Success

import java.nio.file.Path

/**
  * Used for reading tests file data from the DB
  * @author Mikko Hilpinen
  * @since 01.11.2026, v0.1
  */
object TestsFileDbReader extends DbRowReader[TestsFile] with ParseTableModel[TestsFile] with HasTableAsTarget
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	val model = TestsFileDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def table = model.table
	
	override def fromValid(valid: Model) = 
		Success(TestsFile(valid(this.model.id.name).getInt, 
			TestsFileData(apiId = valid(this.model.apiId.name).getInt, 
			path = valid(this.model.path.name).getString: Path, 
			created = valid(this.model.created.name).getInstant)))
}

