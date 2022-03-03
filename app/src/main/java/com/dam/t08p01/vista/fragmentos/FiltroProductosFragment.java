package com.dam.t08p01.vista.fragmentos;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.dam.t08p01.databinding.FragmentFiltroProductoBinding;
import com.dam.t08p01.modelo.Aula;
import com.dam.t08p01.modelo.Departamento;
import com.dam.t08p01.vista.dialogos.DlgSeleccionFecha;
import com.dam.t08p01.vistamodelo.AulasViewModel;
import com.dam.t08p01.vistamodelo.DptosViewModel;
import com.dam.t08p01.vistamodelo.ProductosViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class FiltroProductosFragment extends Fragment {

    private FragmentFiltroProductoBinding binding;
    private FiltroProductosFragmentInterface mCallback;

    private ArrayAdapter<Aula> mAdaptadorAulas;
    private ArrayAdapter<Departamento> mAdaptadorDptos;

    private ProductosViewModel productosVM;
    private Departamento login;

    public interface FiltroProductosFragmentInterface {
//        void onClickAbrirDlgSeleccionFecha();

        void onCancelarFiltroProductosFrag();

        void onAceptarFiltroProductosFrag(String fecAlta, String idAula, String idDpto);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FiltroProductosFragmentInterface) {
            mCallback = (FiltroProductosFragmentInterface) context;
        } else {
            throw new RuntimeException(context.toString() + " must implrement FiltroProductosFragmentInterface");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Inits
        productosVM = new ViewModelProvider(requireActivity()).get(ProductosViewModel.class);
        mAdaptadorAulas = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_dropdown_item);
        mAdaptadorDptos = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_dropdown_item);

        login = productosVM.getLogin();

        // Inits Dptos Observer
        DptosViewModel dptosVM = new ViewModelProvider(requireActivity()).get(DptosViewModel.class);
        dptosVM.getDptos().observe(this, new Observer<List<Departamento>>() {
            @Override
            public void onChanged(List<Departamento> dptos) {
                mAdaptadorDptos.clear();
                mAdaptadorDptos.add(new Departamento());
                mAdaptadorDptos.addAll(dptos);

                String idDpto = productosVM.getmProductoFiltro().getIdDpto();
                for (int i = 0; i < dptos.size(); i++) {
                    if (dptos.get(i).getId().equals(idDpto)) {
                        binding.spDptos.setSelection(i + 1); //Pq 0 es vacio
                        break;
                    }
                }

                binding.spDptos.setEnabled(login.getId().equals("0"));
            }
        });

        //Recordar fecha
        productosVM.getFechaDlg().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.etFecAlta.setText(s);
            }
        });

    }

    private List<Aula> devovolverAula(Spinner spAulas) {
        Adapter adapter1 = spAulas.getAdapter();
        Adapter adapter = mAdaptadorAulas;
        if (adapter == null) { //Esto es para cuando no hay ningún producto en un aula
            return new ArrayList<>();
        }
        int cantidadAulas = adapter.getCount();
        List<Aula> aulas = new ArrayList<>(cantidadAulas);
//        aulas.add(new Aula());
        for (int i = 0; i < cantidadAulas; i++) {
            Aula aula = (Aula) adapter.getItem(i);
            aulas.add(aula);
        }
        return aulas;
    }

    private int devolverPosicionAulaConMismoId(List<Aula> aulas, String aulaId) {
        if (aulaId.equals("")) return 0;
        for (int i = 0; i < aulas.size(); i++) {
            if (aulas.get(i).getId().equals(aulaId)) {
                return i;
            }
        }
        return 0;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFiltroProductoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //INits
        binding.spDptos.setAdapter(mAdaptadorDptos);
        binding.spAulas.setAdapter(mAdaptadorAulas);

        //Hint in fec
        String today = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Calendar.getInstance().getTime());
        binding.etFecAlta.setHint(today);

        binding.btFiltroAceptar.setOnClickListener(btFiltroAceptar_OnClickListener);
        binding.btFiltroCancelar.setOnClickListener(btFiltroCancelar_OnClickListener);
        binding.btFecAlta.setOnClickListener(btFecAlta_onClickListener);

        binding.spDptos.setOnItemSelectedListener(spDptos_onItemSelectedListener);
//        binding.spAulas.setOnItemSelectedListener(spAulas_onItemSelectedListener);

    }

    private View.OnClickListener btFiltroCancelar_OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            if (mCallback != null) {
                mCallback.onCancelarFiltroProductosFrag();
            }
        }
    };
    private View.OnClickListener btFiltroAceptar_OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            if (mCallback != null) {
                String fecha = ((binding.etFecAlta.getText().toString().equals(""))
                        ? new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Calendar.getInstance().getTime())
                        : binding.etFecAlta.getText().toString());
                String idAula = ((Aula) binding.spAulas.getSelectedItem()).getId();
                String idDpto = //Si descomentas las dos siguientes lineas, admin ve todas las aulas
//                        productosVM.getLogin().getId().equals("0") ?
//                        "" :
                        ((Departamento) binding.spDptos.getSelectedItem()).getId();
                mCallback.onAceptarFiltroProductosFrag(fecha, idAula, idDpto);
            }
        }
    };
    private View.OnClickListener btFecAlta_onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            DialogFragment dialogoFecha = new DlgSeleccionFecha();
            dialogoFecha.show(getParentFragmentManager(), "datePicker");
        }
    };

    private AdapterView.OnItemSelectedListener spDptos_onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
            Spinner spDpto = (Spinner) parent;
            String idDptoElegido = ((Departamento) spDpto.getSelectedItem()).getId();

            mAdaptadorAulas = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_dropdown_item);
//            productosVM.getmProductoFiltro().setIdDpto(depElegido.getId());

            AulasViewModel aulasVM = new ViewModelProvider(requireActivity()).get(AulasViewModel.class);
            aulasVM.getAulas().observe(getViewLifecycleOwner(), new Observer<List<Aula>>() {
                @Override
                public void onChanged(List<Aula> aulas) {
                    List<Aula> aulasDeEseDpto = devolverAulasConEseDepartamento(aulas, idDptoElegido);
                    mAdaptadorAulas.clear();
                    mAdaptadorAulas.add(new Aula()); //id = 0
                    mAdaptadorAulas.addAll(aulasDeEseDpto);
                    binding.spAulas.setAdapter(mAdaptadorAulas);
                    for (int i = 0; i < aulasDeEseDpto.size(); i++) {
                        if (aulasDeEseDpto.get(i).getId().equals(productosVM.getmProductoFiltro().getIdAula())) {
                            binding.spAulas.setSelection(i+1);
                            break;
                        }
                    }
                }
            });
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            ;
        }
    };

    private List<Aula> devolverAulasConEseDepartamento(List<Aula> aulas, String idDpto) {
        //Si el aula es "" devolvemos todas las aulas para que pueda elegir cualquiera
        if (idDpto.equals("")
//        || idDpto.equals("0") //Si descomentas esta linea admin tambien tiene todas las aulas disponibles
        ) {
            return aulas;
        }

        List<Aula> aulasDeEseDpto = new ArrayList<>();
        for (Aula aula : aulas) {
            if (aula.getIdDpto().equals(idDpto)) {
                aulasDeEseDpto.add(aula);
            }
        }
        return aulasDeEseDpto;
    }

//    private AdapterView.OnItemSelectedListener spAulas_onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
//        @Override
//        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//            Spinner sp = (Spinner) parent;
////            if (sp.getSelectedItem().toString().equalsIgnoreCase("")) {
////                //Aula vacia, guardamos ese aula en el VM para que luego recuerde que pusimos el aula vacia
////                ProductosViewModel productosVM = new ViewModelProvider(requireActivity()).get(ProductosViewModel.class);
////                productosVM.setAulaSeleccionadaFiltro(new Aula());
////            } else {
////                sp.getSelectedItem().toString();
////                Snackbar.make(binding.getRoot(), sp.getSelectedItem().toString(), BaseTransientBottomBar.LENGTH_SHORT).show();
//            Aula aula = (Aula) sp.getSelectedItem();
//            ProductosViewModel productosVM = new ViewModelProvider(requireActivity()).get(ProductosViewModel.class);
//            productosVM.setAulaSeleccionadaFiltro(aula);
////            }
//        }
//
//        @Override
//        public void onNothingSelected(AdapterView<?> adapterView) {
//            ;
//        }
//    };

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
