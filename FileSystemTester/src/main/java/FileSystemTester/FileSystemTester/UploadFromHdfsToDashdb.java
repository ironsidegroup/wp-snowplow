package FileSystemTester.FileSystemTester;

import java.io.IOException;
import java.text.DecimalFormat;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

/**
 * Test class to pull part files from local HDFS as InputStream's. With those InputStream's, attach them to 
 * a multipart DashDB POST request (for data load into a specified table). Measurement of time is taken to 
 * figure out efficiency in single-thread operation.
 * 
 * @author StevenHu
 *
 */
public class UploadFromHdfsToDashdb {

	public static void main(String[] args) throws InterruptedException {
		long startTime = System.nanoTime(); // start time
		DecimalFormat df = new DecimalFormat("#.0000");
		
		// Accept all SSL certificates
		RestAssured.useRelaxedHTTPSValidation();
		RestAssured.authentication = RestAssured.preemptive().basic(args[0], args[1]);
		// RequestSpecification is the holder of specifications for a REST call
		RequestSpecification post = RestAssured.given();
		RequestSpecification get = RestAssured.given();

		Gson gson = new Gson();

		Path path = new Path("hdfs://" + removeSlashAndHDFS(args[4]) + "/" + removeSlashAndHDFS(args[5]));
		Configuration conf = new Configuration();
		conf.set("fs.defaultFS", "hdfs://" + args[4]);

		// Using a try/catch clause to let the JVM close all InputStream's automatically after POST call
		try {
			FileStatus[] partFiles = getFilePathsInHDFS(path, conf); // get part-files names in array
			FileSystem fs = FileSystem.get(conf);

			for (FileStatus file : partFiles) {
				Path filePath = file.getPath();
				String pathString = filePath.toString();
				// This string will be used as the unique request multipart ID and will be used as the referenced file name
				String file_name = pathString.substring(pathString.lastIndexOf('/') + 1, pathString.length());

				FSDataInputStream fis = null;

				try {
					fis = fs.open(filePath);
				} catch (IOException e) {
					e.printStackTrace();
					System.err.println("Can't open file: " + filePath + ", please check if file is in existence.");
					System.exit(9);
				}

				post.multiPart(file_name, file_name, fis);
				
				// can incorporate into log instead
				System.out.println("Read and attached " + file_name + " into POST request.");
			}

			// Pushing out POST call and saving the return body
			String postReponse = post
					.post("https://" + args[2] + ":8443/dashdb-api/load/local/del/" + args[3] + "?delimiter=0x09")
					.asString();		
			
			// Exit connector if DashDB authentication fails
			if(postReponse.contains("Error 401")) {
				System.err.println("\nDashDB login credentials are incorrect, please check username and password for accuracy.");
				System.exit(8);
			}

			ResponseBody postReponseBody = gson.fromJson(postReponse, ResponseBody.class);
	    	String log = postReponseBody.result.LOAD_LOGFILE.substring(postReponseBody.result.LOAD_LOGFILE.indexOf('/')+1,
	    			postReponseBody.result.LOAD_LOGFILE.length());
	    	System.out.println("\nFinished DashDB POST upload request. Returned Log: " + log + " \n");
	    	
	    	String loadStatus = "";
	    	ResponseBody getReponseBody = null;
	    	
	    	// while loop to see if POST call is complete by checking "LOAD_STATUS" in GET /loadlogs/logs...
	    	while (!loadStatus.equals("COMPLETE")){
	    		Thread.sleep(30000); // giving 30 seconds of wait time
				String getReponse = get
						.get("https://" + args[2] + ":8443/dashdb-api/home/loadlogs/" + log)
						.asString();
				getReponseBody = gson.fromJson(getReponse, ResponseBody.class);
				loadStatus = getReponseBody.result.LOAD_STATUS;
	    	}
	    	
	    	// check if input DashDB table exists
	    	if (getReponseBody.result.LOAD_OUTPUT[0].SQLCODE.contains("SQL3304N")) {
				System.err.println("\nDashDB table <TABLE NAME> does not exist. "
						+ "Please double check table name or create new table, '<TABLE NAME>'");
				System.exit(7);
	    	}
	    	
	    	System.out.println("Rows Committed: " + getReponseBody.result.ROWS_COMMITTED);
	    	System.out.println("Rows Deleted: " + getReponseBody.result.ROWS_DELETED);
	    	System.out.println("Rows Skipped: " + getReponseBody.result.ROWS_SKIPPED);
	    	System.out.println("Success Percentage: " + df.format((
	    			1 - ((Double.parseDouble(getReponseBody.result.ROWS_SKIPPED) + Double.parseDouble(getReponseBody.result.ROWS_DELETED))
	    			/Double.parseDouble(getReponseBody.result.ROWS_COMMITTED)))*100) + "%");
	    	
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Path location, " + path.toUri() + " is giving a problem--please recheck for accuracy.");
			System.exit(10);
		}
		
		System.out.println("Total Run Time: " + df.format((System.nanoTime() - startTime)/1000000000.0) + " seconds"); // elapse time print out
	}

	/**
	 * Given a HDFS folder path, return a list of an array of all the file details.
	 * @param location
	 * @param conf
	 * @return
	 * @throws IOException
	 */
	public static FileStatus[] getFilePathsInHDFS(Path location, Configuration conf) throws IOException {
		FileSystem fileSystem = FileSystem.get(location.toUri(), conf);
		FileStatus[] items = fileSystem.listStatus(location);

		return items;
	}
	
	/**
	 * Given a string 's', remove any lead or tail backslashes and the 'hdfs://' from the beginning of the string
	 * @param s
	 * @return
	 */
	public static String removeSlashAndHDFS(String s){
		s = s.startsWith("hdfs://") ? s.substring(7,s.length()) : s;
		s = s.charAt(0) == '/' ? s.substring(1,s.length()) : s;
		s = s.charAt(s.length()-1) == '/' ? s.substring(0,s.length()-1) : s;
		
		return s;
	}
	
	public class ResponseBody {
	    public Result result;
	    
	    public class Result {
		    public String ROWS_COMMITTED;
		    public String ROWS_DELETED;
		    public String ROWS_SKIPPED;
		    public String LOAD_LOGFILE;
		    public SQLMessage[] LOAD_OUTPUT;
		    public String LOAD_STATUS;
	    }
	    
	    public class SQLMessage {
	    	public String MESSAGE;
	    	public String SQLCODE;
	    }
	}
}
