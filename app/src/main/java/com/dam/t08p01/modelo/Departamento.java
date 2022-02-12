package com.dam.t08p01.modelo;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Departamento implements Parcelable {

    /* Atributos **********************************************************************************/

    private String id;                      // PK
    private String nombre;
    private String clave;

    /* Constructores ******************************************************************************/

    public Departamento() {
        id = "";
        nombre = "";
        clave = "";
    }

    /* Métodos Getters&Setters ********************************************************************/

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

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    /* Métodos Parcelable *************************************************************************/

    protected Departamento(@NonNull Parcel in) {
        id = in.readString();
        nombre = in.readString();
        clave = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(nombre);
        parcel.writeString(clave);
    }

    public static final Creator<Departamento> CREATOR = new Creator<Departamento>() {
        @Override
        public Departamento createFromParcel(Parcel in) {
            return new Departamento(in);
        }

        @Override
        public Departamento[] newArray(int size) {
            return new Departamento[size];
        }
    };

    /* Métodos ************************************************************************************/

    @NonNull
    @Override
    public String toString() {
        return nombre;
    }

}
