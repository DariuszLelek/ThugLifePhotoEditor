package com.darodev.thuglifephotoeditor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.darodev.thuglifephotoeditor.image.BitmapHolder;
import com.darodev.thuglifephotoeditor.image.ImageEditMode;
import com.darodev.thuglifephotoeditor.image.ImageEditor;
import com.darodev.thuglifephotoeditor.image.layer.ImageCenter;
import com.darodev.thuglifephotoeditor.image.layer.ImageLayerEditor;
import com.darodev.thuglifephotoeditor.touch.RotationGestureDetector;
import com.darodev.thuglifephotoeditor.utility.BitmapUtility;
import com.darodev.thuglifephotoeditor.utility.ConfigUtility;
import com.darodev.thuglifephotoeditor.touch.PointPair;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class EditorActivity extends AppCompatActivity implements RotationGestureDetector.OnRotationGestureListener {
    private ImageEditor imageEditor;
    private ImageLayerEditor imageLayerEditor;
    private RotationGestureDetector rotationDetector;

    private AdView adView;
    private ConfigUtility configUtility;
    private Resources resources;
    private ImageView editImageView;
    private ImageButton btnRotate, btnRemove;
    private TextView textEditMode;

    static final int REQUEST_IMAGE_CAPTURE = 5600;
    static final int REQUEST_CAMERA_PERMISSION = 5601;
    static final int SELECT_IMAGE = 5602;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        rotationDetector = new RotationGestureDetector(this);

        resources = getResources();
        configUtility = createConfigUtility();
        imageLayerEditor = createImageLayerController();
        imageEditor = new ImageEditor(BitmapHolder.EMPTY, configUtility);

        btnRotate = findViewById(R.id.btn_rotate);
        btnRemove = findViewById(R.id.btn_remove);
        textEditMode = findViewById(R.id.txt_edit_mode);

        editImageView = findViewById(R.id.view_edit_image);
        editImageView.post(new Runnable() {
            @Override
            public void run() {
                saveEditImageDimensions();
            }
        });

        updateRemoveBitmapButton();
        updateEditModeDisplay();

        prepareLayerListener();
        prepareAds();
    }

    private ConfigUtility createConfigUtility(){
        return new ConfigUtility(
                resources,
                getSharedPreferences(resources.getString(R.string.app_name), Context.MODE_PRIVATE));
    }

    private ImageLayerEditor createImageLayerController(){
        return new ImageLayerEditor(
                this.getApplicationContext(),
                (ImageView) findViewById(R.id.view_image_default_layer),
                (FrameLayout) findViewById(R.id.layout_image_layer));
    }

    private void updateRemoveBitmapButton(){
        setImageButtonEnabled(btnRemove, imageLayerEditor.hasTopLayer());
    }

    private void setImageButtonEnabled(ImageButton button, boolean enabled){
        button.setAlpha(enabled ? 1F : ConfigUtility.DISABLED_BUTTON_ALPHA);
        button.setEnabled(enabled);
    }

    private void prepareLayerListener() {
        findViewById(R.id.layout_image_layer).setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                rotationDetector.onTouchEvent(motionEvent);
                updateEditModeDisplay();

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    imageLayerEditor.setCurrentEditModeByTouchCount(motionEvent.getPointerCount());
                    updateEditModeDisplay();
                    processMotionEvent(motionEvent);
                    return true;
                }else{
                    if(motionEvent.getPointerCount() == 1){
                        imageLayerEditor.resetScaleStartPointPair();
                        imageLayerEditor.updatePrevScaleRotation();
                        imageLayerEditor.changeCurrentEditMode(ImageEditMode.NONE);
                        updateEditModeDisplay();
                    }
                }

                return false;
            }
        });
    }

    private void processMotionEvent(MotionEvent motionEvent){
        if(imageLayerEditor.isNotBetweenEditModeSwitch()){
            if(imageLayerEditor.getCurrentEditMode() == ImageEditMode.MOVE){
                imageLayerEditor.processTopLayerMove(new ImageCenter(motionEvent.getX(), motionEvent.getY()));
            }else if(imageLayerEditor.getCurrentEditMode() == ImageEditMode.ROTATE_RESIZE){
                imageLayerEditor.processTopLayerResizeRotate(new PointPair(motionEvent));
            }
        }
    }

    private void updateEditModeDisplay(){
        textEditMode.setText(imageLayerEditor.getCurrentEditMode().toString());
    }

    private void saveEditImageDimensions(){
        configUtility.save(R.string.key_edit_image_width, editImageView.getMeasuredWidth());
    }

    public void onCamera(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            askToSave();
            dispatchTakePictureIntent();
        } else {
            requestCameraPermission();
        }
    }

    public void onFolder(View view) {
        askToSave();
        dispatchSelectPictureIntent();
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

    private void dispatchSelectPictureIntent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
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
        if ((requestCode == REQUEST_IMAGE_CAPTURE || requestCode == SELECT_IMAGE) && resultCode == RESULT_OK) {
            processPictureInsert(BitmapUtility.getFromIntentData(data, getApplicationContext()));
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private void processPictureInsert(BitmapHolder bitmapHolder) {
        imageEditor.recycleBitmap();
        imageEditor = new ImageEditor(bitmapHolder, configUtility);
        refreshImageBitmap();
        imageLayerEditor.reset();
    }

    private void askToSave(){
        if(imageEditor.isImageEdited()){
            // TODO save ask

        }
    }

    private void refreshImageBitmap(){
        editImageView.setImageBitmap(imageEditor.getImage());
    }

    public void onRotate(View view){
        imageEditor.rotateImage();
        refreshImageBitmap();
    }

    public void onAdd(View view){
        imageEditor.setImageIsEdited();

        final ImageView imageView = new ImageView(getApplicationContext());
        imageView.setLayoutParams((findViewById(R.id.view_image_default_layer)).getLayoutParams());
        imageView.setImageBitmap(BitmapUtility.rotate(
                BitmapFactory.decodeResource(getResources(), R.mipmap.ic_2),
                imageEditor.isImageRotated() ? 90 : 0));
        imageView.setDrawingCacheEnabled(true);

        imageLayerEditor.addLayer(imageView);
        updateRotateButton();
        updateRemoveBitmapButton();

//        imageView.post(new Runnable() {
//            @Override
//            public void run() {
//                imageLayerEditor.addLayer(imageView);
//                updateRotateButton();
//                updateRemoveBitmapButton();
//            }
//        });
//        imageView.invalidate();
    }

    public void onRemove(View view){
        imageLayerEditor.removeTopLayer();

        updateRotateButton();
        updateRemoveBitmapButton();
    }

    private void updateRotateButton(){
        setImageButtonEnabled(btnRotate, !imageLayerEditor.hasTopLayer());
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

    @Override
    public void OnRotation(RotationGestureDetector rotationDetector) {
        if(imageLayerEditor.getCurrentEditMode() == ImageEditMode.ROTATE_RESIZE){
            imageLayerEditor.setCurrentRotation(rotationDetector.getAngle());
        }
    }
}
