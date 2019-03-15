package com.example.user.ndk_pep;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * Created by user on 25-05-16.
 */
class BitmapLoader extends AsyncTask<Intent, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    private Intent data;
    private int width;
    private int height;
    ProgressBar progressBar;
    Activity activity;

    public BitmapLoader(ImageView imageView, Activity activityData, int currentWidth, int currentHeight) {
        imageViewReference = new WeakReference<ImageView>(imageView);
        activity = activityData;
        progressBar = (ProgressBar) activity.findViewById(R.id.ndk_progress_bar);
        if (progressBar == null) {
            progressBar = (ProgressBar) activity.findViewById(R.id.java_progress_bar);
        }

        width = currentWidth;
        height = currentHeight;
    }

    @Override
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Bitmap doInBackground(Intent... intent) {
        data = intent[0];
        Bitmap currentBitmap = null;

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            InputStream inputStream = activity.getContentResolver().openInputStream(data.getData());
            BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

            if(height == 0) height = 800;
            if(width == 0) width = 480;

            int relativeHeight = options.outHeight / height;
            int relativeWidth = options.outWidth / width;

            options.inSampleSize = relativeHeight > relativeWidth ? relativeHeight : relativeWidth;
            options.inJustDecodeBounds = false;

            inputStream = activity.getContentResolver().openInputStream(data.getData());
            currentBitmap = BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();
        } catch (IOException e) {
        }
        return currentBitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);

                ((OnBitmapLoadListener) activity).setBitmap(bitmap);
            }
        }

        progressBar.setVisibility(View.GONE);
    }
}

