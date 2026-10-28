package vf.myapidoc.database.storable.api

import utopia.vault.model.immutable.Storable
import utopia.vault.model.template.HasIdProperty
import utopia.vault.nosql.storable.StorableFactory
import utopia.vault.store.FromIdFactory
import vf.myapidoc.model.factory.api.NodeAuthLinkFactory

/**
  * Common trait for factories used for constructing node auth link database models
  * @tparam DbModel Type of database interaction models constructed
  * @tparam A Type of read instances
  * @tparam Data Supported data-part type
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait NodeAuthLinkDbModelFactoryLike[+DbModel <: Storable, +A, -Data] 
	extends VersionedDocElementDbModelFactoryLike[DbModel, A, Data] with NodeAuthLinkFactory[DbModel]

