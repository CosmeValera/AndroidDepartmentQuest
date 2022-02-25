package com.dam.t08p01.repositorio;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

import com.dam.t08p01.R;
import com.dam.t08p01.modelo.AppDatabase;
import com.dam.t08p01.modelo.Aula;
import com.dam.t08p01.modelo.FiltroProductos;
import com.dam.t08p01.modelo.Producto;

import com.dam.t08p01.modelo.Registro;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;


import java.util.ArrayList;
import java.util.List;

public class ProductosRepository {
    private final AppDatabase mAppDB;
    private final Context mContext;
    private final boolean mRegistro;

    public ProductosRepository(Application application) {
        mContext = application.getApplicationContext();
        mRegistro = PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(mContext.getResources().getString(R.string.registro_key), false);
        mAppDB = AppDatabase.getAppDatabase(application);
    }

    // Wrappers de LiveData<List<Productos>>
    private class FireBaseLiveDataSE extends LiveData<List<Producto>> {
        private final FiltroProductos mFiltroProductos;

        public FireBaseLiveDataSE(FiltroProductos filtroProductos) {
            mFiltroProductos = filtroProductos;
        }

        @Override
        protected void onActive() {
            super.onActive();
            Query query = mAppDB.getRefFS().collection("productos");
            if (mFiltroProductos.getIdAula().equals("")) {   // todas las aulas
                query = query
                        .whereGreaterThanOrEqualTo("fecAlta", mFiltroProductos.getFecAlta())
                        .orderBy("fecAlta").orderBy("idAula").orderBy("id");
            } else {
                query = query
                        .whereGreaterThanOrEqualTo("fecAlta", mFiltroProductos.getFecAlta())
                        .whereEqualTo("idAula", mFiltroProductos.getIdAula())
                        .orderBy("fecAlta").orderBy("idAula").orderBy("id");
            }
            query.get().addOnCompleteListener(productosSE_OnCompleteListener);
        }

        @Override
        protected void onInactive() {
            super.onInactive();
        }

        private final OnCompleteListener<QuerySnapshot> productosSE_OnCompleteListener = new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Producto> productos = new ArrayList<>();
                    for (QueryDocumentSnapshot qds : task.getResult()) {
                        productos.add(qds.toObject(Producto.class));
                    }
                    setValue(productos);
                }
            }
        };
    }

    private class FirebaseLiveDataME extends LiveData<List<Producto>> {
        //        Aqui implementamos nuestro propoio livedata, onActive sirve para lo que hace cuando
        private ListenerRegistration reg;
        private final FiltroProductos mFiltroProductos;

        public FirebaseLiveDataME(FiltroProductos filtroProductos) {
            mFiltroProductos = filtroProductos;
        }

        @Override
        protected void onActive() {
//            TODO filtrar tambien que tenga el mismo departamento, para que a mates solo le salgan sus productos
//            Hay que añadir un campo idDpto en filtroProducto
            super.onActive();
            Query query = mAppDB.getRefFS().collection("productos");

            if (mFiltroProductos.getIdDpto().equals("")) { //Todos los departamentos
                if (mFiltroProductos.getIdAula().equals("")) {   // todas las aulas
                    query = query
                            .whereGreaterThanOrEqualTo("fecAlta", mFiltroProductos.getFecAlta())
                            .orderBy("fecAlta").orderBy("idDpto").orderBy("idAula").orderBy("id");
                } else {
                    query = query
                            .whereGreaterThanOrEqualTo("fecAlta", mFiltroProductos.getFecAlta())
                            .whereEqualTo("idAula", mFiltroProductos.getIdAula())
                            .orderBy("fecAlta").orderBy("idDpto").orderBy("id");
                }
            } else { //Un departamento
                if (mFiltroProductos.getIdAula().equals("")) {   // todas las aulas
                    query = query
                            .whereGreaterThanOrEqualTo("fecAlta", mFiltroProductos.getFecAlta())
                            .whereEqualTo("idDpto", mFiltroProductos.getIdDpto())
                            .orderBy("fecAlta").orderBy("idAula").orderBy("id");
                } else {
                    query = query
                            .whereGreaterThanOrEqualTo("fecAlta", mFiltroProductos.getFecAlta())
                            .whereEqualTo("idAula", mFiltroProductos.getIdAula())
                            .whereEqualTo("idDpto", mFiltroProductos.getIdDpto())
                            .orderBy("fecAlta").orderBy("id");
                }
            }

            //En este callback es donde se devuelven los datos
            reg = query.addSnapshotListener(productosME_EventListener);
        }

        @Override
        protected void onInactive() {
            super.onInactive();
            reg.remove();
        }

        private final EventListener<QuerySnapshot> productosME_EventListener = new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }
                List<Producto> productos = new ArrayList<>();
                for (QueryDocumentSnapshot qds : value) {
                    productos.add(qds.toObject(Producto.class));
                }
                setValue(productos);
            }
        };
    }

    //Metodos logica productos
    public LiveData<List<Producto>> recuperarProductosSE(FiltroProductos filtroProductos) {
        return new ProductosRepository.FireBaseLiveDataSE(filtroProductos);
    }

    public LiveData<List<Producto>> recuperarProductosME(FiltroProductos filtroProductos) {
        return new ProductosRepository.FirebaseLiveDataME(filtroProductos);
    }

    public LiveData<Boolean> altaProducto(@NonNull Producto producto) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        mAppDB.getRefFS().collection("productos").document(producto.getId()).set(producto)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        result.postValue(true);
                        if (mRegistro) {
                            try {
                                Registro.guardarRegistro(mContext, producto, "alta", true);
                            } catch (Exception e) {
                                //:)
                            }
                        }
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

    public LiveData<Boolean> editarProducto(@NonNull Producto producto) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        mAppDB.getRefFS().collection("productos").document(producto.getId()).set(producto, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        result.postValue(true);
                        if (mRegistro) {
                            try {
                                Registro.guardarRegistro(mContext, producto, "alta", true);
                            } catch (Exception e) {
                                //:)
                            }
                        }
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

    public LiveData<Boolean> bajaProducto(@NonNull Producto producto) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        mAppDB.getRefFS().collection("productos").document(producto.getId()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        result.postValue(true);
                        if (mRegistro) {
                            try {
                                Registro.guardarRegistro(mContext, producto, "alta", true);
                            } catch (Exception e) {
                                //:)
                            }
                        }
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
}







