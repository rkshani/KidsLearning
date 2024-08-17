package com.app.abckids;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;


public class FruitsActivity extends AppCompatActivity implements OnClickListener, OnTouchListener {
    private AudioManager audio;
    RelativeLayout bgImage;
    RelativeLayout bgcolor;
    private int currentPosition = 0;
    GlobalvBlue globalvBlue;
    ImageView imghomee;
    ImageView imghomeee;
    ImageView itemImage = null;
    private MediaPlayer mediaPlayer = null;
    ImageView nextBtn = null;
    ImageView playBtn = null;
    MediaPlayer playerr;
    ImageView prevBtn = null;
    ResourcePool resourcePool = new ResourcePool();
    private int totalItem = 0;
    private String type = "";
    private InterstitialAd interstitialAd;
    private Boolean isBackPress = false;
    private int countForAd = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_fruits);

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        loadAd();

        if (!BaseActivity.isInternetOn(this)) {
            alert(this);
        }
        btnClick();
        this.audio = (AudioManager) getSystemService(AUDIO_SERVICE);
        this.globalvBlue = new GlobalvBlue();
        this.type = getIntent().getExtras().getString("type");
        this.nextBtn = (ImageView) findViewById(R.id.nextId);
        this.playBtn = (ImageView) findViewById(R.id.playId);
        this.prevBtn = (ImageView) findViewById(R.id.prevId);
        this.nextBtn.setOnTouchListener(this);
        this.playBtn.setOnTouchListener(this);
        this.prevBtn.setOnTouchListener(this);
        this.itemImage = (ImageView) findViewById(R.id.itemImageId);
        this.bgImage = (RelativeLayout) findViewById(R.id.bgLayout);
        this.bgcolor = (RelativeLayout) findViewById(R.id.bgcolorLayout);
        this.playerr = MediaPlayer.create(this, R.raw.intro_01);
        this.playerr.start();
        this.playerr.setLooping(true);
        this.nextBtn.setOnClickListener(this);
        this.playBtn.setOnClickListener(this);
        this.prevBtn.setOnClickListener(this);
        this.itemImage.setOnClickListener(this);
        this.imghomeee.setVisibility(4);
        this.totalItem = this.resourcePool.icon3Images.length;
        this.itemImage.setImageResource(this.resourcePool.icon3Images[this.currentPosition].intValue());
        this.mediaPlayer = MediaPlayer.create(this, this.resourcePool.icon3Sound[this.currentPosition].intValue());
        this.bgImage.setBackgroundResource(R.drawable.bg_fruit);
        this.bgcolor.setBackgroundColor(Color.parseColor("#265c22"));
        this.mediaPlayer.start();
        updatePreviousButton();
    }

    public void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                this,
                GlobalvBlue.inter_ad_unit_id,
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        FruitsActivity.this.interstitialAd = interstitialAd;
                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        FruitsActivity.this.interstitialAd = null;
                                        if (isBackPress) {
                                            exitByBackKey();
                                        } else {
                                            loadAd();
                                        }
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        FruitsActivity.this.interstitialAd = null;
                                        if (isBackPress) {
                                            exitByBackKey();
                                        } else {
                                            loadAd();
                                        }
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        // Called when fullscreen content is shown.
                                    }
                                });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        interstitialAd = null;

                        String error =
                                String.format(
                                        "domain: %s, code: %d, message: %s",
                                        loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());

                    }

                });
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

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.itemImageId:
                this.mediaPlayer.stop();
                this.mediaPlayer.release();
                this.mediaPlayer = null;
                this.mediaPlayer = MediaPlayer.create(this, this.resourcePool.icon3Sound[this.currentPosition].intValue());
                this.mediaPlayer.start();
                return;
            case R.id.nextId:
                this.mediaPlayer.stop();
                gotoNext();
                return;
            case R.id.playId:
                this.mediaPlayer.stop();
                this.mediaPlayer.release();
                this.mediaPlayer = null;
                this.mediaPlayer = MediaPlayer.create(this, this.resourcePool.icon3Sound[this.currentPosition].intValue());
                this.mediaPlayer.start();
                return;
            case R.id.prevId:
                ++countForAd;
                mediaPlayer.stop();
                if (countForAd == 2) {
                    showInterstitial();
                    countForAd = 0;
                } else {
                    gotoPrevious();
                }
                return;
            default:
                return;
        }
    }

    private void showInterstitial() {
        if (interstitialAd != null) {
            interstitialAd.show(this);
        } else {
            loadAd();
            gotoPrevious();
        }
    }

    private void gotoNext() {
        this.currentPosition++;
        updateNextButton();
        updatePreviousButton();
        if (this.currentPosition >= 0 && this.currentPosition < this.totalItem) {
            this.mediaPlayer.stop();
            this.mediaPlayer.release();
            this.mediaPlayer = null;
            this.itemImage.setImageResource(this.resourcePool.icon3Images[this.currentPosition].intValue());
            this.mediaPlayer = MediaPlayer.create(this, this.resourcePool.icon3Sound[this.currentPosition].intValue());
            this.mediaPlayer.start();
        }
    }

    private void updateNextButton() {
        if (this.currentPosition == this.totalItem - 1) {
            this.nextBtn.setAlpha(0.5f);
            this.nextBtn.setClickable(false);
            this.imghomeee.setClickable(true);
            this.imghomeee.setVisibility(0);
            this.playBtn.setVisibility(4);
            return;
        }
        this.nextBtn.setAlpha(1.0f);
        this.nextBtn.setClickable(true);
        this.imghomeee.setClickable(false);
        this.imghomeee.setVisibility(4);
        this.playBtn.setVisibility(0);
    }

    private void updatePreviousButton() {
        if (this.currentPosition == 0) {
            this.prevBtn.setAlpha(0.5f);
            this.prevBtn.setClickable(false);
            return;
        }
        this.prevBtn.setAlpha(1.0f);
        this.prevBtn.setClickable(true);
    }

    private void gotoPrevious() {
        currentPosition--;
        updateNextButton();
        updatePreviousButton();
        if (currentPosition >= 0 && currentPosition < totalItem) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            itemImage.setImageResource(resourcePool.icon3Images[currentPosition].intValue());
            mediaPlayer = MediaPlayer.create(FruitsActivity.this, resourcePool.icon3Sound[currentPosition].intValue());
            mediaPlayer.start();
        }
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case 0:
                view.setAlpha(0.5f);
                break;
            case 1:
                view.setAlpha(1.0f);
                break;
        }
        return false;
    }

    public void onBackPressed() {
        super.onBackPressed();
        if (interstitialAd != null) {
            interstitialAd.show(this);
            isBackPress = true;
        } else {
            exitByBackKey();
        }
    }

    protected void onUserLeaveHint() {
        this.playerr.pause();
        this.mediaPlayer.stop();
        super.onUserLeaveHint();
    }

    protected void onPause() {
        super.onPause();
        if (!((PowerManager) getSystemService(POWER_SERVICE)).isScreenOn()) {
            this.playerr.pause();
            this.mediaPlayer.stop();
        }
    }

    @Override
    protected void onResume() {
        if (!playerr.isPlaying() && playerr != null) {
            playerr.start();
        }
        if (!mediaPlayer.isPlaying() && mediaPlayer != null) {
            mediaPlayer.start();
        }
        super.onResume();
    }

    public void onRestart() {
        super.onRestart();
        this.playerr.start();
    }

    public void btnClick() {
        getPackageName();
        this.imghomee = (ImageView) findViewById(R.id.ximgvwHome2);
        this.imghomee.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                int num = FruitsActivity.this.globalvBlue.getNum() + 1;
                FruitsActivity.this.globalvBlue.setNum(num);
                if (num % 3 == 0) {
                    FruitsActivity.this.mediaPlayer.stop();
                } else {
                    FruitsActivity.this.mediaPlayer.stop();
                    if (interstitialAd != null) {
                        interstitialAd.show(FruitsActivity.this);
                        isBackPress = true;
                    } else {
                        FruitsActivity.this.exitByBackKey();
                    }

                }
            }
        });
        this.imghomeee = (ImageView) findViewById(R.id.ximgvwHome3);
        this.imghomeee.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                FruitsActivity.this.currentPosition = -1;
                FruitsActivity.this.gotoNext();
            }
        });
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4) {
            int num = this.globalvBlue.getNum() + 1;
            this.globalvBlue.setNum(num);
            if (num % 3 == 0) {
                this.mediaPlayer.stop();
            } else {
                this.mediaPlayer.stop();
                if (interstitialAd != null) {
                    interstitialAd.show(this);
                    isBackPress = true;
                } else {
                    exitByBackKey();
                }
            }
        }
        switch (i) {
            case 24:
                this.audio.adjustStreamVolume(3, 1, 1);
                return true;
            case 25:
                this.audio.adjustStreamVolume(3, -1, 1);
                return true;
            default:
                return false;
        }
    }

    public void exitByBackKey() {
        this.mediaPlayer.stop();
        this.mediaPlayer.release();
        this.mediaPlayer = null;
        this.playerr.stop();
        this.playerr.release();
        this.playerr = null;
        finish();
    }

}
