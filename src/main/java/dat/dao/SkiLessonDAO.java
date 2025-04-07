package dat.dao;

import dat.dto.SkiLessonDTO;
import dat.entities.Instructor;
import dat.entities.SkiLesson;
import dat.enums.LessonLevel;
import dat.exceptions.DaoException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SkiLessonDAO extends GenericDAO implements ISkiLessonInstructorDAO
{
    private final Logger logger = LoggerFactory.getLogger(SkiLessonDAO.class);

    public SkiLessonDAO(EntityManagerFactory emf)
    {
        super(emf);
    }

    public SkiLessonDTO create(SkiLessonDTO lessonInput) throws DaoException
    {
        SkiLesson entity = lessonInput.toEntity();
        // If the instructor is not null, fetch the instructor from the database
        if (lessonInput.getInstructor() != null)
        {
            Instructor instructor = super.getById(Instructor.class, lessonInput.getInstructor().getId());
            entity.setInstructor(instructor);
        }
        // Persist the SkiLesson entity
        entity = super.create(entity);

        // Return the SkiLesson DTO
        return new SkiLessonDTO(entity);
    }

    public SkiLessonDTO getById(Integer id) throws DaoException
    {
        SkiLesson skiLesson = super.getById(SkiLesson.class, id);

        return new SkiLessonDTO(skiLesson);
    }

    public List<SkiLessonDTO> getAll() throws DaoException
    {
        List<SkiLesson> skiLessons = super.getAll(SkiLesson.class);
        return skiLessons.stream().map(SkiLessonDTO::new).toList();
    }

    public SkiLessonDTO update(SkiLessonDTO lessonInput, int idToUpdate) throws DaoException
    {
        SkiLesson skiLesson = super.getById(SkiLesson.class, idToUpdate);
        if (skiLesson == null)
        {
            // Actually an DaoException is thrown if lesson not found
            return null;
        }
        // Update the lesson entity with the new values
        if (lessonInput.getName() != null && !lessonInput.getName().isEmpty())
        {
            skiLesson.setName(lessonInput.getName());
        }
        if (lessonInput.getPrice() != null)
        {
            skiLesson.setPrice(lessonInput.getPrice());
        }
        if (lessonInput.getLevel() != null)
        {
            skiLesson.setLevel(lessonInput.getLevel());
        }
        if (lessonInput.getStartTime() != null && !lessonInput.getStartTime().isEmpty())
        {
            skiLesson.setStartTime(LocalTime.parse(lessonInput.getStartTime()));
        }
        if (lessonInput.getEndTime() != null && !lessonInput.getEndTime().isEmpty())
        {
            skiLesson.setEndTime(LocalTime.parse(lessonInput.getEndTime()));
        }
        if (lessonInput.getLocation() != null)
        {
            skiLesson.getLocation().setDescription(lessonInput.getLocation().getDescription());
            skiLesson.getLocation().setLatitude(lessonInput.getLocation().getLatitude());
            skiLesson.getLocation().setLongitude(lessonInput.getLocation().getLongitude());
        }

        // If the instructor is not null, fetch the instructor from the database
        if (lessonInput.getInstructor() != null)
        {
            Instructor instructor = super.getById(Instructor.class, lessonInput.getInstructor().getId());
            // DaoException is thrown if instructor not found
            skiLesson.setInstructor(instructor);
        }

        // Persist the updated lesson entity
        skiLesson = super.update(skiLesson);

        // Return the updated lesson DTO
        return new SkiLessonDTO(skiLesson);
    }

    public void deleteSkiLesson(Integer id)
    {
        SkiLesson skiLesson = super.getById(SkiLesson.class, id);
        logger.info("Deleting trip: {}", skiLesson);
        if (skiLesson != null)
        {
            Instructor instructor = super.getById(Instructor.class, skiLesson.getInstructor().getId());
            instructor.removeLesson(skiLesson);
            super.update(instructor);
            super.delete(skiLesson);
        }
    }

    @Override
    public void addInstructorToSkiLesson(int lessonId, int instructorId) throws DaoException
    {
        // Fetch the lesson and instructor entities by ID
        SkiLesson skiLesson = super.getById(SkiLesson.class, lessonId);
        Instructor instructor = super.getById(Instructor.class, instructorId);
        if (skiLesson == null || instructor == null)
        {
            logger.error("Lesson with ID {} or Instructor with ID {} not found", lessonId, instructorId);
            return;
        }

        instructor.addLesson(skiLesson);
        // Persist the updated instructor entity
        super.update(instructor);
        // Persist the updated lesson entity
        super.update(skiLesson);
    }

    @Override
    public Set<SkiLessonDTO> getSkiLessonsByInstructor(int instructorId) throws DaoException
    {
        // Fetch the instructor entity by ID
        Instructor instructor = super.getById(Instructor.class, instructorId);
        if (instructor == null)
        {
            logger.error("Instructor with ID {} not found", instructorId);
            return null;
        }
        // Convert the set of Trip entities to a set of TripDTOs
        return instructor.getLessons().stream().map(SkiLessonDTO::new).collect(Collectors.toSet());
    }

    public List<SkiLessonDTO> getByLevel(String level) throws DaoException
    {
        try (EntityManager em = emf.createEntityManager())
        {
            List<SkiLesson> skiLessons = em.createQuery("SELECT t FROM SkiLesson t WHERE t.level = :lessonlevel", SkiLesson.class)
                    .setParameter("lessonlevel", LessonLevel.valueOf(level.toUpperCase()))
                    .getResultList();
            return skiLessons.stream().map(SkiLessonDTO::new).toList();
        }
        catch (Exception e)
        {
            logger.error("Error reading objects from db", e);
            throw new DaoException("Error reading objects from db", e);
        }
    }


}
