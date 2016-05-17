# Snowplow Analytics for Wordpress

## Background

## Installation Instructions
These instructions assume you already have a Snowplow Analytics collector and pipeline running. If you do not, check out [Snowplow Analytics](http://snowplowanalytics.com) for more information on getting started with the platform.

### Step 1: Configure the Wordpress Schema

Snowplow's template for their JSON Schemas shows the most basic fields that are needed for custom contexts:

      {
         "$schema": "http://iglucentral.com/schemas/com.snowplowanalytics.self-desc/schema/jsonschema/1-0-0#",
         "description": "Schema for an example event",
         "self": {
            "vendor": "com.example_company",
            "name": "example_event",
            "format": "jsonschema",
            "version": "1-0-0"
         },

         "type": "object",
         "properties": {
            "exampleStringField": {
                "type": "string",
                "maxLength": 255
            },
            "exampleIntegerField": {
                "type": "integer"
            },
            "exampleNumericField": {
                "type": "number",
                "maxDecimal": 3
            },
            "exampleTimestampField": {
                "type": "string",
                "format": "date-time"
            }
         },
         "minProperties":1, //optional
         "required": ["exampleStringField", "exampleIntegerField"], //optional
         "additionalProperties": false
      }

The critical keys, "$schema", "description", "self", "type", and "properties", are mandatory and "properties" is where you define the custom context fields that are essential to your analytics. There are several simple datatypes supported including String, Integer, Date, and Boolean. For a full list of what datatypes are allowed and how you would format them, check out: http://snowplowanalytics.com/blog/2013/05/14/snowplow-unstructured-events-guide/#supported-datatypes.

Under custom-context/json-schema/schemas/com.yourcompany/WordPress/jsonschema in the repo, you will find a 1-0-0 file which is the JSON Schema that is needed for the WordPress custom context captures. This is a very specific folder structure and a very obscure file name. These JSON Schemas are what are known as self-describing JSONs and to learn more about this topic and the reasonoing behind the folder structure and file name, please visit: https://github.com/snowplow/iglu/wiki/Self-describing-JSONs.

Caveat: When saving the JSON to your Iglu repo, make sure there is public read access to the JSON Schema. In S3, you will have to enable website hosting for the bucket and publicize the JSON Schema inside the bucket.

### Step 2: Prepare your Data Warehouse

With the JSON Schema finished, you can use that file to create the necessary JSONPath file. This file is used after the Snowplow shredding process and facilitates how the different custom context fields are split up and stored inside the custom context Redshift table. 

To help us on this, Snowplow released a command line utility: https://github.com/snowplow/schema-guru

Schema Guru helps create the JSONPath based off of the custom context JSON Schema. The two commands that you would have to use are:

    ./schema-guru-0.6.1 ddl {{input}}

and

    ./schema-guru-0.6.1 ddl --with-json-paths {{input}}

For both commands {{input}} represents the path for the JSON schema, so make sure to save a copy somewhere locally just for the JSONPath and ddl generation. Ultimately, JSONPaths are also saved in S3 in almost the same manner as JSON schemas. For detailed instructions, please refer to: https://github.com/snowplow/snowplow/wiki/4-Loading-shredded-types#jsonpaths.

Assuming that you access as an administrator for your Redshift instance and that you're using the native Snoplow schema, 'atomic', the ddl created by Schema-Guru can be executed as a SQL query or with the storage-loader utility. If you choose to run the ddl manually, the schema might be missing from the comment line, so change the statement:

    COMMENT ON TABLE com_yourcompany_word_press_1 IS 'iglu:com.yourcompany/WordPress/jsonschema/1-0-0';

to:

    COMMENT ON TABLE atomic.com_yourcompany_word_press_1 IS 'iglu:com.yourcompany/WordPress/jsonschema/1-0-0';

### Step 3: Install and Configure the Wordpress Plugin
Coming Soon!
