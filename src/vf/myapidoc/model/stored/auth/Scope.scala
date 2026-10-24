package vf.myapidoc.model.stored.auth

import utopia.vault.store.{FromIdFactory, StandardStoredFactory, StoredModelConvertible}
import vf.myapidoc.database.access.auth.scope.AccessScope
import vf.myapidoc.model.factory.auth.ScopeFactoryWrapper
import vf.myapidoc.model.partial.auth.ScopeData

object Scope extends StandardStoredFactory[ScopeData, Scope]
{
	// ATTRIBUTES	--------------------
	
	override val dataFactory = ScopeData
}

/**
  * Represents a scope that has already been stored in the database. 
  * Represents an authorization scope in the documented API.
  * @param id   ID of this scope in the database
  * @param data Wrapped scope data
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class Scope(id: Int, data: ScopeData) 
	extends StoredModelConvertible[ScopeData] with FromIdFactory[Int, Scope] 
		with ScopeFactoryWrapper[ScopeData, Scope]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this scope in the database
	  */
	def access = AccessScope(id)
	
	
	// IMPLEMENTED	--------------------
	
	override protected def wrappedFactory = data
	
	override def withId(id: Int) = copy(id = id)
	
	override protected def wrap(data: ScopeData) = copy(data = data)
}

