package com.dam.t08p01.repositorio;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

import com.dam.t08p01.modelo.Registro;

import java.util.ArrayList;
import java.util.List;

public class MainRepository {

    /* Repository Main ****************************************************************************/

    private final Context mContext;

    public MainRepository(@NonNull Application application) {
        mContext = application.getApplicationContext();
    }

    /* Métodos Lógica Main ************************************************************************/

    public List<String> recuperarRegistro() {
        List<String> registro = new ArrayList<>();
        try {
            Registro.recuperarRegistro(mContext, registro);
        } catch (Exception e) {
            registro.clear();
            return registro;
        }
        return registro;
    }

    public boolean borrarRegistro() {
        try {
            Registro.borrarRegistro(mContext);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
