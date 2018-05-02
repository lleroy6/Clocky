package arbz.clocky.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

public class CustomEditText extends android.support.v7.widget.AppCompatEditText {
    CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setCursorVisible(false);
        }

        return super.onKeyPreIme(keyCode, event);
    }
}
