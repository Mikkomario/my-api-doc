package vf.myapidoc.model.factory.api

import utopia.flow.util.Mutate

import java.time.Instant

/**
  * Common trait for classes that implement ApiNodeFactory by wrapping a ApiNodeFactory instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
trait ApiNodeFactoryWrapper[A <: ApiNodeFactory[A], +Repr] extends ApiNodeFactory[Repr]
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
	
	override def withDeprecatedAfter(deprecatedAfter: Instant) = 
		mapWrapped { _.withDeprecatedAfter(deprecatedAfter) }
	
	override def withDescription(description: String) = mapWrapped { _.withDescription(description) }
	
	override def withGroupId(groupId: Int) = mapWrapped { _.withGroupId(groupId) }
	
	override def withName(name: String) = mapWrapped { _.withName(name) }
	
	override def withNameNoSpaces(nameNoSpaces: String) = mapWrapped { _.withNameNoSpaces(nameNoSpaces) }
	
	override def withParentId(parentId: Int) = mapWrapped { _.withParentId(parentId) }
	
	override def withRelativePath(relativePath: String) = mapWrapped { _.withRelativePath(relativePath) }
	
	
	// OTHER	--------------------
	
	/**
	  * Modifies this item by mutating the wrapped factory instance
	  * @param f A function for mutating the wrapped factory instance
	  * @return Copy of this item with a mutated wrapped factory
	  */
	protected def mapWrapped(f: Mutate[A]) = wrap(f(wrappedFactory))
}

