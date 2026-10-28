package vf.myapidoc.model.stored.api

import utopia.vault.store.{FromIdFactory, StandardStoredFactory, StoredModelConvertible}
import vf.myapidoc.database.access.api.update.AccessDocUpdate
import vf.myapidoc.model.factory.api.DocUpdateFactoryWrapper
import vf.myapidoc.model.partial.api.DocUpdateData

object DocUpdate extends StandardStoredFactory[DocUpdateData, DocUpdate]
{
	// ATTRIBUTES	--------------------
	
	override val dataFactory = DocUpdateData
}

/**
  * Represents a doc update that has already been stored in the database. 
  * Represents an API documentation update
  * @param id   ID of this doc update in the database
  * @param data Wrapped doc update data
  * @author Mikko Hilpinen
  * @since 28.10.2026, v0.1
  */
case class DocUpdate(id: Int, data: DocUpdateData) 
	extends StoredModelConvertible[DocUpdateData] with FromIdFactory[Int, DocUpdate] 
		with DocUpdateFactoryWrapper[DocUpdateData, DocUpdate]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this doc update in the database
	  */
	def access = AccessDocUpdate(id)
	
	
	// IMPLEMENTED	--------------------
	
	override protected def wrappedFactory: DocUpdateData = data
	
	override def withId(id: Int) = copy(id = id)
	
	override protected def wrap(data: DocUpdateData) = copy(data = data)
}

