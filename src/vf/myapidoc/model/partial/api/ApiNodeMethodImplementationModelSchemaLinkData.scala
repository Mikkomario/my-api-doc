package vf.myapidoc.model.partial.api

import utopia.flow.collection.immutable.Single
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.InstantType
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.time.Now

import java.time.Instant

object ApiNodeMethodImplementationModelSchemaLinkData 
	extends FromModelFactoryWithSchema[ApiNodeMethodImplementationModelSchemaLinkData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Vector(PropertyDeclaration("implementationId", IntType, 
			Single("implementation_id")), PropertyDeclaration("modelTypeId", IntType, 
			Single("model_type_id")), PropertyDeclaration("created", InstantType, isOptional = true), 
			PropertyDeclaration("deprecatedAfter", InstantType, Single("deprecated_after"), 
			isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		ApiNodeMethodImplementationModelSchemaLinkData(valid("implementationId").getInt, 
			valid("modelTypeId").getInt, valid("created").getInstant, valid("deprecatedAfter").instant)
	
	
	// OTHER	--------------------
	
	/**
	  * Creates a new api node method implementation model schema link
	  * @param implementationId ID of the method implementation that uses the linked schema.
	  * @param modelTypeId      ID of the schema that defines the type of model accepted by the 
	  *                         linked implementation.
	  * @param created          Time when this api node method implementation model schema link was 
	  *                         added to the database
	  * @param deprecatedAfter  Time when this api node method implementation model schema link 
	  *                         became deprecated. None while this api node method implementation 
	  *                         model schema link is still valid.
	  * @return api node method implementation model schema link with the specified properties
	  */
	def apply(implementationId: Int, modelTypeId: Int, created: Instant = Now, 
		deprecatedAfter: Option[Instant] = None): ApiNodeMethodImplementationModelSchemaLinkData = 
		_ApiNodeMethodImplementationModelSchemaLinkData(implementationId, modelTypeId, created, 
			deprecatedAfter)
	
	
	// NESTED	--------------------
	
	/**
	  * Concrete implementation of the api node method implementation model schema link data trait
	  * @param implementationId ID of the method implementation that uses the linked schema.
	  * @param modelTypeId      ID of the schema that defines the type of model accepted by the 
	  *                         linked implementation.
	  * @param created          Time when this api node method implementation model schema link was 
	  *                         added to the database
	  * @param deprecatedAfter  Time when this api node method implementation model schema link 
	  *                         became deprecated. None while this api node method implementation 
	  *                         model schema link is still valid.
	  * @author Mikko Hilpinen
	  * @since 22.10.2026
	  */
	private case class _ApiNodeMethodImplementationModelSchemaLinkData(implementationId: Int, 
		modelTypeId: Int, created: Instant = Now, deprecatedAfter: Option[Instant] = None) 
		extends ApiNodeMethodImplementationModelSchemaLinkData
	{
		// IMPLEMENTED	--------------------
		
		/**
		  * @param implementationId ID of the method implementation that uses the linked schema.
		  * @param modelTypeId      ID of the schema that defines the type of model accepted by the 
		  *                         linked implementation.
		  * @param created          Time when this api node method implementation model schema link was 
		  *                         added to the database
		  * @param deprecatedAfter  Time when this api node method implementation model schema link 
		  *                         became deprecated. None while this api node method implementation 
		  *                         model schema link is still valid.
		  */
		override def copyApiNodeMethodImplementationModelSchemaLink(implementationId: Int, modelTypeId: Int, 
			created: Instant = Now, deprecatedAfter: Option[Instant] = None) = 
			_ApiNodeMethodImplementationModelSchemaLinkData(implementationId, modelTypeId, created, 
				deprecatedAfter)
	}
}

/**
  * Links a model schema to to an API node's method implementation.
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait ApiNodeMethodImplementationModelSchemaLinkData 
	extends ApiNodeMethodImplementationModelSchemaLinkDataLike[ApiNodeMethodImplementationModelSchemaLinkData]
{
	// COMPUTED	--------------------
	
	/**
	  * Whether this api node method implementation model schema link has already been deprecated
	  */
	def isDeprecated = deprecatedAfter.isDefined
	
	/**
	  * Whether this api node method implementation model schema link is still valid (not deprecated)
	  */
	def isValid = !isDeprecated
}

