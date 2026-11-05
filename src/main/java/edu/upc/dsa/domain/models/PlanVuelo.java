package edu.upc.dsa.domain.models;


import edu.upc.dsa.util.RandomUtils;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;


public class PlanVuelo {

    private String id;
    private String idDron;
    private String idPiloto;
    private String fecha;            // formato ISO: "2026-11-10T10:00"
    private double horasEstimadas;
    private List<Posicion> posiciones;

    public PlanVuelo() {
        this.id = RandomUtils.getId();
        this.posiciones = new LinkedList<>();
    }

    public PlanVuelo(String idDron, String idPiloto, String fecha, double horasEstimadas) {
        this();
        this.idDron = idDron;
        this.idPiloto = idPiloto;
        this.fecha = fecha;
        this.horasEstimadas = horasEstimadas;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

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
    public LocalDateTime inicio() {
        return LocalDateTime.parse(fecha);
    }

    public LocalDateTime fin() {
        return inicio().plusMinutes(Math.round(horasEstimadas * 60));
    }

    //evitar solapamientos
    public boolean seSolapaCon(PlanVuelo otro) {
        return this.inicio().isBefore(otro.fin())
                && otro.inicio().isBefore(this.fin());
    }

    @Override
    public String toString()
    {
        return "PlanVuelo[id=" + id + ", idDron=" + idDron + ", idPiloto=" + idPiloto
                + ", fecha=" + fecha + ", horas=" + horasEstimadas + ", posiciones=" + posiciones + "]";
    }
}
