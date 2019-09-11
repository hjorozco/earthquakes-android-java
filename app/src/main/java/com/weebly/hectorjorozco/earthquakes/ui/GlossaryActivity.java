package com.weebly.hectorjorozco.earthquakes.ui;

import android.animation.Animator;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.weebly.hectorjorozco.earthquakes.R;

public class GlossaryActivity extends AppCompatActivity {

    private static final String ARE_CONCEPTS_SHOWN_KEY = "ARE_CONCEPTS_SHOWN_KEY";
    private static final float HALF_ROTATION = 180;
    private static final int MAGNITUDE_CONCEPT_INDEX = 0;
    private static final int MAGNITUDE_SCALE_CONCEPT_INDEX = 1;
    private static final int INTENSITY_CONCEPT_INDEX = 2;
    private static final int INTENSITY_SCALE_CONCEPT_INDEX = 3;
    private static final int ALERT_CONCEPT_INDEX = 4;
    private static final int TSUNAMI_CONCEPT_INDEX = 5;

    private boolean mIsAnimationFinished = true;

    // Used to save the status (true for visible, false for gone) of each concept.
    private boolean[] mAreConceptsShown = new boolean[]{false, false, false, false, false, false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glossary);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState != null) {
            mAreConceptsShown = savedInstanceState.getBooleanArray(ARE_CONCEPTS_SHOWN_KEY);
        }

        setupAllEarthquakeConceptsViews();

    }


    private void setupAllEarthquakeConceptsViews() {

        // Used to assign vector drawables in API 19
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        // Magnitude information views
        setupEarthquakeConceptViews(findViewById(R.id.activity_earthquake_terms_magnitude_title_linear_layout),
                findViewById(R.id.activity_earthquake_terms_magnitude_definition_text_view),
                findViewById(R.id.activity_earthquake_terms_magnitude_title_arrow_image_view),
                MAGNITUDE_CONCEPT_INDEX);

        // Richter magnitude scale information views
        setupEarthquakeConceptViews(findViewById(R.id.activity_earthquake_terms_richter_magnitude_scale_title_linear_layout),
                findViewById(R.id.activity_earthquake_terms_richter_magnitude_scale_text_view),
                findViewById(R.id.activity_earthquake_terms_richter_magnitude_scale_title_arrow_image_view),
                MAGNITUDE_SCALE_CONCEPT_INDEX);

        // Intensity information views
        setupEarthquakeConceptViews(findViewById(R.id.activity_earthquake_terms_intensity_title_linear_layout),
                findViewById(R.id.activity_earthquake_terms_intensity_definition_text_view),
                findViewById(R.id.activity_earthquake_terms_intensity_title_arrow_image_view),
                INTENSITY_CONCEPT_INDEX);

        // Modified Mercalli Intensity scale information views
        setupEarthquakeConceptViews(findViewById(R.id.activity_earthquake_terms_modified_mercalli_intensity_scale_title_linear_layout),
                findViewById(R.id.activity_earthquake_terms_modified_mercalli_intensity_scale_text_view),
                findViewById(R.id.activity_earthquake_terms_modified_mercalli_intensity_scale_title_arrow_image_view),
                INTENSITY_SCALE_CONCEPT_INDEX);

        // Alert information views
        setupEarthquakeConceptViews(findViewById(R.id.activity_earthquake_concepts_alert_title_linear_layout),
                findViewById(R.id.activity_earthquake_concepts_alert_text_view),
                findViewById(R.id.activity_earthquake_concepts_alert_title_arrow_image_view),
                ALERT_CONCEPT_INDEX);

        // Alert information views
        setupEarthquakeConceptViews(findViewById(R.id.activity_earthquake_concepts_tsunami_title_linear_layout),
                findViewById(R.id.activity_earthquake_concepts_tsunami_text_view),
                findViewById(R.id.activity_earthquake_concepts_tsunami_title_arrow_image_view),
                TSUNAMI_CONCEPT_INDEX);
    }


    private void setupEarthquakeConceptViews(LinearLayout linearLayout, TextView textView, ImageView imageView, int conceptIndex) {

        if (mAreConceptsShown[conceptIndex]) {
            textView.setVisibility(View.VISIBLE);
            imageView.animate().rotationBy(HALF_ROTATION);
        } else {
            textView.setVisibility(View.GONE);
        }

        linearLayout.setOnClickListener(v -> {

            if (mIsAnimationFinished) {
                mIsAnimationFinished = false;
                if (mAreConceptsShown[conceptIndex]) {
                    textView.setVisibility(View.GONE);
                    imageView.animate().rotationBy(-HALF_ROTATION).setListener(setupAnimatorListener());
                } else {
                    textView.setVisibility(View.VISIBLE);
                    imageView.animate().rotationBy(HALF_ROTATION).setListener(setupAnimatorListener());
                }
                mAreConceptsShown[conceptIndex] = !mAreConceptsShown[conceptIndex];
            }
        });
    }


    private Animator.AnimatorListener setupAnimatorListener() {
        return new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnimationFinished = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        };
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBooleanArray(ARE_CONCEPTS_SHOWN_KEY, mAreConceptsShown);
    }
}
