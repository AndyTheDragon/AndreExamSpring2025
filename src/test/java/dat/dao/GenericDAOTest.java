package dat.dao;

import dat.config.HibernateConfig;
import dat.dto.GuideDTO;
import dat.dto.TripDTO;
import dat.entities.*;
import dat.exceptions.DaoException;
import dat.utils.Populator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GenericDAOTest
{
    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
    private static final TripDAO genericDAO = new TripDAO(emf);
    private static Guide g1, g2;
    private static Trip t1, t2, t3, t4, t5;


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
    void getInstance()
    {
        assertNotNull(emf);
    }

    @Test
    void create()
    {
        // Arrange
        Guide g3 = new Guide();
        Trip t6 = new Trip();


        // Act
        Guide guideResult = genericDAO.create(g3);
        Trip tripResult = genericDAO.create(t6);

        // Assert
        assertThat(guideResult, samePropertyValuesAs(g3));
        assertNotNull(guideResult);
        assertThat(tripResult, samePropertyValuesAs(t6));
        assertNotNull(tripResult);
        try (EntityManager em = emf.createEntityManager())
        {
            Guide foundGuide = em.find(Guide.class, guideResult.getId());
            assertThat(foundGuide, samePropertyValuesAs(g3 ,"trips"));
            assertNotNull(foundGuide);
            Trip foundTrip = em.find(Trip.class, tripResult.getId());
            assertThat(foundTrip, samePropertyValuesAs(t6));

        }

    }

    @Test
    void read()
    {
        // Arrange
        Guide expected = g1;

        // Act
        Guide result = genericDAO.getById(Guide.class, g1.getId());

        // Assert
        assertThat(result, samePropertyValuesAs(expected, "trips"));
        //assertThat(result.getRooms(), containsInAnyOrder(expected.getRooms().toArray()));
    }

    @Test
    void read_notFound()
    {


        // Act
        DaoException exception = assertThrows(DaoException.class, () -> genericDAO.getById(Guide.class, 1000L));
        //Hotel result = genericDAO.read(Hotel.class, 1000L);

        // Assert
        assertThat(exception.getMessage(), is("Error reading object from db"));
    }

    @Test
    void findAll()
    {
        // Arrange
        List<Guide> expected = List.of(g1, g2);

        // Act
        List<Guide> result = genericDAO.getAll(Guide.class);

        // Assert
        assertNotNull(result);
        assertThat(result.size(), is(2));
        assertThat(result.get(0), samePropertyValuesAs(expected.get(0), "trips"));
        assertThat(result.get(1), samePropertyValuesAs(expected.get(1), "trips"));
    }

    @Test
    void update()
    {
        // Arrange
        g1.setFirstName("UpdatedName");

        // Act
        Guide result = genericDAO.update(g1);

        // Assert
        assertThat(result, samePropertyValuesAs(g1, "trips"));
        //assertThat(result.getRooms(), containsInAnyOrder(h1.getRooms()));

    }

    @Test
    void updateMany()
    {
        // Arrange
        g1.setFirstName("UpdatedName");
        g2.setFirstName( "UpdatedName");
        List<Guide> testEntities = List.of(g1, g2);

        // Act
        List<Guide> result = genericDAO.update(testEntities);

        // Assert
        assertNotNull(result);
        assertThat(result.size(), is(2));
        assertThat(result.get(0), samePropertyValuesAs(g1, "trips"));
        assertThat(result.get(1), samePropertyValuesAs(g2, "trips"));
    }

    @Test
    void delete()
    {
        // Act
        genericDAO.deleteTrip(t1.getId());

        // Assert
        try (EntityManager em = emf.createEntityManager())
        {
            Long amountInDb = em.createQuery("SELECT COUNT(t) FROM Trip t", Long.class).getSingleResult();
            assertThat(amountInDb, is(4L));
            Trip found = em.find(Trip.class, t1.getId());
            assertNull(found);
        }
    }

}