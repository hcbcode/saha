package com.hcb.saha.fragment;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import android.content.Context;
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
import com.hcb.saha.data.SahaUserDatabase;
import com.hcb.saha.data.model.User;
import com.hcb.saha.event.RegistrationEvents;
import com.squareup.otto.Bus;

/**
 * Fragment for allowing a user to create a new "account"
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_registration, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        createButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
					      Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(nameField.getWindowToken(), 0);
				User user = new User();
				user.setName(nameField.getText().toString());
				user.setDirectory(nameField.getText().toString().toLowerCase().trim());
				long userId = SahaUserDatabase.addUser(getActivity(), user);
				if (userId >= 0) {
					eventBus.post(new RegistrationEvents.UserCreated(user));
				}
				else {
					Toast.makeText(getActivity(), "Failed to create user!", Toast.LENGTH_SHORT).show();
				}
			}
		});
    }
	
}
