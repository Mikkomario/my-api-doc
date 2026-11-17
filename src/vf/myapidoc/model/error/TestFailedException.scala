package vf.myapidoc.model.error

import utopia.flow.util.StringExtensions._

/**
 * An exception thrown when a test assertion fails
 * @param message Failure message
 * @param line Line on which the test failed
 * @param test Name of the test that failed, if known
 * @param cause Error that caused this failure
 * @author Mikko Hilpinen
 * @since 29.10.2026, v0.1
 */
class TestFailedException(val message: String, val line: Int, val test: String = "", cause: Throwable = null)
	extends Exception(s"${ test.mapIfNotEmpty { t => s"\"$t\" failed: " } }$line: $message", cause)
{
	/**
	 * @param test Test in which this failure occurred
	 * @return A copy of this exception in that test's context
	 */
	def inTest(test: String) = {
		if (this.test == test)
			this
		else
			new TestFailedException(if (this.test.isEmpty) message else s"Nested test \"${ this.test }\" failed", line,
				test, this)
	}
}