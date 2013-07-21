package com.hcb.saha.data;

import android.accounts.Account;

public interface PhoneAccountObserver {

	void onReadAccounts(Account[] accounts);

}
