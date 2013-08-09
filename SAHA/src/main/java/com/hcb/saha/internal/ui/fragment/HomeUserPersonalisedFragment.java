package com.hcb.saha.internal.ui.fragment;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import android.os.Bundle;
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
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class HomeUserPersonalisedFragment extends RoboFragment {

	@Inject
	private Bus eventBus;

	@InjectView(R.id.email_address_text)
	private TextView emailAddress;
	@InjectView(R.id.email_unread_count)
	private TextView emailUnreadCount;

	@Inject
	private EmailManager emailManager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_home_user_personalised,
				container, false);
		eventBus.register(this);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// FIXME: This should not be done here
		eventBus.post(new AccountEvents.QueryAccountsRequest(this.getActivity()));

	}

	@Subscribe
	public void emailUnreadCountAvailable(
			final EmailEvents.QueryEmailResult email) {
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				emailUnreadCount.setText(email.getUnreadCount() + " "
						+ "Unread");
			}
		});
	}

	@Subscribe
	public void onAccountsQueried(AccountEvents.QueryAccountsResult accounts) {
		// FIXME: Just picking first one
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
