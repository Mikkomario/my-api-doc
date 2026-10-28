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
  * Common trait for factories that parse node auth link data from database-originated models
  * @tparam A Type of read instances
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait NodeAuthLinkDbReaderLike[+A] extends VersionedDocElementDbReaderLike[A]
{
	// ABSTRACT	--------------------
	
	/**
	  * Database properties used when parsing column data
	  */
	override def dbProps: NodeAuthLinkDbProps
	
	/**
	  * @param model            Model from which additional data may be read
	  * @param id               ID to assign to the read/parsed node auth link
	  * @param originalUpdateId original update id to assign to the new node auth link
	  * @param nodeId           node id to assign to the new node auth link
	  * @param authId           auth id to assign to the new node auth link
	  * @param removingUpdateId removing update id to assign to the new node auth link
	  * @param method           method to assign to the new node auth link
	  * @param description      description to assign to the new node auth link
	  * @return A node auth link with the specified data
	  */
	protected def apply(model: HasProperties, id: Int, originalUpdateId: Int, nodeId: Int, authId: Int,
	                    removingUpdateId: Option[Int], method: Option[Method], description: String): A
	
	
	// IMPLEMENTED	--------------------
	
	/**
	  * @param model            Model from which additional data may be read
	  * @param id               ID to assign to the read/parsed versioned doc element
	  * @param originalUpdateId original update id to assign to the new versioned doc element
	  * @param removingUpdateId removing update id to assign to the new versioned doc element
	  */
	override protected def apply(model: HasProperties, id: Int, originalUpdateId: Int, removingUpdateId: Option[Int]) =
		apply(model, id, originalUpdateId = originalUpdateId, nodeId = model(dbProps.nodeId.name).getInt, 
			authId = model(dbProps.authId.name).getInt, removingUpdateId = removingUpdateId, 
			method = model(dbProps.method.name).string.map(Method.apply), 
			description = model(dbProps.description.name).getString)
}

