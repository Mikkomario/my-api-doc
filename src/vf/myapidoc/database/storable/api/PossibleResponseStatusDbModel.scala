package vf.myapidoc.database.storable.api

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.flow.util.UncertainBoolean
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.{DeprecatesAfterDefined, HasIdProperty}
import utopia.vault.nosql.storable.StorableFactory
import utopia.vault.store.{FromIdFactory, HasId}
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.database.props.api.VersionedDocElementDbProps
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
	extends VersionedDocElementDbModelFactoryLike[PossibleResponseStatusDbModel, PossibleResponseStatus, PossibleResponseStatusData] 
		with PossibleResponseStatusFactory[PossibleResponseStatusDbModel] with VersionedDocElementDbProps
{
	// ATTRIBUTES	--------------------
	
	override val id = DbPropertyDeclaration("id", index)
	/**
	  * Database property used for interacting with original update ids
	  */
	override lazy val originalUpdateId = property("originalUpdateId")
	/**
	  * Database property used for interacting with implementation ids
	  */
	lazy val implementationId = property("implementationId")
	/**
	  * Database property used for interacting with status codes
	  */
	lazy val statusCode = property("statusCode")
	/**
	  * Database property used for interacting with removing update ids
	  */
	override lazy val removingUpdateId = property("removingUpdateId")
	/**
	  * Database property used for interacting with descriptions
	  */
	lazy val description = property("description")
	/**
	  * Database property used for interacting with have bodies
	  */
	lazy val hasBody = property("hasBody")
	
	
	// IMPLEMENTED	--------------------
	
	override def table = MyApiDocTables.possibleResponseStatus
	
	override def apply(data: PossibleResponseStatusData): PossibleResponseStatusDbModel = 
		apply(None, Some(data.originalUpdateId), Some(data.implementationId), Some(data.statusCode), 
			data.removingUpdateId, data.description, data.hasBody.exact)
	
	override def withDescription(description: String) = apply(description = description)
	override def withHasBody(hasBody: UncertainBoolean) = apply(hasBody = hasBody.exact)
	override def withId(id: Int) = apply(id = Some(id))
	override def withImplementationId(implementationId: Int) = apply(implementationId =Some(implementationId))
	override def withOriginalUpdateId(originalUpdateId: Int) = apply(originalUpdateId = 
		Some(originalUpdateId))
	override def withRemovingUpdateId(removingUpdateId: Int) = apply(removingUpdateId = 
		Some(removingUpdateId))
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
case class PossibleResponseStatusDbModel(id: Option[Int] = None, originalUpdateId: Option[Int] = None,
                                         implementationId: Option[Int] = None, statusCode: Option[Int] = None,
                                         removingUpdateId: Option[Int] = None, description: String = "",
                                         hasBody: Option[Boolean] = None)
	extends VersionedDocElementDbModel with VersionedDocElementDbModelLike[PossibleResponseStatusDbModel] 
		with PossibleResponseStatusFactory[PossibleResponseStatusDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties: Seq[(String, Value)] = 
		super[VersionedDocElementDbModelLike].valueProperties ++ Vector(PossibleResponseStatusDbModel.implementationId.name -> implementationId, 
			PossibleResponseStatusDbModel.statusCode.name -> statusCode, 
			PossibleResponseStatusDbModel.description.name -> description, 
			PossibleResponseStatusDbModel.hasBody.name -> hasBody)
	
	
	// IMPLEMENTED	--------------------
	
	override def dbProps = PossibleResponseStatusDbModel
	
	override def table = PossibleResponseStatusDbModel.table
	
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
	
	override def withDescription(description: String) = copy(description = description)
	override def withHasBody(hasBody: UncertainBoolean) = copy(hasBody = hasBody.exact)
	override def withId(id: Int) = copy(id = Some(id))
	override def withImplementationId(implementationId: Int) = copy(implementationId = Some(implementationId))
	override def withStatusCode(statusCode: Int) = copy(statusCode = Some(statusCode))
}

