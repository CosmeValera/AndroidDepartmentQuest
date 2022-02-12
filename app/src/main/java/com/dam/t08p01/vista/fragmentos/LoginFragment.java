package com.dam.t08p01.vista.fragmentos;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.dam.t08p01.databinding.FragmentLoginBinding;
import com.dam.t08p01.modelo.Departamento;
import com.dam.t08p01.vistamodelo.DptosViewModel;

import java.util.ArrayList;
import java.util.List;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private LoginFragInterface mCallback;

    private ArrayAdapter<Departamento> mAdaptadorDtpos;

    public interface LoginFragInterface {
        void onCancelarLoginFrag();

        void onAceptarLoginFrag(Departamento dpto);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof LoginFragInterface) {
            mCallback = (LoginFragInterface) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement LoginFragInterface");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ;
        }

        // Inits
        mAdaptadorDtpos = new ArrayAdapter<>(requireActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                new ArrayList<>());

        // Inits Dptos Observer
        DptosViewModel dptosVM = new ViewModelProvider(requireActivity()).get(DptosViewModel.class);
        dptosVM.getDptosSE().observe(this, new Observer<List<Departamento>>() {
            @Override
            public void onChanged(List<Departamento> dptos) {
                mAdaptadorDtpos.clear();
                mAdaptadorDtpos.addAll(dptos);
                mAdaptadorDtpos.notifyDataSetChanged();
            }
        });
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Inits
        binding.spLoginDptos.setAdapter(mAdaptadorDtpos);
        // Listeners
        binding.btLoginCancelar.setOnClickListener(btLoginCancelar_OnClickListener);
        binding.btLoginAceptar.setOnClickListener(btLoginAceptar_OnClickListener);
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

    private final View.OnClickListener btLoginCancelar_OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            if (mCallback != null) {
                mCallback.onCancelarLoginFrag();
            }
        }
    };

    private final View.OnClickListener btLoginAceptar_OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            if (binding.spLoginDptos.getSelectedItemPosition() >= 0) {
                Departamento dpto = mAdaptadorDtpos.getItem(binding.spLoginDptos.getSelectedItemPosition());
                if (dpto.getClave().equals(binding.etLoginClave.getText().toString())) {
                    if (mCallback != null)
                        mCallback.onAceptarLoginFrag(dpto);
                } else {
                    if (mCallback != null)
                        mCallback.onAceptarLoginFrag(null);
                }
            }
        }
    };

}
