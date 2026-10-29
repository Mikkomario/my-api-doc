package vf.myapidoc.test

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.util.console.ConsoleExtensions._
import utopia.vault.database.Connection
import vf.myapidoc.database.SetupDb
import vf.myapidoc.database.access.api.AccessApis
import vf.myapidoc.database.access.schema.prop.AccessPropertySchemas
import vf.myapidoc.database.store.ModelSchemaDb
import vf.myapidoc.util.Common._

import scala.io.StdIn

/**
 * Tests pulling model schemas
 * @author Mikko Hilpinen
 * @since 28.10.2026, v0.1
 */
object PullModelsTest extends App
{
	if (SetupDb(enableUserInteraction = true))
		cPool { implicit c =>
			println("Select the API to test")
			StdIn.selectFrom(AccessApis.pull.reverseSortBy { _.created }.map { a => a -> a.name }).foreach { api =>
				Connection.debugPrintsEnabled = true
				ModelSchemaDb.pullApiModels(api.id).foreach { model =>
					println(model.toJson)
				}
				
				val namelessModelProps = AccessPropertySchemas.active
					.whereActiveModels.nameless.whereActiveModels.inApi(api.id).pull
				println(s"Pulled ${ namelessModelProps.size } nameless model properties")
				Connection.debugPrintsEnabled = false
			}
		}
}
