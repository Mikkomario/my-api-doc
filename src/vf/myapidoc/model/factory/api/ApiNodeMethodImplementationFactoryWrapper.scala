package vf.myapidoc.model.factory.api

import utopia.access.model.enumeration.Method
import utopia.flow.util.Mutate

import java.time.Instant

/**
  * Common trait for classes that implement ApiNodeMethodImplementationFactory by wrapping a 
  * ApiNodeMethodImplementationFactory instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait ApiNodeMethodImplementationFactoryWrapper[A <: ApiNodeMethodImplementationFactory[A], +Repr] 
	extends ApiNodeMethodImplementationFactory[Repr]
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
	
	override def withMethod(method: Method) = mapWrapped { _.withMethod(method) }
	
	override def withNodeId(nodeId: Int) = mapWrapped { _.withNodeId(nodeId) }
	
	override def withRequiresRequestBody(requiresRequestBody: Boolean) = 
		mapWrapped { _.withRequiresRequestBody(requiresRequestBody) }
	
	
	// OTHER	--------------------
	
	/**
	  * Modifies this item by mutating the wrapped factory instance
	  * @param f A function for mutating the wrapped factory instance
	  * @return Copy of this item with a mutated wrapped factory
	  */
	protected def mapWrapped(f: Mutate[A]) = wrap(f(wrappedFactory))
}

