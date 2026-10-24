package vf.myapidoc.model.stored.api

import utopia.vault.store.{FromIdFactory, StandardStoredFactory, StoredModelConvertible}
import vf.myapidoc.database.access.api.node.AccessApiNode
import vf.myapidoc.model.factory.api.ApiNodeFactoryWrapper
import vf.myapidoc.model.partial.api.ApiNodeData

object ApiNode extends StandardStoredFactory[ApiNodeData, ApiNode]
{
	// ATTRIBUTES	--------------------
	
	override val dataFactory = ApiNodeData
	
	
	// OTHER	--------------------
	
	/**
	  * Creates a new api node
	  * @param id   ID of this api node in the database
	  * @param data Wrapped api node data
	  * @return api node with the specified id and wrapped data
	  */
	def apply(id: Int, data: ApiNodeData): ApiNode = _ApiNode(id, data)
	
	
	// NESTED	--------------------
	
	/**
	  * Concrete implementation of the api node trait
	  * @param id   ID of this api node in the database
	  * @param data Wrapped api node data
	  * @author Mikko Hilpinen
	  * @since 15.10.2026
	  */
	private case class _ApiNode(id: Int, data: ApiNodeData) extends ApiNode
	{
		// IMPLEMENTED	--------------------
		
		override def withId(id: Int) = copy(id = id)
		
		override protected def wrap(data: ApiNodeData) = copy(data = data)
	}
}

/**
  * Represents a api node that has already been stored in the database. 
  * Represents an individual API node implementation
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
trait ApiNode 
	extends StoredModelConvertible[ApiNodeData] with FromIdFactory[Int, ApiNode] 
		with ApiNodeFactoryWrapper[ApiNodeData, ApiNode]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this api node in the database
	  */
	def access = AccessApiNode(id)
	
	
	// IMPLEMENTED	--------------------
	
	override protected def wrappedFactory = data
}

