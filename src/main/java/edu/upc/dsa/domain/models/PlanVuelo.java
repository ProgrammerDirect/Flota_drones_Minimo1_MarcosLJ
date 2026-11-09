package edu.upc.dsa.domain.models;


import edu.upc.dsa.util.RandomUtils;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;


public class PlanVuelo {

    private String idDron;
    private String idPiloto;
    private String fecha;            // formato ISO: "2026-11-10T10:00"
    private double horasEstimadas;
    private List<Posicion> posiciones;

    public PlanVuelo() {
        this.posiciones = new LinkedList<>();
    }

    public PlanVuelo(String idDron, String idPiloto, String fecha, double horasEstimadas) {
        this();
        this.idDron = idDron;
        this.idPiloto = idPiloto;
        this.fecha = fecha;
        this.horasEstimadas = horasEstimadas;
    }

    //Sección de getters y setters
    public String getIdDron() { return idDron; }
    public void setIdDron(String idDron) { this.idDron = idDron; }

    public String getIdPiloto() { return idPiloto; }
    public void setIdPiloto(String idPiloto) { this.idPiloto = idPiloto; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public double getHorasEstimadas() { return horasEstimadas; }
    public void setHorasEstimadas(double horasEstimadas) { this.horasEstimadas = horasEstimadas; }

    public List<Posicion> getPosiciones() { return posiciones; }
    public void setPosiciones(List<Posicion> posiciones) { this.posiciones = posiciones; }

    public void anadirPosicion(double lat, double lon) {
        posiciones.add(new Posicion(lat, lon));
    }

    // No empiezan por "get" para que no se serialicen en el JSON
    //Funcioes para determinar cuándo empieza y finaliza un plan de vuelo.

    public LocalDateTime inicio() {
        return LocalDateTime.parse(fecha);
    }

    public LocalDateTime fin() {
        return inicio().plusMinutes(Math.round(horasEstimadas * 60));
    }

    //Evitar solapamientos
    public boolean seSolapaCon(PlanVuelo plan)
    {
        return this.inicio().isBefore(plan.fin()) && plan.inicio().isBefore(this.fin());
    }

    //Método para poder leer el objeto PlanVuelo.
    @Override
    public String toString()
    {
        return "PlanVuelo[ idDron=" + idDron + ", idPiloto=" + idPiloto
                + ", fecha=" + fecha + ", horas=" + horasEstimadas + ", posiciones=" + posiciones + "]";
    }
}
