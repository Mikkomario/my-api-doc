package vf.myapidoc.database.reader.api

import utopia.vault.nosql.read.linked.PossiblyCombiningDbRowReader
import vf.myapidoc.model.combined.api.PossibleResponse
import vf.myapidoc.model.stored.api.{PossibleResponseStatus, ResponseContentType}

/**
  * Used for reading possible responses from the database
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
object PossibleResponseDbReader 
	extends PossiblyCombiningDbRowReader[PossibleResponseStatus, ResponseContentType, 
		PossibleResponse](PossibleResponseStatusDbReader, ResponseContentTypeDbReader)
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param status status to wrap
	  * @param body   body to attach
	  */
	override def combine(status: PossibleResponseStatus, body: Option[ResponseContentType]) = 
		PossibleResponse(status, body)
}

