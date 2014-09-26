package com.gling.bookmeup.customer;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gling.bookmeup.R;
import com.gling.bookmeup.business.Business;
import com.gling.bookmeup.business.Service;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

public class CustomerChooseBusinessDialogs {
	
	private static final String TAG = "CustomerMainActivity";
	
	public void createBusinessProfileDialog(final Business business, final Activity activity, 
											final Resources resources, final Customer currentCustomer) {
		LayoutInflater inflater = activity.getLayoutInflater();
		final View dialogView = inflater.inflate(R.layout.customer_business_profile_dialog , null);
		
    	final TextView nameView = (TextView)dialogView.findViewById(R.id.customer_business_profile_dialog_name);
    	nameView.setText(business.getName());
    	
    	final TextView categoryView = (TextView)dialogView.findViewById(R.id.customer_business_profile_dialog_category);
    	if (business.getCategory() != null) {
    		categoryView.setText(business.getCategory().getName());
    	}
    	
    	final TextView descriptionView = (TextView)dialogView.findViewById(R.id.customer_business_profile_dialog_description);
    	descriptionView.setText(business.getDescription());
    	
    	final TextView hoursView = (TextView)dialogView.findViewById(R.id.customer_business_profile_dialog_opening_hours);
    	hoursView.setText(business.getOpeningHours());
    	
    	
    	// Build an alert dialog
    	AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setView(dialogView);
		View titleView = inflater.inflate(R.layout.customer_business_profile_dialog_title, null);
		TextView titleName = (TextView) titleView.findViewById(R.id.customer_business_profile_dialog_title_name);
		titleName.setText(business.getName());
		final ImageView titleIcon = (ImageView) titleView.findViewById(R.id.customer_business_profile_dialog_favourites_button);
//		titleIcon.setImageResource(resources.getDrawable(android.R.drawable.btn_star_big_on));
		boolean favourite = false;
		ArrayList<ParseObject> favouriteBusinesses = (ArrayList<ParseObject>) currentCustomer.get(Customer.Keys.FAVOURITES);
		for (ParseObject parseObject : favouriteBusinesses) {
			if (parseObject instanceof Business) {
				Business businessItem = (Business) parseObject;
				if (business.getObjectId().equals(businessItem.getObjectId())) {
					favourite = true;
					titleIcon.setImageDrawable(resources.getDrawable(android.R.drawable.btn_star_big_on));
					break;
				}
			}
		}
		final boolean finalFavourite = favourite;
		titleIcon.setOnClickListener(new OnClickListener() {
			
			boolean clicked = finalFavourite;
			
			@Override
			public void onClick(View v) {
				if (clicked) {
					titleIcon.setImageDrawable(resources.getDrawable(android.R.drawable.btn_star_big_off));
					ArrayList<ParseObject> businesses = (ArrayList<ParseObject>) currentCustomer.get(Customer.Keys.FAVOURITES);
					ArrayList<ParseObject> toRemove = new ArrayList<ParseObject>();
					for (ParseObject parseObject : businesses) {
						if (parseObject instanceof Business) {
							Business businessItem = (Business) parseObject;
							if (business.getObjectId().equals(businessItem.getObjectId())) {
								toRemove.add(parseObject);
							}
						}
					}
					businesses.removeAll(toRemove);
					currentCustomer.put(Customer.Keys.FAVOURITES, businesses);
					currentCustomer.saveInBackground();
					clicked = false;
				} else {
					ArrayList<ParseObject> businesses = (ArrayList<ParseObject>) currentCustomer.get(Customer.Keys.FAVOURITES);
					if (businesses.size() < 10) {
						titleIcon.setImageDrawable(resources.getDrawable(android.R.drawable.btn_star_big_on));
						boolean exists = false;
						for (ParseObject parseObject : businesses) {
							if (parseObject instanceof Business) {
								Business businessItem = (Business) parseObject;
								if (business.getObjectId().equals(businessItem.getObjectId())) {
									exists = true;
									break;
								}
							}
						}
						if (!exists) {
							businesses.add(business);
						}
						currentCustomer.put(Customer.Keys.FAVOURITES, businesses);
						currentCustomer.saveInBackground();
						clicked = true;
					} else {
						//TODO: pop up error more than 10
					}
				}
			}
		});
//		builder.setTitle(business.getName());
		builder.setCustomTitle(titleView);
		
		builder.setPositiveButton(R.string.customer_business_profile_btnNextTxt, new DialogInterface.OnClickListener() { 
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	Log.i(TAG, "Business was chosen");
		    	createBusinessServicesDialog(business, activity);
		    }
		});
		builder.setNegativeButton(R.string.customer_business_profile_btnCancelTxt, new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.cancel();
		    }
		});
		builder.create().show();
//		builder.show();
	}
	
	private void createBusinessServicesDialog(Business business, final Activity activity){//, ListView servicesListView) {
		LayoutInflater inflater = activity.getLayoutInflater();
		final View servicesView = inflater.inflate(R.layout.customer_business_services_dialog , null);
		ListView servicesListView = (ListView)servicesView.findViewById(R.id.customer_business_services_dialog_listview);
		
		
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setView(servicesView);
		builder.setTitle("gefen");
		
//		_servicesAdapter = new ParseQueryAdapter<Service>(getActivity(),
//				Service.CLASS_NAME);
		ServicesAdapter servicesAdapter = new ServicesAdapter(activity, business);
		servicesAdapter.setTextKey(Service.Keys.NAME);
		Log.e("gefen", ((Boolean)(servicesListView==null)).toString());
		servicesListView.setAdapter(servicesAdapter);
		
		builder.show();
	}
	
	public class ServicesAdapter extends ParseQueryAdapter<ParseObject> {		
		public ServicesAdapter(Context context, final Business business) {
			// Use the QueryFactory to construct a PQA that will only show
			// Todos marked as high-pri
			super(context, new ParseQueryAdapter.QueryFactory<ParseObject>() {
				public ParseQuery create() {
					ParseQuery<Service> query = new ParseQuery<Service>(Service.class);
//					query.whereEqualTo("highPri", true);
					query.whereEqualTo(Service.Keys.BUSINESS, business);
					return query;
				}
			});
		}
	}
}
