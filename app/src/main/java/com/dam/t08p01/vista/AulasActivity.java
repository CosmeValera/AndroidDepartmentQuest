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
import com.dam.t08p01.databinding.ActivityAulasBinding;
import com.dam.t08p01.databinding.ContentAulasBinding;
import com.dam.t08p01.modelo.Aula;
import com.dam.t08p01.modelo.Departamento;
import com.dam.t08p01.vista.dialogos.DlgConfirmacion;
import com.dam.t08p01.vista.fragmentos.BusAulasFragment;
import com.dam.t08p01.vista.fragmentos.MtoAulasFragment;
import com.dam.t08p01.vistamodelo.AulasViewModel;
import com.google.android.material.snackbar.Snackbar;

public class AulasActivity extends AppCompatActivity
        implements DlgConfirmacion.DlgConfirmacionListener,
        BusAulasFragment.BusAulasFragInterface,
        MtoAulasFragment.MtoAulasFragInterface {

    private ActivityAulasBinding binding;
    private ContentAulasBinding bindingC;
    private NavController mNavC;
//    private AppBarConfiguration mAppBarConfiguration;

    private AulasViewModel mAulasVM;
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
                mAulasVM = new ViewModelProvider(this).get(AulasViewModel.class);
                mAulasVM.setLogin(mLogin);      // Guardamos el login en el ViewModel
                // Config. filtro aulas
                if (mLogin.getId().equals("0")) {    // admin
                    mAulasVM.getFiltroAulas().setIdDpto("");
                } else {
                    mAulasVM.getFiltroAulas().setIdDpto(mLogin.getId());
                }
            }
        }
        if (mLogin == null) {   // Esto no debería pasar nunca!!
            finish();
            return;
        }

        // Inflate Layout (esto está aquí para que lance el fragmento buscador después de las inicializaciones anteriores)!!
        binding = ActivityAulasBinding.inflate(getLayoutInflater());
        bindingC = binding.contentAulas;
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Inits Navigation
        NavHostFragment navHost = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_aulas);
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
    public void onCrearBusAulasFrag() {
        // Lanzamos MtoAulasFragment
        Bundle bundle = new Bundle();
        bundle.putInt("op", MtoAulasFragment.OP_CREAR);
        mNavC.navigate(R.id.action_bus_aulas_fragment_to_mto_aulas_fragment, bundle);
    }

    @Override
    public void onEditarBusAulasFrag(Aula aula) {
        // Lanzamos MtoAulasFragment
        Bundle bundle = new Bundle();
        bundle.putInt("op", MtoAulasFragment.OP_EDITAR);
        bundle.putParcelable("aula", aula);
        mNavC.navigate(R.id.action_bus_aulas_fragment_to_mto_aulas_fragment, bundle);
    }

    @Override
    public void onEliminarBusAulasFrag(@NonNull Aula aula) {
        if (aula.getId().equals("0")) return;  // no se puede borrar el aula "sin asignar"!!
        // Guardamos el aula a eliminar en el ViewModel
        mAulasVM.setAulaAEliminar(aula);
        // Lanzamos DlgConfirmacion
        Bundle bundle = new Bundle();
        bundle.putInt("titulo", R.string.app_name);
        bundle.putInt("mensaje", R.string.msg_DlgConfirmacion_Eliminar);
        mNavC.navigate(R.id.action_global_dlgConfirmacionAulas, bundle);
    }

    @Override
    public void onCancelarMtoAulasFrag() {
        // Cerramos MtoAulasFragment
        mNavC.navigateUp();
    }

    @Override
    public void onAceptarMtoAulasFrag(int op, Aula aula) {
        if (aula == null) {   // Faltan Datos Obligatorios
            Snackbar.make(binding.getRoot(), R.string.msg_FaltanDatosObligatorios, Snackbar.LENGTH_SHORT).show();
            return;
        }
        switch (op) {
            case MtoAulasFragment.OP_CREAR:
                mAulasVM.altaAula(aula).observe(this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean ok) {
                        Snackbar.make(binding.getRoot(), (ok) ? R.string.msg_AltaAulaOK : R.string.msg_AltaAulaKO, Snackbar.LENGTH_SHORT).show();
                    }
                });
                break;
            case MtoAulasFragment.OP_EDITAR:
                mAulasVM.editarAula(aula).observe(this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean ok) {
                        Snackbar.make(binding.getRoot(), (ok) ? R.string.msg_EditarAulaOK : R.string.msg_EditarAulaKO, Snackbar.LENGTH_SHORT).show();
                    }
                });
                break;
            case MtoAulasFragment.OP_ELIMINAR:
                break;
        }

        // Cerramos MtoAulasFragment
        mNavC.navigateUp();
    }

    @Override
    public void onDlgConfirmacionPositiveClick(DialogFragment dialog) {
        // Recuperamos el aula a eliminar del ViewModel
        Aula aulaAEliminar = mAulasVM.getAulaAEliminar();
        mAulasVM.bajaAula(aulaAEliminar).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean ok) {
                Snackbar.make(binding.getRoot(), (ok) ? R.string.msg_BajaAulaOK : R.string.msg_BajaAulaKO, Snackbar.LENGTH_SHORT).show();
            }
        });
        // Eliminamos el aula a eliminar del ViewModel
        mAulasVM.setAulaAEliminar(null);
    }

    @Override
    public void onDlgConfirmacionNegativeClick(DialogFragment dialog) {
        // Eliminamos el aula a eliminar del ViewModel
        mAulasVM.setAulaAEliminar(null);
    }

}
