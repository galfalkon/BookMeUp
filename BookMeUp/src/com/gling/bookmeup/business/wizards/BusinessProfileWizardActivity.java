package com.gling.bookmeup.business.wizards;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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

import com.gling.bookmeup.R;
import com.gling.bookmeup.business.Business;
import com.gling.bookmeup.business.BusinessMainActivity;
import com.gling.bookmeup.main.ParseHelper;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;
import com.tech.freak.wizardpager.model.AbstractWizardModel;
import com.tech.freak.wizardpager.model.ModelCallbacks;
import com.tech.freak.wizardpager.model.Page;
import com.tech.freak.wizardpager.ui.PageFragmentCallbacks;
import com.tech.freak.wizardpager.ui.ReviewFragment;
import com.tech.freak.wizardpager.ui.StepPagerStrip;

public class BusinessProfileWizardActivity extends FragmentActivity implements
		PageFragmentCallbacks, ReviewFragment.Callbacks, ModelCallbacks {
	private static final String TAG = "BusinessProfileWizardActivity";

	private ViewPager mPager;
	private MyPagerAdapter mPagerAdapter;

	private boolean mEditingAfterReview;

	private AbstractWizardModel mWizardModel = new BusinessProfileWizardModel(
			this);

	private boolean mConsumePageSelectedEvent;

	private Button mNextButton;
	private Button mPrevButton;

	private List<Page> mCurrentPageSequence;
	private StepPagerStrip mStepPagerStrip;

	private Context mContext;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;

		setContentView(R.layout.business_profile_wizard_activity);

		if (savedInstanceState != null) {
			mWizardModel.load(savedInstanceState.getBundle("model"));
		}

		mWizardModel.registerListener(this);

		mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
		mPager = (ViewPager) findViewById(R.id.business_profile_wizard_pager);
		mPager.setAdapter(mPagerAdapter);
		mStepPagerStrip = (StepPagerStrip) findViewById(R.id.business_profile_wizard_strip);
		mStepPagerStrip
				.setOnPageSelectedListener(new StepPagerStrip.OnPageSelectedListener() {
					@Override
					public void onPageStripSelected(int position) {
						position = Math.min(mPagerAdapter.getCount() - 1,
								position);
						if (mPager.getCurrentItem() != position) {
							mPager.setCurrentItem(position);
						}
					}
				});

		mNextButton = (Button) findViewById(R.id.business_profile_wizard_next_button);
		mPrevButton = (Button) findViewById(R.id.business_profile_wizard_prev_button);

		mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				mStepPagerStrip.setCurrentPage(position);

				if (mConsumePageSelectedEvent) {
					mConsumePageSelectedEvent = false;
					return;
				}

				mEditingAfterReview = false;
				updateBottomBar();
			}
		});

		mNextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mPager.getCurrentItem() == mCurrentPageSequence.size()) {
					final ProgressDialog progressDialog = ProgressDialog.show(
							mContext, null, "Please wait...");
					ParseHelper.fetchBusiness(new GetCallback<Business>() {

						@Override
						public void done(Business business, ParseException e) {
							business.setName(mWizardModel
									.findByKey(
											BusinessProfileWizardModel.GENERAL_INFO)
									.getData()
									.getString(
											NameDescriptionPage.NAME_DATA_KEY));

							String description = mWizardModel
									.findByKey(
											BusinessProfileWizardModel.GENERAL_INFO)
									.getData()
									.getString(
											NameDescriptionPage.DESCRIPTION_DATA_KEY);
							if (description != null) {
								business.setDescription(description);
							}

							business.setCategoryByString(mWizardModel
									.findByKey(
											BusinessProfileWizardModel.CATEGORY)
									.getData().getString(Page.SIMPLE_DATA_KEY));

							business.setPhoneNumber(mWizardModel
									.findByKey(
											BusinessProfileWizardModel.DETAILS)
									.getData()
									.getString(
											PhoneOpeningHoursPage.PHONE_DATA_KEY));

							String openingHours = mWizardModel
									.findByKey(
											BusinessProfileWizardModel.DETAILS)
									.getData()
									.getString(
											PhoneOpeningHoursPage.OPENING_HOURS_DATA_KEY);
							if (openingHours != null) {
								business.setOpeningHours(openingHours);
							}

							String imageUri = mWizardModel
									.findByKey(BusinessProfileWizardModel.IMAGE)
									.getData()
									.getString(ParseImagePage.SIMPLE_DATA_KEY);

							if (imageUri != null) {
								business.setImageFile(getScaledImage(imageUri));
							}

							business.saveInBackground(new SaveCallback() {

								@Override
								public void done(ParseException e) {
									progressDialog.dismiss();
									Intent intent = new Intent(mContext,
											BusinessMainActivity.class);
									startActivity(intent);
								}
							});
						}
					});

				} else {
					if (mEditingAfterReview) {
						mPager.setCurrentItem(mPagerAdapter.getCount() - 1);
					} else {
						mPager.setCurrentItem(mPager.getCurrentItem() + 1);
					}
				}
			}
		});

		mPrevButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mPager.setCurrentItem(mPager.getCurrentItem() - 1);
			}
		});

		onPageTreeChanged();
		updateBottomBar();
	}

	@Override
	public void onPageTreeChanged() {
		mCurrentPageSequence = mWizardModel.getCurrentPageSequence();
		recalculateCutOffPage();
		mStepPagerStrip.setPageCount(mCurrentPageSequence.size() + 1); // + 1 =
		// review
		// step
		mPagerAdapter.notifyDataSetChanged();
		updateBottomBar();
	}

	private void updateBottomBar() {
		int position = mPager.getCurrentItem();
		if (position == mCurrentPageSequence.size()) {
			mNextButton.setText("Done");
			mNextButton
					.setBackgroundResource(R.drawable.wizard_finish_background);
			mNextButton.setTextAppearance(this,
					R.style.TextAppearanceWizardFinish);
		} else {
			mNextButton.setText(mEditingAfterReview ? "Review" : "Next");
			mNextButton
					.setBackgroundResource(R.drawable.selectable_item_background);
			TypedValue v = new TypedValue();
			getTheme().resolveAttribute(android.R.attr.textAppearanceMedium, v,
					true);
			mNextButton.setTextAppearance(this, v.resourceId);
			mNextButton.setEnabled(position != mPagerAdapter.getCutOffPage());
		}

		mPrevButton
				.setVisibility(position <= 0 ? View.INVISIBLE : View.VISIBLE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mWizardModel.unregisterListener(this);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBundle("model", mWizardModel.save());
	}

	@Override
	public AbstractWizardModel onGetModel() {
		return mWizardModel;
	}

	@Override
	public void onEditScreenAfterReview(String key) {
		for (int i = mCurrentPageSequence.size() - 1; i >= 0; i--) {
			if (mCurrentPageSequence.get(i).getKey().equals(key)) {
				mConsumePageSelectedEvent = true;
				mEditingAfterReview = true;
				mPager.setCurrentItem(i);
				updateBottomBar();
				break;
			}
		}
	}

	@Override
	public void onPageDataChanged(Page page) {
		if (page.isRequired()) {
			if (recalculateCutOffPage()) {
				mPagerAdapter.notifyDataSetChanged();
				updateBottomBar();
			}
		}
	}

	@Override
	public Page onGetPage(String key) {
		return mWizardModel.findByKey(key);
	}

	private boolean recalculateCutOffPage() {
		// Cut off the pager adapter at first required page that isn't completed
		int cutOffPage = mCurrentPageSequence.size() + 1;
		for (int i = 0; i < mCurrentPageSequence.size(); i++) {
			Page page = mCurrentPageSequence.get(i);
			if (page.isRequired() && !page.isCompleted()) {
				cutOffPage = i;
				break;
			}
		}

		if (mPagerAdapter.getCutOffPage() != cutOffPage) {
			mPagerAdapter.setCutOffPage(cutOffPage);
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
		int index = mPager.getCurrentItem();
		Fragment activeFragment = mPagerAdapter.getFragment(index);
		if (activeFragment != null) {
			activeFragment.onActivityResult(requestCode & 0xFFFF, resultCode,
					data);
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
			if (i >= mCurrentPageSequence.size()) {
				return new ReviewFragment();
			}

			Fragment f = mCurrentPageSequence.get(i).createFragment();
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
		public void setPrimaryItem(ViewGroup container, int position,
				Object object) {
			super.setPrimaryItem(container, position, object);
			mPrimaryItem = (Fragment) object;
		}

		@Override
		public int getCount() {
			return Math.min(mCutOffPage + 1, mCurrentPageSequence == null ? 1
					: mCurrentPageSequence.size() + 1);
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

	private ParseFile getScaledImage(String uri) {
		byte[] data = null;

		try {
			ContentResolver cr = getBaseContext().getContentResolver();
			InputStream inputStream = cr.openInputStream(Uri.parse(uri));
			Bitmap businessImage = BitmapFactory.decodeStream(inputStream);

			// Resize photo
			businessImage = Bitmap.createScaledBitmap(businessImage, 612, 612
					* businessImage.getHeight() / businessImage.getWidth(),
					false);

			// Override Android default landscape orientation and save portrait
			// Matrix matrix = new Matrix();
			// matrix.postRotate(90);
			// Bitmap rotatedScaledMealImage =
			// Bitmap.createBitmap(businessImage,
			// 0, 0, businessImage.getWidth(), businessImage.getHeight(),
			// matrix, true);

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			businessImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			data = bos.toByteArray();
		} catch (FileNotFoundException e) {
			Log.e(TAG, e.getMessage());
		}

		// each ParseFile has a unique identifier separate from the name.
		ParseFile imageFile = new ParseFile("business_image.jpg", data);
		return imageFile;
	}
}
