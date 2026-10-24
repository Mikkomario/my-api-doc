package vf.myapidoc.model.combined.schema

import utopia.flow.collection.immutable.Empty
import vf.myapidoc.model.factory.schema.PropertySchemaFactoryWrapper
import vf.myapidoc.model.partial.schema.PropertySchemaData
import vf.myapidoc.model.stored.schema.{AlternativePropertyType, StoredPropertySchema}

/**
 * Includes alternative types and referenced model information with a property schema
 * @param id ID of this property schema
 * @param data Wrapped property data
 * @param contentType A type representing the contained object / model, if applicable
 * @param altTypes Alternative accepted content types / data types
 * @author Mikko Hilpinen
 * @since 23.10.2026, v0.1
 */
case class DetailedPropertySchema(id: Int, data: PropertySchemaData, contentType: Option[DetailedModelSchema] = None,
                                  altTypes: Seq[AlternativePropertyType] = Empty)
	extends StoredPropertySchema with PropertySchemaFactoryWrapper[PropertySchemaData, DetailedPropertySchema]
{
	override protected def wrap(factory: PropertySchemaData): DetailedPropertySchema = copy(data = factory)
	override def withId(id: Int): StoredPropertySchema = copy(id = id)
}