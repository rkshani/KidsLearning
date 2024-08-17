package com.app.abckids;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
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


public class DrawingActivity extends AppCompatActivity implements OnClickListener, OnTouchListener {
    private AudioManager audio;
    private int currentPosition = 0;
    View drawingView = null;
    DrawingView dv;
    GlobalvBlue globalvBlue;
    ImageView imghomee;
    ImageView imghomeee;
    ImageView itemImage = null;
    private Paint mPaint;
    private MediaPlayer mediaPlayer = null;
    ImageView nextBtn = null;
    LayoutParams params = null;
    ViewGroup parent = null;
    ImageView playBtn = null;
    MediaPlayer playerr;
    private Integer position = Integer.valueOf(0);
    ImageView prevBtn = null;
    ResourcePool resourcePool = new ResourcePool();
    private int totalItem = 0;
    private String type = "";
    private InterstitialAd interstitialAd;
    private Boolean isBackPress = false;
    private int countForAd = 0;

    public class DrawingView extends View {
        private static final float TOUCH_TOLERANCE = 4.0f;
        private Bitmap bm;
        private Paint circlePaint = new Paint();
        private Path circlePath = new Path();
        Context context;
        private Bitmap mBitmap;
        private Paint mBitmapPaint = new Paint(4);
        private Canvas mCanvas;
        public int mHeight;
        private Path mPath = new Path();
        public int mWidth;
        private float mX;
        private float mY;

        public DrawingView(Context context) {
            super(context);
            this.context = context;
            this.circlePaint.setAntiAlias(true);
            this.circlePaint.setColor(Color.rgb(109, 193, 27));
            this.circlePaint.setStyle(Style.STROKE);
            this.circlePaint.setStrokeJoin(Join.MITER);
            this.circlePaint.setStrokeWidth(TOUCH_TOLERANCE);
        }

        protected void onSizeChanged(int i, int i2, int i3, int i4) {
            super.onSizeChanged(i, i2, i3, i4);
            this.mBitmap = Bitmap.createBitmap(i, i2, Config.ARGB_8888);
            this.mCanvas = new Canvas(this.mBitmap);
            if (DrawingActivity.this.type.equals(ResourcePool.DRAWING_ALPHABET)) {
                this.bm = BitmapFactory.decodeResource(getResources(), DrawingActivity.this.resourcePool.capitalStoke[DrawingActivity.this.position.intValue()].intValue());
            } else {
                this.bm = BitmapFactory.decodeResource(getResources(), DrawingActivity.this.resourcePool.numberStroke[DrawingActivity.this.position.intValue()].intValue());
            }
            this.mCanvas.drawBitmap(this.bm, new Rect(0, 0, this.bm.getWidth(), this.bm.getHeight()), new Rect(0, 0, this.mCanvas.getWidth(), this.mCanvas.getHeight()), this.mBitmapPaint);
            if (DrawingActivity.this.type.equals(ResourcePool.DRAWING_ALPHABET)) {
                DrawingActivity.this.totalItem = DrawingActivity.this.resourcePool.capitalStoke.length;
                DrawingActivity.this.itemImage.setImageResource(DrawingActivity.this.resourcePool.capitalStoke[DrawingActivity.this.currentPosition].intValue());
                return;
            }
            DrawingActivity.this.totalItem = DrawingActivity.this.resourcePool.numberStroke.length;
            DrawingActivity.this.itemImage.setImageResource(DrawingActivity.this.resourcePool.numberStroke[DrawingActivity.this.currentPosition].intValue());
        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawBitmap(this.mBitmap, 0.0f, 0.0f, this.mBitmapPaint);
            canvas.drawPath(this.mPath, DrawingActivity.this.mPaint);
            canvas.drawPath(this.circlePath, this.circlePaint);
        }

        private void touch_start(float f, float f2) {
            this.mPath.reset();
            this.mPath.moveTo(f, f2);
            this.mX = f;
            this.mY = f2;
        }

        private void touch_move(float f, float f2) {
            float abs = Math.abs(f - this.mX);
            float abs2 = Math.abs(f2 - this.mY);
            if (abs >= TOUCH_TOLERANCE || abs2 >= TOUCH_TOLERANCE) {
                this.mPath.quadTo(this.mX, this.mY, (this.mX + f) / 2.0f, (this.mY + f2) / 2.0f);
                this.mX = f;
                this.mY = f2;
                this.circlePath.reset();
                this.circlePath.addCircle(this.mX, this.mY, 30.0f, Direction.CW);
            }
        }

        private void touch_up() {
            this.mPath.lineTo(this.mX, this.mY);
            this.circlePath.reset();
            this.mCanvas.drawPath(this.mPath, DrawingActivity.this.mPaint);
            this.mPath.reset();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            switch (motionEvent.getAction()) {
                case 0:
                    touch_start(x, y);
                    invalidate();
                    break;
                case 1:
                    touch_up();
                    invalidate();
                    break;
                case 2:
                    touch_move(x, y);
                    invalidate();
                    break;
            }
            return true;
        }

        public void resetCanvas() {
            this.bm = null;
            this.mBitmap = null;
            System.gc();
        }
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
        //builder.show();

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_drawing);

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
        this.nextBtn.setOnClickListener(this);
        this.nextBtn.setOnTouchListener(this);
        this.prevBtn.setOnClickListener(this);
        this.prevBtn.setOnTouchListener(this);
        this.playBtn.setOnClickListener(this);
        this.playBtn.setOnTouchListener(this);
        this.itemImage = (ImageView) findViewById(R.id.itemImageId);
        this.drawingView = findViewById(R.id.drawingViewId);
        this.playerr = MediaPlayer.create(this, R.raw.intro_01);
        this.playerr.start();
        this.playerr.setLooping(true);
        this.params = (LayoutParams) this.drawingView.getLayoutParams();
        this.dv = new DrawingView(this);
        this.dv.setLayoutParams(this.params);
        this.imghomeee.setVisibility(4);
        this.parent = (ViewGroup) this.drawingView.getParent();
        int indexOfChild = this.parent.indexOfChild(this.drawingView);
        this.parent.removeView(this.drawingView);
        this.parent.addView(this.dv, indexOfChild);
        if (this.type.equals(ResourcePool.DRAWING_ALPHABET)) {
            this.totalItem = this.resourcePool.capitalStoke.length;
        } else {
            this.totalItem = this.resourcePool.numberStroke.length;
        }
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setDither(true);
        this.mPaint.setColor(Color.rgb(109, 193, 27));
        this.mPaint.setStyle(Style.STROKE);
        this.mPaint.setStrokeJoin(Join.ROUND);
        this.mPaint.setStrokeCap(Cap.ROUND);
        this.mPaint.setStrokeWidth(150.0f);
        updatePreviousButton();
        if (this.type.equals(ResourcePool.DRAWING_ALPHABET)) {
            this.mediaPlayer = MediaPlayer.create(this, this.resourcePool.alphabetSound[this.currentPosition].intValue());
            this.mediaPlayer.start();
        } else {
            this.mediaPlayer = MediaPlayer.create(this, this.resourcePool.numberSounds[this.currentPosition].intValue());
            this.mediaPlayer.start();
        }
        if (this.type.equals(ResourcePool.DRAWING_ALPHABET)) {
            this.totalItem = this.resourcePool.capitalStoke.length;
            this.itemImage.setImageResource(this.resourcePool.capitalStoke[this.currentPosition].intValue());
            return;
        }
        this.totalItem = this.resourcePool.numberStroke.length;
        this.itemImage.setImageResource(this.resourcePool.numberStroke[this.currentPosition].intValue());
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
                        DrawingActivity.this.interstitialAd = interstitialAd;
                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        DrawingActivity.this.interstitialAd = null;
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
                                        DrawingActivity.this.interstitialAd = null;
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

    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case 0:
                if (view.getId() == R.id.nextId || view.getId() == R.id.playId || view.getId() == R.id.prevId) {
                    view.setAlpha(0.5f);
                    break;
                }
            case 1:
                if (view.getId() == R.id.nextId || view.getId() == R.id.playId || view.getId() == R.id.prevId) {
                    view.setAlpha(1.0f);
                    break;
                }
        }
        return false;

    }

    public void onClick(View view) {
        int id = view.getId();
        Integer num;
        if (id != R.id.nextId) {
            switch (id) {
                case R.id.playId:
                    changeStroke();
                    return;
                case R.id.prevId:
                    ++countForAd;
                    this.mediaPlayer.stop();
                    if (countForAd == 2) {
                        showInterstitial();
                        countForAd = 0;
                    } else {
                        this.position = Integer.valueOf(this.position.intValue() - 1);
                        changeStroke();
                        gotoPrevious();
                    }
                    return;
                default:
                    return;
            }
        }
        num = this.position;
        this.position = Integer.valueOf(this.position.intValue() + 1);
        this.mediaPlayer.stop();
        changeStroke();
        gotoNext();
    }

    private void showInterstitial() {
        if (interstitialAd != null) {
            interstitialAd.show(this);
        } else {
            loadAd();
            this.position = Integer.valueOf(this.position.intValue() - 1);
            changeStroke();
            gotoPrevious();
        }
    }

    private void changeStroke() {
        updateNextButton();
        updatePreviousButton();
        int indexOfChild = this.parent.indexOfChild(this.dv);
        this.dv.resetCanvas();
        this.dv = null;
        this.parent.removeViewAt(indexOfChild);
        this.dv = new DrawingView(this);
        this.dv.setLayoutParams(this.params);
        this.parent.addView(this.dv, indexOfChild);
    }

    private void gotoNext() {
        this.currentPosition++;
        updateNextButton();
        updatePreviousButton();
        if (this.currentPosition >= 0 && this.currentPosition < this.totalItem) {
            if (this.type.equals(ResourcePool.DRAWING_ALPHABET)) {
                this.mediaPlayer.stop();
                this.mediaPlayer.release();
                this.mediaPlayer = null;
                this.mediaPlayer = MediaPlayer.create(this, this.resourcePool.alphabetSound[this.currentPosition].intValue());
                this.mediaPlayer.start();
                return;
            }
            this.mediaPlayer.stop();
            this.mediaPlayer.release();
            this.mediaPlayer = null;
            this.mediaPlayer = MediaPlayer.create(this, this.resourcePool.numberSounds[this.currentPosition].intValue());
            this.mediaPlayer.start();
        }
    }

    private void gotoPrevious() {
        currentPosition--;
        updateNextButton();
        updatePreviousButton();
        if (currentPosition >= 0 && currentPosition < totalItem) {
            if (type.equals(ResourcePool.DRAWING_ALPHABET)) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                mediaPlayer = MediaPlayer.create(DrawingActivity.this, resourcePool.alphabetSound[currentPosition].intValue());
                mediaPlayer.start();
                return;
            }
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            mediaPlayer = MediaPlayer.create(DrawingActivity.this, resourcePool.numberSounds[currentPosition].intValue());
            mediaPlayer.start();
        }
    }


    private void updateNextButton() {
        if (this.position.intValue() == this.totalItem - 1) {
            this.nextBtn.setAlpha(0.5f);
            this.nextBtn.setClickable(false);
            this.imghomeee.setClickable(true);
            this.imghomeee.setVisibility(0);
            return;
        }
        this.nextBtn.setAlpha(1.0f);
        this.nextBtn.setClickable(true);
        this.imghomeee.setClickable(false);
        this.imghomeee.setVisibility(4);
    }

    private void updatePreviousButton() {
        if (this.position.intValue() == 0) {
            this.prevBtn.setAlpha(0.5f);
            this.prevBtn.setClickable(false);
            return;
        }
        this.prevBtn.setAlpha(1.0f);
        this.prevBtn.setClickable(true);
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
                int num = DrawingActivity.this.globalvBlue.getNum() + 1;
                DrawingActivity.this.globalvBlue.setNum(num);
                if (num % 3 == 0) {
                    DrawingActivity.this.mediaPlayer.stop();
                } else {
                    DrawingActivity.this.mediaPlayer.stop();
                    if (interstitialAd != null) {
                        interstitialAd.show(DrawingActivity.this);
                        isBackPress = true;
                    } else {
                        DrawingActivity.this.exitByBackKey();
                    }
                }
            }
        });
        this.imghomeee = (ImageView) findViewById(R.id.ximgvwHome3);
        this.imghomeee.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                DrawingActivity.this.currentPosition = -1;
                DrawingActivity.this.position = Integer.valueOf(0);
                DrawingActivity.this.changeStroke();
                DrawingActivity.this.gotoNext();
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
