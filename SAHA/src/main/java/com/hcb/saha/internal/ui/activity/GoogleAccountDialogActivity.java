package com.hcb.saha.internal.ui.activity;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.inject.Inject;
import com.hcb.saha.R;
import com.hcb.saha.external.accounts.AccountEvents;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

@ContentView(R.layout.activity_google_account_dialog)
public class GoogleAccountDialogActivity extends RoboActivity implements
		AccountManagerCallback<Bundle> {

	public static final String ACCOUNT_KEY = "account";

	@InjectView(R.id.account_list)
	private ListView listView;
	@InjectView(R.id.create_new_account)
	private Button createNewAccount;
	@Inject
	private Bus eventBus;


	@Subscribe
	public void onAccountResults(AccountEvents.QueryAccountsResult event) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, event.getNames());
		listView.setAdapter(adapter);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		eventBus.register(this);
		// FIXME this model is broken - should not go via event bus...
		eventBus.post(new AccountEvents.QueryAccountsRequest());
		createNewAccount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				android.accounts.AccountManager.get(
						GoogleAccountDialogActivity.this).addAccount(
						"com.google", "ah", null, null,
						GoogleAccountDialogActivity.this,
						GoogleAccountDialogActivity.this, null);
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int pos,
					long id) {
				String account = (String) adapter.getItemAtPosition(pos);
				Intent result = new Intent();
				result.putExtra(ACCOUNT_KEY, account);
				setResult(RESULT_OK, result);
				finish();
			}
		});

	}

	@Override
	public void run(AccountManagerFuture<Bundle> arg0) {
		// FIXME wire up

	}

}
