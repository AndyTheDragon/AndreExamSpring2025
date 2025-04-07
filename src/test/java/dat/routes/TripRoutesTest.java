package dat.routes;

import dat.config.ApplicationConfig;
import dat.config.HibernateConfig;
import dat.controllers.SecurityController;
import dat.controllers.TripController;
import dat.entities.Guide;
import dat.entities.Trip;
import dat.enums.TripCategory;
import dat.utils.Populator;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.node.ObjectNode;

import static org.junit.jupiter.api.Assertions.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class TripRoutesTest
{

    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
    final ObjectMapper objectMapper = new ObjectMapper();
    Guide g1, g2;
    Trip t1, t2, t3, t4, t5;
    final Logger logger = LoggerFactory.getLogger(TripRoutesTest.class.getName());


    @BeforeAll
    static void setUpAll()
    {
        TripController tripController = new TripController(emf);
        SecurityController securityController = new SecurityController(emf);
        Routes routes = new Routes(tripController, securityController);
        ApplicationConfig
                .getInstance()
                .initiateServer()
                .setRoute(routes.getRoutes())
                .handleException()
                .setApiExceptionHandling()
                .checkSecurityRoles()
                .startServer(7078);
        RestAssured.baseURI = "http://localhost:7078/api";
    }

    @BeforeEach
    void setUp()
    {
        Populator populator = new Populator();
        populator.populate(emf);
        g1 = populator.getGuides().get(0);
        g2 = populator.getGuides().get(1);
        t1 = populator.getTrips().get(0);
        t2 = populator.getTrips().get(1);
        t3 = populator.getTrips().get(2);
        t4 = populator.getTrips().get(3);
        t5 = populator.getTrips().get(4);

    }

    @Test
    void getAll()
    {
        given()
                .when()
                .get("/trips")
                .then()
                .statusCode(200)
                .body("size()", equalTo(5));
    }

    @Test
    void getById()
    {
        given()
                .when()
                .get("/trips/" + t2.getId())
                .then()
                .statusCode(200)
                .body("name", equalTo(t2.getName()));
    }

    @Test
    void getById_includeItems()
    {
        given()
                .when()
                .get("/trips/" + t2.getId() + "?withItems=true")
                .then()
                .statusCode(200)
                .body("name", equalTo(t2.getName()))
                .body("items.size()", equalTo(7));
    }

    @Test
    void create()
    {
        try
        {
            ObjectNode startPositionJson = objectMapper.createObjectNode()
                    .put("description", "Amager Standpark")
                    .put("latitude", 55.6052)
                    .put("longitude", 12.5702);
            String json = objectMapper.createObjectNode().put("name", "Beach Party")
                    .put("price", 100.0)
                    .put("category", "BEACH")
                    .put("startTime", "13:00")
                    .put("endTime", "15:00")
                    .set("startPosition", startPositionJson)
                    .toString();
            given().when()
                    .contentType("application/json")
                    .accept("application/json")
                    .body(json)
                    .post("/trips")
                    .then()
                    .statusCode(201);
        } catch (Exception e)
        {
            logger.error("Error creating trip", e);

            fail();
        }
    }

    @Test
    void update()
    {
        try
        {
            String json = objectMapper.createObjectNode().put("name", "New entity2")
                    .put("price", 100.0)
                    .put("category", TripCategory.BEACH.toString())
                    .put("startTime", "13:00")
                    .put("endTime", "15:00")
                    .toString();
            given().when()
                    .contentType("application/json")
                    .accept("application/json")
                    .body(json)
                    .put("/trips/" + t1.getId()) // double check id
                    .then()
                    .statusCode(200)
                    .body("name", equalTo("New entity2"));
        } catch (Exception e)
        {
            logger.error("Error updating trip", e);
            fail();
        }
    }

    @Test
    void delete()
    {
        given().when()
                .delete("/trips/" + t1.getId())
                .then()
                .statusCode(204);
    }
}