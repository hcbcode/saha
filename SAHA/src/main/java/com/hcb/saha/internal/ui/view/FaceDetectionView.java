package com.hcb.saha.internal.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * View for drawing the face rect on top of the preview frame
 * @author Andreas Borglin
 */
public class FaceDetectionView extends View {

    private Rect faceRect;
    private Matrix matrix;
    private Paint paint;

    public FaceDetectionView(Context context) {
        this(context, null, 0);
    }

    public FaceDetectionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FaceDetectionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5f);
    }

    public void updateRect(Rect rect, Matrix matrix) {
        faceRect = rect;
        this.matrix = matrix;
        invalidate();
    }

    public void clearRect() {
        faceRect = null;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (faceRect != null) {
            canvas.concat(matrix);
            canvas.drawRect(faceRect, paint);
        }
        else {
            canvas.drawColor(Color.TRANSPARENT);
        }
    }
}
