package edu.upc.dsa.domain.models;


public class Posicion {

    private double lat;
    private double lon;

    public Posicion() {}

    public Posicion(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    //Sección de getters y setters.
    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLon() { return lon; }
    public void setLon(double lon) { this.lon = lon; }


    //Método para poder leer el objeto Posicion.
    @Override
    public String toString() {
        return "(" + lat + ", " + lon + ")";
    }
}
