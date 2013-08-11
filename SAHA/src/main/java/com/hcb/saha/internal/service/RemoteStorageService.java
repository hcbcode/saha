package com.hcb.saha.internal.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import roboguice.service.RoboIntentService;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.storage.Storage;
import com.google.inject.Inject;
import com.hcb.saha.R;
import com.hcb.saha.internal.data.fs.SahaFileManager;

import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;

public class RemoteStorageService extends RoboIntentService{

	private static final String TAG = RemoteStorageService.class.getSimpleName();

	/** E-mail address of the service account. */
	private static final String SERVICE_ACCOUNT_EMAIL = "537815805809@developer.gserviceaccount.com";

	/** Bucket Name */
	private static final String BUCKET_NAME = "data-real";

	/** Global configuration of Google Cloud Storage OAuth 2.0 scope. */
	private static final String STORAGE_SCOPE =
			"https://www.googleapis.com/auth/devstorage.read_write";

	/** GCS Application Name */
	private static final String APPLICATION_NAME = "SAHA";

	@Inject
	public NetHttpTransport transport;

	@Inject
	public JacksonFactory jsonFactory; 
	
	public RemoteStorageService() {
		super("RemoteStorageService");

	}

	@Override
	protected void onHandleIntent(Intent intent) {

		Log.d(TAG, "Remote Storage Service started");

		/** Create OAuth2 Credential object with service account  */
		GoogleCredential credential = null;

		try {
			credential = new GoogleCredential.Builder()
			.setTransport(transport)
			.setJsonFactory(jsonFactory)
			.setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
			.setServiceAccountScopes(Collections.singleton(STORAGE_SCOPE))
			.setServiceAccountPrivateKeyFromP12File(getTempPkc12File())
			.build();
		} catch (Exception e) {
			Log.e(TAG, "Could not create credential object: " + e.getMessage());
		}
		

		if (credential != null){

			/** Initialise storage API with above credentials*/
			Storage.Builder builder = new Storage.Builder(transport, jsonFactory, credential);
			builder.setApplicationName(APPLICATION_NAME);
			Storage storage = builder.build();


			List<String> uploadFiles = SahaFileManager.getEventFilesForUpload();
			
			Log.d(TAG, "Size of uploads list: "+uploadFiles.size());
			
			Iterator<String> it = uploadFiles.iterator();
			
			while (it.hasNext()){
				String fileLocation =  it.next();
				Log.d(TAG, fileLocation);
				
				InputStream is = null;
				
				try {
					is = new FileInputStream(fileLocation);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				if (is != null){
					InputStreamContent content = new InputStreamContent("application/json", is);
					
					/** Upload file to Cloud Storage bucket */
					try {
						String uniqueFileName  = System.currentTimeMillis() + "-real-data.json";
						Log.d(TAG, "Upload file name: " + uniqueFileName);
						Storage.Objects.Insert insertObject = storage.objects().insert(BUCKET_NAME, null, content);
						insertObject.setName(uniqueFileName);
						insertObject.execute();
						SahaFileManager.deleteUploadedFile(fileLocation);
	
					} catch (IOException e3) {
						e3.printStackTrace();
					}
						
				}
			}
			
		}


	}

	private File getTempPkc12File() throws IOException {

		InputStream pkc12Stream = getResources().openRawResource(R.raw.privatekey);
		File tempPkc12File = File.createTempFile("temp_pkc12_file", "p12");
		FileUtils.copyInputStreamToFile(pkc12Stream, tempPkc12File);
		return tempPkc12File;
	}







}
