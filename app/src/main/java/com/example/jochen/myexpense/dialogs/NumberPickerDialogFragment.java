package com.example.jochen.myexpense.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.NumberPicker;

import com.example.jochen.myexpense.R;
import com.example.jochen.myexpense.dialogs.listener.OnNumberPickedListener;

/**
 * Created by Jochen on 12.11.2017.
 */

public class NumberPickerDialogFragment extends DialogFragment {

    private static final String MAX_KEY = "max";
    private static final String MIN_KEY = "min";

    private OnNumberPickedListener listener;

    private NumberPicker picker;

    public static NumberPickerDialogFragment newInstance(final int max, final int min) {

        Bundle args = new Bundle();

        args.putInt(MAX_KEY, max);
        args.putInt(MIN_KEY, min);

        NumberPickerDialogFragment fragment = new NumberPickerDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void setListener(final OnNumberPickedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()); // return Activity in dem Kontext eingebettet ist

        // Expense kann ausgelagert werden und in newInstance übergeben werden
        builder.setTitle("NumberPicker");

        // wir brauchen Knöpfe:
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, final int i) {
                if(listener != null) {
                    listener.onNumberPicked(true, picker.getValue(), NumberPickerDialogFragment.this);
                }

                dialogInterface.dismiss();

            }
        });

        builder.setNegativeButton("abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(listener != null) {
                    listener.onNumberPicked(false, picker.getValue(), NumberPickerDialogFragment.this);
                }
                dialogInterface.dismiss();

            }
        });

        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_number_picker, null);

        this.picker = (NumberPicker) v.findViewById(R.id.numberPicker);
        this.picker.setMaxValue(getArguments().getInt(MAX_KEY));
        this.picker.setMinValue(getArguments().getInt(MIN_KEY));

        // Expense fill number picker with min and max values

        builder.setView(v);
        return builder.create();
    }


}
