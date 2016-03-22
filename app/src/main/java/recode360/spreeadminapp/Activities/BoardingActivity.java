package recode360.spreeadminapp.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import recode360.spreeadminapp.R;

public class BoardingActivity extends AppIntro2 {

    // Please DO NOT override onCreate. Use init.
    @Override
    public void init(Bundle savedInstanceState) {


        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        addSlide(AppIntroFragment.newInstance("Products", "Fetch all your products, update them, or even create new ones.", R.drawable.product_slide, Color.parseColor("#3F51B5")));
         addSlide(AppIntroFragment.newInstance("Orders", "Keep track of what is being ordered from your store and by whom.", R.drawable.order_slide, Color.parseColor("#455A64")));
        addSlide(AppIntroFragment.newInstance("Payments", "Choose from multiple payment methods, and keep track of the payments made on your store.", R.drawable.payment_slide, Color.parseColor("#3F51B5")));
        addSlide(AppIntroFragment.newInstance("Shipments", "Create shipping labels and get all the shipping information you need. ", R.drawable.delivery_slide, Color.parseColor("#455A64")));

        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permisssion in Manifest.
        //setVibrate(true);
        //setVibrateIntensity(30);
    }


    @Override
    public void onDonePressed() {
        // Do something when users tap on Done button.
        Intent i = new Intent(BoardingActivity.this, LoginActivity.class);
        startActivity(i);
    }

    @Override
    public void onSlideChanged() {
        // Do something when the slide changes.
    }

    @Override
    public void onNextPressed() {
        // Do something when users tap on Next button.
    }


    // Decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE = 70;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }


}
