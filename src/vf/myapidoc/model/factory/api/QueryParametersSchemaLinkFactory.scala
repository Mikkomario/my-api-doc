package vf.myapidoc.model.factory.api

/**
  * Common trait for query parameters schema link-related factories which allow construction with 
  * individual properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait QueryParametersSchemaLinkFactory[+A] extends ApiNodeMethodImplementationModelSchemaLinkFactory[A]

