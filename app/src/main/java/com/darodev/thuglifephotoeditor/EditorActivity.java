package com.darodev.thuglifephotoeditor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.darodev.thuglifephotoeditor.image.BitmapHolder;
import com.darodev.thuglifephotoeditor.image.ImageBitmap;
import com.darodev.thuglifephotoeditor.image.ImageEditMode;
import com.darodev.thuglifephotoeditor.image.ImageEditor;
import com.darodev.thuglifephotoeditor.image.layer.ImageCenter;
import com.darodev.thuglifephotoeditor.image.layer.ImageLayerEditor;
import com.darodev.thuglifephotoeditor.touch.RotationGestureDetector;
import com.darodev.thuglifephotoeditor.utility.BitmapUtility;
import com.darodev.thuglifephotoeditor.utility.ConfigUtility;
import com.darodev.thuglifephotoeditor.touch.PointPair;
import com.darodev.thuglifephotoeditor.utility.permission.PermissionControl;
import com.darodev.thuglifephotoeditor.utility.permission.Permission;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class EditorActivity extends AppCompatActivity implements RotationGestureDetector.OnRotationGestureListener {
    private ImageEditor imageEditor;
    private ImageLayerEditor imageLayerEditor;
    private RotationGestureDetector rotationDetector;

    private PermissionControl permissionControl;

    private AdView adView;
    private ConfigUtility configUtility;
    private Resources resources;
    private ImageView editImageView;
    private ImageButton btnRotate, btnRemove, btnSave;
    private TextView textEditMode;
    private LinearLayout guiLayoutTopBar, guiLayoutBottomBar;
    private Dialog addBitmapDialog;

    static final int REQUEST_IMAGE_CAPTURE = 5600;
    static final int SELECT_IMAGE = 5602;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        rotationDetector = new RotationGestureDetector(this);

        resources = getResources();
        permissionControl = new PermissionControl(getApplicationContext(), this);
        configUtility = createConfigUtility();
        imageLayerEditor = createImageLayerController();
        imageEditor = new ImageEditor(BitmapHolder.EMPTY, configUtility);

        btnRotate = findViewById(R.id.btn_rotate);
        btnRemove = findViewById(R.id.btn_remove);
        btnSave = findViewById(R.id.btn_save);
        textEditMode = findViewById(R.id.txt_edit_mode);
        guiLayoutTopBar = findViewById(R.id.layout_gui_top_bar);
        guiLayoutBottomBar = findViewById(R.id.layout_gui_bottom_bar);

        editImageView = findViewById(R.id.view_edit_image);
        editImageView.setDrawingCacheEnabled(true);
        editImageView.post(new Runnable() {
            @Override
            public void run() {
                saveEditImageDimensions();
            }
        });

        refreshGui();

        prepareLayerListener();
        prepareAds();
    }

    private void refreshGui(){
        updateRotateButton();
        updateRemoveBitmapButton();
        updateEditModeDisplay();
        updateSaveButton();
    }

    private ConfigUtility createConfigUtility(){
        return new ConfigUtility(
                resources,
                getSharedPreferences(resources.getString(R.string.app_name), Context.MODE_PRIVATE));
    }

    private ImageLayerEditor createImageLayerController(){
        return new ImageLayerEditor(this.getApplicationContext(), (FrameLayout) findViewById(R.id.layout_image_layer));
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
                if(imageLayerEditor.hasTopLayer() && motionEvent.getPointerCount() <= 2){
                    rotationDetector.onTouchEvent(motionEvent);
                    updateEditModeDisplay();

                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                        updateGuiBarsVisibility(false);
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
                            updateGuiBarsVisibility(true);
                        }
                        return false;
                    }
                }else{
                    return false;
                }
            }
        });
    }

    private void updateGuiBarsVisibility(boolean visible){
        int visibility = visible ? View.VISIBLE : View.INVISIBLE;
        if(guiLayoutTopBar.getVisibility() != visibility){
            guiLayoutBottomBar.setVisibility(visibility);
            guiLayoutTopBar.setVisibility(visibility);
        }
    }

    private void processMotionEvent(MotionEvent motionEvent){
        if(imageLayerEditor.isNotBetweenEditModeSwitch()){
            if(imageLayerEditor.getCurrentEditMode() == ImageEditMode.MOVE){
                imageLayerEditor.processTopLayerMove(new ImageCenter(motionEvent.getX(), motionEvent.getY()));
            }else if(imageLayerEditor.getCurrentEditMode() == ImageEditMode.MOVE_ROTATE_RESIZE){
                imageLayerEditor.processTopLayerResizeRotate(new PointPair(motionEvent));
            }
        }
    }

    private void updateEditModeDisplay(){
        textEditMode.setText(imageLayerEditor.getCurrentEditMode().getName());
    }

    private void saveEditImageDimensions(){
        configUtility.save(R.string.key_edit_image_width, editImageView.getMeasuredWidth());
    }

    public void onCamera(View view) {
        if (permissionControl.isPermissionGranted(Permission.CAMERA)) {
            askToSave();
            dispatchTakePictureIntent();
        } else {
            permissionControl.requestPermission(Permission.CAMERA);
        }
    }

    public void onSave(View view) {
        if (permissionControl.isPermissionGranted(Permission.WRITE_STORAGE)) {
            saveImage();
        } else {
            permissionControl.requestPermission(Permission.WRITE_STORAGE);
        }
    }

    private void askToSave(){
        if(imageEditor.isImageEdited()){
            // TODO save ask

        }
    }

    private void saveImage(){
        Bitmap imageBitmap = getImageBitmap();



        // TODO save to file
        processPictureInsert(new BitmapHolder(imageBitmap, 0));
    }

    private Bitmap getImageBitmap(){
        editImageView.invalidate();
        Bitmap bitmap = editImageView.getDrawingCache();
        drawAllBitmapsToCanvas(new Canvas(bitmap));
        return bitmap;
    }

    private void drawAllBitmapsToCanvas(Canvas canvas){
        for(int index = imageLayerEditor.getFreeIndex(); index > 0; index --){
            BitmapUtility.drawViewBitmapOnCanvas(canvas, imageLayerEditor.getImageLayerLayout().getChildAt(index));
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void onFolder(View view) {
        askToSave();
        dispatchSelectPictureIntent();
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

        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
            dispatchActionBasedOnGrantedPermission(Permission.getByRequestCode(requestCode));
        }
    }

    private void dispatchActionBasedOnGrantedPermission(Permission grantedPermission){
        if(grantedPermission == Permission.CAMERA){
            dispatchTakePictureIntent();
        }else if(grantedPermission == Permission.WRITE_STORAGE) {
            saveImage();
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
        imageLayerEditor.reset();

        refreshImageBitmap();
        refreshGui();
    }

    private void refreshImageBitmap(){
        BitmapUtility.setImageBitmap(editImageView, imageEditor.getImage());
    }

    public void onRotate(View view){
        imageEditor.rotateImage();
        refreshImageBitmap();
    }

    public void onAdd(View view){
        showBitmapSelectionDialog();
    }

    private void showBitmapSelectionDialog(){
        addBitmapDialog = new Dialog(this);
        addBitmapDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addBitmapDialog.setContentView(R.layout.add_bitmap_layout);
        addBitmapDialog.show();
    }

    private void onBitmapSelected(Bitmap bitmap){
        imageEditor.setImageIsEdited();

        final ImageView imageView = new ImageView(getApplicationContext());
        imageView.setLayoutParams((findViewById(R.id.view_image_default_layer)).getLayoutParams());

        BitmapUtility.setImageBitmap(imageView, BitmapUtility.rotate(bitmap, imageEditor.isImageRotated() ? 90 : 0));

        imageLayerEditor.addLayer(imageView);
        refreshGui();
    }

    public void onRemove(View view){
        imageLayerEditor.removeTopLayer();
        refreshGui();
    }

    private void updateRotateButton(){
        setImageButtonEnabled(btnRotate, !imageLayerEditor.hasTopLayer());
    }

    private void updateSaveButton(){
        setImageButtonEnabled(btnSave, imageEditor.isImageEdited() && imageLayerEditor.hasTopLayer());
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

    public void onBitmapClick(View view){
        ImageBitmap imageBitmap = getImageBitmapByViewTagObject(view.getTag());

        if(imageBitmap != ImageBitmap.UNKNOWN){
            onBitmapSelected(BitmapFactory.decodeResource(resources, imageBitmap.getResId()));
        }else{
            Toast.makeText(this, "Selection Error", Toast.LENGTH_SHORT).show();
        }

        addBitmapDialog.hide();
    }

    private ImageBitmap getImageBitmapByViewTagObject(Object tag){
        return tag instanceof String ? ImageBitmap.getByTag((String) tag) : ImageBitmap.UNKNOWN;
    }

    @Override
    public void OnRotation(RotationGestureDetector rotationDetector) {
        if(imageLayerEditor.getCurrentEditMode() == ImageEditMode.MOVE_ROTATE_RESIZE){
            imageLayerEditor.setCurrentRotation(rotationDetector.getAngle());
        }
    }
}
