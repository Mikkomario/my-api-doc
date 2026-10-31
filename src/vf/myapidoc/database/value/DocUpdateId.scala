package vf.myapidoc.database.value

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Model
import utopia.scribe.core.controller.logging.Scribe
import utopia.vault.database.Connection
import utopia.vault.database.value.{DbValue, LazyLookUpDbValue}
import vf.myapidoc.database.access.api.update.AccessDocUpdates
import vf.myapidoc.database.storable.api.DocUpdateDbModel
import vf.myapidoc.model.partial.api.DocUpdateData
import vf.myapidoc.util.Common._

object DocUpdateId
{
	// ATTRIBUTES   ------------------
	
	private implicit val scribe: Scribe = newScribe("Doc.Update.ID")
	
	
	// OTHER    ----------------------
	
	/**
	 * @param apiId ID of the targeted API
	 * @param scribe Implicit scribe implementation for logging cases where no IDs are found.
	 * @return
	 */
	def latest(apiId: Int)(implicit scribe: Scribe): DocUpdateId = new LazyLatestDocUpdateId(apiId)
	
	/**
	 * Lazily inserts a Doc Update entry
	 * @param apiId ID of the targeted API
	 * @return A new lazily initialized doc update ID
	 */
	def newLazy(apiId: Int): DocUpdateId = new LazilyInsertedDocUpdateId(apiId)
	
	/**
	 * Creates a new initialized doc update ID
	 * @param apiId ID of the targeted API
	 * @param value Doc update ID to wrap
	 * @return A new doc update ID
	 */
	def apply(apiId: Int, value: Int): DocUpdateId = new InitializedDocUpdateId(apiId, value)
	
	
	// NESTED   ----------------------
	
	private class LazyLatestDocUpdateId(override val apiId: Int) extends LazyLookUpDbValue[Int] with DocUpdateId
	{
		override protected def lookUp(implicit connection: Connection): Int =
			AccessDocUpdates.ofApi(apiId).latest.id.pull.getOrElse {
				// Case: For some reason, there's no doc update entry for the targeted API => Inserts a new update
				scribe.in("DocUpdate.ID.Latest.lookUp").recoverable("No documentation updates were recorded",
					Model.from("apiId" -> apiId))
				DocUpdateDbModel.insert(DocUpdateData(apiId)).id
			}
	}
	
	private class LazilyInsertedDocUpdateId(override val apiId: Int) extends LazyLookUpDbValue[Int] with DocUpdateId
	{
		override protected def lookUp(implicit connection: Connection): Int =
			DocUpdateDbModel.insert(DocUpdateData(apiId)).id
	}
	
	private class InitializedDocUpdateId(override val apiId: Int, override val value: Int) extends DocUpdateId
	{
		override def connectedValue(implicit connection: Connection): Int = value
		
		override def reset(): Boolean = false
		override def isSet: Boolean = true
	}
}

/**
 * Common trait for doc update event -ID wrappers
 * @author Mikko Hilpinen
 * @since 28.10.2026, v0.1
 */
trait DocUpdateId extends DbValue[Int]
{
	/**
	 * @return ID of the updated API
	 */
	def apiId: Int
}