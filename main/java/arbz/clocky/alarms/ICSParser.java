package arbz.clocky.alarms;

import android.util.Log;
import android.util.SparseArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ICSParser {
    private final static int NB_DAYS = 5;
    private static int mWeekDay;

    private static String mMonthDayStr;
    private static String mMonthStr;
    private static String mYearStr;

    private static Calendar mCalendar;

    private ICSParser() {}

    public static SparseArray<Date> getDateList(InputStream inputStream) throws IOException, ParseException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        int i = 0;

        Log.e("parser", "starting to parse");
        mCalendar = Calendar.getInstance();

        Pattern pattern = incrementDay();
        Matcher matcher;

        SparseArray<Date> dateList = new SparseArray<>();

        while ((line = reader.readLine()) != null && i < NB_DAYS) {
            matcher = pattern.matcher(line);

            if(matcher.matches()) {
                Log.e("parser", "found : " + line);
                Pattern datePattern = Pattern.compile(mYearStr + mMonthStr + mMonthDayStr + "T[0-9]{6}");

                Matcher dateMatcher = datePattern.matcher(line);
                dateMatcher.find();

                Date date = new SimpleDateFormat("yyyyMMddkkmmss").parse(dateMatcher.group().replace("T", ""));

                dateList.put(mWeekDay, date);
                pattern = incrementDay();
                i++;
            }
        }

        return dateList;
    }

    private static Pattern incrementDay() {
        mCalendar.add(Calendar.DATE, 1);

        mWeekDay = mCalendar.get(Calendar.DAY_OF_WEEK);

        if(mWeekDay == Calendar.SATURDAY) {
            mCalendar.add(Calendar.DATE, 2);
            mWeekDay = mCalendar.get(Calendar.DAY_OF_WEEK);
        } else if(mWeekDay == Calendar.SUNDAY) {
            mCalendar.add(Calendar.DATE, 1);
            mWeekDay = mCalendar.get(Calendar.DAY_OF_WEEK);
        }

        int mMonthDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        int mMonth = mCalendar.get(Calendar.MONTH) + 1;
        int mYear = mCalendar.get(Calendar.YEAR);

        mMonthDayStr = mMonthDay < 10 ? "0" + mMonthDay : "" + mMonthDay;
        mMonthStr = mMonth < 10 ? "0" + mMonth : "" + mMonthDay;
        mYearStr = "" + mYear;

        return Pattern.compile("DTSTART.*" + mYearStr + mMonthStr + mMonthDayStr + "T[0-9]{6}");
    }

    /*public ICSParser(InputStream inputStream) throws IOException {
        ical = Biweekly.parse(inputStream).first();
    }

    public SparseArray<Date> getDateList() {
        SparseArray<Date> dateMap = null;
        List<VEvent> events = ical.getEvents();
        ListIterator<VEvent> eventIterator = events.listIterator();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("ddMM", Locale.getDefault());

        Calendar mCalendar = Calendar.getInstance();
        int weekDay = mCalendar.get(Calendar.DAY_OF_WEEK);
        int monthDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        int month = mCalendar.get(Calendar.MONTH);

        mCalendar.add(Calendar.DATE, 1);
        int tomorrowWeekDay = mCalendar.get(Calendar.DAY_OF_WEEK);
        int tomorrowMonthDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        int tomorrowMonth = mCalendar.get(Calendar.MONTH);

        boolean found = false;

        while(eventIterator.hasNext() && !found) {
            mCalendar.setTime(eventIterator.next().getDateStart().getValue());
            //found = (mCalendar.get(Calendar.DAY_OF_MONTH) == monthDay && mCalendar.get(Calendar.MONTH) == month);
            Log.e("parser", mCalendar.get(Calendar.DAY_OF_MONTH) +  "/" + mCalendar.get(Calendar.MONTH));
        }

        if(found) {
            dateMap = new SparseArray<>();

            for(int i = 0; i < NB_DAYS; i++) {
                if (weekDay == Calendar.FRIDAY) {
                    mCalendar.add(Calendar.DATE, 2);
                    tomorrowWeekDay = mCalendar.get(Calendar.DAY_OF_WEEK);
                    tomorrowMonthDay = mCalendar.get(Calendar.DAY_OF_MONTH);
                    tomorrowMonth = mCalendar.get(Calendar.MONTH);
                }

                ICalDate date = null;
                found = false;

                while (eventIterator.hasNext() && !found) {
                    date = eventIterator.next().getDateStart().getValue();
                    mCalendar.setTime(date);

                    found = (mCalendar.get(Calendar.DAY_OF_MONTH) == tomorrowMonthDay && mCalendar.get(Calendar.MONTH) == tomorrowMonth);
                }

                if (found) {
                    dateMap.append(tomorrowWeekDay, date);

                    weekDay = tomorrowWeekDay;

                    mCalendar.add(Calendar.DATE, 1);
                    tomorrowWeekDay = mCalendar.get(Calendar.DAY_OF_WEEK);
                    tomorrowMonthDay = mCalendar.get(Calendar.DAY_OF_MONTH);
                    tomorrowMonth = mCalendar.get(Calendar.MONTH);
                }
            }
        }

        return dateMap;

        /*Date today = new Date();
        Calendar mCalendar = Calendar.getInstance();
        Date tomorrow = new Date();
        List<ICalDate> dateList = new ArrayList<>();




        for(int i = 0; i < NB_DAYS; i++) {
            boolean found = false;

            while(eventIterator.hasNext() && !found) {
                ICalDate date = eventIterator.next().getDateStart().getValue();
                String dateString = dateFormatter.format(date);
                String tomorrowString = dateFormatter.format(tomorrow);

                if(date.after(today) && dateString.equals(tomorrowString)) {
                    dateList.add(date);
                    mCalendar.add(Calendar.DATE, 1);
                    today = (Date) tomorrow.clone();
                    tomorrow = mCalendar.getTime();
                    found = true;
                } else if(date.after(tomorrow)) {
                    while(date.after(tomorrow)) {
                        mCalendar.add(Calendar.DATE, 1);
                        today = (Date) tomorrow.clone();
                        tomorrow = mCalendar.getTime();
                    }

                    dateList.add(date);
                    found = true;
                }
            }
        }
    }*/
}
