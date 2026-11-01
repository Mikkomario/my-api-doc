package vf.myapidoc.model.factory.test

import utopia.vault.store.CanCopyData

import java.nio.file.Path
import java.time.Instant

/**
  * Common trait for classes that implement TestsFileFactory by wrapping another such instance
  * @tparam Data Type of the wrapped data
  * @tparam Repr Type of the implementing class
  * @author Mikko Hilpinen
  * @since 01.11.2026, v0.1
  */
trait TestsFileFactoryWrapper[Data <: TestsFileFactory[Data], +Repr] 
	extends TestsFileFactory[Repr] with CanCopyData[Data, Repr]
{
	// IMPLEMENTED	--------------------
	
	override def withApiId(apiId: Int) = mapData { _.withApiId(apiId) }
	
	override def withCreated(created: Instant) = mapData { _.withCreated(created) }
	
	override def withPath(path: Path) = mapData { _.withPath(path) }
}

