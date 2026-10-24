package vf.myapidoc.database.reader.api

import utopia.vault.nosql.read.linked.CombiningDbRowReader
import vf.myapidoc.database.reader.auth.AuthTokenTypeDbReader
import vf.myapidoc.model.combined.api.DetailedNodeAuthTypeLink
import vf.myapidoc.model.stored.api.NodeAuthTypeLink
import vf.myapidoc.model.stored.auth.AuthTokenType

/**
  * Used for reading detailed node auth type links from the database
  * @author Mikko Hilpinen
  * @since 23.10.2026, v0.1
  */
object DetailedNodeAuthTypeLinkDbReader 
	extends CombiningDbRowReader[NodeAuthTypeLink, AuthTokenType, 
		DetailedNodeAuthTypeLink](NodeAuthTypeLinkDbReader, AuthTokenTypeDbReader)
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param link  link to wrap
	  * @param token token to attach
	  */
	override def combine(link: NodeAuthTypeLink, token: AuthTokenType) = DetailedNodeAuthTypeLink(link, token)
}

