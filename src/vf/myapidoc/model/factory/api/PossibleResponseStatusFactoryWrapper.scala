package vf.myapidoc.model.factory.api

import utopia.flow.util.{Mutate, UncertainBoolean}

import java.time.Instant

/**
  * Common trait for classes that implement PossibleResponseStatusFactory by wrapping a 
  * PossibleResponseStatusFactory instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait PossibleResponseStatusFactoryWrapper[A <: PossibleResponseStatusFactory[A], +Repr] 
	extends PossibleResponseStatusFactory[Repr]
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
	
	override def withHasBody(hasBody: UncertainBoolean) = mapWrapped { _.withHasBody(hasBody) }
	
	override def withImplementationId(implementationId: Int) = 
		mapWrapped { _.withImplementationId(implementationId) }
	
	override def withStatusCode(statusCode: Int) = mapWrapped { _.withStatusCode(statusCode) }
	
	
	// OTHER	--------------------
	
	/**
	  * Modifies this item by mutating the wrapped factory instance
	  * @param f A function for mutating the wrapped factory instance
	  * @return Copy of this item with a mutated wrapped factory
	  */
	protected def mapWrapped(f: Mutate[A]) = wrap(f(wrappedFactory))
}

