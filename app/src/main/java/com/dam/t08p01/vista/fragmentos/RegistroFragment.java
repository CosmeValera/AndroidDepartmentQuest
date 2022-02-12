package com.dam.t08p01.vista.fragmentos;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dam.t08p01.databinding.FragmentRegistroBinding;
import com.dam.t08p01.vista.adaptadores.AdaptadorRegistro;
import com.dam.t08p01.vistamodelo.MainViewModel;

public class RegistroFragment extends Fragment {

    private FragmentRegistroBinding binding;
    private RegistroFragInterface mCallback;

    private AdaptadorRegistro mAdaptadorRegistro;

    public interface RegistroFragInterface {
        void onBorrarRegistroFrag();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof RegistroFragInterface) {
            mCallback = (RegistroFragInterface) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement RegistroFragInterface");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ;
        }
        // Inits
        MainViewModel mainVM = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        mAdaptadorRegistro = new AdaptadorRegistro();
        mAdaptadorRegistro.setDatos(mainVM.getRegistro());
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegistroBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Init RecyclerView Registro
        binding.rvRegistro.setHasFixedSize(true);
        binding.rvRegistro.setLayoutManager(new LinearLayoutManager(view.getContext()));
        binding.rvRegistro.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));
        binding.rvRegistro.setAdapter(mAdaptadorRegistro);

        // Listeners
        binding.btRegistroBorrar.setOnClickListener(btRegistroBorrar_OnClickListener);
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

    private final View.OnClickListener btRegistroBorrar_OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mCallback != null) {
                mCallback.onBorrarRegistroFrag();
            }
        }
    };

}
