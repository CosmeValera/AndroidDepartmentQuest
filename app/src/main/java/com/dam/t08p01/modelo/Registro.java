package com.dam.t08p01.modelo;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.dam.t08p01.R;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
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

        try (FileInputStream fis = context.openFileInput(ficheroRegistro);
             DataInputStream dis = new DataInputStream(fis)) {
            while (true) {
                String reg = "";
                reg += dis.readUTF();
                reg += "\nDpto: ";
                reg += dis.readInt();
                reg += " Producto: ";
                reg += dis.readUTF();
                reg += " Op.: ";
                reg += dis.readUTF();
                reg += " Res.: ";
                reg += dis.readBoolean();
                registro.add(reg);
            }
        } catch (EOFException e) {
            // :)
        } catch (IOException e) {
            throw new Exception(context.getResources().getString(R.string.msg_ErrorRecuperarRegistro));
        }
    }

    public static void guardarRegistro(Context context, @NonNull Producto producto, String op, boolean result) throws Exception {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String ficheroRegistro = pref.getString(context.getResources().getString(R.string.ficheroRegistro_key), "");

        try (FileOutputStream fos = context.openFileOutput(ficheroRegistro, Context.MODE_APPEND);
             DataOutputStream dos = new DataOutputStream(fos);) {
            dos.writeUTF(String.valueOf(Calendar.getInstance().getTime()));
            dos.writeUTF(producto.getIdDpto());
            dos.writeUTF(producto.getId());
            dos.writeUTF(op);
            dos.writeBoolean(result);
        } catch (IOException e) {
            throw new Exception(context.getResources().getString(R.string.msg_ErrorGuardarRegistro));
        }
    }

    public static void borrarRegistro(Context context) throws Exception {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String ficheroRegistro = pref.getString(context.getResources().getString(R.string.ficheroRegistro_key), "");

        context.deleteFile(ficheroRegistro);
    }
}
