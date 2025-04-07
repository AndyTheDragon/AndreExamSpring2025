package dat.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dat.dao.SkiLessonDAO;
import dat.dto.*;
import dat.enums.LessonLevel;
import dat.exceptions.ApiException;
import dat.exceptions.DaoException;
import dat.utils.DataAPIReader;
import dat.utils.Populator;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

public class LessonController
{
    private final EntityManagerFactory emf;
    private final SkiLessonDAO dao;
    private final Logger logger = LoggerFactory.getLogger(LessonController.class);

    public LessonController(EntityManagerFactory emf)
    {
        this.emf = emf;
        this.dao = new SkiLessonDAO(emf);
    }

    public void getAllLessons(Context ctx)
    {
        try
        {
            List<SkiLessonDTO> trips = dao.getAll();
            ctx.json(trips);
        } catch (DaoException e)
        {
            logger.error("Error fetching lessons.", e);
            throw new ApiException(500, "Something went wrong with the database. ", e);
        }
    }

    public void getLessonById(Context ctx)
    {
        try
        {
            Integer id = ctx.pathParamAsClass("id", Integer.class)
                    .check(i -> i > 0, "ID must be a positive integer")
                    .getOrThrow((validator) -> new IllegalArgumentException("ID must be a positive integer"));
            SkiLessonDTO lesson = dao.getById(id);
            ctx.json(lesson);
        } catch (IllegalArgumentException e)
        {
            logger.error("Illegal argument. ", e);
            throw new ApiException(400, "Invalid ID format", e);
        } catch (DaoException e)
        {
            logger.error("Error fetching lesson. ", e);
            throw new ApiException(404, "Lesson not found", e);
        }
    }

    public void createLesson(Context ctx)
    {
        try
        {
            SkiLessonDTO lessonInput = ctx.bodyAsClass(SkiLessonDTO.class);
            dao.create(lessonInput);
            ctx.status(201);
        } catch (IllegalArgumentException | DaoException e)
        {
            logger.error("Error creating lesson. ", e);
            throw new ApiException(400, "Invalid input data", e);
        }
    }

    public void updateLesson(Context ctx)
    {
        try
        {
            SkiLessonDTO lessonInput = ctx.bodyAsClass(SkiLessonDTO.class);
            Integer id = ctx.pathParamAsClass("id", Integer.class)
                    .check(i -> i > 0, "ID must be a positive integer")
                    .getOrThrow((validator) -> new IllegalArgumentException("ID must be a positive integer"));
            SkiLessonDTO updatedLesson = dao.update(lessonInput, id);
            ctx.json(updatedLesson);
        } catch (IllegalArgumentException | DaoException e)
        {
            logger.error("Error updating lesson. ", e);
            throw new ApiException(400, "Invalid input data", e);
        }
    }

    public void deleteLesson(Context ctx)
    {
        try
        {
            Integer id = ctx.pathParamAsClass("id", Integer.class)
                    .check(i -> i > 0, "ID must be a positive integer")
                    .getOrThrow((validator) -> new IllegalArgumentException("ID must be a positive integer"));
            dao.deleteSkiLesson(id);
            ctx.status(204);
        } catch (IllegalArgumentException | DaoException e)
        {
            logger.error("Error deleting lesson. ", e);
            throw new ApiException(400, "Invalid ID format", e);
        }
    }

    public void addInstructorToLesson(Context ctx)
    {
        try
        {
            Integer lessonId = ctx.pathParamAsClass("lessonId", Integer.class)
                    .check(i -> i > 0, "ID must be a positive integer")
                    .getOrThrow((validator) -> new IllegalArgumentException("Trip ID must be a positive integer"));
            Integer instructorId = ctx.pathParamAsClass("instructorId", Integer.class)
                    .check(i -> i > 0, "ID must be a positive integer")
                    .getOrThrow((validator) -> new IllegalArgumentException("Instructor ID must be a positive integer"));
            dao.addInstructorToSkiLesson(lessonId, instructorId);
            ctx.status(204);
        } catch (IllegalArgumentException | DaoException e)
        {
            logger.error("addInstructorToLesson failed. ", e);
            throw new ApiException(400, "Invalid input data", e);
        }
    }

    public void populate(Context ctx)
    {
        Populator populator = new Populator();
        populator.populate(emf);
        ctx.status(204);
    }

    public void getLessonsByLevel(Context ctx)
    {
        try
        {
            String level = ctx.pathParamAsClass("level", String.class)
                    .check(this::isValidLessonLevel, "Invalid level")
                    .getOrThrow((validator) -> new IllegalArgumentException("Level is missing or invalid"));
            List<SkiLessonDTO> trips = dao.getByLevel(level);
            ctx.json(trips);
        } catch (IllegalArgumentException e)
        {
            logger.error("Error getting lessons by level. ", e);
            throw new ApiException(400, "Invalid level format", e);
        }
    }


    public void getLessonsByInstructor(Context ctx)
    {
        try
        {
            Integer instructorId = ctx.pathParamAsClass("id", Integer.class)
                    .check(i -> i > 0, "ID must be a positive integer")
                    .getOrThrow((validator) -> new IllegalArgumentException("ID must be a positive integer"));
            Set<SkiLessonDTO> lessons = dao.getSkiLessonsByInstructor(instructorId);
            ctx.json(lessons);
        }
        catch (IllegalArgumentException e)
        {
            logger.error("Error getting lessons by instructor. ", e);
            throw new ApiException(400, "Invalid ID format", e);
        }

    }


    public List<ItemDTO> fetchPackingItems(LessonLevel lessonLevel)
    {
        try
        {
            DataAPIReader dataAPIReader = new DataAPIReader();
            String url = "https://packingapi.cphbusinessapps.dk/packinglist/" + lessonLevel.toString().toLowerCase();
            String jsonResponse = dataAPIReader.getDataFromClient(url);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            ItemsResponseDTO itemsResponse = objectMapper.readValue(jsonResponse, ItemsResponseDTO.class);
            return itemsResponse.getItems();
        } catch (JsonMappingException e)
        {
            throw new ApiException(500, "Error mapping JSON response to DTO", e);
        } catch (JsonProcessingException e)
        {
            throw new ApiException(500, "Error parsing JSON", e);
        }
    }

    private boolean isValidLessonLevel(String level)
    {
        try
        {
            LessonLevel.valueOf(level.toUpperCase());
            return true;
        } catch (IllegalArgumentException e)
        {
            return false;
        }
    }
}
