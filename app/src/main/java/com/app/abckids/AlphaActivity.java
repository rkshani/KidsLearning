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
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;


public class AlphaActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    private static final String TAG = "AlphaActivity";
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
        setContentView(R.layout.activity_alpha);

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
        audio = (AudioManager) getSystemService(AUDIO_SERVICE);
        globalvBlue = new GlobalvBlue();
        type = getIntent().getExtras().getString("type");
        nextBtn = (ImageView) findViewById(R.id.nextId);
        playBtn = (ImageView) findViewById(R.id.playId);
        prevBtn = (ImageView) findViewById(R.id.prevId);
        nextBtn.setOnTouchListener(this);
        playBtn.setOnTouchListener(this);
        prevBtn.setOnTouchListener(this);
        itemImage = (ImageView) findViewById(R.id.itemImageId);
        bgImage = (RelativeLayout) findViewById(R.id.bgLayout);
        bgcolor = (RelativeLayout) findViewById(R.id.bgcolorLayout);
        playerr = MediaPlayer.create(this, R.raw.intro_01);
        playerr.start();
        playerr.setLooping(true);
        nextBtn.setOnClickListener(this);
        playBtn.setOnClickListener(this);
        prevBtn.setOnClickListener(this);
        itemImage.setOnClickListener(this);
        imghomeee.setVisibility(4);
        totalItem = resourcePool.icon1Images.length;
        itemImage.setImageResource(resourcePool.icon1Images[currentPosition].intValue());
        mediaPlayer = MediaPlayer.create(this, resourcePool.icon1Sound[currentPosition].intValue());
        bgImage.setBackgroundResource(R.drawable.abc_bg);
        bgcolor.setBackgroundColor(Color.parseColor("#30054e"));
        mediaPlayer.start();
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
                        AlphaActivity.this.interstitialAd = interstitialAd;
                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        AlphaActivity.this.interstitialAd = null;
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
                                        AlphaActivity.this.interstitialAd = null;
                                        if (isBackPress) {
                                            exitByBackKey();
                                        } else {
                                            loadAd();
                                        }

                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {

                                    }
                                });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        interstitialAd = null;

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
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                mediaPlayer = MediaPlayer.create(this, resourcePool.icon1Sound[currentPosition].intValue());
                mediaPlayer.start();
                return;
            case R.id.nextId:
                mediaPlayer.stop();
                gotoNext();
                return;
            case R.id.playId:
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                mediaPlayer = MediaPlayer.create(this, resourcePool.icon1Sound[currentPosition].intValue());
                mediaPlayer.start();
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
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (interstitialAd != null) {
            interstitialAd.show(this);
        } else {
            loadAd();
            gotoPrevious();
        }
    }

    private void gotoNext() {
        currentPosition++;
        updateNextButton();
        updatePreviousButton();
        if (currentPosition >= 0 && currentPosition < totalItem) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            itemImage.setImageResource(resourcePool.icon1Images[currentPosition].intValue());
            mediaPlayer = MediaPlayer.create(this, resourcePool.icon1Sound[currentPosition].intValue());
            mediaPlayer.start();
        }
    }

    private void updateNextButton() {
        if (currentPosition == totalItem - 1) {
            nextBtn.setAlpha(0.5f);
            nextBtn.setClickable(false);
            imghomeee.setClickable(true);
            imghomeee.setVisibility(0);
            playBtn.setVisibility(4);
            return;
        }
        nextBtn.setAlpha(1.0f);
        nextBtn.setClickable(true);
        imghomeee.setClickable(false);
        imghomeee.setVisibility(4);
        playBtn.setVisibility(0);
    }

    private void updatePreviousButton() {
        if (currentPosition == 0) {
            prevBtn.setAlpha(0.5f);
            prevBtn.setClickable(false);
            return;
        }
        prevBtn.setAlpha(1.0f);
        prevBtn.setClickable(true);
    }

    private void gotoPrevious() {
        currentPosition--;
        updateNextButton();
        updatePreviousButton();
        if (currentPosition >= 0 && currentPosition < totalItem) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            itemImage.setImageResource(resourcePool.icon1Images[currentPosition].intValue());
            mediaPlayer = MediaPlayer.create(AlphaActivity.this, resourcePool.icon1Sound[currentPosition].intValue());
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
        playerr.pause();
        mediaPlayer.stop();
        super.onUserLeaveHint();
    }

    protected void onPause() {
        super.onPause();
        if (!((PowerManager) getSystemService(POWER_SERVICE)).isScreenOn()) {
            playerr.pause();
            mediaPlayer.stop();
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
        playerr.start();
    }

    public void btnClick() {
        getPackageName();
        imghomee = (ImageView) findViewById(R.id.ximgvwHome2);
        imghomee.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                int num = AlphaActivity.this.globalvBlue.getNum() + 1;
                AlphaActivity.this.globalvBlue.setNum(num);
                if (num % 3 == 0) {
                    AlphaActivity.this.mediaPlayer.stop();
                } else {
                    AlphaActivity.this.mediaPlayer.stop();
                    if (interstitialAd != null) {
                        interstitialAd.show(AlphaActivity.this);
                        isBackPress = true;
                    } else {
                        AlphaActivity.this.exitByBackKey();
                    }

                }
            }
        });
        imghomeee = (ImageView) findViewById(R.id.ximgvwHome3);
        imghomeee.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                AlphaActivity.this.currentPosition = -1;
                AlphaActivity.this.gotoNext();
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
                audio.adjustStreamVolume(3, 1, 1);
                return true;
            case 25:
                audio.adjustStreamVolume(3, -1, 1);
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
