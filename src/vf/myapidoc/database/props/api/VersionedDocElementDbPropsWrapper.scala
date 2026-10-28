package vf.myapidoc.database.props.api

/**
  * Common trait for interfaces that provide access to versioned doc element database properties 
  * by wrapping a VersionedDocElementDbProps
  * @author Mikko Hilpinen
  * @since 28.10.2026, v0.1
  */
trait VersionedDocElementDbPropsWrapper extends VersionedDocElementDbProps
{
	// ABSTRACT	--------------------
	
	/**
	  * The wrapped versioned doc element database properties
	  */
	protected def versionedDocElementDbProps: VersionedDocElementDbProps
	
	
	// IMPLEMENTED	--------------------
	
	override def id = versionedDocElementDbProps.id
	
	override def originalUpdateId = versionedDocElementDbProps.originalUpdateId
	
	override def removingUpdateId = versionedDocElementDbProps.removingUpdateId
}

