package com.hcb.saha.data;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Arrays;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.util.Log;

public class PhoneAccountManager {

	private static final String TAG = PhoneAccountManager.class.getSimpleName();

	/**
	 * This could easily be made to query FB, LinkedIn etc.
	 * 
	 * @param ctx
	 * @param observer
	 */
	public static void getGoogleAccounts(Context ctx,
			final WeakReference<PhoneAccountObserver> observer) {

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
						onAccountResults(accounts, observer);
					}
				}, null /* handler */);
	}

	private static void onAccountResults(Account[] accounts,
			WeakReference<PhoneAccountObserver> observer) {
		Log.i("TestApp", "received accounts: " + Arrays.toString(accounts));
		if (null != observer.get()) {
			observer.get().onReadAccounts(accounts);
		}
	}

}
