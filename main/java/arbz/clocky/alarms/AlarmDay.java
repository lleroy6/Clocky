package arbz.clocky.alarms;


import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.List;

public class AlarmDay implements Parent<AlarmHour> {
    public final static String ACTIVATED = "dayActivated";
    public final static String DATE = "dayDate";
    public final static String HOUR = "dayHour";

    private String mAlarmDay;
    private boolean mIsChecked;
    private String mFirstCourse;
    private List<AlarmHour> mAlarmHours;

    public AlarmDay(String alarmDay, boolean isChecked, String firstCourse, List<AlarmHour> alarmHours) {
        mAlarmDay = alarmDay;
        mIsChecked = isChecked;
        mFirstCourse = firstCourse;
        mAlarmHours = alarmHours;
    }

    public String getAlarmDay() {
        return mAlarmDay;
    }

    public boolean isChecked() {
        return mIsChecked;
    }

    public String getFirstCourse() {
        return mFirstCourse;
    }

    public List<AlarmHour> getAlarms() {
        return mAlarmHours;
    }

    public void setDay(String alarmDay) {
        mAlarmDay = alarmDay;
    }

    public void setChecked(boolean isChecked) {
        this.mIsChecked = isChecked;
    }

    public void setFirstCourse(String firstCourse) {
        mFirstCourse = firstCourse;
    }

    public void setHour(List<AlarmHour> alarmHours) {
        mAlarmHours = alarmHours;
    }

    @Override
    public List<AlarmHour> getChildList() {
        return mAlarmHours;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
