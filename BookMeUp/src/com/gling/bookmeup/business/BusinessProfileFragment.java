package com.gling.bookmeup.business;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.gling.bookmeup.R;
import com.gling.bookmeup.login.LoginFragment;
import com.gling.bookmeup.main.FragmentsFlowManager;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.gling.bookmeup.main.ParseHelper;
import com.parse.DeleteCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class BusinessProfileFragment extends OnClickListenerFragment {

    private static final String TAG = "BusinessProfileFragment";

    private EditText edtName, edtDescription, edtPhoneNumber;
    private TextView txtPreviewImage;
    private ParseImageView imgPreviewImage;
    private TextView txtOpeningHours;
    private Button btnOpeningHours;
    private ListView lstServices;
    private Spinner spnCategory;

    private ServicesAdapter _servicesAdapter;

    private Business _business;

    @Override
    protected int getFragmentLayoutId() {
        return R.layout.business_profile_fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        edtName = (EditText) view.findViewById(R.id.business_profile_creation_edtName);
        imgPreviewImage = (ParseImageView) view.findViewById(R.id.business_profile_creation_imgPreviewImage);
        txtPreviewImage = (TextView) view.findViewById(R.id.business_profile_creation_txtPreviewImage);
        edtDescription = (EditText) view.findViewById(R.id.business_profile_creation_edtDescription);
        edtPhoneNumber = (EditText) view.findViewById(R.id.business_profile_creation_edtPhoneNumber);
        txtOpeningHours = (TextView) view.findViewById(R.id.business_profile_creation_txtOpeningHours);
        btnOpeningHours = (Button) view.findViewById(R.id.opening_hours_edit_btnEdit);
        lstServices = (ListView) view.findViewById(R.id.business_profile_creation_lstServices);
        spnCategory = (Spinner) view.findViewById(R.id.business_profile_creation_spnCategory);

        // Until the user has taken a photo, hide the preview
        imgPreviewImage.setVisibility(View.INVISIBLE);

        _business = ((BusinessMainActivity)getActivity()).getBusiness();

        if (savedInstanceState == null) {
            Log.i(TAG, "initProfileDetails");
            initProfileDetails();
        }

        return view;
    }

    private void initProfileDetails() {
        edtName.setText(_business.getName());
        edtDescription.setText(_business.getDescription());
        edtPhoneNumber.setText(_business.getPhoneNumber());
        initOpeningHours();
        initServiceList();
        initCategorySpinner();
        initImage();
    }

    private void initCategorySpinner() {
    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
    	Set<String> categorySet = new HashSet<String>();
    	categorySet = sp.getStringSet(ParseHelper.BUSINESS_CATEGORIES, categorySet);
    	final String[] categoryArr = categorySet.toArray(new String[categorySet.size()]);
    	
    	final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_single_choice, categoryArr);
    	spnCategory.setAdapter(adapter);
    	
    	String category = _business.getCategory();

        int position = 0;
        int count = adapter.getCount();
        for (; position < count; ++position) {
            if (adapter.getItem(position).equalsIgnoreCase(category)) {
            	spnCategory.setSelection(position);
                break;
            }
        }
    }

    private void initOpeningHours() {
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        txtOpeningHours.setText(_business.getOpeningHours());
        btnOpeningHours.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View paramView) {
                AlertDialog dialog = createEditOpeningHoursDialog(inflater);
                dialog.show();
            }
        });
    }

    private AlertDialog createEditOpeningHoursDialog(LayoutInflater inflater) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View dialogView = inflater.inflate(R.layout.business_edit_opening_hours_dialog, null);

        AlertDialog dialog = builder.setTitle("Edit Opening Hours").setView(dialogView).setPositiveButton("Save",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        OpeningHours oh = new OpeningHours(new JSONObject());
                        oh.setDay(
                                OpeningHours.Day.SUNDAY,
                                ((EditText) dialogView.findViewById(R.id.opening_hours_sunday_isOpen)).getText().toString(),
                                ((EditText) dialogView.findViewById(R.id.opening_hours_sunday_from)).getText().toString(),
                                ((EditText) dialogView.findViewById(R.id.opening_hours_sunday_to)).getText().toString());

                        oh.setDay(
                                OpeningHours.Day.MONDAY,
                                ((EditText) dialogView.findViewById(R.id.opening_hours_monday_isOpen)).getText().toString(),
                                ((EditText) dialogView.findViewById(R.id.opening_hours_monday_from)).getText().toString(),
                                ((EditText) dialogView.findViewById(R.id.opening_hours_monday_to)).getText().toString());

                        oh.setDay(
                                OpeningHours.Day.TUESDAY,
                                ((EditText) dialogView.findViewById(R.id.opening_hours_tuesday_isOpen)).getText().toString(),
                                ((EditText) dialogView.findViewById(R.id.opening_hours_tuesday_from)).getText().toString(),
                                ((EditText) dialogView.findViewById(R.id.opening_hours_tuesday_to)).getText().toString());

                        oh.setDay(
                                OpeningHours.Day.WEDNESDAY,
                                ((EditText) dialogView.findViewById(R.id.opening_hours_wednesday_isOpen)).getText().toString(),
                                ((EditText) dialogView.findViewById(R.id.opening_hours_wednesday_from)).getText().toString(),
                                ((EditText) dialogView.findViewById(R.id.opening_hours_wednesday_to)).getText().toString());

                        oh.setDay(
                                OpeningHours.Day.THURSDAY,
                                ((EditText) dialogView.findViewById(R.id.opening_hours_thursday_isOpen)).getText().toString(),
                                ((EditText) dialogView.findViewById(R.id.opening_hours_thursday_from)).getText().toString(),
                                ((EditText) dialogView.findViewById(R.id.opening_hours_thursday_to)).getText().toString());

                        oh.setDay(
                                OpeningHours.Day.FRIDAY,
                                ((EditText) dialogView.findViewById(R.id.opening_hours_friday_isOpen)).getText().toString(),
                                ((EditText) dialogView.findViewById(R.id.opening_hours_friday_from)).getText().toString(),
                                ((EditText) dialogView.findViewById(R.id.opening_hours_friday_to)).getText().toString());

                        oh.setDay(
                                OpeningHours.Day.SATURDAY,
                                ((EditText) dialogView.findViewById(R.id.opening_hours_saturday_isOpen)).getText().toString(),
                                ((EditText) dialogView.findViewById(R.id.opening_hours_saturday_from)).getText().toString(),
                                ((EditText) dialogView.findViewById(R.id.opening_hours_saturday_to)).getText().toString());

                        _business.setOpeningHours(oh);
                        Log.i(TAG, _business.getOpeningHours());
                        txtOpeningHours.setText(_business.getOpeningHours());
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        }).create();

        return dialog;
    }

    private void initServiceList() {

        _servicesAdapter = new ServicesAdapter(getActivity());

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
        lstServices.addHeaderView(servicesHeader);

        lstServices.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> paramAdapterView, View paramView, int position, long id) {
                AlertDialog dialog = createServiceDeleteDialog(position);
                dialog.show();
                return true;
            }
        });

        lstServices.setAdapter(_servicesAdapter);
        _servicesAdapter.loadObjects();
    }

    private AlertDialog createServiceAddDialog(LayoutInflater inflater) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogView = inflater.inflate(R.layout.business_add_service_dialog, null);
        final EditText edtServiceName = (EditText) dialogView.findViewById(R.id.services_add_item_edtName);
        final EditText edtServicePrice = (EditText) dialogView.findViewById(R.id.services_add_item_edtPrice);
        final EditText edtServiceDuration = (EditText) dialogView.findViewById(R.id.services_add_item_edtDuration);

        AlertDialog dialog = builder.setTitle("Add Service").setView(dialogView).setPositiveButton("Add",
                new DialogInterface.OnClickListener() {
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
        AlertDialog dialog = builder.setTitle("Delete Service?").setPositiveButton("Delete",
                new DialogInterface.OnClickListener() {
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

        _business.setUser(currentUser);
        _business.setName(edtName.getText().toString());
        _business.setDescription(edtDescription.getText().toString());
        _business.setPhoneNumber(edtPhoneNumber.getText().toString());
        _business.setCategory(spnCategory.getSelectedItem().toString());

        // If the user added a photo, that data will be added in the
        // BusinessImageCaptureFragment
        // services are edited via list vie
        // opening hours are edited via dialog

        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...");
        _business.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i(TAG, "Done saving business");
                    getActivity().setResult(Activity.RESULT_OK);

                    // jump to home screen
                    ((BusinessMainActivity)getActivity()).onNavigationDrawerItemSelected(0);
                } else {
                    Log.e(TAG, "Exception occurred: " + e.getMessage());
                    Crouton.showText(getActivity(), "Error saving: " + e.getMessage(), Style.ALERT);
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
            imm.hideSoftInputFromWindow(edtName.getWindowToken(), 0);
            break;
        case R.id.business_profile_creation_btnCreate:
            Log.i(TAG, "business_profile_creation_btnCreate clicked");
            if (!validateInput()) {
                Log.i(TAG, "invalid input");
                return;
            }
            if (!userInCache()) {
                Log.i(TAG, "user not found in cache, redirecting to login...");
                Crouton.showText(getActivity(), "Please sign up or log in first...", Style.ALERT);
                getActivity().getFragmentManager().beginTransaction().addToBackStack(null).replace(
                        R.id.container,
                        Fragment.instantiate(getActivity(), LoginFragment.class.getName())).commit();
                return;
            }
            saveBusiness();
            return;
        }
        FragmentsFlowManager.goToNextFragment(getActivity(), R.id.container, v.getId());
    }

    private boolean userInCache() { // TODO move to common
        ParseUser currentUser = ParseUser.getCurrentUser();
        return (currentUser != null);
    }

    private boolean validateInput() {
        return true;
    }

    private void initImage() {
        ParseFile imageFile = _business.getImageFile();
        if (imageFile != null) {
            txtPreviewImage.setText("Image");
            imgPreviewImage.setParseFile(imageFile);
            imgPreviewImage.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    imgPreviewImage.setVisibility(View.VISIBLE);
                }
            });
        } else {
            imgPreviewImage.setVisibility(View.INVISIBLE);
            txtPreviewImage.setText("Please upload an image");
        }
    }

    private class ServicesAdapter extends ParseQueryAdapter<Service> {

        public ServicesAdapter(Context context) {
            super(context, new ParseQueryAdapter.QueryFactory<Service>() {
                public ParseQuery<Service> create() {
                    return _business.getServicesQuery();
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