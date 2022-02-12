package com.dam.t08p01.modelo;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.dam.t08p01.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppDatabase {

    private static volatile AppDatabase db = null;  // Singleton

    private static DocumentReference refFS = null;

    //    private static final int NUMBER_OF_THREADS = 1;
    //    public static final ExecutorService dbWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    public static final ExecutorService dbWriteExecutor = Executors.newSingleThreadExecutor();

    private AppDatabase() {
        // Patrón Singleton
    }

    public DocumentReference getRefFS() {
        return refFS;
    }

    public static AppDatabase getAppDatabase(Context context) {
        if (db == null) {
            synchronized (AppDatabase.class) {
                if (db == null) {
                    db = new AppDatabase();

                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                    String nombreBD = pref.getString(context.getResources().getString(R.string.Firebase_name_key), "");
                    if (!nombreBD.equals("")) {
                        // Creamos Dpto 0 admin
                        Departamento dpto = new Departamento();
                        dpto.setId("0");
                        dpto.setNombre("admin");
                        dpto.setClave("aaaaaa");
                        // Ini FirebaseFireStore
                        FirebaseFirestore dbFS = FirebaseFirestore.getInstance();

//                        refFS =  dbFS.collection("proyectos").document(nombreBD); //Es =
                        refFS = dbFS.document("proyectos/" + nombreBD);
                        // Guardamos Dpto 0 admin (si no existe ya)
                        refFS.collection("dptos").document(dpto.getId()).set(dpto);
                    }

                }
            }
        }
        return db;
    }

    public static boolean cerrarAppDatabase() {
        if (db != null) {
            refFS = null;
            db = null;
            return true;
        }
        return false;
    }

}
