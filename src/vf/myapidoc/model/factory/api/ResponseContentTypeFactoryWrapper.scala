package vf.myapidoc.model.factory.api

import utopia.flow.util.Mutate
import vf.myapidoc.model.enumeration.PropertyType

import java.time.Instant

/**
  * Common trait for classes that implement ResponseContentTypeFactory by wrapping a 
  * ResponseContentTypeFactory instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
trait ResponseContentTypeFactoryWrapper[A <: ResponseContentTypeFactory[A], +Repr] 
	extends ResponseContentTypeFactory[Repr]
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
	
	override def withContentType(contentType: PropertyType) = mapWrapped { _.withContentType(contentType) }
	
	override def withCreated(created: Instant) = mapWrapped { _.withCreated(created) }
	
	override def withDeprecatedAfter(deprecatedAfter: Instant) = 
		mapWrapped { _.withDeprecatedAfter(deprecatedAfter) }
	
	override def withIsArray(isArray: Boolean) = mapWrapped { _.withIsArray(isArray) }
	
	override def withIsEmpty(isEmpty: Boolean) = mapWrapped { _.withIsEmpty(isEmpty) }
	
	override def withModelTypeId(modelTypeId: Int) = mapWrapped { _.withModelTypeId(modelTypeId) }
	
	override def withStatusId(statusId: Int) = mapWrapped { _.withStatusId(statusId) }
	
	
	// OTHER	--------------------
	
	/**
	  * Modifies this item by mutating the wrapped factory instance
	  * @param f A function for mutating the wrapped factory instance
	  * @return Copy of this item with a mutated wrapped factory
	  */
	protected def mapWrapped(f: Mutate[A]) = wrap(f(wrappedFactory))
}

