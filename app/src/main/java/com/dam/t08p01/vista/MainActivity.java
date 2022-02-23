package com.dam.t08p01.vista;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.dam.t08p01.R;
import com.dam.t08p01.databinding.ActivityMainBinding;
import com.dam.t08p01.databinding.ContentMainBinding;
import com.dam.t08p01.modelo.Departamento;
import com.dam.t08p01.vista.dialogos.DlgConfirmacion;
import com.dam.t08p01.vista.fragmentos.LoginFragment;
import com.dam.t08p01.vista.fragmentos.RegistroFragment;
import com.dam.t08p01.vistamodelo.MainViewModel;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity
        implements DlgConfirmacion.DlgConfirmacionListener,
        LoginFragment.LoginFragInterface,
        RegistroFragment.RegistroFragInterface {

    private ActivityMainBinding binding;
    private ContentMainBinding bindingC;
    private NavController mNavC;
    private AppBarConfiguration mAppBarConfiguration;

    private MainViewModel mMainVM;
    private Departamento mLogin;

    private static final String TAG_CONFIRMACION_SALIR = "tagConfirmacion_Salir";
    private static final String TAG_CONFIRMACION_BORRARREGISTRO = "tagConfirmacion_BorrarRegistro";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_T08p01_NoActionBar);    // Splash
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        bindingC = binding.appBarMain.contentMain;
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);

        // Inits Navigation
        NavHostFragment navHost = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        mNavC = navHost.getNavController();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    mNavC.getGraph())
                    .build();
            NavigationUI.setupActionBarWithNavController(this, mNavC, mAppBarConfiguration);
            //NavigationUI.setupWithNavController(binding.appBarMain.bottomNav, mNavController);
            // Me interesa controlarlo "a mano" ya que mis "top destinations" no son todos fragmentos!!
            binding.appBarMain.bottomNav.setOnItemSelectedListener(bottomNav_OnItemSelectedListener);
            binding.appBarMain.bottomNav.setVisibility(View.VISIBLE);
        } else {
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    mNavC.getGraph())
                    .setOpenableLayout(binding.drawerLayout)
                    .build();
            NavigationUI.setupActionBarWithNavController(this, mNavC, mAppBarConfiguration);
            //NavigationUI.setupWithNavController(binding.navView, mNavController);
            // Me interesa controlarlo "a mano" ya que mis "top destinations" no son todos fragmentos!!
            binding.navView.setNavigationItemSelectedListener(navView_OnNavigationItemSelected);
            binding.appBarMain.bottomNav.setVisibility(View.INVISIBLE);
        }

        // Inits
        mMainVM = new ViewModelProvider(this).get(MainViewModel.class);
        mLogin = mMainVM.getLogin(); // Recuperamos el login del ViewModel!!

        if (mLogin == null && savedInstanceState == null) {
            // Esta condición de savedInstanceState==null evita que ante un "giro" se ejecute estas inicializaciones de nuevo.

            // Establecemos preferencias
            // Para que cree los valores por defecto correctamente la primera vez al instalar la App,
            // NO debe existir la carpeta /data/data/com.dam.t08p01 !!
            PreferenceManager.setDefaultValues(this, R.xml.prefs, false);
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

            // Splash
            boolean mostrarSplash = pref.getBoolean(getResources().getString(R.string.splashApp_key), false);
            if (mostrarSplash) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            // Lanzamos el LoginFragment
            boolean mostrarLogin = pref.getBoolean(getString(R.string.login_key), true);
            if (mostrarLogin) {
                mNavC.navigate(R.id.action_nav_main_to_nav_login);
            }
        }

        // Listeners

//        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.appBarMain.bottomNav.getMenu().findItem(R.id.nav_inicio).setVisible(true);
            binding.appBarMain.bottomNav.getMenu().findItem(R.id.nav_inicio).setChecked(true);
            binding.appBarMain.bottomNav.getMenu().findItem(R.id.nav_inicio).setVisible(false);
        } else {
            binding.navView.getMenu().findItem(R.id.nav_inicio).setVisible(true);
            binding.navView.getMenu().findItem(R.id.nav_inicio).setChecked(true);
            binding.navView.getMenu().findItem(R.id.nav_inicio).setVisible(false);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.appBarMain.bottomNav.setVisibility(View.VISIBLE);
        }
        return NavigationUI.navigateUp(mNavC, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuSalir) {
            mostrarDlgSalir();
            return true;
        } else if (id == R.id.menuLogin) {
            // Lanzamos LoginFragment
            if (mNavC.getCurrentDestination() != null && mNavC.getCurrentDestination().getId() == R.id.nav_main) {
                mNavC.navigate(R.id.action_nav_main_to_nav_login);
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    binding.appBarMain.bottomNav.setVisibility(View.INVISIBLE);
                }
            }
        } else if (id == R.id.menuRegistro) {
            // Lanzamos RegistroFragment
            if (mNavC.getCurrentDestination() != null && mNavC.getCurrentDestination().getId() == R.id.nav_main) {
                mNavC.navigate(R.id.action_nav_main_to_nav_registro, null);
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    binding.appBarMain.bottomNav.setVisibility(View.INVISIBLE);
                }
            }
        } else if (id == R.id.menuPrefs) {
            // Lanzamos PrefsFragment
            if (mNavC.getCurrentDestination() != null && mNavC.getCurrentDestination().getId() == R.id.nav_main) {
                mNavC.navigate(R.id.action_nav_main_to_nav_prefs);
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    binding.appBarMain.bottomNav.setVisibility(View.INVISIBLE);
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void mostrarDlgSalir() {
        // Lanzamos DlgConfirmacion
        Bundle bundle = new Bundle();
        bundle.putInt("titulo", R.string.app_name);
        bundle.putInt("mensaje", R.string.msg_DlgConfirmacion_Salir);
        bundle.putString("tag", TAG_CONFIRMACION_SALIR);
        mNavC.navigate(R.id.action_global_dlgConfirmacionMain, bundle);
    }

    @Override
    public void onDlgConfirmacionPositiveClick(@NonNull DialogFragment dialog) {
        if (dialog.getArguments() != null) {
            String tag = dialog.getArguments().getString("tag");
            switch (tag) {
                case TAG_CONFIRMACION_SALIR:
                    finish();
                    break;
                case TAG_CONFIRMACION_BORRARREGISTRO:
                    // Borramos Registro
                    if (mMainVM.borrarRegistro()) {
                        Snackbar.make(binding.getRoot(), R.string.msg_BorrarRegistroOK, Snackbar.LENGTH_SHORT).show();
                        // Cerramos DlgConfirmacion!!
                        mNavC.navigateUp();
                    } else {
                        Snackbar.make(binding.getRoot(), R.string.msg_BorrarRegistroKO, Snackbar.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }

    @Override
    public void onDlgConfirmacionNegativeClick(@NonNull DialogFragment dialog) {
        if (dialog.getArguments() != null) {
            String tag = dialog.getArguments().getString("tag");
            switch (tag) {
                case TAG_CONFIRMACION_SALIR:
                    ;
                    break;
                case TAG_CONFIRMACION_BORRARREGISTRO:
                    ;
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (mNavC.getCurrentDestination() != null && mNavC.getCurrentDestination().getId() == R.id.nav_main) {
                mostrarDlgSalir();
            } else {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    binding.appBarMain.bottomNav.setVisibility(View.VISIBLE);
                }
                super.onBackPressed();
            }
        }
    }

    private final NavigationBarView.OnItemSelectedListener bottomNav_OnItemSelectedListener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            return navegacionItemSeleccionado(item);
        }
    };

    private final NavigationView.OnNavigationItemSelectedListener navView_OnNavigationItemSelected = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            return navegacionItemSeleccionado(item);
        }
    };

    private boolean navegacionItemSeleccionado(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_dptos) {
            if (mLogin != null) {
                if (mLogin.getId().equals("0")) {  //admin
                    Intent i = new Intent(MainActivity.this, DptosActivity.class);
                    i.putExtra("login", mLogin);
                    startActivity(i);
                } else {
                    Snackbar.make(binding.getRoot(), R.string.msg_LoginPermisos, Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Snackbar.make(binding.getRoot(), R.string.msg_NoLogin, Snackbar.LENGTH_SHORT).show();
            }
        } else if (item.getItemId() == R.id.nav_aulas) {
            if (mLogin != null) {
                Intent i = new Intent(MainActivity.this, AulasActivity.class);
                i.putExtra("login", mLogin);
                startActivity(i);
            } else {
                Snackbar.make(binding.getRoot(), R.string.msg_NoLogin, Snackbar.LENGTH_SHORT).show();
            }
        } else if (item.getItemId() == R.id.nav_productos) {
            if (mLogin != null) {
                Intent i = new Intent(MainActivity.this, ProductosActivity.class);
                i.putExtra("login", mLogin);
                startActivity(i);
            } else {
                Snackbar.make(binding.getRoot(), R.string.msg_NoLogin, Snackbar.LENGTH_SHORT).show();
            }
        } else {
            return false;
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onCancelarLoginFrag() {
        // Cerramos LoginFragment
        mNavC.navigateUp();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.appBarMain.bottomNav.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAceptarLoginFrag(Departamento dpto) {
        if (dpto == null) { // Error clave
            Snackbar.make(binding.getRoot(), R.string.msg_LoginKO, Snackbar.LENGTH_SHORT).show();
            return;
        }
        mLogin = dpto;
        mMainVM.setLogin(mLogin);   // Guardamos el login en el ViewModel
        Snackbar.make(binding.getRoot(), R.string.msg_LoginOK, Snackbar.LENGTH_SHORT).show();
        // Cerramos LoginFragment
        mNavC.navigateUp();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.appBarMain.bottomNav.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBorrarRegistroFrag() {
        // Lanzamos DlgConfirmacion
        Bundle bundle = new Bundle();
        bundle.putInt("titulo", R.string.app_name);
        bundle.putInt("mensaje", R.string.msg_DlgConfirmacion_BorrarRegistro);
        bundle.putString("tag", TAG_CONFIRMACION_BORRARREGISTRO);
        mNavC.navigate(R.id.action_global_dlgConfirmacionMain, bundle);
    }

}
