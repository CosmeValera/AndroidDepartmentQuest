package com.dam.t08p01.vista.fragmentos;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dam.t08p01.R;
import com.dam.t08p01.databinding.FragmentMtoDptosBinding;
import com.dam.t08p01.modelo.Departamento;

public class MtoDptosFragment extends Fragment {

    private FragmentMtoDptosBinding binding;
    private MtoDptosFragInterface mCallback;

    private int mOp;    // Operación a realizar
    private Departamento mDpto;

    public static final int OP_ELIMINAR = 1;
    public static final int OP_EDITAR = 2;
    public static final int OP_CREAR = 3;

    public interface MtoDptosFragInterface {
        void onCancelarMtoDptosFrag();

        void onAceptarMtoDptosFrag(int op, Departamento dpto);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MtoDptosFragInterface) {
            mCallback = (MtoDptosFragInterface) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement MtoDptosFragInterface");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mOp = getArguments().getInt("op");
            mDpto = getArguments().getParcelable("dpto");
        } else {
            mOp = -1;
            mDpto = null;
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMtoDptosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inits
        if (mOp != -1) {    // MtoDptosFragment requiere una operación válida!!
            binding.btDptoCancelar.setEnabled(true);
            binding.btDptoAceptar.setEnabled(true);

            switch (mOp) {
                case OP_CREAR:
                    binding.tvDptoCabecera.setText(getString(R.string.tv_Dpto_Cabecera_Crear));
                    break;
                case OP_EDITAR:
                    binding.tvDptoCabecera.setText(getString(R.string.tv_Dpto_Cabecera_Editar));
                    binding.etDptoId.setText(mDpto.getId());
                    binding.etDptoId.setEnabled(false);
                    binding.etDptoNombre.setText(mDpto.getNombre());
                    binding.etDptoClave.setText(mDpto.getClave());
                    break;
            }

            // Listeners
            binding.btDptoCancelar.setOnClickListener(btDptoCancelar_OnClickListener);
            binding.btDptoAceptar.setOnClickListener(btDptoAceptar_OnClickListener);

        } else {
            binding.btDptoCancelar.setEnabled(false);
            binding.btDptoAceptar.setEnabled(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    private final View.OnClickListener btDptoCancelar_OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            if (mCallback != null) {
                mCallback.onCancelarMtoDptosFrag();
            }
        }
    };

    private final View.OnClickListener btDptoAceptar_OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            if (mCallback != null) {
                if (!binding.etDptoId.getText().toString().equals("") &&
                        !binding.etDptoNombre.getText().toString().equals("") &&
                        !binding.etDptoClave.getText().toString().equals("")) {

                    // Creamos un nuevo departameto, independientemente de la operación a realizar!!
                    mDpto = new Departamento();
                    mDpto.setId(binding.etDptoId.getText().toString());
                    mDpto.setNombre(binding.etDptoNombre.getText().toString());
                    mDpto.setClave(binding.etDptoClave.getText().toString());

                    mCallback.onAceptarMtoDptosFrag(mOp, mDpto);
                } else {
                    mCallback.onAceptarMtoDptosFrag(mOp, null);  // Faltan Datos Obligatorios
                }
            }
        }
    };

}
