package com.example.jochen.myexpense.dialogs.listener;

import android.support.v4.app.DialogFragment;

/**
 * Created by Jochen on 12.11.2017.
 */

public interface OnNumberPickedListener {
    void onNumberPicked(final boolean numberPicked, final int number, final DialogFragment dialogFragment);

}
