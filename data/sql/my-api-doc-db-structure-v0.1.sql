-- 
-- Database structure for my api doc models
-- Version: v0.1
-- Last generated: 2026-10-26
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

-- Defines a group of API nodes
-- api_id:           ID of the API this node group belongs to
-- name:             (Human readable) name of this node group
-- description:      Description of this group's function or theme
-- created:          Time when this api node group was added to the database
-- deprecated_after: Time when this api node group became deprecated. None while this api node group is still valid.
CREATE TABLE `api_node_group`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`api_id` INT NOT NULL, 
	`name` VARCHAR(48) NOT NULL, 
	`description` VARCHAR(255), 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	`deprecated_after` DATETIME, 
	INDEX ang_name_idx (`name`), 
	INDEX ang_created_idx (`created`), 
	INDEX ang_deprecated_after_idx (`deprecated_after`), 
	CONSTRAINT ang_a_api_ref_fk FOREIGN KEY ang_a_api_ref_idx (api_id) REFERENCES `api`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents an individual API node implementation
-- group_id:         ID of the node group this node belongs to
-- name:             Human-readable name of this node
-- name_no_spaces:   A lowercase version of the node name, with no whitespace included. Used for indexing & search.
-- relative_path:    Path of this node, relative to the parent node (if defined) or the API root (if no parent is defined). 
-- 		May contain path parameters.
-- parent_id:        ID of the node that appears directly above/before this node on the path. 
-- 		None if no parent node appears between this node and the API root.
-- description:      A description of this API node.
-- created:          Time when this api node was added to the database
-- deprecated_after: Time when this api node became deprecated. None while this api node is still valid.
CREATE TABLE `api_node`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`group_id` INT NOT NULL, 
	`name` VARCHAR(48) NOT NULL, 
	`name_no_spaces` VARCHAR(48) NOT NULL, 
	`relative_path` VARCHAR(48) NOT NULL, 
	`parent_id` INT, 
	`description` VARCHAR(255), 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	`deprecated_after` DATETIME, 
	INDEX an_name_no_spaces_idx (`name_no_spaces`), 
	INDEX an_relative_path_idx (`relative_path`), 
	INDEX an_created_idx (`created`), 
	INDEX an_deprecated_after_idx (`deprecated_after`), 
	CONSTRAINT an_ang_group_ref_fk FOREIGN KEY an_ang_group_ref_idx (group_id) REFERENCES `api_node_group`(`id`) ON DELETE CASCADE, 
	CONSTRAINT an_an_parent_ref_fk FOREIGN KEY an_an_parent_ref_idx (parent_id) REFERENCES `api_node`(`id`) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Describes how a specific HTTP method is implemented in an API node.
-- node_id:               ID of the API node that contains this implementation.
-- method:                The implemented method.
-- description:           A description of this implementation.
-- created:               Time when this api node method implementation was added to the database
-- deprecated_after:      Time when this api node method implementation became deprecated. None while this api node method implementation is still valid.
-- requires_request_body: Whether a request body is required for this implementation.
CREATE TABLE `api_node_method_implementation`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`node_id` INT NOT NULL, 
	`method` VARCHAR(8) NOT NULL DEFAULT 'GET', 
	`description` VARCHAR(255), 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	`deprecated_after` DATETIME, 
	`requires_request_body` BOOLEAN NOT NULL DEFAULT FALSE, 
	INDEX anmi_method_idx (`method`), 
	INDEX anmi_created_idx (`created`), 
	INDEX anmi_deprecated_after_idx (`deprecated_after`), 
	CONSTRAINT anmi_an_node_ref_fk FOREIGN KEY anmi_an_node_ref_idx (node_id) REFERENCES `api_node`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Applies a specific schema to the path parameters received by an API node.
-- node_id:          ID of the API node that accepts these parameters.
-- model_type_id:    ID of the model type that describes the received path parameters.
-- created:          Time when this path parameters schema link was added to the database
-- deprecated_after: Time when this path parameters schema link became deprecated. None while this path parameters schema link is still valid.
CREATE TABLE `path_parameters_schema_link`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`node_id` INT NOT NULL, 
	`model_type_id` INT NOT NULL, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	`deprecated_after` DATETIME, 
	INDEX ppsl_created_idx (`created`), 
	INDEX ppsl_deprecated_after_idx (`deprecated_after`), 
	CONSTRAINT ppsl_an_node_ref_fk FOREIGN KEY ppsl_an_node_ref_idx (node_id) REFERENCES `api_node`(`id`) ON DELETE CASCADE, 
	CONSTRAINT ppsl_mt_model_type_ref_fk FOREIGN KEY ppsl_mt_model_type_ref_idx (model_type_id) REFERENCES `model_type`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Defines a possible response status for an API node method call.
-- implementation_id: ID of the implementation that may return this response status.
-- status_code:       HTTP status code that may be returned by the API node method call.
-- description:       Description of what this status signifies.
-- created:           Time when this possible response status was added to the database
-- deprecated_after:  Time when this possible response status became deprecated. None while this possible response status is still valid.
-- has_body:          Whether a response body is returned with this status. Uncertain if not specified in the documentation.
CREATE TABLE `possible_response_status`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`implementation_id` INT NOT NULL, 
	`status_code` SMALLINT(3) NOT NULL, 
	`description` VARCHAR(96), 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	`deprecated_after` DATETIME, 
	`has_body` BOOLEAN, 
	INDEX prs_status_code_idx (`status_code`), 
	INDEX prs_created_idx (`created`), 
	INDEX prs_deprecated_after_idx (`deprecated_after`), 
	CONSTRAINT prs_anmi_implementation_ref_fk FOREIGN KEY prs_anmi_implementation_ref_idx (implementation_id) REFERENCES `api_node_method_implementation`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;


--	Schema	----------

-- Defines properties in an input or output model / JSON object.
-- api_id:           ID of the API this model type belongs to.
-- name:             Name of this model type (/class). Empty if unnamed. 
-- 		Named model types are often referenced from multiple sources, while unnamed types are typically single-use.
-- description:      Description of this model type's role / function.
-- created:          Time when this model schema was added to the database
-- deprecated_after: Time when this model schema became deprecated. None while this model schema is still valid.
CREATE TABLE `model_schema`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`api_id` INT NOT NULL, 
	`name` VARCHAR(64), 
	`description` VARCHAR(255), 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	`deprecated_after` DATETIME, 
	INDEX ms_name_idx (`name`), 
	INDEX ms_created_idx (`created`), 
	INDEX ms_deprecated_after_idx (`deprecated_after`), 
	CONSTRAINT ms_a_api_ref_fk FOREIGN KEY ms_a_api_ref_idx (api_id) REFERENCES `api`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Defines an individual property in a model schema.
-- model_type_id:    ID of the model type that this property belongs to.
-- name:             Name of this property, as it should appear in the input or output model / JSON object.
-- data_type_id:     This property's expected value type.
-- 		References enumeration PropertyType
-- 		Possible values are: 1 = any prop, 2 = string prop, 3 = int prop, 4 = decimal prop, 5 = long prop, 6 = boolean prop, 7 = instant prop, 8 = duration prop, 9 = model prop
-- content_type_id:  If this property contains a model/object, this is the ID of the contained model's type. 
-- 		None if this property uses some other data type.
-- description:      A description of this property's role/function.
-- created:          Time when this property schema was added to the database
-- deprecated_after: Time when this property schema became deprecated. None while this property schema is still valid.
-- is_array:         Whether this property is an array of values. False if this property contains a single value.
-- is_optional:      Whether this property may be omitted from the input and/or output.
CREATE TABLE `property_schema`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`model_type_id` INT NOT NULL, 
	`name` VARCHAR(64), 
	`data_type_id` TINYINT NOT NULL, 
	`content_type_id` INT, 
	`description` VARCHAR(255), 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	`deprecated_after` DATETIME, 
	`is_array` BOOLEAN NOT NULL DEFAULT FALSE, 
	`is_optional` BOOLEAN NOT NULL DEFAULT FALSE, 
	INDEX ps_name_idx (`name`), 
	INDEX ps_created_idx (`created`), 
	INDEX ps_deprecated_after_idx (`deprecated_after`), 
	CONSTRAINT ps_ms_model_type_ref_fk FOREIGN KEY ps_ms_model_type_ref_idx (model_type_id) REFERENCES `model_schema`(`id`) ON DELETE CASCADE, 
	CONSTRAINT ps_ms_content_type_ref_fk FOREIGN KEY ps_ms_content_type_ref_idx (content_type_id) REFERENCES `model_schema`(`id`) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Defines an alternative supported data type for a property.
-- prop_id:          ID of the property that has this alternative type
-- alt_type_id:      Alternative data type supported by the linked property
-- 		References enumeration PropertyType
-- 		Possible values are: 1 = any prop, 2 = string prop, 3 = int prop, 4 = decimal prop, 5 = long prop, 6 = boolean prop, 7 = instant prop, 8 = duration prop, 9 = model prop
-- created:          Time when this alternative property type was added to the database
-- deprecated_after: Time when this alternative property type became deprecated. None while this alternative property type is still valid.
CREATE TABLE `alternative_property_type`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`prop_id` INT NOT NULL, 
	`alt_type_id` TINYINT NOT NULL, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	`deprecated_after` DATETIME, 
	INDEX apt_created_idx (`created`), 
	INDEX apt_deprecated_after_idx (`deprecated_after`), 
	CONSTRAINT apt_ps_prop_ref_fk FOREIGN KEY apt_ps_prop_ref_idx (prop_id) REFERENCES `property_schema`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Defines a possible value for an enum property
-- prop_id:          ID of the property that this value may be assigned to
-- value:            The value the linked property may have
-- description:      Description of this value's role / function
-- created:          Time when this possible property value was added to the database
-- deprecated_after: Time when this possible property value became deprecated. None while this possible property value is still valid.
CREATE TABLE `possible_property_value`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`prop_id` INT NOT NULL, 
	`value` VARCHAR(24) NOT NULL, 
	`description` VARCHAR(255), 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	`deprecated_after` DATETIME, 
	INDEX ppv_created_idx (`created`), 
	INDEX ppv_deprecated_after_idx (`deprecated_after`), 
	CONSTRAINT ppv_ps_prop_ref_fk FOREIGN KEY ppv_ps_prop_ref_idx (prop_id) REFERENCES `property_schema`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;


--	Auth	----------

-- Represents some (custom) token type used by the documented API.
-- api_id:           ID of the API where this token type is used.
-- name:             (Human-readable) name of this token type
-- name_no_spaces:   A lowercase copy of this token's name with all whitespaces removed. Used for indexing and search.
-- use_type_id:      Determines how this token is applied.
-- 		References enumeration TokenUseType
-- 		Possible values are: 1 = bearer, 2 = basic
-- created:          Time when this auth token type was added to the database
-- deprecated_after: Time when this auth token type became deprecated. None while this auth token type is still valid.
CREATE TABLE `auth_token_type`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`api_id` INT NOT NULL, 
	`name` VARCHAR(48) NOT NULL, 
	`name_no_spaces` VARCHAR(48) NOT NULL, 
	`use_type_id` TINYINT NOT NULL, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	`deprecated_after` DATETIME, 
	INDEX att_name_no_spaces_idx (`name_no_spaces`), 
	INDEX att_created_idx (`created`), 
	INDEX att_deprecated_after_idx (`deprecated_after`), 
	CONSTRAINT att_a_api_ref_fk FOREIGN KEY att_a_api_ref_idx (api_id) REFERENCES `api`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents an authorization scope in the documented API.
-- api_id:           ID of the API where this scope is used.
-- name:             Name / identifier of this scope.
-- created:          Time when this scope was added to the database
-- deprecated_after: Time when this scope became deprecated. None while this scope is still valid.
CREATE TABLE `scope`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`api_id` INT NOT NULL, 
	`name` VARCHAR(48) NOT NULL, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	`deprecated_after` DATETIME, 
	INDEX s_name_idx (`name`), 
	INDEX s_created_idx (`created`), 
	INDEX s_deprecated_after_idx (`deprecated_after`), 
	CONSTRAINT s_a_api_ref_fk FOREIGN KEY s_a_api_ref_idx (api_id) REFERENCES `api`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;


--	Api	----------

-- Describes an authorization scope required by an API node. 
-- 		If multiple scopes are linked, they typically represent alternative auth scopes, usually for different purposes.
-- node_id:          ID of the node that accepts the linked authorization method.
-- scope_id:         ID of the authorization scope required by the linked node.
-- method:           The HTTP method that the linked authorization method is used for. 
-- 		None if the authorization method is used as a default for all methods (where not overridden).
-- description:      A description concerning this authorization method's function in the linked node.
-- created:          Time when this node auth link was added to the database
-- deprecated_after: Time when this node auth link became deprecated. None while this node auth link is still valid.
CREATE TABLE `node_auth_scope_link`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`node_id` INT NOT NULL, 
	`scope_id` INT NOT NULL, 
	`method` VARCHAR(8) DEFAULT 'GET', 
	`description` VARCHAR(255), 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	`deprecated_after` DATETIME, 
	INDEX nasl_scope_id_idx (`scope_id`), 
	INDEX nasl_method_idx (`method`), 
	INDEX nasl_created_idx (`created`), 
	INDEX nasl_deprecated_after_idx (`deprecated_after`), 
	CONSTRAINT nasl_an_node_ref_fk FOREIGN KEY nasl_an_node_ref_idx (node_id) REFERENCES `api_node`(`id`) ON DELETE CASCADE, 
	CONSTRAINT nasl_s_scope_ref_fk FOREIGN KEY nasl_s_scope_ref_idx (scope_id) REFERENCES `scope`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Describes an authorization type accepted by an API node.
-- node_id:          ID of the node that accepts the linked authorization method.
-- auth_type_id:     ID of the authorization type accepted by the linked node.
-- method:           The HTTP method that the linked authorization method is used for. 
-- 		None if the authorization method is used as a default for all methods (where not overridden).
-- description:      A description concerning this authorization method's function in the linked node.
-- created:          Time when this node auth link was added to the database
-- deprecated_after: Time when this node auth link became deprecated. None while this node auth link is still valid.
CREATE TABLE `node_auth_type_link`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`node_id` INT NOT NULL, 
	`auth_type_id` INT NOT NULL, 
	`method` VARCHAR(8) DEFAULT 'GET', 
	`description` VARCHAR(255), 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	`deprecated_after` DATETIME, 
	INDEX natl_auth_type_id_idx (`auth_type_id`), 
	INDEX natl_method_idx (`method`), 
	INDEX natl_created_idx (`created`), 
	INDEX natl_deprecated_after_idx (`deprecated_after`), 
	CONSTRAINT natl_an_node_ref_fk FOREIGN KEY natl_an_node_ref_idx (node_id) REFERENCES `api_node`(`id`) ON DELETE CASCADE, 
	CONSTRAINT natl_att_auth_type_ref_fk FOREIGN KEY natl_att_auth_type_ref_idx (auth_type_id) REFERENCES `auth_token_type`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Applies a specific schema to the query parameters accepted by an API node.
-- implementation_id: ID of the method implementation that uses the linked schema.
-- model_type_id:     ID of the schema that defines the type of model accepted by the linked implementation.
-- created:           Time when this api node method implementation model schema link was added to the database
-- deprecated_after:  Time when this api node method implementation model schema link became deprecated. None while this api node method implementation model schema link is still valid.
CREATE TABLE `query_parameters_schema_link`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`implementation_id` INT NOT NULL, 
	`model_type_id` INT NOT NULL, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	`deprecated_after` DATETIME, 
	INDEX qpsl_created_idx (`created`), 
	INDEX qpsl_deprecated_after_idx (`deprecated_after`), 
	CONSTRAINT qpsl_anmi_implementation_ref_fk FOREIGN KEY qpsl_anmi_implementation_ref_idx (implementation_id) REFERENCES `api_node_method_implementation`(`id`) ON DELETE CASCADE, 
	CONSTRAINT qpsl_ms_model_type_ref_fk FOREIGN KEY qpsl_ms_model_type_ref_idx (model_type_id) REFERENCES `model_schema`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Applies a specific schema to the request body accepted by an API node. 
-- 		Multiple alternative schemas may be defined separately.
-- implementation_id:   ID of the method implementation that uses the linked schema.
-- model_type_id:       ID of the schema that defines the type of model accepted by the linked implementation.
-- created:             Time when this api node method implementation model schema link was added to the database
-- deprecated_after:    Time when this api node method implementation model schema link became deprecated. None while this api node method implementation model schema link is still valid.
-- custom_content_type: Non-standard content type expected from the request body, if applicable.
-- description:         Description of the expected request body.
-- is_array:            Whether a JSON array is expected.
CREATE TABLE `request_body_schema`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`implementation_id` INT NOT NULL, 
	`model_type_id` INT NOT NULL, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	`deprecated_after` DATETIME, 
	`custom_content_type` VARCHAR(32), 
	`description` VARCHAR(255), 
	`is_array` BOOLEAN NOT NULL DEFAULT FALSE, 
	INDEX rbs_created_idx (`created`), 
	INDEX rbs_deprecated_after_idx (`deprecated_after`), 
	CONSTRAINT rbs_anmi_implementation_ref_fk FOREIGN KEY rbs_anmi_implementation_ref_idx (implementation_id) REFERENCES `api_node_method_implementation`(`id`) ON DELETE CASCADE, 
	CONSTRAINT rbs_ms_model_type_ref_fk FOREIGN KEY rbs_ms_model_type_ref_idx (model_type_id) REFERENCES `model_schema`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Defines how an API node's response body is formed.
-- status_id:        ID of the status this type of response body is returned with.
-- content_type_id:  If this body is non-empty, specifies the type of content it contains.
-- model_type_id:    If this body contains a JSON object or objects, contains the ID of the model schema that describes the applicable object structure.
-- created:          Time when this response content type was added to the database
-- deprecated_after: Time when this response content type became deprecated. None while this response content type is still valid.
-- is_array:         Whether this response body contains an array of values. False if no more than one value may be returned.
-- is_empty:         Whether this response body is empty / no response body is returned.
CREATE TABLE `response_content_type`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`status_id` INT NOT NULL, 
	`content_type_id` TINYINT, 
	`model_type_id` INT, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	`deprecated_after` DATETIME, 
	`is_array` BOOLEAN NOT NULL DEFAULT FALSE, 
	`is_empty` BOOLEAN NOT NULL DEFAULT FALSE, 
	INDEX rct_created_idx (`created`), 
	INDEX rct_deprecated_after_idx (`deprecated_after`), 
	CONSTRAINT rct_prs_status_ref_fk FOREIGN KEY rct_prs_status_ref_idx (status_id) REFERENCES `possible_response_status`(`id`) ON DELETE CASCADE, 
	CONSTRAINT rct_ms_model_type_ref_fk FOREIGN KEY rct_ms_model_type_ref_idx (model_type_id) REFERENCES `model_schema`(`id`) ON DELETE SET NULL
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

