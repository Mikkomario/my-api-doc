package vf.myapidoc.database.storable.api

import utopia.access.model.enumeration.Method
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.Storable
import utopia.vault.store.{FromIdFactory, HasId}
import vf.myapidoc.database.props.api.NodeAuthLinkDbProps
import vf.myapidoc.model.factory.api.NodeAuthLinkFactory

import java.time.Instant

/**
  * Common trait for database models used for interacting with node auth link data in the database
  * @tparam Repr Type of this DB model
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait NodeAuthLinkDbModelLike[+Repr] 
	extends Storable with HasId[Option[Int]] with FromIdFactory[Int, Repr] with NodeAuthLinkFactory[Repr]
{
	// ABSTRACT	--------------------
	
	def nodeId: Option[Int]
	
	def authId: Option[Int]
	
	def method: Option[Method]
	
	def description: String
	
	def created: Option[Instant]
	
	def deprecatedAfter: Option[Instant]
	
	/**
	  * Access to the database properties which are utilized in this model
	  */
	def dbProps: NodeAuthLinkDbProps
	
	/**
	  * @param id              Id to assign to the new model (default = currently assigned id)
	  * @param nodeId          node id to assign to the new model (default = currently assigned value)
	  * @param authId          auth id to assign to the new model (default = currently assigned value)
	  * @param method          method to assign to the new model (default = currently assigned value)
	  * @param description     description to assign to the new model (default = currently assigned 
	  *                        value)
	  * @param created         created to assign to the new model (default = currently assigned value)
	  * @param deprecatedAfter deprecated after to assign to the new model (default = currently 
	  *                        assigned value)
	  * @return Copy of this model with the specified node auth link properties
	  */
	protected def copyNodeAuthLink(id: Option[Int] = id, nodeId: Option[Int] = nodeId, 
		authId: Option[Int] = authId, method: Option[Method] = method, description: String = description, 
		created: Option[Instant] = created, deprecatedAfter: Option[Instant] = deprecatedAfter): Repr
	
	
	// IMPLEMENTED	--------------------
	
	override def valueProperties: Seq[(String, Value)] = 
		Vector(dbProps.id.name -> id, dbProps.nodeId.name -> nodeId, dbProps.authId.name -> authId, 
			dbProps.method.name -> method.map { _.name }, dbProps.description.name -> description, 
			dbProps.created.name -> created, dbProps.deprecatedAfter.name -> deprecatedAfter)
	
	override def withAuthId(authId: Int) = copyNodeAuthLink(authId = Some(authId))
	
	override def withCreated(created: Instant) = copyNodeAuthLink(created = Some(created))
	
	override def withDeprecatedAfter(deprecatedAfter: Instant) = 
		copyNodeAuthLink(deprecatedAfter = Some(deprecatedAfter))
	
	override def withDescription(description: String) = copyNodeAuthLink(description = description)
	
	override def withId(id: Int) = copyNodeAuthLink(id = Some(id))
	
	override def withMethod(method: Method) = copyNodeAuthLink(method = Some(method))
	
	override def withNodeId(nodeId: Int) = copyNodeAuthLink(nodeId = Some(nodeId))
}

