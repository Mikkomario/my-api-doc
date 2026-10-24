# My Api Doc
Version: **v0.1**  
Updated: 2026-10-15

## Table of Contents
- [Enumerations](#enumerations)
  - [Property Type](#property-type)
  - [Token Use Type](#token-use-type)
- [Packages & Classes](#packages-and-classes)
  - [Api](#api)
    - [Api Node](#api-node)
    - [Api Node Group](#api-node-group)
    - [Api Node Method Implementation](#api-node-method-implementation)
    - [Node Auth Scope Link](#node-auth-scope-link)
    - [Node Auth Type Link](#node-auth-type-link)
    - [Path Parameters Schema Link](#path-parameters-schema-link)
    - [Possible Response Status](#possible-response-status)
    - [Query Parameters Schema Link](#query-parameters-schema-link)
    - [Request Body Schema](#request-body-schema)
    - [Response Content Type](#response-content-type)
  - [Auth](#auth)
    - [Auth Token Type](#auth-token-type)
    - [Scope](#scope)
  - [Schema](#schema)
    - [Alternative Property Type](#alternative-property-type)
    - [Model Schema](#model-schema)
    - [Property Schema](#property-schema)

## Enumerations
Below are listed all enumerations introduced in My Api Doc, in alphabetical order  

### Property Type
An enumeration for different data types in input and output models

Key: `id: Int`  

**Values:**
- **String Prop** (1)
- **Int Prop** (2)
- **Decimal Prop** (3) - A property type for decimal numbers: Double and Float.
- **Long Prop** (4)
- **Boolean Prop** (5) - A boolean property type. Transmitted in JSON as `true` or `false`.
- **Instant Prop** (6) - A timestamp property type. Transmitted in JSON as ISO 8601 strings.
- **Duration Prop** (7) - A property for representing durations. Transmitted in JSON by coupling amount and unit, e.g. "48 seconds" or "2min"
- **Model Prop** (8) - A JSON object type. Usually coupled with a model type reference.

Utilized by the following 2 classes:
- [Alternative Property Type](#alternative-property-type)
- [Property Schema](#property-schema)

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
There are a total number of 3 packages and 15 classes

### Api
This package contains the following 10 classes: [Api Node](#api-node), [Api Node Group](#api-node-group), [Api Node Method Implementation](#api-node-method-implementation), [Node Auth Scope Link](#node-auth-scope-link), [Node Auth Type Link](#node-auth-type-link), [Path Parameters Schema Link](#path-parameters-schema-link), [Possible Response Status](#possible-response-status), [Query Parameters Schema Link](#query-parameters-schema-link), [Request Body Schema](#request-body-schema), [Response Content Type](#response-content-type)

#### Api Node
Represents an individual API node implementation

##### Details
- Uses 2 database **indices**: `name_no_spaces`, `relative_path`

##### Properties
Api Node contains the following 6 properties:
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

##### Referenced from
- [Api Node](#api-node).`parentId`
- [Api Node Method Implementation](#api-node-method-implementation).`nodeId`
- [Node Auth Scope Link](#node-auth-scope-link).`nodeId`
- [Node Auth Type Link](#node-auth-type-link).`nodeId`
- [Path Parameters Schema Link](#path-parameters-schema-link).`nodeId`

#### Api Node Group
Defines a group of API nodes

##### Details
- Uses **index**: `name`

##### Properties
Api Node Group contains the following 2 properties:
- **Name** - `name: String` - (Human readable) name of this node group
- **Description** - `description: String` - Description of this group's function or theme

##### Referenced from
- [Api Node](#api-node).`groupId`

#### Api Node Method Implementation
Describes how a specific HTTP method is implemented in an API node.

##### Details
- Uses **index**: `method_id`

##### Properties
Api Node Method Implementation contains the following 3 properties:
- **Node Id** - `nodeId: Int` - ID of the API node that contains this implementation.
  - Refers to [Api Node](#api-node)
- **Method** - `method: Method` - The implemented method.
- **Description** - `description: String` - A description of this implementation.

##### Referenced from
- [Possible Response Status](#possible-response-status).`implementationId`
- [Query Parameters Schema Link](#query-parameters-schema-link).`implementationId`
- [Request Body Schema](#request-body-schema).`implementationId`

#### Node Auth Scope Link
Describes an authorization scope required by an API node. 
If multiple scopes are linked, they typically represent alternative auth scopes, usually for different purposes.

##### Details
- Uses **index**: `method_id`

##### Properties
Node Auth Scope Link contains the following 4 properties:
- **Node Id** - `nodeId: Int` - ID of the node that requires the linked authorization scope.
  - Refers to [Api Node](#api-node)
- **Scope Id** - `scopeId: Int` - ID of the authorization scope required by the linked node.
  - Refers to [Scope](#scope)
- **Method** - `method: Option[Method]` - Method for which the linked authorization scope is required. None if the scope is required for all methods.
- **Also Required Id** - `alsoRequiredId: Option[Int]` - ID of another scope link that must also be met. None if no additional scopes are required.
  - Refers to [Node Auth Scope Link](#node-auth-scope-link)

##### Referenced from
- [Node Auth Scope Link](#node-auth-scope-link).`alsoRequiredId`

#### Node Auth Type Link

##### Details

##### Properties
Node Auth Type Link contains the following 2 properties:
- **Node Id** - `nodeId: Int` - ID of the node that accepts the linked authorization method.
  - Refers to [Api Node](#api-node)
- **Auth Type Id** - `authTypeId: Int` - ID of the authorization type accepted by the linked node.
  - Refers to [Auth Token Type](#auth-token-type)

#### Path Parameters Schema Link
Applies a specific schema to the path parameters received by an API node.

##### Details

##### Properties
Path Parameters Schema Link contains the following 2 properties:
- **Node Id** - `nodeId: Int` - ID of the API node that accepts these parameters.
  - Refers to [Api Node](#api-node)
- **Model Type Id** - `modelTypeId: Int` - ID of the model type that describes the received path parameters.
  - Refers to *model_type* from another module

#### Possible Response Status
Defines a possible response status for an API node method call.

##### Details
- Combines with [Response Content Type](#response-content-type), creating a **Possible Response**
- Uses **index**: `status_code`

##### Properties
Possible Response Status contains the following 3 properties:
- **Implementation Id** - `implementationId: Int` - ID of the implementation that may return this response status.
  - Refers to [Api Node Method Implementation](#api-node-method-implementation)
- **Status Code** - `statusCode: Int` - HTTP status code that may be returned by the API node method call.
- **Description** - `description: String` - Description of what this status signifies.

##### Referenced from
- [Response Content Type](#response-content-type).`statusId`

#### Query Parameters Schema Link
Applies a specific schema to the query parameters accepted by an API node.

##### Details

##### Properties
Query Parameters Schema Link contains the following 2 properties:
- **Implementation Id** - `implementationId: Int` - ID of the method implementation that uses the linked schema.
  - Refers to [Api Node Method Implementation](#api-node-method-implementation)
- **Model Type Id** - `modelTypeId: Int` - ID of the schema that defines the query parameters accepted by the linked implementation.
  - Refers to [Model Schema](#model-schema)

#### Request Body Schema
Applies a specific schema to the request body accepted by an API node. 
Multiple alternative schemas may be defined separately.

##### Details

##### Properties
Request Body Schema contains the following 4 properties:
- **Implementation Id** - `implementationId: Int` - ID of the method implementation that expects this type of request body.
  - Refers to [Api Node Method Implementation](#api-node-method-implementation)
- **Model Type Id** - `modelTypeId: Int` - ID of the schema that defines the request body accepted by the linked implementation.
  - Refers to [Model Schema](#model-schema)
- **Custom Content Type** - `customContentType: String` - Non-standard content type expected from the request body.
- **Is Array** - `isArray: Boolean` - Whether a JSON array is expected.

#### Response Content Type
Defines how an API node's response body is formed.

##### Details

##### Properties
Response Content Type contains the following 5 properties:
- **Status Id** - `statusId: Int` - ID of the status this type of response body is returned with.
  - Refers to [Possible Response Status](#possible-response-status)
- **Content Type** - `contentType: Option[PropertyType]` - If this body is non-empty, specifies the type of content it contains.
- **Model Type Id** - `modelTypeId: Option[Int]` - If this body contains a JSON object or objects, contains the ID of the model schema that describes the applicable object structure.
  - Refers to [Model Schema](#model-schema)
- **Is Array** - `isArray: Boolean` - Whether this response body contains an array of values. False if no more than one value may be returned.
- **Is Empty** - `isEmpty: Boolean` - Whether this response body is empty / no response body is returned.

### Auth
This package contains the following 2 classes: [Auth Token Type](#auth-token-type), [Scope](#scope)

#### Auth Token Type
Represents some (custom) token type used by the documented API.

##### Details
- Uses **index**: `name_no_spaces`

##### Properties
Auth Token Type contains the following 3 properties:
- **Name** - `name: String` - (Human-readable) name of this token type
- **Name No Spaces** - `nameNoSpaces: String` - A lowercase copy of this token's name with all whitespaces removed. Used for indexing and search.
- **Use Type** - `useType: TokenUseType` - Determines how this token is applied.

##### Referenced from
- [Node Auth Type Link](#node-auth-type-link).`authTypeId`

#### Scope
Represents an authorization scope in the documented API.

##### Details
- Uses **index**: `name`

##### Properties
Scope contains the following 1 properties:
- **Name** - `name: String` - Name / identifier of this scope.

##### Referenced from
- [Node Auth Scope Link](#node-auth-scope-link).`scopeId`

### Schema
This package contains the following 3 classes: [Alternative Property Type](#alternative-property-type), [Model Schema](#model-schema), [Property Schema](#property-schema)

#### Alternative Property Type
Defines an alternative supported data type for a property.

##### Details

##### Properties
Alternative Property Type contains the following 2 properties:
- **Prop Id** - `propId: Int` - ID of the property that has this alternative type
  - Refers to [Property Schema](#property-schema)
- **Alt Type** - `altType: PropertyType` - Alternative data type supported by the linked property

#### Model Schema
Defines properties in an input or output model / JSON object.

##### Details
- Combines with multiple [Property Schemas](#property-schema), creating a **Complete Model Schema**
- Uses **index**: `name`

##### Properties
Model Schema contains the following 2 properties:
- **Name** - `name: String` - Name of this model type (/class). Empty if unnamed. 
Named model types are often referenced from multiple sources, while unnamed types are typically single-use.
- **Description** - `description: String` - Description of this model type's role / function.

##### Referenced from
- [Property Schema](#property-schema).`modelTypeId`
- [Property Schema](#property-schema).`contentTypeId`
- [Query Parameters Schema Link](#query-parameters-schema-link).`modelTypeId`
- [Request Body Schema](#request-body-schema).`modelTypeId`
- [Response Content Type](#response-content-type).`modelTypeId`

#### Property Schema
Defines an individual property in a model schema.

##### Details
- Uses **index**: `name`

##### Properties
Property Schema contains the following 7 properties:
- **Model Type Id** - `modelTypeId: Int` - ID of the model type that this property belongs to.
  - Refers to [Model Schema](#model-schema)
- **Name** - `name: String` - Name of this property, as it should appear in the input or output model / JSON object.
- **Data Type** - `dataType: PropertyType` - This property's expected value type.
- **Content Type Id** - `contentTypeId: Option[Int]` - If this property contains a model/object, this is the ID of the contained model's type. 
None if this property uses some other data type.
  - Refers to [Model Schema](#model-schema)
- **Description** - `description: String` - A description of this property's role/function.
- **Is Array** - `isArray: Boolean`, `false` by default - Whether this property is an array of values. False if this property contains a single value.
- **Is Optional** - `isOptional: Boolean`, `false` by default - Whether this property may be omitted from the input and/or output.

##### Referenced from
- [Alternative Property Type](#alternative-property-type).`propId`
