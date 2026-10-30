package vf.myapidoc.model.factory.example

import utopia.flow.generic.model.immutable.Value
import utopia.flow.util.Mutate

/**
  * Common trait for classes that implement ExamplePropertyValueFactory by wrapping a 
  * ExamplePropertyValueFactory instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 30.10.2026, v0.1
  */
trait ExamplePropertyValueFactoryWrapper[A <: ExamplePropertyValueFactory[A], +Repr] 
	extends ExamplePropertyValueFactory[Repr]
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
	
	override def withPropId(propId: Int) = mapWrapped { _.withPropId(propId) }
	
	override def withResponseId(responseId: Int) = mapWrapped { _.withResponseId(responseId) }
	
	override def withValue(value: Value) = mapWrapped { _.withValue(value) }
	
	
	// OTHER	--------------------
	
	/**
	  * Modifies this item by mutating the wrapped factory instance
	  * @param f A function for mutating the wrapped factory instance
	  * @return Copy of this item with a mutated wrapped factory
	  */
	protected def mapWrapped(f: Mutate[A]) = wrap(f(wrappedFactory))
}

