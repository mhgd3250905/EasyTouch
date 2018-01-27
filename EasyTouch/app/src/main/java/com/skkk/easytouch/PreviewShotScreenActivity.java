package com.skkk.easytouch;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PreviewShotScreenActivity extends AppCompatActivity {

    @Bind(R.id.iv_shot_screen_preview)
    ImageView ivShotScreenPreview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_shot_screen);
        ButterKnife.bind(this);

        Uri uri = getIntent().getData();
        ivShotScreenPreview.setImageURI(uri);
    }
}
