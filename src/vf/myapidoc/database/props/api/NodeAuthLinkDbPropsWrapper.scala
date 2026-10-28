package vf.myapidoc.database.props.api

/**
  * Common trait for interfaces that provide access to node auth link database properties by 
  * wrapping a NodeAuthLinkDbProps
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait NodeAuthLinkDbPropsWrapper extends NodeAuthLinkDbProps
{
	// ABSTRACT	--------------------
	
	/**
	  * The wrapped node auth link database properties
	  */
	protected def nodeAuthLinkDbProps: NodeAuthLinkDbProps
	
	
	// IMPLEMENTED	--------------------
	
	override def authId = nodeAuthLinkDbProps.authId
	override def description = nodeAuthLinkDbProps.description
	override def id = nodeAuthLinkDbProps.id
	override def method = nodeAuthLinkDbProps.method
	override def nodeId = nodeAuthLinkDbProps.nodeId
	override def originalUpdateId = nodeAuthLinkDbProps.originalUpdateId
	override def removingUpdateId = nodeAuthLinkDbProps.removingUpdateId
}

