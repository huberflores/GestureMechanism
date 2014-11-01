package com.in.mobile.manager.adfile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

/*
 * author Huber Flores
 * in-mobile, 2014
 */

public class FileDownloader extends AsyncTask<String, Void, String> {
	
	Context context;
	
	public FileDownloader(Context context){
		this.context = context;
	}

	@Override
	protected String doInBackground(String... urls) {
		String response = "";
	      for (String pathUrl : urls) {
	    	  try{
					
					URL url = new URL(pathUrl);			
					HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestMethod("GET");
			        urlConnection.setDoOutput(true);
			        urlConnection.connect();
			        
			        File SDCardRoot = Environment.getExternalStorageDirectory();
			        File file = new File(SDCardRoot,"ad.jpg");

			        FileOutputStream fileOutput = new FileOutputStream(file);
			        InputStream inputStream = urlConnection.getInputStream();

			        //int totalSize = urlConnection.getContentLength();
			        //int downloadedSize = 0;

			        byte[] buffer = new byte[1024];
			        int bufferLength = 0; 

			       
			        while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
			                fileOutput.write(buffer, 0, bufferLength);		                
			                //downloadedSize += bufferLength;
			        }
			   
			        fileOutput.close();
			
		
				} catch (MalformedURLException e) {
			        e.printStackTrace();
				} catch (IOException e) {
			        e.printStackTrace();
				}
	    	  
	      }
	      return response;
	}

		
	@Override
	protected void onPostExecute(String result) {
		Toast.makeText(context, "File downloaded",Toast.LENGTH_SHORT).show();
	}
	
}




