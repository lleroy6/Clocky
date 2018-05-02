package arbz.clocky.alarms;


public class AlarmHour {
    public final static String ALARM = "alarm";

    private String alarmHour;
    private boolean isChecked;

    public AlarmHour(String hour, boolean checked) {
        alarmHour = hour;
        isChecked = checked;
    }

    public String getHour() {

        return alarmHour;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setAlarmHour(String alarmHour) {
        this.alarmHour = alarmHour;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
