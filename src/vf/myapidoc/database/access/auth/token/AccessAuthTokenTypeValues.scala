package vf.myapidoc.database.access.auth.token

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.{AccessManyColumns, AccessValues}
import vf.myapidoc.database.storable.auth.AuthTokenTypeDbModel
import vf.myapidoc.model.enumeration.TokenUseType
import vf.myapidoc.util.Common._

/**
  * Used for accessing auth token type values from the DB
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessAuthTokenTypeValues(access: AccessManyColumns) extends AccessValues
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing auth token type database properties
	  */
	val model = AuthTokenTypeDbModel
	
	/**
	  * Access to auth token type ids
	  */
	lazy val ids = apply(model.index) { _.getInt }
	
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
	lazy val useTypes = {
		newScribe("DB.Access.AuthTokenType.Values").use 
		{
			 implicit scribe =>
				apply(model.useType).logging { v => TokenUseType.fromValue(v) }
		}
	}
	
	/**
	  * Time when this auth token type was added to the database
	  */
	lazy val creationTimes = apply(model.created) { v => v.getInstant }
	
	/**
	  * Time when this auth token type became deprecated. None while this auth token type is still 
	  * valid.
	  */
	lazy val deprecationTimes = apply(model.deprecatedAfter).flatten { v => v.instant }
}

