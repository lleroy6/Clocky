package arbz.clocky.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;

import java.util.Calendar;
import java.util.List;

import arbz.clocky.R;
import arbz.clocky.alarms.AlarmDay;
import arbz.clocky.alarms.AlarmHour;
import arbz.clocky.viewholders.AlarmDayViewHolder;
import arbz.clocky.viewholders.AlarmHourViewHolder;

public class AlarmExpandableAdapter extends ExpandableRecyclerAdapter<AlarmDay, AlarmHour, AlarmDayViewHolder, AlarmHourViewHolder> {
    public interface OnSwitchChangedListener {
        void onDayBoxChecked(boolean value, int dayPosition);
        void onAlarmSwitchChanged(boolean value, int dayPosition, int alarmPosition);
    }

    private LayoutInflater mInflater;
    private OnSwitchChangedListener mCallback;
    private int mNextDay;

    public AlarmExpandableAdapter(Context context, OnSwitchChangedListener callback, List<AlarmDay> alarmDayList) {
        super(alarmDayList);
        mCallback = callback;
        mInflater = LayoutInflater.from(context);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);

        mNextDay = cal.get(Calendar.DAY_OF_WEEK);
        mNextDay = (mNextDay == Calendar.SATURDAY || mNextDay == Calendar.SUNDAY) ? 0 : mNextDay - 2;
    }

    @NonNull
    @Override
    public AlarmDayViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {});

        View alarmView = mInflater.inflate(R.layout.alarm_list_group, parentViewGroup, false);
        return new AlarmDayViewHolder(alarmView);
    }

    @NonNull
    @Override
    public AlarmHourViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {});

        View alarmChildView = mInflater.inflate(R.layout.alarm_list_item, childViewGroup, false);
        return new AlarmHourViewHolder(alarmChildView);
    }

    @Override
    public void onBindParentViewHolder(@NonNull final AlarmDayViewHolder alarmDayViewHolder, final int parentPosition, @NonNull final AlarmDay alarmDay) {
        alarmDayViewHolder.bind(alarmDay, parentPosition == mNextDay);
        alarmDayViewHolder.mAlarmCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = alarmDayViewHolder.mAlarmCheckBox.isChecked();
                List<AlarmHour> alarmHours = alarmDay.getChildList();

                for (int i = 0; i < alarmHours.size(); i++) {
                    alarmHours.get(i).setChecked(checked);
                    mCallback.onAlarmSwitchChanged(checked, parentPosition, i);
                    notifyChildChanged(parentPosition, i);
                }

                alarmDayViewHolder.bind(alarmDay, parentPosition == mNextDay);
                mCallback.onDayBoxChecked(checked, parentPosition);
            }
        });
    }

    @Override
    public void onBindChildViewHolder(@NonNull final AlarmHourViewHolder alarmHourViewHolder, final int parentPosition, final int childPosition, @NonNull final AlarmHour alarmHour) {
        alarmHourViewHolder.bind(alarmHour);
        alarmHourViewHolder.mAlarmSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = alarmHourViewHolder.mAlarmSwitch.isChecked();
                alarmHour.setChecked(checked);
                notifyParentChanged(parentPosition);

                mCallback.onAlarmSwitchChanged(checked, parentPosition, childPosition);
            }
        });
    }
}
