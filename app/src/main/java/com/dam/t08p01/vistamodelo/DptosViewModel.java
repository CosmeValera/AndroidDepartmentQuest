package com.dam.t08p01.vistamodelo;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.dam.t08p01.modelo.Departamento;
import com.dam.t08p01.repositorio.DptosRepository;

import java.util.List;

public class DptosViewModel extends AndroidViewModel {

    /* ViewModel Dptos ****************************************************************************/

    private final DptosRepository mDptosRep;
    private LiveData<List<Departamento>> mDptos;

    private Departamento mLogin;
    private Departamento mDptoAEliminar;

    public DptosViewModel(@NonNull Application application) {
        super(application);
        mDptosRep = new DptosRepository(application);
        mDptos = null;
//        mDptos = mDptosRep.recuperarDepartamentos();
        mLogin = null;
        mDptoAEliminar = null;
    }

    /* Métodos Mantenimiento Departamentos ********************************************************/

    public LiveData<List<Departamento>> getDptos() {      // Multiple Events
        mDptos = mDptosRep.recuperarDepartamentosME();
        return mDptos;
    }

    public LiveData<List<Departamento>> getDptosSE() {      // Single Event
        mDptos = mDptosRep.recuperarDepartamentosSE();
        return mDptos;
    }

    public LiveData<Boolean> altaDepartamento(Departamento dpto) {
        return mDptosRep.altaDepartamento(dpto);
    }

    public LiveData<Boolean> editarDepartamento(Departamento dpto) {
        return mDptosRep.editarDepartamento(dpto);
    }

    public LiveData<Boolean> bajaDepartamento(Departamento dpto) {
        return mDptosRep.bajaDepartamento(dpto);
    }

    /* Getters & Setters Objetos Persistentes *****************************************************/

    public Departamento getLogin() {
        return mLogin;
    }

    public void setLogin(Departamento login) {
        mLogin = login;
    }

    public Departamento getDptoAEliminar() {
        return mDptoAEliminar;
    }

    public void setDptoAEliminar(Departamento dptoAEliminar) {
        mDptoAEliminar = dptoAEliminar;
    }

}
