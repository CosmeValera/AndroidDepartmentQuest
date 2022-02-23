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
import com.dam.t08p01.vista.dialogos.DlgSeleccionFecha;
import com.dam.t08p01.vistamodelo.AulasViewModel;
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
        if (getArguments() != null) {
            mAula = getArguments().getParcelable("aula");
        } else {
            mAula = null;
        }

        //Inits
        AulasViewModel aulasVM = new ViewModelProvider(requireActivity()).get(AulasViewModel.class);
        ArrayAdapter<Aula> aulasAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1);
        aulasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Recuperar aulas al spinner
        aulasVM.getAulas().observe(this, new Observer<List<Aula>>() {
            @Override
            public void onChanged(List<Aula> aulas) {
                aulasAdapter.clear();
                aulasAdapter.add(new Aula()); //id = 0
                aulasAdapter.addAll(aulas);
                binding.spAulas.setAdapter(aulasAdapter);
                binding.spAulas.setSelection(0, false);
                if (mAula != null) {
                    for (int i = 0; i < aulas.size(); i++) {
                        if (aulas.get(i).getId().equals(mAula.getId())) {
                            binding.spAulas.setSelection(i);
                            break;
                        }
                    }
                }
            }
        });

        //Recordar fecha
        ProductosViewModel productosVM = new ViewModelProvider(requireActivity()).get(ProductosViewModel.class);
        productosVM.getFechaDlg().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.etFecAlta.setText(s);
            }
        });

        //Recordar aula
//
        productosVM.getAulaSeleccionada().observe(this, new Observer<Aula>() {
            @Override
            public void onChanged(Aula aula) {
                String aulaId = aula.getId();
                List<Aula> aulas = devolverTodasLasAulas(binding.spAulas);
                int posicion = devolverPosicionAulaConMismoId(aulas, aulaId);

                binding.spAulas.setSelection(posicion);
            }
        });




//        productosVM.getProductosByFiltro().observe(this, new Observer<List<Producto>>() {
//            @Override
//            public void onChanged(List<Producto> productos) {
//                //Cojo el id de uno de los productos, busco el aulas con ese mismo id y la pongo
//                List<Aula> aulas = devolverTodasLasAulas(binding.spAulas);
//                if (aulas.size() == 0 || productos.size() == 0) {
//                    return;
//                }
//                String aulaId = productos.get(0).getIdAula();
//                int posicion = devolverPosicionAulaConMismoId(aulas, aulaId);
//
//                binding.spAulas.setSelection(posicion);
//            }
//        });
    }

    private List<Aula> devolverTodasLasAulas(Spinner spAulas) {
        Adapter adapter = spAulas.getAdapter();
        if (adapter == null) { //Esto es para cuando no hay ningún producto en un aula
            return new ArrayList<>();
        }
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
                productosVM.setAulaSeleccionada(new Aula());
            } else {
                sp.getSelectedItem().toString();
//                Snackbar.make(binding.getRoot(), sp.getSelectedItem().toString(), BaseTransientBottomBar.LENGTH_SHORT).show();
                Aula aula = (Aula)sp.getSelectedItem();
                ProductosViewModel productosVM = new ViewModelProvider(requireActivity()).get(ProductosViewModel.class);
                productosVM.setAulaSeleccionada(aula);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
;
        }
    };
}
