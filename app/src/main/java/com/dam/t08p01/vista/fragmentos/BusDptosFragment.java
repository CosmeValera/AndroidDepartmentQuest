package com.dam.t08p01.vista.fragmentos;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dam.t08p01.databinding.FragmentBusDptosBinding;
import com.dam.t08p01.modelo.Departamento;
import com.dam.t08p01.vista.adaptadores.AdaptadorDptos;
import com.dam.t08p01.vistamodelo.DptosViewModel;

import java.util.List;

public class BusDptosFragment extends Fragment
        implements AdaptadorDptos.AdaptadorDptosInterface {

    private FragmentBusDptosBinding binding;
    private BusDptosFragInterface mCallback;

    private AdaptadorDptos mAdaptadorDptos;

    public interface BusDptosFragInterface {
        void onCrearBusDptosFrag();

        void onEditarBusDptosFrag(Departamento dpto);

        void onEliminarBusDptosFrag(Departamento dpto);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof BusDptosFragInterface) {
            mCallback = (BusDptosFragInterface) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement BusDptosFragInterface");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ;
        }

        // Inits
        mAdaptadorDptos = new AdaptadorDptos(this);

        // Inits Dptos Observer
        DptosViewModel dptosVM = new ViewModelProvider(requireActivity()).get(DptosViewModel.class);
        dptosVM.getDptos().observe(this, new Observer<List<Departamento>>() {
            @Override
            public void onChanged(List<Departamento> dptos) {
                mAdaptadorDptos.setDatos(dptos);
                mAdaptadorDptos.notifyDataSetChanged();
                if (mAdaptadorDptos.getItemPos() != -1 &&
                        mAdaptadorDptos.getItemPos() < mAdaptadorDptos.getItemCount()) {
                    binding.rvDptos.scrollToPosition(mAdaptadorDptos.getItemPos());
                } else if (mAdaptadorDptos.getItemCount() > 0) {
                    binding.rvDptos.scrollToPosition(mAdaptadorDptos.getItemCount() - 1);
                }
                mAdaptadorDptos.setItemPos(-1);
                binding.btDptoEliminar.setEnabled(false);
                binding.btDptoEditar.setEnabled(false);
                binding.btDptoCrear.setEnabled(true);
            }
        });
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBusDptosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init RecyclerView Dptos
        binding.rvDptos.setHasFixedSize(true);
        binding.rvDptos.setLayoutManager(new LinearLayoutManager(view.getContext()));
        binding.rvDptos.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));
        binding.rvDptos.setAdapter(mAdaptadorDptos);

        // Inits
        if (mAdaptadorDptos.getItemPos() != -1) {
            binding.btDptoEliminar.setEnabled(true);
            binding.btDptoEditar.setEnabled(true);
            binding.btDptoCrear.setEnabled(false);
        } else {
            binding.btDptoEliminar.setEnabled(false);
            binding.btDptoEditar.setEnabled(false);
            binding.btDptoCrear.setEnabled(true);
        }

        // Listeners
        binding.btDptoCrear.setOnClickListener(btDptoCrear_OnClickListener);
        binding.btDptoEditar.setOnClickListener(btDptoEditar_OnClickListener);
        binding.btDptoEliminar.setOnClickListener(btDptoEliminar_OnClickListener);
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

    @Override
    public void onItemClickRVDptos(int pos) {
        if (pos != -1) {
            binding.btDptoEliminar.setEnabled(true);
            binding.btDptoEditar.setEnabled(true);
            binding.btDptoCrear.setEnabled(false);
        } else {
            binding.btDptoEliminar.setEnabled(false);
            binding.btDptoEditar.setEnabled(false);
            binding.btDptoCrear.setEnabled(true);
        }
    }

    private final View.OnClickListener btDptoCrear_OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int pos = mAdaptadorDptos.getItemPos();
            if (pos == -1) {
                if (mCallback != null) {
                    mCallback.onCrearBusDptosFrag();
                }
            }
        }
    };

    private final View.OnClickListener btDptoEditar_OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int pos = mAdaptadorDptos.getItemPos();
            if (pos >= 0) {
                if (mCallback != null) {
                    mCallback.onEditarBusDptosFrag(mAdaptadorDptos.getItem(pos));
                }
            }
        }
    };

    private final View.OnClickListener btDptoEliminar_OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int pos = mAdaptadorDptos.getItemPos();
            if (pos >= 0) {
                if (mCallback != null) {
                    mCallback.onEliminarBusDptosFrag(mAdaptadorDptos.getItem(pos));
                }
            }
        }
    };

}
