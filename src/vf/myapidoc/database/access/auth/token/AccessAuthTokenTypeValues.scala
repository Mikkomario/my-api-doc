package vf.myapidoc.database.access.auth.token

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.AccessManyColumns
import vf.myapidoc.database.access.api.update.AccessVersionedDocElementValues
import vf.myapidoc.database.storable.auth.AuthTokenTypeDbModel
import vf.myapidoc.model.enumeration.TokenUseType

/**
  * Used for accessing auth token type values from the DB
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessAuthTokenTypeValues(access: AccessManyColumns) extends AccessVersionedDocElementValues
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing auth token type database properties
	  */
	override val model = AuthTokenTypeDbModel
	
	/**
	  * ID of the API where this token type is used.
	  */
	lazy val apiIds = apply(model.apiId) { v => v.getInt }
	/**
	  * (Human-readable) name of this token type
	  */
	lazy val names = apply(model.name) { v => v.getString }
	/**
	  * A lowercase copy of this token's name with all whitespaces removed. Used for indexing and 
	  * search.
	  */
	lazy val namesWithoutSpaces = apply(model.nameNoSpaces) { v => v.getString }
	/**
	  * Determines how this token is applied.
	  */
	lazy val useTypes = apply(model.useType)(TokenUseType.fromValue)
}

