package com.jacky.camera;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Picture;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Build;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lhm05 on 2016/08/03.
 */
public class CameraView extends SurfaceView
        implements SurfaceHolder.Callback,View.OnClickListener,Camera.AutoFocusCallback {

    private SurfaceHolder holder;
    private Camera camera;
    private SharedPreferences sp;
    private int index=0;
    final private File sdRoot;
    private String path;
    private int picWidth,picHeight,pvWidth,pvHeight;
    private ContinueFoucs continueFoucs;
    private CameraTimerTask mTimerTask;


    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        _("CameraView");
        holder=getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        //// TODO: 2016/08/03  holder.setType(....)

        setOnClickListener(this);
        sp=context.getSharedPreferences("data",Context.MODE_PRIVATE);
        sdRoot= Environment.getExternalStorageDirectory();
        path = sdRoot.getPath()+"/pics/";


    }




    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        _("surfaceCreated");

        if(camera==null)
        {
            camera=Camera.open();
            if(Build.VERSION.SDK_INT>=8)
            {camera.setDisplayOrientation(90);
            }

            try {
                camera.setPreviewDisplay(surfaceHolder);

                Camera.Parameters parameters=camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                parameters.setPictureFormat(PixelFormat.JPEG);
                List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
                List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();

                //閃光燈
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                picWidth=pictureSizes.get(0).width;
                pvHeight=previewSizes.get(0).height;
                pvWidth=previewSizes.get(0).width;

                //對焦模式參數
                List<String> allFocus = parameters.getSupportedFocusModes();
                 parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);


                // 設定最佳預覽尺寸
                parameters.setPreviewSize(pvWidth, pvHeight);
                camera.setParameters(parameters);



            } catch (IOException e) {
                _(e.toString());
             }

        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

        Timer timer=new Timer();
        mTimerTask=new CameraTimerTask();
        timer.schedule(mTimerTask,0,5000);
        camera.startPreview();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        _("surfaceDestroyed");
        camera.stopPreview();
        camera.release();
        camera=null;

    }


    public void _(String mesg){
        Log.i("TAG",mesg);
    }

    @Override
    public void onClick(View view) {

        continueFoucs=new ContinueFoucs();
        camera.autoFocus(continueFoucs);
    }


    Camera.ShutterCallback shutter=new Camera.ShutterCallback() {
        @Override
        public void onShutter() {

        }
    };

    Camera.PictureCallback jpeg=new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {

            if(bytes!=null)
            {
                Bitmap picture= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                picture=rotationBitMap(picture);
                saveBitMap(picture);

            }

        }
    };

    private Bitmap rotationBitMap(Bitmap picture) {
        Matrix matrix =new Matrix();
        matrix.setRotate(90);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(picture, picture.getWidth(), picture.getHeight(), true);
        Bitmap pic = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        return pic;
    }

    private void saveBitMap(Bitmap picture) {
       index=sp.getInt("Index",0);
       File f=new File(path+index+".jpeg");



       try {
           FileOutputStream fout=new FileOutputStream(f);
           picture.compress(Bitmap.CompressFormat.JPEG, 100, fout);

           fout.flush();
           fout.close();

       }catch (Exception e)
       {
        _(e.toString());
       }


       index++;
       sp.edit().putInt("Index",index).apply();
       camera.startPreview();


    }


    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        if(success)
        {
             camera.takePicture(shutter,null,jpeg);
        }
    }

    public class ContinueFoucs implements Camera.AutoFocusCallback
    {

        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if(success)
            {
                camera.takePicture(shutter,null,jpeg);
            }
        }
    }

    class CameraTimerTask extends TimerTask {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if(camera != null)
            {
                camera.autoFocus(continueFoucs);
            }

        }

    }


}


