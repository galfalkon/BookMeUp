package com.gling.bookmeup.business.wizards.profile;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardExpand;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.ViewToClickToExpand;
import it.gmariotti.cardslib.library.internal.dismissanimation.SwipeDismissAnimation;
import it.gmariotti.cardslib.library.view.CardView;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gling.bookmeup.main.GenericCardArrayAdapter;
import com.gling.bookmeup.main.ICardGenerator;
import com.gling.bookmeup.main.IObservableList;
import com.gling.bookmeup.main.ObservableArrayList;
import com.gling.bookmeup.main.views.BaseListViewWrapperView.DisplayMode;
import com.gling.bookmeup.main.views.CardListViewWrapperView;
import com.gling.bookmeup.sharedlib.R;
import com.gling.bookmeup.sharedlib.parse.Business;
import com.gling.bookmeup.sharedlib.parse.Service;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.SaveCallback;
import com.tech.freak.wizardpager.ui.PageFragmentCallbacks;

public class ServicesFragment extends Fragment {
    private static final String TAG = "ServicesFragment";
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private ServicesPage mPage;

    private IObservableList<Service> _services;
    private GenericCardArrayAdapter<Service> _servicesCardAdapter;
    private CardListViewWrapperView _servicesListViewWrapperView;
    private SwipeDismissAnimation _dismissAnimation;

    private Card _addServiceCard;

    public static ServicesFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        ServicesFragment fragment = new ServicesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ServicesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = (ServicesPage) mCallbacks.onGetPage(mKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.business_profile_wizard_services_fragment,
                                         container,
                                         false);
        ((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());

        _addServiceCard = new Card(getActivity());

        CardHeader header = new CardHeader(getActivity());
        header.setOtherButtonDrawable(R.drawable.card_menu_button_other);
        header.setOtherButtonVisible(true);
        header.setTitle("Add a service");
        header.setOtherButtonClickListener(new CardHeader.OnClickCardHeaderOtherButtonListener() {
            @Override
            public void onButtonItemClick(Card card, View view) {
                card.doToogleExpand();
            }
        });
        _addServiceCard.addCardHeader(header);

        AddServiceExpand expand = new AddServiceExpand(getActivity());
        _addServiceCard.addCardExpand(expand);

        CardView addServiceCardView = (CardView) rootView
                                                         .findViewById(R.id.business_profile_wizard_add_service_card);
        ViewToClickToExpand viewToClickToExpand = ViewToClickToExpand.builder()
                                                                     .setupView(addServiceCardView);
        _addServiceCard.setViewToClickToExpand(viewToClickToExpand);

        addServiceCardView.setCard(_addServiceCard);

        /**** services list ****/

        _services = new ObservableArrayList<Service>();
        _servicesCardAdapter = new GenericCardArrayAdapter<Service>(getActivity(), _services,
                new ServicesCardGenerator());

        _dismissAnimation = (SwipeDismissAnimation) new SwipeDismissAnimation(getActivity())
                                                                                            .setup(_servicesCardAdapter);

        _servicesListViewWrapperView = (CardListViewWrapperView) rootView
                                                                         .findViewById(R.id.business_profile_wizard_services_cardListViewWrapper);
        _servicesListViewWrapperView.setAdapter(_servicesCardAdapter);

        _servicesListViewWrapperView.setDisplayMode(DisplayMode.LOADING_VIEW);

        // TODO maybe fetch services in splash screen?
        Business.getCurrentBusiness()
                .getServicesQuery()
                .findInBackground(new FindCallback<Service>() {
                    @Override
                    public void done(List<Service> services, ParseException e) {
                        Log.i(TAG, "Done querying services, # services: " + services.size());
                        if (e != null) {
                            Log.e(TAG, "Exception: " + e.getMessage());
                            return;
                        }

                        for (Service service : services) {
                            _services.add(service);
                        }

                        DisplayMode newDisplayMode = _services.isEmpty() ? DisplayMode.NO_ITEMS_VIEW
                                : DisplayMode.LIST_VIEW;
                        _servicesListViewWrapperView.setDisplayMode(newDisplayMode);
                    }
                });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof PageFragmentCallbacks)) {
            throw new ClassCastException("Activity must implement PageFragmentCallbacks");
        }

        mCallbacks = (PageFragmentCallbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    private class AddServiceExpand extends CardExpand {

        public AddServiceExpand(Context context) {
            super(context, R.layout.business_profile_wizard_add_service_card_expand);
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, final View view) {
            super.setupInnerViewElements(parent, view);

            Button btnAddService = (Button) view
                                                .findViewById(R.id.business_profile_wizard_services_btnAddService);
            btnAddService.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    _servicesListViewWrapperView.setDisplayMode(DisplayMode.LOADING_VIEW);

                    final EditText edtName = (EditText) view
                                                            .findViewById(R.id.business_profile_wizard_services_edtName);
                    final EditText edtPrice = (EditText) view
                                                             .findViewById(R.id.business_profile_wizard_services_edtPrice);
                    final EditText edtDuration = (EditText) view
                                                                .findViewById(R.id.business_profile_wizard_services_edtDuration);

                    final Service service = new Service();
                    service.setBusiness(Business.getCurrentBusiness())
                           .setName(edtName.getText().toString())
                           .setPrice(Integer.valueOf(edtPrice.getText().toString()))
                           .setDuration(Integer.valueOf(edtDuration.getText().toString()));

                    service.saveInBackground(new SaveCallback() {

                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "saving service failed " + e.getMessage());
                                // TODO crouton
                                return;
                            }
                            // TODO prepend the service so it appears on the top
                            // of the list
                            _services.add(0, service);

                            _addServiceCard.doToogleExpand();
                            edtName.setText("");
                            edtPrice.setText("");
                            edtDuration.setText("");

                            _servicesListViewWrapperView.setDisplayMode(DisplayMode.LIST_VIEW);
                        }
                    });
                }
            });
        }
    }

    // TODO maybe pass the service to the card in the constructor, and do all
    // the logic there
    private class ServicesCardGenerator implements ICardGenerator<Service> {
        @Override
        public Card generateCard(final Service service) {

            CardHeader.OnClickCardHeaderOtherButtonListener dissmissCallback = new CardHeader.OnClickCardHeaderOtherButtonListener() {
                @Override
                public void onButtonItemClick(Card card, View view) {
                    _dismissAnimation.animateDismiss(card);
                    // TODO show an "undo" crouton
                    service.deleteInBackground();
                }
            };

            return new BusinessProfileWizardServiceCard(getActivity(), service.getName(),
                    service.getPrice(), service.getDuration(), dissmissCallback);
        }
    }
}
