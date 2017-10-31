package com.darodev.thuglifephotoeditor;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.darodev.thuglifephotoeditor.image.ImageEditor;
import com.darodev.thuglifephotoeditor.utility.BitmapUtility;
import com.darodev.thuglifephotoeditor.utility.ConfigUtility;
import com.darodev.thuglifephotoeditor.utility.DefaultConfig;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class EditorActivity extends AppCompatActivity {
    private final ImageEditor imageEditor = new ImageEditor();

    private AdView adView;
    private ConfigUtility configUtility;
    private Resources resources;
    ImageView imagePicture;
    private int imageWidth, imageHeight;

    static final int REQUEST_IMAGE_CAPTURE = 5600;
    static final int REQUEST_CAMERA_PERMISSION = 5601;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        resources = getResources();
        imagePicture = findViewById(R.id.image_picture);

        imagePicture.post(new Runnable() {
            @Override
            public void run() {
                configUtility.save(R.string.key_image_height, imagePicture.getMeasuredHeight());
                configUtility.save(R.string.key_image_width, imagePicture.getMeasuredWidth());
            }
        });

        configUtility = new ConfigUtility(
                resources,
                getSharedPreferences(resources.getString(R.string.app_name), Context.MODE_PRIVATE));


        prepareAds();
    }

    public void btnTakePicture(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        } else {
            requestCameraPermission();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CAMERA_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            dispatchTakePictureIntent();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            processPictureInsert(BitmapUtility.getFromIntentData(data, getApplicationContext()));
        }
    }

    private void processPictureInsert(Bitmap bitmap){
        if(bitmap != null){
            imageEditor.setOriginalBitmap(bitmap);
            Bitmap scaled = imageEditor.getBitmapScaledToDimensions(
                    configUtility.get(R.string.key_image_width, DefaultConfig.IMAGE_DEFAULT_WIDTH.getIntValue()),
                    configUtility.get(R.string.key_image_height, DefaultConfig.IMAGE_DEFAULT_WIDTH.getIntValue()), true);
            imagePicture.setImageBitmap(scaled);
            //scaled.recycle();
        }
    }

    public void onRotateClick(View view){
        if(imageEditor.isOriginalBitmapSet()){

        }
    }

    private void prepareAds() {
        if (configUtility.adsEnabled()) {
            LinearLayout add_holder = findViewById(R.id.layout_ad);
            add_holder.addView(getAdView());
        }
    }

    private AdView getAdView(){
        if(adView == null){
            MobileAds.initialize(getApplicationContext(), resources.getString(R.string.app_id));

            RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            adParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            adParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

            adView = new AdView(getApplicationContext());
            adView.setAdSize(AdSize.SMART_BANNER);
            adView.setAdUnitId(resources.getString(R.string.add_unit_id));
            adView.setBackgroundColor(Color.TRANSPARENT);
            adView.setLayoutParams(adParams);

            // Test Ads
            // AdRequest adRequest = new AdRequest.Builder().addTestDevice(getResources().getString(R.string.test_device_id)).build();
            AdRequest adRequest = new AdRequest.Builder().build();

            adView.loadAd(adRequest);
        }

        return adView;
    }
}
