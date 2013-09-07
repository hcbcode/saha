package com.hcb.saha.internal.ui.fragment.widget;

import roboguice.inject.InjectView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.inject.Inject;
import com.hcb.saha.R;
import com.hcb.saha.external.accounts.AccountEvents;
import com.hcb.saha.external.email.EmailEvents;
import com.hcb.saha.external.email.EmailEvents.QueryEmailRequest;
import com.hcb.saha.external.email.EmailManager;
import com.hcb.saha.internal.core.SahaSystemState;
import com.hcb.saha.internal.data.model.User;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * User specific data. In this case email.
 *
 * @author Steven Hadley
 *
 */
public class UserFragment extends BaseWidgetFragment {

	@Inject
	private Bus eventBus;
	@InjectView(R.id.email_address_text)
	private TextView emailAddress;
	@InjectView(R.id.email_unread_count)
	private TextView emailUnreadCount;
	@InjectView(R.id.user)
	private TextView user;
	@Inject
	private EmailManager emailManager;
	@Inject
	private SahaSystemState systemState;

	public UserFragment() {
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// FIXME: This should not be done here
		eventBus.post(new AccountEvents.QueryAccountsRequest());

		user.setText("User: " + systemState.getCurrentUser().toString());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		eventBus.register(this);
		return getView(getArguments().getString(STATE_TYPE), container,
				inflater);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EmailManager.startGmailClient(getActivity());
			}
		});
	}

	@Override
	public int getFullLayout() {
		return R.layout.fragment_widget_user_full;
	}

	@Override
	public int getCompressedLayout() {
		return R.layout.fragment_widget_user_compressed;
	}

	/**
	 * Constructs the fragment with the required parameters.
	 * 
	 * @param state
	 * @return fragment
	 */
	public static Fragment create(StateType state) {
		Fragment fragment = new UserFragment();
		BaseWidgetFragment.addBundle(state, fragment);
		return fragment;
	}

	@Subscribe
	public void emailUnreadCountAvailable(
			final EmailEvents.QueryEmailResult email) {
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				emailUnreadCount.setText(email.getUnreadCount() + " "
						+ getString(R.string.email_unread));
			}
		});
	}

	@Subscribe
	public void onAccountsQueried(AccountEvents.QueryAccountsResult accounts) {
		User user = systemState.getCurrentUser();
		String googleAccount = null;
		if (user != null && user.getGoogleAccount() != null) {
			googleAccount = user.getGoogleAccount();
		} else if (accounts.getNames().length > 0) {
			googleAccount = accounts.getNames()[0];
		}

		if (googleAccount != null) {
			eventBus.post(new QueryEmailRequest(googleAccount, this
				.getActivity()));
			emailAddress.setText(googleAccount);
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		eventBus.unregister(this);
	}

}
