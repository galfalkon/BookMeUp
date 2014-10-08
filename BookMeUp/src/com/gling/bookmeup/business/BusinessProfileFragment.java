package com.gling.bookmeup.business;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.gling.bookmeup.R;
import com.gling.bookmeup.business.wizards.BusinessProfileWizardActivity;
import com.gling.bookmeup.main.FragmentsFlowManager;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.SaveCallback;

public class BusinessProfileFragment extends OnClickListenerFragment {

    private static final String TAG = "BusinessProfileFragment";

    private Button btnLaunchWizard;
    private ListView lstServices;
    private ServicesAdapter _servicesAdapter;
    
    @Override
    protected int getFragmentLayoutId() {
        return R.layout.business_profile_fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        btnLaunchWizard = (Button) view.findViewById(R.id.business_profile_wizard_launch);
        lstServices = (ListView) view.findViewById(R.id.business_profile_creation_lstServices);
        
        if (savedInstanceState == null) {
            Log.i(TAG, "initProfileDetails");
            initProfileDetails();
        }
        
        btnLaunchWizard.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), BusinessProfileWizardActivity.class);
				startActivity(intent);
			}
		});

        return view;
    }

    private void initProfileDetails() {
        initServiceList();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.business_profile_wizard_launch:

            break;
        }
        FragmentsFlowManager.goToNextFragment(getActivity(), R.id.container, v.getId());
    }

    private class ServicesAdapter extends ParseQueryAdapter<Service> {

        public ServicesAdapter(Context context) {
            super(context, new ParseQueryAdapter.QueryFactory<Service>() {
                public ParseQuery<Service> create() {
                    return Business.getCurrentBusiness().getServicesQuery();
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