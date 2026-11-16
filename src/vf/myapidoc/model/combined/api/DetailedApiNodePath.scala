package vf.myapidoc.model.combined.api

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.{Constant, Model}
import vf.myapidoc.model.combined.schema.DetailedModelSchema
import vf.myapidoc.model.partial.api.ApiNodePathData
import vf.myapidoc.model.stored.api.{ApiNodePath, ApiNodePathLike}

object DetailedApiNodePath
{
	def apply(path: ApiNodePath, params: Option[DetailedModelSchema]): DetailedApiNodePath =
		DetailedApiNodePath(path.id, path.data, params)
}

/**
 * Attaches accepted path parameters to an API node path entry
 * @author Mikko Hilpinen
 * @since 15.11.2026, v0.1
 */
case class DetailedApiNodePath(id: Int, data: ApiNodePathData, params: Option[DetailedModelSchema])
	extends ApiNodePath with ApiNodePathLike[DetailedApiNodePath]
{
	// IMPLEMENTED  --------------------
	
	override def toModel: Model = super.toModel + Constant("params", params)
	
	override def wrap(data: ApiNodePathData): DetailedApiNodePath = copy(data = data)
	override def withId(id: Int): DetailedApiNodePath = copy(id = id)
}