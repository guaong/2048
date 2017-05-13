package io.guaong.a2048;

import android.content.Context;
import android.graphics.Point;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BlockLayout extends GridLayout {

    public BlockLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BlockLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BlockLayout(Context context) {
        super(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = h;
        params.width = w;
        setLayoutParams(params);
    }

}
