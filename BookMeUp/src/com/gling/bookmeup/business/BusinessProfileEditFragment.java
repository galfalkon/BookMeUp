package com.gling.bookmeup.business;

import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gling.bookmeup.R;
import com.gling.bookmeup.login.LoginFragment;
import com.gling.bookmeup.login.LoginMainActivity;
import com.gling.bookmeup.main.FragmentsFlowManager;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.gling.bookmeup.main.ParseHelper;
import com.gling.bookmeup.main.ParseHelper.Category;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseQueryAdapter.OnQueryLoadListener;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class BusinessProfileEditFragment extends OnClickListenerFragment {

    private static final String TAG = "BusinessProfileCreationFragment";
    public final static String EXTRA_BUSINESS = "com.gling.bookmeup.EXTRA_BUSINESS";

    private EditText edtBusinessName, edtBusinessDescription;
    private TextView txtPreviewImage;
    private ParseImageView imgBusinessPreviewImage;
    private TextView txtBusinessOpeningHours;
    private Button btnOpeningHoursEdit;
    private ListView lstBusinessServices;
    private Spinner spnCategory;

    private ServicesAdapter _servicesAdapter;

    @Override
    protected int getFragmentLayoutId() {
        return R.layout.business_profile_fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        edtBusinessName = (EditText) view.findViewById(R.id.business_profile_creation_edtBusinessName);
        imgBusinessPreviewImage = (ParseImageView) view.findViewById(R.id.business_profile_creation_imgPreviewImage);
        txtPreviewImage = (TextView) view.findViewById(R.id.business_profile_creation_txtPreviewImage);
        edtBusinessDescription = (EditText) view.findViewById(R.id.business_profile_creation_edtDescription);
        txtBusinessOpeningHours = (TextView) view.findViewById(R.id.business_profile_creation_txtOpeningHours);
        btnOpeningHoursEdit = (Button) view.findViewById(R.id.opening_hours_edit_btnEdit);
        lstBusinessServices = (ListView) view.findViewById(R.id.business_profile_creation_lstServices);
        spnCategory = (Spinner) view.findViewById(R.id.business_profile_creation_spnCategory);

        // Until the user has taken a photo, hide the preview
        imgBusinessPreviewImage.setVisibility(View.INVISIBLE);

        initProfileDetails();

        return view;
    }

    private void initProfileDetails() {
        Business business = ((BusinessMainActivity) getActivity()).getBusiness();
        
        edtBusinessName.setText(business.getName());
        // image is loaded onResume
        edtBusinessDescription.setText(business.getDescription());
        initOpeningHours(business);
        initServiceList(business);
        initCategorySpinner(business);
    }

    private void initCategorySpinner(final Business business) {
        final ParseQueryAdapter<ParseObject> adapter = new ParseQueryAdapter<ParseObject>(getActivity(),
                ParseHelper.Category.CLASS_NAME);
        adapter.setTextKey(ParseHelper.Category.Keys.NAME);
        adapter.addOnQueryLoadListener(new OnQueryLoadListener<ParseObject>() {
            public void onLoading() {
              // Trigger any "loading" UI
            }
          
            public void onLoaded(final List<ParseObject> categories, Exception paramException) {
                ParseObject category = business.getCategory();
                if (category == null) {
                    spnCategory.setSelection(0);
                    return;
                }
                
                category.fetchIfNeededInBackground( new GetCallback<ParseObject>() {

                    @Override
                    public void done(ParseObject category, ParseException e) {
                        if (e == null) {
                            String categoryName = category.getString(Category.Keys.NAME);
                            int position = 0;
                            for (int i = 0; i < categories.size(); i++) {
                                if (categories.get(i).getString(Category.Keys.NAME).equalsIgnoreCase(categoryName)) {
                                    position = i;
                                    break;
                                }
                            }

                            spnCategory.setSelection(position);
                        } else {
                            Log.e(TAG, "Exception: " + e.getMessage());
                        }
                    }
                });
            }
          });

        spnCategory.setAdapter(adapter);
    }

    private void initOpeningHours(final Business business) {
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        txtBusinessOpeningHours.setText(business.getOpeningHours());
        btnOpeningHoursEdit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View paramView) {
                AlertDialog dialog = createEditOpeningHoursDialog(inflater, business);
                dialog.show();
            }
        });
    }

    private AlertDialog createEditOpeningHoursDialog(LayoutInflater inflater, final Business business) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View dialogView = inflater.inflate(R.layout.business_edit_opening_hours_dialog, null);

        AlertDialog dialog = builder.setTitle("Edit Opening Hours").setView(dialogView)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        OpeningHours oh = new OpeningHours(new JSONObject());
                        oh.setDay(OpeningHours.Day.SUNDAY, ((EditText) dialogView
                                .findViewById(R.id.opening_hours_sunday_isOpen)).getText().toString(),
                                ((EditText) dialogView.findViewById(R.id.opening_hours_sunday_from)).getText()
                                        .toString(), ((EditText) dialogView.findViewById(R.id.opening_hours_sunday_to))
                                        .getText().toString());

                        oh.setDay(OpeningHours.Day.MONDAY, ((EditText) dialogView
                                .findViewById(R.id.opening_hours_monday_isOpen)).getText().toString(),
                                ((EditText) dialogView.findViewById(R.id.opening_hours_monday_from)).getText()
                                        .toString(), ((EditText) dialogView.findViewById(R.id.opening_hours_monday_to))
                                        .getText().toString());

                        oh.setDay(OpeningHours.Day.TUESDAY, ((EditText) dialogView
                                .findViewById(R.id.opening_hours_tuesday_isOpen)).getText().toString(),
                                ((EditText) dialogView.findViewById(R.id.opening_hours_tuesday_from)).getText()
                                        .toString(),
                                ((EditText) dialogView.findViewById(R.id.opening_hours_tuesday_to)).getText()
                                        .toString());

                        oh.setDay(OpeningHours.Day.WEDNESDAY, ((EditText) dialogView
                                .findViewById(R.id.opening_hours_wednesday_isOpen)).getText().toString(),
                                ((EditText) dialogView.findViewById(R.id.opening_hours_wednesday_from)).getText()
                                        .toString(), ((EditText) dialogView
                                        .findViewById(R.id.opening_hours_wednesday_to)).getText().toString());

                        oh.setDay(OpeningHours.Day.THURSDAY, ((EditText) dialogView
                                .findViewById(R.id.opening_hours_thursday_isOpen)).getText().toString(),
                                ((EditText) dialogView.findViewById(R.id.opening_hours_thursday_from)).getText()
                                        .toString(), ((EditText) dialogView
                                        .findViewById(R.id.opening_hours_thursday_to)).getText().toString());

                        oh.setDay(OpeningHours.Day.FRIDAY, ((EditText) dialogView
                                .findViewById(R.id.opening_hours_friday_isOpen)).getText().toString(),
                                ((EditText) dialogView.findViewById(R.id.opening_hours_friday_from)).getText()
                                        .toString(), ((EditText) dialogView.findViewById(R.id.opening_hours_friday_to))
                                        .getText().toString());

                        oh.setDay(OpeningHours.Day.SATURDAY, ((EditText) dialogView
                                .findViewById(R.id.opening_hours_saturday_isOpen)).getText().toString(),
                                ((EditText) dialogView.findViewById(R.id.opening_hours_saturday_from)).getText()
                                        .toString(), ((EditText) dialogView
                                        .findViewById(R.id.opening_hours_saturday_to)).getText().toString());

                        business.setOpeningHours(oh);
                        Log.i(TAG, business.getOpeningHours());
                        txtBusinessOpeningHours.setText(business.getOpeningHours());
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                }).create();

        return dialog;
    }

    private void initServiceList(Business business) {

        _servicesAdapter = new ServicesAdapter(getActivity(), business);

        final LayoutInflater inflater = getActivity().getLayoutInflater();

        // add a header with 'add' button for the services list
        LinearLayout servicesHeader = (LinearLayout) inflater.inflate(R.layout.business_service_list_header, null);
        Button btnAddService = (Button) servicesHeader.findViewById(R.id.services_list_header_btnAdd);
        btnAddService.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View paramView) {
                AlertDialog dialog = createServiceAddDialog(inflater);
                dialog.show();
            }
        });
        lstBusinessServices.addHeaderView(servicesHeader);

        lstBusinessServices.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> paramAdapterView, View paramView, int position, long id) {
                AlertDialog dialog = createServiceDeleteDialog(position);
                dialog.show();
                return true;
            }
        });

        lstBusinessServices.setAdapter(_servicesAdapter);
        _servicesAdapter.loadObjects();
    }

    private AlertDialog createServiceAddDialog(LayoutInflater inflater) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogView = inflater.inflate(R.layout.business_add_service_dialog, null);
        final EditText edtServiceName = (EditText) dialogView.findViewById(R.id.services_add_item_edtName);
        final EditText edtServicePrice = (EditText) dialogView.findViewById(R.id.services_add_item_edtPrice);
        final EditText edtServiceDuration = (EditText) dialogView.findViewById(R.id.services_add_item_edtDuration);

        AlertDialog dialog = builder.setTitle("Add Service").setView(dialogView)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Service service = new Service();
                        service.setName(edtServiceName.getText().toString());
                        service.setPrice(edtServicePrice.getText().toString());
                        service.setDuration(edtServiceDuration.getText().toString());
                        //service.setBusiness(((LoginActivity) getActivity()).getCurrentBusiness());

                        service.saveInBackground(new SaveCallback() {

                            @Override
                            public void done(ParseException e) {
                                _servicesAdapter.loadObjects();
                            }
                        });
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                }).create();

        return dialog;
    }

    private AlertDialog createServiceDeleteDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog dialog = builder.setTitle("Delete Service?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO add service to temp list of modified services,
                        // delete on pause or somthing
                        // TODO feature: parse - call 'eventually' methods
                        _servicesAdapter.getItem(position - 1).deleteInBackground(new DeleteCallback() {

                            @Override
                            public void done(ParseException paramParseException) {
                                _servicesAdapter.loadObjects();
                            }
                        });
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                }).create();

        return dialog;
    }

    private void saveBusiness() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        Log.i(TAG, "current user is: " + currentUser.getUsername());

        //final Business business = ((LoginActivity) getActivity()).getCurrentBusiness();
        final Business business = new Business();

        business.setUser(currentUser);
        business.setName(edtBusinessName.getText().toString());
        business.setDescription(edtBusinessDescription.getText().toString());
        business.setCategory((ParseObject) spnCategory.getSelectedItem());

        // If the user added a photo, that data will be added in the
        // BusinessImageCaptureFragment
        // services are edited via list vie
        // opening hours are edited via dialog

        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...");
        business.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i(TAG, "Done creating new business");
                    getActivity().setResult(Activity.RESULT_OK);
                    
                    // jump to business main activity
                    Intent intent = new Intent(getActivity(), BusinessMainActivity.class);
                    startActivity(intent);
                } else {
                    Log.e(TAG, "Exception occurred: " + e.getMessage());
                    Toast.makeText(getActivity().getApplicationContext(), "Error saving: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.business_profile_creation_btnImageUpload:
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
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
                Toast.makeText(getActivity(), "Please sign up or log in first...", Toast.LENGTH_SHORT).show();
                getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null)
                        .replace(R.id.container, Fragment.instantiate(getActivity(), LoginFragment.class.getName()))
                        .commit();
                return;
            }
            saveBusiness();
            return;
        }
        FragmentsFlowManager.goToNextFragment(getActivity(), v.getId());
    }

    private boolean userInCache() { // TODO move to common
        ParseUser currentUser = ParseUser.getCurrentUser();
        return (currentUser != null);
    }

    private boolean validateInput() {
        return true;
    }

    /*
     * On resume, check and see if a business image has been set from the
     * BusinessImageCaptureFragment. If it has, load the image in this fragment
     * and make the preview image visible.
     */
    @Override
    public void onResume() {
        super.onResume();

        Log.i(TAG, "On Resume");

        Business business = ((BusinessMainActivity) getActivity()).getBusiness();
        
        ParseFile imageFile = business.getImageFile();
        if (imageFile != null) {
            txtPreviewImage.setText("Image");
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

    private class ServicesAdapter extends ParseQueryAdapter<Service> {

        public ServicesAdapter(Context context, final Business business) {
            super(context, new ParseQueryAdapter.QueryFactory<Service>() {
                public ParseQuery<Service> create() {
                    return business.getServicesQuery();
                }
            });
        }

        @Override
        public View getItemView(Service service, View v, ViewGroup parent) {
            if (v == null) {
                v = View.inflate(getContext(), R.layout.business_service_list_item, null);
            }

            super.getItemView(service, v, parent);

            TextView txtServiceName = (TextView) v.findViewById(R.id.services_list_item_txtServiceName);
            txtServiceName.setText(service.getName());

            TextView txtServicePrice = (TextView) v.findViewById(R.id.services_list_item_txtPrice);
            txtServicePrice.setText(service.getPrice());

            TextView txtServiceDuration = (TextView) v.findViewById(R.id.services_list_item_txtDuration);
            txtServiceDuration.setText(service.getDuration());

            return v;
        }

    }

}

// private static byte[] getByteArrayFromImageView(ImageView imgView) {
// Bitmap bitmap = ((BitmapDrawable)imgView.getDrawable()).getBitmap();
// ByteArrayOutputStream stream = new ByteArrayOutputStream();
// bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
// return stream.toByteArray();
// }
