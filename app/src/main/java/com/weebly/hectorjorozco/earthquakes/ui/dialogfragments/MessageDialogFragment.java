package com.weebly.hectorjorozco.earthquakes.ui.dialogfragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.weebly.hectorjorozco.earthquakes.R;
import com.weebly.hectorjorozco.earthquakes.utils.UiUtils;

import java.util.Objects;

public class MessageDialogFragment extends DialogFragment {

    private static final String DIALOG_FRAGMENT_MESSAGE_ARGUMENT_KEY = "message";
    private static final String DIALOG_FRAGMENT_TITLE_ARGUMENT_KEY = "title";
    private static final String DIALOG_FRAGMENT_CALLER_ARGUMENT_KEY = "caller";

    private static final byte MESSAGE_DIALOG_FRAGMENT_CALLER_NOT_DEFINED = -1;
    public static final byte MESSAGE_DIALOG_FRAGMENT_CALLER_REPORT_BUTTON = 0;
    public static final byte MESSAGE_DIALOG_FRAGMENT_CALLER_LOCATION_PERMISSION_REQUEST = 1;
    public static final byte MESSAGE_DIALOG_FRAGMENT_CALLER_OTHER = 2;


    public interface MessageDialogFragmentListener {
        void onAccept();
    }


    public MessageDialogFragment() {
        // Empty constructor is required for DialogFragment
    }


    public static MessageDialogFragment newInstance(CharSequence message, String title, byte caller) {
        MessageDialogFragment messageDialogFragment = new MessageDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putCharSequence(DIALOG_FRAGMENT_MESSAGE_ARGUMENT_KEY, message);
        bundle.putString(DIALOG_FRAGMENT_TITLE_ARGUMENT_KEY, title);
        bundle.putByte(DIALOG_FRAGMENT_CALLER_ARGUMENT_KEY, caller);
        messageDialogFragment.setArguments(bundle);
        if (caller == MESSAGE_DIALOG_FRAGMENT_CALLER_LOCATION_PERMISSION_REQUEST ||
                caller==MESSAGE_DIALOG_FRAGMENT_CALLER_REPORT_BUTTON) {
            messageDialogFragment.setCancelable(false);
        }
        return messageDialogFragment;
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
        byte caller = MESSAGE_DIALOG_FRAGMENT_CALLER_NOT_DEFINED;

        if (arguments != null) {
            message = arguments.getCharSequence(DIALOG_FRAGMENT_MESSAGE_ARGUMENT_KEY, "Hello");
            title = arguments.getString(DIALOG_FRAGMENT_TITLE_ARGUMENT_KEY);
            caller = arguments.getByte(DIALOG_FRAGMENT_CALLER_ARGUMENT_KEY);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()),
                R.style.ThemeDialogCustomPrimaryColor);
        builder.setMessage(message).setTitle(Html.fromHtml(getString(R.string.html_text_with_color,
                colorPrimaryDarkString, title)));

        byte finalCaller = caller;
        builder.setPositiveButton(R.string.positive_button_text, (dialog, id) -> {
            MessageDialogFragmentListener listener;
            if (finalCaller == MESSAGE_DIALOG_FRAGMENT_CALLER_REPORT_BUTTON) {
                listener = (MessageDialogFragmentListener) getActivity();
                listener.onAccept();
            } else if (finalCaller == MESSAGE_DIALOG_FRAGMENT_CALLER_LOCATION_PERMISSION_REQUEST) {
                listener = (MessageDialogFragmentListener) getTargetFragment();
                if (listener != null) {
                    listener.onAccept();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        button.setTextColor(colorAccent);
        UiUtils.setupAlertDialogButtonBackground(getContext(), button);

        return alertDialog;
    }

}
