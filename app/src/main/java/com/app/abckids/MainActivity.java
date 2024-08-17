package com.app.abckids;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity /*implements View.OnClickListener*/ {
    private static final String TAG = "MainActivity";
    private AudioManager audio;
    private long mLastClickTime = 0;
    MediaPlayer playerr;
    RecyclerView recyclerView;
    List<Integer> items = new ArrayList<>();
    private int currentPosition = 0;
    private ImageView next, pre;
    private AdView adView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GlobalvBlue.banner_ad_unit_id = getResources().getString(R.string.banner_ad_unit_id);
        GlobalvBlue.inter_ad_unit_id = getResources().getString(R.string.inter_ad_unit_id);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.audio = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (VERSION.SDK_INT < 16) {
            getWindow().setFlags(1024, 1024);
        }
        if (!BaseActivity.isInternetOn(this)) {
            alert(this);
        }
        this.playerr = MediaPlayer.create(this, R.raw.intro_01);
        this.playerr.start();
        this.playerr.setLooping(true);
        requestWindowFeature(1);

        setContentView(R.layout.activity_main);

        showAds();
        initView();
        updatePreviousButton();
        updateNextButton();
    }

    private void showAds() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        MobileAds.setRequestConfiguration(
                new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("1B97BA0D890D76F46C622E853310C208"))
                        .build());

        adView = findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }


    private void initView() {

        next = findViewById(R.id.next);
        pre = findViewById(R.id.pre);
        recyclerView = findViewById(R.id.rv_Item);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.smoothScrollToPosition(items.size());
                currentPosition = 1;
                updateNextButton();
                updatePreviousButton();
            }
        });

        pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.smoothScrollToPosition(0);
                currentPosition = 0;
                updateNextButton();
                updatePreviousButton();
            }
        });


        items.add(R.drawable.icon1);
        items.add(R.drawable.icon3);
        items.add(R.drawable.icon5);
        items.add(R.drawable.icon7);
        items.add(R.drawable.icon2);
        items.add(R.drawable.icon4);
        items.add(R.drawable.icon6);
        items.add(R.drawable.icon8);

        recyclerView.setAdapter(new RvIconAdapter(items, this, playerr));

    }

    @SuppressLint("ResourceAsColor")
    public void alert(final Context context) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setTitle("No Internet Connection");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setCancelable(false);
        builder.setMessage("Please check your internet connection.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                dialog.cancel();
            }
        });

        androidx.appcompat.app.AlertDialog dialog = builder.show();

        try {
            TextView textView = (TextView) dialog.findViewById(android.R.id.message);
            Typeface face = ResourcesCompat.getFont(this, R.font.poppinsmedium);
            textView.setTypeface(face);
            textView.setTextColor(R.color.ColorBlack);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }

    }


    public void exitByBackKey() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, 5);
        builder.setTitle(getString(R.string.app_name));
        builder.setIcon(R.drawable.logo);
        builder.setMessage("Thank You For Rating Us");
        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                MainActivity.this.playerr.stop();
                MainActivity.this.finish();
            }
        });
        builder.setNegativeButton("Add Your Rating", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                MainActivity.this.playerr.stop();
                String packageName = MainActivity.this.getPackageName();
                MainActivity mainActivity;
                StringBuilder stringBuilder;
                try {
                    mainActivity = MainActivity.this;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("market://details?id=");
                    stringBuilder.append(packageName);
                    mainActivity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(stringBuilder.toString())));
                } catch (ActivityNotFoundException unused) {
                    mainActivity = MainActivity.this;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("http://play.google.com/store/apps/details?id=");
                    stringBuilder.append(packageName);
                    mainActivity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(stringBuilder.toString())));
                }
            }
        });
        builder.show();
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4) {
            exitByBackKey();
        }
        return false;
    }


    private void updateNextButton() {
        if (currentPosition == 0) {
            next.setAlpha(1.0f);
        } else {
            next.setAlpha(0.5f);
        }

    }

    private void updatePreviousButton() {
        if (currentPosition == 0) {
            pre.setAlpha(0.5f);
        } else {
            pre.setAlpha(1.0f);
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        exitByBackKey();
    }

    protected void onUserLeaveHint() {
        this.playerr.pause();
        super.onUserLeaveHint();
    }

    protected void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
        if (!((PowerManager) getSystemService(POWER_SERVICE)).isScreenOn()) {
            this.playerr.pause();
        }
    }

    protected void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }


    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    public void onRestart() {
        super.onRestart();
        this.playerr.start();
    }

}
