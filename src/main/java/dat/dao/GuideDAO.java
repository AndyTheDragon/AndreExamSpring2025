package dat.dao;

import dat.dto.GuideDTO;
import dat.entities.Guide;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GuideDAO extends GenericDAO
{
    private Logger logger = LoggerFactory.getLogger(GuideDAO.class);
    public GuideDAO(EntityManagerFactory emf)
    {
        super(emf);
    }

    public GuideDTO create(GuideDTO guideInput)
    {
        // Create a new Guide entity from the input DTO
        Guide entity = new Guide(guideInput);
        // Persist the guide entity
        entity = super.create(entity);
        // Return the guide DTO
        return new GuideDTO(entity);
    }

    public GuideDTO getById(Integer id)
    {
        // Fetch the guide entity by ID
        Guide guide = super.getById(Guide.class, id);
        if (guide == null)
        {
            return null;
        }
        // Return the guide DTO
        return new GuideDTO(guide);
    }

    public List<GuideDTO> getAll()
    {
        // Fetch all guide entities
        List<Guide> guides = super.getAll(Guide.class);
        // Convert the list of Guide entities to a list of GuideDTOs
        return guides.stream().map(GuideDTO::new).toList();
    }

    public GuideDTO update(GuideDTO guideInput, int idToUpdate)
    {
        // Fetch the existing guide entity by ID
        Guide guide = super.getById(Guide.class, idToUpdate);
        if (guide == null)
        {
            return null;
        }
        // Update the guide entity with new values
        if (guideInput.getFirstName() != null && !guideInput.getFirstName().isEmpty())
        {
            guide.setFirstName(guideInput.getFirstName());
        }
        if (guideInput.getLastName() != null && !guideInput.getLastName().isEmpty())
        {
            guide.setLastName(guideInput.getLastName());
        }
        if (guideInput.getEmail() != null && !guideInput.getEmail().isEmpty())
        {
            guide.setEmail(guideInput.getEmail());
        }
        if (guideInput.getPhone() != null && !guideInput.getPhone().isEmpty())
        {
            guide.setPhone(guideInput.getPhone());
        }
        if (guideInput.getYearsOfExperience() != null)
        {
            guide.setYearsOfExperience(guideInput.getYearsOfExperience());
        }

        // Persist the updated entity
        super.update(guide);

        // Return the updated guide DTO
        return new GuideDTO(guide);
    }

    public void delete(int idToDelete)
    {
        // Fetch the guide entity by ID
        Guide guide = super.getById(Guide.class, idToDelete);
        if (guide == null)
        {
            logger.error("Guide with ID {} not found", idToDelete);
            return;
        }
        // Delete the guide entity
        super.delete(guide);
    }
}
