package vf.myapidoc.database.store

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.collection.immutable.Pair
import utopia.flow.operator.Identity
import utopia.flow.util.StringExtensions._
import utopia.vault.database.{Connection, Store}
import vf.myapidoc.database.access.auth.scope.AccessScopes
import vf.myapidoc.database.access.auth.token.AccessAuthTokenTypes
import vf.myapidoc.database.storable.auth.{AuthTokenTypeDbModel, ScopeDbModel}
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
	private val _storeTokenTypes: Store.GenericStoreFactory[(Pair[String], TokenUseType), AuthTokenTypeData, AuthTokenType, Int] =
		Store(AuthTokenTypeDbModel)
			.generic { (data: (Pair[String], TokenUseType), apiId: Int) =>
				AuthTokenTypeData(apiId, name = data._1.first, nameNoSpaces = data._1.second, useType = data._2)
			}
			// May deprecate previous values on use-case and name changes
			.replacing { case ((nameVersions, useCase), existing) =>
				existing.name != nameVersions.first || existing.useType != useCase
			} {
				(replacements, connection) =>
					AccessAuthTokenTypes(replacements.view.map { _._2.id }).deprecate()(connection)
			}
	/**
	 * Used for storing auth scopes to the DB.
	 * Receives 2 input parameters:
	 *      1. Generic: API ID
	 *      1. Item: Scope name
	 */
	private val _storeScopes = Store(ScopeDbModel).generic { (name: String, apiId: Int) => ScopeData(apiId, name) }
	
	
	// OTHER    ----------------------------
	
	/**
	 * Stores token types to the DB
	 * @param apiId ID of the targeted API
	 * @param tokenTypes Token type names to store
	 * @param exclusive Whether this list contains **all** token types supported by the current API version.
	 *                  If true, all token types not present on this set will be deprecated.
	 *                  Default = false.
	 * @param connection Implicit DB connection
	 * @return A map (view) where keys are token type names (auto-standardized)
	 *         and values are matching token type entries.
	 */
	def storeApiTokenTypes(apiId: Int, tokenTypes: IterableOnce[String], exclusive: Boolean = false)
	                      (implicit connection: Connection) =
	{
		// Parses the use-cases from the input
		val items = tokenTypes.iterator
			.map { tokenTypeName =>
				val (name, useCase) = {
					if (tokenTypeName.startsWithIgnoreCase("Basic")) {
						if (tokenTypeName.lift(5).contains(':'))
							tokenTypeName.drop(6).trim -> Basic
						else
							tokenTypeName -> Basic
					}
					else
						tokenTypeName -> Bearer
				}
				Pair(name, standardizeName(name)) -> (useCase: TokenUseType)
			}
			.toSet
		
		// Looks up existing token entries
		val existingTypes = AccessAuthTokenTypes.active.forApi(apiId).withNameMatchingAnyOf(items.map { _._1.second })
			.pull
		
		// Stores the token types to the DB
		val storedTypes = _storeTokenTypes(apiId).keyMap(items, existingTypes) { _._1.second } { _.nameNoSpaces }
		
		// Optionally deprecates all existing token types that were not included
		if (exclusive) {
			val currentTokenTypeIds = storedTypes.valuesIterator.map { _.id }.toSet
			AccessAuthTokenTypes(existingTypes.iterator.map { _.id }.toSet -- currentTokenTypeIds).deprecate()
		}
		
		// Yields a map that automatically standardizes the input
		storedTypes.mapInputView(standardizeName)
	}
	
	/**
	 * Stores API scopes to the DB
	 * @param apiId ID of the targeted API
	 * @param scopes Referenced scope names
	 * @param exclusive Whether this list contains **all** scopes supported by the current API version.
	 *                  If true, all scopes not present on this set will be deprecated.
	 *                  Default = false.
	 * @param connection Implicit DB connection
	 * @return A map (view) where keys are scope names (auto-standardized) and values are the matching scope entries.
	 */
	def storeApiScopes(apiId: Int, scopes: IterableOnce[String], exclusive: Boolean = false)
	                  (implicit connection: Connection) =
	{
		// Standardizes the input
		val standardized = scopes.iterator.map(standardizeScope).toSet
		
		// Stores the scope entries
		val existingScopes = AccessScopes.active.forApi(apiId).withNames(standardized).pull
		val stored = _storeScopes(apiId).keyMap(standardized, existingScopes)(Identity) { _.name }
		
		// Optionally deprecates all existing scopes that were not included
		if (exclusive) {
			val currentScopeIds = stored.valuesIterator.map { _.id }.toSet
			AccessScopes(existingScopes.iterator.map { _.id }.toSet -- currentScopeIds).deprecate()
		}
		
		// Yields a map that automatically standardizes the input
		stored.mapInputView(standardizeScope)
	}
	
	private def standardizeName(name: String) = name.filter { _.isLetter }.toLowerCase
	private def standardizeScope(scope: String) = scope.filterNot { _ == '`' }.trim.toLowerCase
}
