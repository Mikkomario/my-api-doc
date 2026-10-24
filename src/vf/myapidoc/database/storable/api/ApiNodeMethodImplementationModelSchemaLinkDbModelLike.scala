package vf.myapidoc.database.storable.api

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.Storable
import utopia.vault.store.{FromIdFactory, HasId}
import vf.myapidoc.database.props.api.ApiNodeMethodImplementationModelSchemaLinkDbProps
import vf.myapidoc.model.factory.api.ApiNodeMethodImplementationModelSchemaLinkFactory

import java.time.Instant

/**
  * Common trait for database models used for interacting with api node method implementation 
  * model schema link data in the database
  * @tparam Repr Type of this DB model
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait ApiNodeMethodImplementationModelSchemaLinkDbModelLike[+Repr] 
	extends Storable with HasId[Option[Int]] with FromIdFactory[Int, Repr] 
		with ApiNodeMethodImplementationModelSchemaLinkFactory[Repr]
{
	// ABSTRACT	--------------------
	
	def implementationId: Option[Int]
	
	def modelTypeId: Option[Int]
	
	def created: Option[Instant]
	
	def deprecatedAfter: Option[Instant]
	
	/**
	  * Access to the database properties which are utilized in this model
	  */
	def dbProps: ApiNodeMethodImplementationModelSchemaLinkDbProps
	
	/**
	  * @param id               Id to assign to the new model (default = currently assigned id)
	  * @param implementationId implementation id to assign to the new model (default = currently 
	  *                         assigned value)
	  * @param modelTypeId      model type id to assign to the new model (default = currently 
	  *                         assigned value)
	  * @param created          created to assign to the new model (default = currently assigned 
	  *                         value)
	  * @param deprecatedAfter  deprecated after to assign to the new model (default = currently 
	  *                         assigned value)
	  * @return Copy of this model with the specified api node method implementation model schema link 
	  * properties
	  */
	protected def copyApiNodeMethodImplementationModelSchemaLink(id: Option[Int] = id, 
		implementationId: Option[Int] = implementationId, modelTypeId: Option[Int] = modelTypeId, 
		created: Option[Instant] = created, deprecatedAfter: Option[Instant] = deprecatedAfter): Repr
	
	
	// IMPLEMENTED	--------------------
	
	override def valueProperties: Seq[(String, Value)] = 
		Vector(dbProps.id.name -> id, dbProps.implementationId.name -> implementationId, 
			dbProps.modelTypeId.name -> modelTypeId, dbProps.created.name -> created, 
			dbProps.deprecatedAfter.name -> deprecatedAfter)
	
	override def withCreated(created: Instant) = 
		copyApiNodeMethodImplementationModelSchemaLink(created = Some(created))
	
	override def withDeprecatedAfter(deprecatedAfter: Instant) = 
		copyApiNodeMethodImplementationModelSchemaLink(deprecatedAfter = Some(deprecatedAfter))
	
	override def withId(id: Int) = copyApiNodeMethodImplementationModelSchemaLink(id = Some(id))
	
	override def withImplementationId(implementationId: Int) = 
		copyApiNodeMethodImplementationModelSchemaLink(implementationId = Some(implementationId))
	
	override def withModelTypeId(modelTypeId: Int) = 
		copyApiNodeMethodImplementationModelSchemaLink(modelTypeId = Some(modelTypeId))
}

