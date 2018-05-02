package arbz.clocky.alarms;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.Calendar;
import java.util.List;

import arbz.clocky.R;

public class AlarmRingtoneManager {

    public static void setAlarms(List<AlarmDay> alarmDays, Context context) {
        for(int i = Calendar.MONDAY; i <= Calendar.FRIDAY; i++) {
            AlarmDay alarmDay = alarmDays.get(i - Calendar.MONDAY);

            Intent ringtoneService = new Intent(context, RingtoneService.class);
            ringtoneService.putExtra(context.getString(R.string.first_course_time), "Cours à " + alarmDay.getFirstCourse());


            List<AlarmHour> alarmHours = alarmDay.getChildList();

            for(int j = 0; j < alarmHours.size(); j++) {
                AlarmHour alarmHour = alarmHours.get(j);

                if(alarmHour.isChecked());
            }
        }
    }

    public static void setAlarm(AlarmDay alarmDay) {

    }

    public static void cancelAlarms() {

    }

    public  static void cancelAlarm() {

    }
}
