package vf.myapidoc.model.combined.schema

import utopia.flow.collection.immutable.{Empty, Pair, Single}
import utopia.flow.generic.model.immutable.{Model, Value}
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.util.UncertainBoolean
import utopia.flow.util.UncertainBoolean.CertainlyTrue
import vf.myapidoc.model.factory.schema.PropertySchemaFactoryWrapper
import vf.myapidoc.model.partial.schema.PropertySchemaData
import vf.myapidoc.model.stored.schema.{AlternativePropertyType, StoredPropertySchema}
import vf.myapidoc.model.template.ClientModelConvertible

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
		with ClientModelConvertible
{
	override def toModel = super.toModel ++ Model.from("altTypes" -> altTypes, "content" -> contentType)
	override def toClientModel: Model = Model
		.apply(Vector.concat[(String, Value)](
			Pair("name" -> data.name, {
				if (altTypes.isEmpty)
					"type" -> data.dataType.name
				else
					"types" -> (data.dataType.name +: altTypes.map { _.altType.name })
			}),
			data.isArray match {
				case CertainlyTrue => Single("array" -> true)
				case UncertainBoolean => Single("mayBeArray" -> true)
				case _ => Empty
			},
			if (data.isOptional) Single("optional" -> true) else Empty,
			Pair("description" -> data.description, "content" -> contentType.map { _.toClientModel })
		))
		.withoutEmptyValues
	
	override protected def wrap(factory: PropertySchemaData): DetailedPropertySchema = copy(data = factory)
	override def withId(id: Int): StoredPropertySchema = copy(id = id)
}