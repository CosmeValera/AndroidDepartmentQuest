package com.dam.t08p01.vista.fragmentos;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.dam.t08p01.R;
import com.dam.t08p01.databinding.FragmentMtoProductosBinding;
import com.dam.t08p01.modelo.Aula;
import com.dam.t08p01.modelo.Departamento;
import com.dam.t08p01.modelo.Producto;
import com.dam.t08p01.vistamodelo.AulasViewModel;
import com.dam.t08p01.vistamodelo.ProductosViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MtoProductosFragment extends Fragment {

    private FragmentMtoProductosBinding binding;
    private MtoProductosFragInterface mCallback;

    private int mOp;    // Operación a realizar
    private Producto mProducto;

    public static final int OP_ELIMINAR = 1;
    public static final int OP_EDITAR = 2;
    public static final int OP_CREAR = 3;

    public interface MtoProductosFragInterface {
        void onCancelarMtoProductosFrag();

        void onAceptarMtoProductosFrag(int op, Producto producto);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MtoProductosFragInterface) {
            mCallback = (MtoProductosFragInterface) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement MtoProductosFragInterface");
        }
    }

    //Esta mejor la recuperación de aulas en el onCreate para que solo se ejecute una vez
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mOp = getArguments().getInt("op");
            mProducto = getArguments().getParcelable("producto");
        } else {
            mOp = -1;
            mProducto = null;
        }

//        NavHostFragment navHost = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);


        //Inits
        ProductosViewModel productosVM = new ViewModelProvider(requireActivity()).get(ProductosViewModel.class);
        AulasViewModel aulasVM = new ViewModelProvider(requireActivity()).get(AulasViewModel.class);
        ArrayAdapter<Aula> aulasAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1);
        aulasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Recuperar aulas de Firebase


        //Aulas observer
        aulasVM.getAulas().observe(this, new Observer<List<Aula>>() {
            @Override
            public void onChanged(List<Aula> aulas) {
                aulasAdapter.clear();
                List<Aula> aulasDeEseDpto = devolverAulasConEseDepartamento(aulas, productosVM.getLogin().getId());
                aulasAdapter.addAll(aulasDeEseDpto);
                binding.spAulas.setAdapter(aulasAdapter);
                binding.spAulas.setSelection(0, false);

                //en caso de edicion seleccionar por defecto el aula que había
                if (mProducto != null) { //Editar
                    for (int i = 0; i < aulas.size(); i++) {
                        if (aulas.get(i).getId().equals(mProducto.getIdAula())) {
                            binding.spAulas.setSelection(i);
                            break;
                        }
                    }
                }
            }
        });

        //Recordar aulas
        productosVM.getAulaSeleccionadaMto().observe(this, new Observer<Aula>() {
            @Override
            public void onChanged(Aula aula) {
                String aulaId = aula.getId();
                List<Aula> aulas = devolverTodasLasAulas(binding.spAulas);
                int posicion = devolverPosicionAulaConMismoId(aulas, aulaId);

                binding.spAulas.setSelection(posicion);
            }
        });
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

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMtoProductosBinding
                .inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inits
        if (mOp != -1) {    // MtoProductosFragment requiere una operación válida!!
            binding.btProductoCancelar.setEnabled(true);
            binding.btProductoAceptar.setEnabled(true);

            ProductosViewModel productosVM = new ViewModelProvider
                    (requireActivity()).get(ProductosViewModel.class);
            Departamento login = productosVM.getLogin();

            switch (mOp) {
                case OP_CREAR:
                    binding.tvProductoCabecera.setText(getString(R.string.tv_Producto_Cabecera_Crear));
                    binding.etProductoIdDpto.setText(String.valueOf(login.getId()));
                    binding.etProductoIdDpto.setEnabled(false);
                    binding.etProductoIdDptoNombre.setText(login.getNombre());
                    binding.etProductoIdDptoNombre.setEnabled(false);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss", Locale.getDefault());
                    binding.etProductoId.setText(sdf.format(Calendar.getInstance().getTime()));
                    binding.etProductoId.setEnabled(false);
                    break;
                case OP_EDITAR:
                    binding.tvProductoCabecera.setText(getString(R.string.tv_Producto_Cabecera_Editar));
                    binding.etProductoIdDpto.setText(String.valueOf(mProducto.getIdDpto()));
                    binding.etProductoIdDpto.setEnabled(false);
                    binding.etProductoIdDptoNombre.setText(login.getNombre());
                    binding.etProductoIdDptoNombre.setEnabled(false);
                    binding.etProductoId.setText(mProducto.getId());
                    binding.etProductoId.setEnabled(false);
                    binding.etProductoNombre.setText(mProducto.getNombre());

                    binding.etProductoIdDpto.setText(String.valueOf(mProducto.getIdDpto()));
                    binding.etProductoId.setText(mProducto.getId());
                    binding.etFecAlta.setText(String.valueOf(mProducto.getFecAltaF()));
                    binding.etProductoNombre.setText(String.valueOf(mProducto.getNombre()));
                    binding.etProductoCantidad.setText(String.valueOf(mProducto.getCantidad()));
                    break;
            }
            //Hint in fec
            String today = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Calendar.getInstance().getTime());
            binding.etFecAlta.setHint(today);

            // Listeners
            binding.btProductoCancelar.setOnClickListener(btProductoCancelar_OnClickListener);
            binding.btProductoAceptar.setOnClickListener(btProductoAceptar_OnClickListener);
        } else {
            binding.btProductoCancelar.setEnabled(false);
            binding.btProductoAceptar.setEnabled(false);
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

    private final View.OnClickListener btProductoCancelar_OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            if (mCallback != null) {
                mCallback.onCancelarMtoProductosFrag();
            }
        }
    };

    private final View.OnClickListener btProductoAceptar_OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            if (mCallback != null) {
                if (!binding.etProductoIdDpto.getText().toString().equals("") &&
                        !binding.etProductoId.getText().toString().equals("") &&
//                        !binding.etFecAlta.getText().toString().equals("") && //Si está vacío ponemos la fecha actual
                        respetaFormatoFecha() &&
                        !binding.etProductoNombre.getText().toString().equals("")
//                       && !binding.etProductoCantidad.getText().toString().equals("")  //Cantidad no es obligatoria
                ) {
                    // Creamos un nuevo producto, independientemente de la operación a realizar!!
                    mProducto = new Producto();
                    mProducto.setIdDpto(Integer.parseInt(binding.etProductoIdDpto.getText().toString()));
                    mProducto.setId(binding.etProductoId.getText().toString());
                    mProducto.setFecAltaF((binding.etFecAlta.getText().toString().equals(""))
                            ? new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Calendar.getInstance().getTime())
                            : binding.etFecAlta.getText().toString());
                    mProducto.setNombre(binding.etProductoNombre.getText().toString());
                    mProducto.setIdAula(((Aula) binding.spAulas.getSelectedItem()).getId());
                    mProducto.setCantidad(Integer.parseInt(
                            (binding.etProductoCantidad.getText().toString().equals("")
                                    ? "0"
                                    : binding.etProductoCantidad.getText().toString())
                    ));

                    mCallback.onAceptarMtoProductosFrag(mOp, mProducto);
                } else {
                    mCallback.onAceptarMtoProductosFrag(mOp, null);  // Faltan Datos Obligatorios
                }
            }
        }
    };

    private boolean respetaFormatoFecha() {
        String fecha = binding.etFecAlta.getText().toString();
        if (fecha.equals("")) { //Si es "" entonces ponemos la fecha actual
            return true;
        }

        //Validaciones
        if (fecha.length() != 10) {
            return false;
        }
        if (fecha.charAt(2) != '/' || fecha.charAt(5) != '/') {
            return false;
        }
        try { //Comprobar que solo tiene números
            int dia = Integer.parseInt(fecha.substring(0, 2));
            int mes = Integer.parseInt(fecha.substring(3, 5));
            Integer.parseInt(fecha.substring(6, 10));
            if (dia > 31 || mes > 12) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
