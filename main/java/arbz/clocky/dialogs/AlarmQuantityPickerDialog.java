package arbz.clocky.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;

import arbz.clocky.R;
import arbz.clocky.fragments.AlarmConfigFragment;

public class AlarmQuantityPickerDialog extends DialogFragment {
    public interface OnQuantityPickedListener {
        void onValidate(int numberPicked);
    }

    public static final String TAG = "AlarmQuantityPickerDialog";
    private OnQuantityPickedListener mCallback;

    public void setOnValidateListener(OnQuantityPickedListener callback) {
        mCallback = callback;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences(AlarmConfigFragment.EDITOR_FILENAME, Context.MODE_PRIVATE);
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_alarm_quantity_picker, null);
        final NumberPicker numberPicker = view.findViewById(R.id.dialog_quantity_picker);

        numberPicker.setValue(sharedPreferences.getInt(AlarmConfigFragment.EDITOR_ALARM_QTY_KEY, 1));
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(10);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setPositiveButton(R.string.alarm_picker_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(AlarmConfigFragment.EDITOR_ALARM_QTY_KEY, numberPicker.getValue());
                editor.commit();

                mCallback.onValidate(numberPicker.getValue());
            }
        });

        builder.setNegativeButton(R.string.alarm_picker_cancel, null);
        builder.setView(view);

        return builder.create();
    }
}
