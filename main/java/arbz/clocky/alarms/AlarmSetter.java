package arbz.clocky.alarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

import arbz.clocky.fragments.AlarmConfigFragment;

public class AlarmSetter {
    public static void setAlarms(Context context) throws ParseException {
        List<AlarmDay> dayList = AlarmRetriever.getData(context.getSharedPreferences(AlarmConfigFragment.EDITOR_FILENAME, Context.MODE_PRIVATE));

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();

        for(int i = 0; i < dayList.size(); i++) {
            AlarmDay alarmDay = dayList.get(i);

            List<AlarmHour> hourList = alarmDay.getChildList();

            for(int j = 0; j < hourList.size(); j++) {
                AlarmHour alarmHour = hourList.get(j);

                calendar = setDateInfo(alarmDay, alarmHour, calendar);

                PendingIntent pendingIntent = getPendingIntent(context, alarmDay, i * 10 + j);
                alarmManager.cancel(pendingIntent);

                if(alarmHour.isChecked()) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    Log.e("alarmSetter", "setting alarm to : " + calendar.getTime().toString());
                } else {
                    Log.e("alarmSetter", "cancelling alarm to : " + calendar.getTime().toString());
                }
            }
        }
    }

    public static void setAlarmHour(Context context, AlarmDay alarmDay, AlarmHour alarmHour, int dayPos, int hourPos) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = setDateInfo(alarmDay, alarmHour, Calendar.getInstance());

        PendingIntent pendingIntent = getPendingIntent(context, alarmDay, dayPos * 10 + hourPos);
        alarmManager.cancel(pendingIntent);

        if(alarmHour.isChecked()) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Log.e("setAlarm", "date : " + calendar.getTime().toString());
        }
    }

    private static PendingIntent getPendingIntent(Context context, AlarmDay alarmDay, int requestCode) {
        Intent ringtoneService = new Intent(context, RingtoneService.class);
        ringtoneService.putExtra("courseTime", "Premier cours à " + alarmDay.getFirstCourse());
        ringtoneService.putExtra("requestCode", requestCode);

        return PendingIntent.getService(context, requestCode, ringtoneService, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    private static Calendar setDateInfo(AlarmDay alarmDay, AlarmHour alarmHour, Calendar calendar) {
        String dayInfo[] = alarmDay.getAlarmDay().split(" ");
        dayInfo = dayInfo[1].split("/");
        String[] hourInfo = alarmHour.getHour().split(":");

        int day = Integer.parseInt(dayInfo[0]);
        int month = Integer.parseInt(dayInfo[1]) - 1;
        int hour = Integer.parseInt(hourInfo[0]);
        int minute = Integer.parseInt(hourInfo[1]);

        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        return calendar;
    }
}
