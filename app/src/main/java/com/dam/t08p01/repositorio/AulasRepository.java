package com.dam.t08p01.repositorio;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dam.t08p01.modelo.AppDatabase;
import com.dam.t08p01.modelo.Aula;
import com.dam.t08p01.modelo.FiltroAulas;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.List;

public class AulasRepository {

    /* Repository Aulas ***************************************************************************/

    private final AppDatabase mAppDB;

    public AulasRepository(Application application) {
        mAppDB = AppDatabase.getAppDatabase(application);
    }

    /* Clases Lógica Aulas ************************************************************************/

    private class FirebaseLiveDataSE extends LiveData<List<Aula>> {
        private final FiltroAulas mFiltroAulas;

        public FirebaseLiveDataSE(FiltroAulas filtroAulas) {
            mFiltroAulas = filtroAulas;
        }

        @Override
        protected void onActive() {
            super.onActive();
            Query query = mAppDB.getRefFS().collection("aulas");
            if (mFiltroAulas.getIdDpto().equals("")) {   // todos los dptos
                query = query.orderBy("idDpto").orderBy("id");
            } else {
                query = query.orderBy("id")
                        .whereEqualTo("idDpto", mFiltroAulas.getIdDpto());
            }
            query.get().addOnCompleteListener(aulasSE_OnCompleteListener);
        }

        @Override
        protected void onInactive() {
            super.onInactive();
        }

        private final OnCompleteListener<QuerySnapshot> aulasSE_OnCompleteListener = new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Aula> aulas = new ArrayList<>();
                    for (QueryDocumentSnapshot qds : task.getResult()) {
                        aulas.add(qds.toObject(Aula.class));
                    }
                    setValue(aulas);
                }
            }
        };
    }

    private class FirebaseLiveDataME extends LiveData<List<Aula>> {
//        Aqui implementamos nuestro propoio livedata, onActive sirve para lo que hace cuando
        private ListenerRegistration reg;
        private final FiltroAulas mFiltroAulas;

        public FirebaseLiveDataME(FiltroAulas filtroAulas) {
            mFiltroAulas = filtroAulas;
        }

        @Override
        protected void onActive() {
            super.onActive();
            Query query = mAppDB.getRefFS().collection("aulas");
            if (mFiltroAulas.getIdDpto().equals("")) {   // todos los dptos
                query = query.orderBy("idDpto").orderBy("id");
            } else {
                query = query.orderBy("id")
                        .whereEqualTo("idDpto", mFiltroAulas.getIdDpto());
            }
            //En este callback es donde se devuelven los datos
            reg = query.addSnapshotListener(aulasME_EventListener);
        }

        @Override
        protected void onInactive() {
            super.onInactive();
            reg.remove();
        }

        private final EventListener<QuerySnapshot> aulasME_EventListener = new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }
                List<Aula> aulas = new ArrayList<>();
                for (QueryDocumentSnapshot qds : value) {
                    aulas.add(qds.toObject(Aula.class));
                }
                setValue(aulas);
            }
        };
    }

    /* Métodos Lógica Aulas ***********************************************************************/

    public LiveData<List<Aula>> recuperarAulasSE(FiltroAulas filtroAulas) {
//        Con esta sola linea ya se hace pero necesita el livedata interno que ya esta hecho
        return new AulasRepository.FirebaseLiveDataSE(filtroAulas);

////        Como se haría en unprincio, con esto no hace falta el livedata interno. Con lo de arriba sí, pero ya esta hecho
////        addOnCompleteListener lanza un procesos asincrono que tambien esta a la escuah(como un observer pero para Firebase)
//        MutableLiveData<List<Aula>> result = new MutableLiveData<>();
//        mAppDB.getRefFS().collection("aulas").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                    List<Aula> aulas = new ArrayList<>();
//                    for (QueryDocumentSnapshot qds : task.getResult()) {
//                        aulas.add(qds.toObject(Aula.class));
//                    }
//                    result.postValue(aulas);
//            }
//        });
//        return result;
    }

    public LiveData<List<Aula>> recuperarAulasME(FiltroAulas filtroAulas) {
//        Con esta sola linea ya se hace pero necesita el livedata interno que ya esta hecho
        return new AulasRepository.FirebaseLiveDataME(filtroAulas);

//        MutableLiveData<List<Aula>> result = new MutableLiveData<>();
//        mAppDB.getRefFS().collection("aulas").addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                List<Aula> aulas = new ArrayList<>();
//                for (DocumentSnapshot qds : value.getDocuments()) {
//                    aulas.add(qds.toObject(Aula.class));
//                }
//                result.postValue(aulas);
//            }
//        });
//        return result;
    }

    public LiveData<Boolean> altaAula(@NonNull Aula aula) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        mAppDB.getRefFS().collection("aulas").document(aula.getIdDpto() + aula.getId()).set(aula)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        result.postValue(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        result.postValue(false);
                    }
                });
        return result;
    }

    public LiveData<Boolean> editarAula(@NonNull Aula aula) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        mAppDB.getRefFS().collection("aulas").document(aula.getIdDpto() + aula.getId()).set(aula, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        result.postValue(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        result.postValue(false);
                    }
                });
        return result;
    }

    public LiveData<Boolean> bajaAula(@NonNull Aula aula) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        mAppDB.getRefFS().collection("aulas").document(aula.getIdDpto() + aula.getId()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        result.postValue(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        result.postValue(false);
                    }
                });

        // OnDeleteCascade de Productos!!
        mAppDB.getRefFS().collection("productos")
                .whereEqualTo("idDpto", aula.getIdDpto())
                .whereEqualTo("idAula", aula.getId())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot qds : task.getResult()) {
                        qds.getReference().delete();
                    }
                }
            }
        });

        return result;
    }

}
