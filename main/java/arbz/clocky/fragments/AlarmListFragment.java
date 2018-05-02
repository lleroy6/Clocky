package arbz.clocky.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

import arbz.clocky.R;
import arbz.clocky.adapters.AlarmExpandableAdapter;
import arbz.clocky.alarms.AlarmDay;
import arbz.clocky.alarms.AlarmHour;
import arbz.clocky.alarms.AlarmRetriever;
import arbz.clocky.alarms.AlarmSetter;


public class AlarmListFragment extends Fragment implements AlarmExpandableAdapter.OnSwitchChangedListener {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AlarmExpandableAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private List<AlarmDay> mAlarmDays;
    private NetworkFragment mNetworkFragment;
    private int lastExpandedPos = -1;

    public AlarmListFragment() {}

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        //création des alarmes
        mAlarmDays = retrieveAlarms();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm_list, container, false);

        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(mNetworkFragment == null) {
                    mNetworkFragment = NetworkFragment.getInstance(getFragmentManager(), getContext());
                } else {
                    mNetworkFragment.startDownload();
                }

                updateAlarms();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        mAdapter = new AlarmExpandableAdapter(getContext(), this, mAlarmDays);
        mAdapter.setExpandCollapseListener(new ExpandableRecyclerAdapter.ExpandCollapseListener() {
            @Override
            public void onParentExpanded(int parentPosition) {
                //collapse le groupe déjà étendu si sélection d'un autre
                if(lastExpandedPos != -1 && parentPosition != lastExpandedPos) {
                    mAdapter.collapseParent(lastExpandedPos);
                }
                lastExpandedPos = parentPosition;
            }

            @Override
            public void onParentCollapsed(int parentPosition) {
            }
        });

        //custom animator pour éviter le clignotement lors du rafraîchissement d'un item
        DefaultItemAnimator itemAnimator = new DefaultItemAnimator() {
            @Override
            public boolean canReuseUpdatedViewHolder(@NonNull RecyclerView.ViewHolder viewHolder) { return true; }
        };

        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(itemAnimator);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }


    public void updateAlarms() {
        updateAlarms(retrieveAlarms());
    }

    public void updateAlarms(List<AlarmDay> alarmDays) {
        mAlarmDays = alarmDays;
        mAdapter.setParentList(mAlarmDays, true);
    }

    private List<AlarmDay> retrieveAlarms() {
        List<AlarmDay> alarmDays = null;
        try {
            alarmDays = AlarmRetriever.getData(getActivity().getSharedPreferences(AlarmConfigFragment.EDITOR_FILENAME, Context.MODE_PRIVATE));
        } catch (ParseException e) {
            Toast.makeText(getContext(), R.string.parsing_alarms_exception, Toast.LENGTH_SHORT).show();
        }

        return alarmDays;
    }

    @Override
    public void onDayBoxChecked(boolean value, int dayPosition) {
        mAlarmDays = mAdapter.getParentList();
        SharedPreferences.Editor editor = getContext().getSharedPreferences(AlarmConfigFragment.EDITOR_FILENAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(AlarmDay.ACTIVATED + (dayPosition + Calendar.MONDAY), value);
        editor.commit();
    }

    @Override
    public void onAlarmSwitchChanged(boolean value, int dayPosition, int hourPosition) {
        mAlarmDays = mAdapter.getParentList();

        SharedPreferences.Editor editor = getContext().getSharedPreferences(AlarmConfigFragment.EDITOR_FILENAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(AlarmHour.ALARM + (dayPosition + Calendar.MONDAY) + hourPosition, value);
        editor.commit();

        AlarmDay alarmDay = mAlarmDays.get(dayPosition);
        AlarmHour alarmHour = alarmDay.getChildList().get(hourPosition);
        boolean active = alarmHour.isChecked();

        Toast.makeText(getContext(), "Alarme du " + alarmDay.getAlarmDay() + " à " + alarmHour.getHour() + (active ? " activée" : " désactivée"), Toast.LENGTH_SHORT).show();
        Log.e("listfrag", "alarmhour checked = " + active);

        AlarmSetter.setAlarmHour(getContext(), mAlarmDays.get(dayPosition), mAlarmDays.get(dayPosition).getChildList().get(hourPosition), dayPosition, hourPosition);
    }

    public List<AlarmDay> getAlarmDays() {
        return mAlarmDays;
    }
    public NetworkFragment getNetworkFragment() { return mNetworkFragment; }
}