package vf.myapidoc.model.factory.schema

import utopia.flow.util.{Mutate, UncertainBoolean}
import vf.myapidoc.model.enumeration.PropertyType

import java.time.Instant

/**
  * Common trait for classes that implement PropertySchemaFactory by wrapping a 
  * PropertySchemaFactory instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
trait PropertySchemaFactoryWrapper[A <: PropertySchemaFactory[A], +Repr] extends PropertySchemaFactory[Repr]
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
	
	override def withContentTypeId(contentTypeId: Int) = mapWrapped { _.withContentTypeId(contentTypeId) }
	
	override def withCreated(created: Instant) = mapWrapped { _.withCreated(created) }
	
	override def withDataType(dataType: PropertyType) = mapWrapped { _.withDataType(dataType) }
	
	override def withDeprecatedAfter(deprecatedAfter: Instant) = 
		mapWrapped { _.withDeprecatedAfter(deprecatedAfter) }
	
	override def withDescription(description: String) = mapWrapped { _.withDescription(description) }
	
	override def withIsArray(isArray: UncertainBoolean) = mapWrapped { _.withIsArray(isArray) }
	
	override def withIsOptional(isOptional: Boolean) = mapWrapped { _.withIsOptional(isOptional) }
	
	override def withModelTypeId(modelTypeId: Int) = mapWrapped { _.withModelTypeId(modelTypeId) }
	
	override def withName(name: String) = mapWrapped { _.withName(name) }
	
	
	// OTHER	--------------------
	
	/**
	  * Modifies this item by mutating the wrapped factory instance
	  * @param f A function for mutating the wrapped factory instance
	  * @return Copy of this item with a mutated wrapped factory
	  */
	protected def mapWrapped(f: Mutate[A]) = wrap(f(wrappedFactory))
}

