package com.hcb.saha.external.accounts;

import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.Application;
import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hcb.saha.external.accounts.AccountEvents.QueryAccountsRequest;
import com.hcb.saha.external.accounts.AccountEvents.QueryAccountsResult;
import com.hcb.saha.shared.AccountManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * Handles app wide account management
 *
 * FIXME This is broken - comms here should not be via req/resp over event bus
 *
 * @author Steven Hadley
 */
@Singleton
public class AccountManagerImpl implements AccountManager {

	private static final String TAG = AccountManagerImpl.class.getSimpleName();

	private Bus eventBus;
	@Inject
	private Application context;

	@Inject
	public AccountManagerImpl(Bus eventBus) {
		this.eventBus = eventBus;
		eventBus.register(this);
	}

	@Subscribe
	public void queryAccounts(QueryAccountsRequest query) {
		getGoogleAccounts();
	}

	/**
	 * This could easily be made to query FB, LinkedIn etc.
	 *
	 * @param ctx
	 * @param observer
	 */
	private void getGoogleAccounts() {

		Log.d(TAG, "Get accounts");

		final String ACCOUNT_TYPE_GOOGLE = "com.google";
		final String[] FEATURES_MAIL = { "service_mail" };

		android.accounts.AccountManager.get(context)
				.getAccountsByTypeAndFeatures(
				ACCOUNT_TYPE_GOOGLE, FEATURES_MAIL,
				new AccountManagerCallback<Account[]>() {
					@Override
					public void run(AccountManagerFuture<Account[]> future) {
						Account[] accounts = null;
						try {
							accounts = future.getResult();
						} catch (OperationCanceledException oce) {
							Log.e(TAG, "Got OperationCanceledException", oce);
						} catch (IOException ioe) {
							Log.e(TAG, "Got OperationCanceledException", ioe);
						} catch (AuthenticatorException ae) {
							Log.e(TAG, "Got OperationCanceledException", ae);
						}
						onAccountResults(accounts);
					}
				}, null);
	}

	private void onAccountResults(Account[] accounts) {
		String[] accnts = new String[accounts.length];
		int i = 0;
		for (Account acc : accounts) {
			accnts[i] = acc.name;
			i++;
		}
		eventBus.post(new QueryAccountsResult(accnts));
	}

	@Override
	public void addGoogleAccount(Activity activity) {
		// FIXME
		// android.accounts.AccountManager.get(context).addAccount("com.google",
		// "ah", null, null, activity, new AccountManagerCallback<Bundle>() {
		// } , handler)

	}
}
