# Snowplow Analytics for Wordpress
This Wordpress plugin and associated data schema for Snowplow Analytics can be used to capture additional contextual information about Wordpress posts for **page view** events in Snowplow. With wp-snowplow installed and configured properly, the following additional contextual information will be captured for all Wordpress pages of type **post**. (This does not include static pages or other taxonomies in wordpress at this time - making this setting configurable is in our short-term roadmap):

- **Post ID**
  - (Integer) The internal ID of the post, for use when joining back to the Wordpress database.
- **Post Author**
  - (Integer) The internal ID of the post author, for use when joining back to the Wordpress database.
- **Post Author Name**
  - (String) The display name of the post author.
- **Post Date**
  - (Timestamp) The date and time of the post.
- **Post Date (GMT)**
  - (Timestamp) The date and time of the post adjusted to GMT.
- **Post Title**
  - (String) The title of the post.
- **Post Name**
  - (String) The post's slug.
- **Post Modified Date**
  - (Timestamp) The last modified date of the post.
- **Post Modified Date (GMT)**
  - (Timestamp) The last mofified date of the post adjusted to GMT.
- **GUID**
  - (String) A link to the post. This is not the permalink.
- **Post Permalink**
  - (String) The permalink of the post.
- **Post Type**
  - (String) The [post type](https://codex.wordpress.org/Post_Types) of the Wordpress object. This should always be 'post' for now.
- **Comment Status**
  - (String) The status of commenting on the post. Expected values are 'open' or 'closed'
- **Comment Count**
  - (Integer) A count of comments on the post.
- **Post Tags**
  - (String) A comma seperated list of post tags.
- **Post Categories**
  - (String) A comma seperated list of post categories.
- **Post Thumbnail**
  - (String) If the post has a featured image, the URL of the full sized version of that image.

For additional information on the above attributes, please refer to the [Wordpress Codex](https://codex.wordpress.org/Function_Reference/$post).

## Installation Instructions
These instructions assume you already have a Snowplow Analytics collector and pipeline running. If you do not, check out [Snowplow Analytics](http://snowplowanalytics.com) for more information on getting started with the platform.

[Clone this repo](https://github.com/ironsidegroup/wp-snowplow.git) to your local machine and follow the steps below to setup Snowplow Analytics for Wordpress.

### Step 1: Configure the Wordpress JSON Schema

If you're hosting your Iglu repository in Amazon S3, create or use an existing bucket with 'Static Website Hosting' enabled. From the root, create the folder structure, schemas/com.ironsidegroup/wordpress/jsonschema/, inside the bucket. From this repository, upload the file (named: 1-0-0) under schemas/com.ironsidegroup/wordpress/jsonschema/, into previously mentioned S3 path. After a successful upload, select '1-0-0' and under the 'Actions' button in the S3 UI, 'Make Public' the file; this will ensure that read permissions is given to anyone that access that file's particular link.

On a personal Iglu repository outside of Amazon S3, make sure you follow the same folder structure and upload the same JSON Schema file (schemas/com.ironsidegroup/wordpress/jsonschema/1-0-0 from the cloned repo). Again, ensure that this file is made read accessable to the public.

These JSON Schemas are what are known as self-describing JSONs and to learn more about this topic and the reasoning behind the folder structure and file name, please [check the Snowplow/Iglu documentation](https://github.com/snowplow/iglu/wiki/Self-describing-JSONs).

### Step 2: Configure the JSONPath for Snowplow Shredding

Similiar to Step 1, you can host the JSONPath in Amazon S3 or on your own private domain. Again, create or use an existing bucket with 'Static Website Hosting' enabled. From the root, create the folder structure, jsonpaths/com.ironsidegroup/, inside the bucket. From this repository, upload the file (named: 1-0-0) under schemas/com.ironsidegroup/wordpress/jsonschema/, into previously mentioned S3 path. After a successful upload, select 'wordpress_1.json' and 'Make Public' the file.

For independent hosting, ensure that you create the same folder structure, upload the same JSONPath file, publicize the file.

Either way that you choose to host the JSONPath, make sure that you update your 'jsonpath_assets' field in your StorageLoader configurations file. If you don't, StorageLoader will only recognize the public Snowplow JSONPath files.

### Step 3: Prepare your Data Warehouse

Assuming that you have access as an administrator for your Redshift instance and that you're using the native Snowplow schema, 'atomic', the SQL DDL (wordpress_1.sql under redshift-storage/sql/ from the cloned repo) can be executed as a SQL query or with the psql (Postgres) command-line utility. If your Snowplow 'events' table is under a different database schema, replace all instances of 'atomic' inside the DDL with the schema name that is required.

If you're using the psql utility, you can follow the examples from the [Snowplow Redshift documentation](https://github.com/snowplow/snowplow/wiki/setting-up-redshift#db).

### Step 4: Install and Configure the Wordpress Plugin

1. [Download the plugin zip file](https://github.com/ironsidegroup/wp-snowplow/raw/master/wordpress/wp-snowplow.zip) and install in your Wordpress instance.
2. Navigate to settings and be sure to enter your Snowplow Collector Host Name and Snowplow App Name. **If you do not enter and save these settings the plugin will not send any events to your collector.**
3. If you would like to extract UserID information from a cookie value, enter the name of the cookie in that field.
