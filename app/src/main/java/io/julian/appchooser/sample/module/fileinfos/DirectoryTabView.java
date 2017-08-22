package io.julian.appchooser.sample.module.fileinfos;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;

import io.julian.appchooser.sample.R;

/**
 * @author Zhu Liang
 */

public class DirectoryTabView extends AppCompatTextView {
    public DirectoryTabView(Context context) {
        this(context, null);
    }

    public DirectoryTabView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DirectoryTabView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Drawable arrowIcon = AppCompatDrawableManager.get()
                .getDrawable(context, R.drawable.ic_keyboard_arrow_right_white_24dp);
        arrowIcon.setBounds(0, 0, arrowIcon.getIntrinsicWidth(), arrowIcon.getIntrinsicHeight());

        setCompoundDrawables(null, null, arrowIcon, null);

        setGravity(Gravity.CENTER);

        setTextAppearance(context, R.style.TextAppearance_Design_Tab);

        ColorStateList tabTextColors = getTextColors();
        tabTextColors = createColorStateList(tabTextColors.getDefaultColor(), Color.WHITE);
        setTextColor(tabTextColors);

        setMaxLines(1);

        setEllipsize(TextUtils.TruncateAt.MIDDLE);

        setAllCaps(false);
    }

    private static ColorStateList createColorStateList(int defaultColor, int selectedColor) {
        final int[][] states = new int[2][];
        final int[] colors = new int[2];
        int i = 0;

        states[i] = SELECTED_STATE_SET;
        colors[i] = selectedColor;
        i++;

        // Default enabled state
        states[i] = EMPTY_STATE_SET;
        colors[i] = defaultColor;

        return new ColorStateList(states, colors);
    }
}
