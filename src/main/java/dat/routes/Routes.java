package dat.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import dat.controllers.SecurityController;
import dat.controllers.LessonController;
import dat.enums.Roles;
import io.javalin.apibuilder.EndpointGroup;
import org.jetbrains.annotations.NotNull;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Routes
{
    private final LessonController lessonController;
    private final SecurityController securityController;
    private final ObjectMapper jsonMapper = new ObjectMapper();

    public Routes(LessonController lessonController, SecurityController securityController)
    {
        this.lessonController = lessonController;
        this.securityController = securityController;
    }

    public  EndpointGroup getRoutes()
    {
        return () -> {
            path("skilessons", lessonRoutes());
            path("instructors", instructorRoutes());
            path("auth", authRoutes());
        };
    }

    private EndpointGroup instructorRoutes()
    {
        return () -> {
            get("/{id}/skilessons", lessonController::getLessonsByInstructor, Roles.USER);
            get("/{id}/totalsumprice", lessonController::getTotalSumPriceOfLessonsByInstructor, Roles.USER);
            get("/totalsumprice", lessonController::getTotalSumPriceOfLessons, Roles.USER);
            get("/{id}/totalsumduration", lessonController::getSumOfLessonTimesByInstructor, Roles.USER);
            get("/totalsumduration", lessonController::getSumOfLessonTimes, Roles.USER);
        };
    }

    private  EndpointGroup lessonRoutes()
    {
        return () -> {
            get(lessonController::getAllLessons);
            get("/level/{level}", lessonController::getLessonsByLevel);
            get("/{id}", lessonController::getLessonById);
            post(lessonController::createLesson, Roles.ADMIN);
            put("/{id}", lessonController::updateLesson, Roles.ADMIN);
            delete("/{id}", lessonController::deleteLesson, Roles.ADMIN);
            put("/{lessonId}/instructors/{instructorId}", lessonController::addInstructorToLesson, Roles.USER);
            post("/populate", lessonController::populate, Roles.USER);
        };
    }

    private  EndpointGroup authRoutes()
    {
        return () -> {
            get("/test", ctx->ctx.json(jsonMapper.createObjectNode().put("msg",  "Hello from Open")), Roles.ANYONE);
            get("/healthcheck", securityController::healthCheck, Roles.ANYONE);
            post("/login", securityController::login, Roles.ANYONE);
            post("/register", securityController::register, Roles.ANYONE);
            get("/verify", securityController::verify , Roles.ANYONE);
            get("/tokenlifespan", securityController::timeToLive , Roles.ANYONE);
        };
    }

}
