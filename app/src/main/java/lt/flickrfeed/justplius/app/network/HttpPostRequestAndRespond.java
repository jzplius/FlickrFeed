package lt.flickrfeed.justplius.app.network;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * This class performs HTTP POST request on given URL and tries to return 
 * response in String
 */

public class HttpPostRequestAndRespond {
    
	private static String TAG = "HttpPostRequestAndRespond.java: ";
    private String postResult = null;
    private InputStream is = null;    
    
    // Post a request on a given URL and retrieve it's response as a String   
    public HttpPostRequestAndRespond (String url){    	
    	
	    try{
	    	
	    	//http post request
	    	HttpClient httpclient = new DefaultHttpClient();
	        HttpPost httppost = new HttpPost(url);
	        
	        //http post response	        
	        HttpResponse response = httpclient.execute(httppost);
	        HttpEntity entity = response.getEntity();
	        is = entity.getContent();	
	        
	    } catch(Exception e){

	        Log.e(TAG, "Error in http connection: "+e.toString());
	    }
	    	    
	    try{
	    	
	    	// Convert response to string
	        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"),8);
	        StringBuilder sb = new StringBuilder();
	        String line = null;
	        while ((line = reader.readLine()) != null) {
	            sb.append(line + "\n");
	        }	                 
	        postResult=sb.toString();	
	        
	    } catch(Exception e){

	        Log.e(TAG, "Error converting result: "+e.toString());

	    } finally {
	    	try {
	    		
	    		if (is != null) {
	    			is.close();
	    		}
	    		
			} catch (IOException e) {
				e.printStackTrace();
			}	   
	    }
	    
    }
    
    // Return respond as a String
	public String getPostResult (){
    	return postResult;
    }
}
