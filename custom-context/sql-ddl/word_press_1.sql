CREATE SCHEMA IF NOT EXISTS atomic;

CREATE TABLE IF NOT EXISTS atomic.com_yourcompany_word_press_1 (
    "schema_vendor"     VARCHAR(128)  ENCODE RUNLENGTH NOT NULL,
    "schema_name"       VARCHAR(128)  ENCODE RUNLENGTH NOT NULL,
    "schema_format"     VARCHAR(128)  ENCODE RUNLENGTH NOT NULL,
    "schema_version"    VARCHAR(128)  ENCODE RUNLENGTH NOT NULL,
    "root_id"           CHAR(36)      ENCODE RAW       NOT NULL,
    "root_tstamp"       TIMESTAMP     ENCODE LZO       NOT NULL,
    "ref_root"          VARCHAR(255)  ENCODE RUNLENGTH NOT NULL,
    "ref_tree"          VARCHAR(1500) ENCODE RUNLENGTH NOT NULL,
    "ref_parent"        VARCHAR(255)  ENCODE RUNLENGTH NOT NULL,
    "comment_count"     BIGINT        ENCODE LZO,
    "comment_status"    VARCHAR(4096) ENCODE LZO,
    "guid"              VARCHAR(4096) ENCODE LZO,
    "id"                BIGINT        ENCODE LZO,
    "post_author"       BIGINT        ENCODE LZO,
    "post_author_name"  VARCHAR(4096) ENCODE LZO,
    "post_categories"   VARCHAR(4096) ENCODE LZO,
    "post_date"         TIMESTAMP     ENCODE LZO,
    "post_date_gmt"     TIMESTAMP     ENCODE LZO,
    "post_modified"     TIMESTAMP     ENCODE LZO,
    "post_modified_gmt" TIMESTAMP     ENCODE LZO,
    "post_name"         VARCHAR(4096) ENCODE LZO,
    "post_permalink"    VARCHAR(4096) ENCODE LZO,
    "post_tags"         VARCHAR(4096) ENCODE LZO,
    "post_thumbnail"    VARCHAR(4096) ENCODE LZO,
    "post_title"        VARCHAR(4096) ENCODE LZO,
    "post_type"         VARCHAR(4096) ENCODE LZO,
    FOREIGN KEY (root_id) REFERENCES atomic.events(event_id)
)
DISTSTYLE KEY
DISTKEY (root_id)
SORTKEY (root_tstamp);

COMMENT ON TABLE atomic.com_yourcompany_word_press_1 IS 'iglu:com.yourcompany/WordPress/jsonschema/1-0-0';