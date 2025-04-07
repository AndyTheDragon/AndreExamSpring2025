package dat.utils;

import dat.entities.Guide;
import dat.entities.Trip;
import dat.enums.TripCategory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;

public class Populator
{
    private Logger logger = LoggerFactory.getLogger(Populator.class);

    private Guide guide1, guide2;
    private Trip trip1, trip2, trip3, trip4, trip5;

    public Populator()
    {
        guide1 = new Guide(null,
                "Jesper",
                "Jesperson",
                "jesper@cph.dk",
                "44554455",
                12, new HashSet<>());
        guide2 = new Guide(null,
                "Jon",
                "",
                "jon@cph.dk",
                "22332233",
                15, new HashSet<>());
        trip1 = new Trip("Trip to the mountains",
                600.0,
                TripCategory.FOREST,
                LocalTime.of(8,0),
                LocalTime.of(19,30),
                new Trip.Position("Copenhagen", 55.6761, 12.5683));
        trip2 = new Trip("Trip to the beach",
                300.0,
                TripCategory.BEACH,
                LocalTime.of(9,0),
                LocalTime.of(18,0),
                new Trip.Position("Copenhagen", 55.6761, 12.5683));
        trip3 = new Trip("Trip to the city",
                200.0,
                TripCategory.CITY,
                LocalTime.of(10,0),
                LocalTime.of(17,0),
                new Trip.Position("Copenhagen", 55.6761, 12.5683));
        trip4 = new Trip("Trip to Silkeborgsøerne",
                400.0,
                TripCategory.LAKE,
                LocalTime.of(6,43),
                LocalTime.of(17,03),
                new Trip.Position("Silkeborg", 56.1629, 9.5459));
        trip5 = new Trip("Trip to the mountains",
                1600.0,
                TripCategory.SNOW,
                LocalTime.of(5,15),
                LocalTime.of(21,30),
                new Trip.Position("Amager Bakke", 55.6759, 12.5655));

    }

    public List<Guide> getGuides()
    {
        return List.of(guide1, guide2);
    }

    public List<Trip> getTrips()
    {
        return List.of(trip1, trip2, trip3, trip4, trip5);
    }

    public void populate(EntityManagerFactory emf)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Trip ").executeUpdate();
            em.createQuery("DELETE FROM Guide ").executeUpdate();

            em.persist(guide1);
            em.persist(guide2);
            guide1.addTrip(trip1);
            guide1.addTrip(trip2);
            guide1.addTrip(trip3);
            guide2.addTrip(trip4);
            guide2.addTrip(trip5);
            em.persist(trip1);
            em.persist(trip2);
            em.persist(trip3);
            em.persist(trip4);
            em.persist(trip5);
            guide1 = em.merge(guide1);
            guide2 = em.merge(guide2);

            em.getTransaction().commit();
        }
        catch (Exception e)
        {
            logger.error("Error populating database", e);
        }
    }


}
