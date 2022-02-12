package com.dam.t08p01.vista.dialogos;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.dam.t08p01.R;

public class DlgConfirmacion extends DialogFragment {

    private int mTitulo;
    private int mMensaje;

    public void setTitulo(int titulo) {
        mTitulo = titulo;
    }

    public void setMensaje(int mensaje) {
        mMensaje = mensaje;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (getActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            if (getArguments() != null) {
                builder.setTitle(getArguments().getInt("titulo"));
                builder.setMessage(getArguments().getInt("mensaje"));
            } else {
                builder.setTitle(mTitulo);
                builder.setMessage(mMensaje);
            }
            builder.setPositiveButton(R.string.bt_Aceptar, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mListener.onDlgConfirmacionPositiveClick(DlgConfirmacion.this);
                }
            });
            builder.setNegativeButton(R.string.bt_Cancelar, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mListener.onDlgConfirmacionNegativeClick(DlgConfirmacion.this);
                }
            });
            return builder.create();
        } else {
            return super.onCreateDialog(savedInstanceState);
        }
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface DlgConfirmacionListener {
        void onDlgConfirmacionPositiveClick(DialogFragment dialog);

        void onDlgConfirmacionNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    private DlgConfirmacionListener mListener;

    // Override the Fragment.onAttach() method to instantiate the DlgConfirmacionListener
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the DlgConfirmacionListener so we can send events to the host
            mListener = (DlgConfirmacionListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString() + " must implement DlgConfirmacionListener");
        }
    }

}
