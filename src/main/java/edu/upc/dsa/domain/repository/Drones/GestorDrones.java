package edu.upc.dsa.domain.repository.Drones;


import edu.upc.dsa.domain.models.Dron;
import edu.upc.dsa.domain.models.Piloto;
import edu.upc.dsa.domain.models.PlanVuelo;
import edu.upc.dsa.exceptions.*;

import java.util.List;

public interface GestorDrones {

    void anadirDron(String id, String nombre, String fabricante, String modelo);
    void anadirPiloto(String id, String nombre, String apellidos);

    List<Dron> getDronesPorHorasVuelo();
    List<Piloto> getPilotosPorHorasVuelo();

    void guardarDronEnAlmacen(String idDron) throws DronNoEncontradoException, DronEnMantenimientoException;
    Dron repararDron() throws AlmacenVacioException;

    PlanVuelo crearPlanVuelo(PlanVuelo plan) throws DronNoEncontradoException, PilotoNoEncontradoException,
            DronEnMantenimientoException, ConflictoPlanVueloException;

    List<PlanVuelo> getPlanesVueloPiloto(String idPiloto) throws PilotoNoEncontradoException;
    List<PlanVuelo> getPlanesVueloDron(String idDron) throws DronNoEncontradoException;


    Dron getDron(String id);
    Piloto getPiloto(String id);

    int numDrones();
    int numPilotos();
    int numDronesEnAlmacen();

    List<Dron> getTodosDrones();
    List<Piloto> getTodosPilotos();
    void eliminarDron(String id);
    Dron actualizarDron(Dron dron);

    void limpiar();
}
