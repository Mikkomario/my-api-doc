package vf.myapidoc.model.factory.api

/**
  * Common trait for classes that implement ApiNodeFactory by wrapping a ApiNodeFactory instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
trait ApiNodeFactoryWrapper[A <: ApiNodeFactory[A], +Repr] 
	extends ApiNodeFactory[Repr] with VersionedDocElementFactoryWrapper[A, Repr]
{
	// IMPLEMENTED	--------------------
	
	override def withDescription(description: String) = mapData { _.withDescription(description) }
	override def withGroupId(groupId: Int) = mapData { _.withGroupId(groupId) }
	override def withName(name: String) = mapData { _.withName(name) }
	override def withNameNoSpaces(nameNoSpaces: String) = mapData { _.withNameNoSpaces(nameNoSpaces) }
	override def withParentId(parentId: Int) = mapData { _.withParentId(parentId) }
	override def withRelativePath(relativePath: String) = mapData { _.withRelativePath(relativePath) }
}

