package vf.myapidoc.database.reader.example

import utopia.flow.generic.model.immutable.Model
import utopia.flow.parse.file.FileExtensions._
import utopia.vault.model.template.HasTableAsTarget
import utopia.vault.nosql.read.DbRowReader
import utopia.vault.nosql.read.parse.ParseTableModel
import vf.myapidoc.database.storable.example.ExampleResponseDbModel
import vf.myapidoc.model.partial.example.ExampleResponseData
import vf.myapidoc.model.stored.example.ExampleResponse

import scala.util.Success

import java.nio.file.Path

/**
  * Used for reading example response data from the DB
  * @author Mikko Hilpinen
  * @since 30.10.2026, v0.1
  */
object ExampleResponseDbReader 
	extends DbRowReader[ExampleResponse] with ParseTableModel[ExampleResponse] with HasTableAsTarget
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	val model = ExampleResponseDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def table = model.table
	
	override def fromValid(valid: Model) = 
		Success(ExampleResponse(valid(this.model.id.name).getInt, 
			ExampleResponseData(testedApiVersionId = valid(this.model.testedApiVersionId.name).getInt, 
			statusId = valid(this.model.statusId.name).getInt, 
			relativeFilePath = valid(this.model.relativeFilePath.name).getString: Path, 
			created = valid(this.model.created.name).getInstant)))
}

