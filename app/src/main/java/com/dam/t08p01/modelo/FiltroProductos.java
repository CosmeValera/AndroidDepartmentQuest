package com.dam.t08p01.modelo;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FiltroProductos {
    private String fecAlta;
    private String idAula;

    public FiltroProductos() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        this.fecAlta = sdf.format(Calendar.getInstance().getTime());
        this.idAula = "";
    }

    public FiltroProductos(String idAula, String fecAlta) {
        this.fecAlta = fecAlta;
        this.idAula = idAula;
    }

    public String getFecAlta() {
        // yyyyMMdd -> dd/MM/yyyy
        return fecAlta;
//        return String.format("%02d/%02d/%04d", Integer.parseInt(fecAlta.substring(6, 8)),
//                Integer.parseInt(fecAlta.substring(4, 6)),
//                Integer.parseInt(fecAlta.substring(0, 4)));
//        return fecAlta;
    }

//    public String getFecAltaFiltro() {
//        // yyyyMMdd -> dd/MM/yyyy
//        return String.format("%02d/%02d/%04d", Integer.parseInt(fecAlta.substring(6, 8)),
//                Integer.parseInt(fecAlta.substring(4, 6)),
//                Integer.parseInt(fecAlta.substring(0, 4)));
////        return fecAlta;
//    }

    public void setFecAlta(String fecAlta) {
        // dd/MM/yyyy -> yyyyMMdd
        String fecAltaF =  fecAlta.substring(6, 10) + fecAlta.substring(3, 5) + fecAlta.substring(0, 2);
        this.fecAlta =fecAltaF;
//        this.fecAlta = fecAlta;
    }

    public String getIdAula() {
        return idAula;
    }

    public void setIdAula(String idAula) {
        this.idAula = idAula;
    }
}
