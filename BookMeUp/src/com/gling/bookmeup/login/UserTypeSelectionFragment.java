package com.gling.bookmeup.login;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.gling.bookmeup.R;
import com.gling.bookmeup.business.BusinessMainActivity;
import com.gling.bookmeup.business.wizards.BusinessProfileWizardActivity;
import com.gling.bookmeup.customer.CustomerMainActivity;
import com.gling.bookmeup.customer.CustomerProfileCreationActivity;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.gling.bookmeup.sharedlib.parse.Business;
import com.gling.bookmeup.sharedlib.parse.Customer;
import com.gling.bookmeup.sharedlib.parse.ParseHelper;
import com.parse.ParseUser;

public class UserTypeSelectionFragment extends OnClickListenerFragment implements OnClickListener 
{
    private static final String TAG = "UserTypeSelectionFragment";

    public int getFragmentLayoutId() 
    {
        return R.layout.login_user_type_selection_fragment;
    }

    @Override
    public void onClick(View v) 
    {
        ParseUser currentUser = ParseUser.getCurrentUser();

        switch (v.getId()) {
        case R.id.user_type_selection_btnBusiness:
            Log.i(TAG, "btnBusiness clicked");
            
            Business currentBusiness = Business.getCurrentBusiness();
            if (currentBusiness == null)
            {
            	currentBusiness = new Business();
            	Business.setCurrentBusiness(currentBusiness);
            	currentBusiness.saveInBackground();
            	
                currentUser.put(ParseHelper.User.Keys.BUSINESS_POINTER, currentBusiness);
                currentUser.saveInBackground();
            }
            
            if (TextUtils.isEmpty(currentBusiness.getName())) 
        	{
        		startActivity(BusinessProfileWizardActivity.class);
			}
            else 
			{
				startActivity(BusinessMainActivity.class);
			}
            
            break;
        case R.id.user_type_selection_btnCustomer:
            Log.i(TAG, "btnCustomer clicked");
            
            Customer currentCustomer = Customer.getCurrentCustomer();
            if (currentCustomer == null)
            {
            	currentCustomer = new Customer();
            	Customer.setCurrentCustomer(currentCustomer);
            	currentCustomer.saveInBackground();
            	
            	currentUser.put(ParseHelper.User.Keys.CUSTOMER_POINTER, currentCustomer);
            	currentUser.saveInBackground();
            }
            
            if (TextUtils.isEmpty(currentCustomer.getPhoneNumber())) 
        	{
        		startActivity(CustomerProfileCreationActivity.class);
			}
            else 
			{
				startActivity(CustomerMainActivity.class);
			}
            
            break;
        }
    }
    
    private void startActivity(Class<? extends Activity> activity)
    {
    	startActivity(new Intent(getActivity(), activity));
    }
}