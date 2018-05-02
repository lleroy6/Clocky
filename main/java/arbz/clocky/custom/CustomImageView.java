package arbz.clocky.custom;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;

import arbz.clocky.R;

public class CustomImageView extends android.support.v7.widget.AppCompatImageView {
    private boolean mIsChecked;

    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setChecked(boolean isChecked) {
        mIsChecked = isChecked;

        setImageTintList(ColorStateList.valueOf(getResources().getColor(mIsChecked ? R.color.vibratorOnColor : R.color.vibratorOffColor)));
    }

    public boolean isChecked() {
        return mIsChecked;
    }
}
