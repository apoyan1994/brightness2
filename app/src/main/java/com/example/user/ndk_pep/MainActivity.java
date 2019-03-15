package com.example.user.ndk_pep;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, OnBitmapLoadListener {

    private LinearLayout actionLayout;
    private ImageView imageView;
    private Bitmap originalBitmap;
    private Button loadImageButton;

    private boolean isLayoutVisible = false;
    private boolean makeNegative = false;
    private boolean isLoadButtonVisible = true;

    private float brightness;
    private float actionLayoutHeight;
    private int currentHeight;
    private int currentWidth;


    private final int colourRed = 0;
    private final int colourGreen = 1;
    private final int colourBlue = 2;
    static final int REQUEST_SELECTED_IMAGE = 1;

    private boolean[] myRgb = {
            true, true, true
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        setListeners();
    }

    void initData() {
        imageView = (ImageView) findViewById(R.id.ndk_imageView);
        actionLayout = (LinearLayout) findViewById(R.id.actions_set);
        loadImageButton = (Button) findViewById(R.id.ndk_load_image);

        findViewById(R.id.ndk_imageView).setOnClickListener(this);
        findViewById(R.id.actions_set).setOnClickListener(this);

        actionLayout.post(new Runnable() {
            @Override
            public void run() {
                actionLayoutHeight = actionLayout.getHeight();
                currentWidth = imageView.getWidth();
                currentHeight = imageView.getHeight();
            }
        });

    }

    private void setListeners() {
        findViewById(R.id.ndk_imageView).setOnLongClickListener(this);
        findViewById(R.id.actions_set).setOnLongClickListener(this);

        loadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
                findViewById(R.id.ndk_load_image).setVisibility(View.GONE);
                isLoadButtonVisible = false;
            }
        });

        ((CheckBox) findViewById(R.id.red_checkBox))
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        myRgb[colourBlue] = isChecked;
                        adjustBrightness();

                    }
                });

        ((CheckBox) findViewById(R.id.green_checkBox))
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        myRgb[colourGreen] = isChecked;
                        adjustBrightness();
                    }
                });

        ((CheckBox) findViewById(R.id.blue_checkBox))
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        myRgb[colourRed] = isChecked;
                        adjustBrightness();
                    }
                });

        ((SeekBar) findViewById(R.id.seekBar)).
                setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        brightness = progress;
                        adjustBrightness();

                    }

                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });

        findViewById(R.id.ndk_imageButton).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), JavaVersionActivity.class);
                        startActivity(intent);
                    }
                });

        findViewById(R.id.negative_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeNegative = true;
                adjustBrightness();
            }
        });
    }

    private void adjustBrightness() {
        if (originalBitmap == null) {
            Tools.showToast(this, "don't have image");
            return;
        }

        long start = System.currentTimeMillis();

        Bitmap bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        changeBrightness(bitmap, makeNegative, brightness, myRgb);

        long time = System.currentTimeMillis() - start;
        ((TextView) findViewById(R.id.ndk_textClock)).setText("" + time);

        imageView.setImageBitmap(bitmap);
        makeNegative = false;
    }

    void moveLayout() {
        float value = Tools.convertDpToPixel(actionLayoutHeight, this);
        Tools.startAnimation(isLayoutVisible, actionLayout, value);
        isLayoutVisible = !isLayoutVisible;
    }

    private void dispatchTakePictureIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK)
                .setType("image/*");
        startActivityForResult(intent, REQUEST_SELECTED_IMAGE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() != R.id.actions_set) {
            if (isLoadButtonVisible) {
                loadImageButton.setVisibility(View.GONE);
                isLoadButtonVisible = false;
            } else {
                loadImageButton.setVisibility(View.VISIBLE);
                isLoadButtonVisible = true;
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (v.getId() != R.id.actions_set)
            moveLayout();
        return true;
    }

    @Override
    public void setBitmap(Bitmap bitmap) {
        originalBitmap = bitmap;

        if (!isLayoutVisible) {
            moveLayout();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECTED_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }

            BitmapLoader bitmapLoader = new BitmapLoader(imageView, this, currentWidth, currentHeight);
            bitmapLoader.execute(data);
        }
    }

    public native void changeBrightness(Bitmap bmp, boolean makeNegative, float brightness, boolean[] rgb_exist);

    static {
        System.loadLibrary("changeBrightness");
    }
}
