package vf.myapidoc.database.storable.auth

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.{DeprecatesAfterDefined, HasIdProperty}
import utopia.vault.nosql.storable.StorableFactory
import utopia.vault.store.{FromIdFactory, HasId}
import vf.myapidoc.database.MyApiDocTables
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
	extends StorableFactory[AuthTokenTypeDbModel, AuthTokenType, AuthTokenTypeData] 
		with FromIdFactory[Int, AuthTokenTypeDbModel] with HasIdProperty 
		with AuthTokenTypeFactory[AuthTokenTypeDbModel] with DeprecatesAfterDefined
{
	// ATTRIBUTES	--------------------
	
	override val id = DbPropertyDeclaration("id", index)
	
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
	  * Database property used for interacting with use types
	  */
	lazy val useType = property("useTypeId")
	
	/**
	  * Database property used for interacting with creation times
	  */
	lazy val created = property("created")
	
	/**
	  * Database property used for interacting with deprecation times
	  */
	lazy val deprecatedAfter = property("deprecatedAfter")
	
	override val deprecationColumn = deprecatedAfter
	
	
	// IMPLEMENTED	--------------------
	
	override def table = MyApiDocTables.authTokenType
	
	override def apply(data: AuthTokenTypeData): AuthTokenTypeDbModel = 
		apply(None, Some(data.apiId), data.name, data.nameNoSpaces, Some(data.useType), Some(data.created), 
			data.deprecatedAfter)
	
	override def withApiId(apiId: Int) = apply(apiId = Some(apiId))
	
	override def withCreated(created: Instant) = apply(created = Some(created))
	
	override def withDeprecatedAfter(deprecatedAfter: Instant) = apply(deprecatedAfter =Some(deprecatedAfter))
	
	override def withId(id: Int) = apply(id = Some(id))
	
	override def withName(name: String) = apply(name = name)
	
	override def withNameNoSpaces(nameNoSpaces: String) = apply(nameNoSpaces = nameNoSpaces)
	
	override def withUseType(useType: TokenUseType) = apply(useType = Some(useType))
	
	override protected def complete(id: Value, data: AuthTokenTypeData) = AuthTokenType(id.getInt, data)
}

/**
  * Used for interacting with AuthTokenTypes in the database
  * @param id auth token type database id
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AuthTokenTypeDbModel(id: Option[Int] = None, apiId: Option[Int] = None, name: String = "", 
	nameNoSpaces: String = "", useType: Option[TokenUseType] = None, created: Option[Instant] = None, 
	deprecatedAfter: Option[Instant] = None) 
	extends Storable with HasId[Option[Int]] with FromIdFactory[Int, AuthTokenTypeDbModel] 
		with AuthTokenTypeFactory[AuthTokenTypeDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties: Seq[(String, Value)] = 
		Vector(AuthTokenTypeDbModel.id.name -> id, AuthTokenTypeDbModel.apiId.name -> apiId, 
			AuthTokenTypeDbModel.name.name -> name, AuthTokenTypeDbModel.nameNoSpaces.name -> nameNoSpaces, 
			AuthTokenTypeDbModel.useType.name -> useType.map[Value] { e => e.id }.getOrElse(Value.empty), 
			AuthTokenTypeDbModel.created.name -> created, 
			AuthTokenTypeDbModel.deprecatedAfter.name -> deprecatedAfter)
	
	
	// IMPLEMENTED	--------------------
	
	override def table = AuthTokenTypeDbModel.table
	
	override def withApiId(apiId: Int) = copy(apiId = Some(apiId))
	
	override def withCreated(created: Instant) = copy(created = Some(created))
	
	override def withDeprecatedAfter(deprecatedAfter: Instant) = copy(deprecatedAfter = Some(deprecatedAfter))
	
	override def withId(id: Int) = copy(id = Some(id))
	
	override def withName(name: String) = copy(name = name)
	
	override def withNameNoSpaces(nameNoSpaces: String) = copy(nameNoSpaces = nameNoSpaces)
	
	override def withUseType(useType: TokenUseType) = copy(useType = Some(useType))
}

