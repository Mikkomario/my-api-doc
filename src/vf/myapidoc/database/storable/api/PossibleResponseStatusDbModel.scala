package vf.myapidoc.database.storable.api

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.flow.util.UncertainBoolean
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.{DeprecatesAfterDefined, HasIdProperty}
import utopia.vault.nosql.storable.StorableFactory
import utopia.vault.store.{FromIdFactory, HasId}
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.model.factory.api.PossibleResponseStatusFactory
import vf.myapidoc.model.partial.api.PossibleResponseStatusData
import vf.myapidoc.model.stored.api.PossibleResponseStatus

import java.time.Instant

/**
  * Used for constructing PossibleResponseStatusDbModel instances and for inserting possible 
  * response statuses to the database
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
object PossibleResponseStatusDbModel 
	extends StorableFactory[PossibleResponseStatusDbModel, PossibleResponseStatus, PossibleResponseStatusData] 
		with FromIdFactory[Int, PossibleResponseStatusDbModel] with HasIdProperty 
		with PossibleResponseStatusFactory[PossibleResponseStatusDbModel] with DeprecatesAfterDefined
{
	// ATTRIBUTES	--------------------
	
	override val id = DbPropertyDeclaration("id", index)
	
	/**
	  * Database property used for interacting with implementation ids
	  */
	lazy val implementationId = property("implementationId")
	
	/**
	  * Database property used for interacting with status codes
	  */
	lazy val statusCode = property("statusCode")
	
	/**
	  * Database property used for interacting with descriptions
	  */
	lazy val description = property("description")
	
	/**
	  * Database property used for interacting with creation times
	  */
	lazy val created = property("created")
	
	/**
	  * Database property used for interacting with deprecation times
	  */
	lazy val deprecatedAfter = property("deprecatedAfter")
	
	/**
	  * Database property used for interacting with have bodies
	  */
	lazy val hasBody = property("hasBody")
	
	override val deprecationColumn = deprecatedAfter
	
	
	// IMPLEMENTED	--------------------
	
	override def table = MyApiDocTables.possibleResponseStatus
	
	override def apply(data: PossibleResponseStatusData): PossibleResponseStatusDbModel = 
		apply(None, Some(data.implementationId), Some(data.statusCode), data.description, Some(data.created), 
			data.deprecatedAfter, data.hasBody.exact)
	
	override def withCreated(created: Instant) = apply(created = Some(created))
	
	override def withDeprecatedAfter(deprecatedAfter: Instant) = apply(deprecatedAfter = 
		Some(deprecatedAfter))
	
	override def withDescription(description: String) = apply(description = description)
	
	override def withHasBody(hasBody: UncertainBoolean) = apply(hasBody = hasBody.exact)
	
	override def withId(id: Int) = apply(id = Some(id))
	
	override def withImplementationId(implementationId: Int) = apply(implementationId = 
		Some(implementationId))
	
	override def withStatusCode(statusCode: Int) = apply(statusCode = Some(statusCode))
	
	override protected def complete(id: Value, data: PossibleResponseStatusData) = 
		PossibleResponseStatus(id.getInt, data)
}

/**
  * Used for interacting with PossibleResponseStatuses in the database
  * @param id possible response status database id
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class PossibleResponseStatusDbModel(id: Option[Int] = None, implementationId: Option[Int] = None, 
	statusCode: Option[Int] = None, description: String = "", created: Option[Instant] = None, 
	deprecatedAfter: Option[Instant] = None, hasBody: Option[Boolean] = None) 
	extends Storable with HasId[Option[Int]] with FromIdFactory[Int, PossibleResponseStatusDbModel] 
		with PossibleResponseStatusFactory[PossibleResponseStatusDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties: Seq[(String, Value)] = 
		Vector(PossibleResponseStatusDbModel.id.name -> id, 
			PossibleResponseStatusDbModel.implementationId.name -> implementationId, 
			PossibleResponseStatusDbModel.statusCode.name -> statusCode, 
			PossibleResponseStatusDbModel.description.name -> description, 
			PossibleResponseStatusDbModel.created.name -> created, 
			PossibleResponseStatusDbModel.deprecatedAfter.name -> deprecatedAfter, 
			PossibleResponseStatusDbModel.hasBody.name -> hasBody)
	
	
	// IMPLEMENTED	--------------------
	
	override def table = PossibleResponseStatusDbModel.table
	
	override def withCreated(created: Instant) = copy(created = Some(created))
	
	override def withDeprecatedAfter(deprecatedAfter: Instant) = copy(deprecatedAfter = Some(deprecatedAfter))
	
	override def withDescription(description: String) = copy(description = description)
	
	override def withHasBody(hasBody: UncertainBoolean) = copy(hasBody = hasBody.exact)
	
	override def withId(id: Int) = copy(id = Some(id))
	
	override def withImplementationId(implementationId: Int) = copy(implementationId = Some(implementationId))
	
	override def withStatusCode(statusCode: Int) = copy(statusCode = Some(statusCode))
}

