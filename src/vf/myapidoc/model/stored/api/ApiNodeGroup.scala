package vf.myapidoc.model.stored.api

import utopia.vault.store.{FromIdFactory, StandardStoredFactory, StoredModelConvertible}
import vf.myapidoc.database.access.api.node.group.AccessApiNodeGroup
import vf.myapidoc.model.factory.api.ApiNodeGroupFactoryWrapper
import vf.myapidoc.model.partial.api.ApiNodeGroupData

object ApiNodeGroup extends StandardStoredFactory[ApiNodeGroupData, ApiNodeGroup]
{
	// ATTRIBUTES	--------------------
	
	override val dataFactory = ApiNodeGroupData
	
	
	// OTHER	--------------------
	
	/**
	  * Creates a new api node group
	  * @param id   ID of this api node group in the database
	  * @param data Wrapped api node group data
	  * @return api node group with the specified id and wrapped data
	  */
	def apply(id: Int, data: ApiNodeGroupData): ApiNodeGroup = _ApiNodeGroup(id, data)
	
	
	// NESTED	--------------------
	
	/**
	  * Concrete implementation of the api node group trait
	  * @param id   ID of this api node group in the database
	  * @param data Wrapped api node group data
	  * @author Mikko Hilpinen
	  * @since 22.10.2026
	  */
	private case class _ApiNodeGroup(id: Int, data: ApiNodeGroupData) extends ApiNodeGroup
	{
		// IMPLEMENTED	--------------------
		
		override def withId(id: Int) = copy(id = id)
		
		override protected def wrap(data: ApiNodeGroupData) = copy(data = data)
	}
}

/**
  * Represents a api node group that has already been stored in the database. 
  * Defines a group of API nodes
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait ApiNodeGroup 
	extends StoredModelConvertible[ApiNodeGroupData] with FromIdFactory[Int, ApiNodeGroup] 
		with ApiNodeGroupFactoryWrapper[ApiNodeGroupData, ApiNodeGroup]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this api node group in the database
	  */
	def access = AccessApiNodeGroup(id)
	
	
	// IMPLEMENTED	--------------------
	
	override protected def wrappedFactory = data
}

