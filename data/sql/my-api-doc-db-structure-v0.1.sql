-- 
-- Database structure for my api doc models
-- Version: v0.1
-- Last generated: 2026-10-28
--

CREATE DATABASE IF NOT EXISTS `my_api_doc_db` 
	DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
USE `my_api_doc_db`;

--	Api	----------

-- Represents a documented API
-- name:    (Human readable) name of this API
-- created: Time when this api was added to the database
CREATE TABLE `api`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`name` VARCHAR(48) NOT NULL, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	INDEX a_name_idx (`name`), 
	INDEX a_created_idx (`created`)
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents an API documentation update
-- api_id:  ID of the updated API
-- created: Time when this doc update was added to the database
CREATE TABLE `doc_update`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`api_id` INT NOT NULL, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	INDEX du_created_idx (`created`), 
	CONSTRAINT du_a_api_ref_fk FOREIGN KEY du_a_api_ref_idx (api_id) REFERENCES `api`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Defines a group of API nodes
-- original_update_id: ID of the update that introduced this element (version)
-- api_id:             ID of the API this node group belongs to
-- name:               (Human readable) name of this node group
-- removing_update_id: ID of the update that removed or replaced this element (version)
-- description:        Description of this group's function or theme
CREATE TABLE `api_node_group`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`original_update_id` INT NOT NULL, 
	`api_id` INT NOT NULL, 
	`name` VARCHAR(48) NOT NULL, 
	`removing_update_id` INT, 
	`description` VARCHAR(255), 
	INDEX ang_name_idx (`name`), 
	CONSTRAINT ang_du_original_update_ref_fk FOREIGN KEY ang_du_original_update_ref_idx (original_update_id) REFERENCES `doc_update`(`id`) ON DELETE CASCADE, 
	CONSTRAINT ang_a_api_ref_fk FOREIGN KEY ang_a_api_ref_idx (api_id) REFERENCES `api`(`id`) ON DELETE CASCADE, 
	CONSTRAINT ang_du_removing_update_ref_fk FOREIGN KEY ang_du_removing_update_ref_idx (removing_update_id) REFERENCES `doc_update`(`id`) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents an individual API node implementation
-- original_update_id: ID of the update that introduced this element (version)
-- group_id:           ID of the node group this node belongs to
-- name:               Human-readable name of this node
-- name_no_spaces:     A lowercase version of the node name, with no whitespace included. Used for indexing & search.
-- relative_path:      Path of this node, relative to the parent node (if defined) or the API root (if no parent is defined). 
-- 		May contain path parameters.
-- removing_update_id: ID of the update that removed or replaced this element (version)
-- parent_id:          ID of the node that appears directly above/before this node on the path. 
-- 		None if no parent node appears between this node and the API root.
-- description:        A description of this API node.
CREATE TABLE `api_node`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`original_update_id` INT NOT NULL, 
	`group_id` INT NOT NULL, 
	`name` VARCHAR(48) NOT NULL, 
	`name_no_spaces` VARCHAR(48) NOT NULL, 
	`relative_path` VARCHAR(48) NOT NULL, 
	`removing_update_id` INT, 
	`parent_id` INT, 
	`description` VARCHAR(255), 
	INDEX an_name_no_spaces_idx (`name_no_spaces`), 
	INDEX an_relative_path_idx (`relative_path`), 
	CONSTRAINT an_du_original_update_ref_fk FOREIGN KEY an_du_original_update_ref_idx (original_update_id) REFERENCES `doc_update`(`id`) ON DELETE CASCADE, 
	CONSTRAINT an_ang_group_ref_fk FOREIGN KEY an_ang_group_ref_idx (group_id) REFERENCES `api_node_group`(`id`) ON DELETE CASCADE, 
	CONSTRAINT an_du_removing_update_ref_fk FOREIGN KEY an_du_removing_update_ref_idx (removing_update_id) REFERENCES `doc_update`(`id`) ON DELETE SET NULL, 
	CONSTRAINT an_an_parent_ref_fk FOREIGN KEY an_an_parent_ref_idx (parent_id) REFERENCES `api_node`(`id`) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Describes how a specific HTTP method is implemented in an API node.
-- original_update_id:    ID of the update that introduced this element (version)
-- node_id:               ID of the API node that contains this implementation.
-- removing_update_id:    ID of the update that removed or replaced this element (version)
-- method:                The implemented method.
-- description:           A description of this implementation.
-- requires_request_body: Whether a request body is required for this implementation.
CREATE TABLE `api_node_method_implementation`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`original_update_id` INT NOT NULL, 
	`node_id` INT NOT NULL, 
	`removing_update_id` INT, 
	`method` VARCHAR(8) NOT NULL DEFAULT 'GET', 
	`description` VARCHAR(255), 
	`requires_request_body` BOOLEAN NOT NULL DEFAULT FALSE, 
	INDEX anmi_method_idx (`method`), 
	CONSTRAINT anmi_du_original_update_ref_fk FOREIGN KEY anmi_du_original_update_ref_idx (original_update_id) REFERENCES `doc_update`(`id`) ON DELETE CASCADE, 
	CONSTRAINT anmi_an_node_ref_fk FOREIGN KEY anmi_an_node_ref_idx (node_id) REFERENCES `api_node`(`id`) ON DELETE CASCADE, 
	CONSTRAINT anmi_du_removing_update_ref_fk FOREIGN KEY anmi_du_removing_update_ref_idx (removing_update_id) REFERENCES `doc_update`(`id`) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Defines a possible response status for an API node method call.
-- original_update_id: ID of the update that introduced this element (version)
-- implementation_id:  ID of the implementation that may return this response status.
-- status_code:        HTTP status code that may be returned by the API node method call.
-- removing_update_id: ID of the update that removed or replaced this element (version)
-- description:        Description of what this status signifies.
-- has_body:           Whether a response body is returned with this status. Uncertain if not specified in the documentation.
CREATE TABLE `possible_response_status`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`original_update_id` INT NOT NULL, 
	`implementation_id` INT NOT NULL, 
	`status_code` SMALLINT(3) NOT NULL, 
	`removing_update_id` INT, 
	`description` VARCHAR(96), 
	`has_body` BOOLEAN, 
	INDEX prs_status_code_idx (`status_code`), 
	CONSTRAINT prs_du_original_update_ref_fk FOREIGN KEY prs_du_original_update_ref_idx (original_update_id) REFERENCES `doc_update`(`id`) ON DELETE CASCADE, 
	CONSTRAINT prs_anmi_implementation_ref_fk FOREIGN KEY prs_anmi_implementation_ref_idx (implementation_id) REFERENCES `api_node_method_implementation`(`id`) ON DELETE CASCADE, 
	CONSTRAINT prs_du_removing_update_ref_fk FOREIGN KEY prs_du_removing_update_ref_idx (removing_update_id) REFERENCES `doc_update`(`id`) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;


--	Schema	----------

-- Defines properties in an input or output model / JSON object.
-- original_update_id: ID of the update that introduced this element (version)
-- api_id:             ID of the API this model type belongs to.
-- removing_update_id: ID of the update that removed or replaced this element (version)
-- name:               Name of this model type (/class). Empty if unnamed. 
-- 		Named model types are often referenced from multiple sources, while unnamed types are typically single-use.
-- description:        Description of this model type's role / function.
CREATE TABLE `model_schema`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`original_update_id` INT NOT NULL, 
	`api_id` INT NOT NULL, 
	`removing_update_id` INT, 
	`name` VARCHAR(64), 
	`description` VARCHAR(255), 
	INDEX ms_name_idx (`name`), 
	CONSTRAINT ms_du_original_update_ref_fk FOREIGN KEY ms_du_original_update_ref_idx (original_update_id) REFERENCES `doc_update`(`id`) ON DELETE CASCADE, 
	CONSTRAINT ms_a_api_ref_fk FOREIGN KEY ms_a_api_ref_idx (api_id) REFERENCES `api`(`id`) ON DELETE CASCADE, 
	CONSTRAINT ms_du_removing_update_ref_fk FOREIGN KEY ms_du_removing_update_ref_idx (removing_update_id) REFERENCES `doc_update`(`id`) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Defines an individual property in a model schema.
-- original_update_id: ID of the update that introduced this element (version)
-- model_type_id:      ID of the model type that this property belongs to.
-- removing_update_id: ID of the update that removed or replaced this element (version)
-- name:               Name of this property, as it should appear in the input or output model / JSON object.
-- data_type_id:       This property's expected value type.
-- 		References enumeration PropertyType
-- 		Possible values are: 1 = any prop, 2 = string prop, 3 = int prop, 4 = decimal prop, 5 = long prop, 6 = boolean prop, 7 = instant prop, 8 = duration prop, 9 = model prop
-- content_type_id:    If this property contains a model/object, this is the ID of the contained model's type. 
-- 		None if this property uses some other data type.
-- description:        A description of this property's role/function.
-- is_array:           Whether this property is an array of values. 
-- 		False if this property contains a single value. Uncertain if both are possible.
-- is_optional:        Whether this property may be omitted from the input and/or output.
CREATE TABLE `property_schema`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`original_update_id` INT NOT NULL, 
	`model_type_id` INT NOT NULL, 
	`removing_update_id` INT, 
	`name` VARCHAR(64), 
	`data_type_id` TINYINT NOT NULL, 
	`content_type_id` INT, 
	`description` VARCHAR(255), 
	`is_array` BOOLEAN DEFAULT FALSE, 
	`is_optional` BOOLEAN NOT NULL DEFAULT FALSE, 
	INDEX ps_name_idx (`name`), 
	CONSTRAINT ps_du_original_update_ref_fk FOREIGN KEY ps_du_original_update_ref_idx (original_update_id) REFERENCES `doc_update`(`id`) ON DELETE CASCADE, 
	CONSTRAINT ps_ms_model_type_ref_fk FOREIGN KEY ps_ms_model_type_ref_idx (model_type_id) REFERENCES `model_schema`(`id`) ON DELETE CASCADE, 
	CONSTRAINT ps_du_removing_update_ref_fk FOREIGN KEY ps_du_removing_update_ref_idx (removing_update_id) REFERENCES `doc_update`(`id`) ON DELETE SET NULL, 
	CONSTRAINT ps_ms_content_type_ref_fk FOREIGN KEY ps_ms_content_type_ref_idx (content_type_id) REFERENCES `model_schema`(`id`) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Defines an alternative supported data type for a property.
-- original_update_id: ID of the update that introduced this element (version)
-- prop_id:            ID of the property that has this alternative type
-- removing_update_id: ID of the update that removed or replaced this element (version)
-- alt_type_id:        Alternative data type supported by the linked property
-- 		References enumeration PropertyType
-- 		Possible values are: 1 = any prop, 2 = string prop, 3 = int prop, 4 = decimal prop, 5 = long prop, 6 = boolean prop, 7 = instant prop, 8 = duration prop, 9 = model prop
CREATE TABLE `alternative_property_type`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`original_update_id` INT NOT NULL, 
	`prop_id` INT NOT NULL, 
	`removing_update_id` INT, 
	`alt_type_id` TINYINT NOT NULL, 
	CONSTRAINT apt_du_original_update_ref_fk FOREIGN KEY apt_du_original_update_ref_idx (original_update_id) REFERENCES `doc_update`(`id`) ON DELETE CASCADE, 
	CONSTRAINT apt_ps_prop_ref_fk FOREIGN KEY apt_ps_prop_ref_idx (prop_id) REFERENCES `property_schema`(`id`) ON DELETE CASCADE, 
	CONSTRAINT apt_du_removing_update_ref_fk FOREIGN KEY apt_du_removing_update_ref_idx (removing_update_id) REFERENCES `doc_update`(`id`) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Defines a possible value for an enum property
-- original_update_id: ID of the update that introduced this element (version)
-- prop_id:            ID of the property that this value may be assigned to
-- value:              The value the linked property may have
-- removing_update_id: ID of the update that removed or replaced this element (version)
-- description:        Description of this value's role / function
CREATE TABLE `possible_property_value`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`original_update_id` INT NOT NULL, 
	`prop_id` INT NOT NULL, 
	`value` VARCHAR(24) NOT NULL, 
	`removing_update_id` INT, 
	`description` VARCHAR(255), 
	CONSTRAINT ppv_du_original_update_ref_fk FOREIGN KEY ppv_du_original_update_ref_idx (original_update_id) REFERENCES `doc_update`(`id`) ON DELETE CASCADE, 
	CONSTRAINT ppv_ps_prop_ref_fk FOREIGN KEY ppv_ps_prop_ref_idx (prop_id) REFERENCES `property_schema`(`id`) ON DELETE CASCADE, 
	CONSTRAINT ppv_du_removing_update_ref_fk FOREIGN KEY ppv_du_removing_update_ref_idx (removing_update_id) REFERENCES `doc_update`(`id`) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;


--	Auth	----------

-- Represents some (custom) token type used by the documented API.
-- original_update_id: ID of the update that introduced this element (version)
-- api_id:             ID of the API where this token type is used.
-- name:               (Human-readable) name of this token type
-- name_no_spaces:     A lowercase copy of this token's name with all whitespaces removed. Used for indexing and search.
-- removing_update_id: ID of the update that removed or replaced this element (version)
-- use_type_id:        Determines how this token is applied.
-- 		References enumeration TokenUseType
-- 		Possible values are: 1 = bearer, 2 = basic
CREATE TABLE `auth_token_type`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`original_update_id` INT NOT NULL, 
	`api_id` INT NOT NULL, 
	`name` VARCHAR(48) NOT NULL, 
	`name_no_spaces` VARCHAR(48) NOT NULL, 
	`removing_update_id` INT, 
	`use_type_id` TINYINT NOT NULL, 
	INDEX att_name_no_spaces_idx (`name_no_spaces`), 
	CONSTRAINT att_du_original_update_ref_fk FOREIGN KEY att_du_original_update_ref_idx (original_update_id) REFERENCES `doc_update`(`id`) ON DELETE CASCADE, 
	CONSTRAINT att_a_api_ref_fk FOREIGN KEY att_a_api_ref_idx (api_id) REFERENCES `api`(`id`) ON DELETE CASCADE, 
	CONSTRAINT att_du_removing_update_ref_fk FOREIGN KEY att_du_removing_update_ref_idx (removing_update_id) REFERENCES `doc_update`(`id`) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents an authorization scope in the documented API.
-- original_update_id: ID of the update that introduced this element (version)
-- api_id:             ID of the API where this scope is used.
-- name:               Name / identifier of this scope.
-- removing_update_id: ID of the update that removed or replaced this element (version)
CREATE TABLE `scope`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`original_update_id` INT NOT NULL, 
	`api_id` INT NOT NULL, 
	`name` VARCHAR(48) NOT NULL, 
	`removing_update_id` INT, 
	INDEX s_name_idx (`name`), 
	CONSTRAINT s_du_original_update_ref_fk FOREIGN KEY s_du_original_update_ref_idx (original_update_id) REFERENCES `doc_update`(`id`) ON DELETE CASCADE, 
	CONSTRAINT s_a_api_ref_fk FOREIGN KEY s_a_api_ref_idx (api_id) REFERENCES `api`(`id`) ON DELETE CASCADE, 
	CONSTRAINT s_du_removing_update_ref_fk FOREIGN KEY s_du_removing_update_ref_idx (removing_update_id) REFERENCES `doc_update`(`id`) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;


--	Api	----------

-- Describes an authorization scope required by an API node. 
-- 		If multiple scopes are linked, they typically represent alternative auth scopes, usually for different purposes.
-- original_update_id: ID of the update that introduced this element (version)
-- node_id:            ID of the node that accepts the linked authorization method.
-- scope_id:           ID of the authorization scope required by the linked node.
-- removing_update_id: ID of the update that removed or replaced this element (version)
-- method:             The HTTP method that the linked authorization method is used for. 
-- 		None if the authorization method is used as a default for all methods (where not overridden).
-- description:        A description concerning this authorization method's function in the linked node.
CREATE TABLE `node_auth_scope_link`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`original_update_id` INT NOT NULL, 
	`node_id` INT NOT NULL, 
	`scope_id` INT NOT NULL, 
	`removing_update_id` INT, 
	`method` VARCHAR(8) DEFAULT 'GET', 
	`description` VARCHAR(255), 
	INDEX nasl_scope_id_idx (`scope_id`), 
	INDEX nasl_method_idx (`method`), 
	CONSTRAINT nasl_du_original_update_ref_fk FOREIGN KEY nasl_du_original_update_ref_idx (original_update_id) REFERENCES `doc_update`(`id`) ON DELETE CASCADE, 
	CONSTRAINT nasl_an_node_ref_fk FOREIGN KEY nasl_an_node_ref_idx (node_id) REFERENCES `api_node`(`id`) ON DELETE CASCADE, 
	CONSTRAINT nasl_s_scope_ref_fk FOREIGN KEY nasl_s_scope_ref_idx (scope_id) REFERENCES `scope`(`id`) ON DELETE CASCADE, 
	CONSTRAINT nasl_du_removing_update_ref_fk FOREIGN KEY nasl_du_removing_update_ref_idx (removing_update_id) REFERENCES `doc_update`(`id`) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Describes an authorization type accepted by an API node.
-- original_update_id: ID of the update that introduced this element (version)
-- node_id:            ID of the node that accepts the linked authorization method.
-- auth_type_id:       ID of the authorization type accepted by the linked node.
-- removing_update_id: ID of the update that removed or replaced this element (version)
-- method:             The HTTP method that the linked authorization method is used for. 
-- 		None if the authorization method is used as a default for all methods (where not overridden).
-- description:        A description concerning this authorization method's function in the linked node.
CREATE TABLE `node_auth_type_link`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`original_update_id` INT NOT NULL, 
	`node_id` INT NOT NULL, 
	`auth_type_id` INT NOT NULL, 
	`removing_update_id` INT, 
	`method` VARCHAR(8) DEFAULT 'GET', 
	`description` VARCHAR(255), 
	INDEX natl_auth_type_id_idx (`auth_type_id`), 
	INDEX natl_method_idx (`method`), 
	CONSTRAINT natl_du_original_update_ref_fk FOREIGN KEY natl_du_original_update_ref_idx (original_update_id) REFERENCES `doc_update`(`id`) ON DELETE CASCADE, 
	CONSTRAINT natl_an_node_ref_fk FOREIGN KEY natl_an_node_ref_idx (node_id) REFERENCES `api_node`(`id`) ON DELETE CASCADE, 
	CONSTRAINT natl_att_auth_type_ref_fk FOREIGN KEY natl_att_auth_type_ref_idx (auth_type_id) REFERENCES `auth_token_type`(`id`) ON DELETE CASCADE, 
	CONSTRAINT natl_du_removing_update_ref_fk FOREIGN KEY natl_du_removing_update_ref_idx (removing_update_id) REFERENCES `doc_update`(`id`) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Applies a specific schema to the path parameters received by an API node.
-- original_update_id: ID of the update that introduced this element (version)
-- node_id:            ID of the API node that accepts these parameters.
-- model_type_id:      ID of the model type that describes the received path parameters.
-- removing_update_id: ID of the update that removed or replaced this element (version)
CREATE TABLE `path_parameters_schema_link`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`original_update_id` INT NOT NULL, 
	`node_id` INT NOT NULL, 
	`model_type_id` INT NOT NULL, 
	`removing_update_id` INT, 
	CONSTRAINT ppsl_du_original_update_ref_fk FOREIGN KEY ppsl_du_original_update_ref_idx (original_update_id) REFERENCES `doc_update`(`id`) ON DELETE CASCADE, 
	CONSTRAINT ppsl_an_node_ref_fk FOREIGN KEY ppsl_an_node_ref_idx (node_id) REFERENCES `api_node`(`id`) ON DELETE CASCADE, 
	CONSTRAINT ppsl_ms_model_type_ref_fk FOREIGN KEY ppsl_ms_model_type_ref_idx (model_type_id) REFERENCES `model_schema`(`id`) ON DELETE CASCADE, 
	CONSTRAINT ppsl_du_removing_update_ref_fk FOREIGN KEY ppsl_du_removing_update_ref_idx (removing_update_id) REFERENCES `doc_update`(`id`) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Applies a specific schema to the query parameters accepted by an API node.
-- original_update_id: ID of the update that introduced this element (version)
-- implementation_id:  ID of the method implementation that receives the linked parameters.
-- model_type_id:      ID of the schema that defines the accepted path parameters.
-- removing_update_id: ID of the update that removed or replaced this element (version)
CREATE TABLE `query_parameters_schema_link`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`original_update_id` INT NOT NULL, 
	`implementation_id` INT NOT NULL, 
	`model_type_id` INT NOT NULL, 
	`removing_update_id` INT, 
	CONSTRAINT qpsl_du_original_update_ref_fk FOREIGN KEY qpsl_du_original_update_ref_idx (original_update_id) REFERENCES `doc_update`(`id`) ON DELETE CASCADE, 
	CONSTRAINT qpsl_anmi_implementation_ref_fk FOREIGN KEY qpsl_anmi_implementation_ref_idx (implementation_id) REFERENCES `api_node_method_implementation`(`id`) ON DELETE CASCADE, 
	CONSTRAINT qpsl_ms_model_type_ref_fk FOREIGN KEY qpsl_ms_model_type_ref_idx (model_type_id) REFERENCES `model_schema`(`id`) ON DELETE CASCADE, 
	CONSTRAINT qpsl_du_removing_update_ref_fk FOREIGN KEY qpsl_du_removing_update_ref_idx (removing_update_id) REFERENCES `doc_update`(`id`) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Applies a specific schema to the request body accepted by an API node. 
-- 		Multiple alternative schemas may be defined separately.
-- original_update_id:  ID of the update that introduced this element (version)
-- implementation_id:   ID of the method implementation that receives this request body.
-- removing_update_id:  ID of the update that removed or replaced this element (version)
-- data_type_id:        Type of this body's contents.
-- 		References enumeration PropertyType
-- 		Possible values are: 1 = any prop, 2 = string prop, 3 = int prop, 4 = decimal prop, 5 = long prop, 6 = boolean prop, 7 = instant prop, 8 = duration prop, 9 = model prop
-- model_type_id:       If this body contains an object or objects, contains the ID of the model schema that describes the applicable object structure.
-- is_array:            Whether this body contains an array of values. False if no more than one value may be included.
-- custom_content_type: Non-standard content type expected from the request body, if applicable.
-- description:         Description of the expected request body.
CREATE TABLE `request_body_schema`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`original_update_id` INT NOT NULL, 
	`implementation_id` INT NOT NULL, 
	`removing_update_id` INT, 
	`data_type_id` TINYINT NOT NULL, 
	`model_type_id` INT, 
	`is_array` BOOLEAN NOT NULL DEFAULT FALSE, 
	`custom_content_type` VARCHAR(32), 
	`description` VARCHAR(255), 
	INDEX rqbs_implementation_id_idx (`implementation_id`), 
	CONSTRAINT rqbs_du_original_update_ref_fk FOREIGN KEY rqbs_du_original_update_ref_idx (original_update_id) REFERENCES `doc_update`(`id`) ON DELETE CASCADE, 
	CONSTRAINT rqbs_anmi_implementation_ref_fk FOREIGN KEY rqbs_anmi_implementation_ref_idx (implementation_id) REFERENCES `api_node_method_implementation`(`id`) ON DELETE CASCADE, 
	CONSTRAINT rqbs_du_removing_update_ref_fk FOREIGN KEY rqbs_du_removing_update_ref_idx (removing_update_id) REFERENCES `doc_update`(`id`) ON DELETE SET NULL, 
	CONSTRAINT rqbs_ms_model_type_ref_fk FOREIGN KEY rqbs_ms_model_type_ref_idx (model_type_id) REFERENCES `model_schema`(`id`) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Defines how an API node's response body is formed.
-- original_update_id: ID of the update that introduced this element (version)
-- status_id:          ID of the status this type of response body is returned with.
-- removing_update_id: ID of the update that removed or replaced this element (version)
-- data_type_id:       Type of this body's contents.
-- 		References enumeration PropertyType
-- 		Possible values are: 1 = any prop, 2 = string prop, 3 = int prop, 4 = decimal prop, 5 = long prop, 6 = boolean prop, 7 = instant prop, 8 = duration prop, 9 = model prop
-- model_type_id:      If this body contains an object or objects, contains the ID of the model schema that describes the applicable object structure.
-- is_array:           Whether this body contains an array of values. False if no more than one value may be included.
CREATE TABLE `response_body_schema`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`original_update_id` INT NOT NULL, 
	`status_id` INT NOT NULL, 
	`removing_update_id` INT, 
	`data_type_id` TINYINT NOT NULL, 
	`model_type_id` INT, 
	`is_array` BOOLEAN NOT NULL DEFAULT FALSE, 
	INDEX rsbs_status_id_idx (`status_id`), 
	CONSTRAINT rsbs_du_original_update_ref_fk FOREIGN KEY rsbs_du_original_update_ref_idx (original_update_id) REFERENCES `doc_update`(`id`) ON DELETE CASCADE, 
	CONSTRAINT rsbs_prs_status_ref_fk FOREIGN KEY rsbs_prs_status_ref_idx (status_id) REFERENCES `possible_response_status`(`id`) ON DELETE CASCADE, 
	CONSTRAINT rsbs_du_removing_update_ref_fk FOREIGN KEY rsbs_du_removing_update_ref_idx (removing_update_id) REFERENCES `doc_update`(`id`) ON DELETE SET NULL, 
	CONSTRAINT rsbs_ms_model_type_ref_fk FOREIGN KEY rsbs_ms_model_type_ref_idx (model_type_id) REFERENCES `model_schema`(`id`) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Links an additional required scope to an auth-scope link
-- scope_link_id: ID of the auth-scope link that requires the additional scope
-- scope_id:      ID of the additional authorization scope required by the node.
CREATE TABLE `additional_node_auth_scope_requirement`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`scope_link_id` INT NOT NULL, 
	`scope_id` INT NOT NULL, 
	CONSTRAINT anasr_nasl_scope_link_ref_fk FOREIGN KEY anasr_nasl_scope_link_ref_idx (scope_link_id) REFERENCES `node_auth_scope_link`(`id`) ON DELETE CASCADE, 
	CONSTRAINT anasr_s_scope_ref_fk FOREIGN KEY anasr_s_scope_ref_idx (scope_id) REFERENCES `scope`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

