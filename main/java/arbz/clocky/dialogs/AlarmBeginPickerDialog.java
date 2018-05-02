package arbz.clocky.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import arbz.clocky.R;
import arbz.clocky.fragments.AlarmConfigFragment;

public class AlarmBeginPickerDialog extends DialogFragment {
    public interface OnBeginTimePickedListener {
        void onBeginTimePicked(int hour, int minute);
    }

    public static final String TAG = "AlarmBeginPickerDialog";

    private OnBeginTimePickedListener mCallback;

    public void setOnValidateListener(OnBeginTimePickedListener callback) {
        mCallback = callback;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences(AlarmConfigFragment.EDITOR_FILENAME, Context.MODE_PRIVATE);
        int hour = sharedPreferences.getInt(AlarmConfigFragment.EDITOR_ALARM_BEG_HOUR_KEY, 1);
        int minute = sharedPreferences.getInt(AlarmConfigFragment.EDITOR_ALARM_BEG_MIN_KEY, 0);

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_alarm_interval_picker, null);
        final TimePicker timePicker = view.findViewById(R.id.dialog_interval_picker);

        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(minute);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setPositiveButton(R.string.alarm_picker_ok, null);
        builder.setNegativeButton(R.string.alarm_picker_cancel, null);
        builder.setView(view);

        AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override @SuppressLint("ApplySharedPref")
                    public void onClick(View view) {
                        int h = timePicker.getCurrentHour();
                        int m = timePicker.getCurrentMinute();

                        if(h > 0 || m > 0) {
                            sharedPreferences.edit()
                                    .putInt(AlarmConfigFragment.EDITOR_ALARM_BEG_HOUR_KEY, h)
                                    .putInt(AlarmConfigFragment.EDITOR_ALARM_BEG_MIN_KEY, m)
                                    .commit();

                            mCallback.onBeginTimePicked(h, m);
                            dismiss();
                        } else {
                            Toast.makeText(getActivity(), R.string.alarm_picker_null_value, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        return alertDialog;
    }
}