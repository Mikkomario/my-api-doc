package vf.myapidoc.model.combined.schema

import utopia.flow.collection.immutable.{Empty, Single}
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.{Constant, Model}
import vf.myapidoc.model.partial.schema.ModelSchemaData
import vf.myapidoc.model.stored.schema.ModelSchema
import vf.myapidoc.model.template.ClientModelConvertible

object DetailedModelSchema
{
	def apply(schema: ModelSchema, properties: Seq[DetailedPropertySchema]): DetailedModelSchema =
		apply(schema.id, schema.data, properties)
}

/**
 * Includes detailed property information with a model schema
 * @author Mikko Hilpinen
 * @since 23.10.2026, v0.1
 */
case class DetailedModelSchema(id: Int, data: ModelSchemaData, properties: Seq[DetailedPropertySchema])
	extends CompleteModelSchema with CompleteModelSchemaLike[DetailedPropertySchema, DetailedModelSchema]
		with ClientModelConvertible
{
	// COMPUTED ----------------------------
	
	/**
	 * @return An iterator that yields all property names in this model structure (including nested models)
	 */
	def propertyNamesIterator: Iterator[String] = properties.iterator.flatMap { prop =>
		prop.contentType match {
			case Some(content) => Iterator.single(prop.name) ++ content.propertyNamesIterator
			case None => Single(prop.name)
		}
	}
	/**
	 * @return An iterator that yields all names of sensitive properties within this model structure
	 *         (including nested models)
	 */
	def sensitivePropertyNamesIterator: Iterator[String] = properties.iterator.flatMap { prop =>
		if (prop.isSensitive)
			prop.contentType match {
				case Some(content) => Iterator.single(prop.name) ++ content.propertyNamesIterator
				case None => Single(prop.name)
			}
		else
			prop.contentType match {
				case Some(content) => content.sensitivePropertyNamesIterator
				case None => Empty
			}
	}
	
	
	// IMPLEMENTED  ------------------------
	
	override def toModel = super.toModel + Constant("properties", properties)
	override def toClientModel: Model = Model.from("name" -> data.name, "description" -> data.description,
		"properties" -> properties.map { _.toClientModel })
		.withoutEmptyValues
	
	override def withId(id: Int) = copy(id = id)
	override def wrap(factory: ModelSchemaData): DetailedModelSchema = copy(data = factory)
}