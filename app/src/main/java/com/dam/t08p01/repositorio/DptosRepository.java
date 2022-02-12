package com.dam.t08p01.repositorio;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dam.t08p01.modelo.AppDatabase;
import com.dam.t08p01.modelo.Aula;
import com.dam.t08p01.modelo.Departamento;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class DptosRepository {

    /* Repository Dptos ***************************************************************************/

    private final AppDatabase mAppDB;

    public DptosRepository(Application application) {
        mAppDB = AppDatabase.getAppDatabase(application);
    }

    /* Clases Lógica Dptos ************************************************************************/

    private class FirebaseLiveDataSE extends LiveData<List<Departamento>> {
        @Override
        protected void onActive() {
            super.onActive();
            mAppDB.getRefFS().collection("dptos")
                    .orderBy("id")
                    .get()
                    .addOnCompleteListener(dptosSE_OnCompleteListener);
        }

        @Override
        protected void onInactive() {
            super.onInactive();
        }

        private final OnCompleteListener<QuerySnapshot> dptosSE_OnCompleteListener = new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Departamento> dptos = new ArrayList<>();
                    for (QueryDocumentSnapshot qds : task.getResult()) {
                        dptos.add(qds.toObject(Departamento.class));
                    }
                    setValue(dptos);
                }
            }
        };
    }

    private class FirebaseLiveDataME extends LiveData<List<Departamento>> {
        private ListenerRegistration reg;

        @Override
        protected void onActive() {
            super.onActive();
            reg = mAppDB.getRefFS().collection("dptos")
                    .orderBy("id")
                    .addSnapshotListener(dptosME_EventListener);
        }

        @Override
        protected void onInactive() {
            super.onInactive();
            reg.remove();
        }

        private final EventListener<QuerySnapshot> dptosME_EventListener = new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }
                List<Departamento> dptos = new ArrayList<>();
                for (QueryDocumentSnapshot qds : value) {
                    dptos.add(qds.toObject(Departamento.class));
                }
                setValue(dptos);
            }
        };
    }

    /* Métodos Lógica Dptos ***********************************************************************/

    public LiveData<List<Departamento>> recuperarDepartamentosSE() {
        return new FirebaseLiveDataSE();
    }

    public LiveData<List<Departamento>> recuperarDepartamentosME() {
        return new FirebaseLiveDataME();
    }

    public LiveData<Boolean> altaDepartamento(@NonNull Departamento dpto) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        WriteBatch batch = mAppDB.getRefFS().getFirestore().batch();

        DocumentReference ref1 = mAppDB.getRefFS().collection("dptos").document(dpto.getId());
        batch.set(ref1, dpto);

        Aula aula = new Aula();
        aula.setIdDpto(dpto.getId());
        aula.setId("0");
        aula.setNombre("Sin Asignar");
        DocumentReference ref2 = mAppDB.getRefFS().collection("aulas").document(aula.getIdDpto() + aula.getId());
        batch.set(ref2, aula);

        batch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
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

    public LiveData<Boolean> editarDepartamento(@NonNull Departamento dpto) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        mAppDB.getRefFS().collection("dptos").document(dpto.getId()).set(dpto, SetOptions.merge())
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

    public LiveData<Boolean> bajaDepartamento(@NonNull Departamento dpto) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        mAppDB.getRefFS().collection("dptos").document(dpto.getId()).delete()
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

        // OnDeleteCascade de Aulas!!
        mAppDB.getRefFS().collection("aulas")
                .whereEqualTo("idDpto", dpto.getId())
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

        // OnDeleteCascade de Productos!!
        mAppDB.getRefFS().collection("productos")
                .whereEqualTo("idDpto", dpto.getId())
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
