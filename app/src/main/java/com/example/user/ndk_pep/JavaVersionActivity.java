package com.example.user.ndk_pep;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;

/**
 * Created by user on 17-03-16.
 */
public class JavaVersionActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, OnBitmapLoadListener {

    private LinearLayout actionLayout;
    private ImageView imageView;
    private ProgressBar progressBar;
    private Bitmap originalBitmap;
    private Button loadImageButton;

    AsyncTask myAsyncTask;

    private int bitmapOriginalWidth;
    private int bitmapOriginalHeight;
    private int membersCount = 0;
    private int colour;

    private boolean isLayoutVisible = false;
    private boolean isLoadButtonVisible = true;

    private int brightness;
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
        setContentView(R.layout.activity_pure_java);

        initData();
        setListeners();
    }

    void initData() {
        imageView = (ImageView) findViewById(R.id.java_imageView);
        actionLayout = (LinearLayout) findViewById(R.id.actions_set);
        progressBar = (ProgressBar) findViewById(R.id.java_progress_bar);
        loadImageButton = (Button) findViewById(R.id.java_load_image);

        findViewById(R.id.java_imageView).setOnClickListener(this);
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

    void setListeners() {
        findViewById(R.id.java_imageView).setOnLongClickListener(this);
        findViewById(R.id.actions_set).setOnLongClickListener(this);

        loadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
                findViewById(R.id.java_load_image).setVisibility(View.GONE);
                isLoadButtonVisible = false;
            }
        });

        ((CheckBox) findViewById(R.id.red_checkBox))
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        myRgb[colourRed] = isChecked;
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
                        myRgb[colourBlue] = isChecked;
                        adjustBrightness();
                    }
                });

        ((SeekBar) findViewById(R.id.seekBar)).
                setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        brightness = progress;
                        adjustBrightness();
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });

        findViewById(R.id.java_imageButton)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });

        findViewById(R.id.negative_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeNegative();
            }
        });
    }

    void moveLayout() {
        float value = Tools.convertDpToPixel(actionLayoutHeight, this);
        Tools.startAnimation(isLayoutVisible, actionLayout, value);
        isLayoutVisible = !isLayoutVisible;
    }

    void adjustBrightness() {
        if (originalBitmap == null) {
            Tools.showToast(this, "don't have image");
            return;
        }

        AsyncTask<Integer, String, Bitmap> asyncTask = new AsyncTask<Integer, String, Bitmap>() {
            long start;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                if (membersCount > 0) {
                    membersCount--;
                    myAsyncTask.cancel(true);
                }

                membersCount++;
                progressBar.setVisibility(View.VISIBLE);

                myAsyncTask = this;

                start = System.currentTimeMillis();
            }

            @Override
            protected Bitmap doInBackground(Integer... params) {
                int progress = params[0];
                Bitmap changedBitmap = Bitmap.createBitmap(bitmapOriginalWidth, bitmapOriginalHeight, Bitmap.Config.ARGB_8888);
                int colourRed;
                int colourGreen;
                int colourBlue;

                for (int valueX = 0; valueX < bitmapOriginalWidth; valueX++) {
                    for (int valueY = 0; valueY < bitmapOriginalHeight; valueY++) {
                        colour = originalBitmap.getPixel(valueX, valueY);

                        if (myRgb[0])
                            colourRed = colour;
                        else
                            colourRed = 0;

                        if (myRgb[1])
                            colourGreen = colour;
                        else
                            colourGreen = 0;

                        if (myRgb[2])
                            colourBlue = colour;
                        else
                            colourBlue = 0;

                        changedBitmap.setPixel(valueX, valueY, Color.argb(255 - progress, Color.red(colourRed),
                                Color.green(colourGreen), Color.blue(colourBlue)));
                    }

                    if (isCancelled()) return null;
                    publishProgress("");

                }

                return changedBitmap;
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);

                long time = System.currentTimeMillis() - start;

                ((TextView) findViewById(R.id.java_textClock)).setText("" + time);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);

                long time = System.currentTimeMillis() - start;

                ((TextView) findViewById(R.id.java_textClock)).setText("" + time);

                membersCount--;
                imageView.setImageBitmap(bitmap);
                progressBar.setVisibility(View.GONE);
            }
        };

        asyncTask.execute(brightness);
    }

    void makeNegative() {
        if (originalBitmap == null) {
            Tools.showToast(this, "don't have image");
            return;
        }

        AsyncTask<Integer, String, Bitmap> asyncTask = new AsyncTask<Integer, String, Bitmap>() {
            long start;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                if (membersCount > 0) {
                    membersCount--;
                    myAsyncTask.cancel(true);
                }

                membersCount++;
                progressBar.setVisibility(View.VISIBLE);

                myAsyncTask = this;

                start = System.currentTimeMillis();
            }

            @Override
            protected Bitmap doInBackground(Integer... params) {
                int progress = params[0];
                Bitmap changedBitmap = Bitmap.createBitmap(bitmapOriginalWidth, bitmapOriginalHeight, Bitmap.Config.ARGB_8888);
                int colourRed;
                int colourGreen;
                int colourBlue;

                for (int valueX = 0; valueX < bitmapOriginalWidth; valueX++) {
                    for (int valueY = 0; valueY < bitmapOriginalHeight; valueY++) {
                        colour = originalBitmap.getPixel(valueX, valueY);

                        if (myRgb[0])
                            colourRed = colour;
                        else
                            colourRed = 0;

                        if (myRgb[1])
                            colourGreen = colour;
                        else
                            colourGreen = 0;

                        if (myRgb[2])
                            colourBlue = colour;
                        else
                            colourBlue = 0;


                        changedBitmap.setPixel(valueX, valueY, Color.argb(255 - progress, Color.red(255 - colourRed),
                                Color.green(255 - colourGreen), Color.blue(255 - colourBlue)));
                    }

                    if (isCancelled()) return null;
                    publishProgress("");

                }

                return changedBitmap;
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);

                long time = System.currentTimeMillis() - start;

                ((TextView) findViewById(R.id.java_textClock)).setText("" + time);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);

                long time = System.currentTimeMillis() - start;

                ((TextView) findViewById(R.id.java_textClock)).setText("" + time);

                membersCount--;
                imageView.setImageBitmap(bitmap);
                progressBar.setVisibility(View.GONE);
            }
        };

        asyncTask.execute(brightness);
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
        bitmapOriginalWidth = bitmap.getWidth();
        bitmapOriginalHeight = bitmap.getHeight();

        if(!isLayoutVisible) {
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
}