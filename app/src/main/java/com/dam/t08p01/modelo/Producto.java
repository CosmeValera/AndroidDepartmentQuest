package com.dam.t08p01.modelo;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Producto implements Parcelable {
    private int idDpto; //PK, FK
    @NonNull
    private String id; //PK
    private String fecAlta; //yyyyMMdd
    private String nombre;
    @NonNull
    private String idAula; //FK
    private int cantidad;

    //Constructores
    public Producto() {
        idDpto = 0;
        id = "";
        fecAlta = "";
        nombre = "";
        idAula = "";
        cantidad = 0;
    }

    protected Producto(Parcel in) {
        idDpto = in.readInt();
        id = in.readString();
        fecAlta = in.readString();
        nombre = in.readString();
        idAula = in.readString();
        cantidad = in.readInt();
    }

    //getters & setters
    public int getIdDpto() {
        return idDpto;
    }

    public void setIdDpto(int idDpto) {
        this.idDpto = idDpto;
    }

    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getFecAlta() {
        return fecAlta;
    }

    //La fecha será la fecha actual al dar de alta el producto, se guardará en formato
    //“yyyyMMdd” pero se mostrará con formato “dd/MM/yyyy” (día, mes, año).
    public String getFecAltaF() {        //De "yyyyMMdd" a "dd/MM/yyyy"
        return String.format("%02d/%02d/%04d", Integer.parseInt(fecAlta.substring(6, 8)),
                Integer.parseInt(fecAlta.substring(4, 6)),
                Integer.parseInt(fecAlta.substring(0, 4)));
    }

    public void setFecAlta(String fecAlta) {
        this.fecAlta = fecAlta;
    }

    public void setFecAltaF(String fecAlta) { //De "dd/MM/yyyy" a "yyyyMMdd"
        this.fecAlta = fecAlta.substring(6, 10) + fecAlta.substring(3, 5) + fecAlta.substring(0, 2);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIdAula() {
        return idAula;
    }

    public void setIdAula(String idAula) {
        this.idAula = idAula;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(idDpto);
        parcel.writeString(id);
        parcel.writeString(fecAlta);
        parcel.writeString(nombre);
        parcel.writeString(idAula);
        parcel.writeInt(cantidad);
    }

    public static final Creator<Producto> CREATOR = new Creator<Producto>() {
        @Override
        public Producto createFromParcel(Parcel in) {
            return new Producto(in);
        }

        @Override
        public Producto[] newArray(int size) {
            return new Producto[size];
        }
    };

    //Metodos
    @NonNull
    @Override
    public String toString() {
        return nombre;
    }
}
