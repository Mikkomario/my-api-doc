package vf.myapidoc.database.storable.api

import utopia.vault.model.immutable.Storable
import utopia.vault.model.template.HasIdProperty
import utopia.vault.nosql.storable.StorableFactory
import utopia.vault.store.FromIdFactory
import vf.myapidoc.model.factory.api.BodySchemaFactory

/**
  * Common trait for factories used for constructing body schema database models
  * @tparam DbModel Type of database interaction models constructed
  * @tparam A Type of read instances
  * @tparam Data Supported data-part type
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
trait BodySchemaDbModelFactoryLike[+DbModel <: Storable, +A, -Data] 
	extends VersionedDocElementDbModelFactoryLike[DbModel, A, Data] with BodySchemaFactory[DbModel]

