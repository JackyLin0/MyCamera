package com.jacky.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

/**
 * Created by lhm05 on 2016/08/03.
 */
public class CameraViewGroup extends RelativeLayout {
    private ImageButton camera_btn;

    private CameraView cameraView;
    public CameraViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        cameraView=new CameraView(context,attrs);
        addView(cameraView);

        camera_btn=new ImageButton(context, attrs);
        RelativeLayout.LayoutParams lp=new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(ALIGN_PARENT_BOTTOM);
        lp.addRule(CENTER_HORIZONTAL);
        camera_btn.setLayoutParams(lp);
        Bitmap bm= BitmapFactory.decodeResource(getResources(),R.drawable.camera_btn);
        camera_btn.setImageBitmap(bm);
        addView(camera_btn);
        camera_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraView.onClick(view);
            }
        });

    }
}
