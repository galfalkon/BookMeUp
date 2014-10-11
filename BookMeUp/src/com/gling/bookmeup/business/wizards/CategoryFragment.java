package com.gling.bookmeup.business.wizards;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.gling.bookmeup.business.Business;
import com.gling.bookmeup.main.ParseHelper;
import com.gling.bookmeup.main.ParseHelper.Category;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.tech.freak.wizardpager.R;
import com.tech.freak.wizardpager.model.Page;
import com.tech.freak.wizardpager.ui.SingleChoiceFragment;

public class CategoryFragment extends SingleChoiceFragment {
	
	public static CategoryFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        CategoryFragment fragment = new CategoryFragment();
        fragment.setArguments(args);
        return fragment;
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page, container, false);
        ((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());

        final ListView listView = (ListView) rootView.findViewById(android.R.id.list);
        setListAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_single_choice,
                android.R.id.text1,
                mChoices));
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        final String category = mPage.getData().getString(Page.SIMPLE_DATA_KEY);
        // Pre-select currently selected item.
        if (!TextUtils.isEmpty(category)) {
        	new Handler().post(new Runnable() {
                @Override
                public void run() {
                    
                    for (int i = 0; i < mChoices.size(); i++) {
                        if (mChoices.get(i).equals(category)) {
                            listView.setItemChecked(i, true);
                            break;
                        }
                    }
                }
            });
		} else {
			ParseHelper.fetchBusiness(new GetCallback<Business>() {

				@Override
				public void done(Business business, ParseException e) {
					String categoryString = "";
					if (business != null) {
						Category category = business.getCategory();
						if (category != null) {						
							categoryString = business.getCategory().getName();
						}
					}
					final String finalCategoryString = categoryString;					
					mPage.getData().putString(CategoryPage.SIMPLE_DATA_KEY, finalCategoryString);
					mPage.notifyDataChanged();
				}
			});
		} 
        
        return rootView;
    }

}