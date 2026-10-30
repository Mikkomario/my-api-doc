package vf.myapidoc.model.factory.example

import utopia.flow.util.Mutate

import java.nio.file.Path
import java.time.Instant

/**
  * Common trait for classes that implement ExampleResponseFactory by wrapping a 
  * ExampleResponseFactory instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 30.10.2026, v0.1
  */
trait ExampleResponseFactoryWrapper[A <: ExampleResponseFactory[A], +Repr] 
	extends ExampleResponseFactory[Repr]
{
	// ABSTRACT	--------------------
	
	/**
	  * The factory wrapped by this instance
	  */
	protected def wrappedFactory: A
	
	/**
	  * Mutates this item by wrapping a mutated instance
	  * @param factory The new factory instance to wrap
	  * @return Copy of this item with the specified wrapped factory
	  */
	protected def wrap(factory: A): Repr
	
	
	// IMPLEMENTED	--------------------
	
	override def withCreated(created: Instant) = mapWrapped { _.withCreated(created) }
	
	override def withRelativeFilePath(relativeFilePath: Path) = 
		mapWrapped { _.withRelativeFilePath(relativeFilePath) }
	
	override def withStatusId(statusId: Int) = mapWrapped { _.withStatusId(statusId) }
	
	override def withTestedApiVersionId(testedApiVersionId: Int) = 
		mapWrapped { _.withTestedApiVersionId(testedApiVersionId) }
	
	
	// OTHER	--------------------
	
	/**
	  * Modifies this item by mutating the wrapped factory instance
	  * @param f A function for mutating the wrapped factory instance
	  * @return Copy of this item with a mutated wrapped factory
	  */
	protected def mapWrapped(f: Mutate[A]) = wrap(f(wrappedFactory))
}

