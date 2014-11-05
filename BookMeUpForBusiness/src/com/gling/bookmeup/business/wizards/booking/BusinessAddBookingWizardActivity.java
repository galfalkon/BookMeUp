package com.gling.bookmeup.business.wizards.booking;

import java.util.List;

import org.joda.time.DateTime;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.gling.bookmeup.business.BusinessCalendarActivity;
import com.gling.bookmeup.business.R;
import com.gling.bookmeup.sharedlib.parse.Business;
import com.gling.bookmeup.sharedlib.parse.Customer;
import com.gling.bookmeup.sharedlib.parse.ParseHelper.Booking;
import com.gling.bookmeup.sharedlib.parse.Service;
import com.parse.ParseException;
import com.tech.freak.wizardpager.model.AbstractWizardModel;
import com.tech.freak.wizardpager.model.ModelCallbacks;
import com.tech.freak.wizardpager.model.Page;
import com.tech.freak.wizardpager.ui.PageFragmentCallbacks;
import com.tech.freak.wizardpager.ui.ReviewFragment;
import com.tech.freak.wizardpager.ui.StepPagerStrip;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class BusinessAddBookingWizardActivity extends FragmentActivity implements
        PageFragmentCallbacks, ReviewFragment.Callbacks, ModelCallbacks {

    private static final String TAG = "BusinessAddBookingWizardActivity";

    private ViewPager _pager;
    private MyPagerAdapter _pagerAdapter;

    private boolean _editingAfterReview;

    private static final String MODEL = "model";
    private AbstractWizardModel _wizardModel;

    private boolean _consumePageSelectedEvent;

    private Button _nextButton;
    private Button _prevButton;

    private List<Page> _currentPageSequence;
    private StepPagerStrip _stepPagerStrip;

    private Activity _context;

    private void addBooking() {
        final ProgressDialog progressDialog = ProgressDialog
                                                            .show(_context,
                                                                  null,
                                                                  getApplicationContext().getString(R.string.progress_dialog_please_wait));

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                String customerType = _wizardModel.findByKey(BusinessAddBookingWizardModel.CUSTOMER_TYPE)
                                                  .getData()
                                                  .getString(Page.SIMPLE_DATA_KEY);

                Customer customer = new Customer();

                if (customerType.equals(BusinessAddBookingWizardModel.REGULAR_CUSTOMER)) {
                    String regularCustomerPageKey = BusinessAddBookingWizardModel.REGULAR_CUSTOMER
                            + ":" + BusinessAddBookingWizardModel.REGULAR_CUSTOMER;
                    String customerId = _wizardModel.findByKey(regularCustomerPageKey)
                                                    .getData()
                                                    .getString(RegularCustomerPage.CUSTOMER_ID);
                    customer.setObjectId(customerId);
                } else if (customerType.equals(BusinessAddBookingWizardModel.NEW_CUSTOMER)) {
                    String newCustomerPageKey = BusinessAddBookingWizardModel.NEW_CUSTOMER + ":"
                            + BusinessAddBookingWizardModel.NEW_CUSTOMER;
                    String customerName = _wizardModel.findByKey(newCustomerPageKey)
                                                      .getData()
                                                      .getString(NewCustomerPage.CUSTOMER_NAME);
                    String customerPhoneNumber = _wizardModel.findByKey(newCustomerPageKey)
                                                             .getData()
                                                             .getString(NewCustomerPage.CUSTOMER_PHONE_NUMBER);

                    customer.setName(customerName);
                    customer.setPhoneNumber(customerPhoneNumber);
                } else {
                    return null;
                }

                String serviceId = _wizardModel.findByKey(BusinessAddBookingWizardModel.SERVICE)
                                               .getData()
                                               .getString(ServicePage.SERVICE_ID);
                Service service = new Service();
                service.setObjectId(serviceId);
                DateTime date = (DateTime) _wizardModel.findByKey(BusinessAddBookingWizardModel.DATE)
                                                       .getData()
                                                       .getSerializable(DatePage.SIMPLE_DATA_KEY);

                Booking booking = new Booking();
                booking.setBusiness(Business.getCurrentBusiness())
                       .setCustomer(customer)
                       .setService(service)
                       .setDate(date.toDate())
                       .setStatus(Booking.Status.APPROVED);

                try {
                    booking.save();
                    Intent returnIntent = new Intent();
                    setResult(RESULT_CANCELED, returnIntent);
                    finish();
                } catch (ParseException e) {
                    Log.e(TAG, "Failed creating booking " + e.getMessage());
                    _context.runOnUiThread(new Runnable() {
                        public void run() {
                            Crouton.showText(_context,
                                             R.string.generic_exception_message,
                                             Style.ALERT);
                        }
                    });
                }
                
                progressDialog.dismiss();
                return null;
            }

        }.execute();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _context = this;

        _wizardModel = new BusinessAddBookingWizardModel(this);
        DateTime date = (DateTime) getIntent().getExtras()
                                              .getSerializable(BusinessCalendarActivity.EXTRA_DATE);
        ((DatePage) _wizardModel.findByKey(BusinessAddBookingWizardModel.DATE)).setValue(date);

        setContentView(R.layout.business_profile_wizard_activity);

        if (savedInstanceState != null) {
            _wizardModel.load(savedInstanceState.getBundle(MODEL));
        }

        _wizardModel.registerListener(this);

        _pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        _pager = (ViewPager) findViewById(R.id.business_profile_wizard_pager);
        _pager.setAdapter(_pagerAdapter);

        _stepPagerStrip = (StepPagerStrip) findViewById(R.id.business_profile_wizard_strip);
        _stepPagerStrip.setOnPageSelectedListener(new StepPagerStrip.OnPageSelectedListener() {
            @Override
            public void onPageStripSelected(int position) {
                position = Math.min(_pagerAdapter.getCount() - 1, position);
                if (_pager.getCurrentItem() != position) {
                    _pager.setCurrentItem(position);
                }
            }
        });

        _nextButton = (Button) findViewById(R.id.business_profile_wizard_next_button);
        _prevButton = (Button) findViewById(R.id.business_profile_wizard_prev_button);

        _pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                _stepPagerStrip.setCurrentPage(position);

                if (_consumePageSelectedEvent) {
                    _consumePageSelectedEvent = false;
                    return;
                }

                _editingAfterReview = false;
                updateBottomBar();
            }
        });

        _nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_pager.getCurrentItem() == _currentPageSequence.size()) {
                    addBooking();
                } else {
                    if (_editingAfterReview) {
                        _pager.setCurrentItem(_pagerAdapter.getCount() - 1);
                    } else {
                        _pager.setCurrentItem(_pager.getCurrentItem() + 1);
                    }
                }
            }

        });

        _prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _pager.setCurrentItem(_pager.getCurrentItem() - 1);
            }
        });

        onPageTreeChanged();
        updateBottomBar();
    }

    @Override
    public void onPageTreeChanged() {
        _currentPageSequence = _wizardModel.getCurrentPageSequence();
        recalculateCutOffPage();
        _stepPagerStrip.setPageCount(_currentPageSequence.size() + 1); // + 1 =
        // review
        // step
        _pagerAdapter.notifyDataSetChanged();
        updateBottomBar();
    }

    private void updateBottomBar() {
        int position = _pager.getCurrentItem();
        if (position == _currentPageSequence.size()) {
            _nextButton.setText("Done");
            _nextButton.setBackgroundResource(R.drawable.wizard_finish_background);
            _nextButton.setTextAppearance(this, R.style.TextAppearanceWizardFinish);
        } else {
            _nextButton.setText(_editingAfterReview ? "Review" : "Next");
            _nextButton.setBackgroundResource(R.drawable.selectable_item_background);
            TypedValue v = new TypedValue();
            getTheme().resolveAttribute(android.R.attr.textAppearanceMedium, v, true);
            _nextButton.setTextAppearance(this, v.resourceId);
            _nextButton.setEnabled(position != _pagerAdapter.getCutOffPage());
        }

        _prevButton.setVisibility(position <= 0 ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        _wizardModel.unregisterListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(MODEL, _wizardModel.save());
    }

    @Override
    public AbstractWizardModel onGetModel() {
        return _wizardModel;
    }

    @Override
    public void onEditScreenAfterReview(String key) {
        for (int i = _currentPageSequence.size() - 1; i >= 0; i--) {
            if (_currentPageSequence.get(i).getKey().equals(key)) {
                _consumePageSelectedEvent = true;
                _editingAfterReview = true;
                _pager.setCurrentItem(i);
                updateBottomBar();
                break;
            }
        }
    }

    @Override
    public void onPageDataChanged(Page page) {
        if (page.isRequired()) {
            if (recalculateCutOffPage()) {
                _pagerAdapter.notifyDataSetChanged();
                updateBottomBar();
            }
        }
    }

    @Override
    public Page onGetPage(String key) {
        return _wizardModel.findByKey(key);
    }

    private boolean recalculateCutOffPage() {
        // Cut off the pager adapter at first required page that isn't completed
        int cutOffPage = _currentPageSequence.size() + 1;
        for (int i = 0; i < _currentPageSequence.size(); i++) {
            Page page = _currentPageSequence.get(i);
            if (page.isRequired() && !page.isCompleted()) {
                cutOffPage = i;
                break;
            }
        }

        if (_pagerAdapter.getCutOffPage() != cutOffPage) {
            _pagerAdapter.setCutOffPage(cutOffPage);
            return true;
        }

        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // crazy hack
        // http://blog.shamanland.com/2014/01/nested-fragments-for-result.html
        // http://androidprofessionals.blogspot.co.il/2013/06/get-current-visible-fragment-page-in.html
        super.onActivityResult(requestCode, resultCode, data);
        int index = _pager.getCurrentItem();
        Fragment activeFragment = _pagerAdapter.getFragment(index);
        if (activeFragment != null) {
            activeFragment.onActivityResult(requestCode & 0xFFFF, resultCode, data);
        }
    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {
        private int mCutOffPage;
        private Fragment mPrimaryItem;
        private SparseArray<Fragment> mPageReferenceMap;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
            mPageReferenceMap = new SparseArray<Fragment>();
        }

        @Override
        public Fragment getItem(int i) {
            if (i >= _currentPageSequence.size()) {
                return new ReviewFragment();
            }

            Fragment f = _currentPageSequence.get(i).createFragment();
            mPageReferenceMap.put(i, f);
            return f;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            mPageReferenceMap.remove(position);
        }

        public Fragment getFragment(int key) {
            return mPageReferenceMap.get(key);
        }

        @Override
        public int getItemPosition(Object object) {
            // TODO: be smarter about this
            if (object == mPrimaryItem) {
                // Re-use the current fragment (its position never changes)
                return POSITION_UNCHANGED;
            }

            return POSITION_NONE;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            mPrimaryItem = (Fragment) object;
        }

        @Override
        public int getCount() {
            return Math.min(mCutOffPage + 1, _currentPageSequence == null ? 1
                    : _currentPageSequence.size() + 1);
        }

        public void setCutOffPage(int cutOffPage) {
            if (cutOffPage < 0) {
                cutOffPage = Integer.MAX_VALUE;
            }
            mCutOffPage = cutOffPage;
        }

        public int getCutOffPage() {
            return mCutOffPage;
        }
    }
}
