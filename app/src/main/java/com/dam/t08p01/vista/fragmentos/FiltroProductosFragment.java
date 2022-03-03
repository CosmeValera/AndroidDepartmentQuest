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

public class FiltroProductosFragment extends Fragment {

    private FragmentFiltroProductoBinding binding;
    private FiltroProductosFragmentInterface mCallback;

    private Aula mAula;
    private ArrayAdapter<Aula> mAdaptadorAulas;
    private ArrayAdapter<Departamento> mAdaptadorDtpos;

    private ProductosViewModel productosVM;
    private AulasViewModel aulasVM;

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
        //Siempre llega mAula vacia, no se hace con esto, para recuperar el aula
        if (getArguments() != null) {
            mAula = getArguments().getParcelable("aula");
        } else {
            mAula = null;
        }

        //Inits
        productosVM = new ViewModelProvider(requireActivity()).get(ProductosViewModel.class);
        aulasVM = new ViewModelProvider(requireActivity()).get(AulasViewModel.class);
        mAdaptadorAulas = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1);
        mAdaptadorAulas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Esto ya no lo hacemos así pq de esta forma ya no funciona, si no que lo ponemos en el onViewCreated un onClick sobre spineerDptos
//        //Recuperar aulas al spinner
//        aulasVM.getAulas().observe(this, new Observer<List<Aula>>() {
//            @Override
//            public void onChanged(List<Aula> aulas) {
//                mAdaptadorAulas.clear();
//                mAdaptadorAulas.add(new Aula()); //id = 0
//                List<Aula> aulasDeEseDpto = devolverAulasConEseDepartamento(aulas, productosVM.getLogin().getId());
//                mAdaptadorAulas.addAll(aulasDeEseDpto);
//                binding.spAulas.setAdapter(mAdaptadorAulas);
//                binding.spAulas.setSelection(0, false);
//                if (mAula != null) {
//                    for (int i = 0; i < aulas.size(); i++) {
//                        if (aulas.get(i).getId().equals(mAula.getId())) {
//                            binding.spAulas.setSelection(i);
//                            productosVM.setAulaSeleccionadaFiltro(aulas.get(i));
//                            break;
//                        }
//                    }
//                }
//            }
//        });

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
                Departamento loginGeneral = productosVM.getLogin();
                if (loginGeneral.getId().equals("0")) {
                    binding.spDptos.setEnabled(true);
                } else {
                    binding.spDptos.setEnabled(false);
                }
                mAdaptadorDtpos.add(new Departamento());
                mAdaptadorDtpos.addAll(dptos);

                String idDpto = productosVM.getmProductoFiltro().getIdDpto();
                for (int i = 0; i < dptos.size(); i++) {
                    if (dptos.get(i).getId().equals(idDpto)) {
                        binding.spDptos.setSelection(i+1); //Pq 0 es vacio
                        break;
                    }
                }

                mAdaptadorDtpos.notifyDataSetChanged();
            }
        });

        //Recordar fecha
        productosVM.getFechaDlg().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.etFecAlta.setText(s);
            }
        });

        //Recordar aula
//        productosVM.getAulaSeleccionadaFiltro().observe(this, new Observer<Aula>() {
//            @Override
//            public void onChanged(Aula aula) {
//                String aulaId = aula.getId();
//                List<Aula> aulas = devolverTodasLasAulas(binding.spAulas);
//                int posicion = devolverPosicionAulaConMismoId(aulas, aulaId);
//
//                binding.spAulas.setSelection(posicion);
//            }
//        });
        //Recordar aula
        productosVM.getAulaSeleccionadaFiltro().observe(this, new Observer<Aula>() {
            @Override
            public void onChanged(Aula aula) {
                String aulaId = aula.getId();
                List<Aula> aulas = devolverTodasLasAulas(binding.spAulas);
                //Aula de 0 es todas
                for (int i = 0; i < aulas.size(); i++) {
                    if (aulas.get(i).equals(aulaId)) {
                        binding.spAulas.setSelection(i);
                        break;
                    }
                }
            }
        });

    }

    private List<Aula> devolverTodasLasAulas(Spinner spAulas) {
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

    private List<Aula> devolverAulasConEseDepartamento(List<Aula> aulas, String idDpto) {
        //Si el aula es admin devolvemos todas las aulas para que pueda elegir cualquiera
        if (idDpto.equals("0")) {
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFiltroProductoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btFiltroAceptar.setEnabled(true);
        binding.btFiltroCancelar.setEnabled(true);

        //INits
        binding.spDptos.setAdapter(mAdaptadorDtpos);
        binding.spAulas.setAdapter(mAdaptadorAulas);

        //Hint in fec
        String today = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Calendar.getInstance().getTime());
        binding.etFecAlta.setHint(today);

        binding.btFiltroAceptar.setOnClickListener(btFiltroAceptar_OnClickListener);
        binding.btFiltroCancelar.setOnClickListener(btFiltroCancelar_OnClickListener);
        binding.btFecAlta.setOnClickListener(btFecAlta_onClickListener);
        binding.spAulas.setOnItemSelectedListener(spAulas_onItemSelectedListener);

        binding.spDptos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                Spinner sp = (Spinner) parent;
                Departamento depElegido = (Departamento) sp.getSelectedItem();
                if (!((Departamento)sp.getSelectedItem()).getId().equals("")){
                    productosVM.getmProductoFiltro().setIdDpto(depElegido.getId());
                }
                //Hay que poner aqui el login del departamento que acabamos de seelccionar para que en el observer de despues lo haga bien
                aulasVM.getAulas().observe(getViewLifecycleOwner(), new Observer<List<Aula>>() {
                    @Override
                    public void onChanged(List<Aula> aulas) {
                        mAdaptadorAulas.clear();
                        mAdaptadorAulas.add(new Aula()); //id = 0
                        List<Aula> aulasDeEseDpto = devolverAulasConEseDepartamento(aulas, productosVM.getLogin().getId());
                        mAdaptadorAulas.addAll(aulasDeEseDpto);
                        binding.spAulas.setAdapter(mAdaptadorAulas);
                        binding.spAulas.setSelection(0, false);
                        if (mAula != null) {
                            for (int i = 0; i < aulas.size(); i++) {
                                if (aulas.get(i).getId().equals(mAula.getId())) {
                                    binding.spAulas.setSelection(i);
                                    productosVM.setAulaSeleccionadaFiltro(aulas.get(i));
                                    break;
                                }
                            }
                        }
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                ;
            }
        });

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
                String idAula = (binding.spAulas.getSelectedItem() != null) ?
                        ((Aula) binding.spAulas.getSelectedItem()).getId()
                        : "%";
                String idDpto = productosVM.getLogin().getId().equals("0")?
                        ""
                        : ((Departamento)binding.spDptos.getSelectedItem()).getId();
                mCallback.onAceptarFiltroProductosFrag(fecha, idAula, idDpto);
            }
        }
    };

    private View.OnClickListener btFecAlta_onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            DialogFragment dialogoFecha = new DlgSeleccionFecha();
            dialogoFecha.show(getParentFragmentManager(), "datePicker");
//            mCallback.onClickAbrirDlgSeleccionFecha();
        }
    };

    private AdapterView.OnItemSelectedListener spAulas_onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Spinner sp = (Spinner) parent;
//            if (sp.getSelectedItem().toString().equalsIgnoreCase("")) {
//                //Aula vacia, guardamos ese aula en el VM para que luego recuerde que pusimos el aula vacia
//                ProductosViewModel productosVM = new ViewModelProvider(requireActivity()).get(ProductosViewModel.class);
//                productosVM.setAulaSeleccionadaFiltro(new Aula());
//            } else {
//                sp.getSelectedItem().toString();
//                Snackbar.make(binding.getRoot(), sp.getSelectedItem().toString(), BaseTransientBottomBar.LENGTH_SHORT).show();
                Aula aula = (Aula) sp.getSelectedItem();
                ProductosViewModel productosVM = new ViewModelProvider(requireActivity()).get(ProductosViewModel.class);
                productosVM.setAulaSeleccionadaFiltro(aula);
//            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            ;
        }
    };
}
