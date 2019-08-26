package com.weebly.hectorjorozco.earthquakes.ui;

import android.animation.ObjectAnimator;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.weebly.hectorjorozco.earthquakes.R;

public class EarthquakesInformationActivity extends AppCompatActivity {

    private boolean mMagnitudeInfoShown = false;
    private ObjectAnimator mAnimator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquakes_information);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setupTextViews();
    }


    private void setupTextViews() {

        TextView magnitudeTitleTextView = findViewById(R.id.activity_earthquakes_information_magnitude_title_text_view);
        TextView magnitudeTextView = findViewById(R.id.activity_earthquakes_information_magnitude_text_view);

        Drawable arrowDrawable = AppCompatResources.getDrawable(this, R.drawable.ic_animated_arrow_down);
        magnitudeTitleTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, arrowDrawable, null);
        arrowDrawable = magnitudeTitleTextView.getCompoundDrawables()[2];
        mAnimator = ObjectAnimator.ofInt(arrowDrawable, "level", 0, 10000);

        magnitudeTitleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMagnitudeInfoShown) {
                    magnitudeTextView.setVisibility(View.GONE);
                    mAnimator.reverse();

                } else {
                    magnitudeTextView.setVisibility(View.VISIBLE);
                    mAnimator.start();
                }
                mMagnitudeInfoShown = !mMagnitudeInfoShown;
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (mMagnitudeInfoShown){
            mAnimator.reverse();
        }
        super.onDestroy();
    }
}
