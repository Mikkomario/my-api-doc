package vf.myapidoc.controller

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.generic.model.immutable.Value
import utopia.flow.operator.equality.EqualsExtensions._
import utopia.flow.util.console.ConsoleExtensions._
import utopia.vault.database.Connection
import vf.myapidoc.database.access.api.AccessApis
import vf.myapidoc.database.store.ApiDocDb

import scala.io.StdIn

/**
 * Provides utility functions for the console classes
 * @author Mikko Hilpinen
 * @since 25.10.2026, v0.1
 */
package object console
{
	/**
	 * Allows the user to select an API
	 * @param arg An argument that refers to the selected API
	 * @param connection Implicit DB connection
	 * @return Selected API, including full details
	 */
	def resolveApi(arg: Value = Value.empty)(implicit connection: Connection) = {
		val options = arg.string match {
			case Some(name) => AccessApis.withNameContaining(name).pull.bestMatch { _.name ~== name }
			case None => AccessApis.pull
		}
		options.only
			.orElse {
				if (options.isEmpty) {
					println("No APIs have been documented yet")
					None
				}
				else {
					println("Which API did you mean?")
					StdIn.selectFrom(options.map { api => api -> api.name })
				}
			}
			.map { ApiDocDb.pullDetailsFor(_) }
	}
}
