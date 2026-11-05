package edu.upc.dsa.domain.models;


import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlTransient;
import java.util.LinkedList;
import java.util.List;

public class Piloto {

    private String id;
    private String nombre;
    private String apellidos;
    private double horasVuelo;

    private List<PlanVuelo> planesVuelo;   // planes asignados a este piloto

    public Piloto(String id, String nombre, String apellidos)
    {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.planesVuelo = new LinkedList<>();
    }

    public Piloto() {
        this.planesVuelo = new LinkedList<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public double getHorasVuelo() { return horasVuelo; }
    public void setHorasVuelo(double horasVuelo) { this.horasVuelo = horasVuelo; }

    // Se oculta en el JSON: los planes se consultan con su propio endpoint
    @XmlTransient
    @ApiModelProperty(hidden = true)
    public List<PlanVuelo> getPlanesVuelo() { return planesVuelo; }

    public void anadirPlanVuelo(PlanVuelo plan) {
        planesVuelo.add(plan);
        this.horasVuelo += plan.getHorasEstimadas();
    }

    public int numPlanesVuelo() { return planesVuelo.size(); }

    @Override
    public String toString()
    {
        return "Piloto[id=" + id + ", nombre=" + nombre + ", apellidos=" + apellidos
                + ", horasVuelo=" + horasVuelo + ", planes=" + planesVuelo.size() + "]";
    }
}
