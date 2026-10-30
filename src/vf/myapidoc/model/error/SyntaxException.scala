package vf.myapidoc.model.error

import utopia.flow.util.StringExtensions._

/**
 * Exceptions throw on malformed code
 * @author Mikko Hilpinen
 * @since 29.10.2026, v0.1
 */
class SyntaxException(val line: String, message: String = "", cause: Throwable = null)
	extends Exception(message.nonEmptyOrElse(s"Syntax error on line `$line`"), cause)
