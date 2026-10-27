package vf.myapidoc.database.props.api

/**
  * Common trait for interfaces that provide access to body schema database properties by 
  * wrapping a BodySchemaDbProps
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
trait BodySchemaDbPropsWrapper extends BodySchemaDbProps
{
	// ABSTRACT	--------------------
	
	/**
	  * The wrapped body schema database properties
	  */
	protected def bodySchemaDbProps: BodySchemaDbProps
	
	
	// IMPLEMENTED	--------------------
	
	override def created = bodySchemaDbProps.created
	
	override def dataType = bodySchemaDbProps.dataType
	
	override def deprecatedAfter = bodySchemaDbProps.deprecatedAfter
	
	override def id = bodySchemaDbProps.id
	
	override def isArray = bodySchemaDbProps.isArray
	
	override def modelTypeId = bodySchemaDbProps.modelTypeId
	
	override def parentId = bodySchemaDbProps.parentId
}

