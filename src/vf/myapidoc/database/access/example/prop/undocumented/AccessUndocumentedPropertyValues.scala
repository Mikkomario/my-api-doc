package vf.myapidoc.database.access.example.prop.undocumented

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.targeting.columns.{AccessManyColumns, AccessValues}
import vf.myapidoc.database.storable.example.UndocumentedPropertyDbModel
import vf.myapidoc.util.Common.jsonParser

/**
  * Used for accessing undocumented property values from the DB
  * @author Mikko Hilpinen
  * @since 12.11.2026, v0.1
  */
case class AccessUndocumentedPropertyValues(access: AccessManyColumns) extends AccessValues
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing undocumented property database properties
	  */
	val model = UndocumentedPropertyDbModel
	
	/**
	  * Access to undocumented property ids
	  */
	lazy val ids = apply(model.index) { _.getInt }
	/**
	  * ID of the response that contained this property
	  */
	lazy val responseIds = apply(model.responseId) { v => v.getInt }
	/**
	  * ID of the model schema that missed this property
	  */
	lazy val modelTypeIds = apply(model.modelTypeId) { v => v.getInt }
	/**
	  * Name of the received property
	  */
	lazy val names = apply(model.name) { v => v.getString }
	/**
	  * Received property value
	  */
	lazy val values = apply(model.value) { v => v.mapIfNotEmpty { v => jsonParser.valueOf(v.getString) } }
	
	
	// COMPUTED -------------------------
	
	/**
	 * @param connection Implicit DB connection
	 * @return All accessible properties as a map where keys are model ID + property names
	 *         and values are encountered (distinct) property values.
	 */
	def valuesByProperty(implicit connection: Connection) =
		access.streamColumns(model.modelTypeId, model.name, model.value) { rowsIter =>
			rowsIter
				.map { row =>
					val value = row(2).string match {
						case Some(json) => jsonParser.valueOf(json)
						case None => Value.empty
					}
					(row.head.getInt, row(1).getString, value)
				}
				.groupMapToSets { case (modelId, name, _) => modelId -> name } { _._3 }
				.withDefaultValue(Set.empty)
		}
}

