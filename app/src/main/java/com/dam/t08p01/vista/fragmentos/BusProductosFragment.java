package com.dam.t08p01.vista.fragmentos;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dam.t08p01.R;
import com.dam.t08p01.databinding.FragmentBusProductosBinding;
import com.dam.t08p01.modelo.Aula;
import com.dam.t08p01.modelo.Producto;
import com.dam.t08p01.vista.adaptadores.AdaptadorProductos;
import com.dam.t08p01.vistamodelo.ProductosViewModel;

import java.util.List;

public class BusProductosFragment extends Fragment
        implements AdaptadorProductos.AdaptadorProductosInterface {

    private FragmentBusProductosBinding binding;
    private BusProductosFragInterface mCallback;

    private AdaptadorProductos mAdaptadorProductos;

    public interface BusProductosFragInterface {
        void onCrearBusProductosFrag();

        void onEditarBusProductosFrag(Producto producto);

        void onEliminarBusProductosFrag(Producto producto);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof BusProductosFragInterface) {
            mCallback = (BusProductosFragInterface) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement BusProductosFragInterface");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Inits
        ProductosViewModel productosVM = new ViewModelProvider(requireActivity()).get(ProductosViewModel.class);
//        productosVM.getmProductoFiltro().setIdDpto(mLogin.getId());
        mAdaptadorProductos = new AdaptadorProductos(this);

        // Inits Productos Observer
        productosVM.getProductosByFiltro().observe(this, new Observer<List<Producto>>() {
            @Override
            public void onChanged(List<Producto> productos) {

                mAdaptadorProductos.setDatos(productos);

                mAdaptadorProductos.notifyDataSetChanged();
                if (mAdaptadorProductos.getItemPos() != -1 &&
                        mAdaptadorProductos.getItemPos() < mAdaptadorProductos.getItemCount()) {
                    binding.rvProductos.scrollToPosition(mAdaptadorProductos.getItemPos());
                } else if (mAdaptadorProductos.getItemCount() > 0) {
                    binding.rvProductos.scrollToPosition(mAdaptadorProductos.getItemCount() - 1);
                }
                mAdaptadorProductos.setItemPos(-1);
                binding.btProductoEliminar.setEnabled(false);
                binding.btProductoEditar.setEnabled(false);
                binding.btProductoCrear.setEnabled(true);

            }
        });
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBusProductosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init RecyclerView Dptos
        binding.rvProductos.setHasFixedSize(true);
        binding.rvProductos.setLayoutManager(new LinearLayoutManager(view.getContext()));
        binding.rvProductos.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));
        binding.rvProductos.setAdapter(mAdaptadorProductos);

        // Inits
        if (mAdaptadorProductos.getItemPos() != -1) {
            binding.btProductoEliminar.setEnabled(true);
            binding.btProductoEditar.setEnabled(true);
            binding.btProductoCrear.setEnabled(false);
        } else {
            binding.btProductoEliminar.setEnabled(false);
            binding.btProductoEditar.setEnabled(false);
            binding.btProductoCrear.setEnabled(true);
        }

        // Listeners
        binding.btProductoCrear.setOnClickListener(btProductoCrear_OnClickListener);
        binding.btProductoEditar.setOnClickListener(btProductoEditar_OnClickListener);
        binding.btProductoEliminar.setOnClickListener(btProductoEliminar_OnClickListener);
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
    public void onItemClickRVProductos(int pos) {
        if (pos != -1) {
            binding.btProductoEliminar.setEnabled(true);
            binding.btProductoEditar.setEnabled(true);
            binding.btProductoCrear.setEnabled(false);
        } else {
            binding.btProductoEliminar.setEnabled(false);
            binding.btProductoEditar.setEnabled(false);
            binding.btProductoCrear.setEnabled(true);
        }
    }

    private final View.OnClickListener btProductoCrear_OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int pos = mAdaptadorProductos.getItemPos();
            if (pos == -1) {
                if (mCallback != null) {
                    mCallback.onCrearBusProductosFrag();
                }
            }
        }
    };

    private final View.OnClickListener btProductoEditar_OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int pos = mAdaptadorProductos.getItemPos();
            if (pos >= 0) {
                if (mCallback != null) {
//                    Producto productoVista = mAdaptadorProductos.getItem(pos);
//                    Producto prodAPasar = productoVista.getProducto();
//
//                    mCallback.onEditarBusProductosFrag(prodAPasar);


                    //Al editar un producto que te salga puesto de antes el aula que tiene
                    ProductosViewModel productosVM = new ViewModelProvider(requireActivity()).get(ProductosViewModel.class);
                    Aula aulaConIdParaMto = new Aula();
                    Producto prod = mAdaptadorProductos.getItem(pos);
                    aulaConIdParaMto.setId(prod.getIdAula());
                    productosVM.setAulaSeleccionadaMto(aulaConIdParaMto);


                    mCallback.onEditarBusProductosFrag(mAdaptadorProductos.getItem(pos));
                }
            }
        }
    };

    private final View.OnClickListener btProductoEliminar_OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int pos = mAdaptadorProductos.getItemPos();
            if (pos >= 0) {
                if (mCallback != null) {
//                    Producto productoVista = mAdaptadorProductos.getItem(pos);
//                    Producto prodAPasar = productoVista.getProducto();
//                    mCallback.onEliminarBusProductosFrag(prodAPasar);

                    mCallback.onEliminarBusProductosFrag(mAdaptadorProductos.getItem(pos));
                }
            }
        }
    };

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_bus_productos, menu);
    }


}
