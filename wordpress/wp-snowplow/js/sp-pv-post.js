window.snowplow('trackPageView', null, [{
	schema: "iglu:com.ironside/WordPress/jsonschema/1-0-0",
	data: {
		id: sp_post_meta.data.id,
		post_author: sp_post_meta.data.post_author,
		post_author_name: sp_post_meta.data.post_author_name,
		post_date: sp_post_meta.data.post_date,
		post_date_gmt: sp_post_meta.data.post_date_gmt,
		post_title: sp_post_meta.data.post_title,
		post_name: sp_post_meta.data.post_name,
		post_modified: sp_post_meta.data.post_modified,
		post_modified_gmt: sp_post_meta.data.post_modified_gmt,
		guid: sp_post_meta.data.guid,
		post_permalink: sp_post_meta.data.post_permalink,
		post_type: sp_post_meta.data.post_type,
		comment_status: sp_post_meta.data.comment_status,
		comment_count: sp_post_meta.data.comment_count,
		post_tags: sp_post_meta.data.post_tags,
		post_categories: sp_post_meta.data.post_categories,
		post_thumbnail: sp_post_meta.data.post_thumbnail,
		post_links: sp_post_meta.data.post_links,
		post_headings: sp_post_meta.data.post_headings,
		post_paragraphs: sp_post_meta.data.post_paragraphs,
		post_images: sp_post_meta.data.post_images,
		post_videos: sp_post_meta.data.post_videos,
		post_length: sp_post_meta.data.post_length,
		post_words: sp_post_meta.data.post_words
	}
}]);