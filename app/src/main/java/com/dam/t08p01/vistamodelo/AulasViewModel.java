package com.dam.t08p01.vistamodelo;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.dam.t08p01.modelo.Aula;
import com.dam.t08p01.modelo.Departamento;
import com.dam.t08p01.modelo.FiltroAulas;
import com.dam.t08p01.repositorio.AulasRepository;

import java.util.List;

public class AulasViewModel extends AndroidViewModel {

    /* ViewModel Aulas ****************************************************************************/

    private final AulasRepository mAulasRep;
    private LiveData<List<Aula>> mAulas;

    private Departamento mLogin;
    private Aula mAulaAEliminar;

    private FiltroAulas mFiltroAulas;

    public AulasViewModel(@NonNull Application application) {
        super(application);
        mAulasRep = new AulasRepository(application);
        mAulas = null;
//        mAulas = mAulasRep.recuperarAulas();
        mLogin = null;
        mAulaAEliminar = null;
        mFiltroAulas = new FiltroAulas();
    }

    /* Métodos Mantenimiento Aulas ****************************************************************/

    public LiveData<List<Aula>> getAulas() {      // Multiple Events
        mAulas = mAulasRep.recuperarAulasME(mFiltroAulas);
        return mAulas;
    }

    public LiveData<List<Aula>> getAulasSE() {      // Single Event
        mAulas = mAulasRep.recuperarAulasSE(mFiltroAulas);
        return mAulas;
    }

    public LiveData<Boolean> altaAula(Aula aula) {
        return mAulasRep.altaAula(aula);
    }

    public LiveData<Boolean> editarAula(Aula aula) {
        return mAulasRep.editarAula(aula);
    }

    public LiveData<Boolean> bajaAula(Aula aula) {
        return mAulasRep.bajaAula(aula);
    }

    /* Getters & Setters Objetos Persistentes *****************************************************/

    public Departamento getLogin() {
        return mLogin;
    }

    public void setLogin(Departamento login) {
        mLogin = login;
    }

    public Aula getAulaAEliminar() {
        return mAulaAEliminar;
    }

    public void setAulaAEliminar(Aula aulaAEliminar) {
        mAulaAEliminar = aulaAEliminar;
    }

    public FiltroAulas getFiltroAulas() {
        return mFiltroAulas;
    }

    public void setFiltroAulas(FiltroAulas filtroAulas) {
        mFiltroAulas = filtroAulas;
    }

}
