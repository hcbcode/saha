package com.hcb.saha.external;

import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.util.Log;

import com.google.inject.Inject;
import com.hcb.saha.external.AccountEvents.QueryAccountsRequest;
import com.hcb.saha.external.AccountEvents.QueryAccountsResult;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * 
 * @author steven hadley
 * 
 */
public class AccountsManager {

	private static final String TAG = AccountsManager.class.getSimpleName();

	private Bus eventBus;

	@Inject
	public AccountsManager(Bus eventBus) {
		this.eventBus = eventBus;
		eventBus.register(this);
	}

	@Subscribe
	public void queryAccounts(QueryAccountsRequest query) {
		getGoogleAccounts(query.getContext());
	}

	/**
	 * This could easily be made to query FB, LinkedIn etc.
	 * 
	 * @param ctx
	 * @param observer
	 */
	private void getGoogleAccounts(final Context ctx) {

		final String ACCOUNT_TYPE_GOOGLE = "com.google";
		final String[] FEATURES_MAIL = { "service_mail" };

		AccountManager.get(ctx).getAccountsByTypeAndFeatures(
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
						onAccountResults(accounts, ctx);
					}
				}, null);
	}

	private void onAccountResults(Account[] accounts, Context ctx) {
		String[] accnts = new String[accounts.length];
		int i = 0;
		for (Account acc : accounts) {
			accnts[i] = acc.name;
			i++;
		}
		eventBus.post(new QueryAccountsResult(accnts, ctx));
	}
}
