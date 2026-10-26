package vf.myapidoc.model.factory.schema

import utopia.flow.util.Mutate

import java.time.Instant

/**
  * Common trait for classes that implement PossiblePropertyValueFactory by wrapping a 
  * PossiblePropertyValueFactory instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 26.10.2026, v0.1
  */
trait PossiblePropertyValueFactoryWrapper[A <: PossiblePropertyValueFactory[A], +Repr] 
	extends PossiblePropertyValueFactory[Repr]
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
	
	override def withPropId(propId: Int) = mapWrapped { _.withPropId(propId) }
	
	override def withValue(value: String) = mapWrapped { _.withValue(value) }
	
	
	// OTHER	--------------------
	
	/**
	  * Modifies this item by mutating the wrapped factory instance
	  * @param f A function for mutating the wrapped factory instance
	  * @return Copy of this item with a mutated wrapped factory
	  */
	protected def mapWrapped(f: Mutate[A]) = wrap(f(wrappedFactory))
}

