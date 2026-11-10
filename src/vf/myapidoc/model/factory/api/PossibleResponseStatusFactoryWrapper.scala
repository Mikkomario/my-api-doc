package vf.myapidoc.model.factory.api

import utopia.flow.util.UncertainBoolean

/**
  * Common trait for classes that implement PossibleResponseStatusFactory by wrapping a 
  * PossibleResponseStatusFactory instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait PossibleResponseStatusFactoryWrapper[A <: PossibleResponseStatusFactory[A], +Repr] 
	extends PossibleResponseStatusFactory[Repr] with VersionedDocElementFactoryWrapper[A, Repr]
{
	// IMPLEMENTED	--------------------
	
	override def withDescription(description: String) = mapData { _.withDescription(description) }
	override def withHasBody(hasBody: UncertainBoolean) = mapData { _.withHasBody(hasBody) }
	override def withImplementationId(implementationId: Int) =
		mapData { _.withImplementationId(implementationId) }
	override def withStatusCode(statusCode: Int) = mapData { _.withStatusCode(statusCode) }
}

