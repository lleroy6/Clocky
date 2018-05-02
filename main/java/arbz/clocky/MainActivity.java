package arbz.clocky;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

import arbz.clocky.adapters.ViewPagerAdapter;
import arbz.clocky.alarms.AlarmDay;
import arbz.clocky.alarms.AlarmHour;
import arbz.clocky.alarms.AlarmRetriever;
import arbz.clocky.alarms.AlarmSetter;
import arbz.clocky.alarms.UpdateService;
import arbz.clocky.fragments.AlarmConfigFragment;
import arbz.clocky.fragments.AlarmListFragment;
import arbz.clocky.fragments.NetworkFragment;

public class MainActivity extends AppCompatActivity implements AccelerometerManager.AccelerometerListener {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private AlarmConfigFragment mAlarmConfigFragment;
    private AlarmListFragment mAlarmListFragment;
    private int[] mImageResId = {R.drawable.ic_horloge, R.drawable.ic_settings};
    private boolean mAlarmsActivated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = findViewById(R.id.view_pager);
        setViewPager();

        mTabLayout = findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) { setColor(tab, true); }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { setColor(tab, false); }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}

            private void setColor(TabLayout.Tab tab, boolean selected) {
                int color = selected ? R.color.tab_selected : R.color.tab_unselected;
                TextView textTab = tab.getCustomView().findViewById(android.R.id.text1);

                tab.getIcon().setTint(ContextCompat.getColor(MainActivity.this, color));
                textTab.setTextColor(ContextCompat.getColor(MainActivity.this, color));
            }
        });

        setCustomViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AccelerometerManager.isSupported(this)) {
            AccelerometerManager.startListening(this, this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (AccelerometerManager.isListening()) {
            AccelerometerManager.stopListening();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (AccelerometerManager.isListening()) {
            AccelerometerManager.stopListening();
        }
    }

    //création et ajout des fragment à l'adapter
    private void setViewPager() {
        mAlarmListFragment = new AlarmListFragment();
        mAlarmConfigFragment = new AlarmConfigFragment();

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(mAlarmListFragment, "alarmes");
        adapter.addFragment(mAlarmConfigFragment,"paramètres");
        mViewPager.setAdapter(adapter);
    }

    //icônes + textes pour alarmes/paramètres
    private void setCustomViews() {
        for(int i = 0; i < mTabLayout.getTabCount(); i++) {
            int color = ContextCompat.getColor(this, (mTabLayout.getSelectedTabPosition() == i) ? R.color.tab_selected : R.color.tab_unselected);
            Drawable image = getDrawable(mImageResId[i]);
            image.setTint(color);

            View view = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
            ((TextView) view.findViewById(android.R.id.text1)).setTextColor(color);

            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            CharSequence text = tab.getText();

            tab.setCustomView(view);
            tab.setIcon(image);
            tab.setText(text);
        }
    }

    //activation des alarmes
    @Override
    public void onShakeActiveAlarms(float force) {
        mAlarmsActivated = !mAlarmsActivated;
        updateAlarms(mAlarmsActivated);
        Toast.makeText(this, "Alarmes " + (mAlarmsActivated ? "activées" : "désactivées"), Toast.LENGTH_SHORT).show();
    }

    //activation/désactivation des alarmes
    @Override
    public void onShakeUpdateAlarms(float force) {
        NetworkFragment networkFragment = mAlarmListFragment.getNetworkFragment();

        if(networkFragment == null) {
            NetworkFragment.getInstance(getSupportFragmentManager(), this);
        } else {
            networkFragment.startDownload();
        }
    }

    private void updateAlarms(boolean activated) {
        List<AlarmDay> alarmDays = mAlarmListFragment.getAlarmDays();

        for(AlarmDay alarmDay : alarmDays) {
            alarmDay.setChecked(activated);

            for(AlarmHour alarmHour : alarmDay.getChildList()) {
                alarmHour.setChecked(activated);
            }
        }

        AlarmRetriever.writeDate(alarmDays, getSharedPreferences(AlarmConfigFragment.EDITOR_FILENAME, Context.MODE_PRIVATE));

        try {
            AlarmSetter.setAlarms(this);
        } catch (ParseException e) {
            Toast.makeText(this, R.string.parsing_alarms_exception, Toast.LENGTH_SHORT).show();
        }

        mAlarmListFragment.updateAlarms(alarmDays);
    }

    public void updateAlarms() {
        mAlarmListFragment.updateAlarms();
    }
}
