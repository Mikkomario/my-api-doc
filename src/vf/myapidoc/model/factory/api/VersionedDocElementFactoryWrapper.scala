package vf.myapidoc.model.factory.api

import utopia.flow.util.Mutate

/**
  * Common trait for classes that implement VersionedDocElementFactory by wrapping a 
  * VersionedDocElementFactory instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 28.10.2026, v0.1
  */
trait VersionedDocElementFactoryWrapper[A <: VersionedDocElementFactory[A], +Repr] 
	extends VersionedDocElementFactory[Repr]
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
	
	override def withOriginalUpdateId(originalUpdateId: Int) = 
		mapWrapped { _.withOriginalUpdateId(originalUpdateId) }
	
	override def withRemovingUpdateId(removingUpdateId: Int) = 
		mapWrapped { _.withRemovingUpdateId(removingUpdateId) }
	
	
	// OTHER	--------------------
	
	/**
	  * Modifies this item by mutating the wrapped factory instance
	  * @param f A function for mutating the wrapped factory instance
	  * @return Copy of this item with a mutated wrapped factory
	  */
	protected def mapWrapped(f: Mutate[A]) = wrap(f(wrappedFactory))
}

