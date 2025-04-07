package dat.dao;

import dat.dto.TotalPriceDTO;
import dat.dto.TripDTO;
import dat.dto.TripInputDTO;
import dat.entities.Guide;
import dat.entities.Trip;
import dat.exceptions.DaoException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TripDAO extends GenericDAO implements ITripGuideDAO
{
    private final Logger logger = LoggerFactory.getLogger(TripDAO.class);

    public TripDAO(EntityManagerFactory emf)
    {
        super(emf);
    }

    public TripDTO create(TripInputDTO tripInput)
    {
        Trip entity = new Trip(tripInput);
        // If the guide is not null, fetch the guide from the database
        if (tripInput.getGuideId() != null)
        {
            Guide guide = super.getById(Guide.class, tripInput.getGuideId());
            entity.setGuide(guide);
        }
        // Persist the trip entity
        entity = super.create(entity);

        // Return the trip DTO
        return new TripDTO(entity);
    }

    public TripDTO getById(Integer id)
    {
        Trip trip = super.getById(Trip.class, id);
        if (trip == null)
        {
            return null;
        }
        return new TripDTO(trip);
    }

    public List<TripDTO> getAll()
    {
        List<Trip> trips = super.getAll(Trip.class);
        return trips.stream().map(TripDTO::new).toList();
    }

    public TripDTO update(TripInputDTO tripInput, int idToUpdate)
    {
        Trip trip = super.getById(Trip.class, idToUpdate);
        if (trip == null)
        {
            return null;
        }
        // Update the trip entity with the new values
        if (tripInput.getName() != null && !tripInput.getName().isEmpty())
        {
            trip.setName(tripInput.getName());
        }
        if (tripInput.getPrice() != null)
        {
            trip.setPrice(tripInput.getPrice());
        }
        if (tripInput.getCategory() != null)
        {
            trip.setCategory(tripInput.getCategory());
        }
        if (tripInput.getStartTime() != null && !tripInput.getStartTime().isEmpty())
        {
            trip.setStartTime(LocalTime.parse(tripInput.getStartTime()));
        }
        if (tripInput.getEndTime() != null && !tripInput.getEndTime().isEmpty())
        {
            trip.setEndTime(LocalTime.parse(tripInput.getEndTime()));
        }
        if (tripInput.getStartPosition() != null)
        {
            trip.getStartPosition().setDescription(tripInput.getStartPosition().getDescription());
            trip.getStartPosition().setLatitude(tripInput.getStartPosition().getLatitude());
            trip.getStartPosition().setLongitude(tripInput.getStartPosition().getLongitude());
        }

        // If the guide is not null, fetch the guide from the database
        if (tripInput.getGuideId() != null)
        {
            Guide guide = super.getById(Guide.class, tripInput.getGuideId());
            trip.setGuide(guide);
        }

        // Persist the updated trip entity
        trip = super.update(trip);

        // Return the updated trip DTO
        return new TripDTO(trip);
    }

    public void deleteTrip(Integer id)
    {
        Trip trip = super.getById(Trip.class, id);
        logger.info("Deleting trip: {}", trip);
        if (trip != null)
        {
            Guide guide = super.getById(Guide.class, trip.getGuide().getId());
            guide.removeTrip(trip);
            super.update(guide);
            super.delete(trip);
        }
    }

    @Override
    public void addGuideToTrip(int tripId, int guideId)
    {
        // Fetch the trip and guide entities by ID
        Trip trip = super.getById(Trip.class, tripId);
        Guide guide = super.getById(Guide.class, guideId);
        if (trip == null || guide == null)
        {
            logger.error("Trip with ID {} or Guide with ID {} not found", tripId, guideId);
            return;
        }
        // Add the trip to the guide's list of trips
        guide.addTrip(trip);

        // Persist the updated guide entity
        super.update(guide);
        // Persist the updated trip entity
        super.update(trip);
    }

    @Override
    public Set<TripDTO> getTripsByGuide(int guideId)
    {
        // Fetch the guide entity by ID
        Guide guide = super.getById(Guide.class, guideId);
        if (guide == null)
        {
            logger.error("Guide with ID {} not found", guideId);
            return null;
        }
        // Convert the set of Trip entities to a set of TripDTOs
        return guide.getTrips().stream().map(TripDTO::new).collect(Collectors.toSet());
    }

    public List<TripDTO> getByCategory(String category)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            List<Trip> trips = em.createQuery("SELECT t FROM Trip t WHERE t.category = :category", Trip.class)
                    .setParameter("category", category.toUpperCase())
                    .getResultList();
            return trips.stream().map(TripDTO::new).toList();
        }
        catch (Exception e)
        {
            logger.error("Error reading objects from db", e);
            throw new DaoException("Error reading objects from db", e);
        }
    }

    public List<TotalPriceDTO> getGuidesTotalPrice()
    {
        try (EntityManager em = emf.createEntityManager())
        {
            List<TotalPriceDTO> totalPriceList = em.createQuery("SELECT g.id AS guideId, SUM(t.price) AS totalPrice " +
                            "FROM Trip t JOIN t.guide g GROUP BY g.id", TotalPriceDTO.class)
                    .getResultList();
            return totalPriceList;
        }
        catch (Exception e)
        {
            logger.error("Error getting price sums from db", e);
            throw new DaoException("Error getting price sums from db", e);
        }
    }
}
