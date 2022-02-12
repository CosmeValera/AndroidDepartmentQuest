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

import com.dam.t08p01.databinding.FragmentBusAulasBinding;
import com.dam.t08p01.modelo.Aula;
import com.dam.t08p01.vista.adaptadores.AdaptadorAulas;
import com.dam.t08p01.vistamodelo.AulasViewModel;

import java.util.List;

public class BusAulasFragment extends Fragment
        implements AdaptadorAulas.AdaptadorAulasInterface {

    private FragmentBusAulasBinding binding;
    private BusAulasFragInterface mCallback;

    private AdaptadorAulas mAdaptadorAulas;

    public interface BusAulasFragInterface {
        void onCrearBusAulasFrag();

        void onEditarBusAulasFrag(Aula aula);

        void onEliminarBusAulasFrag(Aula aula);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof BusAulasFragInterface) {
            mCallback = (BusAulasFragInterface) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement BusAulasFragInterface");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ;
        }

        // Inits
        mAdaptadorAulas = new AdaptadorAulas(this);

        // Inits Aulas Observer
        AulasViewModel aulasVM = new ViewModelProvider(requireActivity()).get(AulasViewModel.class);
        aulasVM.getAulas().observe(this, new Observer<List<Aula>>() {
            @Override
            public void onChanged(List<Aula> aulas) {
                mAdaptadorAulas.setDatos(aulas);
                mAdaptadorAulas.notifyDataSetChanged();
                if (mAdaptadorAulas.getItemPos() != -1 &&
                        mAdaptadorAulas.getItemPos() < mAdaptadorAulas.getItemCount()) {
                    binding.rvAulas.scrollToPosition(mAdaptadorAulas.getItemPos());
                } else if (mAdaptadorAulas.getItemCount() > 0) {
                    binding.rvAulas.scrollToPosition(mAdaptadorAulas.getItemCount() - 1);
                }
                mAdaptadorAulas.setItemPos(-1);
                binding.btAulaEliminar.setEnabled(false);
                binding.btAulaEditar.setEnabled(false);
                binding.btAulaCrear.setEnabled(true);
            }
        });
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBusAulasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init RecyclerView Aulas
        binding.rvAulas.setHasFixedSize(true);
        binding.rvAulas.setLayoutManager(new LinearLayoutManager(view.getContext()));
        binding.rvAulas.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));
        binding.rvAulas.setAdapter(mAdaptadorAulas);

        // Inits
        if (mAdaptadorAulas.getItemPos() != -1) {
            binding.btAulaEliminar.setEnabled(true);
            binding.btAulaEditar.setEnabled(true);
            binding.btAulaCrear.setEnabled(false);
        } else {
            binding.btAulaEliminar.setEnabled(false);
            binding.btAulaEditar.setEnabled(false);
            binding.btAulaCrear.setEnabled(true);
        }

        // Listeners
        binding.btAulaCrear.setOnClickListener(btAulaCrear_OnClickListener);
        binding.btAulaEditar.setOnClickListener(btAulaEditar_OnClickListener);
        binding.btAulaEliminar.setOnClickListener(btAulaEliminar_OnClickListener);
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
    public void onItemClickRVAulas(int pos) {
        if (pos != -1) {
            binding.btAulaEliminar.setEnabled(true);
            binding.btAulaEditar.setEnabled(true);
            binding.btAulaCrear.setEnabled(false);
        } else {
            binding.btAulaEliminar.setEnabled(false);
            binding.btAulaEditar.setEnabled(false);
            binding.btAulaCrear.setEnabled(true);
        }
    }

    private final View.OnClickListener btAulaCrear_OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int pos = mAdaptadorAulas.getItemPos();
            if (pos == -1) {
                if (mCallback != null) {
                    mCallback.onCrearBusAulasFrag();
                }
            }
        }
    };

    private final View.OnClickListener btAulaEditar_OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int pos = mAdaptadorAulas.getItemPos();
            if (pos >= 0) {
                if (mCallback != null) {
                    mCallback.onEditarBusAulasFrag(mAdaptadorAulas.getItem(pos));
                }
            }
        }
    };

    private final View.OnClickListener btAulaEliminar_OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int pos = mAdaptadorAulas.getItemPos();
            if (pos >= 0) {
                if (mCallback != null) {
                    mCallback.onEliminarBusAulasFrag(mAdaptadorAulas.getItem(pos));
                }
            }
        }
    };

}
