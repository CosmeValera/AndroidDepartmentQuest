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

    public interface FiltroProductosFragmentInterface {
//        void onClickAbrirDlgSeleccionFecha();

        void onCancelarFiltroProductosFrag();

        void onAceptarFiltroProductosFrag(String fecAlta, String idAula);
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
        ProductosViewModel productosVM = new ViewModelProvider(requireActivity()).get(ProductosViewModel.class);
        AulasViewModel aulasVM = new ViewModelProvider(requireActivity()).get(AulasViewModel.class);
        mAdaptadorAulas = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1);
        mAdaptadorAulas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Recuperar aulas al spinner
        aulasVM.getAulas().observe(this, new Observer<List<Aula>>() {
            @Override
            public void onChanged(List<Aula> aulas) {
                mAdaptadorAulas.clear();
                mAdaptadorAulas.add(new Aula()); //id = 0
                mAdaptadorAulas.addAll(aulas);
                binding.spAulas.setAdapter(mAdaptadorAulas);
                binding.spAulas.setSelection(0, false);
                if (mAula != null) {
                    for (int i = 0; i < aulas.size(); i++) {
                        if (aulas.get(i).getId().equals(mAula.getId())) {
                            binding.spAulas.setSelection(i);
                            //TODO yo creo que lo que pasa es que se otro hilo el que ejecuta el
                            // obesrver y por tanto no sabe las aulas  tiene
                            productosVM.setAulaSeleccionadaFiltro(aulas.get(i));
                            break;
                        }
                    }
                }

            }
        });

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
                Departamento login = productosVM.getLogin();
                if (login.getId().equals("0")) {
                    binding.spDptos.setEnabled(true);
                } else {
                    binding.spDptos.setEnabled(false);
                }
                mAdaptadorDtpos.addAll(dptos);
                mAdaptadorDtpos.notifyDataSetChanged();


                for (int i = 0; i < dptos.size(); i++) {
                    if (dptos.get(i).getId().equals(login.getId())) {
                        binding.spDptos.setSelection(i);
                    }
                }

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
                //TODO el problema es que este metodo devolverTodasLasAulas no me devuelve nada
                List<Aula> aulas = devolverTodasLasAulas(binding.spAulas);
                for (int i = 0; i < aulas.size(); i++) {
                    if (aulas.get(i).equals(aulaId)) {
                        binding.spAulas.setSelection(i);
                    }
                }
            }
        });

    }

    private List<Aula> devolverTodasLasAulas(Spinner spAulas) {
        Adapter adapter1 = spAulas.getAdapter();
        Adapter adapter = mAdaptadorAulas;
        if (adapter == null) { //Esto es para cuando no hay ningún producto en un aula
            //TODO: En el observer de getAulaSeleccionadaFiltro() le lllega bien la alta pero al llegar aqui el adapter es null
            return new ArrayList<>();
        }
        //TODO adapter.getCount es 0
        int cantidadAulas = adapter.getCount();
        List<Aula> aulas = new ArrayList<>(cantidadAulas);
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
                String id = (binding.spAulas.getSelectedItem() != null) ?
                        ((Aula) binding.spAulas.getSelectedItem()).getId()
                        : "%";
                mCallback.onAceptarFiltroProductosFrag(fecha, id);
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
            if (sp.getSelectedItem().toString().equalsIgnoreCase("")) {
                //Aula vacia, guardamos ese aula en el VM para que luego recuerde que pusimos el aula vacia
                ProductosViewModel productosVM = new ViewModelProvider(requireActivity()).get(ProductosViewModel.class);
                productosVM.setAulaSeleccionadaFiltro(new Aula());
            } else {
                sp.getSelectedItem().toString();
//                Snackbar.make(binding.getRoot(), sp.getSelectedItem().toString(), BaseTransientBottomBar.LENGTH_SHORT).show();
                Aula aula = (Aula) sp.getSelectedItem();
                ProductosViewModel productosVM = new ViewModelProvider(requireActivity()).get(ProductosViewModel.class);
                productosVM.setAulaSeleccionadaFiltro(aula);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            ;
        }
    };
}
