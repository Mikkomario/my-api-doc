package vf.myapidoc.database.props.api

/**
  * Common trait for interfaces that provide access to api node method implementation model 
  * schema link database properties by wrapping a 
  * ApiNodeMethodImplementationModelSchemaLinkDbProps
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait ApiNodeMethodImplementationModelSchemaLinkDbPropsWrapper 
	extends ApiNodeMethodImplementationModelSchemaLinkDbProps
{
	// ABSTRACT	--------------------
	
	/**
	  * The wrapped api node method implementation model schema link database properties
	  */
	protected
		 def apiNodeMethodImplementationModelSchemaLinkDbProps: ApiNodeMethodImplementationModelSchemaLinkDbProps
	
	
	// IMPLEMENTED	--------------------
	
	override def created = apiNodeMethodImplementationModelSchemaLinkDbProps.created
	
	override def deprecatedAfter = apiNodeMethodImplementationModelSchemaLinkDbProps.deprecatedAfter
	
	override def id = apiNodeMethodImplementationModelSchemaLinkDbProps.id
	
	override def implementationId = apiNodeMethodImplementationModelSchemaLinkDbProps.implementationId
	
	override def modelTypeId = apiNodeMethodImplementationModelSchemaLinkDbProps.modelTypeId
}

