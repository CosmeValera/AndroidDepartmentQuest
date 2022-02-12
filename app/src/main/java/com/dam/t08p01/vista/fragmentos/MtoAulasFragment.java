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
import androidx.lifecycle.ViewModelProvider;

import com.dam.t08p01.R;
import com.dam.t08p01.databinding.FragmentMtoAulasBinding;
import com.dam.t08p01.modelo.Aula;
import com.dam.t08p01.modelo.Departamento;
import com.dam.t08p01.vistamodelo.AulasViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MtoAulasFragment extends Fragment {

    private FragmentMtoAulasBinding binding;
    private MtoAulasFragInterface mCallback;

    private int mOp;    // Operación a realizar
    private Aula mAula;

    public static final int OP_ELIMINAR = 1;
    public static final int OP_EDITAR = 2;
    public static final int OP_CREAR = 3;

    public interface MtoAulasFragInterface {
        void onCancelarMtoAulasFrag();

        void onAceptarMtoAulasFrag(int op, Aula aula);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MtoAulasFragInterface) {
            mCallback = (MtoAulasFragInterface) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement MtoAulasFragInterface");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mOp = getArguments().getInt("op");
            mAula = getArguments().getParcelable("aula");
        } else {
            mOp = -1;
            mAula = null;
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMtoAulasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inits
        if (mOp != -1) {    // MtoAulasFragment requiere una operación válida!!
            binding.btAulaCancelar.setEnabled(true);
            binding.btAulaAceptar.setEnabled(true);

            AulasViewModel aulasVM = new ViewModelProvider(requireActivity()).get(AulasViewModel.class);
            Departamento login = aulasVM.getLogin();

            switch (mOp) {
                case OP_CREAR:
                    binding.tvAulaCabecera.setText(getString(R.string.tv_Aula_Cabecera_Crear));
                    binding.etAulaIdDpto.setText(login.getId());
                    binding.etAulaIdDpto.setEnabled(false);
                    binding.etAulaIdDptoNombre.setText(login.getNombre());
                    binding.etAulaIdDptoNombre.setEnabled(false);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss", Locale.getDefault());
                    binding.etAulaId.setText(sdf.format(Calendar.getInstance().getTime()));
                    binding.etAulaId.setEnabled(false);
                    break;
                case OP_EDITAR:
                    binding.tvAulaCabecera.setText(getString(R.string.tv_Aula_Cabecera_Editar));
                    binding.etAulaIdDpto.setText(mAula.getIdDpto());
                    binding.etAulaIdDpto.setEnabled(false);
                    binding.etAulaIdDptoNombre.setText(login.getNombre());
                    binding.etAulaIdDptoNombre.setEnabled(false);
                    binding.etAulaId.setText(mAula.getId());
                    binding.etAulaId.setEnabled(false);
                    binding.etAulaNombre.setText(mAula.getNombre());
                    break;
            }

            // Listeners
            binding.btAulaCancelar.setOnClickListener(btAulaCancelar_OnClickListener);
            binding.btAulaAceptar.setOnClickListener(btAulaAceptar_OnClickListener);

        } else {
            binding.btAulaCancelar.setEnabled(false);
            binding.btAulaAceptar.setEnabled(false);
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

    private final View.OnClickListener btAulaCancelar_OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            if (mCallback != null) {
                mCallback.onCancelarMtoAulasFrag();
            }
        }
    };

    private final View.OnClickListener btAulaAceptar_OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            if (mCallback != null) {
                if (!binding.etAulaIdDpto.getText().toString().equals("") &&
                        !binding.etAulaId.getText().toString().equals("") &&
                        !binding.etAulaNombre.getText().toString().equals("")) {

                    // Creamos una nueva aula, independientemente de la operación a realizar!!
                    mAula = new Aula();
                    mAula.setIdDpto(binding.etAulaIdDpto.getText().toString());
                    mAula.setId(binding.etAulaId.getText().toString());
                    mAula.setNombre(binding.etAulaNombre.getText().toString());

                    mCallback.onAceptarMtoAulasFrag(mOp, mAula);
                } else {
                    mCallback.onAceptarMtoAulasFrag(mOp, null);  // Faltan Datos Obligatorios
                }
            }
        }
    };

}
