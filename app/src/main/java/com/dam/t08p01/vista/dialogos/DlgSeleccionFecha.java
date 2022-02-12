package com.dam.t08p01.vista.dialogos;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Locale;

public class DlgSeleccionFecha extends DialogFragment
        implements DatePickerDialog.OnDateSetListener, DialogInterface.OnCancelListener {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (getActivity() != null) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // De esta manera no funciona porque requiere API24
//            DatePickerDialog dpd = new DatePickerDialog(getActivity());
//            dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
//                @Override
//                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                    ;
//                }
//            });

            return new DatePickerDialog(getActivity(), this, year, month, day);
        } else {
            return super.onCreateDialog(savedInstanceState);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String fecha = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
        mListener.onDlgSeleccionFechaClick(this, fecha);
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        mListener.onDlgSeleccionFechaCancel(this);
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface DlgSeleccionFechaListener {
        void onDlgSeleccionFechaClick(DialogFragment dialog, String fecha);

        void onDlgSeleccionFechaCancel(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    private DlgSeleccionFechaListener mListener;

    // Override the Fragment.onAttach() method to instantiate the DlgSeleccionFechaListener
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the DlgSeleccionFechaListener so we can send events to the host
            mListener = (DlgSeleccionFechaListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString() + " must implement DlgSeleccionFechaListener");
        }
    }

}
