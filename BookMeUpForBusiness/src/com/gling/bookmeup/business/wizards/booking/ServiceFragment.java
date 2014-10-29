package com.gling.bookmeup.business.wizards.booking;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.gling.bookmeup.sharedlib.parse.Business;
import com.gling.bookmeup.sharedlib.parse.Service;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.tech.freak.wizardpager.R;
import com.tech.freak.wizardpager.ui.PageFragmentCallbacks;

public class ServiceFragment extends ListFragment {

    private static final String TAG = "ServiceFragment";
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks _callbacks;
    private String _key;
    private ServicePage _page;

    public static ServiceFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        ServiceFragment fragment = new ServiceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        _key = args.getString(ARG_KEY);
        _page = (ServicePage) _callbacks.onGetPage(_key);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page, container, false);
        ((TextView) rootView.findViewById(android.R.id.title)).setText(_page.getTitle());

        final ListView listView = (ListView) rootView.findViewById(android.R.id.list);

        Business.getCurrentBusiness().getServices(new FindCallback<Service>() {

            @Override
            public void done(List<Service> services, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Exception: " + e.getMessage());
                    return;
                }
                
                if (isAdded()) {
                    setListAdapter(new ServicesAdapter(getActivity(),
                                                       android.R.layout.simple_list_item_single_choice, android.R.id.text1,
                                                       services));
                }
            }
        });

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof PageFragmentCallbacks)) {
            throw new ClassCastException("Activity must implement PageFragmentCallbacks");
        }

        _callbacks = (PageFragmentCallbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        _callbacks = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Service service = ((Service) getListAdapter().getItem(position));
        _page.getData().putString(ServicePage.SERVICE_NAME,
                                  service.getName());
        _page.getData().putString(ServicePage.SERVICE_ID,
                                  service.getObjectId());
        _page.notifyDataChanged();
    }

    private class ServicesAdapter extends ArrayAdapter<Service> {

        public ServicesAdapter(Context context, int resource, int textViewResourceId,
                List<Service> objects) {
            super(context, resource, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            Service service = getItem(position);
            if (service != null) {
                textView.setText(service.getName());
            }
            return view;
        }
    }

}
