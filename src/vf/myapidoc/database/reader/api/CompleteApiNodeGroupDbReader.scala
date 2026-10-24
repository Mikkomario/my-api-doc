package vf.myapidoc.database.reader.api

import utopia.vault.nosql.read.linked.MultiLinkedDbReader
import vf.myapidoc.model.combined.api.CompleteApiNodeGroup
import vf.myapidoc.model.stored.api.{ApiNode, ApiNodeGroup}

/**
  * Used for reading complete api node groups from the database
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
object CompleteApiNodeGroupDbReader 
	extends MultiLinkedDbReader[ApiNodeGroup, ApiNode, CompleteApiNodeGroup](ApiNodeGroupDbReader, 
		ApiNodeDbReader, neverEmptyRight = true)
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param group group to wrap
	  * @param nodes nodes to attach
	  */
	override def combine(group: ApiNodeGroup, nodes: Seq[ApiNode]) = CompleteApiNodeGroup(group, nodes)
}

