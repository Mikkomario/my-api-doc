package vf.myapidoc.database.reader.api

import utopia.access.model.enumeration.Method
import utopia.flow.generic.model.immutable.Model
import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
import utopia.vault.model.template.HasTableAsTarget
import utopia.vault.nosql.read.DbRowReader
import utopia.vault.nosql.read.parse.ParseTableModel
import vf.myapidoc.database.props.api.NodeAuthLinkDbProps

import scala.util.Success

import java.time.Instant

/**
  * Common trait for factories which parse node auth link data from database-originated models
  * @tparam A Type of read instances
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait NodeAuthLinkDbReaderLike[+A] extends DbRowReader[A] with ParseTableModel[A] with HasTableAsTarget
{
	// ABSTRACT	--------------------
	
	/**
	  * Database properties used when parsing column data
	  */
	def dbProps: NodeAuthLinkDbProps
	
	/**
	  * @param model           Model from which additional data may be read
	  * @param id              Id to assign to the read/parsed node auth link
	  * @param nodeId          node id to assign to the new node auth link
	  * @param authId          auth id to assign to the new node auth link
	  * @param method          method to assign to the new node auth link
	  * @param description     description to assign to the new node auth link
	  * @param created         created to assign to the new node auth link
	  * @param deprecatedAfter deprecated after to assign to the new node auth link
	  * @return A node auth link with the specified data
	  */
	protected def apply(model: HasProperties, id: Int, nodeId: Int, authId: Int, method: Option[Method], 
		description: String, created: Instant, deprecatedAfter: Option[Instant]): A
	
	
	// IMPLEMENTED	--------------------
	
	override def fromValid(valid: Model) = 
		Success(apply(valid, valid(dbProps.id.name).getInt, nodeId = valid(dbProps.nodeId.name).getInt, 
			authId = valid(dbProps.authId.name).getInt, 
			method = valid(dbProps.method.name).string.map(Method.apply), 
			description = valid(dbProps.description.name).getString, 
			created = valid(dbProps.created.name).getInstant, 
			deprecatedAfter = valid(dbProps.deprecatedAfter.name).instant))
}

