package arbz.clocky;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

import arbz.clocky.alarms.UpdateService;
import arbz.clocky.fragments.AlarmConfigFragment;

public class WelcomeActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private MyViewPagerAdapter mViewPagerAdapter;
    private SharedPreferences mSharedPreferences;
    private LinearLayout mDotsLayout;
    private Button mBtnNext;
    private TextView[] mDots;
    private int[] mLayouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mSharedPreferences = getSharedPreferences(AlarmConfigFragment.EDITOR_FILENAME, MODE_PRIVATE);

        if(!mSharedPreferences.getBoolean(AlarmConfigFragment.EDITOR_FIRST_LAUNCH, true)) {
            launchHomeScreen(true);
            finish();
        }

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        mViewPager = findViewById(R.id.welcome_view_pager);
        mDotsLayout = findViewById(R.id.layoutDots);
        mBtnNext = findViewById(R.id.welcome_btn_next);

        mLayouts = new int[]{
                R.layout.welcome_slide1,
                R.layout.welcome_slide2,
                R.layout.welcome_slide3,
                R.layout.welcome_slide4,
                R.layout.welcome_slide5};

        addBottomDots(0);
        changeStatusBarColor();

        mViewPagerAdapter = new MyViewPagerAdapter();
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int current = getItem(+1);
                if (current < mLayouts.length) {
                    mViewPager.setCurrentItem(current);
                } else {
                    launchHomeScreen(false);
                }
            }
        });

    }

    private void addBottomDots(int currentPage) {
        mDots = new TextView[mLayouts.length];

        int colorActive = getResources().getColor(R.color.dot_active);
        int colorsInactive = getResources().getColor(R.color.dot_inactive);

        mDotsLayout.removeAllViews();
        for (int i = 0; i < mDots.length; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(colorsInactive);
            mDotsLayout.addView(mDots[i]);
        }

        if (mDots.length > 0)
            mDots[currentPage].setTextColor(colorActive);
    }

    private int getItem(int i) {
        return mViewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen(boolean skip) {
        if(!skip) {
            EditText editText = findViewById(R.id.welc_editText);

            if(!editText.getText().toString().equals("")) {
                mSharedPreferences.edit()
                        .putBoolean(AlarmConfigFragment.EDITOR_FIRST_LAUNCH, false)
                        .putString(AlarmConfigFragment.EDITOR_ICS_LINK_KEY, editText.getText().toString())
                        .apply();

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, 21);
                cal.set(Calendar.MINUTE, 0);

                Intent majService = new Intent(this, UpdateService.class);
                PendingIntent pendingIntent = PendingIntent.getService(this, 0, majService, 0);
                AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
                manager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                launchHomeScreen(true);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Vous devez rentrez une URL !");
                builder.create().show();
            }
        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            if (position == mLayouts.length - 1) {
                mBtnNext.setText(getString(R.string.btn_start));
            } else {
                mBtnNext.setText(getString(R.string.btn_next
                ));
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {  }

        @Override
        public void onPageScrollStateChanged(int arg0) {}
    };

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private class MyViewPagerAdapter extends PagerAdapter implements Animation.AnimationListener {
        private LayoutInflater layoutInflater;
        private RotateAnimation mRotateAnimation;
        private ImageView mHammersImageView;
        private float mStartAngle = -10;
        private float mStopAngle = 10;
        private float mPivotXValue = 0.5f;
        private float mPivotYValue = 0.55f;
        private int mDuration = 10;

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(mLayouts[position], container, false);
            container.addView(view);

            if(position == 0 || position == mLayouts.length - 1) {
                final MyViewPagerAdapter instance = this;

                final ImageView mHourImageView = view.findViewById(R.id.ic_launcher_hour);
                final ImageView mMinuteImageView = view.findViewById(R.id.ic_launcher_minute);
                mHammersImageView = view.findViewById(R.id.ic_launcher_hammers);

                mHourImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RotateAnimation hourAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.55f);
                        hourAnimation.setDuration(1000);
                        mHourImageView.startAnimation(hourAnimation);

                        RotateAnimation minuteAnimation = new RotateAnimation(0, 360 * 2, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.54f);
                        minuteAnimation.setDuration(1000);
                        minuteAnimation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {}

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                mStartAngle = -10;
                                mStopAngle = 10;
                                mDuration = 10;

                                mRotateAnimation = new RotateAnimation(mStartAngle, mStopAngle, Animation.RELATIVE_TO_SELF, mPivotXValue, Animation.RELATIVE_TO_SELF, mPivotYValue);
                                mRotateAnimation.setDuration(mDuration);
                                mRotateAnimation.setAnimationListener(instance);
                                mHammersImageView.startAnimation(mRotateAnimation);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {}
                        });
                        mMinuteImageView.startAnimation(minuteAnimation);
                    }
                });

                mHourImageView.callOnClick();
            }
            return view;
        }

        @Override
        public void onAnimationStart(Animation animation) {}

        @Override
        public void onAnimationEnd(Animation animation) {
            int duration = 50;

            mStartAngle *= -1;
            mStopAngle *= -1;

            if(mDuration < 100) {
                mDuration += 10;

                mRotateAnimation = new RotateAnimation(mStartAngle, mStopAngle, Animation.RELATIVE_TO_SELF, mPivotXValue, Animation.RELATIVE_TO_SELF, mPivotYValue);
                mRotateAnimation.setAnimationListener(this);
            } else if (mDuration < 200){
                float delta = mStartAngle > 0 ? - 1 : 1;
                mStartAngle += delta;
                mStopAngle -= delta;

                mDuration += 10;

                duration = mDuration;

                mRotateAnimation = new RotateAnimation(mStartAngle, mStopAngle, Animation.RELATIVE_TO_SELF, mPivotXValue, Animation.RELATIVE_TO_SELF, mPivotYValue);
                mRotateAnimation.setAnimationListener(this);
            } else {
                mStartAngle *= 0.5;
                mStopAngle *= 0.5;

                duration = mDuration;
                mRotateAnimation = new RotateAnimation(mStartAngle, mStopAngle, Animation.RELATIVE_TO_SELF, mPivotXValue, Animation.RELATIVE_TO_SELF, mPivotYValue);

                if(mStartAngle != 0 && mStopAngle != 0)
                    mRotateAnimation.setAnimationListener(this);
            }

            mRotateAnimation.setDuration(duration);
            mHammersImageView.startAnimation(mRotateAnimation);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {}

        @Override
        public int getCount() {
            return mLayouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
