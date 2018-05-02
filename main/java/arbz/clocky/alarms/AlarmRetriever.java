package arbz.clocky.alarms;

import android.content.SharedPreferences;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import arbz.clocky.fragments.AlarmConfigFragment;

public class AlarmRetriever {
    public static boolean writeDate(List<AlarmDay> alarmDays, SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        for(int i = Calendar.MONDAY; i <= Calendar.FRIDAY; i++) {
            String dayKey = AlarmDay.ACTIVATED + i;
            AlarmDay alarmDay = alarmDays.get(i - Calendar.MONDAY);

            editor.putString(AlarmDay.DATE + i, alarmDay.getAlarmDay())
                    .putString(AlarmDay.HOUR + i, alarmDay.getFirstCourse())
                    .putBoolean(dayKey, alarmDay.isChecked());

            List<AlarmHour> alarmHours = alarmDay.getChildList();

            for (int j = 0; j < alarmDay.getChildList().size(); j++) {
                String alarmKey = AlarmHour.ALARM + i + j;
                editor.putBoolean(alarmKey, alarmHours.get(j).isChecked());

                Log.e("retriver / writedata", "day : " + alarmDay.getAlarmDay() + ", alarm " + alarmKey + " activated : " + alarmHours.get(j).isChecked());
            }
        }

        return  editor.commit();
    }

    public static List<AlarmDay> getData(SharedPreferences sharedPreferences) throws ParseException {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("hh:mm", Locale.getDefault());

        int intervalHour = sharedPreferences.getInt(AlarmConfigFragment.EDITOR_ALARM_INT_HOUR_KEY, 0);
        int intervalMinute = sharedPreferences.getInt(AlarmConfigFragment.EDITOR_ALARM_INT_MIN_KEY, 1);
        int offsetHour = sharedPreferences.getInt(AlarmConfigFragment.EDITOR_ALARM_BEG_HOUR_KEY, 1);
        int offsetMin = sharedPreferences.getInt(AlarmConfigFragment.EDITOR_ALARM_BEG_MIN_KEY, 0);

        int alarmQuantity = sharedPreferences.getInt(AlarmConfigFragment.EDITOR_ALARM_QTY_KEY, 1);
        int alarmInterval = intervalHour * 60 + intervalMinute;
        int alarmOffset = offsetHour * 60 + offsetMin;

        List<AlarmDay> alarmDays = new ArrayList<>();

        for(int i = Calendar.MONDAY; i <= Calendar.FRIDAY; i++) {
            boolean dayActivated = sharedPreferences.getBoolean(AlarmDay.ACTIVATED + i, true);
            String alarmLabel = sharedPreferences.getString(AlarmDay.DATE + i, "lun 01/01");
            String alarmHour = sharedPreferences.getString(AlarmDay.HOUR + i, "12:00");

            List<AlarmHour> hourList = new ArrayList<>();
            Date beginHour = dateFormatter.parse(alarmHour);

            for(int j = 0; j < alarmQuantity; j++) {
                boolean alarmActivated = sharedPreferences.getBoolean(AlarmHour.ALARM + i + j, true);

                cal.setTime(beginHour);
                cal.add(Calendar.MINUTE, - alarmOffset + j * alarmInterval);
                hourList.add(new AlarmHour(dateFormatter.format(cal.getTime()), alarmActivated));
            }

            alarmDays.add(new AlarmDay(alarmLabel, dayActivated, alarmHour, hourList));
        }

        /*List<AlarmDay> alarmDays = new ArrayList<>();
        List<AlarmHour> alarmHours = new ArrayList<>();

        Calendar cal = Calendar.getInstance();

        cal.add(Calendar.MINUTE, 1);
        alarmHours.add(new AlarmHour((cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE)), true));

        cal.add(Calendar.MINUTE, 1);
        alarmHours.add(new AlarmHour((cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE)), true));
        alarmDays.add(new AlarmDay(("LUN " + cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1)), true, "yolo", alarmHours));

        cal.add(Calendar.MINUTE, 1);
        alarmHours = new ArrayList<>();
        alarmHours.add(new AlarmHour((cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE)), true));

        cal.add(Calendar.MINUTE, 1);
        alarmHours.add(new AlarmHour((cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE)), true));
        alarmDays.add(new AlarmDay(("LUN " + cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1)), true, "yolo2", alarmHours));*/


        return alarmDays;
    }
}
