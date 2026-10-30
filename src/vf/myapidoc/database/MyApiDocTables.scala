package vf.myapidoc.database

import utopia.vault.database.Tables
import utopia.vault.model.immutable.Table
import vf.myapidoc.util.Common._

/**
  * Used for accessing the database tables introduced in this project
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
object MyApiDocTables extends Tables(cPool)
{
	// ATTRIBUTES	--------------------
	
	/**
	 * Table that contains apis (Represents a documented API)
	 */
	lazy val api = apply("api")
	
	lazy val docUpdate = apply("doc_update")
	
	/**
	  * Table that contains alternative property types (Defines an alternative supported data type 
	  * for a property.)
	  */
	lazy val alternativePropertyType = apply("alternative_property_type")
	/**
	 * Table that contains possible property values (Defines a possible value for an enum property)
	 */
	lazy val possiblePropertyValue = apply("possible_property_value")
	
	/**
	  * Table that contains api nodes (Represents an individual API node implementation)
	  */
	lazy val apiNode = apply("api_node")
	
	/**
	  * Table that contains api node groups (Defines a group of API nodes)
	  */
	lazy val apiNodeGroup = apply("api_node_group")
	
	/**
	  * Table that contains api node method implementations (Describes how a specific HTTP method is 
	  * implemented in an API node.)
	  */
	lazy val apiNodeMethodImplementation = apply("api_node_method_implementation")
	
	/**
	  * Table that contains auth token types (Represents some (custom) token type used by the 
	  * documented API.)
	  */
	lazy val authTokenType = apply("auth_token_type")
	
	/**
	  * Table that contains model schemas (Defines properties in an input or output model / JSON 
	  * object.)
	  */
	lazy val modelSchema = apply("model_schema")
	
	/**
	  * Table that contains node auth scope links (Describes an authorization scope required by an 
	  * API node. 
	  * If multiple scopes are linked, they typically represent alternative auth scopes, usually for 
	  * different purposes.)
	  */
	lazy val nodeAuthScopeLink = apply("node_auth_scope_link")
	/**
	 * Table that contains additional node auth scope requirements (Links an additional required
	 * scope to an auth-scope link)
	 */
	lazy val additionalNodeAuthScopeRequirement = apply("additional_node_auth_scope_requirement")
	
	/**
	  * Table that contains node auth type links
	  */
	lazy val nodeAuthTypeLink = apply("node_auth_type_link")
	
	/**
	  * Table that contains path parameter schema links (Applies a specific schema to the the path 
	  * parameters received by an API node.)
	  */
	lazy val pathParametersSchemaLink = apply("path_parameters_schema_link")
	
	/**
	  * Table that contains possible response statuses (Defines a possible response status for an API 
	  * node method call.)
	  */
	lazy val possibleResponseStatus = apply("possible_response_status")
	
	/**
	  * Table that contains property schemas (Defines an individual property in a model schema.)
	  */
	lazy val propertySchema = apply("property_schema")
	
	/**
	  * Table that contains query parameter schema links (Applies a specific schema to the query 
	  * parameters accepted by an API node.)
	  */
	lazy val queryParametersSchemaLink = apply("query_parameters_schema_link")
	
	/**
	  * Table that contains request body schemas (Applies a specific schema to the request body 
	  * accepted by an API node. 
	  * Multiple alternative schemas may be defined separately.)
	  */
	lazy val requestBodySchema = apply("request_body_schema")
	
	/**
	 * Table that contains response body schemas (Defines how an API node's response body is formed.)
	 */
	lazy val responseBodySchema = apply("response_body_schema")
	
	/**
	  * Table that contains scopes (Represents an authorization scope in the documented API.)
	  */
	lazy val scope = apply("scope")
	
	/**
	 * Table that contains example property values (Documents a single property value received from
	 * a tested API)
	 */
	lazy val examplePropertyValue = apply("example_property_value")
	/**
	 * Table that contains example responses (Represents a response received from a tested API)
	 */
	lazy val exampleResponse = apply("example_response")
	
	
	// OTHER	--------------------
	
	private def apply(tableName: String): Table = apply(dbName, tableName)
}

