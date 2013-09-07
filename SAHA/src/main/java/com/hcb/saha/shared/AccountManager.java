package com.hcb.saha.shared;


/**
 * Interface for account management
 *
 * @author Andreas Borglin
 */
public interface AccountManager {

	public interface AccountCallback {
		void onAccountsResults(String[] accounts);
	}

	void getGoogleAccounts(AccountCallback callback);

}
