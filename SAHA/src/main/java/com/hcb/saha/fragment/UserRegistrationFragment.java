package com.hcb.saha.fragment;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.hcb.saha.R;
import com.hcb.saha.data.SahaUserDatabase;
import com.hcb.saha.data.model.User;

public class UserRegistrationFragment extends RoboFragment {
	
	@InjectView(R.id.namefield)
	private EditText nameField;
	@InjectView(R.id.create)
	private Button createButton;

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
				User user = new User();
				user.setName(nameField.getText().toString());
				user.setDirectory(nameField.getText().toString().toLowerCase().trim());
				SahaUserDatabase.addUser(getActivity(), user);
			}
		});
    }
	
}
