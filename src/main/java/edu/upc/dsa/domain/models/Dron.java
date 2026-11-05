package edu.upc.dsa.domain.models;


import edu.upc.dsa.util.RandomUtils;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlTransient;
import java.util.LinkedList;
import java.util.List;

public class Dron {

    private String id;
    private String nombre;
    private String fabricante;
    private String modelo;
    private double horasVuelo;
    private boolean enMantenimiento;

    private List<PlanVuelo> planesVuelo;

    public Dron() {
        this.id = RandomUtils.getId();
        this.planesVuelo = new LinkedList<>();
    }

    public Dron(String id, String nombre, String fabricante, String modelo) {
        this();
        if (id != null) this.id = id;
        this.nombre = nombre;
        this.fabricante = fabricante;
        this.modelo = modelo;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getFabricante() { return fabricante; }
    public void setFabricante(String fabricante) { this.fabricante = fabricante; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public double getHorasVuelo() { return horasVuelo; }
    public void setHorasVuelo(double horasVuelo) { this.horasVuelo = horasVuelo; }

    public boolean isEnMantenimiento() { return enMantenimiento; }
    public void setEnMantenimiento(boolean enMantenimiento) { this.enMantenimiento = enMantenimiento; }

    @XmlTransient
    @ApiModelProperty(hidden = true)
    public List<PlanVuelo> getPlanesVuelo() { return planesVuelo; }

    public void anadirPlanVuelo(PlanVuelo plan) {
        planesVuelo.add(plan);
        this.horasVuelo += plan.getHorasEstimadas();
    }

    public int numPlanesVuelo() { return planesVuelo.size(); }

    @Override
    public String toString() {
        return "Dron[id=" + id + ", nombre=" + nombre + ", fabricante=" + fabricante
                + ", modelo=" + modelo + ", horasVuelo=" + horasVuelo
                + ", enMantenimiento=" + enMantenimiento + "]";
    }
}
