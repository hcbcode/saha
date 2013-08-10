package com.hcb.saha.internal.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;

import org.apache.commons.io.IOUtils;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.storage.Storage;
import com.hcb.saha.R;
import android.app.IntentService;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;

public class RemoteStorageService extends IntentService{

	private static final String TAG = RemoteStorageService.class.getSimpleName();

	/** E-mail address of the service account. */
	private static final String SERVICE_ACCOUNT_EMAIL = "537815805809@developer.gserviceaccount.com";

	/** Bucket Name */
	private static final String BUCKET_NAME = "data64";

	/** Global configuration of Google Cloud Storage OAuth 2.0 scope. */
	private static final String STORAGE_SCOPE =
			"https://www.googleapis.com/auth/devstorage.read_write";

	/** GCS Application Name */
	private static final String APPLICATION_NAME = "SAHA";


	public RemoteStorageService() {
		super("RemoteStorageService");

	}

	@Override
	protected void onHandleIntent(Intent intent) {

		Log.d(TAG, "Cloud Service started");

		/** initialise transport and json parser */
		HttpTransport transport = new NetHttpTransport();
		JsonFactory jsonFactory = new JacksonFactory();;

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
			Log.d(TAG, "Could not create credential object: " + e.getMessage());
		}


		if (credential != null){

			/** Initialise storage API with above credentials*/
			Storage.Builder builder = new Storage.Builder(transport, jsonFactory, credential);
			builder.setApplicationName(APPLICATION_NAME);
			Storage storage = builder.build();


			/** temp: Load JSON from file system while events DB not yet built */
			Resources res = getResources();
			InputStream is = res.openRawResource(R.raw.datauf);
			InputStreamContent content = new InputStreamContent("application/json", is);



			/** Upload file to Cloud Storage bucket */
			try {
				String uniqueFileName  = System.currentTimeMillis() + "-data.json";
				Log.d(TAG, "Upload file name: " + uniqueFileName);
				Storage.Objects.Insert insertObject = storage.objects().insert(BUCKET_NAME, null, content);
				insertObject.setName(uniqueFileName);
				insertObject.execute();

				//Retrieve stored object to check its there (temp for now)
				//StorageObject response = storage.objects().get(BUCKET_NAME, uniqueFileName).execute();
				//Log.d("SAHA", "Size of stored object: " + response.getSize());

			} catch (IOException e3) {
				e3.printStackTrace();
			}
		}


	}

	private File getTempPkc12File() throws IOException {

		InputStream pkc12Stream = getResources().openRawResource(R.raw.privatekey);
		File tempPkc12File = File.createTempFile("temp_pkc12_file", "p12");
		OutputStream tempFileStream = new FileOutputStream(tempPkc12File);
		int copied = IOUtils.copy(pkc12Stream, tempFileStream);
		tempFileStream.flush();
		pkc12Stream.close();
		tempFileStream.close();

		if (copied == 0) {
			Log.e(TAG, "Temporary creation of certificate file failed");

		}

		return tempPkc12File;
	}







}
