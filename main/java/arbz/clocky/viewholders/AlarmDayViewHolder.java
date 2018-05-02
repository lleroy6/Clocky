package arbz.clocky.viewholders;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ParentViewHolder;

import arbz.clocky.R;
import arbz.clocky.alarms.AlarmDay;
import arbz.clocky.alarms.AlarmHour;

public class AlarmDayViewHolder extends ParentViewHolder {
    public TextView mAlarmTextView;
    public CheckBox mAlarmCheckBox;
    public TextView mAlarmResume;
    public ImageView mAlarmExpander;
    public LinearLayout mDivider;
    public LinearLayout mLayout;
    private ImageView mAlarmIcon;
    private boolean mIsDayOfWeek;

    public AlarmDayViewHolder(View itemView) {
        super(itemView);

        mLayout = itemView.findViewById(R.id.alarm_group_layout);
        mDivider = itemView.findViewById(R.id.alarm_group_divider);
        mAlarmCheckBox = itemView.findViewById(R.id.alarm_checkBox);
        mAlarmTextView = itemView.findViewById(R.id.alarm_day);
        mAlarmResume = itemView.findViewById(R.id.alarm_resume);
        mAlarmIcon = itemView.findViewById(R.id.alarm_nextday_icon);
        mAlarmExpander = itemView.findViewById(R.id.alarm_group_expander);
        mAlarmExpander.setSelected(false);
    }

    public void bind(final AlarmDay alarmDay, boolean isDayOfWeek) {
        mAlarmTextView.setText(alarmDay.getAlarmDay());

        boolean isChecked = false;
        int nbAlarms = 0;

        for(AlarmHour alarmHour : alarmDay.getChildList()) {
            if(alarmHour.isChecked()) {
                nbAlarms++;
                isChecked = true;
            }
        }

        mAlarmCheckBox.setChecked(isChecked);
        mAlarmResume.setText(nbAlarms + " alarme" + (nbAlarms > 1 ? "s " : " ") + " - cours à " + alarmDay.getFirstCourse());

        mIsDayOfWeek = isDayOfWeek;
        if(isDayOfWeek) {
            mAlarmIcon.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setExpanded(boolean expanded) {
        super.setExpanded(expanded);

        mAlarmExpander.setSelected(expanded);
        mDivider.setBackgroundResource(expanded ? R.color.transparent : R.drawable.list_divider);
        mLayout.setBackgroundResource(expanded ? R.color.selectedBackgroundColor : R.color.backgroundColor);
    }
}
