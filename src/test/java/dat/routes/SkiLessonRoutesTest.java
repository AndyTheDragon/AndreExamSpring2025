package dat.routes;

import dat.config.ApplicationConfig;
import dat.config.HibernateConfig;
import dat.controllers.SecurityController;
import dat.controllers.LessonController;
import dat.entities.Instructor;
import dat.entities.SkiLesson;
import dat.enums.LessonLevel;
import dat.utils.Populator;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.node.ObjectNode;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class SkiLessonRoutesTest
{

    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
    final ObjectMapper objectMapper = new ObjectMapper();
    Instructor g1, g2;
    SkiLesson l1, l2, l3, l4, l5;
    final Logger logger = LoggerFactory.getLogger(SkiLessonRoutesTest.class.getName());


    @BeforeAll
    static void setUpAll()
    {
        LessonController lessonController = new LessonController(emf);
        SecurityController securityController = new SecurityController(emf);
        Routes routes = new Routes(lessonController, securityController);
        ApplicationConfig
                .getInstance()
                .initiateServer()
                .setRoute(routes.getRoutes())
                .handleException()
                .setApiExceptionHandling()
                //.checkSecurityRoles()
                .startServer(7078);
        RestAssured.baseURI = "http://localhost:7078/api";
    }

    @BeforeEach
    void setUp()
    {
        Populator populator = new Populator();
        populator.populate(emf);
        g1 = populator.getInstructors().get(0);
        g2 = populator.getInstructors().get(1);
        l1 = populator.getLessons().get(0);
        l2 = populator.getLessons().get(1);
        l3 = populator.getLessons().get(2);
        l4 = populator.getLessons().get(3);
        l5 = populator.getLessons().get(4);

    }

    @Test
    void getAll()
    {
        given()
                .when()
                .get("/skilessons")
                .then()
                .statusCode(200)
                .body("size()", equalTo(5))
                .body("name", hasItem(l1.getName()))
                .body("name", hasItem(l2.getName()))
                .body("name", hasItem(l3.getName()))
                .body("name", hasItem(l4.getName()))
                .body("name", hasItem(l5.getName()))
                .body("instructor.firstName", hasItem(g1.getFirstName()))
                .body("instructor.firstName", hasItem(g2.getFirstName()));
    }

    @Test
    void getById()
    {
        given()
                .when()
                .get("/skilessons/" + l2.getId())
                .then()
                .statusCode(200)
                .body("name", equalTo(l2.getName()));
    }

    @Test
    void getById_NotFound()
    {
        given()
                .when()
                .get("/skilessons/999")
                .then()
                .statusCode(404)
                .body("message", equalTo("Lesson not found"));
    }

    @Test
    void create()
    {
        try
        {
            ObjectNode locationJson = objectMapper.createObjectNode()
                    .put("description", "Amager Standpark")
                    .put("latitude", 55.6052)
                    .put("longitude", 12.5702);
            String json = objectMapper.createObjectNode().put("name", "Beach Party")
                    .put("price", 100.0)
                    .put("level", "ADVANCED")
                    .put("startTime", "13:00")
                    .put("endTime", "15:00")
                    .set("location", locationJson)
                    .toString();
            given().when()
                    .contentType("application/json")
                    .accept("application/json")
                    .body(json)
                    .post("/skilessons")
                    .then()
                    .statusCode(201);
        } catch (Exception e)
        {
            logger.error("Error creating lesson", e);

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
                    .put("level", LessonLevel.ADVANCED.toString())
                    .put("startTime", "13:00")
                    .put("endTime", "15:00")
                    .toString();
            given().when()
                    .contentType("application/json")
                    .accept("application/json")
                    .body(json)
                    .put("/skilessons/" + l1.getId()) // double check id
                    .then()
                    .statusCode(200)
                    .body("name", equalTo("New entity2"));
        } catch (Exception e)
        {
            logger.error("Error updating lesson", e);
            fail();
        }
    }

    @Test
    void delete()
    {
        given().when()
                .delete("/skilessons/" + l1.getId())
                .then()
                .statusCode(204);
    }
}