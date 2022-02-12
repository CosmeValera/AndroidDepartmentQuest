package com.dam.t08p01.modelo;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.dam.t08p01.R;

import java.io.File;
import java.util.List;

public class Registro {

    // Rutas Ficheros
    // Internos -> /data/data/com.dam.t08p01/files
    // Externos -> /storage/emulated/0/Android/data/com.dam.t08p01/files

    // No utilizo las funciones openFileInput y openFileOutput de la documentación Android,
    // porque solo funcionan para almacenamiento interno!!
    // ya que solo piden el nombre del fichero sin ruta y esto falla para almacenamiento externo!!

    public static void recuperarRegistro(Context context, @NonNull List<String> registro) throws Exception {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String ficheroRegistro = pref.getString(context.getResources().getString(R.string.ficheroRegistro_key), "");

        //ficheroRegistro = context.getFilesDir() + "/" + ficheroRegistro;

        //FileInputStream fis = context.openFileInput(ficheroRegistro);//new FileInputStream(ficheroRegistro);

    }

//    public static void guardarRegistro(Context context, @NonNull Producto producto, String op, boolean result) throws Exception {
//
//        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
//        String ficheroRegistro = pref.getString(context.getResources().getString(R.string.ficheroRegistro_key), "");
//
//        //ficheroRegistro = context.getFilesDir() + "/" + ficheroRegistro;
//
//        //FileOutputStream fos = context.openFileOutput(ficheroRegistro, Context.MODE_APPEND);//new FileOutputStream(ficheroRegistro, true);
//
//    }

    public static void borrarRegistro(Context context) throws Exception {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String ficheroRegistro = pref.getString(context.getResources().getString(R.string.ficheroRegistro_key), "");

        ficheroRegistro = context.getFilesDir() + "/" + ficheroRegistro;

        try {
            File f = new File(ficheroRegistro);
            if (!f.delete())
                throw new Exception();
        } catch (Exception e) {
            throw new Exception(context.getResources().getString(R.string.msg_ErrorRegistro), e);
        }
    }

}
