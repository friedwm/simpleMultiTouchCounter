package com.robertlevonyan.examples.multitouch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by robert on 11/18/16.
 *
 * This custom view allows you to count the multi-touches of your screen
 */

public class MultiTouchView extends View {
    private static final int SIZE = 120;

    private Context context;
    private SparseArray<PointF> activePointers;
    private Paint paint;
    private Paint textPaint;
    private int[] colors;
    private int touches = 0;

    public MultiTouchView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public MultiTouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    private void initView() {
        activePointers = new SparseArray<>();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        colors = context.getResources().getIntArray(R.array.colors);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setTextSize(120);
        textPaint.setColor(ContextCompat.getColor(context, R.color.colorAccent));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // the index of each pointer
        int pointerIndex = event.getActionIndex();
        // the id of each pointer
        int pointerId = event.getPointerId(pointerIndex);
        // the the touch event action
        int maskedAction = event.getActionMasked();

        switch (maskedAction) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                // capturing the pointer and adding to pointers list
                PointF f = new PointF();
                f.x = event.getX(pointerIndex);
                f.y = event.getY(pointerIndex);
                activePointers.put(pointerId, f);
                break;
            case MotionEvent.ACTION_MOVE:
                for (int i = 0; i < event.getPointerCount(); i++) {
                    PointF point = activePointers.get(event.getPointerId(i));
                    if (point != null) {
                        point.x = event.getX(i);
                        point.y = event.getY(i);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
                // removes the pointer if your finger goes up
                activePointers.remove(pointerId);
                if (activePointers.size() + 1 > touches) {
                    // saving real count of touches
                    touches = activePointers.size() + 1;
                }
                break;
        }
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < activePointers.size(); i++) {
            PointF point = activePointers.valueAt(i);
            if (point != null) {
                paint.setColor(colors[i]);
                canvas.drawCircle(point.x, point.y, SIZE, paint);
            }
        }
        if (activePointers.size() + 1 >= touches) {
            canvas.drawText("" + activePointers.size(), getWidth() / 2 - 15, getHeight() / 2, textPaint);
        } else {
            canvas.drawText("" + touches, getWidth() / 2 - 15, getHeight() / 2, textPaint);
        }
    }
}
