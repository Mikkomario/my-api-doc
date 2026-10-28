package vf.myapidoc.model.factory.api

import utopia.flow.util.Mutate

/**
  * Common trait for classes that implement AdditionalNodeAuthScopeRequirementFactory by wrapping 
  * a AdditionalNodeAuthScopeRequirementFactory instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait AdditionalNodeAuthScopeRequirementFactoryWrapper[A <: AdditionalNodeAuthScopeRequirementFactory[A], +Repr]
	extends AdditionalNodeAuthScopeRequirementFactory[Repr]
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
	
	override def withScopeId(scopeId: Int) = mapWrapped { _.withScopeId(scopeId) }
	override def withScopeLinkId(scopeLinkId: Int) = mapWrapped { _.withScopeLinkId(scopeLinkId) }
	
	
	// OTHER	--------------------
	
	/**
	  * Modifies this item by mutating the wrapped factory instance
	  * @param f A function for mutating the wrapped factory instance
	  * @return Copy of this item with a mutated wrapped factory
	  */
	protected def mapWrapped(f: Mutate[A]) = wrap(f(wrappedFactory))
}

