package vf.myapidoc.database.storable.api

import utopia.vault.model.immutable.Storable
import utopia.vault.model.template.HasIdProperty
import utopia.vault.nosql.storable.StorableFactory
import utopia.vault.store.FromIdFactory
import vf.myapidoc.model.factory.api.ApiNodeMethodImplementationModelSchemaLinkFactory

/**
  * Common trait for factories used for constructing api node method implementation model schema 
  * link database models
  * @tparam DbModel Type of database interaction models constructed
  * @tparam A Type of read instances
  * @tparam Data Supported data-part type
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait ApiNodeMethodImplementationModelSchemaLinkDbModelFactoryLike[+DbModel <: Storable, +A, -Data] 
	extends StorableFactory[DbModel, A, Data] with FromIdFactory[Int, DbModel] with HasIdProperty 
		with ApiNodeMethodImplementationModelSchemaLinkFactory[DbModel]

