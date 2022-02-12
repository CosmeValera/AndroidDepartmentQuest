package com.dam.t08p01.vista.fragmentos;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.dam.t08p01.R;
import com.dam.t08p01.modelo.Departamento;
import com.dam.t08p01.vistamodelo.MainViewModel;

public class PrefsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs, rootKey);
        // Inits
        MainViewModel mainVM = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        Departamento login = mainVM.getLogin(); // Recuperamos el login del ViewModel
        if (login != null && login.getId().equals("0")) { // admin
            Preference pref;
            pref = findPreference(getString(R.string.Firebase_name_key));
            if (pref != null) pref.setVisible(true);
        }
        // Oculta el teclado después de editar!!
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getResources().getString(R.string.Firebase_name_key))) {
            Toast.makeText(requireActivity(), R.string.msg_CambioBD, Toast.LENGTH_LONG).show();
        }
    }

}
