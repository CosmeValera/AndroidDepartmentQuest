package com.dam.t08p01.vista;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.dam.t08p01.R;
import com.dam.t08p01.databinding.ActivityDptosBinding;
import com.dam.t08p01.databinding.ContentDptosBinding;
import com.dam.t08p01.modelo.Departamento;
import com.dam.t08p01.vista.dialogos.DlgConfirmacion;
import com.dam.t08p01.vista.fragmentos.BusDptosFragment;
import com.dam.t08p01.vista.fragmentos.MtoDptosFragment;
import com.dam.t08p01.vistamodelo.DptosViewModel;
import com.google.android.material.snackbar.Snackbar;

public class DptosActivity extends AppCompatActivity
        implements DlgConfirmacion.DlgConfirmacionListener,
        BusDptosFragment.BusDptosFragInterface,
        MtoDptosFragment.MtoDptosFragInterface {

    private ActivityDptosBinding binding;
    private ContentDptosBinding bindingC;
    private NavController mNavC;
//    private AppBarConfiguration mAppBarConfiguration;

    private DptosViewModel mDptosVM;
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
                mDptosVM = new ViewModelProvider(this).get(DptosViewModel.class);
                mDptosVM.setLogin(mLogin);      // Guardamos el login en el ViewModel
            }
        }
        if (mLogin == null) {   // Esto no debería pasar nunca!!
            finish();
            return;
        }

        // Inflate Layout (esto está aquí para que lance el fragmento buscador después de las inicializaciones anteriores)!!
        binding = ActivityDptosBinding.inflate(getLayoutInflater());
        bindingC = binding.contentDptos;
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Inits Navigation
        NavHostFragment navHost = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_dptos);
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
    public void onCrearBusDptosFrag() {
        // Lanzamos MtoDptosFragment
        Bundle bundle = new Bundle();
        bundle.putInt("op", MtoDptosFragment.OP_CREAR);
        mNavC.navigate(R.id.action_bus_dptos_fragment_to_mto_dptos_fragment, bundle);
    }

    @Override
    public void onEditarBusDptosFrag(Departamento dpto) {
        // Lanzamos MtoDptosFragment
        Bundle bundle = new Bundle();
        bundle.putInt("op", MtoDptosFragment.OP_EDITAR);
        bundle.putParcelable("dpto", dpto);
        mNavC.navigate(R.id.action_bus_dptos_fragment_to_mto_dptos_fragment, bundle);
    }

    @Override
    public void onEliminarBusDptosFrag(@NonNull Departamento dpto) {
        if (dpto.getId().equals("0")) return;  // no se puede borrar el admin!!
        // Guardamos el dpto a eliminar en el ViewModel
        mDptosVM.setDptoAEliminar(dpto);
        // Lanzamos DlgConfirmacion
        Bundle bundle = new Bundle();
        bundle.putInt("titulo", R.string.app_name);
        bundle.putInt("mensaje", R.string.msg_DlgConfirmacion_Eliminar);
        mNavC.navigate(R.id.action_global_dlgConfirmacionDptos, bundle);
    }

    @Override
    public void onCancelarMtoDptosFrag() {
        // Cerramos MtoDptosFragment
        mNavC.navigateUp();
    }

    @Override
    public void onAceptarMtoDptosFrag(int op, Departamento dpto) {
        if (dpto == null) {   // Faltan Datos Obligatorios
            Snackbar.make(binding.getRoot(), R.string.msg_FaltanDatosObligatorios, Snackbar.LENGTH_SHORT).show();
            return;
        }
        switch (op) {
            case MtoDptosFragment.OP_CREAR:
                mDptosVM.altaDepartamento(dpto).observe(this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean ok) {
                        Snackbar.make(binding.getRoot(), (ok) ? R.string.msg_AltaDepartamentoOK : R.string.msg_AltaDepartamentoKO, Snackbar.LENGTH_SHORT).show();
                    }
                });
                break;
            case MtoDptosFragment.OP_EDITAR:
                mDptosVM.editarDepartamento(dpto).observe(this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean ok) {
                        Snackbar.make(binding.getRoot(), (ok) ? R.string.msg_EditarDepartamentoOK : R.string.msg_EditarDepartamentoKO, Snackbar.LENGTH_SHORT).show();
                    }
                });
                break;
            case MtoDptosFragment.OP_ELIMINAR:
                break;
        }

        // Cerramos MtoDptosFragment
        mNavC.navigateUp();
    }

    @Override
    public void onDlgConfirmacionPositiveClick(DialogFragment dialog) {
        // Recuperamos el dpto a eliminar del ViewModel
        Departamento dptoAEliminar = mDptosVM.getDptoAEliminar();
        mDptosVM.bajaDepartamento(dptoAEliminar).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean ok) {
                Snackbar.make(binding.getRoot(), (ok) ? R.string.msg_BajaDepartamentoOK : R.string.msg_BajaDepartamentoKO, Snackbar.LENGTH_SHORT).show();
            }
        });
        // Eliminamos el dpto a eliminar del ViewModel
        mDptosVM.setDptoAEliminar(null);
    }

    @Override
    public void onDlgConfirmacionNegativeClick(DialogFragment dialog) {
        // Eliminamos el dpto a eliminar del ViewModel
        mDptosVM.setDptoAEliminar(null);
    }

}
