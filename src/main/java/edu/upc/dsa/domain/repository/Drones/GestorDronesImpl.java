package edu.upc.dsa.domain.repository.Drones;

import edu.upc.dsa.domain.models.Dron;
import edu.upc.dsa.domain.models.Piloto;
import edu.upc.dsa.domain.models.PlanVuelo;
import edu.upc.dsa.exceptions.*;
import org.apache.log4j.Logger;

import java.time.format.DateTimeParseException;
import java.util.*;

public class GestorDronesImpl implements GestorDrones {

    private static GestorDrones instance;

    private HashMap<String, Dron> drones;
    private HashMap<String, Piloto> pilotos;
    private Deque<Dron> almacen;

    final static Logger logger = Logger.getLogger(GestorDronesImpl.class);


    private GestorDronesImpl()
    {
        drones = new HashMap<>();
        pilotos = new HashMap<>();
        almacen = new LinkedList<>();
    }

    public static GestorDrones getInstance()
    {
        if (instance == null) instance = new GestorDronesImpl();
        return instance;
    }

    @Override
    public void limpiar() {
        logger.info("limpiar()");
        this.drones.clear();
        this.pilotos.clear();
        this.almacen.clear();
        logger.info("limpiar() -> las estructuras de datos se han limpiado");
    }


    @Override
    public void anadirDron(String id, String nombre, String fabricante, String modelo)
    {
        logger.info("anadirDron(id=" + id + ", nombre=" + nombre + ", fabricante=" + fabricante + ", modelo=" + modelo + ")");

        Dron dron = new Dron(id, nombre, fabricante, modelo);
        if (drones.containsKey(dron.getId()))
            logger.warn("anadirDron() -> ya existía un dron con id " + dron.getId() + ", se sobrescribe");

        drones.put(dron.getId(), dron);
        logger.info("anadirDron() -> se ha añadido el dron, " + dron + ", el número total drones es de: " + drones.size());
    }

    @Override
    public void anadirPiloto(String id, String nombre, String apellidos)
    {
        logger.info("anadirPiloto(id=" + id + ", nombre=" + nombre + ", apellidos=" + apellidos + ")");

        if (pilotos.containsKey(id))
            logger.warn("anadirPiloto() -> ya existía un piloto con id " + id + ", se sobrescribe");

        Piloto piloto = new Piloto(id, nombre, apellidos);
        pilotos.put(id, piloto);
        logger.info("anadirPiloto() -> se ha añadido el piloto, " + piloto + ", el número total de pilotos es de: " + pilotos.size());
    }



    @Override
    public List<Dron> getDronesPorHorasVuelo()
    {
        logger.info("getDronesPorHorasVuelo()");

        List<Dron> copia = new ArrayList<>(drones.values());
        copia.sort(Comparator.comparing(Dron::getHorasVuelo).reversed());

        logger.info("getDronesPorHorasVuelo() -> Listado de drones ordenado descendientemente por horas de vuelo, " + copia);
        return copia;
    }

    @Override
    public List<Piloto> getPilotosPorHorasVuelo()
    {
        logger.info("getPilotosPorHorasVuelo()");

        List<Piloto> copia = new ArrayList<>(pilotos.values());
        copia.sort(Comparator.comparing(Piloto::getHorasVuelo).reversed());

        logger.info("getPilotosPorHorasVuelo() -> Listado de pilotos ordenado descendientemente por horas de vuelo, " + copia);
        return copia;
    }


    @Override
    public void guardarDronEnAlmacen(String idDron) throws DronNoEncontradoException, DronEnMantenimientoException
    {
        logger.info("guardarDronEnAlmacen(idDron=" + idDron + ")");

        Dron dron = drones.get(idDron);
        if (dron == null)
        {
            logger.error("guardarDronEnAlmacen() -> el dron " + idDron + " no existe ");
            throw new DronNoEncontradoException(idDron);
        }
        if (dron.isEnMantenimiento())
        {
            logger.error("guardarDronEnAlmacen() -> el dron " + idDron + " ya está en el almacén");
            throw new DronEnMantenimientoException(idDron + " ya está en el almacén");
        }

        dron.setEnMantenimiento(true);
        almacen.push(dron);
        logger.info("guardarDronEnAlmacen() -> el dron " + dron + " se ha guardado en el almacén, y en almacén hay: " + almacen.size() + "drones");
    }

    @Override
    public Dron repararDron() throws AlmacenVacioException
    {
        logger.info("repararDron()");

        Dron dron = almacen.poll();
        if (dron == null)
        {
            logger.error("repararDron() -> el almacén está vacío");
            throw new AlmacenVacioException("Almacén vacío");
        }

        dron.setEnMantenimiento(false);
        logger.info("repararDron() -> Reparación exitosa, se encuentra reparado y disponible el dron: " + dron + ", aún quedan en almacén: " + almacen.size());
        return dron;
    }



    @Override
    public PlanVuelo crearPlanVuelo(PlanVuelo plan) throws DronNoEncontradoException, PilotoNoEncontradoException,
            DronEnMantenimientoException, ConflictoPlanVueloException
    {
        logger.info("crearPlanVuelo(plan=" + plan + ")");

        if (plan == null)
        {
            logger.fatal("crearPlanVuelo() -> el plan de vuelo recibido es null");
            throw new IllegalArgumentException("El plan de vuelo es null");
        }
        if (plan.getHorasEstimadas() <= 0)
        {
            logger.error("crearPlanVuelo() -> estimación de horas no válida: " + plan.getHorasEstimadas());
            throw new IllegalArgumentException("Las horas estimadas deben ser > 0");
        }
        try
        {
            plan.inicio();
        }

        catch (DateTimeParseException | NullPointerException e)

        {
            logger.error("crearPlanVuelo() -> fecha no válida: " + plan.getFecha() + " (formato esperado yyyy-MM-ddTHH:mm)");
            throw new IllegalArgumentException("Fecha no válida: " + plan.getFecha());
        }

        Dron dron = drones.get(plan.getIdDron());

        if (dron == null)
        {
            logger.error("crearPlanVuelo() -> el dron " + plan.getIdDron() + " no existe");
            throw new DronNoEncontradoException(plan.getIdDron());
        }
        Piloto piloto = pilotos.get(plan.getIdPiloto());
        if (piloto == null)
        {
            logger.error("crearPlanVuelo() -> el piloto " + plan.getIdPiloto() + " no existe");
            throw new PilotoNoEncontradoException(plan.getIdPiloto());
        }
        if (dron.isEnMantenimiento())
        {
            logger.error("crearPlanVuelo() -> el dron " + dron.getId() + " está en el almacén y no está operativo");
            throw new DronEnMantenimientoException(dron.getId());
        }
        for (PlanVuelo evaluandoPlandeVuelo : dron.getPlanesVuelo())
        {
            if (plan.seSolapaCon(evaluandoPlandeVuelo))
            {
                logger.error("crearPlanVuelo() -> el dron " + dron.getId() + " ya tiene un plan en ese intervalo temporal: " + evaluandoPlandeVuelo);
                throw new ConflictoPlanVueloException("El dron " + dron.getId() + " ya tiene un plan de vuelo en ese intervalo temporal");
            }
        }
        for (PlanVuelo examinandoPlandeVuelo : piloto.getPlanesVuelo())
        {
            if (plan.seSolapaCon(examinandoPlandeVuelo))
            {
                logger.error("crearPlanVuelo() -> el piloto " + piloto.getId() + " ya tiene un plan en ese intervalo: " + examinandoPlandeVuelo);
                throw new ConflictoPlanVueloException("El piloto " + piloto.getId() + " ya tiene un plan de vuelo en ese intervalo");
            }
        }

        dron.anadirPlanVuelo(plan);
        piloto.anadirPlanVuelo(plan);
        logger.info("crearPlanVuelo() -> Se ha creado el plan, " + plan + " asignado a " + dron.getId() + " y " + piloto.getId());
        return plan;
    }

    @Override
    public List<PlanVuelo> getPlanesVueloPiloto(String idPiloto) throws PilotoNoEncontradoException
    {
        logger.info("getPlanesVueloPiloto(idPiloto=" + idPiloto + ")");

        Piloto piloto = pilotos.get(idPiloto);
        if (piloto == null)
        {
            logger.error("getPlanesVueloPiloto() -> el piloto " + idPiloto + " no existe");
            throw new PilotoNoEncontradoException(idPiloto);
        }

        List<PlanVuelo> resultado = piloto.getPlanesVuelo();
        logger.info("getPlanesVueloPiloto() -> Los planes de vuelo del piloto, " + idPiloto + " son: "+ resultado);
        return resultado;
    }

    @Override
    public List<PlanVuelo> getPlanesVueloDron(String idDron) throws DronNoEncontradoException
    {
        logger.info("getPlanesVueloDron(idDron=" + idDron + ")");

        Dron dron = drones.get(idDron);
        if (dron == null)
        {
            logger.error("getPlanesVueloDron() -> el dron " + idDron + " no existe");
            throw new DronNoEncontradoException(idDron);
        }

        List<PlanVuelo> resultado = dron.getPlanesVuelo();
        logger.info("getPlanesVueloDron() -> Los planes de vuelo del dron " + idDron + "son" + resultado);
        return resultado;
    }


    @Override
    public Dron getDron(String id)
    {
        logger.info("getDron(id=" + id + ")");
        Dron dron = drones.get(id);
        if (dron == null) logger.warn("getDron() -> no existe el dron " + id);
        logger.info("getDron() -> Ha sido encontrado el dron , " + dron);
        return dron;
    }

    @Override
    public Piloto getPiloto(String id)
    {
        logger.info("getPiloto(id=" + id + ")");
        Piloto piloto = pilotos.get(id);
        if (piloto == null) logger.warn("getPiloto() -> no existe el piloto " + id);
        logger.info("getPiloto() -> Ha sido encontrado el piloto, " + piloto);
        return piloto;
    }

    @Override
    public int numDrones() {
        logger.info("numDrones() -> El número de drones es:" + drones.size());
        return drones.size();
    }

    @Override
    public int numPilotos() {
        logger.info("numPilotos() -> El número de pilotos es:" + pilotos.size());
        return pilotos.size();
    }

    @Override
    public int numDronesEnAlmacen() {
        logger.info("numDronesEnAlmacen() -> El número de drones es:" + almacen.size());
        return almacen.size();
    }

    //Las siguientes funciones no son requeridas por el enunciado, aunque resultan útiles para gestionar una flota de drones.
    /*
    @Override
    public List<Dron> getTodosDrones()
    {
        logger.info("getTodosDrones() -> " + drones.size() + " drones");
        return new ArrayList<>(drones.values());
    }

    @Override
    public List<Piloto> getTodosPilotos()
    {
        logger.info("getTodosPilotos() -> " + pilotos.size() + " pilotos");
        return new ArrayList<>(pilotos.values());
    }

    @Override
    public void eliminarDron(String id)
    {
        logger.info("eliminarDron(id=" + id + ")");

        Dron d = drones.remove(id);
        if (d == null) logger.warn("eliminarDron() -> no encontrado " + id);
        else {
            almacen.remove(d);    // por si estaba en mantenimiento
            logger.info("eliminarDron() -> fin, " + d + " eliminado");
        }
    }

    @Override
    public Dron actualizarDron(Dron d)
    {
        logger.info("actualizarDron(dron=" + d + ")");

        Dron t = this.drones.get(d.getId());
        if (t != null) {
            t.setNombre(d.getNombre());
            t.setFabricante(d.getFabricante());
            t.setModelo(d.getModelo());
            logger.info("actualizarDron() -> fin, " + t + " actualizado");
        }
        else {
            logger.warn("actualizarDron() -> no encontrado " + d);
        }
        return t;
    }
    */
}
