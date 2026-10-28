package vf.myapidoc.database.storable.auth

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.{DeprecatesAfterDefined, HasIdProperty}
import utopia.vault.nosql.storable.StorableFactory
import utopia.vault.store.{FromIdFactory, HasId}
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.database.props.api.VersionedDocElementDbProps
import vf.myapidoc.database.storable.api.{VersionedDocElementDbModel, VersionedDocElementDbModelFactoryLike, VersionedDocElementDbModelLike}
import vf.myapidoc.model.enumeration.TokenUseType
import vf.myapidoc.model.factory.auth.AuthTokenTypeFactory
import vf.myapidoc.model.partial.auth.AuthTokenTypeData
import vf.myapidoc.model.stored.auth.AuthTokenType

import java.time.Instant

/**
  * Used for constructing AuthTokenTypeDbModel instances and for inserting auth token types to 
  * the database
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
object AuthTokenTypeDbModel 
	extends VersionedDocElementDbModelFactoryLike[AuthTokenTypeDbModel, AuthTokenType, AuthTokenTypeData] 
		with AuthTokenTypeFactory[AuthTokenTypeDbModel] with VersionedDocElementDbProps
{
	// ATTRIBUTES	--------------------
	
	override val id = DbPropertyDeclaration("id", index)
	/**
	  * Database property used for interacting with original update ids
	  */
	override lazy val originalUpdateId = property("originalUpdateId")
	/**
	  * Database property used for interacting with api ids
	  */
	lazy val apiId = property("apiId")
	/**
	  * Database property used for interacting with names
	  */
	lazy val name = property("name")
	/**
	  * Database property used for interacting with names without spaces
	  */
	lazy val nameNoSpaces = property("nameNoSpaces")
	/**
	  * Database property used for interacting with removing update ids
	  */
	override lazy val removingUpdateId = property("removingUpdateId")
	/**
	  * Database property used for interacting with use types
	  */
	lazy val useType = property("useTypeId")
	
	
	// IMPLEMENTED	--------------------
	
	override def table = MyApiDocTables.authTokenType
	
	override def apply(data: AuthTokenTypeData): AuthTokenTypeDbModel = 
		apply(None, Some(data.originalUpdateId), Some(data.apiId), data.name, data.nameNoSpaces, 
			data.removingUpdateId, Some(data.useType))
	
	override def withApiId(apiId: Int) = apply(apiId = Some(apiId))
	override def withId(id: Int) = apply(id = Some(id))
	override def withName(name: String) = apply(name = name)
	override def withNameNoSpaces(nameNoSpaces: String) = apply(nameNoSpaces = nameNoSpaces)
	override def withOriginalUpdateId(originalUpdateId: Int) = apply(originalUpdateId = Some(originalUpdateId))
	override def withRemovingUpdateId(removingUpdateId: Int) = apply(removingUpdateId = Some(removingUpdateId))
	override def withUseType(useType: TokenUseType) = apply(useType = Some(useType))
	
	override protected def complete(id: Value, data: AuthTokenTypeData) = AuthTokenType(id.getInt, data)
}

/**
  * Used for interacting with AuthTokenTypes in the database
  * @param id auth token type database id
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AuthTokenTypeDbModel(id: Option[Int] = None, originalUpdateId: Option[Int] = None,
                                apiId: Option[Int] = None, name: String = "", nameNoSpaces: String = "",
                                removingUpdateId: Option[Int] = None, useType: Option[TokenUseType] = None)
	extends VersionedDocElementDbModel with VersionedDocElementDbModelLike[AuthTokenTypeDbModel] 
		with AuthTokenTypeFactory[AuthTokenTypeDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties: Seq[(String, Value)] = 
		super[VersionedDocElementDbModelLike].valueProperties ++ Vector(AuthTokenTypeDbModel.apiId.name -> apiId, 
			AuthTokenTypeDbModel.name.name -> name, AuthTokenTypeDbModel.nameNoSpaces.name -> nameNoSpaces, 
			AuthTokenTypeDbModel.useType.name -> useType.map[Value] { e => e.id }.getOrElse(Value.empty))
	
	
	// IMPLEMENTED	--------------------
	
	override def dbProps = AuthTokenTypeDbModel
	
	override def table = AuthTokenTypeDbModel.table
	
	/**
	  * @param id               ID to assign to the new model (default = currently assigned id)
	  * @param originalUpdateId original update id to assign to the new model (default = currently 
	  *                         assigned value)
	  * @param removingUpdateId removing update id to assign to the new model (default = currently 
	  *                         assigned value)
	  */
	override def copyVersionedDocElement(id: Option[Int] = id, 
		originalUpdateId: Option[Int] = originalUpdateId, removingUpdateId: Option[Int] = removingUpdateId) = 
		copy(id = id, originalUpdateId = originalUpdateId, removingUpdateId = removingUpdateId)
	
	override def withApiId(apiId: Int) = copy(apiId = Some(apiId))
	override def withId(id: Int) = copy(id = Some(id))
	override def withName(name: String) = copy(name = name)
	override def withNameNoSpaces(nameNoSpaces: String) = copy(nameNoSpaces = nameNoSpaces)
	override def withUseType(useType: TokenUseType) = copy(useType = Some(useType))
}

