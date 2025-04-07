package dat.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dat.dao.InstructorDAO;
import dat.dao.SkiLessonDAO;
import dat.dto.*;
import dat.enums.LessonLevel;
import dat.exceptions.ApiException;
import dat.exceptions.DaoException;
import dat.utils.DataAPIReader;
import dat.utils.Populator;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class LessonController
{
    private final EntityManagerFactory emf;
    private final SkiLessonDAO dao;
    private final InstructorDAO instructorDao;
    private final Logger logger = LoggerFactory.getLogger(LessonController.class);

    public LessonController(EntityManagerFactory emf)
    {
        this.emf = emf;
        this.dao = new SkiLessonDAO(emf);
        this.instructorDao = new InstructorDAO(emf);
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
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            ObjectNode lessonJson = mapper.valueToTree(lesson);
            Boolean includeInstructions = ctx.queryParamAsClass("includeInstructions", Boolean.class)
                    .check(i -> i != null, "Include instructions is missing")
                    .getOrDefault(false);
            if (includeInstructions)
            {
                List<SkiInstructionDTO> instructions = fetchInstructionData(lesson.getLevel());
                ArrayNode instructionsArray = mapper.valueToTree(instructions);
                lessonJson.set("instructions", instructionsArray);
            }
            ctx.json(lessonJson);
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

    public void getTotalSumPriceOfLessonsByInstructor(Context ctx)
    {
        try
        {
            Integer instructorId = ctx.pathParamAsClass("id", Integer.class)
                    .check(i -> i > 0, "ID must be a positive integer")
                    .getOrThrow((validator) -> new IllegalArgumentException("ID must be a positive integer"));
            Double totalSum = instructorDao.getTotalSumPriceOfLessonsByInstructor(instructorId);
            ctx.json(totalSum);
        }
        catch (IllegalArgumentException e)
        {
            logger.error("Error getting total sum of lessons by instructor. ", e);
            throw new ApiException(400, "Invalid ID format", e);
        }
    }

    public void getTotalSumPriceOfLessons(Context ctx)
    {
        try
        {
            List<TotalPriceDTO> totalSum = instructorDao.getTotalSumPriceOfLessonsByInstructor();
            ctx.json(totalSum);
        } catch (Exception e)
        {
            logger.error("Error getting total sum of lessons. ", e);
            throw new ApiException(500, "Something went wrong with the database. ", e);
        }
    }

    public void getSumOfLessonTimesByInstructor(Context ctx)
    {
        try
        {
            Integer instructorId = ctx.pathParamAsClass("id", Integer.class)
                    .check(i -> i > 0, "ID must be a positive integer")
                    .getOrThrow((validator) -> new IllegalArgumentException("ID must be a positive integer"));
            Duration totalSum = dao.getSkiLessonsByInstructor(instructorId).stream()
                    .map(l->Duration.between(LocalTime.parse(l.getStartTime()),LocalTime.parse(l.getEndTime())))
                    .reduce(Duration.ZERO, Duration::plus);

            ctx.json(totalSum);
        }
        catch (IllegalArgumentException e)
        {
            logger.error("Error getting total sum of lessons by instructor. ", e);
            throw new ApiException(400, "Invalid ID format", e);
        }
    }


    public void getSumOfLessonTimes(Context ctx)
    {
        try
        {
            List<SkiLessonDTO> lessons = dao.getAll();
            Map<Integer, Duration> totalSum = lessons.stream()
                    .collect(Collectors.groupingBy(l->l.getInstructor().getId(),
                            Collectors.reducing(Duration.ZERO,
                                    l -> Duration.between(LocalTime.parse(l.getStartTime()), LocalTime.parse(l.getEndTime())),
                                    Duration::plus)
                    ));
            List<TotalLessonTimeDTO> totalSumList = totalSum.entrySet().stream()
                    .map(entry -> new TotalLessonTimeDTO(entry.getKey(), entry.getValue().toHours()))
                    .toList();
            ctx.json(totalSumList);
        } catch (Exception e)
        {
            logger.error("Error getting total sum of lessons. ", e);
            throw new ApiException(500, "Something went wrong. ", e);
        }
    }

    public List<SkiInstructionDTO> fetchInstructionData(LessonLevel lessonLevel)
    {
        try
        {
            DataAPIReader dataAPIReader = new DataAPIReader();
            String url = "https://apiprovider.cphbusinessapps.dk/skilesson/" + lessonLevel.toString().toLowerCase();
            String jsonResponse = dataAPIReader.getDataFromClient(url);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            SkiInstructionResponseDTO itemsResponse = objectMapper.readValue(jsonResponse, SkiInstructionResponseDTO.class);
            return itemsResponse.getLessons();
        } catch (JsonMappingException e)
        {
            logger.error("Error fetching instruction data. ", e);
            throw new ApiException(500, "Error mapping JSON response to DTO", e);
        } catch (JsonProcessingException e)
        {
            logger.error("Error fetching instruction data. ", e);
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
