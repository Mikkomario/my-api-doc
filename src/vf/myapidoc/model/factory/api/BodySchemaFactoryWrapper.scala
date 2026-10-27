package vf.myapidoc.model.factory.api

import utopia.flow.util.Mutate
import vf.myapidoc.model.enumeration.PropertyType

import java.time.Instant

/**
  * Common trait for classes that implement BodySchemaFactory by wrapping a BodySchemaFactory instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
trait BodySchemaFactoryWrapper[A <: BodySchemaFactory[A], +Repr] extends BodySchemaFactory[Repr]
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
	override def withDataType(dataType: PropertyType) = mapWrapped { _.withDataType(dataType) }
	override def withDeprecatedAfter(deprecatedAfter: Instant) = mapWrapped { _.withDeprecatedAfter(deprecatedAfter) }
	override def withIsArray(isArray: Boolean) = mapWrapped { _.withIsArray(isArray) }
	override def withModelTypeId(modelTypeId: Int) = mapWrapped { _.withModelTypeId(modelTypeId) }
	override def withParentId(parentId: Int) = mapWrapped { _.withParentId(parentId) }
	
	
	// OTHER	--------------------
	
	/**
	  * Modifies this item by mutating the wrapped factory instance
	  * @param f A function for mutating the wrapped factory instance
	  * @return Copy of this item with a mutated wrapped factory
	  */
	protected def mapWrapped(f: Mutate[A]) = wrap(f(wrappedFactory))
}

