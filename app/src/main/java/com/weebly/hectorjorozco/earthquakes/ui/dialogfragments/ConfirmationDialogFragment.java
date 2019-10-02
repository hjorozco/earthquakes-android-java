package com.weebly.hectorjorozco.earthquakes.ui.dialogfragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.weebly.hectorjorozco.earthquakes.R;

import java.util.Objects;

/**
 * Dialog Fragment class that shows a yes/no confirmation alert dialog
 */
public class ConfirmationDialogFragment extends DialogFragment {

    private static final String DIALOG_FRAGMENT_MESSAGE_ARGUMENT_KEY = "message";
    private static final String DIALOG_FRAGMENT_TITLE_ARGUMENT_KEY = "title";
    private static final String DIALOG_FRAGMENT_ITEM_TO_DELETE_POSITION_ARGUMENT_KEY = "item_to_delete_position";
    private static final String DIALOG_FRAGMENT_CALLER_ARGUMENT_KEY = "caller";

    // Values used to identify who is calling the ConfirmationDialogFragment
    public static final byte FAVORITES_ACTIVITY_DELETE_ONE_FAVORITE = 0;
    public static final byte FAVORITES_ACTIVITY_DELETE_SOME_FAVORITES = 1;
    public static final byte FAVORITES_ACTIVITY_DELETE_ALL_FAVORITES = 2;


    public interface ConfirmationDialogFragmentListener {
        void onConfirmation(boolean answerYes, int itemToDeletePosition, byte caller);
    }

    public ConfirmationDialogFragment() {
        // Empty constructor is required for DialogFragment
    }

    public static ConfirmationDialogFragment newInstance(CharSequence message, String title,
                                                         int itemToDeletePosition, byte caller) {
        ConfirmationDialogFragment confirmationDialogFragment =
                new ConfirmationDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putCharSequence(DIALOG_FRAGMENT_MESSAGE_ARGUMENT_KEY, message);
        bundle.putString(DIALOG_FRAGMENT_TITLE_ARGUMENT_KEY, title);
        bundle.putInt(DIALOG_FRAGMENT_ITEM_TO_DELETE_POSITION_ARGUMENT_KEY, itemToDeletePosition);
        bundle.putByte(DIALOG_FRAGMENT_CALLER_ARGUMENT_KEY, caller);
        confirmationDialogFragment.setArguments(bundle);
        confirmationDialogFragment.setCancelable(false);
        return confirmationDialogFragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int colorPrimaryDark = getResources().getColor(R.color.colorPrimaryDark);
        int colorAccent = getResources().getColor(R.color.colorAccent);
        String colorPrimaryDarkString = Integer.toHexString(colorPrimaryDark & 0x00ffffff);

        Bundle arguments = getArguments();

        CharSequence message = null;
        String title = "";
        int itemToDeletePosition = 0;
        byte caller = 0;

        if (arguments != null) {
            message = arguments.getCharSequence(DIALOG_FRAGMENT_MESSAGE_ARGUMENT_KEY,
                    "");
            title = arguments.getString(DIALOG_FRAGMENT_TITLE_ARGUMENT_KEY);
            itemToDeletePosition = arguments.getInt(DIALOG_FRAGMENT_ITEM_TO_DELETE_POSITION_ARGUMENT_KEY);
            caller = arguments.getByte(DIALOG_FRAGMENT_CALLER_ARGUMENT_KEY);
        }

        // Ask if the user is sure about deleting the student

        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()),
                R.style.ThemeDialogCustomPrimaryColor);
        builder.setMessage(message).setTitle(Html.fromHtml(getString(R.string.html_text_with_color,
                colorPrimaryDarkString, title)));

        final ConfirmationDialogFragmentListener listenerFinal = (ConfirmationDialogFragmentListener) getActivity();
        final byte finalCaller = caller;
        final int finalItemToDeletePosition = itemToDeletePosition;

        builder.setPositiveButton(R.string.yes_text, (dialog, id) ->
                Objects.requireNonNull(listenerFinal).onConfirmation
                (true, finalItemToDeletePosition, finalCaller));

        builder.setNegativeButton(R.string.no_text, (dialog, id) ->
                Objects.requireNonNull(listenerFinal).onConfirmation
                (false, finalItemToDeletePosition, finalCaller));

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setIcon(R.drawable.ic_warning_brown_24dp);
        alertDialog.show();

        Button button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        button.setTextColor(colorAccent);
        button.setBackgroundColor(Color.TRANSPARENT);
        button = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        button.setTextColor(colorAccent);
        button.setBackgroundColor(Color.TRANSPARENT);

        return alertDialog;
    }

}
