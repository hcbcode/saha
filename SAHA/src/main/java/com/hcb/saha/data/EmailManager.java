package com.hcb.saha.data;

import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;

import com.google.inject.Inject;
import com.hcb.saha.event.EmailEvents;
import com.hcb.saha.event.EmailEvents.QueryEmailResult;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * 
 * @author steven hadley
 * 
 *         Only supports monitoring one email address.
 * 
 */
public class EmailManager {

	private Bus eventBus;
	private EmailEvents.QueryEmailRequest cachedEvent;

	static final String[] COLUMNS_TO_SHOW = new String[] {
			GmailContract.Labels.NUM_CONVERSATIONS,
			GmailContract.Labels.NUM_UNREAD_CONVERSATIONS, };

	@Inject
	public EmailManager(Bus eventBus) {
		this.eventBus = eventBus;
		eventBus.register(this);
	}

	@Subscribe
	public void queryEmails(EmailEvents.QueryEmailRequest email) {

		Cursor labelsCursor = email
				.getContext()
				.getContentResolver()
				.query(GmailContract.Labels.getLabelsUri(email.getName()),
						COLUMNS_TO_SHOW, null, null, null);

		Integer unread = null;
		if (labelsCursor.moveToFirst()) {
			unread = labelsCursor
					.getInt(labelsCursor
							.getColumnIndex(GmailContract.Labels.NUM_UNREAD_CONVERSATIONS));

		}

		// relies on old cursor being garbage collected and the observer
		// becoming unreachable and hence garbage collected.
		labelsCursor.registerContentObserver(new Observer(null));

		eventBus.post(new QueryEmailResult(unread));

		cachedEvent = email;
	}

	private class Observer extends ContentObserver {

		public Observer(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			queryEmails(cachedEvent);
		}

		@Override
		public boolean deliverSelfNotifications() {
			return true;
		}
	}

}
