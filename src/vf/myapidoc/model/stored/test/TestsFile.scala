package vf.myapidoc.model.stored.test

import utopia.vault.store.{FromIdFactory, StandardStoredFactory, StoredModelConvertible}
import vf.myapidoc.database.access.test.file.AccessTestsFile
import vf.myapidoc.model.factory.test.TestsFileFactoryWrapper
import vf.myapidoc.model.partial.test.TestsFileData

object TestsFile extends StandardStoredFactory[TestsFileData, TestsFile]
{
	// ATTRIBUTES	--------------------
	
	override val dataFactory = TestsFileData
}

/**
  * Represents a tests file that has already been stored in the database. 
  * Documents the location of a single tests-file
  * @param id   ID of this tests file in the database
  * @param data Wrapped tests file data
  * @author Mikko Hilpinen
  * @since 01.11.2026, v0.1
  */
case class TestsFile(id: Int, data: TestsFileData) 
	extends StoredModelConvertible[TestsFileData] with FromIdFactory[Int, TestsFile] 
		with TestsFileFactoryWrapper[TestsFileData, TestsFile]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this tests file in the database
	  */
	def access = AccessTestsFile(id)
	
	
	// IMPLEMENTED	--------------------
	
	override def withId(id: Int) = copy(id = id)
	
	override def wrap(data: TestsFileData) = copy(data = data)
}

