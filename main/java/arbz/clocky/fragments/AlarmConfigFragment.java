package arbz.clocky.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kevalpatel.ringtonepicker.RingtonePickerDialog;
import com.kevalpatel.ringtonepicker.RingtonePickerListener;

import arbz.clocky.custom.CustomEditText;
import arbz.clocky.custom.CustomImageView;
import arbz.clocky.R;
import arbz.clocky.dialogs.AlarmBeginPickerDialog;
import arbz.clocky.dialogs.AlarmIntervalPickerDialog;
import arbz.clocky.dialogs.AlarmQuantityPickerDialog;

public class AlarmConfigFragment extends Fragment {
    public final static String EDITOR_FIRST_LAUNCH = "firstLaunch";

    public final static String EDITOR_ICS_LINK_KEY = "icsLink";
    public final static String EDITOR_ALARM_QTY_KEY = "alarmQuantity";

    public final static String EDITOR_ALARM_INT_HOUR_KEY = "alarmIntervalHour";
    public final static String EDITOR_ALARM_INT_MIN_KEY = "alarmIntervalMinute";

    public final static String EDITOR_ALARM_BEG_HOUR_KEY = "alarmBeginHour";
    public final static String EDITOR_ALARM_BEG_MIN_KEY = "alarmBeginMinute";

    public final static String EDITOR_RINGTONE_KEY = "alarmRingtone";
    public final static String EDITOR_RINGTONE_NAME_KEY = "alarmRingtoneName";
    public final static String EDITOR_RINGTONE_VIBRATOR_KEY = "alarmRingtoneVibrator";
    public final static String EDITOR_RINGTONE_NOTMUTE_KEY = "alarmRingtoneMute";

    public final static String EDITOR_FILENAME = "arbz.clocky.conf";

    private SharedPreferences mSharedPreferences;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm_config, container, false);

        mSharedPreferences = getContext().getSharedPreferences(EDITOR_FILENAME, Context.MODE_PRIVATE);

        configureAlarmLink(view);
        configureAlarmRingtone(view);
        configureAlarmQuantity(view);
        configureAlarmInterval(view);
        configureAlarmBegin(view);

        return view;
    }

    private void configureAlarmLink(View view) {
        final CustomEditText calendarLinkEditText = view.findViewById(R.id.alarm_link_editText);

        LinearLayout calendarLinkLayout = view.findViewById(R.id.alarm_link_layout);
        calendarLinkLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarLinkEditText.setCursorVisible(true);
                calendarLinkEditText.requestFocus();
                ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(calendarLinkEditText, InputMethodManager.SHOW_IMPLICIT);
            }
        });


        calendarLinkEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override @SuppressLint("ApplySharedPref")
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    calendarLinkEditText.setCursorVisible(false);
                    mSharedPreferences.edit()
                            .putString(EDITOR_ICS_LINK_KEY, calendarLinkEditText.getText().toString())
                            .commit();
                }

                calendarLinkEditText.setCursorVisible(false);

                return false;
            }
        });
    }

    private void configureAlarmRingtone(View view) {
        Uri defaultRingtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        String defaultRingtoneStr = mSharedPreferences.getString(EDITOR_RINGTONE_KEY, defaultRingtone.toString());

        final Uri currentRingtone = Uri.parse(defaultRingtoneStr);
        String currentRingtoneName = mSharedPreferences.getString(EDITOR_RINGTONE_NAME_KEY, getString(R.string.default_ringtone_name));

        final TextView alarmRingtoneTextView = view.findViewById(R.id.alarm_ringtone_choice);
        alarmRingtoneTextView.setText(currentRingtoneName);

        LinearLayout alarmRingtoneChoiceLayout = view.findViewById(R.id.ringtone_choice_layout);
        alarmRingtoneChoiceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RingtonePickerDialog.Builder ringonePickerBuilder = new RingtonePickerDialog.Builder(getContext(), getFragmentManager());

                ringonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_ALARM)
                        .addRingtoneType(RingtonePickerDialog.Builder.TYPE_NOTIFICATION)
                        .addRingtoneType(RingtonePickerDialog.Builder.TYPE_RINGTONE);

                ringonePickerBuilder.setTitle(R.string.alarm_ringtone_selection_title)
                        .setCurrentRingtoneUri(currentRingtone)
                        .displayDefaultRingtone(true)
                        .setPositiveButtonText("OK")
                        .setCancelButtonText(R.string.alarm_picker_cancel)
                        .setPlaySampleWhileSelection(true)
                        .setListener(new RingtonePickerListener() {

                            @Override @SuppressLint("ApplySharedPref")
                            public void OnRingtoneSelected(@NonNull String ringtoneName, @Nullable Uri ringtoneUri) {
                                alarmRingtoneTextView.setText(ringtoneName);

                                mSharedPreferences.edit()
                                        .putString(EDITOR_RINGTONE_KEY, ringtoneUri.toString())
                                        .putString(EDITOR_RINGTONE_NAME_KEY, ringtoneName)
                                        .commit();
                            }
                        });

                ringonePickerBuilder.show();
            }
        });

        CustomImageView alarmVibratorImageView = view.findViewById(R.id.ic_alarm_vibrator);
        CustomImageView alarmRingtoneImageView = view.findViewById(R.id.ic_alarm_vibrator_only);

        configureRingtoneIcon(alarmVibratorImageView, alarmRingtoneImageView, EDITOR_RINGTONE_VIBRATOR_KEY, EDITOR_RINGTONE_NOTMUTE_KEY, R.string.alarm_vibrator_activated, R.string.alarm_ringtone_only);
        configureRingtoneIcon(alarmRingtoneImageView, alarmVibratorImageView, EDITOR_RINGTONE_NOTMUTE_KEY, EDITOR_RINGTONE_VIBRATOR_KEY, R.string.alarm_ringtone_activated, R.string.alarm_vibrator_only);
    }

    private void configureRingtoneIcon(final CustomImageView imageView1, final CustomImageView imageView2, final String editorKey1, final String editorKey2, final int toast1, final int toast2) {
        boolean ringtoneChecked = mSharedPreferences.getBoolean(editorKey1, true);

        imageView1.setActivated(ringtoneChecked);
        imageView1.setChecked(ringtoneChecked);
        imageView1.setOnClickListener(new View.OnClickListener() {

            @Override @SuppressLint("ApplySharedPref")
            public void onClick(View view) {
                boolean checked = !imageView1.isChecked();
                imageView1.setChecked(checked);
                imageView1.setActivated(checked);

                if(checked) {
                    mSharedPreferences.edit()
                            .putBoolean(editorKey1, true)
                            .commit();

                    Toast.makeText(getContext(), toast1, Toast.LENGTH_SHORT).show();
                } else {
                    imageView2.setChecked(true);
                    imageView2.setActivated(true);

                    mSharedPreferences.edit()
                            .putBoolean(editorKey1, false)
                            .putBoolean(editorKey2, true)
                            .commit();

                    Toast.makeText(getContext(), toast2, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void configureAlarmQuantity(View view) {
        int nbAlarms = mSharedPreferences.getInt(EDITOR_ALARM_QTY_KEY, 1);
        String text = nbAlarms + (nbAlarms > 1 ? " alarmes" : " alarme");

        final TextView alarmQuantityTextView = view.findViewById(R.id.alarm_quantity);
        alarmQuantityTextView.setText(text);

        LinearLayout alarmQuantityLayout = view.findViewById(R.id.alarm_quantity_layout);
        alarmQuantityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmQuantityPickerDialog alarmQuantityPickerDialog = new AlarmQuantityPickerDialog();
                alarmQuantityPickerDialog.show(getActivity().getFragmentManager(), AlarmQuantityPickerDialog.TAG);
                alarmQuantityPickerDialog.setOnValidateListener(new AlarmQuantityPickerDialog.OnQuantityPickedListener() {
                    @Override
                    public void onValidate(int numberPicked) {
                        String text = numberPicked + (numberPicked > 1 ? " alarmes" : " alarme");
                        alarmQuantityTextView.setText(text);
                    }
                });
            }
        });
    }

    private void configureAlarmInterval(View view) {
        int intervalHour = mSharedPreferences.getInt(EDITOR_ALARM_INT_HOUR_KEY, 0);
        int intervalMinute = mSharedPreferences.getInt(EDITOR_ALARM_INT_MIN_KEY, 1);

        String hourText = intervalHour > 0 ? intervalHour > 1 ? intervalHour + " heures et " : intervalHour + " heure et " : "";
        String minuteText = intervalMinute > 1 ? intervalMinute + " minutes" : intervalMinute + " minute";
        String text = hourText + minuteText;

        final TextView alarmIntervalTextView = view.findViewById(R.id.alarm_interval);
        alarmIntervalTextView.setText(text);

        LinearLayout alarmIntervalLayout = view.findViewById(R.id.alarm_interval_layout);
        alarmIntervalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmIntervalPickerDialog alarmIntervalPickerDialog = new AlarmIntervalPickerDialog();
                alarmIntervalPickerDialog.show(getActivity().getFragmentManager(), AlarmIntervalPickerDialog.TAG);
                alarmIntervalPickerDialog.setOnValidateListener(new AlarmIntervalPickerDialog.OnIntervalTimePickedListener() {
                    @Override
                    public void onIntervalTimePicked(int hour, int minute) {
                        String hourText = hour > 0 ? hour > 1 ? hour + " heures et " : hour + " heure et " : "";
                        String minuteText = minute > 1 ? minute + " minutes" : minute + " minute";
                        String text = hourText + minuteText;
                        alarmIntervalTextView.setText(text);
                    }
                });
            }
        });
    }

    private void configureAlarmBegin(View view) {
        int beginHour = mSharedPreferences.getInt(EDITOR_ALARM_BEG_HOUR_KEY, 1);
        int beginMinute = mSharedPreferences.getInt(EDITOR_ALARM_BEG_MIN_KEY, 0);

        String hourText = beginHour > 0 ? beginHour > 1 ? beginHour + " heures" : beginHour + " heure" : "";
        String liaison = beginHour > 0 && beginMinute > 0 ? " et " : "";
        String minuteText = (beginMinute > 0 ? beginMinute > 1 ? beginMinute + " minutes" : beginMinute + " minute" : "") + " avant le début des cours";

        String text = hourText + liaison + minuteText;

        final TextView alarmBeginTextView = view.findViewById(R.id.alarm_begin);
        alarmBeginTextView.setText(text);

        LinearLayout alarmBeginLayout = view.findViewById(R.id.alarm_begin_layout);
        alarmBeginLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmBeginPickerDialog alarmBeginPickerDialog= new AlarmBeginPickerDialog();
                alarmBeginPickerDialog.show(getActivity().getFragmentManager(), AlarmBeginPickerDialog.TAG);
                alarmBeginPickerDialog.setOnValidateListener(new AlarmBeginPickerDialog.OnBeginTimePickedListener() {
                    @Override
                    public void onBeginTimePicked(int hour, int minute) {
                        String hourText = hour > 0 ? hour > 1 ? hour + " heures" : hour + " heure" : "";
                        String liaison = hour > 0 && minute > 0 ? " et " : "";
                        String minuteText = (minute > 0 ? minute > 1 ? minute + " minutes" : minute + " minute" : "") + " avant le début des cours";
                        String text = hourText + liaison + minuteText;
                        alarmBeginTextView.setText(text);
                    }
                });
            }
        });
    }
}

/*
package arbz.clocky.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kevalpatel.ringtonepicker.RingtonePickerDialog;
import com.kevalpatel.ringtonepicker.RingtonePickerListener;

import arbz.clocky.custom.CustomEditText;
import arbz.clocky.custom.CustomImageView;
import arbz.clocky.R;
import arbz.clocky.dialogs.AlarmBeginPickerDialog;
import arbz.clocky.dialogs.AlarmIntervalPickerDialog;
import arbz.clocky.dialogs.AlarmQuantityPickerDialog;

public class AlarmConfigFragment extends Fragment {
    public final static String EDITOR_ICS_LINK_KEY = "icsLink";
    public final static String EDITOR_ALARM_QTY_KEY = "alarmQuantity";

    public final static String EDITOR_ALARM_INT_HOUR_KEY = "alarmIntervalHour";
    public final static String EDITOR_ALARM_INT_MIN_KEY = "alarmIntervalMinute";

    public final static String EDITOR_ALARM_BEG_HOUR_KEY = "alarmBeginHour";
    public final static String EDITOR_ALARM_BEG_MIN_KEY = "alarmBeginMinute";

    public final static String EDITOR_RINGTONE_KEY = "alarmRingtone";
    public final static String EDITOR_RINGTONE_NAME_KEY = "alarmRingtoneName";
    public final static String EDITOR_RINGTONE_VIBRATOR_KEY = "alarmRingtoneVibrator";
    public final static String EDITOR_RINGTONE_NOTMUTE_KEY = "alarmRingtoneMute";

    public final static String EDITOR_FILENAME = "arbz.clocky.conf";

    private SharedPreferences mSharedPreferences;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm_config, container, false);

        mSharedPreferences = getContext().getSharedPreferences(EDITOR_FILENAME, Context.MODE_PRIVATE);

        configureAlarmLink(view);
        configureAlarmRingtone(view);
        configureAlarmQuantity(view);
        configureAlarmInterval(view);
        configureAlarmBegin(view);

        return view;
    }

    private void configureAlarmLink(View view) {
        final CustomEditText calendarLinkEditText = view.findViewById(R.id.alarm_link_editText);

        LinearLayout calendarLinkLayout = view.findViewById(R.id.alarm_link_layout);
        calendarLinkLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarLinkEditText.setCursorVisible(true);
                calendarLinkEditText.requestFocus();
                ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(calendarLinkEditText, InputMethodManager.SHOW_IMPLICIT);
            }
        });


        calendarLinkEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override @SuppressLint("ApplySharedPref")
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    calendarLinkEditText.setCursorVisible(false);
                    mSharedPreferences.edit()
                            .putString(EDITOR_ICS_LINK_KEY, calendarLinkEditText.getText().toString())
                            .commit();
                }

                calendarLinkEditText.setCursorVisible(false);

                return false;
            }
        });
    }

    private void configureAlarmRingtone(View view) {
        Uri defaultRingtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        String defaultRingtoneStr = mSharedPreferences.getString(EDITOR_RINGTONE_KEY, defaultRingtone.toString());

        final Uri currentRingtone = Uri.parse(defaultRingtoneStr);
        String currentRingtoneName = mSharedPreferences.getString(EDITOR_RINGTONE_NAME_KEY, getString(R.string.default_ringtone_name));

        final TextView alarmRingtoneTextView = view.findViewById(R.id.alarm_ringtone_choice);
        alarmRingtoneTextView.setText(currentRingtoneName);

        LinearLayout alarmRingtoneChoiceLayout = view.findViewById(R.id.ringtone_choice_layout);
        alarmRingtoneChoiceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RingtonePickerDialog.Builder ringonePickerBuilder = new RingtonePickerDialog.Builder(getContext(), getFragmentManager());

                ringonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_ALARM)
                        .addRingtoneType(RingtonePickerDialog.Builder.TYPE_NOTIFICATION)
                        .addRingtoneType(RingtonePickerDialog.Builder.TYPE_RINGTONE);

                ringonePickerBuilder.setTitle(R.string.alarm_ringtone_selection_title)
                        .setCurrentRingtoneUri(currentRingtone)
                        .displayDefaultRingtone(true)
                        .setPositiveButtonText("OK")
                        .setCancelButtonText(R.string.alarm_picker_cancel)
                        .setPlaySampleWhileSelection(true)
                        .setListener(new RingtonePickerListener() {

                            @Override @SuppressLint("ApplySharedPref")
                            public void OnRingtoneSelected(@NonNull String ringtoneName, @Nullable Uri ringtoneUri) {
                                alarmRingtoneTextView.setText(ringtoneName);

                                mSharedPreferences.edit()
                                        .putString(EDITOR_RINGTONE_KEY, ringtoneUri.toString())
                                        .putString(EDITOR_RINGTONE_NAME_KEY, ringtoneName)
                                        .commit();
                            }
                        });

                ringonePickerBuilder.show();
            }
        });

        CustomImageView alarmVibratorImageView = view.findViewById(R.id.ic_alarm_vibrator);
        CustomImageView alarmRingtoneImageView = view.findViewById(R.id.ic_alarm_vibrator_only);

        configureRingtoneIcon(alarmVibratorImageView, alarmRingtoneImageView, EDITOR_RINGTONE_VIBRATOR_KEY, EDITOR_RINGTONE_NOTMUTE_KEY, R.string.alarm_vibrator_activated, R.string.alarm_ringtone_only);
        configureRingtoneIcon(alarmRingtoneImageView, alarmVibratorImageView, EDITOR_RINGTONE_NOTMUTE_KEY, EDITOR_RINGTONE_VIBRATOR_KEY, R.string.alarm_ringtone_activated, R.string.alarm_vibrator_only);
    }

    private void configureRingtoneIcon(final CustomImageView imageView1, final CustomImageView imageView2, final String editorKey1, final String editorKey2, final int toast1, final int toast2) {
        boolean ringtoneChecked = mSharedPreferences.getBoolean(editorKey1, true);

        imageView1.setActivated(ringtoneChecked);
        imageView1.setChecked(ringtoneChecked);
        imageView1.setOnClickListener(new View.OnClickListener() {

            @Override @SuppressLint("ApplySharedPref")
            public void onClick(View view) {
                boolean checked = !imageView1.isChecked();
                imageView1.setChecked(checked);
                imageView1.setActivated(checked);

                if(checked) {
                    mSharedPreferences.edit()
                            .putBoolean(editorKey1, true)
                            .commit();

                    Toast.makeText(getContext(), toast1, Toast.LENGTH_SHORT).show();
                } else {
                    imageView2.setChecked(true);
                    imageView2.setActivated(true);

                    mSharedPreferences.edit()
                            .putBoolean(editorKey1, false)
                            .putBoolean(editorKey2, true)
                            .commit();

                    Toast.makeText(getContext(), toast2, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void configureAlarmQuantity(View view) {
        int nbAlarms = mSharedPreferences.getInt(EDITOR_ALARM_QTY_KEY, 1);
        String text = nbAlarms + (nbAlarms > 1 ? " alarmes" : " alarme");

        final TextView alarmQuantityTextView = view.findViewById(R.id.alarm_quantity);
        alarmQuantityTextView.setText(text);

        LinearLayout alarmQuantityLayout = view.findViewById(R.id.alarm_quantity_layout);
        alarmQuantityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmQuantityPickerDialog alarmQuantityPickerDialog = new AlarmQuantityPickerDialog();
                alarmQuantityPickerDialog.show(getActivity().getFragmentManager(), AlarmQuantityPickerDialog.TAG);
                alarmQuantityPickerDialog.setOnValidateListener(new AlarmQuantityPickerDialog.OnQuantityPickedListener() {
                    @Override
                    public void onValidate(int numberPicked) {
                        String text = numberPicked + (numberPicked > 1 ? " alarmes" : " alarme");
                        alarmQuantityTextView.setText(text);
                    }
                });
            }
        });
    }

    private void configureAlarmInterval(View view) {
        int intervalHour = mSharedPreferences.getInt(EDITOR_ALARM_INT_HOUR_KEY, 0);
        int intervalMinute = mSharedPreferences.getInt(EDITOR_ALARM_INT_MIN_KEY, 1);

        String hourText = intervalHour > 0 ? intervalHour > 1 ? intervalHour + " heures et " : intervalHour + " heure et " : "";
        String minuteText = intervalMinute > 1 ? intervalMinute + " minutes" : intervalMinute + " minute";
        String text = hourText + minuteText;

        final TextView alarmIntervalTextView = view.findViewById(R.id.alarm_interval);
        alarmIntervalTextView.setText(text);

        LinearLayout alarmIntervalLayout = view.findViewById(R.id.alarm_interval_layout);
        alarmIntervalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmIntervalPickerDialog alarmIntervalPickerDialog = new AlarmIntervalPickerDialog();
                alarmIntervalPickerDialog.show(getActivity().getFragmentManager(), AlarmIntervalPickerDialog.TAG);
                alarmIntervalPickerDialog.setOnValidateListener(new AlarmIntervalPickerDialog.OnIntervalTimePickedListener() {
                    @Override
                    public void onIntervalTimePicked(int hour, int minute) {
                        String hourText = hour > 0 ? hour > 1 ? hour + " heures et " : hour + " heure et " : "";
                        String minuteText = minute > 1 ? minute + " minutes" : minute + " minute";
                        String text = hourText + minuteText;
                        alarmIntervalTextView.setText(text);
                    }
                });
            }
        });
    }

    private void configureAlarmBegin(View view) {
        int beginHour = mSharedPreferences.getInt(EDITOR_ALARM_BEG_HOUR_KEY, 1);
        int beginMinute = mSharedPreferences.getInt(EDITOR_ALARM_BEG_MIN_KEY, 0);

        String hourText = beginHour > 0 ? beginHour > 1 ? beginHour + " heures" : beginHour + " heure" : "";
        String liaison = beginHour > 0 && beginMinute > 0 ? " et " : "";
        String minuteText = (beginMinute > 0 ? beginMinute > 1 ? beginMinute + " minutes" : beginMinute + " minute" : "") + " avant le début des cours";

        String text = hourText + liaison + minuteText;

        final TextView alarmBeginTextView = view.findViewById(R.id.alarm_begin);
        alarmBeginTextView.setText(text);

        LinearLayout alarmBeginLayout = view.findViewById(R.id.alarm_begin_layout);
        alarmBeginLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmBeginPickerDialog alarmBeginPickerDialog= new AlarmBeginPickerDialog();
                alarmBeginPickerDialog.show(getActivity().getFragmentManager(), AlarmBeginPickerDialog.TAG);
                alarmBeginPickerDialog.setOnValidateListener(new AlarmBeginPickerDialog.OnBeginTimePickedListener() {
                    @Override
                    public void onBeginTimePicked(int hour, int minute) {
                        String hourText = hour > 0 ? hour > 1 ? hour + " heures" : hour + " heure" : "";
                        String liaison = hour > 0 && minute > 0 ? " et " : "";
                        String minuteText = (minute > 0 ? minute > 1 ? minute + " minutes" : minute + " minute" : "") + " avant le début des cours";
                        String text = hourText + liaison + minuteText;
                        alarmBeginTextView.setText(text);
                    }
                });
            }
        });
    }
}


 */