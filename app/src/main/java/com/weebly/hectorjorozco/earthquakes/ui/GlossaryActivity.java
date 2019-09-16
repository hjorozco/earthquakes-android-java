package com.weebly.hectorjorozco.earthquakes.ui;

import android.animation.Animator;
import android.os.Bundle;
import android.text.Html;
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

    private static final int EARTHQUAKE_CONCEPT_INDEX = 0;
    private static final int EPICENTER_CONCEPT_INDEX = 1;
    private static final int FAULT_CONCEPT_INDEX = 2;
    private static final int INTENSITY_CONCEPT_INDEX = 3;
    private static final int MAGNITUDE_CONCEPT_INDEX = 4;
    private static final int MERCALLI_CONCEPT_INDEX = 5;
    private static final int MERCALLI_VALUES_CONCEPT_INDEX = 6;
    private static final int RICHTER_CONCEPT_INDEX = 7;
    private static final int RICHTER_VALUES_CONCEPT_INDEX = 8;
    private static final int SEISMOGRAPH_CONCEPT_INDEX = 9;
    private static final int SEISMOLOGY_CONCEPT_INDEX = 10;
    private static final int TECTONIC_CONCEPT_INDEX = 11;
    private static final int TSUNAMI_CONCEPT_INDEX = 12;

    private static final int NUMBER_OF_CONCEPTS = 13;

    private boolean mIsAnimationFinished = true;

    // Used to save the status (true for visible, false for gone) of each concept.
    private boolean[] mAreConceptsShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glossary);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState != null) {
            mAreConceptsShown = savedInstanceState.getBooleanArray(ARE_CONCEPTS_SHOWN_KEY);
        } else {
            initializeAreConceptsShownArray();
        }

        setupAllEarthquakeConceptsViews();

    }


    private void initializeAreConceptsShownArray(){
        mAreConceptsShown = new boolean[NUMBER_OF_CONCEPTS];
        for (int i=0; i<NUMBER_OF_CONCEPTS; i++){
            mAreConceptsShown[i]=false;
         }
    }


    private void setupAllEarthquakeConceptsViews() {

        // Used to assign vector drawables in API 19
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        // Earthquake
        TextView earthquakeTextView = findViewById(R.id.activity_glossary_earthquake_definition_text_view);
        earthquakeTextView.setText(Html.fromHtml(getString(R.string.activity_glossary_earthquake_definition)));
        setupEarthquakeConceptViews(findViewById(R.id.activity_glossary_earthquake_title_linear_layout),
                earthquakeTextView, findViewById(R.id.activity_glossary_earthquake_title_arrow_image_view),
                EARTHQUAKE_CONCEPT_INDEX);

        // Epicenter
        TextView epicenterTextView = findViewById(R.id.activity_glossary_epicenter_definition_text_view);
        epicenterTextView.setText(Html.fromHtml(getString(R.string.activity_glossary_epicenter_definition)));
        setupEarthquakeConceptViews(findViewById(R.id.activity_glossary_epicenter_title_linear_layout),
                epicenterTextView, findViewById(R.id.activity_glossary_epicenter_title_arrow_image_view),
                EPICENTER_CONCEPT_INDEX);

        // Fault
        TextView faultTextView = findViewById(R.id.activity_glossary_fault_definition_text_view);
        faultTextView.setText(Html.fromHtml(getString(R.string.activity_glossary_fault_definition)));
        setupEarthquakeConceptViews(findViewById(R.id.activity_glossary_fault_title_linear_layout),
                faultTextView, findViewById(R.id.activity_glossary_fault_title_arrow_image_view),
                FAULT_CONCEPT_INDEX);

        // Intensity
        TextView intensityTextView = findViewById(R.id.activity_glossary_intensity_definition_text_view);
        intensityTextView.setText(Html.fromHtml(getString(R.string.activity_glossary_intensity_definition)));
        setupEarthquakeConceptViews(findViewById(R.id.activity_glossary_intensity_title_linear_layout),
                intensityTextView, findViewById(R.id.activity_glossary_intensity_title_arrow_image_view),
                INTENSITY_CONCEPT_INDEX);

        // Magnitude
        TextView magnitudeTextView = findViewById(R.id.activity_glossary_magnitude_definition_text_view);
        magnitudeTextView.setText(Html.fromHtml(getString(R.string.activity_glossary_magnitude_definition)));
        setupEarthquakeConceptViews(findViewById(R.id.activity_glossary_magnitude_title_linear_layout),
                magnitudeTextView, findViewById(R.id.activity_glossary_magnitude_title_arrow_image_view),
                MAGNITUDE_CONCEPT_INDEX);

        // Modified Mercalli Intensity scale
        TextView mercalliTextView = findViewById(R.id.activity_glossary_mercalli_definition_text_view);
        mercalliTextView.setText(Html.fromHtml(getString(R.string.activity_glossary_mercalli_definition)));
        setupEarthquakeConceptViews(findViewById(R.id.activity_glossary_mercalli_title_linear_layout),
                mercalliTextView, findViewById(R.id.activity_glossary_mercalli_title_arrow_image_view),
                MERCALLI_CONCEPT_INDEX);

        // Modified Mercalli Intensity scale values
        TextView mercalliValuesTextView = findViewById(R.id.activity_glossary_mercalli_values_definition_text_view);
        mercalliValuesTextView.setText(Html.fromHtml(getString(R.string.activity_glossary_mercalli_values_definition)));
        setupEarthquakeConceptViews(findViewById(R.id.activity_glossary_mercalli_values_title_linear_layout),
                mercalliValuesTextView, findViewById(R.id.activity_glossary_mercalli_values_title_arrow_image_view),
                MERCALLI_VALUES_CONCEPT_INDEX);

        // Richter magnitude scale
        TextView richterTextView = findViewById(R.id.activity_glossary_richter_definition_text_view);
        richterTextView.setText(Html.fromHtml(getString(R.string.activity_glossary_richter_definition)));
        setupEarthquakeConceptViews(findViewById(R.id.activity_glossary_richter_title_linear_layout),
                richterTextView, findViewById(R.id.activity_glossary_richter_title_arrow_image_view),
                RICHTER_CONCEPT_INDEX);

        // Richter magnitude scale values
        TextView richterValuesTextView = findViewById(R.id.activity_glossary_richter_values_definition_text_view);
        richterValuesTextView.setText(Html.fromHtml(getString(R.string.activity_glossary_richter_values_definition)));
        setupEarthquakeConceptViews(findViewById(R.id.activity_glossary_richter_values_title_linear_layout),
                richterValuesTextView, findViewById(R.id.activity_glossary_richter_values_title_arrow_image_view),
                RICHTER_VALUES_CONCEPT_INDEX);

        // Seismograph
        TextView seismographTextView = findViewById(R.id.activity_glossary_seismograph_definition_text_view);
        seismographTextView.setText(Html.fromHtml(getString(R.string.activity_glossary_seismograph_definition)));
        setupEarthquakeConceptViews(findViewById(R.id.activity_glossary_seismograph_title_linear_layout),
                seismographTextView, findViewById(R.id.activity_glossary_seismograph_title_arrow_image_view),
                SEISMOGRAPH_CONCEPT_INDEX);

        // Seismology
        TextView seismologyTextView = findViewById(R.id.activity_glossary_seismology_definition_text_view);
        seismologyTextView.setText(Html.fromHtml(getString(R.string.activity_glossary_seismology_definition)));
        setupEarthquakeConceptViews(findViewById(R.id.activity_glossary_seismology_title_linear_layout),
                seismologyTextView, findViewById(R.id.activity_glossary_seismology_title_arrow_image_view),
                SEISMOLOGY_CONCEPT_INDEX);

        // Tectonic plates
        TextView tectonicTextView = findViewById(R.id.activity_glossary_tectonic_definition_text_view);
        tectonicTextView.setText(Html.fromHtml(getString(R.string.activity_glossary_tectonic_definition)));
        setupEarthquakeConceptViews(findViewById(R.id.activity_glossary_tectonic_title_linear_layout),
                tectonicTextView, findViewById(R.id.activity_glossary_tectonic_title_arrow_image_view),
                TECTONIC_CONCEPT_INDEX);

        // Tsunami
        TextView tsunamiTextView = findViewById(R.id.activity_glossary_tsunami_definition_text_view);
        tsunamiTextView.setText(Html.fromHtml(getString(R.string.activity_glossary_tsunami_definition)));
        setupEarthquakeConceptViews(findViewById(R.id.activity_glossary_tsunami_title_linear_layout),
                tsunamiTextView, findViewById(R.id.activity_glossary_tsunami_title_arrow_image_view),
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
