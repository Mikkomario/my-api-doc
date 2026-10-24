package vf.myapidoc.model.factory.api

/**
  * Common trait for request body schema-related factories which allow construction with 
  * individual properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait RequestBodySchemaFactory[+A] extends ApiNodeMethodImplementationModelSchemaLinkFactory[A]
{
	// ABSTRACT	--------------------
	
	/**
	  * @param customContentType New custom content type to assign
	  * @return Copy of this item with the specified custom content type
	  */
	def withCustomContentType(customContentType: String): A
	
	/**
	  * @param description New description to assign
	  * @return Copy of this item with the specified description
	  */
	def withDescription(description: String): A
	
	/**
	  * @param isArray New is array to assign
	  * @return Copy of this item with the specified is array
	  */
	def withIsArray(isArray: Boolean): A
}

