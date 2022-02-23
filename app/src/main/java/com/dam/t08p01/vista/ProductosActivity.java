package com.dam.t08p01.vista;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.dam.t08p01.R;
import com.dam.t08p01.databinding.ActivityProductosBinding;
import com.dam.t08p01.databinding.ContentProductosBinding;
import com.dam.t08p01.modelo.Departamento;
import com.dam.t08p01.modelo.Producto;
import com.dam.t08p01.vista.dialogos.DlgConfirmacion;
import com.dam.t08p01.vista.dialogos.DlgSeleccionFecha;
import com.dam.t08p01.vista.fragmentos.BusProductosFragment;
import com.dam.t08p01.vista.fragmentos.FiltroProductosFragment;
import com.dam.t08p01.vista.fragmentos.MtoProductosFragment;
import com.dam.t08p01.vistamodelo.ProductosViewModel;
import com.google.android.material.snackbar.Snackbar;

public class ProductosActivity extends AppCompatActivity
        implements DlgConfirmacion.DlgConfirmacionListener,
        MtoProductosFragment.MtoProductosFragInterface,
        DlgSeleccionFecha.DlgSeleccionFechaListener,
        BusProductosFragment.BusProductosFragInterface,
        FiltroProductosFragment.FiltroProductosFragmentInterface {

    private ActivityProductosBinding binding;
    private ContentProductosBinding bindingC;
    private NavController mNavC;
//    private AppBarConfiguration mAppBarConfiguration;

    private ProductosViewModel mProductosVM;
    private Departamento mLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Recuperamos el dpto login
        Intent i = getIntent();
        if (i != null) {
            Bundle b = i.getExtras();
            if (b != null) {
                mLogin = b.getParcelable("login");
                mProductosVM = new ViewModelProvider(this).get(ProductosViewModel.class);
                mProductosVM.setLogin(mLogin);      // Guardamos el login en el ViewModel
            }
        }
        if (mLogin == null) {   // Esto no debería pasar nunca!!
            finish();
            return;
        }

        // Inflate Layout (esto está aquí para que lance el fragmento buscador después de las inicializaciones anteriores)!!
        binding = ActivityProductosBinding.inflate(getLayoutInflater());
        bindingC = binding.contentProductos;
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Inits Navigation
        NavHostFragment navHost = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_productos);
        mNavC = navHost.getNavController();
//        mAppBarConfiguration = new AppBarConfiguration.Builder(mNavController.getGraph()).build();
//        NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);

        // Inits

        // Listeners

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

//    @Override
//    public boolean onSupportNavigateUp() {
//        return NavigationUI.navigateUp(mNavController, mAppBarConfiguration)
//                || super.onSupportNavigateUp();
//    }

    @Override
    public void onCrearBusProductosFrag() {
        // Lanzamos MtoProductosFragment
        Bundle bundle = new Bundle();
        bundle.putInt("op", MtoProductosFragment.OP_CREAR);
        mNavC.navigate(R.id.action_bus_productos_fragment_to_mto_productos_fragment, bundle);
    }

    @Override
    public void onEditarBusProductosFrag(Producto producto) {
        // Lanzamos MtoProductosFragment
        Bundle bundle = new Bundle();
        bundle.putInt("op", MtoProductosFragment.OP_EDITAR);
        bundle.putParcelable("producto", producto);
        mNavC.navigate(R.id.action_bus_productos_fragment_to_mto_productos_fragment, bundle);
    }

    @Override
    public void onEliminarBusProductosFrag(@NonNull Producto producto) {
        if (producto.getId().equals("0")) return;  // no se puede borrar el producto "sin asignar"!!
        // Guardamos el producto a eliminar en el ViewModel
        mProductosVM.setProductoAEliminar(producto);
        // Lanzamos DlgConfirmacion
        Bundle bundle = new Bundle();
        bundle.putInt("titulo", R.string.app_name);
        bundle.putInt("mensaje", R.string.msg_DlgConfirmacion_Eliminar);
        mNavC.navigate(R.id.action_global_dlgConfirmacionProductos, bundle);
    }

//    @Override
//    public void onClickAbrirDlgSeleccionFecha() {
//        DialogFragment dialogoFecha = new DlgSeleccionFecha();
//        dialogoFecha.show(getSupportFragmentManager(), "datePicker");
//    }

    @Override
    public void onCancelarMtoProductosFrag() {
        // Cerramos MtoProductosFragment
        mNavC.navigateUp();
    }

    @Override
    public void onAceptarMtoProductosFrag(int op, Producto producto) {
        if (producto == null) {   // Faltan Datos Obligatorios
            Snackbar.make(binding.getRoot(), R.string.msg_FaltanDatosObligatorios, Snackbar.LENGTH_SHORT).show();
            return;
        }
        switch (op) {
            case MtoProductosFragment.OP_CREAR:
                mProductosVM.altaProducto(producto).observe(this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean ok) {
                        Snackbar.make(binding.getRoot(), (ok) ? R.string.msg_AltaProductoOK : R.string.msg_AltaProductoKO, Snackbar.LENGTH_SHORT).show();
                    }
                });
                break;
            case MtoProductosFragment.OP_EDITAR:
                mProductosVM.editarProducto(producto).observe(this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean ok) {
                        Snackbar.make(binding.getRoot(), (ok) ? R.string.msg_EditarProductoOK : R.string.msg_EditarProductoKO, Snackbar.LENGTH_SHORT).show();
                    }
                });
                break;
            case MtoProductosFragment.OP_ELIMINAR:
                break;
        }

        // Cerramos MtoProductosFragment
        mNavC.navigateUp();
    }

    @Override
    public void onDlgConfirmacionPositiveClick(DialogFragment dialog) {
        // Recuperamos el producto a eliminar del ViewModel
        Producto productoAEliminar = mProductosVM.getProductoAEliminar();
        mProductosVM.bajaProducto(productoAEliminar).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean ok) {
                Snackbar.make(binding.getRoot(), (ok) ? R.string.msg_BajaProductoOK : R.string.msg_BajaProductoKO, Snackbar.LENGTH_SHORT).show();
            }
        });
        // Eliminamos el producto a eliminar del ViewModel
        mProductosVM.setProductoAEliminar(null);
    }

    @Override
    public void onDlgConfirmacionNegativeClick(DialogFragment dialog) {
        // Eliminamos el producto a eliminar del ViewModel
        mProductosVM.setProductoAEliminar(null);
    }

    // ESte va directamenete en el productos activity
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuFiltro) {
            mNavC.navigate(R.id.action_bus_productos_fragment_to_filtroProductosFragment);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDlgSeleccionFechaClick(DialogFragment dialog, String fecha) {
        mProductosVM.setFechaDlg(fecha);
//        mNavC.navigateUp();
    }

    @Override
    public void onDlgSeleccionFechaCancel(DialogFragment dialog) {
//        mNavC.navigateUp();
        ;
    }

//    @Override
//    public void onCancelarFiltroProductosFrag() {
//        mNavC.navigateUp();
//    }

//    @Override
//    public void onAceptarFiltroProductosFrag(String fecAlta, String idAula) {
//        mProductosVM.getmProductoFiltro().setFecAlta(fecAlta);
//        mProductosVM.getmProductoFiltro().setIdAula(idAula);
//        LiveData<List<Producto>> productosByFiltro = mProductosVM.getProductosByFiltro();
//        mNavC.navigate(R.id.action_filtroProductosFragment_to_bus_productos_fragment);
//
//    }

//    @Override
//    public void onClickAbrirDlgSeleccionFecha() {
//        DialogFragment dialogoFecha = new DlgSeleccionFecha();
//        dialogoFecha.show(getSupportFragmentManager(), "datePicker");
////        mNavC.navigate(R.id.action_filtroProductosFragment_to_dlgSeleccionFecha);
//    }

    @Override
    public void onCancelarFiltroProductosFrag() {
        mNavC.navigateUp();
    }

    @Override
    public void onAceptarFiltroProductosFrag(String fecAlta, String idAula) {
        mProductosVM.getmProductoFiltro().setFecAlta(fecAlta);
        mProductosVM.getmProductoFiltro().setIdAula(idAula);
//        mProductosVM.getProductosByFiltro();
        mNavC.navigate(R.id.action_filtroProductosFragment_to_bus_productos_fragment);
    }

//    @Override
//    public void onAceptarFiltroProductosFrag(String fecAlta, String idAula) {
//        //to do hay que hacer navigate al busProductosFragment, no hacer navigateUp() pq si no no se dispara el observer al aplicar la select
////        mPrduc....
////        mNavC.navigate(R.id.)
//    }
}
