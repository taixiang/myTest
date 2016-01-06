package com.overtake.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * 设置一张normal state图片，模拟pressed的效果
 *
 * @author 畅彬
 */
public class SimpleButton extends Button {

    public SimpleButton(Context context) {
        super(context);
    }

    public SimpleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SimpleButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @SuppressWarnings("deprecation")
    public void setBackgroundDrawable(Drawable d) {

        SimpleButtonBackgroundDrawable layer = new SimpleButtonBackgroundDrawable(d);

        super.setBackgroundDrawable(layer);
    }

    public static class SimpleButtonBackgroundDrawable extends LayerDrawable {

        protected ColorFilter _pressedFilter = new LightingColorFilter(Color.LTGRAY, 1);
        protected int _diabledAlpha = 100;
        protected int _fullAlpha = 255;

        public SimpleButtonBackgroundDrawable(Drawable d) {
            super(new Drawable[]{d});
        }

        protected boolean onStateChange(int[] states) {
            boolean enabled = false;
            boolean pressed = false;
            boolean focus = false;

            for (int state : states) {
                if (state == android.R.attr.state_enabled) {
                    enabled = true;
                } else if (state == android.R.attr.state_pressed) {
                    pressed = true;
                } else if (state == android.R.attr.state_focused) {
                    focus = true;
                }
            }

            mutate();
            if (enabled && pressed || enabled && focus) {
                setColorFilter(_pressedFilter);
            } else if (!enabled) {
                setColorFilter(null);
                setAlpha(_diabledAlpha);
            } else {
                setColorFilter(null);
                setAlpha(_fullAlpha);
            }

            invalidateSelf();

            return super.onStateChange(states);
        }

        public boolean isStateful() {
            return true;
        }

    }

}
