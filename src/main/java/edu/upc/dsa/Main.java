package edu.upc.dsa;

import edu.upc.dsa.domain.models.PlanVuelo;
import edu.upc.dsa.domain.repository.Drones.GestorDrones;
import edu.upc.dsa.domain.repository.Drones.GestorDronesImpl;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jersey.listing.ApiListingResourceJSON;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import org.apache.log4j.Logger;

/**
 * Main class.
 *
 */



public class Main {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8080/dsaApp/";
    final static Logger logger = Logger.getLogger(Main.class);
    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in edu.upc.dsa package
        final ResourceConfig rc = new ResourceConfig().packages("edu.upc.dsa.services");

        rc.register(io.swagger.jaxrs.listing.ApiListingResource.class);
        rc.register(io.swagger.jaxrs.listing.SwaggerSerializers.class);
        rc.register(com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider.class);
        BeanConfig beanConfig = new BeanConfig();

        beanConfig.setHost("localhost:8080");
        beanConfig.setBasePath("/dsaApp");
        beanConfig.setContact("support@example.com");
        beanConfig.setDescription("API REST para la gestión de una flota de drones");
        beanConfig.setLicenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html");
        beanConfig.setResourcePackage("edu.upc.dsa.services");
        beanConfig.setTermsOfServiceUrl("http://www.example.com/resources/eula");
        beanConfig.setTitle("REST API");
        beanConfig.setVersion("1.0.0");
        beanConfig.setScan(true);

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void cargarDatosIniciales() {
        GestorDrones gd = GestorDronesImpl.getInstance();

        if (gd.numDrones()==0) {
            gd.anadirDron("D1", "DronEetac1", "DJI", "Básico");
            gd.anadirDron("D2", "DronEetac2", "DJI", "Profesional");
            gd.anadirDron("D3", "DronEetac3", "Estudiantes", "Mejorado");
            gd.anadirDron("D4", "DronEetac4", "ProfesorInfoII", "Espionaje");

            gd.anadirPiloto("P1", "Laura", "Perez");
            gd.anadirPiloto("P2", "Jordi", "Alegria");
            gd.anadirPiloto("P3", "Marta", "Ortiz");

            try {
                PlanVuelo p1 = new PlanVuelo("D1", "P1", "2026-11-10T10:00", 2);
                p1.anadirPosicion(46.2750, 1.86);
                p1.anadirPosicion(45.5230, 1.90);
                gd.crearPlanVuelo(p1);

                PlanVuelo p2 = new PlanVuelo("D2", "P2", "2026-11-10T10:00", 4.5);
                p2.anadirPosicion(41.2750, 1.72);
                p2.anadirPosicion(42.5230, 1.52);
                gd.crearPlanVuelo(p2);

                PlanVuelo p3 = new PlanVuelo("D3", "P1", "2026-11-11T09:00", 1);
                p3.anadirPosicion(44.2750, 1.986);
                gd.crearPlanVuelo(p3);

                gd.guardarDronEnAlmacen("D5");    // D5 empieza en mantenimiento
            } catch (Exception e) {
                logger.error("cargarDatosIniciales() -> error cargando datos iniciales: " + e.getMessage());
            }
        }
    }


    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        cargarDatosIniciales();
        final HttpServer server = startServer();

        StaticHttpHandler staticHttpHandler = new StaticHttpHandler("./public/");
        server.getServerConfiguration().addHttpHandler(staticHttpHandler, "/");


        logger.info(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));

        System.in.read();
        server.stop();
    }
}

