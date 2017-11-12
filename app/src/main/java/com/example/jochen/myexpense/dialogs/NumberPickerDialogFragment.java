package com.example.jochen.myexpense.dialogs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by Jochen on 12.11.2017.
 */

public class NumberPickerDialogFragment extends DialogFragment {

    public static NumberPickerDialogFragment newInstance(final int max, final int min) {

        Bundle args = new Bundle();

        NumberPickerDialogFragment fragment = new NumberPickerDialogFragment();
        fragment.setArguments(args);
        return fragment;

    }
}
