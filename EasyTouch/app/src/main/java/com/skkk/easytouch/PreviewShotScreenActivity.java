package com.skkk.easytouch;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PreviewShotScreenActivity extends AppCompatActivity {

    @Bind(R.id.iv_shot_screen_preview)
    ImageView ivShotScreenPreview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_preview_shot_screen);
        ButterKnife.bind(this);

        Uri uri = getIntent().getData();
        ivShotScreenPreview.setImageURI(uri);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        },2000);
    }
}
