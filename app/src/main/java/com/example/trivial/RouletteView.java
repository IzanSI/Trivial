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
    private ObjectAnimator animator;

    // Paleta de colores basada en la imagen de referencia
    private final int[] colors = {
            Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN,
            Color.MAGENTA, Color.rgb(255, 165, 0), Color.rgb(165, 42, 42)
    };

    public RouletteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rect = new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setPivotX(w / 2f);
        setPivotY(h / 2f);
        startRotation();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int size = Math.min(width, height);
        float radius = size / 2f;
        rect.set(0, 0, size, size);

        float startAngle = 0;
        float sweepAngle = 360f / colors.length;

        for (int color : colors) {
            paint.setColor(color);
            canvas.drawArc(rect, startAngle, sweepAngle, true, paint);
            startAngle += sweepAngle;
        }

        // Dibujar el centro de la ruleta (círculo pequeño para el efecto)
        paint.setColor(Color.WHITE);
        canvas.drawCircle(width / 2f, height / 2f, radius / 8, paint);
    }

    public void startRotation() {
        if (animator == null) {
            animator = ObjectAnimator.ofFloat(this, "rotation", 0, 360);
            animator.setDuration(3000);
            animator.setRepeatCount(ObjectAnimator.INFINITE);
            animator.setInterpolator(null);
        }
        animator.start();
    }

    public void stopRotation() {
        if (animator != null) {
            animator.cancel();
        }
    }
}
