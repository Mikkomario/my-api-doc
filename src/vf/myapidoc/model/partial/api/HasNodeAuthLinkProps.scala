package vf.myapidoc.model.partial.api

import utopia.access.model.enumeration.Method
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Model
import utopia.flow.generic.model.template.ModelConvertible

import java.time.Instant

/**
  * Common trait for classes which provide access to node auth link properties
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait HasNodeAuthLinkProps extends ModelConvertible
{
	// ABSTRACT	--------------------
	
	/**
	  * ID of the node that accepts the linked authorization method.
	  */
	def nodeId: Int
	
	/**
	  * ID of the authorization element accepted by the linked node.
	  */
	def authId: Int
	
	/**
	  * The HTTP method that the linked authorization method is used for. 
	  * None if the authorization method is used as a default for all methods (where not overridden).
	  */
	def method: Option[Method]
	
	/**
	  * A description concerning this authorization method's function in the linked node.
	  */
	def description: String
	
	/**
	  * Time when this node auth link was added to the database
	  */
	def created: Instant
	
	/**
	  * Time when this node auth link became deprecated. None while this node auth link is still 
	  * valid.
	  */
	def deprecatedAfter: Option[Instant]
	
	
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Vector("nodeId" -> nodeId, "authId" -> authId, "method" -> method.map { _.name }, 
			"description" -> description, "created" -> created, "deprecatedAfter" -> deprecatedAfter))
}

