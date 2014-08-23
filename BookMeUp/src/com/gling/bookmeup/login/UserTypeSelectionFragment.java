package com.gling.bookmeup.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.gling.bookmeup.R;
import com.gling.bookmeup.business.Business;
import com.gling.bookmeup.business.BusinessMainActivity;
import com.gling.bookmeup.customer.Customer;
import com.gling.bookmeup.customer.CustomerMainActivity;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.parse.ParseException;
import com.parse.ParseUser;

public class UserTypeSelectionFragment extends OnClickListenerFragment implements OnClickListener {
	private static final String TAG = "UserTypeSelectionFragment";
	
	public int getFragmentLayoutId()
	{
		return R.layout.login_user_type_selection_fragment;
	}
	
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		super.onCreateView(inflater, container, savedInstanceState);
//		Log.i(TAG, "onCreateView");
//
//		View rootView = inflater.inflate(R.layout.fragment_user_type_selection,
//				container, false);
//		
//		// Set event listeners
//		rootView.findViewById(R.id.user_type_selection_btnBusiness).setOnClickListener(this);
//		rootView.findViewById(R.id.user_type_selection_btnCustomer).setOnClickListener(this);
//
//		return rootView;
//	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.user_type_selection_btnBusiness:
			Log.i(TAG, "btnBusiness clicked");
	        try {
	            Business business = new Business();
	            business.setUser(ParseUser.getCurrentUser());
	            business.save();
	            
	            Bundle bundle = new Bundle();
	            bundle.putSerializable(Business.CLASS_NAME, business);
                Intent intent = new Intent(getActivity(), BusinessMainActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
	        } catch (ParseException e) {
	            Log.i(TAG, "Business creation failed: " + e.getMessage());
	            e.printStackTrace();
	        }
			break;
		case R.id.user_type_selection_btnCustomer:
			Log.i(TAG, "btnCustomer clicked");
			try {
                Customer customer = new Customer();
                customer.setUser(ParseUser.getCurrentUser());
                customer.save();
                
                Bundle bundle = new Bundle();
                bundle.putSerializable(Customer.CLASS_NAME, customer);
                Intent intent = new Intent(getActivity(), CustomerMainActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            } catch (ParseException e) {
                Log.i(TAG, "Customer creation failed: " + e.getMessage());
                e.printStackTrace();
            }
            break;
		}
	}
}
