package com.darodev.thuglifephotoeditor;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.darodev.thuglifephotoeditor.utility.ConfigHelper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class EditorActivity extends AppCompatActivity {
    private AdView adView;
    private ConfigHelper configHelper;
    private Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        resources = getResources();

        configHelper = new ConfigHelper(
                resources,
                getSharedPreferences(resources.getString(R.string.app_name), Context.MODE_PRIVATE));


        if (configHelper.adsEnabled()) {
            prepareAds();
        }
    }

    private void prepareAds(){
        LinearLayout add_holder = findViewById(R.id.layout_ad);
        add_holder.addView(getAdView());
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
