package dat.dao;

import dat.dto.InstructorDTO;
import dat.dto.TotalPriceDTO;
import dat.entities.Instructor;
import dat.exceptions.DaoException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

public class InstructorDAO extends GenericDAO
{
    private Logger logger = LoggerFactory.getLogger(InstructorDAO.class);
    public InstructorDAO(EntityManagerFactory emf)
    {
        super(emf);
    }

    public InstructorDTO create(InstructorDTO instructorInput) throws DaoException
    {
        // Create a new Instructor entity from the input DTO
        Instructor entity = new Instructor(instructorInput);
        // Persist the instructor entity
        entity = super.create(entity);
        // Return the instructor DTO
        return new InstructorDTO(entity);
    }

    public InstructorDTO getById(Integer id) throws DaoException
    {
        // Fetch the instructor entity by ID
        Instructor instructor = super.getById(Instructor.class, id);
        if (instructor == null)
        {
            // DaoException is thrown if instructor not found
            return null;
        }
        // Return the instructor DTO
        return new InstructorDTO(instructor);
    }

    public List<InstructorDTO> getAll()
    {
        // Fetch all instructor entities
        List<Instructor> instructors = super.getAll(Instructor.class);
        // Convert the list of Instructor entities to a list of GuideDTOs
        return instructors.stream().map(InstructorDTO::new).toList();
    }

    public InstructorDTO update(InstructorDTO guideInput, int idToUpdate)
    {
        // Fetch the existing instructor entity by ID
        Instructor instructor = super.getById(Instructor.class, idToUpdate);
        if (instructor == null)
        {
            return null;
        }
        // Update the instructor entity with new values
        if (guideInput.getFirstName() != null && !guideInput.getFirstName().isEmpty())
        {
            instructor.setFirstName(guideInput.getFirstName());
        }
        if (guideInput.getLastName() != null && !guideInput.getLastName().isEmpty())
        {
            instructor.setLastName(guideInput.getLastName());
        }
        if (guideInput.getEmail() != null && !guideInput.getEmail().isEmpty())
        {
            instructor.setEmail(guideInput.getEmail());
        }
        if (guideInput.getPhone() != null && !guideInput.getPhone().isEmpty())
        {
            instructor.setPhone(guideInput.getPhone());
        }
        if (guideInput.getYearsOfExperience() != null)
        {
            instructor.setYearsOfExperience(guideInput.getYearsOfExperience());
        }

        // Persist the updated entity
        super.update(instructor);

        // Return the updated instructor DTO
        return new InstructorDTO(instructor);
    }

    public void delete(int idToDelete)
    {
        // Fetch the instructor entity by ID
        Instructor instructor = super.getById(Instructor.class, idToDelete);
        if (instructor == null)
        {
            logger.error("Instructor with ID {} not found", idToDelete);
            return;
        }
        // Remove the instructor from any associated lessons
        instructor.getLessons().forEach(lesson -> lesson.setInstructor(null));
        super.update(instructor.getLessons());

        // Delete the instructor entity
        super.delete(instructor);
    }

    public Double getTotalSumPriceOfLessonsByInstructor(int instructorId)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            return em.createQuery("SELECT SUM(l.price) FROM SkiLesson l WHERE l.instructor.id = :instructorId", Double.class)
                    .setParameter("instructorId", instructorId)
                    .getSingleResult();
        }
        catch (Exception e)
        {
            logger.error("Error calculating total sum price of lessons for instructor with ID {}: {}", instructorId, e.getMessage());
            throw new DaoException("Error calculating total sum price of lessons for instructor with ID " + instructorId, e);
        }
    }

    public List<TotalPriceDTO> getTotalSumPriceOfLessonsByInstructor()
    {
        try (EntityManager em = emf.createEntityManager())
        {
            return em.createQuery("SELECT l.instructor.id, SUM(l.price) FROM SkiLesson l GROUP BY l.instructor.id", TotalPriceDTO.class)
                    .getResultList();
        }
        catch (Exception e)
        {
            logger.error("Error calculating total sum price of lessons for instructors: {}", e.getMessage());
            throw new DaoException("Error calculating total sum price of lessons for instructors", e);
        }
    }

}
