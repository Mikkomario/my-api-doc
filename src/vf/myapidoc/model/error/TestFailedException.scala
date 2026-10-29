package vf.myapidoc.model.error

/**
 * An exception thrown when a test assertion fails
 * @author Mikko Hilpinen
 * @since 29.10.2026, v0.1
 */
class TestFailedException(message: String, cause: Throwable = null) extends Exception(message, cause)
