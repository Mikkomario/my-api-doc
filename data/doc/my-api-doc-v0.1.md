# My Api Doc
Version: **v0.1**  
Updated: 2026-10-28

## Table of Contents
- [Enumerations](#enumerations)
  - [Property Type](#property-type)
  - [Token Use Type](#token-use-type)
- [Packages & Classes](#packages-and-classes)
  - [Api](#api)
    - [Additional Node Auth Scope Requirement](#additional-node-auth-scope-requirement)
    - [Api](#api)
    - [Api Node](#api-node)
    - [Api Node Group](#api-node-group)
    - [Api Node Method Implementation](#api-node-method-implementation)
    - [Body Schema](#body-schema)
    - [Doc Update](#doc-update)
    - [Node Auth Link](#node-auth-link)
    - [Node Auth Scope Link](#node-auth-scope-link)
    - [Node Auth Type Link](#node-auth-type-link)
    - [Path Parameters Schema Link](#path-parameters-schema-link)
    - [Possible Response Status](#possible-response-status)
    - [Query Parameters Schema Link](#query-parameters-schema-link)
    - [Request Body Schema](#request-body-schema)
    - [Response Body Schema](#response-body-schema)
    - [Versioned Doc Element](#versioned-doc-element)
  - [Auth](#auth)
    - [Auth Token Type](#auth-token-type)
    - [Scope](#scope)
  - [Schema](#schema)
    - [Alternative Property Type](#alternative-property-type)
    - [Model Schema](#model-schema)
    - [Possible Property Value](#possible-property-value)
    - [Property Schema](#property-schema)

## Enumerations
Below are listed all enumerations introduced in My Api Doc, in alphabetical order  

### Property Type
An enumeration for different data types in input and output models

Key: `id: Int`  
Default Value: **Any Prop**

**Values:**
- **Any Prop** (1) - An undefined property type. Used when the type is not known or doesn't match any other type.
- **String Prop** (2)
- **Int Prop** (3)
- **Decimal Prop** (4) - A property type for decimal numbers: Double and Float.
- **Long Prop** (5)
- **Boolean Prop** (6) - A boolean property type. Transmitted in JSON as `true` or `false`.
- **Instant Prop** (7) - A timestamp property type. Transmitted in JSON as ISO 8601 strings.
- **Duration Prop** (8) - A property for representing durations. Transmitted in JSON by coupling amount and unit, e.g. "48 seconds" or "2min"
- **Model Prop** (9) - A JSON object type. Usually coupled with a model type reference.

Utilized by the following 5 classes:
- [Alternative Property Type](#alternative-property-type)
- [Body Schema](#body-schema)
- [Property Schema](#property-schema)
- [Request Body Schema](#request-body-schema)
- [Response Body Schema](#response-body-schema)

### Token Use Type
An enumeration for different token use types

Key: `id: Int`  
Default Value: **Bearer**

**Values:**
- **Bearer** (1) - Represents a case where the token is used for bearer authentication
- **Basic** (2) - Represents the basic authentication use-case, which combines username and password.

Utilized by the following 1 classes:
- [Auth Token Type](#auth-token-type)

## Packages and Classes
Below are listed all classes introduced in My Api Doc, grouped by package and in alphabetical order.  
There are a total number of 3 packages and 22 classes

### Api
This package contains the following 16 classes: [Additional Node Auth Scope Requirement](#additional-node-auth-scope-requirement), [Api](#api), [Api Node](#api-node), [Api Node Group](#api-node-group), [Api Node Method Implementation](#api-node-method-implementation), [Body Schema](#body-schema), [Doc Update](#doc-update), [Node Auth Link](#node-auth-link), [Node Auth Scope Link](#node-auth-scope-link), [Node Auth Type Link](#node-auth-type-link), [Path Parameters Schema Link](#path-parameters-schema-link), [Possible Response Status](#possible-response-status), [Query Parameters Schema Link](#query-parameters-schema-link), [Request Body Schema](#request-body-schema), [Response Body Schema](#response-body-schema), [Versioned Doc Element](#versioned-doc-element)

#### Additional Node Auth Scope Requirement
Links an additional required scope to an auth-scope link

##### Details

##### Properties
Additional Node Auth Scope Requirement contains the following 2 properties:
- **Scope Link Id** - `scopeLinkId: Int` - ID of the auth-scope link that requires the additional scope
  - Refers to [Node Auth Scope Link](#node-auth-scope-link)
- **Scope Id** - `scopeId: Int` - ID of the additional authorization scope required by the node.
  - Refers to [Scope](#scope)

#### Api
Represents a documented API

##### Details
- **Chronologically** indexed
- Uses 2 database **indices**: `name`, `created`

##### Properties
Api contains the following 2 properties:
- **Name** - `name: String` - (Human readable) name of this API
- **Created** - `created: Instant` - Time when this api was added to the database

##### Referenced from
- [Api Node Group](#api-node-group).`apiId`
- [Auth Token Type](#auth-token-type).`apiId`
- [Doc Update](#doc-update).`apiId`
- [Model Schema](#model-schema).`apiId`
- [Scope](#scope).`apiId`

#### Api Node
Represents an individual API node implementation

##### Details
- Uses 2 database **indices**: `name_no_spaces`, `relative_path`

##### Properties
Api Node contains the following 8 properties:
- **Original Update Id** - `originalUpdateId: Int` - ID of the update that introduced this element (version)
  - Refers to [Doc Update](#doc-update)
- **Group Id** - `groupId: Int` - ID of the node group this node belongs to
  - Refers to [Api Node Group](#api-node-group)
- **Name** - `name: String` - Human-readable name of this node
- **Name No Spaces** - `nameNoSpaces: String` - A lowercase version of the node name, with no whitespace included. Used for indexing & search.
- **Relative Path** - `relativePath: String` - Path of this node, relative to the parent node (if defined) or the API root (if no parent is defined). 
May contain path parameters.
- **Removing Update Id** - `removingUpdateId: Option[Int]` - ID of the update that removed or replaced this element (version)
  - Refers to [Doc Update](#doc-update)
- **Parent Id** - `parentId: Option[Int]` - ID of the node that appears directly above/before this node on the path. 
None if no parent node appears between this node and the API root.
  - Refers to [Api Node](#api-node)
- **Description** - `description: String` - A description of this API node.

##### Referenced from
- [Api Node](#api-node).`parentId`
- [Api Node Method Implementation](#api-node-method-implementation).`nodeId`
- [Node Auth Link](#node-auth-link).`nodeId`
- [Node Auth Scope Link](#node-auth-scope-link).`nodeId`
- [Node Auth Type Link](#node-auth-type-link).`nodeId`
- [Path Parameters Schema Link](#path-parameters-schema-link).`nodeId`

#### Api Node Group
Defines a group of API nodes

##### Details
- Uses **index**: `name`

##### Properties
Api Node Group contains the following 5 properties:
- **Original Update Id** - `originalUpdateId: Int` - ID of the update that introduced this element (version)
  - Refers to [Doc Update](#doc-update)
- **Api Id** - `apiId: Int` - ID of the API this node group belongs to
  - Refers to [Api](#api)
- **Name** - `name: String` - (Human readable) name of this node group
- **Removing Update Id** - `removingUpdateId: Option[Int]` - ID of the update that removed or replaced this element (version)
  - Refers to [Doc Update](#doc-update)
- **Description** - `description: String` - Description of this group's function or theme

##### Referenced from
- [Api Node](#api-node).`groupId`

#### Api Node Method Implementation
Describes how a specific HTTP method is implemented in an API node.

##### Details
- Uses **index**: `method`

##### Properties
Api Node Method Implementation contains the following 6 properties:
- **Original Update Id** - `originalUpdateId: Int` - ID of the update that introduced this element (version)
  - Refers to [Doc Update](#doc-update)
- **Node Id** - `nodeId: Int` - ID of the API node that contains this implementation.
  - Refers to [Api Node](#api-node)
- **Removing Update Id** - `removingUpdateId: Option[Int]` - ID of the update that removed or replaced this element (version)
  - Refers to [Doc Update](#doc-update)
- **Method** - `method: Method` - The implemented method.
- **Description** - `description: String` - A description of this implementation.
- **Requires Request Body** - `requiresRequestBody: Boolean` - Whether a request body is required for this implementation.

##### Referenced from
- [Possible Response Status](#possible-response-status).`implementationId`
- [Query Parameters Schema Link](#query-parameters-schema-link).`implementationId`
- [Request Body Schema](#request-body-schema).`implementationId`

#### Body Schema
Common trait for request & response body schemas.

##### Details
- Uses **index**: `parent_id`

##### Properties
Body Schema contains the following 6 properties:
- **Original Update Id** - `originalUpdateId: Int` - ID of the update that introduced this element (version)
  - Refers to [Doc Update](#doc-update)
- **Parent Id** - `parentId: Int` - ID of the element that contains this body.
- **Removing Update Id** - `removingUpdateId: Option[Int]` - ID of the update that removed or replaced this element (version)
  - Refers to [Doc Update](#doc-update)
- **Data Type** - `dataType: PropertyType` - Type of this body's contents.
- **Model Type Id** - `modelTypeId: Option[Int]` - If this body contains an object or objects, contains the ID of the model schema that describes the applicable object structure.
  - Refers to [Model Schema](#model-schema)
- **Is Array** - `isArray: Boolean` - Whether this body contains an array of values. False if no more than one value may be included.

#### Doc Update
Represents an API documentation update

##### Details
- **Chronologically** indexed
- Uses **index**: `created`

##### Properties
Doc Update contains the following 2 properties:
- **Api Id** - `apiId: Int` - ID of the updated API
  - Refers to [Api](#api)
- **Created** - `created: Instant` - Time when this doc update was added to the database

##### Referenced from
- [Alternative Property Type](#alternative-property-type).`originalUpdateId`
- [Alternative Property Type](#alternative-property-type).`removingUpdateId`
- [Api Node](#api-node).`originalUpdateId`
- [Api Node](#api-node).`removingUpdateId`
- [Api Node Group](#api-node-group).`originalUpdateId`
- [Api Node Group](#api-node-group).`removingUpdateId`
- [Api Node Method Implementation](#api-node-method-implementation).`originalUpdateId`
- [Api Node Method Implementation](#api-node-method-implementation).`removingUpdateId`
- [Auth Token Type](#auth-token-type).`originalUpdateId`
- [Auth Token Type](#auth-token-type).`removingUpdateId`
- [Body Schema](#body-schema).`originalUpdateId`
- [Body Schema](#body-schema).`removingUpdateId`
- [Model Schema](#model-schema).`originalUpdateId`
- [Model Schema](#model-schema).`removingUpdateId`
- [Node Auth Link](#node-auth-link).`originalUpdateId`
- [Node Auth Link](#node-auth-link).`removingUpdateId`
- [Node Auth Scope Link](#node-auth-scope-link).`originalUpdateId`
- [Node Auth Scope Link](#node-auth-scope-link).`removingUpdateId`
- [Node Auth Type Link](#node-auth-type-link).`originalUpdateId`
- [Node Auth Type Link](#node-auth-type-link).`removingUpdateId`
- [Path Parameters Schema Link](#path-parameters-schema-link).`originalUpdateId`
- [Path Parameters Schema Link](#path-parameters-schema-link).`removingUpdateId`
- [Possible Property Value](#possible-property-value).`originalUpdateId`
- [Possible Property Value](#possible-property-value).`removingUpdateId`
- [Possible Response Status](#possible-response-status).`originalUpdateId`
- [Possible Response Status](#possible-response-status).`removingUpdateId`
- [Property Schema](#property-schema).`originalUpdateId`
- [Property Schema](#property-schema).`removingUpdateId`
- [Query Parameters Schema Link](#query-parameters-schema-link).`originalUpdateId`
- [Query Parameters Schema Link](#query-parameters-schema-link).`removingUpdateId`
- [Request Body Schema](#request-body-schema).`originalUpdateId`
- [Request Body Schema](#request-body-schema).`removingUpdateId`
- [Response Body Schema](#response-body-schema).`originalUpdateId`
- [Response Body Schema](#response-body-schema).`removingUpdateId`
- [Scope](#scope).`originalUpdateId`
- [Scope](#scope).`removingUpdateId`
- [Versioned Doc Element](#versioned-doc-element).`originalUpdateId`
- [Versioned Doc Element](#versioned-doc-element).`removingUpdateId`

#### Node Auth Link
Common trait for links between API nodes and possible authorization requirements

##### Details
- Uses 2 database **indices**: `auth_id`, `method`

##### Properties
Node Auth Link contains the following 6 properties:
- **Original Update Id** - `originalUpdateId: Int` - ID of the update that introduced this element (version)
  - Refers to [Doc Update](#doc-update)
- **Node Id** - `nodeId: Int` - ID of the node that accepts the linked authorization method.
  - Refers to [Api Node](#api-node)
- **Auth Id** - `authId: Int` - ID of the authorization element accepted by the linked node.
- **Removing Update Id** - `removingUpdateId: Option[Int]` - ID of the update that removed or replaced this element (version)
  - Refers to [Doc Update](#doc-update)
- **Method** - `method: Option[Method]` - The HTTP method that the linked authorization method is used for. 
None if the authorization method is used as a default for all methods (where not overridden).
- **Description** - `description: String` - A description concerning this authorization method's function in the linked node.

#### Node Auth Scope Link
Describes an authorization scope required by an API node. 
If multiple scopes are linked, they typically represent alternative auth scopes, usually for different purposes.

##### Details
- Uses 2 database **indices**: `scope_id`, `method`

##### Properties
Node Auth Scope Link contains the following 6 properties:
- **Original Update Id** - `originalUpdateId: Int` - ID of the update that introduced this element (version)
  - Refers to [Doc Update](#doc-update)
- **Node Id** - `nodeId: Int` - ID of the node that accepts the linked authorization method.
  - Refers to [Api Node](#api-node)
- **Scope Id** - `scopeId: Int` - ID of the authorization scope required by the linked node.
  - Refers to [Scope](#scope)
- **Removing Update Id** - `removingUpdateId: Option[Int]` - ID of the update that removed or replaced this element (version)
  - Refers to [Doc Update](#doc-update)
- **Method** - `method: Option[Method]` - The HTTP method that the linked authorization method is used for. 
None if the authorization method is used as a default for all methods (where not overridden).
- **Description** - `description: String` - A description concerning this authorization method's function in the linked node.

##### Referenced from
- [Additional Node Auth Scope Requirement](#additional-node-auth-scope-requirement).`scopeLinkId`

#### Node Auth Type Link
Describes an authorization type accepted by an API node.

##### Details
- Uses 2 database **indices**: `auth_type_id`, `method`

##### Properties
Node Auth Type Link contains the following 6 properties:
- **Original Update Id** - `originalUpdateId: Int` - ID of the update that introduced this element (version)
  - Refers to [Doc Update](#doc-update)
- **Node Id** - `nodeId: Int` - ID of the node that accepts the linked authorization method.
  - Refers to [Api Node](#api-node)
- **Auth Type Id** - `authTypeId: Int` - ID of the authorization type accepted by the linked node.
  - Refers to [Auth Token Type](#auth-token-type)
- **Removing Update Id** - `removingUpdateId: Option[Int]` - ID of the update that removed or replaced this element (version)
  - Refers to [Doc Update](#doc-update)
- **Method** - `method: Option[Method]` - The HTTP method that the linked authorization method is used for. 
None if the authorization method is used as a default for all methods (where not overridden).
- **Description** - `description: String` - A description concerning this authorization method's function in the linked node.

#### Path Parameters Schema Link
Applies a specific schema to the path parameters received by an API node.

##### Details

##### Properties
Path Parameters Schema Link contains the following 4 properties:
- **Original Update Id** - `originalUpdateId: Int` - ID of the update that introduced this element (version)
  - Refers to [Doc Update](#doc-update)
- **Node Id** - `nodeId: Int` - ID of the API node that accepts these parameters.
  - Refers to [Api Node](#api-node)
- **Model Type Id** - `modelTypeId: Int` - ID of the model type that describes the received path parameters.
  - Refers to [Model Schema](#model-schema)
- **Removing Update Id** - `removingUpdateId: Option[Int]` - ID of the update that removed or replaced this element (version)
  - Refers to [Doc Update](#doc-update)

#### Possible Response Status
Defines a possible response status for an API node method call.

##### Details
- Uses **index**: `status_code`

##### Properties
Possible Response Status contains the following 6 properties:
- **Original Update Id** - `originalUpdateId: Int` - ID of the update that introduced this element (version)
  - Refers to [Doc Update](#doc-update)
- **Implementation Id** - `implementationId: Int` - ID of the implementation that may return this response status.
  - Refers to [Api Node Method Implementation](#api-node-method-implementation)
- **Status Code** - `statusCode: Int` - HTTP status code that may be returned by the API node method call.
- **Removing Update Id** - `removingUpdateId: Option[Int]` - ID of the update that removed or replaced this element (version)
  - Refers to [Doc Update](#doc-update)
- **Description** - `description: String` - Description of what this status signifies.
- **Has Body** - `hasBody: UncertainBoolean` - Whether a response body is returned with this status. Uncertain if not specified in the documentation.

##### Referenced from
- [Response Body Schema](#response-body-schema).`statusId`

#### Query Parameters Schema Link
Applies a specific schema to the query parameters accepted by an API node.

##### Details

##### Properties
Query Parameters Schema Link contains the following 4 properties:
- **Original Update Id** - `originalUpdateId: Int` - ID of the update that introduced this element (version)
  - Refers to [Doc Update](#doc-update)
- **Implementation Id** - `implementationId: Int` - ID of the method implementation that receives the linked parameters.
  - Refers to [Api Node Method Implementation](#api-node-method-implementation)
- **Model Type Id** - `modelTypeId: Int` - ID of the schema that defines the accepted path parameters.
  - Refers to [Model Schema](#model-schema)
- **Removing Update Id** - `removingUpdateId: Option[Int]` - ID of the update that removed or replaced this element (version)
  - Refers to [Doc Update](#doc-update)

#### Request Body Schema
Applies a specific schema to the request body accepted by an API node. 
Multiple alternative schemas may be defined separately.

##### Details
- Uses **index**: `implementation_id`

##### Properties
Request Body Schema contains the following 8 properties:
- **Original Update Id** - `originalUpdateId: Int` - ID of the update that introduced this element (version)
  - Refers to [Doc Update](#doc-update)
- **Implementation Id** - `implementationId: Int` - ID of the method implementation that receives this request body.
  - Refers to [Api Node Method Implementation](#api-node-method-implementation)
- **Removing Update Id** - `removingUpdateId: Option[Int]` - ID of the update that removed or replaced this element (version)
  - Refers to [Doc Update](#doc-update)
- **Data Type** - `dataType: PropertyType` - Type of this body's contents.
- **Model Type Id** - `modelTypeId: Option[Int]` - If this body contains an object or objects, contains the ID of the model schema that describes the applicable object structure.
  - Refers to [Model Schema](#model-schema)
- **Is Array** - `isArray: Boolean` - Whether this body contains an array of values. False if no more than one value may be included.
- **Custom Content Type** - `customContentType: String` - Non-standard content type expected from the request body, if applicable.
- **Description** - `description: String` - Description of the expected request body.

#### Response Body Schema
Defines how an API node's response body is formed.

##### Details
- Uses **index**: `status_id`

##### Properties
Response Body Schema contains the following 6 properties:
- **Original Update Id** - `originalUpdateId: Int` - ID of the update that introduced this element (version)
  - Refers to [Doc Update](#doc-update)
- **Status Id** - `statusId: Int` - ID of the status this type of response body is returned with.
  - Refers to [Possible Response Status](#possible-response-status)
- **Removing Update Id** - `removingUpdateId: Option[Int]` - ID of the update that removed or replaced this element (version)
  - Refers to [Doc Update](#doc-update)
- **Data Type** - `dataType: PropertyType` - Type of this body's contents.
- **Model Type Id** - `modelTypeId: Option[Int]` - If this body contains an object or objects, contains the ID of the model schema that describes the applicable object structure.
  - Refers to [Model Schema](#model-schema)
- **Is Array** - `isArray: Boolean` - Whether this body contains an array of values. False if no more than one value may be included.

#### Versioned Doc Element
Common trait for documentation elements which have current and historical versions.

##### Details

##### Properties
Versioned Doc Element contains the following 2 properties:
- **Original Update Id** - `originalUpdateId: Int` - ID of the update that introduced this element (version)
  - Refers to [Doc Update](#doc-update)
- **Removing Update Id** - `removingUpdateId: Option[Int]` - ID of the update that removed or replaced this element (version)
  - Refers to [Doc Update](#doc-update)

### Auth
This package contains the following 2 classes: [Auth Token Type](#auth-token-type), [Scope](#scope)

#### Auth Token Type
Represents some (custom) token type used by the documented API.

##### Details
- Uses **index**: `name_no_spaces`

##### Properties
Auth Token Type contains the following 6 properties:
- **Original Update Id** - `originalUpdateId: Int` - ID of the update that introduced this element (version)
  - Refers to [Doc Update](#doc-update)
- **Api Id** - `apiId: Int` - ID of the API where this token type is used.
  - Refers to [Api](#api)
- **Name** - `name: String` - (Human-readable) name of this token type
- **Name No Spaces** - `nameNoSpaces: String` - A lowercase copy of this token's name with all whitespaces removed. Used for indexing and search.
- **Removing Update Id** - `removingUpdateId: Option[Int]` - ID of the update that removed or replaced this element (version)
  - Refers to [Doc Update](#doc-update)
- **Use Type** - `useType: TokenUseType` - Determines how this token is applied.

##### Referenced from
- [Node Auth Type Link](#node-auth-type-link).`authTypeId`

#### Scope
Represents an authorization scope in the documented API.

##### Details
- Uses **index**: `name`

##### Properties
Scope contains the following 4 properties:
- **Original Update Id** - `originalUpdateId: Int` - ID of the update that introduced this element (version)
  - Refers to [Doc Update](#doc-update)
- **Api Id** - `apiId: Int` - ID of the API where this scope is used.
  - Refers to [Api](#api)
- **Name** - `name: String` - Name / identifier of this scope.
- **Removing Update Id** - `removingUpdateId: Option[Int]` - ID of the update that removed or replaced this element (version)
  - Refers to [Doc Update](#doc-update)

##### Referenced from
- [Additional Node Auth Scope Requirement](#additional-node-auth-scope-requirement).`scopeId`
- [Node Auth Scope Link](#node-auth-scope-link).`scopeId`

### Schema
This package contains the following 4 classes: [Alternative Property Type](#alternative-property-type), [Model Schema](#model-schema), [Possible Property Value](#possible-property-value), [Property Schema](#property-schema)

#### Alternative Property Type
Defines an alternative supported data type for a property.

##### Details

##### Properties
Alternative Property Type contains the following 4 properties:
- **Original Update Id** - `originalUpdateId: Int` - ID of the update that introduced this element (version)
  - Refers to [Doc Update](#doc-update)
- **Prop Id** - `propId: Int` - ID of the property that has this alternative type
  - Refers to [Property Schema](#property-schema)
- **Removing Update Id** - `removingUpdateId: Option[Int]` - ID of the update that removed or replaced this element (version)
  - Refers to [Doc Update](#doc-update)
- **Alt Type** - `altType: PropertyType` - Alternative data type supported by the linked property

#### Model Schema
Defines properties in an input or output model / JSON object.

##### Details
- Uses **index**: `name`

##### Properties
Model Schema contains the following 5 properties:
- **Original Update Id** - `originalUpdateId: Int` - ID of the update that introduced this element (version)
  - Refers to [Doc Update](#doc-update)
- **Api Id** - `apiId: Int` - ID of the API this model type belongs to.
  - Refers to [Api](#api)
- **Removing Update Id** - `removingUpdateId: Option[Int]` - ID of the update that removed or replaced this element (version)
  - Refers to [Doc Update](#doc-update)
- **Name** - `name: String` - Name of this model type (/class). Empty if unnamed. 
Named model types are often referenced from multiple sources, while unnamed types are typically single-use.
- **Description** - `description: String` - Description of this model type's role / function.

##### Referenced from
- [Body Schema](#body-schema).`modelTypeId`
- [Path Parameters Schema Link](#path-parameters-schema-link).`modelTypeId`
- [Property Schema](#property-schema).`modelTypeId`
- [Property Schema](#property-schema).`contentTypeId`
- [Query Parameters Schema Link](#query-parameters-schema-link).`modelTypeId`
- [Request Body Schema](#request-body-schema).`modelTypeId`
- [Response Body Schema](#response-body-schema).`modelTypeId`

#### Possible Property Value
Defines a possible value for an enum property

##### Details

##### Properties
Possible Property Value contains the following 5 properties:
- **Original Update Id** - `originalUpdateId: Int` - ID of the update that introduced this element (version)
  - Refers to [Doc Update](#doc-update)
- **Prop Id** - `propId: Int` - ID of the property that this value may be assigned to
  - Refers to [Property Schema](#property-schema)
- **Value** - `value: String` - The value the linked property may have
- **Removing Update Id** - `removingUpdateId: Option[Int]` - ID of the update that removed or replaced this element (version)
  - Refers to [Doc Update](#doc-update)
- **Description** - `description: String` - Description of this value's role / function

#### Property Schema
Defines an individual property in a model schema.

##### Details
- Uses **index**: `name`

##### Properties
Property Schema contains the following 9 properties:
- **Original Update Id** - `originalUpdateId: Int` - ID of the update that introduced this element (version)
  - Refers to [Doc Update](#doc-update)
- **Model Type Id** - `modelTypeId: Int` - ID of the model type that this property belongs to.
  - Refers to [Model Schema](#model-schema)
- **Removing Update Id** - `removingUpdateId: Option[Int]` - ID of the update that removed or replaced this element (version)
  - Refers to [Doc Update](#doc-update)
- **Name** - `name: String` - Name of this property, as it should appear in the input or output model / JSON object.
- **Data Type** - `dataType: PropertyType` - This property's expected value type.
- **Content Type Id** - `contentTypeId: Option[Int]` - If this property contains a model/object, this is the ID of the contained model's type. 
None if this property uses some other data type.
  - Refers to [Model Schema](#model-schema)
- **Description** - `description: String` - A description of this property's role/function.
- **Is Array** - `isArray: UncertainBoolean`, `false` by default - Whether this property is an array of values. 
False if this property contains a single value. Uncertain if both are possible.
- **Is Optional** - `isOptional: Boolean`, `false` by default - Whether this property may be omitted from the input and/or output.

##### Referenced from
- [Alternative Property Type](#alternative-property-type).`propId`
- [Possible Property Value](#possible-property-value).`propId`
