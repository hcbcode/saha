package com.hcb.saha.internal.ui.fragment;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hcb.saha.R;
import com.hcb.saha.internal.data.db.SahaUserDatabase;
import com.hcb.saha.internal.data.model.User;
import com.hcb.saha.internal.ui.activity.GoogleAccountDialogActivity;

/**
 * Fragment for allowing a user to create a new "account"
 *
 * @author Andreas Borglin
 */
public class UserRegistrationFragment extends RoboFragment {

	public static interface UserCreatedHandler {
		void onUserCreated(User user);
	}

	@InjectView(R.id.namefield)
	private EditText nameField;
	@InjectView(R.id.surnamefield)
	private EditText surnameField;
	@InjectView(R.id.create)
	private Button createButton;
	@InjectView(R.id.attach_google_account)
	private Button attachGoogleAccountButton;
	@InjectView(R.id.attached_account)
	private TextView attachedAccount;

	private String googleAccount;

	private UserCreatedHandler handler;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_user_registration,
				container, false);
		return view;
	}

	public void setUserCreatedHandler(UserCreatedHandler handler) {
		this.handler = handler;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		nameField.requestFocus();
		final InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(nameField, InputMethodManager.SHOW_IMPLICIT);

		attachGoogleAccountButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(getActivity(),
						GoogleAccountDialogActivity.class), 1);
			}
		});

		createButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (nameField.getText().length() < 2
						&& surnameField.getText().length() > 2) {
					Toast.makeText(getActivity(),
							"Name fields must be at least 2 characters",
							Toast.LENGTH_SHORT).show();
					return;
				}

				imm.hideSoftInputFromWindow(nameField.getWindowToken(), 0);
				final User user = User.createUser(nameField.getText()
						.toString(), surnameField.getText().toString(),
						googleAccount);
				long userId = SahaUserDatabase.addUser(user);
				if (userId >= 0) {
					AlertDialog.Builder dialog = new AlertDialog.Builder(
							getActivity());
					dialog.setMessage(R.string.face_reg_message);
					dialog.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									handler.onUserCreated(user);
								}
							});
					dialog.show();

				} else {
					Toast.makeText(getActivity(), "Failed to create user!",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			googleAccount = data.getExtras().getString(
					GoogleAccountDialogActivity.ACCOUNT_KEY);
			if (googleAccount != null) {
				attachGoogleAccountButton.setVisibility(View.GONE);
				attachedAccount.setText(getString(
						R.string.attached_google_account, googleAccount));
				attachedAccount.setVisibility(View.VISIBLE);
			}
		}
	}

}
