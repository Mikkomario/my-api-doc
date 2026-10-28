package vf.myapidoc.database.store

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.collection.immutable.Pair
import utopia.flow.operator.Identity
import utopia.flow.util.StringExtensions._
import utopia.vault.database.{Connection, Store}
import vf.myapidoc.database.access.auth.scope.AccessScopes
import vf.myapidoc.database.access.auth.token.AccessAuthTokenTypes
import vf.myapidoc.database.storable.auth.{AuthTokenTypeDbModel, ScopeDbModel}
import vf.myapidoc.database.value.DocUpdateId
import vf.myapidoc.model.enumeration.TokenUseType
import vf.myapidoc.model.enumeration.TokenUseType.{Basic, Bearer}
import vf.myapidoc.model.partial.auth.{AuthTokenTypeData, ScopeData}
import vf.myapidoc.model.stored.auth.AuthTokenType

/**
 * Provides functions for interacting with authorization data
 * @author Mikko Hilpinen
 * @since 22.10.2026, v0.1
 */
object AuthDb
{
	// ATTRIBUTES   ------------------------
	
	/**
	 * Used for storing token types to the DB.
	 * Receives 4 input parameters:
	 *      1. Generic: API ID
	 *      1. Entry 1: Token type name as given in the input
	 *      1. Entry 2: Standardized token type name
	 *      1. Entry 3: Token use-type
	 */
	private val _storeTokenTypes: Store.GenericStoreFactory[(Pair[String], TokenUseType), AuthTokenTypeData, AuthTokenType, DocUpdateId] =
		Store(AuthTokenTypeDbModel)
			.generic { (data: (Pair[String], TokenUseType), update: DocUpdateId) =>
				AuthTokenTypeData(originalUpdateId = update.value, apiId = update.apiId, name = data._1.first,
					nameNoSpaces = data._1.second, useType = data._2)
			}
			// May deprecate previous values on use-case and name changes
			.replacing { case ((nameVersions, useCase), existing) =>
				existing.name != nameVersions.first || existing.useType != useCase
			} {
				(replacements, connection) =>
					implicit val c: Connection = connection
					val firstReplacing = replacements.head._1
					implicit val updateId: DocUpdateId =
						DocUpdateId(firstReplacing.apiId, firstReplacing.originalUpdateId)
					AccessAuthTokenTypes(replacements.view.map { _._2.id }).deprecate()
			}
	/**
	 * Used for storing auth scopes to the DB.
	 * Receives 2 input parameters:
	 *      1. Generic: API ID
	 *      1. Item: Scope name
	 */
	private val _storeScopes = Store(ScopeDbModel).generic { (name: String, update: DocUpdateId) =>
		ScopeData(update.value, update.apiId, name)
	}
	
	
	// OTHER    ----------------------------
	
	/**
	 * Stores token types to the DB
	 * @param tokenTypes Token type names to store
	 * @param exclusive Whether this list contains **all** token types supported by the current API version.
	 *                  If true, all token types not present on this set will be deprecated.
	 *                  Default = false.
	 * @param connection Implicit DB connection
	 * @param update Update that introduces these changes
	 * @return A map (view) where keys are token type names (auto-standardized)
	 *         and values are matching token type entries.
	 */
	def storeApiTokenTypes(tokenTypes: IterableOnce[String], exclusive: Boolean = false)
	                      (implicit connection: Connection, update: DocUpdateId) =
	{
		// Parses the use-cases from the input
		val items = tokenTypes.iterator
			.map { tokenTypeName =>
				val useCase = if (tokenTypeName.startsWithIgnoreCase("Basic")) Basic else Bearer
				Pair(tokenTypeName, standardizeRef(tokenTypeName)) -> (useCase: TokenUseType)
			}
			.toSet
		
		// Looks up existing token entries
		val existingTypes = AccessAuthTokenTypes.active
			.forApi(update.apiId).withNameMatchingAnyOf(items.map { _._1.second })
			.pull
		
		// Stores the token types to the DB
		val storedTypes = _storeTokenTypes(update).keyMap(items, existingTypes) { _._1.second } { _.nameNoSpaces }
		
		// Optionally deprecates all existing token types that were not included
		if (exclusive) {
			val currentTokenTypeIds = storedTypes.valuesIterator.map { _.id }.toSet
			AccessAuthTokenTypes(existingTypes.iterator.map { _.id }.toSet -- currentTokenTypeIds).deprecate()
		}
		
		// Yields a map that automatically standardizes the input
		storedTypes.mapInputView(standardizeRef)
	}
	
	/**
	 * Stores API scopes to the DB
	 * @param scopes Referenced scope names
	 * @param exclusive Whether this list contains **all** scopes supported by the current API version.
	 *                  If true, all scopes not present on this set will be deprecated.
	 *                  Default = false.
	 * @param connection Implicit DB connection
	 * @param update Update that introduces these changes
	 * @return A map (view) where keys are scope names (auto-standardized) and values are the matching scope entries.
	 */
	def storeApiScopes(scopes: IterableOnce[String], exclusive: Boolean = false)
	                  (implicit connection: Connection, update: DocUpdateId) =
	{
		// Standardizes the input
		val standardized = scopes.iterator.map(standardizeScope).toSet
		
		// Stores the scope entries
		val existingScopes = AccessScopes.active.forApi(update.apiId).withNames(standardized).pull
		val stored = _storeScopes(update).keyMap(standardized, existingScopes)(Identity) { _.name }
		
		// Optionally deprecates all existing scopes that were not included
		if (exclusive) {
			val currentScopeIds = stored.valuesIterator.map { _.id }.toSet
			AccessScopes(existingScopes.iterator.map { _.id }.toSet -- currentScopeIds).deprecate()
		}
		
		// Yields a map that automatically standardizes the input
		stored.mapInputView(standardizeScope)
	}
	
	private def standardizeScope(scope: String) = scope.filterNot { _ == '`' }.trim.toLowerCase
}
