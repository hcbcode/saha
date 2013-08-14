package com.hcb.saha.internal.ui.fragment.widget;

import roboguice.inject.InjectView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.inject.Inject;
import com.hcb.saha.R;
import com.hcb.saha.external.AccountEvents;
import com.hcb.saha.external.EmailEvents;
import com.hcb.saha.external.EmailEvents.QueryEmailRequest;
import com.hcb.saha.external.EmailManager;
import com.hcb.saha.internal.core.SahaSystemState;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * 
 * @author Steven Hadley
 * 
 */
public class UserFragment extends WidgetFragment {

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

		user.setText("User: " + systemState.getCurrentUser().getName());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		eventBus.register(this);
		return getView(getArguments().getInt(STATE_TYPE), container, inflater);
	}

	@Override
	public int getFullLayout() {
		return R.layout.fragment_widget_user_full;
	}

	@Override
	public int getCompressedLayout() {
		return R.layout.fragment_widget_user_compressed;
	}

	public static Fragment create(StateType state) {
		Fragment fragment = new UserFragment();
		WidgetFragment.addBundle(state, fragment);
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
		// FIXME: Just picking first one should not be from here but from user
		// object
		eventBus.post(new QueryEmailRequest(accounts.getNames()[0], this
				.getActivity()));
		emailAddress.setText(accounts.getNames()[0]);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		eventBus.unregister(this);
	}

}
