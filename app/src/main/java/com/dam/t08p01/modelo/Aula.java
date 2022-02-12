package com.dam.t08p01.modelo;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Aula implements Parcelable {

    /* Atributos **********************************************************************************/

    private String idDpto;                  // PK
    private String id;                      // PK
    private String nombre;

    /* Constructores ******************************************************************************/

    public Aula() {
        idDpto = "";
        id = "";
        nombre = "";
    }

    /* Métodos Getters&Setters ********************************************************************/

    public String getIdDpto() {
        return idDpto;
    }

    public void setIdDpto(String idDpto) {
        this.idDpto = idDpto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /* Métodos Parcelable *************************************************************************/

    protected Aula(@NonNull Parcel in) {
        idDpto = in.readString();
        id = in.readString();
        nombre = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(idDpto);
        parcel.writeString(id);
        parcel.writeString(nombre);
    }

    public static final Creator<Aula> CREATOR = new Creator<Aula>() {
        @Override
        public Aula createFromParcel(Parcel in) {
            return new Aula(in);
        }

        @Override
        public Aula[] newArray(int size) {
            return new Aula[size];
        }
    };

    /* Métodos ************************************************************************************/

    @NonNull
    @Override
    public String toString() {
        return nombre;
    }

}
