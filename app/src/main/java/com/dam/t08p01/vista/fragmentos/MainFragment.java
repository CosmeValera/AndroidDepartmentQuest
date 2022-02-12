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

import com.dam.t08p01.databinding.FragmentMainBinding;
import com.dam.t08p01.modelo.Departamento;
import com.dam.t08p01.vistamodelo.MainViewModel;

public class MainFragment extends Fragment {

    private FragmentMainBinding binding;
    private MainFragInterface mCallback;

    public interface MainFragInterface {
        ;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
//        if (context instanceof MainFragInterface) {
//            mCallback = (MainFragInterface) context;
//        } else {
//            throw new RuntimeException(context.toString() + " must implement MainFragInterface");
//        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ;
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Inits
        MainViewModel mainVM = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        Departamento login = mainVM.getLogin();     // Recuperamos el login del ViewModel
        if (login != null) {
            binding.tvMainLogin.setText(login.getNombre());
        }
        // Listeners
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

}
