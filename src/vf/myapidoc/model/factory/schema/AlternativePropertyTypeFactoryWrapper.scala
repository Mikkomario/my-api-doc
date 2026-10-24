package vf.myapidoc.model.factory.schema

import utopia.flow.util.Mutate
import vf.myapidoc.model.enumeration.PropertyType

import java.time.Instant

/**
  * Common trait for classes that implement AlternativePropertyTypeFactory by wrapping a 
  * AlternativePropertyTypeFactory instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
trait AlternativePropertyTypeFactoryWrapper[A <: AlternativePropertyTypeFactory[A], +Repr] 
	extends AlternativePropertyTypeFactory[Repr]
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
	
	override def withAltType(altType: PropertyType) = mapWrapped { _.withAltType(altType) }
	
	override def withCreated(created: Instant) = mapWrapped { _.withCreated(created) }
	
	override def withDeprecatedAfter(deprecatedAfter: Instant) = 
		mapWrapped { _.withDeprecatedAfter(deprecatedAfter) }
	
	override def withPropId(propId: Int) = mapWrapped { _.withPropId(propId) }
	
	
	// OTHER	--------------------
	
	/**
	  * Modifies this item by mutating the wrapped factory instance
	  * @param f A function for mutating the wrapped factory instance
	  * @return Copy of this item with a mutated wrapped factory
	  */
	protected def mapWrapped(f: Mutate[A]) = wrap(f(wrappedFactory))
}

