package com.example.trivial;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class RouletteView extends View {
    private Paint paint;
    private RectF rect;
    private float rotationAngle = 0;
    private final int[] colors = {Color.BLUE, Color.MAGENTA, Color.YELLOW, Color.rgb(165, 42, 42), // Marrón
            Color.GREEN, Color.rgb(255, 165, 0), Color.RED}; // Naranja y rojo

    public RouletteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rect = new RectF();
        startRotation();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int size = Math.min(width, height);
        rect.set(0, 0, size, size);

        float startAngle = 0;
        float sweepAngle = 360f / colors.length;

        for (int color : colors) {
            paint.setColor(color);
            canvas.drawArc(rect, startAngle + rotationAngle, sweepAngle, true, paint);
            startAngle += sweepAngle;
        }
    }

    private void startRotation() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "rotationAngle", 0, 360);
        animator.setDuration(3000); // 3 segundos por vuelta
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.start();
    }

    public void setRotationAngle(float angle) {
        rotationAngle = angle;
        invalidate();
    }

    public float getRotationAngle() {
        return rotationAngle;
    }
}
