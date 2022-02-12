package com.dam.t08p01.modelo;

public class FiltroProductos {

    /* Atributos **********************************************************************************/

    private String idDpto;                    // "" todos
    //TODO
    private String idAula;                    // "" todos
    private String fecAlta;                    // yyyyMMdd

    /* Constructores ******************************************************************************/

    public FiltroProductos() {
        idDpto = "";
    }

    /* Métodos Getters&Setters ********************************************************************/

    public String getIdDpto() {
        return idDpto;
    }

    public void setIdDpto(String idDpto) {
        this.idDpto = idDpto;
    }

    /* Métodos ************************************************************************************/

}
