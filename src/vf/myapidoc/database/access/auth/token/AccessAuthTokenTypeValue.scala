package vf.myapidoc.database.access.auth.token

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.AccessColumns.AccessColumn
import utopia.vault.nosql.targeting.columns.AccessValue
import vf.myapidoc.database.storable.auth.AuthTokenTypeDbModel
import vf.myapidoc.model.enumeration.TokenUseType

/**
  * Used for accessing individual auth token type values from the DB
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessAuthTokenTypeValue(access: AccessColumn) extends AccessValue
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing auth token type database properties
	  */
	val model = AuthTokenTypeDbModel
	
	/**
	  * Access to auth token type id
	  */
	lazy val id = apply(model.index).optional { _.int }
	
	/**
	  * ID of the API where this token type is used.
	  */
	lazy val apiId = apply(model.apiId).optional { v => v.int }
	
	/**
	  * (Human-readable) name of this token type
	  */
	lazy val name = apply(model.name) { v => v.getString }
	
	/**
	  * A lowercase copy of this token's name with all whitespaces removed. Used for indexing and 
	  * search.
	  */
	lazy val nameNoSpaces = apply(model.nameNoSpaces) { v => v.getString }
	
	/**
	  * Determines how this token is applied.
	  */
	lazy val useType = apply(model.useType).optional { v => TokenUseType.findForValue(v) }
	
	/**
	  * Time when this auth token type was added to the database
	  */
	lazy val created = apply(model.created).optional { v => v.instant }
	
	/**
	  * Time when this auth token type became deprecated. None while this auth token type is still 
	  * valid.
	  */
	lazy val deprecatedAfter = apply(model.deprecatedAfter).optional { v => v.instant }
}

