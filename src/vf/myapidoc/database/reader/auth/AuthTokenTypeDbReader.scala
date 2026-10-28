package vf.myapidoc.database.reader.auth

import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
import vf.myapidoc.database.reader.api.VersionedDocElementDbReaderLike
import vf.myapidoc.database.storable.auth.AuthTokenTypeDbModel
import vf.myapidoc.model.enumeration.TokenUseType
import vf.myapidoc.model.partial.auth.AuthTokenTypeData
import vf.myapidoc.model.stored.auth.AuthTokenType

/**
  * Used for reading auth token type data from the DB
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
object AuthTokenTypeDbReader extends VersionedDocElementDbReaderLike[AuthTokenType]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	override val dbProps = AuthTokenTypeDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def table = dbProps.table
	
	/**
	  * @param model            Model from which additional data may be read
	  * @param id               ID to assign to the read/parsed versioned doc element
	  * @param originalUpdateId original update id to assign to the new versioned doc element
	  * @param removingUpdateId removing update id to assign to the new versioned doc element
	  */
	override protected def apply(model: HasProperties, id: Int, originalUpdateId: Int, removingUpdateId: Option[Int]) =
		AuthTokenType(id, AuthTokenTypeData(originalUpdateId = originalUpdateId, 
			apiId = model(dbProps.apiId.name).getInt, name = model(dbProps.name.name).getString, 
			nameNoSpaces = model(dbProps.nameNoSpaces.name).getString, removingUpdateId = removingUpdateId, 
			useType = TokenUseType.fromValue(model(dbProps.useType.name))
		))
}

