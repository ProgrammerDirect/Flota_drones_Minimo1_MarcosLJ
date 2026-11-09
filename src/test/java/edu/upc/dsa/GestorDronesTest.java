package edu.upc.dsa;

import edu.upc.dsa.domain.models.Dron;
import edu.upc.dsa.domain.models.Piloto;
import edu.upc.dsa.domain.models.PlanVuelo;
import edu.upc.dsa.domain.repository.Drones.GestorDrones;
import edu.upc.dsa.domain.repository.Drones.GestorDronesImpl;
import edu.upc.dsa.exceptions.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class GestorDronesTest {
    GestorDrones gd;

    @Before
    public void setUp() {
        this.gd = GestorDronesImpl.getInstance(); 

        gd.anadirDron("D1", "DronEetac1", "DJI", "Básico");
        gd.anadirDron("D2", "DronEetac2", "DJI", "Profesional");
        gd.anadirDron("D3", "DronEetac3", "Estudiantes", "Mejorado");
        gd.anadirDron("D4", "DronEetac4", "ProfesorInfoII", "Espionaje");

        gd.anadirPiloto("P1", "Laura", "Perez");
        gd.anadirPiloto("P2", "Jordi", "Alegria");
        gd.anadirPiloto("P3", "Marta", "Ortiz");
    }

    @After
    public void tearDown()
    {
        this.gd.limpiar();
    }


    private PlanVuelo plan(String idDron, String idPiloto, String fecha, double horas) {
        PlanVuelo p = new PlanVuelo(idDron, idPiloto, fecha, horas);
        p.anadirPosicion(46.2750, 1.86);
        p.anadirPosicion(45.5230, 1.90);
        return p;
    }

    @Test
    public void anadirDronTest() {
        Assert.assertEquals(4, gd.numDrones());
        gd.anadirDron("D5", "DronDSA", "ProfesorDSA", "Profesional");
        Assert.assertEquals(5, gd.numDrones());
        Assert.assertEquals("ProfesorDSA", gd.getDron("D5").getFabricante());
    }

    @Test
    public void anadirPilotoTest() {
        Assert.assertEquals(3, gd.numPilotos());
        gd.anadirPiloto("P4", "Marcos", "López");
        Assert.assertEquals(4, gd.numPilotos());
        Assert.assertEquals("López", gd.getPiloto("P4").getApellidos());
    }

    @Test
    //Verifiar que guardamos en la pila del almacén al dron, al reparar miramos si funciona
    //correctamente sacarlo de la pila y lo sacamos del estado de mantenimiento.
    public void mantenimientoDronTest() throws Exception {
        Assert.assertEquals(0, gd.numDronesEnAlmacen());

        gd.guardarDronEnAlmacen("D1");
        gd.guardarDronEnAlmacen("D2");
        gd.guardarDronEnAlmacen("D3");
        Assert.assertEquals(3, gd.numDronesEnAlmacen());
        Assert.assertTrue(gd.getDron("D1").isEnMantenimiento());


        Dron r1 = gd.repararDron();
        Assert.assertEquals("D3", r1.getId());
        Assert.assertFalse(r1.isEnMantenimiento());

        Assert.assertEquals("D2", gd.repararDron().getId());
        Assert.assertEquals("D1", gd.repararDron().getId());
        Assert.assertEquals(0, gd.numDronesEnAlmacen());
    }

    //Test de la excepción que debe ejecutarse si el almacén está vacío.
    @Test(expected = AlmacenVacioException.class)
    public void repararAlmacenVacioTest() throws Exception {
        gd.repararDron();
    }

    //Test de la excepción que debe ejecutarse si no se encuentra el dron.
    @Test(expected = DronNoEncontradoException.class)
    public void guardarDronInexistenteTest() throws Exception {
        gd.guardarDronEnAlmacen("XX");
    }

    @Test
    public void crearPlanVueloTest() throws Exception {
        PlanVuelo p = gd.crearPlanVuelo(plan("D1", "P1", "2026-11-10T10:00", 2));

        Assert.assertEquals(1, gd.getDron("D1").numPlanesVuelo());
        Assert.assertEquals(1, gd.getPiloto("P1").numPlanesVuelo());
        Assert.assertEquals(2, p.getPosiciones().size());
        Assert.assertEquals(2.0, gd.getDron("D1").getHorasVuelo(), 0.001);
        Assert.assertEquals(2.0, gd.getPiloto("P1").getHorasVuelo(), 0.001);
    }

    @Test(expected = ConflictoPlanVueloException.class)
    public void conflictoDronTest() throws Exception {
        gd.crearPlanVuelo(plan("D1", "P1", "2026-11-10T10:00", 2));
        gd.crearPlanVuelo(plan("D1", "P2", "2026-11-10T11:00", 1));
    }

    @Test(expected = ConflictoPlanVueloException.class)
    public void conflictoPilotoTest() throws Exception {
        gd.crearPlanVuelo(plan("D1", "P1", "2026-11-10T10:00", 2));
        gd.crearPlanVuelo(plan("D2", "P1", "2026-11-10T09:30", 1));
    }

    @Test
    public void planesContiguosSonValidosTest() throws Exception {
        gd.crearPlanVuelo(plan("D1", "P1", "2026-11-10T10:00", 2));
        gd.crearPlanVuelo(plan("D1", "P1", "2026-11-10T12:00", 1));
        Assert.assertEquals(2, gd.getDron("D1").numPlanesVuelo());
    }

    @Test(expected = DronEnMantenimientoException.class)
    public void planVueloDronEnMantenimientoTest() throws Exception {
        gd.guardarDronEnAlmacen("D2");
        gd.crearPlanVuelo(plan("D2", "P1", "2026-11-10T10:00", 1));
    }

    @Test
    public void planVueloTrasRepararTest() throws Exception {
        gd.guardarDronEnAlmacen("D2");
        gd.repararDron();
        gd.crearPlanVuelo(plan("D2", "P1", "2026-11-10T10:00", 1));
        Assert.assertEquals(1, gd.getDron("D2").numPlanesVuelo());
    }

    @Test
    public void dronesYPilotosPorHorasVueloTest() throws Exception {
        gd.crearPlanVuelo(plan("D1", "P1", "2026-11-10T10:00", 1));
        gd.crearPlanVuelo(plan("D2", "P2", "2026-11-10T10:00", 5));
        gd.crearPlanVuelo(plan("D3", "P3", "2026-11-10T10:00", 3));
        gd.crearPlanVuelo(plan("D3", "P1", "2026-11-11T10:00", 0.5));

        List<Dron> drones = gd.getDronesPorHorasVuelo();
        Assert.assertEquals("D2", drones.get(0).getId());
        Assert.assertEquals(5.0, drones.get(0).getHorasVuelo(), 0.001);
        Assert.assertEquals("D3", drones.get(1).getId());
        Assert.assertEquals(3.5, drones.get(1).getHorasVuelo(), 0.001);
        Assert.assertEquals("D1", drones.get(2).getId());
        Assert.assertEquals("D4", drones.get(3).getId());
        Assert.assertEquals(0.0, drones.get(3).getHorasVuelo(), 0.001);

        List<Piloto> pilotos = gd.getPilotosPorHorasVuelo();
        Assert.assertEquals("P2", pilotos.get(0).getId());
        Assert.assertEquals("P3", pilotos.get(1).getId());
        Assert.assertEquals("P1", pilotos.get(2).getId());
        Assert.assertEquals(1.5, pilotos.get(2).getHorasVuelo(), 0.001);
    }

    @Test
    public void planesVueloPorPilotoYDronTest() throws Exception {
        gd.crearPlanVuelo(plan("D1", "P1", "2026-11-10T10:00", 1));
        gd.crearPlanVuelo(plan("D2", "P1", "2026-11-10T12:00", 1));
        gd.crearPlanVuelo(plan("D1", "P2", "2026-11-10T14:00", 1));

        List<PlanVuelo> deP1 = gd.getPlanesVueloPiloto("P1");
        Assert.assertEquals(2, deP1.size());
        Assert.assertEquals("D1", deP1.get(0).getIdDron());
        Assert.assertEquals("D2", deP1.get(1).getIdDron());

        List<PlanVuelo> deD1 = gd.getPlanesVueloDron("D1");
        Assert.assertEquals(2, deD1.size());
        Assert.assertEquals("P1", deD1.get(0).getIdPiloto());
        Assert.assertEquals("P2", deD1.get(1).getIdPiloto());

        Assert.assertEquals(0, gd.getPlanesVueloPiloto("P3").size());
    }

    @Test(expected = PilotoNoEncontradoException.class)
    public void planesVueloPilotoInexistenteTest() throws Exception {
        gd.getPlanesVueloPiloto("Inexistente");
    }
}
