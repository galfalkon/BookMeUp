package com.gling.bookmeup.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.gling.bookmeup.R;
import com.gling.bookmeup.fragments.LoginFragment;
import com.gling.bookmeup.main.FragmentsFlowManager;
import com.gling.bookmeup.main.MainActivity;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.gling.bookmeup.main.ParseHelper.BookingsClass;
import com.gling.bookmeup.main.ParseHelper.BusinessClass;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class BusinessProfileCreationFragment extends OnClickListenerFragment {

    private static final String TAG = "BusinessProfileCreationFragment";

    private SimpleExpandableListAdapter _expandableListAdapter;
    Map<String, String> _servicesGroupHeaderData;
    private List<Map<String, String>> _servicesData;

    private EditText edtBusinessName, edtBusinessDescription, edtBusinessOpeningHours;
    private TextView txtPreviewImage;
    private ParseImageView imgBusinessPreviewImage;
    private ExpandableListView lstBusinessServices;

    private static class ExpandableListKeys {
        public static class GroupHeader {
            public final static String HEADER_TITLE = "TITLE";
            public final static String NUM_OF_ITEMS = "NUM_OF_ITEMS";
        }

        public static class GroupItem {
            public final static String NAME = "NAME";
            public final static String PRICE = "PRICE";
            public final static String DURATION = "DURATION";
        }
    }

    @Override
    protected int getFragmentLayoutId() {
        return R.layout.fragment_business_profile_creation;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        edtBusinessName = (EditText) view
                .findViewById(R.id.business_profile_creation_edtBusinessName);
        imgBusinessPreviewImage = (ParseImageView) view
                .findViewById(R.id.business_profile_creation_imgPreviewImage);
        txtPreviewImage = (TextView) view
                .findViewById(R.id.business_profile_creation_txtPreviewImage);
        edtBusinessDescription = (EditText) view
                .findViewById(R.id.business_profile_creation_edtDescription);
        edtBusinessOpeningHours = (EditText) view
                .findViewById(R.id.business_profile_creation_edtOpeningHours);
        lstBusinessServices = (ExpandableListView) view
                .findViewById(R.id.business_profile_creation_lstServices);

        // Until the user has taken a photo, hide the preview
        imgBusinessPreviewImage.setVisibility(View.INVISIBLE);

        // Initialize the group header data
        List<Map<String, String>> groupHeadersData = new ArrayList<Map<String, String>>();

        _servicesGroupHeaderData = new HashMap<String, String>();
        _servicesGroupHeaderData.put(ExpandableListKeys.GroupHeader.HEADER_TITLE, "Services");
        _servicesGroupHeaderData.put(ExpandableListKeys.GroupHeader.NUM_OF_ITEMS, "(0)");
        groupHeadersData.add(_servicesGroupHeaderData);

        // Initialize the group items lists
        List<List<Map<String, String>>> listOfChildGroups = new ArrayList<List<Map<String, String>>>();

        _servicesData = new ArrayList<Map<String, String>>();
        listOfChildGroups.add(_servicesData);

        _expandableListAdapter = new SimpleExpandableListAdapter(getActivity(),

        groupHeadersData, R.layout.expandable_services_list_header, new String[] {
                ExpandableListKeys.GroupHeader.HEADER_TITLE,
                ExpandableListKeys.GroupHeader.NUM_OF_ITEMS }, new int[] {
                R.id.expandable_services_list_header_txtTitle,
                R.id.expandable_services_list_header_txtNumOfItems },

        listOfChildGroups, R.layout.expandable_services_list_item, new String[] {
                ExpandableListKeys.GroupItem.NAME, ExpandableListKeys.GroupItem.PRICE,
                ExpandableListKeys.GroupItem.DURATION }, new int[] {
                R.id.expandable_services_list_item_txtServiceName,
                R.id.expandable_services_list_item_txtPrice,
                R.id.expandable_services_list_item_txtDuration });

        lstBusinessServices.setAdapter(_expandableListAdapter);

        inflateListsWithDetails();

        return view;
    }

    private void inflateListsWithDetails() {
        // Business business = ((MainActivity)
        // getActivity()).getCurrentBusiness();

        // TODO: The businessId should be saved in the shared preferences during
        // the profile creation.
        final String businessId = "btoFQT9CMQ";
        final ParseQuery<Business> query = ParseQuery.getQuery(Business.class).whereEqualTo(
                BusinessClass.Keys.ID, businessId);

        query.findInBackground(new FindCallback<Business>() {
            @Override
            public void done(List<Business> businesses, ParseException e) {
                Log.i(TAG, "Done querying business");
                if (e != null) {
                    Log.e(TAG, "Exception occurred: " + e.getMessage());
                    return;
                }

                _servicesData.clear();

                Services services = businesses.get(0).getServices();
                if (services == null) {
                    Log.i(TAG, "No services");
                    // return;
                    try {
                        services = new Services().putService("Hair Cut", "50", "30 min")
                                .putService("Blow Job", "200", "depends");
                    } catch (JSONException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                }

                Iterator<String> keys = services.keys();
                try {
                    while (keys.hasNext()) {
                        Map<String, String> map = new HashMap<String, String>();
                        String name = keys.next();
                        JSONArray details;

                        details = services.getService(name);
                        map.put(ExpandableListKeys.GroupItem.NAME, name);
                        map.put(ExpandableListKeys.GroupItem.PRICE, details.getString(0));
                        map.put(ExpandableListKeys.GroupItem.DURATION, details.getString(1));

                        _servicesData.add(map);
                    }
                } catch (JSONException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

                _servicesGroupHeaderData.put(ExpandableListKeys.GroupHeader.NUM_OF_ITEMS, "("
                        + String.valueOf(_servicesData.size()) + ")");

                _expandableListAdapter.notifyDataSetChanged();
                lstBusinessServices.expandGroup(0);

                Log.i(TAG, "#Services = " + _servicesData.size());
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.business_profile_creation_btnImageUpload:
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtBusinessName.getWindowToken(), 0);
            break;
        case R.id.business_profile_creation_btnCreate:
            Log.i(TAG, "business_profile_creation_btnCreate clicked");
            if (!validateInput()) {
                Log.i(TAG, "invalid input");
                return;
            }
            if (!userInCache()) {
                Log.i(TAG, "user not found in cache, redirecting to login...");
                Toast.makeText(getActivity(), "Please sign up or log in first...",
                        Toast.LENGTH_SHORT).show();
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.container,
                                Fragment.instantiate(getActivity(), LoginFragment.class.getName()))
                        .commit();
                return;
            }
            try {
                createBusiness();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            break;
        }
        FragmentsFlowManager.goToNextFragment(getActivity(), v.getId());
    }

    private boolean userInCache() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        return (currentUser != null);
    }

    private boolean validateInput() {
        return true;
    }

    private void createBusiness() throws JSONException {
        ParseUser currentUser = ParseUser.getCurrentUser();
        Log.i(TAG, "current user is: " + currentUser.getUsername());

        Business business = ((MainActivity) getActivity()).getCurrentBusiness();

        business.setUser(currentUser);
        business.setName(edtBusinessName.getText().toString());
        business.setDescription(edtBusinessDescription.getText().toString());

        Services services = new Services();
        for (Map<String, String> service : _servicesData) {
            services.putService(
                    service.get(ExpandableListKeys.GroupItem.NAME),
                    service.get(ExpandableListKeys.GroupItem.PRICE),
                    service.get(ExpandableListKeys.GroupItem.DURATION));
        }

        business.setServices(services);

        // If the user added a photo, that data will be
        // added in the BusinessImageCaptureFragment

        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null,
                "Please wait...");
        business.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                progressDialog.dismiss();
                if (e == null) {
                    Log.i(TAG, "Done creating new business");
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                } else {
                    Log.e(TAG, "Exception occurred: " + e.getMessage());
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Error saving: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // private static byte[] getByteArrayFromImageView(ImageView imgView) {
    // Bitmap bitmap = ((BitmapDrawable)imgView.getDrawable()).getBitmap();
    // ByteArrayOutputStream stream = new ByteArrayOutputStream();
    // bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
    // return stream.toByteArray();
    // }

    /*
     * On resume, check and see if a business image has been set from the
     * BusinessImageCaptureFragment. If it has, load the image in this fragment
     * and make the preview image visible.
     */
    @Override
    public void onResume() {
        super.onResume();
        ParseFile imageFile = ((MainActivity) getActivity()).getCurrentBusiness().getImageFile();
        if (imageFile != null) {
            txtPreviewImage.setText("My business image:");
            imgBusinessPreviewImage.setParseFile(imageFile);
            imgBusinessPreviewImage.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    imgBusinessPreviewImage.setVisibility(View.VISIBLE);
                }
            });
        } else {
            imgBusinessPreviewImage.setVisibility(View.INVISIBLE);
            txtPreviewImage.setText("Please upload an image");
        }
    }
}
