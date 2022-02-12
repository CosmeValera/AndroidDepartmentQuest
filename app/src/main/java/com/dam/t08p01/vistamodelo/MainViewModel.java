package com.dam.t08p01.vistamodelo;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.dam.t08p01.modelo.AppDatabase;
import com.dam.t08p01.modelo.Departamento;
import com.dam.t08p01.repositorio.MainRepository;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private final MainRepository mMainRep;
    private Departamento mLogin;

    /* ViewModel Main *****************************************************************************/

    public MainViewModel(@NonNull Application application) {
        super(application);
        mMainRep = new MainRepository(application);
        mLogin = null;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Cerramos AppDatabase
        if (!AppDatabase.cerrarAppDatabase()) {
            ;
        }
    }

    /* Getters & Setters Objetos Persistentes *****************************************************/

    public Departamento getLogin() {
        return mLogin;
    }

    public void setLogin(Departamento login) {
        mLogin = login;
    }

    public List<String> getRegistro() {
        return mMainRep.recuperarRegistro();
    }

    public boolean borrarRegistro() {
        return mMainRep.borrarRegistro();
    }

}
