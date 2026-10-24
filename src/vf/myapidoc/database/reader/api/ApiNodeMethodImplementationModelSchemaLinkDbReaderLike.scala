package vf.myapidoc.database.reader.api

import utopia.flow.generic.model.immutable.Model
import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
import utopia.vault.model.template.HasTableAsTarget
import utopia.vault.nosql.read.DbRowReader
import utopia.vault.nosql.read.parse.ParseTableModel
import vf.myapidoc.database.props.api.ApiNodeMethodImplementationModelSchemaLinkDbProps

import scala.util.Success

import java.time.Instant

/**
  * Common trait for factories which parse api node method implementation model schema link data 
  * from database-originated models
  * @tparam A Type of read instances
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait ApiNodeMethodImplementationModelSchemaLinkDbReaderLike[+A] 
	extends DbRowReader[A] with ParseTableModel[A] with HasTableAsTarget
{
	// ABSTRACT	--------------------
	
	/**
	  * Database properties used when parsing column data
	  */
	def dbProps: ApiNodeMethodImplementationModelSchemaLinkDbProps
	
	/**
	  * @param model            Model from which additional data may be read
	  * @param id               Id to assign to the read/parsed api node method implementation model 
	  *                         schema link
	  * @param implementationId implementation id to assign to the new api node method implementation 
	  *                         model schema link
	  * @param modelTypeId      model type id to assign to the new api node method implementation 
	  *                         model schema link
	  * @param created          created to assign to the new api node method implementation model 
	  *                         schema link
	  * @param deprecatedAfter  deprecated after to assign to the new api node method implementation 
	  *                         model schema link
	  * @return A api node method implementation model schema link with the specified data
	  */
	protected def apply(model: HasProperties, id: Int, implementationId: Int, modelTypeId: Int, 
		created: Instant, deprecatedAfter: Option[Instant]): A
	
	
	// IMPLEMENTED	--------------------
	
	override def fromValid(valid: Model) = 
		Success(apply(valid, valid(dbProps.id.name).getInt, 
			implementationId = valid(dbProps.implementationId.name).getInt, 
			modelTypeId = valid(dbProps.modelTypeId.name).getInt, 
			created = valid(dbProps.created.name).getInstant, 
			deprecatedAfter = valid(dbProps.deprecatedAfter.name).instant))
}

