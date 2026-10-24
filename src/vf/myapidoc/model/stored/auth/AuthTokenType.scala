package vf.myapidoc.model.stored.auth

import utopia.vault.store.{FromIdFactory, StandardStoredFactory, StoredModelConvertible}
import vf.myapidoc.database.access.auth.token.AccessAuthTokenType
import vf.myapidoc.model.factory.auth.AuthTokenTypeFactoryWrapper
import vf.myapidoc.model.partial.auth.AuthTokenTypeData

object AuthTokenType extends StandardStoredFactory[AuthTokenTypeData, AuthTokenType]
{
	// ATTRIBUTES	--------------------
	
	override val dataFactory = AuthTokenTypeData
}

/**
  * Represents a auth token type that has already been stored in the database. 
  * Represents some (custom) token type used by the documented API.
  * @param id   ID of this auth token type in the database
  * @param data Wrapped auth token type data
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AuthTokenType(id: Int, data: AuthTokenTypeData) 
	extends StoredModelConvertible[AuthTokenTypeData] with FromIdFactory[Int, AuthTokenType] 
		with AuthTokenTypeFactoryWrapper[AuthTokenTypeData, AuthTokenType]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this auth token type in the database
	  */
	def access = AccessAuthTokenType(id)
	
	
	// IMPLEMENTED	--------------------
	
	override protected def wrappedFactory = data
	
	override def withId(id: Int) = copy(id = id)
	
	override protected def wrap(data: AuthTokenTypeData) = copy(data = data)
}

