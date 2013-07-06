package com.hcb.saha.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.hcb.saha.R;
import com.hcb.saha.view.FaceDetectionView;

import java.io.IOException;

/**
 * Fragment for detecting a face from camera
 * @author Andreas Borglin
 */
public class FaceDetectionFragment extends Fragment implements SurfaceHolder.Callback {

    private SurfaceView surfaceView;
    private FaceDetectionView overlay;
    private Camera camera;
    private SurfaceHolder surfaceHolder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.face_detection, container, false);
        surfaceView = (SurfaceView) view.findViewById(R.id.surface);
        overlay = (FaceDetectionView) view.findViewById(R.id.overlay);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setSizeFromLayout();
        surfaceHolder.addCallback(this);

        // TODO Hardcoded front camera id
        camera = Camera.open(1);
        camera.setDisplayOrientation(90);

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        Camera.Parameters params = camera.getParameters();
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
            camera.setFaceDetectionListener(new Camera.FaceDetectionListener() {
                @Override
                public void onFaceDetection(Camera.Face[] faces, Camera camera) {
                    if (faces.length > 0) {
                        // Only care about one face for now
                        Camera.Face face = faces[0];
                        Matrix matrix = new Matrix();
                        boolean mirror = true;
                        matrix.setScale(mirror ? -1 : 1, 1);
                        // TODO Set via variable
                        matrix.postRotate(90);
                        // Camera driver coordinates range from (-1000, -1000) to (1000, 1000).
                        // UI coordinates range from (0, 0) to (width, height).
                        matrix.postScale(surfaceView.getWidth() / 2000f, surfaceView.getHeight() / 2000f);
                        matrix.postTranslate(surfaceView.getWidth() / 2f, surfaceView.getHeight() / 2f);
                        overlay.updateRect(face.rect, matrix);
                    }
                    else {
                        overlay.clearRect();
                    }
                }
            });
            camera.startFaceDetection();
        }
        catch (IOException e) {
            Log.e("SAHA", e.toString());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        camera.stopPreview();
        camera.release();
    }
}
