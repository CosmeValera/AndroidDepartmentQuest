package com.dam.t08p01.repositorio;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

import com.dam.t08p01.R;
import com.dam.t08p01.modelo.AppDatabase;
import com.dam.t08p01.modelo.Producto;
import com.dam.t08p01.modelo.Registro;

import java.util.List;

public class ProductosRepository {
    private final AppDatabase mAppDB;
    private final Context mContext;
    private final SharedPreferences pref;
    private final boolean mRegistro;

    public ProductosRepository(Application application) {
        mContext = application.getApplicationContext();
        pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        mRegistro = pref.getBoolean(mContext.getResources().getString(R.string.registro_key), false);
        mAppDB = AppDatabase.getAppDatabase(application);
    }

    //Metodos logica productos

    public LiveData<Boolean> altaProducto(Producto producto) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        AppDatabase.dbWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                result.postValue(mAppDB.getProductoDao().insert(producto) > 0);
                if (mRegistro) {
                    try {
                        Registro.guardarRegistro(mContext, producto, "alta", true);
                    } catch (Exception e) {
                        //:)
                    }
                }
            }
        });
        return result;
    }

    public LiveData<Boolean> editarProducto(Producto producto) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        AppDatabase.dbWriteExecutor.execute(() -> {
            result.postValue(mAppDB.getProductoDao().update(producto) > 0);
            if (mRegistro) {
                try {
                    Registro.guardarRegistro(mContext, producto, "edición", true);
                } catch (Exception e) {
                    //:)
                }
            }
        });
        return result;
    }

    public LiveData<Boolean> bajaProducto(Producto producto) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        AppDatabase.dbWriteExecutor.execute(() -> {
            result.postValue(mAppDB.getProductoDao().delete(producto) > 0);
            if (mRegistro) {
                try {
                    Registro.guardarRegistro(mContext, producto, "baja", true);
                } catch (Exception e) {
                    //:)
                }
            }
        });
        return result;
    }

    public LiveData<List<Producto>> recuperarProductosFiltro(String fecAlta, String idAula) {
        return mAppDB.getProductoDao().getAllByFiltro(fecAlta, idAula);
    }

//    public LiveData<List<Producto>> recuperarProductos() {
//        String actualFec = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDateTime.now());
//
//        return mAppDB.getProductoDao().getAllByFiltro(actualFec, "%");
////        return  mAppDB.getProductoDao().getAll();
//    }

//    public LiveData<List<Producto>> recuperarProductosFiltro(String fecAlta, String idAula) {
//        //Cada vez que recuperamos con DAO nos dvulve un livedata diferente
////        LiveData<List<Producto>> productosByFiltro = mAppDB.getProductoDao().getAllByFiltro(fecAlta, idAula);
////
////        try {
////            Thread.sleep(2000);
////        } catch (InterruptedException ignored) {
////
////        }
////        return productosByFiltro;
//
////        return mAppDB.getProductoDao().getAllByFiltro(fecAlta, idAula);
//        return mAppDB.getProductoDao().getAllByFiltroBORRARLUEGOESPARAUNAPRUEBAAAAAAAAA();
//    }

}







