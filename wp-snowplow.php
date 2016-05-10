<?php
/*
Plugin Name: Snowplow Analytics for Wordpress
Plugin URI: http://ironsidegroup.com
Description: Adds a Snowplow tracking code with custom Wordpress context to your site.
Version: 1.0
Author: Greg Bonnette
Author URI: http://gregbonnette.com
License: Apache 2.0
 */
 
add_action( 'admin_menu', 'snowplow_add_admin_menu' );
add_action( 'admin_init', 'snowplow_settings_init' );


function snowplow_add_admin_menu(  ) { 
	add_options_page( 'Snowplow Analytics', 'Snowplow Analytics', 'manage_options', 'wp-snowplow', 'snowplow_options_page' );
}


function snowplow_settings_init(  ) { 
	register_setting( 'pluginPage', 'snowplow_settings' );

	add_settings_section(
		'snowplow_pluginPage_section', 
		__( 'Please enter your Snowplow Analytics settings here to enable the plugin.', 'wordpress' ), 
		'snowplow_settings_section_callback', 
		'pluginPage'
	);

	add_settings_field( 
		'snowplow_app_id', 
		__( 'Application ID', 'wordpress' ), 
		'snowplow_app_id_render', 
		'pluginPage', 
		'snowplow_pluginPage_section' 
	);

	add_settings_field( 
		'snowplow_collector_host', 
		__( 'Collector Host Name', 'wordpress' ), 
		'snowplow_collector_host_render', 
		'pluginPage', 
		'snowplow_pluginPage_section' 
	);
}


function snowplow_app_id_render(  ) { 
	$options = get_option( 'snowplow_settings' );
	?>
	<input type='text' name='snowplow_settings[snowplow_app_id]' value='<?php echo $options['snowplow_app_id']; ?>'>
	<?php
}


function snowplow_collector_host_render(  ) { 
	$options = get_option( 'snowplow_settings' );
	?>
	<input type='text' name='snowplow_settings[snowplow_collector_host]' value='<?php echo $options['snowplow_collector_host']; ?>'>
	<?php
}


function snowplow_settings_section_callback(  ) { 
	//echo __( 'Snowplow', 'wordpress' );
}


function snowplow_options_page(  ) { 
	?>
	<form action='options.php' method='post'>
		<h2>Snowplow Analytics Settings</h2>
		<?php
		settings_fields( 'pluginPage' );
		do_settings_sections( 'pluginPage' );
		submit_button();
		?>
	</form>
	<?php
}

function wp_snowplow_init() {
	//Initiate the Snowplow tracker
	$options = get_option( 'snowplow_settings' );
	$app_id = $options['snowplow_app_id'];
	$host = $options['snowplow_collector_host'];
	
	$sp_parms = array (
		'script' => plugin_dir_url( __FILE__ ) . 'js/2.6.1/ocTMKibakCci.js',
		'app_id' => 'ironsidegroup.com',
		'host' => 'sp.ironsidecloud.com'
	);
		
	wp_enqueue_script(
		'sp-init',
		plugin_dir_url( __FILE__ ) . 'js/sp-init.js'
 	);
	
	// Pass parms to the single post JS
 	wp_localize_script(
 		'sp-init',
 		'sp_parms',
		$sp_parms
 	);
}
add_action('wp_enqueue_scripts', 'wp_snowplow_init');

function wp_snowplow_track() {
	
	//Set the UserID to the Hubspot user token is that plugin is active
	if(is_plugin_active('hubspot-tracking-code/hubspot-tracking-code.php')) 
	{
		wp_enqueue_script(
			'sp-uid-hs',
			plugin_dir_url( __FILE__ ) . 'js/sp-uid-hs.js',
			'sp-init'
		);
	}
	
	if(is_singular('post')){	
		//Extract post metadata
		$this_post = get_queried_object();
		
		//Nest arrays and type cast to overcome wp_localize_script issues
		//https://wpbeaches.com/using-wp_localize_script-and-jquery-values-including-strings-booleans-and-integers/
		$sp_post_meta = array (
			'data' => array(
 				'id' => (int)$this_post->ID,
				'post_author' => (int)$this_post->post_author,
 				'post_author_name' => get_the_author_meta('display_name', $this_post->post_author),
 				'post_date' => date('c', strtotime($this_post->post_date)),
				'post_date_gmt' => date('c', strtotime($this_post->post_date_gmt)),
				'post_title' => $this_post->post_title,
				'post_name' => $this_post->post_name,
				'post_modified' => date('c', strtotime($this_post->post_modified)),
				'post_modified_gmt' => date('c', strtotime($this_post->post_modified_gmt)),
				'guid' => $this_post->guid,
				'post_permalink' => get_permalink($this_post->ID),
				'post_type' => $this_post->post_type,
				'comment_status' => $this_post->comment_status,
				'comment_count' => (int)$this_post->comment_count
 			)
		);
		
		// Enqueue the single post JS
		wp_enqueue_script(
			'sp-pv-post',
			plugin_dir_url( __FILE__ ) . 'js/sp-pv-post.js',
			'sp-init'
 		);

		// Pass parms to the single post JS
 		wp_localize_script(
 			'sp-pv-post',
 			'sp_post_meta',
			$sp_post_meta
 		);
	} else {
		wp_enqueue_script(
			'sp-pv-non-post',
			plugin_dir_url( __FILE__ ) . 'js/sp-pv-non-post.js',
			'sp-init'
 		);
	}
}
add_action('wp_enqueue_scripts', 'wp_snowplow_track');
?>