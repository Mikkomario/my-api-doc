package vf.myapidoc.database.store

import utopia.access.model.enumeration.Method
import utopia.flow.collection.CollectionExtensions._
import utopia.flow.collection.immutable.tree.ValueTree
import utopia.flow.collection.immutable.{Empty, Pair}
import utopia.flow.collection.mutable.iterator.{LazyInitIterator, OptionsIterator}
import utopia.flow.collection.template.MapAccess
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Model
import utopia.flow.parse.string.Regex
import utopia.flow.util.StringExtensions._
import utopia.flow.util.UncertainBoolean
import utopia.scribe.core.controller.logging.Scribe
import utopia.vault.database.Connection
import utopia.vault.store.StoreResult
import vf.myapidoc.database.access.api.AccessApi
import vf.myapidoc.database.access.api.body.AccessRequestBodySchemas
import vf.myapidoc.database.access.api.node.AccessApiNodes
import vf.myapidoc.database.access.api.node.auth.AccessNodeAuthTypeLinks
import vf.myapidoc.database.access.api.node.auth.scope.{AccessAdditionalNodeAuthScopeRequirements, AccessNodeAuthScopeLinks}
import vf.myapidoc.database.access.api.node.group.AccessApiNodeGroups
import vf.myapidoc.database.access.api.node.impl.AccessApiNodeMethodImplementations
import vf.myapidoc.database.access.api.params.path.AccessPathParameterSchemaLinks
import vf.myapidoc.database.access.api.params.query.AccessQueryParameterSchemaLinks
import vf.myapidoc.database.access.api.response.body.AccessResponseBodySchemas
import vf.myapidoc.database.access.api.response.status.AccessPossibleResponseStatuses
import vf.myapidoc.database.access.auth.scope.AccessScopes
import vf.myapidoc.database.access.auth.token.AccessAuthTokenTypes
import vf.myapidoc.database.access.schema.model.AccessModelSchemas
import vf.myapidoc.database.storable.api._
import vf.myapidoc.model.cached._
import vf.myapidoc.model.combined.api._
import vf.myapidoc.model.combined.schema.CompleteModelSchema
import vf.myapidoc.model.partial.api._
import vf.myapidoc.model.stored.api.{Api, ApiNodeMethodImplementation, NodeAuthTypeLink, PossibleResponseStatus}
import vf.myapidoc.model.stored.auth.{AuthTokenType, Scope}
import vf.myapidoc.model.stored.schema.ModelSchema
import vf.myapidoc.util.Common._

import scala.collection.mutable

/**
 * Provides functions for interacting with API node information in the DB
 * @author Mikko Hilpinen
 * @since 21.10.2026, v0.1
 */
object ApiDocDb
{
	// ATTRIBUTES   ---------------------
	
	private implicit val scribe: Scribe = newScribe("DB.ApiDoc")
	
	private val pathParamR = {
		val closing = Regex.escape('}')
		Regex.escape('{') + (!closing).anyTimes + closing
	}
	
	
	// OTHER    -------------------------
	
	/**
	 * Pulls full API documentation.
	 * @param api The API instance to complete
	 * @param connection Implicit DB connection
	 * @return That API's currently valid documentation.
	 */
	def pullDetailsFor(api: Api)(implicit connection: Connection) = {
		// Pulls the API data
		val apiId = api.id
		val models = ModelSchemaDb.pullApiModels(apiId)
		val modelById = models.iterator.map { m => m.id -> m }.toMap
		val tokenTypeById = AccessAuthTokenTypes.active.forApi(apiId).toMapBy { _.id }
		val scopeById = AccessScopes.active.forApi(apiId).toMapBy { _.id }
		
		val rawNodeGroups = AccessApiNodeGroups.active.inApi(apiId).pull
		val rawNodesPerGroup = AccessApiNodes.active.whereActiveGroups.inApi(apiId).pullAndGroupBy { _.groupId }
		val rawImplementationsPerNode = AccessApiNodeMethodImplementations.active
			.whereActiveNodeGroups.inApi(apiId)
			.pullAndGroupBy { _.nodeId }
		
		val pathParamRefs = AccessPathParameterSchemaLinks.active.whereActiveNodeGroups.inApi(apiId)
			.modelIdPerNodeId
		val tokenTypeLinksPerNode = AccessNodeAuthTypeLinks.active.whereActiveNodeGroups.inApi(apiId).stream {
			_.map { link => link + tokenTypeById(link.authTypeId) }
				.groupToSeqsBy { _.nodeId }.withDefaultValue(Empty)
		}
		
		val additionalScopeLinks = AccessAdditionalNodeAuthScopeRequirements.whereActiveNodeGroups.inApi(apiId)
			.scopeIdsPerLinkId
		val scopeLinksPerNode = AccessNodeAuthScopeLinks.active.whereActiveNodeGroups.inApi(apiId).stream {
			linksIter =>
				linksIter
					.map { link =>
						DetailedNodeAuthScopeLink(link.id, link.data,
							scopes = scopeById(link.scopeId) +: additionalScopeLinks(link.id).map(scopeById))
					}
					.groupToSeqsBy { _.nodeId }.withDefaultValue(Empty)
		}
		
		val queryParamRefs = AccessQueryParameterSchemaLinks.active.whereActiveNodeGroups.inApi(apiId)
			.modelIdPerImplementationId
		val requestBodiesPerImplementation = AccessRequestBodySchemas.active.whereActiveNodeGroups.inApi(apiId)
			.stream {
				_.map { body => DetailedRequestBodySchema(body, body.modelTypeId.flatMap(modelById.get)) }
					.groupToSeqsBy { _.implementationId }.withDefaultValue(Empty)
			}
		
		val responseBodiesByStatusId = AccessResponseBodySchemas.active.whereActiveNodeGroups.inApi(apiId)
			.stream { bodiesIter =>
				bodiesIter
					.map { body =>
						body.statusId -> DetailedResponseBodySchema(body, body.modelTypeId.flatMap(modelById.get))
					}
					.toMap
			}
		val responsesPerImplementation = AccessPossibleResponseStatuses.active.whereActiveNodeGroups.inApi(apiId)
			.stream { statusIter =>
				statusIter
					.map { status =>
						DetailedPossibleResponse(status.id, status.data, responseBodiesByStatusId.get(status.id))
					}
					.groupToSeqsBy { _.implementationId }.withDefaultValue(Empty)
			}
		
		// Combines the collected data
		var nodeById: Map[Int, DetailedApiNode] = Map.empty
		DetailedApi(apiId, api.data,
			nodeGroups = rawNodeGroups.map { group =>
				// Forms the detailed API nodes
				val nodes = rawNodesPerGroup(group.id).map { node =>
					DetailedApiNode(node.id, node.data,
						implementations = rawImplementationsPerNode(node.id).map { impl =>
							DetailedApiNodeMethodImplementation(impl.id, impl.data,
								queryParams = queryParamRefs.get(impl.id).flatMap(modelById.get),
								requestBodyOptions = requestBodiesPerImplementation(impl.id),
								responses = responsesPerImplementation(impl.id))
						},
						definedTokenTypes = tokenTypeLinksPerNode(node.id),
						definedScopes = scopeLinksPerNode(node.id),
						directPathParams = pathParamRefs.get(node.id).flatMap(modelById.get),
						// Because the parents involve cyclic references, we resolve them lazily
						parents = node.parentId match {
							case Some(firstParentId) =>
								LazyInitIterator
									.apply {
										OptionsIterator.iterate(nodeById.get(firstParentId)) {
											_.parentId.flatMap(nodeById.get) }
									}
									.caching
							case None => Empty
						})
				}
				nodeById ++= nodes.iterator.map { n => n.id -> n }
				
				// Structures the nodes into a tree
				val nodesPerParent = nodes.iterator
					.flatMap { n => n.parentId.map { _ -> n } }.groupMapToSeqs { _._1 } { _._2 }
					.withDefaultValue(Empty)
				DetailedApiNodeGroup(group.id, group.data,
					rootNodes = nodes.iterator.filterNot { _.hasParent }
						.map { rootNode => ValueTree(rootNode).iterate { parent => nodesPerParent(parent.id) } }
						.toOptimizedSeq)
			},
			models = models.filter { _.name.nonEmpty })
	}
	
	/**
	 * Stores full API documentation
	 * @param name Name of the targeted API
	 * @param namedModels Named models in the specified API
	 * @param nodeGroups Node groups in the specified API
	 * @param connection Implicit DB connection
	 * @return Stored API entry + named model references that couldn't be resolved
	 */
	def storeApi(name: String, namedModels: Iterable[NewNamedModel], nodeGroups: Iterable[NewApiNodeGroup])
	            (implicit connection: Connection) =
	{
		// Pulls the existing API documentation
		val existingVersion = AccessApi.withName(name).pull.map(pullDetailsFor)
		// If no existing documentation was found, inserts a new API entry
		val api = StoreResult.existingOrInsert[Api](existingVersion) { ApiDbModel.insert(ApiData(name)) }
		// Stores the named model types
		val (storedNamedModels, unmatchedModelReferences) = storeApiModels(api.id, namedModels)
		
		existingVersion match {
			// Case: Updating an existing version
			case Some(existing) => storeApiNodes(existing, nodeGroups, storedNamedModels.map { _.wrapped })
			// Case: Inserting a new API version
			case None =>
				// Stores all referenced token types & auth scopes (WET WET)
				implicit val tokenTypes: MapAccess[String, StoreResult[AuthTokenType]] = AuthDb.storeApiTokenTypes(
					api.id, nodeGroups.iterator.flatMap { _.tokenTypeNamesIterator }.toSet)
				implicit val scopes: MapAccess[String, StoreResult[Scope]] = AuthDb.storeApiScopes(api.id,
					nodeGroups.iterator.flatMap { _.scopeNamesIterator }.toSet)
				
				// Inserts the API node groups
				val nodesByGroupId = ApiNodeGroupDbModel.insertFrom(nodeGroups.toOptimizedSeq) { group =>
					ApiNodeGroupData(api.id, group.name, group.description)
				} { (inserted, group ) => inserted.id -> group.nodes }
				
				// Inserts the individual API nodes
				val modelNameToId = storedNamedModels.iterator.map { m => standardizeRef(m.name) -> m.id }.toMap
				val nodeNameToId = mutable.Map[String, Int]()
				nodesByGroupId.foreach { case (groupId, nodes) =>
					nodes.foreach { insertNode(api.id, groupId, _, modelNameToId, nodeNameToId) }
				}
		}
		
		api -> unmatchedModelReferences
	}
	
	/**
	 * Stores named API models
	 * @param apiId ID of the updated API
	 * @param models **All** named models in the API's current documentation
	 * @param connection Implicit DB connection
	 * @return Returns two values:
	 *         1. Stored models
	 *         1. Model names that were referenced but couldn't be resolved
	 */
	def storeApiModels(apiId: Int, models: Iterable[NewNamedModel])(implicit connection: Connection) = {
		// Pulls existing models from the DB
		val existingModelsByName = AccessModelSchemas.complete.active.named.inApi(apiId).toMapBy { _.name }
		
		// Deprecates existing models that are not present in the new set
		AccessModelSchemas(existingModelsByName.valuesIterator
			.filterNot { existing => models.exists { _.name == existing.name } }.map { _.id })
			.deprecate()
		
		// Divides the input into inserts and updates
		val (newModels, modelMatches) = models.divideZipWith { m => existingModelsByName.get(m.name) }
		val modelNameToId = mutable.Map[String, Int]()
		
		// Handles model updates
		val (updatedModels, delayedUpdatedModelPropInserts) = modelMatches.splitMap { case (model, existing) =>
			StoreResult.existed(existing) -> ModelSchemaDb.updateNamed(existing, model, modelNameToId)
		}
		// Inserts the new models
		val (insertedNewModels, delayedNewModelPropInserts) = newModels.splitMap {
			ModelSchemaDb.insert(apiId, _, modelNameToId)
		}
		
		// Resolves the delayed property inserts, if at all possible
		val missingReferences = Pair(delayedUpdatedModelPropInserts, delayedNewModelPropInserts)
			.iterator.flatten.flatten
			.flatMap { case (ref, modelName, completeInsert) =>
				val referencedModelId = modelNameToId.get(ref)
				completeInsert(referencedModelId)
				
				if (referencedModelId.isEmpty)
					Some(modelName)
				else
					None
			}
			.toSet
		
		(updatedModels ++ insertedNewModels, missingReferences)
	}
	
	/**
	 * Stores API node information.
	 * Assumes that the full documentation is included.
	 * @param existing Existing API documentation (pulled before updating the models)
	 * @param nodeGroups Node groups in the current documentation version
	 * @param namedModels Named models from the current documentation version
	 * @param connection Implicit DB connection
	 */
	def storeApiNodes(existing: DetailedApi, nodeGroups: Iterable[NewApiNodeGroup], namedModels: Iterable[ModelSchema])
	                 (implicit connection: Connection): Unit =
	{
		val apiId = existing.id
		val modelNameToId = namedModels.iterator.map { model => standardizeRef(model.name) -> model.id }.toMap
		
		// Stores all referenced token types & auth scopes
		implicit val tokenTypes: MapAccess[String, StoreResult[AuthTokenType]] = AuthDb.storeApiTokenTypes(apiId,
			nodeGroups.iterator.flatMap { _.tokenTypeNamesIterator }.toSet)
		implicit val scopes: MapAccess[String, StoreResult[Scope]] = AuthDb.storeApiScopes(apiId,
			nodeGroups.iterator.flatMap { _.scopeNamesIterator }.toSet)
		
		val nodesByGroup = updateExclusiveMatches(existing.nodeGroups, nodeGroups) {
			_.name.toLowerCase } { _.name.toLowerCase } {
			// Case: Match => Updates the group name & description, if necessary
			(existing, group) =>
				if (existing.name != group.name)
					existing.access.name.set(group.name)
				if (existing.description != group.description)
					existing.access.description.set(group.description)
				
				existing.id -> group.nodes
		} { AccessApiNodeGroups(_).deprecate() } {
			// Inserts the missing groups
			newGroups =>
				ApiNodeGroupDbModel.insertFrom(newGroups) {
					group => ApiNodeGroupData(apiId, group.name, group.description) } {
					(inserted, group) => inserted.id -> group.nodes }
		}
		
		// Updates the API nodes next
		// Checks for matches with existing nodes
		val existingNodeByPath = existing.nodesIterator.map { n => standardizePath(n.absolutePath) -> n }.toMap
		val newNodeByName = nodeGroups.iterator.flatMap { _.nodes }.map { n => standardizeRef(n.name) -> n }.toMap
			.mapInputView(standardizeRef)
		// Maps nodes as they get stored.
		// All input must be standardized.
		val nodeNameToId = mutable.Map.empty[String, Int]
		val handledExistingNodeIds = nodesByGroup.iterator
			.flatMap { case (groupId, nodes) => nodes.iterator.map { groupId -> _ } }
			.flatMap { case (groupId, node) =>
				// Matches the nodes by their absolute path
				val path = standardizePath(node.fullPathUsing(newNodeByName))
				existingNodeByPath.get(path) match {
					// Case: Match with an existing node => Checks whether to replace or to update the existing version
					case Some(existingNode) =>
						val shouldReplace = existingNode.groupId != groupId || (existingNode.name !~== node.name) ||
							existingNode.parentId != nodeNameToId.get(standardizeRef(node.parentName))
							
						// Case: Incompatible changes => Deprecates the previous version and inserts a new one
						if (shouldReplace) {
							existingNode.access.deprecate()
							insertNode(apiId, groupId, node, modelNameToId, nodeNameToId)
						}
						// Case: The node itself is compatible => Proceeds to update its data
						else {
							nodeNameToId += (standardizeRef(node.name) -> existingNode.id)
							updateNode(apiId, existingNode, node, modelNameToId)
						}
						
						Some(existingNode.id)
						
					// Case: New node => Inserts it
					case None =>
						insertNode(apiId, groupId, node, modelNameToId, nodeNameToId)
						None
				}
			}
			.toSet
		
		// Deprecates existing nodes that were no longer included
		AccessApiNodes(existing.nodesIterator.map { _.id }.toSet -- handledExistingNodeIds).deprecate()
	}
	
	private def updateNode(apiId: Int, existing: DetailedApiNode, node: NewApiNode, modelNameToId: Map[String, Int])
	                      (implicit connection: Connection,
	                       tokenTypes: MapAccess[String, StoreResult[AuthTokenType]],
	                       scopes: MapAccess[String, StoreResult[Scope]]) =
	{
		// Updates the name, description and/or relative path, if necessary
		if (existing.relativePath != node.path)
			existing.access.relativePath.set(node.path)
		if (existing.name != node.name)
			existing.access.name.set(node.name)
		if (existing.description != node.description)
			existing.access.description.set(node.description)
		
		updatePathParams(apiId, existing.id, existing.directPathParams, node.pathParams, modelNameToId)
		updateTokenTypeLinks(existing.id, existing.definedTokenTypes, node.allTokenOptions)
		updateScopeLinks(existing.id, existing.definedScopes, node.allScopes)
		
		// Updates the method implementations
		updateExclusiveMatches(existing.implementations, node.methodImplementations) { _.method } { _.method } {
			// Case: Matching implementation => Updates the existing implementation
			(existing, implementation) =>
				// Updates the direct properties, if needed
				if (existing.description != implementation.description)
					existing.access.description.set(implementation.description)
				if (existing.requiresRequestBody == implementation.requestBodyIsOptional)
					existing.access.requiresRequestBody.set(!implementation.requestBodyIsOptional)
				
				// Updates the linked data:
				//      1. Query params
				//      2. Request body
				//      3. Responses (status + body)
				updateQueryParams(apiId, existing.id, existing.queryParams, implementation.queryParams, modelNameToId)
				updateRequestBodyOptions(apiId, existing.id, existing.requestBodyOptions,
					implementation.requestBodyOptions, modelNameToId)
				updateResponsesStatuses(apiId, existing.id, existing.responses, implementation.responses, modelNameToId)
				
				StoreResult.existed[ApiNodeMethodImplementation](existing)
				
		} { AccessApiNodeMethodImplementations(_).deprecate() } {
			// Inserts the new method implementations
			newImplementations =>
				newImplementations.map { insertMethodImplementation(apiId, existing.id, _, modelNameToId) }
		}
		
		StoreResult.existed(existing)
	}
	private def insertNode(apiId: Int, groupId: Int, node: NewApiNode, modelNameToId: Map[String, Int],
	                       nodeNameToId: mutable.Map[String, Int])
	                      (implicit connection: Connection,
	                       tokenTypes: MapAccess[String, StoreResult[AuthTokenType]],
	                       scopes: MapAccess[String, StoreResult[Scope]]) =
	{
		// Inserts the API node entry
		val stored = ApiNodeDbModel.insert(ApiNodeData(groupId, node.name, standardizeRef(node.name), node.path,
			parentId = node.parentName.ifNotEmpty.flatMap { parentName =>
				val parentId = nodeNameToId.get(standardizeRef(parentName))
				if (parentId.isEmpty)
					scribe.in("insert.apiNode.resolveRef.parent").recoverable("Reference to a non-existing parent node",
						Model.from("node" -> node.name, "parent" -> parentName))
				parentId
			},
			description = node.description))
		// Remembers this node so that parent references may be resolved
		nodeNameToId += (standardizeRef(node.name) -> stored.id)
		
		// Inserts the rest of the node data
		insertPathParams(apiId, stored.id, node.pathParams, modelNameToId)
		insertTokenTypeLinks(stored.id, node.allTokenOptions)
		insertScopeLinks(stored.id, node.allScopes.toOptimizedSeq)
		node.methodImplementations.foreach { insertMethodImplementation(apiId, stored.id, _, modelNameToId) }
		
		StoreResult.inserted(stored)
	}
	
	private def updatePathParams(apiId: Int, nodeId: Int, existingParams: Option[CompleteModelSchema],
	                             params: Seq[NewProperty], modelNameToId: Map[String, Int])
	                            (implicit connection: Connection): Unit =
	{
		// Case: No path parameters present => Deprecates any previous path parameters, where applicable
		if (params.isEmpty)
			existingParams.foreach { params =>
				params.access.deprecate()
				AccessPathParameterSchemaLinks.active.ofNode(nodeId).deprecate()
			}
		else
			existingParams match {
				// Case: Updating existing path params => Updates
				case Some(existingParams) => ModelSchemaDb.updateNameless(existingParams, params, modelNameToId)
				// Case: Adding new path params => Inserts
				case None => insertPathParams(apiId, nodeId, params, modelNameToId)
			}
	}
	private def insertPathParams(apiId: Int, nodeId: Int, params: Iterable[NewProperty],
	                             modelNameToId: Map[String, Int])
	                            (implicit connection: Connection) =
		ModelSchemaDb.insertNamelessUseCase(apiId, params, modelNameToId) { modelId =>
			PathParametersSchemaLinkDbModel.insert(PathParametersSchemaLinkData(nodeId, modelId))
		}
	
	private def updateTokenTypeLinks(nodeId: Int, existingTokenLinks: Iterable[NodeAuthTypeLink],
	                                 tokenTypes: Iterable[(Option[Method], String, String)])
	                                (implicit connection: Connection,
	                                 storedTypes: MapAccess[String, StoreResult[AuthTokenType]]): Unit =
	{
		// Matches by targeted method & token type ID
		updateExclusiveMatches(
			existingTokenLinks,
			tokenTypes.iterator.map { case (method, typeName, description) =>
				(method, storedTypes(typeName).id, description)
			}) { l => l.method -> l.authTypeId } { case (method, typeId, _) => method -> typeId } {
			case (existing, (_, _, description)) =>
				// Case: Match => Updates the link description if necessary
				if (existing.description != description)
					existing.access.description.set(description)
				StoreResult.existed(existing)
				
		} { AccessNodeAuthTypeLinks(_).deprecate() } {
			// Inserts the missing links
			linksToInsert =>
				NodeAuthTypeLinkDbModel
					.insert(linksToInsert.map { case (method, typeId, description) =>
						NodeAuthTypeLinkData(nodeId, typeId, method, description)
					})
					.map {
						//noinspection ConvertibleToMethodValue
						StoreResult.inserted(_)
					}
		}
	}
	private def insertTokenTypeLinks(nodeId: Int, tokenTypes: Iterable[(Option[Method], String, String)])
	                                (implicit connection: Connection,
	                                 storedTypes: MapAccess[String, StoreResult[AuthTokenType]]) =
		NodeAuthTypeLinkDbModel.insert(tokenTypes.view
			.map { case (method, name, description) =>
				NodeAuthTypeLinkData(nodeId, storedTypes(name).id, method, description)
			}
			.toOptimizedSeq)
			
	private def updateScopeLinks(nodeId: Int, existingScopeLinks: Iterable[DetailedNodeAuthScopeLink],
	                             scopeLinks: IterableOnce[(Option[Method], Seq[String], String)])
	                            (implicit connection: Connection, storedScopes: MapAccess[String, StoreResult[Scope]]): Unit =
	{
		// Matches scope links by three parameters:
		//      1. Targeted method
		//      2. Primary scope ID
		//      3. Other scope IDs (unordered)
		updateExclusiveMatches(existingScopeLinks,
			scopeLinks.iterator.map { case (method, scopes, description) =>
				val matchedScopes = scopes.map(storedScopes.apply)
				(method, matchedScopes.head.id, matchedScopes.view.tail.map { _.id }.toSet, description)
			}) { l => (l.method, l.scopeId, l.scopes.view.tail.iterator.map { _.id }.toSet) } {
			case (method, primaryScopeId, otherScopeIds, _) => (method, primaryScopeId, otherScopeIds) } {
			case (existing, (_, _, _, description)) =>
				// Case: Match => Updates description, if necessary, but makes no other changes
				if (existing.description != description)
					existing.access.description.set(description)
				()
		} { AccessNodeAuthScopeLinks(_).deprecate() } {
			inserts =>
				// Inserts the new links
				val additionalInserts = NodeAuthScopeLinkDbModel
					.insertFrom(inserts) { case (method, primaryScopeId, _, description) =>
						NodeAuthScopeLinkData(nodeId, primaryScopeId, method, description)
					} { case (inserted, (_, _, otherScopeIds, _)) =>
						otherScopeIds.map { AdditionalNodeAuthScopeRequirementData(inserted.id, _) }
					}
				AdditionalNodeAuthScopeRequirementDbModel.insert(additionalInserts.flatten)
				Empty
		}
	}
	private def insertScopeLinks(nodeId: Int, scopes: Seq[(Option[Method], Seq[String], String)])
	                            (implicit connection: Connection, storedScopes: MapAccess[String, StoreResult[Scope]]) =
	{
		// Stores the scope links and prepares the additional requirements
		val storedLinks = NodeAuthScopeLinkDbModel.insertFrom(scopes) { case (method, scopes, description) =>
			NodeAuthScopeLinkData(nodeId, storedScopes(scopes.head).id, method, description)
		} { case (storedLink, (_, scopes, _)) =>
			storedLink -> scopes.tail.map { scope =>
				AdditionalNodeAuthScopeRequirementData(storedLink.id, storedScopes(scope).id)
			}
		}
		// Inserts the additional requirements
		val additionalRequirements = AdditionalNodeAuthScopeRequirementDbModel
			.insert(storedLinks.flatMap { _._2 }).groupBy { _.scopeLinkId }.withDefaultValue(Empty)
		
		// Combines the inserted data
		storedLinks.map { case (link, _) =>
			StoreResult.inserted(link.withAdditionalRequirements(additionalRequirements(link.id)))
		}
	}
	
	private def insertMethodImplementation(apiId: Int, nodeId: Int, implementation: NewMethodImplementation,
	                                       modelNameToId: Map[String, Int])
	                                      (implicit connection: Connection) =
	{
		// Inserts the method implementation
		val stored = ApiNodeMethodImplementationDbModel.insert(
			ApiNodeMethodImplementationData(nodeId, implementation.method, implementation.description,
				requiresRequestBody = !implementation.requestBodyIsOptional &&
					implementation.requestBodyOptions.nonEmpty))
		
		// Inserts linked data
		insertQueryParams(apiId, stored.id, implementation.queryParams, modelNameToId)
		implementation.requestBodyOptions.foreach { insertRequestBodyOption(apiId, stored.id, _, modelNameToId) }
		implementation.responses.foreach { case (statusCode, body, description, hasBody) =>
			insertResponseStatus(apiId, stored.id, statusCode, body, description, modelNameToId, hasBody = hasBody)
		}
		
		StoreResult.inserted(stored)
	}
	
	private def updateQueryParams(apiId: Int, implementationId: Int, existingParams: Option[CompleteModelSchema],
	                              params: Iterable[NewProperty], modelNameToId: Map[String, Int])
	                             (implicit connection: Connection): Unit =
		existingParams match {
			case Some(existing) =>
				// Case: Removed query params => Deprecates the existing entries
				if (params.isEmpty) {
					existing.access.deprecate()
					AccessQueryParameterSchemaLinks.active.ofImplementation(implementationId).deprecate()
				}
				// Case: Updated query params => Updates the existing entries
				else
					ModelSchemaDb.updateNameless(existing, params, modelNameToId)
			
			case None =>
				// Case: Added query params => Inserts the new entries
				if (params.nonEmpty)
					insertQueryParams(apiId, implementationId, params, modelNameToId)
		}
	private def insertQueryParams(apiId: Int, implementationId: Int, params: Iterable[NewProperty],
	                              modelNameToId: Map[String, Int])
	                             (implicit connection: Connection) =
		ModelSchemaDb.insertNamelessUseCase(apiId, params, modelNameToId) { modelId =>
			QueryParametersSchemaLinkDbModel.insert(QueryParametersSchemaLinkData(implementationId, modelId))
		}
	
	private def updateRequestBodyOptions(apiId: Int, implementationId: Int,
	                                     existingOptions: IterableOnce[DetailedRequestBodySchema],
	                                     bodyOptions: IterableOnce[NewBodyDefinition], modelNameToId: Map[String, Int])
	                                    (implicit connection: Connection): Unit =
	{
		// Matches the body versions using three parameters:
		//      1. Data type
		//      2. Referenced named model type ID
		//      3. Whether an array is received
		//      4. Custom content type
		updateExclusiveMatches(existingOptions,
			bodyOptions.iterator.map { body =>
				val modelTypeId = body.referencedType.ifNotEmpty.flatMap { n => modelNameToId.get(standardizeRef(n)) }
				body -> modelTypeId
			}) {
			e => (e.dataType, e.modelType.filter { _.hasName }.map { _.id }, e.isArray, e.customContentType) } {
			case (body, modelTypeId) => (body.dataType, modelTypeId, body.isArray, body.contentType.mkString) } {
			// Case: Match => Updates the description and nameless model properties, if needed
			case (existing, (body, _)) =>
				// Updates the details, if necessary
				if (existing.description != body.details)
					existing.access.description.set(body.details)
					
				// Updates the referenced model type
				if (body.properties.nonEmpty)
					existing.modelType.foreach { ModelSchemaDb.updateNameless(_, body.properties, modelNameToId) }
				
				()
				
		} { bodyIds =>
			// Deprecates the request body schemas
			AccessRequestBodySchemas(bodyIds).deprecate()
			// If nameless models were referenced, deprecates them
			AccessModelSchemas.active.nameless.whereReferencingRequestBodySchemas.in(bodyIds).deprecate()
			
		} { newBodies =>
			newBodies.map { case (body, _) => insertRequestBodyOption(apiId, implementationId, body, modelNameToId) }
		}
	}
	private def insertRequestBodyOption(apiId: Int, implementationId: Int, body: NewBodyDefinition,
	                                    modelNameToId: Map[String, Int])
	                                   (implicit connection: Connection) =
	{
		// Resolves the referenced model type
		val schemaId = body.referencedType.ifNotEmpty match {
			// Case: Named type reference => Attempts to resolve it
			case Some(typeReference) => modelNameToId.get(standardizeRef(typeReference))
			// Case: Unnamed model => Inserts a new nameless model schema, if needed
			case None => ModelSchemaDb.insertNameless(apiId, body.properties, modelNameToId).map { _.id }
		}
		// Inserts the request body schema
		RequestBodySchemaDbModel.insert(RequestBodySchemaData(implementationId, body.dataType, schemaId,
			customContentType = body.contentType.mkString, description = body.details, isArray = body.isArray))
	}
	
	private def updateResponsesStatuses(apiId: Int, implementationId: Int, existing: Iterable[DetailedPossibleResponse],
	                                    responses: Iterable[(Int, Option[NewBodyDefinition], String, UncertainBoolean)],
	                                    modelNameToId: Map[String, Int])
	                                   (implicit connection: Connection) =
	{
		// Matches against existing statuses by using status codes
		updateExclusiveMatches(existing, responses) { _.statusCode } { _._1 } {
			// Case: Updating an existing status entry
			case (existing, (_, body, description, hasBody)) =>
				// Updates the description & hasBody -properties
				if (existing.description != description)
					existing.access.description.set(description)
				if (existing.hasBody != hasBody)
					existing.access.hasBody.set(hasBody)
					
				// Updates the response body
				body match {
					case Some(body) =>
						existing.body match {
							// Case: Possibly updating an existing response body
							//       => Checks whether there are incompatible changes
							case Some(existingBody) =>
								val referencedModelTypeId = body.referencedType.ifNotEmpty.flatMap { ref =>
									modelNameToId.get(standardizeRef(ref))
								}
								// Checks:
								//      1. Whether the body data type has changed
								//      2. Whether yielding one or more instances (isArray)
								//      3. Whether the referenced **named** model type has changed
								val shouldReplace = existingBody.dataType != body.dataType ||
									existingBody.isArray != body.isArray ||
									referencedModelTypeId != existingBody.modelType.filter { _.hasName }.map { _.id }
								// Case: Replacing the previous body version
								//       => Deprecates previous entry (including referenced named model types)
								//          and inserts a new version
								if (shouldReplace) {
									existingBody.access.deprecate()
									existingBody.modelType.filter { _.isNameless }.foreach { _.access.deprecate() }
									ResponseBodySchemaDbModel.insert(ResponseBodySchemaData(existing.id,
										dataType = body.dataType,
										modelTypeId = referencedModelTypeId.orElse {
											ModelSchemaDb.insertNameless(apiId, body.properties, modelNameToId)
										},
										isArray = body.isArray))
								}
								// Case: Non-breaking changes (if any) => Updates the nameless model content,
								//       if applicable
								else
									existingBody.modelType.filter { _.isNameless }.foreach {
										ModelSchemaDb.updateNameless(_, body.properties, modelNameToId)
									}
								
							// Case: Adding a new response body
							case None => insertResponseBody(apiId, existing.id, body, modelNameToId)
						}
					// Case: Response body removed => Deprecates the previous documentation
					case None => existing.body.foreach { _.access.deprecate() }
				}
				
				StoreResult.existed[PossibleResponseStatus](existing)
		} {
			// Deprecates the (1) targeted statuses, (2) the referenced response bodies,
			// and (3) associated nameless model schemas
			removedStatusIds =>
				AccessPossibleResponseStatuses(removedStatusIds).deprecate()
				AccessResponseBodySchemas.active.withStatuses(removedStatusIds).deprecate()
				AccessModelSchemas.active.nameless
					.whereReferencingResponseBodySchemas.withStatuses(removedStatusIds).deprecate()
		} {
			// Inserts the missing status (& body) entries
			newResponses => newResponses.map { case (statusCode, body, description, hasBody) =>
				insertResponseStatus(apiId, implementationId, statusCode, body, description, modelNameToId, hasBody)
			}
		}
	}
	private def insertResponseStatus(apiId: Int, implementationId: Int, statusCode: Int,
	                                 body: Option[NewBodyDefinition], description: String,
	                                 modelNameToId: Map[String, Int], hasBody: UncertainBoolean)
	                                (implicit connection: Connection) =
	{
		// Stores the status entry
		val stored = PossibleResponseStatusDbModel.insert(PossibleResponseStatusData(implementationId,
			statusCode, description, hasBody = hasBody))
		
		// Stores the response body, if applicable
		body.foreach { insertResponseBody(apiId, stored.id, _, modelNameToId) }
		
		StoreResult.inserted(stored)
	}
	private def insertResponseBody(apiId: Int, statusId: Int, body: NewBodyDefinition, modelNameToId: Map[String, Int])
	                              (implicit connection: Connection) =
		ResponseBodySchemaDbModel.insert(ResponseBodySchemaData(statusId,
			dataType = body.dataType,
			// Resolves the referenced type
			modelTypeId = body.referencedType.ifNotEmpty.flatMap { ref => modelNameToId.get(standardizeRef(ref)) }
				// Handles the nameless model type insert, if applicable
				.orElse { ModelSchemaDb.insertNameless(apiId, body.properties, modelNameToId).map { _.id } },
			isArray = body.isArray))
	
	private def standardizePath(path: String) = path.replaceEachMatchOf(pathParamR, "*").toLowerCase
}
