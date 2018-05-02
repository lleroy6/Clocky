package arbz.clocky.viewholders;

import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;

import arbz.clocky.alarms.AlarmHour;
import arbz.clocky.R;

public class AlarmHourViewHolder extends ChildViewHolder {
    public TextView mAlarmHourTextView;
    public Switch mAlarmSwitch;

    public AlarmHourViewHolder(View itemView) {
        super(itemView);

        mAlarmHourTextView = itemView.findViewById(R.id.alarm_hour);
        mAlarmSwitch = itemView.findViewById(R.id.alarm_switch);
    }

    public void bind(final AlarmHour alarmHour) {
        mAlarmHourTextView.setText(alarmHour.getHour());
        mAlarmSwitch.setChecked(alarmHour.isChecked());
    }
}
