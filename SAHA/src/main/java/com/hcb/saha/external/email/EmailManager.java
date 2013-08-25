package com.hcb.saha.external.email;

import java.util.concurrent.locks.ReentrantLock;

import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hcb.saha.external.email.EmailEvents.QueryEmailResult;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * 
 * Only supports monitoring one email address
 * 
 * @author steven hadley
 */
@Singleton
public class EmailManager {

	private Bus eventBus;
	private EmailEvents.QueryEmailRequest cachedEvent;

	/**
	 * Used to stop race conditions.
	 */
	private ReentrantLock lock = new ReentrantLock();

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
		lock.lock();

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
		Observer observer = new Observer(null);
		observer.setName(email.getName());
		labelsCursor.registerContentObserver(observer);

		eventBus.post(new QueryEmailResult(unread));

		cachedEvent = email;

		lock.unlock();

	}

	/**
	 * Monitor content changes to email.
	 * 
	 * @author steven hadley
	 * 
	 */
	private class Observer extends ContentObserver {

		private String name;

		public Observer(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			lock.lock();
			// make sure the email account being displayed has not changed.
			if (name.equals(cachedEvent.getName())) {
				queryEmails(cachedEvent);
			}
			lock.unlock();
		}

		public void setName(String name) {
			this.name = name;
		}
	}

}
