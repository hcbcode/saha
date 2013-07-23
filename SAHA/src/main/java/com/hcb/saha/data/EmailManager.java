package com.hcb.saha.data;

import android.database.Cursor;

import com.google.inject.Inject;
import com.hcb.saha.event.EmailEvents;
import com.hcb.saha.event.EmailEvents.QueryEmailResult;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * 
 * @author steven hadley
 *
 */
public class EmailManager {

	private Bus eventBus;

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
		// FIXME: Use loader or something else dynamic.
		if (labelsCursor.moveToFirst()) {
			unread = labelsCursor
					.getInt(labelsCursor
							.getColumnIndex(GmailContract.Labels.NUM_UNREAD_CONVERSATIONS));

		}
		eventBus.post(new QueryEmailResult(unread));
	}
}
