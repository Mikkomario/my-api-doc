# My Api Doc
Version: **v0.1**  
Updated: 2026-10-27

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
    - [Api Node Method Implementation Model Schema Link](#api-node-method-implementation-model-schema-link)
    - [Body Schema](#body-schema)
    - [Node Auth Link](#node-auth-link)
    - [Node Auth Scope Link](#node-auth-scope-link)
    - [Node Auth Type Link](#node-auth-type-link)
    - [Path Parameters Schema Link](#path-parameters-schema-link)
    - [Possible Response Status](#possible-response-status)
    - [Query Parameters Schema Link](#query-parameters-schema-link)
    - [Request Body Schema](#request-body-schema)
    - [Response Body Schema](#response-body-schema)
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

**Values:**
- **Bearer** (1) - Represents a case where the token is used for bearer authentication
- **Basic** (2) - Represents the basic authentication use-case, which combines username and password.

Utilized by the following 1 classes:
- [Auth Token Type](#auth-token-type)

## Packages and Classes
Below are listed all classes introduced in My Api Doc, grouped by package and in alphabetical order.  
There are a total number of 3 packages and 21 classes

### Api
This package contains the following 15 classes: [Additional Node Auth Scope Requirement](#additional-node-auth-scope-requirement), [Api](#api), [Api Node](#api-node), [Api Node Group](#api-node-group), [Api Node Method Implementation](#api-node-method-implementation), [Api Node Method Implementation Model Schema Link](#api-node-method-implementation-model-schema-link), [Body Schema](#body-schema), [Node Auth Link](#node-auth-link), [Node Auth Scope Link](#node-auth-scope-link), [Node Auth Type Link](#node-auth-type-link), [Path Parameters Schema Link](#path-parameters-schema-link), [Possible Response Status](#possible-response-status), [Query Parameters Schema Link](#query-parameters-schema-link), [Request Body Schema](#request-body-schema), [Response Body Schema](#response-body-schema)

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
- [Model Schema](#model-schema).`apiId`
- [Scope](#scope).`apiId`

#### Api Node
Represents an individual API node implementation

##### Details
- Fully **versioned**
- Uses 4 database **indices**: `name_no_spaces`, `relative_path`, `created`, `deprecated_after`

##### Properties
Api Node contains the following 8 properties:
- **Group Id** - `groupId: Int` - ID of the node group this node belongs to
  - Refers to [Api Node Group](#api-node-group)
- **Name** - `name: String` - Human-readable name of this node
- **Name No Spaces** - `nameNoSpaces: String` - A lowercase version of the node name, with no whitespace included. Used for indexing & search.
- **Relative Path** - `relativePath: String` - Path of this node, relative to the parent node (if defined) or the API root (if no parent is defined). 
May contain path parameters.
- **Parent Id** - `parentId: Option[Int]` - ID of the node that appears directly above/before this node on the path. 
None if no parent node appears between this node and the API root.
  - Refers to [Api Node](#api-node)
- **Description** - `description: String` - A description of this API node.
- **Created** - `created: Instant` - Time when this api node was added to the database
- **Deprecated After** - `deprecatedAfter: Option[Instant]` - Time when this api node became deprecated. None while this api node is still valid.

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
- Combines with multiple [Api Nodes](#api-node), creating a **Complete Api Node Group**
- Fully **versioned**
- Uses 3 database **indices**: `name`, `created`, `deprecated_after`

##### Properties
Api Node Group contains the following 5 properties:
- **Api Id** - `apiId: Int` - ID of the API this node group belongs to
  - Refers to [Api](#api)
- **Name** - `name: String` - (Human readable) name of this node group
- **Description** - `description: String` - Description of this group's function or theme
- **Created** - `created: Instant` - Time when this api node group was added to the database
- **Deprecated After** - `deprecatedAfter: Option[Instant]` - Time when this api node group became deprecated. None while this api node group is still valid.

##### Referenced from
- [Api Node](#api-node).`groupId`

#### Api Node Method Implementation
Describes how a specific HTTP method is implemented in an API node.

##### Details
- Fully **versioned**
- Uses 3 database **indices**: `method`, `created`, `deprecated_after`

##### Properties
Api Node Method Implementation contains the following 6 properties:
- **Node Id** - `nodeId: Int` - ID of the API node that contains this implementation.
  - Refers to [Api Node](#api-node)
- **Method** - `method: Method` - The implemented method.
- **Description** - `description: String` - A description of this implementation.
- **Created** - `created: Instant` - Time when this api node method implementation was added to the database
- **Deprecated After** - `deprecatedAfter: Option[Instant]` - Time when this api node method implementation became deprecated. None while this api node method implementation is still valid.
- **Requires Request Body** - `requiresRequestBody: Boolean` - Whether a request body is required for this implementation.

##### Referenced from
- [Api Node Method Implementation Model Schema Link](#api-node-method-implementation-model-schema-link).`implementationId`
- [Possible Response Status](#possible-response-status).`implementationId`
- [Query Parameters Schema Link](#query-parameters-schema-link).`implementationId`
- [Request Body Schema](#request-body-schema).`implementationId`

#### Api Node Method Implementation Model Schema Link
Links a model schema to to an API node's method implementation.

##### Details
- Fully **versioned**
- Uses 2 database **indices**: `created`, `deprecated_after`

##### Properties
Api Node Method Implementation Model Schema Link contains the following 4 properties:
- **Implementation Id** - `implementationId: Int` - ID of the method implementation that uses the linked schema.
  - Refers to [Api Node Method Implementation](#api-node-method-implementation)
- **Model Type Id** - `modelTypeId: Int` - ID of the schema that defines the type of model accepted by the linked implementation.
  - Refers to [Model Schema](#model-schema)
- **Created** - `created: Instant` - Time when this api node method implementation model schema link was added to the database
- **Deprecated After** - `deprecatedAfter: Option[Instant]` - Time when this api node method implementation model schema link became deprecated. None while this api node method implementation model schema link is still valid.

#### Body Schema
Common trait for request & response body schemas.

##### Details
- Fully **versioned**
- Uses 3 database **indices**: `parent_id`, `created`, `deprecated_after`

##### Properties
Body Schema contains the following 6 properties:
- **Parent Id** - `parentId: Int` - ID of the element that contains this body.
- **Data Type** - `dataType: PropertyType` - Type of this body's contents.
- **Model Type Id** - `modelTypeId: Option[Int]` - If this body contains an object or objects, contains the ID of the model schema that describes the applicable object structure.
  - Refers to [Model Schema](#model-schema)
- **Created** - `created: Instant` - Time when this body schema was added to the database
- **Deprecated After** - `deprecatedAfter: Option[Instant]` - Time when this body schema became deprecated. None while this body schema is still valid.
- **Is Array** - `isArray: Boolean` - Whether this body contains an array of values. False if no more than one value may be included.

#### Node Auth Link
Common trait for links between API nodes and possible authorization requirements

##### Details
- Fully **versioned**
- Uses 4 database **indices**: `auth_id`, `method`, `created`, `deprecated_after`

##### Properties
Node Auth Link contains the following 6 properties:
- **Node Id** - `nodeId: Int` - ID of the node that accepts the linked authorization method.
  - Refers to [Api Node](#api-node)
- **Auth Id** - `authId: Int` - ID of the authorization element accepted by the linked node.
- **Method** - `method: Option[Method]` - The HTTP method that the linked authorization method is used for. 
None if the authorization method is used as a default for all methods (where not overridden).
- **Description** - `description: String` - A description concerning this authorization method's function in the linked node.
- **Created** - `created: Instant` - Time when this node auth link was added to the database
- **Deprecated After** - `deprecatedAfter: Option[Instant]` - Time when this node auth link became deprecated. None while this node auth link is still valid.

#### Node Auth Scope Link
Describes an authorization scope required by an API node. 
If multiple scopes are linked, they typically represent alternative auth scopes, usually for different purposes.

##### Details
- Fully **versioned**
- Uses 4 database **indices**: `scope_id`, `method`, `created`, `deprecated_after`

##### Properties
Node Auth Scope Link contains the following 6 properties:
- **Node Id** - `nodeId: Int` - ID of the node that accepts the linked authorization method.
  - Refers to [Api Node](#api-node)
- **Scope Id** - `scopeId: Int` - ID of the authorization scope required by the linked node.
  - Refers to [Scope](#scope)
- **Method** - `method: Option[Method]` - The HTTP method that the linked authorization method is used for. 
None if the authorization method is used as a default for all methods (where not overridden).
- **Description** - `description: String` - A description concerning this authorization method's function in the linked node.
- **Created** - `created: Instant` - Time when this node auth link was added to the database
- **Deprecated After** - `deprecatedAfter: Option[Instant]` - Time when this node auth link became deprecated. None while this node auth link is still valid.

##### Referenced from
- [Additional Node Auth Scope Requirement](#additional-node-auth-scope-requirement).`scopeLinkId`

#### Node Auth Type Link
Describes an authorization type accepted by an API node.

##### Details
- Fully **versioned**
- Uses 4 database **indices**: `auth_type_id`, `method`, `created`, `deprecated_after`

##### Properties
Node Auth Type Link contains the following 6 properties:
- **Node Id** - `nodeId: Int` - ID of the node that accepts the linked authorization method.
  - Refers to [Api Node](#api-node)
- **Auth Type Id** - `authTypeId: Int` - ID of the authorization type accepted by the linked node.
  - Refers to [Auth Token Type](#auth-token-type)
- **Method** - `method: Option[Method]` - The HTTP method that the linked authorization method is used for. 
None if the authorization method is used as a default for all methods (where not overridden).
- **Description** - `description: String` - A description concerning this authorization method's function in the linked node.
- **Created** - `created: Instant` - Time when this node auth link was added to the database
- **Deprecated After** - `deprecatedAfter: Option[Instant]` - Time when this node auth link became deprecated. None while this node auth link is still valid.

#### Path Parameters Schema Link
Applies a specific schema to the path parameters received by an API node.

##### Details
- Fully **versioned**
- Uses 2 database **indices**: `created`, `deprecated_after`

##### Properties
Path Parameters Schema Link contains the following 4 properties:
- **Node Id** - `nodeId: Int` - ID of the API node that accepts these parameters.
  - Refers to [Api Node](#api-node)
- **Model Type Id** - `modelTypeId: Int` - ID of the model type that describes the received path parameters.
  - Refers to [Model Schema](#model-schema)
- **Created** - `created: Instant` - Time when this path parameters schema link was added to the database
- **Deprecated After** - `deprecatedAfter: Option[Instant]` - Time when this path parameters schema link became deprecated. None while this path parameters schema link is still valid.

#### Possible Response Status
Defines a possible response status for an API node method call.

##### Details
- Fully **versioned**
- Uses 3 database **indices**: `status_code`, `created`, `deprecated_after`

##### Properties
Possible Response Status contains the following 6 properties:
- **Implementation Id** - `implementationId: Int` - ID of the implementation that may return this response status.
  - Refers to [Api Node Method Implementation](#api-node-method-implementation)
- **Status Code** - `statusCode: Int` - HTTP status code that may be returned by the API node method call.
- **Description** - `description: String` - Description of what this status signifies.
- **Created** - `created: Instant` - Time when this possible response status was added to the database
- **Deprecated After** - `deprecatedAfter: Option[Instant]` - Time when this possible response status became deprecated. None while this possible response status is still valid.
- **Has Body** - `hasBody: UncertainBoolean` - Whether a response body is returned with this status. Uncertain if not specified in the documentation.

##### Referenced from
- [Response Body Schema](#response-body-schema).`statusId`

#### Query Parameters Schema Link
Applies a specific schema to the query parameters accepted by an API node.

##### Details
- Fully **versioned**
- Uses 2 database **indices**: `created`, `deprecated_after`

##### Properties
Query Parameters Schema Link contains the following 4 properties:
- **Implementation Id** - `implementationId: Int` - ID of the method implementation that uses the linked schema.
  - Refers to [Api Node Method Implementation](#api-node-method-implementation)
- **Model Type Id** - `modelTypeId: Int` - ID of the schema that defines the type of model accepted by the linked implementation.
  - Refers to [Model Schema](#model-schema)
- **Created** - `created: Instant` - Time when this api node method implementation model schema link was added to the database
- **Deprecated After** - `deprecatedAfter: Option[Instant]` - Time when this api node method implementation model schema link became deprecated. None while this api node method implementation model schema link is still valid.

#### Request Body Schema
Applies a specific schema to the request body accepted by an API node. 
Multiple alternative schemas may be defined separately.

##### Details
- Fully **versioned**
- Uses 3 database **indices**: `implementation_id`, `created`, `deprecated_after`

##### Properties
Request Body Schema contains the following 8 properties:
- **Implementation Id** - `implementationId: Int` - ID of the method implementation that receives this request body.
  - Refers to [Api Node Method Implementation](#api-node-method-implementation)
- **Data Type** - `dataType: PropertyType` - Type of this body's contents.
- **Model Type Id** - `modelTypeId: Option[Int]` - If this body contains an object or objects, contains the ID of the model schema that describes the applicable object structure.
  - Refers to [Model Schema](#model-schema)
- **Created** - `created: Instant` - Time when this body schema was added to the database
- **Deprecated After** - `deprecatedAfter: Option[Instant]` - Time when this body schema became deprecated. None while this body schema is still valid.
- **Is Array** - `isArray: Boolean` - Whether this body contains an array of values. False if no more than one value may be included.
- **Custom Content Type** - `customContentType: String` - Non-standard content type expected from the request body, if applicable.
- **Description** - `description: String` - Description of the expected request body.

#### Response Body Schema
Defines how an API node's response body is formed.

##### Details
- Fully **versioned**
- Uses 3 database **indices**: `status_id`, `created`, `deprecated_after`

##### Properties
Response Body Schema contains the following 6 properties:
- **Status Id** - `statusId: Int` - ID of the status this type of response body is returned with.
  - Refers to [Possible Response Status](#possible-response-status)
- **Data Type** - `dataType: PropertyType` - Type of this body's contents.
- **Model Type Id** - `modelTypeId: Option[Int]` - If this body contains an object or objects, contains the ID of the model schema that describes the applicable object structure.
  - Refers to [Model Schema](#model-schema)
- **Created** - `created: Instant` - Time when this body schema was added to the database
- **Deprecated After** - `deprecatedAfter: Option[Instant]` - Time when this body schema became deprecated. None while this body schema is still valid.
- **Is Array** - `isArray: Boolean` - Whether this body contains an array of values. False if no more than one value may be included.

### Auth
This package contains the following 2 classes: [Auth Token Type](#auth-token-type), [Scope](#scope)

#### Auth Token Type
Represents some (custom) token type used by the documented API.

##### Details
- Fully **versioned**
- Uses 3 database **indices**: `name_no_spaces`, `created`, `deprecated_after`

##### Properties
Auth Token Type contains the following 6 properties:
- **Api Id** - `apiId: Int` - ID of the API where this token type is used.
  - Refers to [Api](#api)
- **Name** - `name: String` - (Human-readable) name of this token type
- **Name No Spaces** - `nameNoSpaces: String` - A lowercase copy of this token's name with all whitespaces removed. Used for indexing and search.
- **Use Type** - `useType: TokenUseType` - Determines how this token is applied.
- **Created** - `created: Instant` - Time when this auth token type was added to the database
- **Deprecated After** - `deprecatedAfter: Option[Instant]` - Time when this auth token type became deprecated. None while this auth token type is still valid.

##### Referenced from
- [Node Auth Type Link](#node-auth-type-link).`authTypeId`

#### Scope
Represents an authorization scope in the documented API.

##### Details
- Fully **versioned**
- Uses 3 database **indices**: `name`, `created`, `deprecated_after`

##### Properties
Scope contains the following 4 properties:
- **Api Id** - `apiId: Int` - ID of the API where this scope is used.
  - Refers to [Api](#api)
- **Name** - `name: String` - Name / identifier of this scope.
- **Created** - `created: Instant` - Time when this scope was added to the database
- **Deprecated After** - `deprecatedAfter: Option[Instant]` - Time when this scope became deprecated. None while this scope is still valid.

##### Referenced from
- [Additional Node Auth Scope Requirement](#additional-node-auth-scope-requirement).`scopeId`
- [Node Auth Scope Link](#node-auth-scope-link).`scopeId`

### Schema
This package contains the following 4 classes: [Alternative Property Type](#alternative-property-type), [Model Schema](#model-schema), [Possible Property Value](#possible-property-value), [Property Schema](#property-schema)

#### Alternative Property Type
Defines an alternative supported data type for a property.

##### Details
- Fully **versioned**
- Uses 2 database **indices**: `created`, `deprecated_after`

##### Properties
Alternative Property Type contains the following 4 properties:
- **Prop Id** - `propId: Int` - ID of the property that has this alternative type
  - Refers to [Property Schema](#property-schema)
- **Alt Type** - `altType: PropertyType` - Alternative data type supported by the linked property
- **Created** - `created: Instant` - Time when this alternative property type was added to the database
- **Deprecated After** - `deprecatedAfter: Option[Instant]` - Time when this alternative property type became deprecated. None while this alternative property type is still valid.

#### Model Schema
Defines properties in an input or output model / JSON object.

##### Details
- Combines with multiple [Property Schemas](#property-schema), creating a **Complete Model Schema**
- Fully **versioned**
- Uses 3 database **indices**: `name`, `created`, `deprecated_after`

##### Properties
Model Schema contains the following 5 properties:
- **Api Id** - `apiId: Int` - ID of the API this model type belongs to.
  - Refers to [Api](#api)
- **Name** - `name: String` - Name of this model type (/class). Empty if unnamed. 
Named model types are often referenced from multiple sources, while unnamed types are typically single-use.
- **Description** - `description: String` - Description of this model type's role / function.
- **Created** - `created: Instant` - Time when this model schema was added to the database
- **Deprecated After** - `deprecatedAfter: Option[Instant]` - Time when this model schema became deprecated. None while this model schema is still valid.

##### Referenced from
- [Api Node Method Implementation Model Schema Link](#api-node-method-implementation-model-schema-link).`modelTypeId`
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
- Fully **versioned**
- Uses 2 database **indices**: `created`, `deprecated_after`

##### Properties
Possible Property Value contains the following 5 properties:
- **Prop Id** - `propId: Int` - ID of the property that this value may be assigned to
  - Refers to [Property Schema](#property-schema)
- **Value** - `value: String` - The value the linked property may have
- **Description** - `description: String` - Description of this value's role / function
- **Created** - `created: Instant` - Time when this possible property value was added to the database
- **Deprecated After** - `deprecatedAfter: Option[Instant]` - Time when this possible property value became deprecated. None while this possible property value is still valid.

#### Property Schema
Defines an individual property in a model schema.

##### Details
- Fully **versioned**
- Uses 3 database **indices**: `name`, `created`, `deprecated_after`

##### Properties
Property Schema contains the following 9 properties:
- **Model Type Id** - `modelTypeId: Int` - ID of the model type that this property belongs to.
  - Refers to [Model Schema](#model-schema)
- **Name** - `name: String` - Name of this property, as it should appear in the input or output model / JSON object.
- **Data Type** - `dataType: PropertyType` - This property's expected value type.
- **Content Type Id** - `contentTypeId: Option[Int]` - If this property contains a model/object, this is the ID of the contained model's type. 
None if this property uses some other data type.
  - Refers to [Model Schema](#model-schema)
- **Description** - `description: String` - A description of this property's role/function.
- **Created** - `created: Instant` - Time when this property schema was added to the database
- **Deprecated After** - `deprecatedAfter: Option[Instant]` - Time when this property schema became deprecated. None while this property schema is still valid.
- **Is Array** - `isArray: UncertainBoolean`, `false` by default - Whether this property is an array of values. 
False if this property contains a single value. Uncertain if both are possible.
- **Is Optional** - `isOptional: Boolean`, `false` by default - Whether this property may be omitted from the input and/or output.

##### Referenced from
- [Alternative Property Type](#alternative-property-type).`propId`
- [Possible Property Value](#possible-property-value).`propId`
