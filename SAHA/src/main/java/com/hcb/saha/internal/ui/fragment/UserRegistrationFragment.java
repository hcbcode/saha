package com.hcb.saha.internal.ui.fragment;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.inject.Inject;
import com.hcb.saha.R;
import com.hcb.saha.internal.data.db.SahaUserDatabase;
import com.hcb.saha.internal.data.model.User;
import com.squareup.otto.Bus;

/**
 * Fragment for allowing a user to create a new "account"
 * 
 * @author Andreas Borglin
 */
public class UserRegistrationFragment extends RoboFragment {

	@InjectView(R.id.namefield)
	private EditText nameField;
	@InjectView(R.id.create)
	private Button createButton;
	@Inject
	private Bus eventBus;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_user_registration,
				container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		nameField.requestFocus();
		final InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(nameField, InputMethodManager.SHOW_IMPLICIT);

		createButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (nameField.getText().length() < 2) {
					Toast.makeText(getActivity(),
							"Name must be at least 2 characters",
							Toast.LENGTH_SHORT).show();
					return;
				}

				imm.hideSoftInputFromWindow(nameField.getWindowToken(), 0);
				final User user = new User();
				user.setName(nameField.getText().toString());
				user.setDirectory(nameField.getText().toString().toLowerCase()
						.trim());
				long userId = SahaUserDatabase.addUser(getActivity(), user);
				if (userId >= 0) {
					AlertDialog.Builder dialog = new AlertDialog.Builder(
							getActivity());
					dialog.setMessage(R.string.face_reg_message);
					dialog.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// eventBus.post(new
									// RegistrationEvents.UserCreated(user));
									// FIXME
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

}
