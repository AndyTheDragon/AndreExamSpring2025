package dat.dao;

import dat.config.HibernateConfig;
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
    private static final GenericDAO dao = new SkiLessonDAO(emf);
    private static Instructor g1, g2;
    private static SkiLesson l1, l2, l3, l4, l5;


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
    void getInstance()
    {
        assertNotNull(emf);
    }

    @Test
    void create()
    {
        // Arrange
        Instructor g3 = new Instructor();
        SkiLesson t6 = new SkiLesson();


        // Act
        Instructor instructorResult = dao.create(g3);
        SkiLesson skiLessonResult = dao.create(t6);

        // Assert
        assertThat(instructorResult, samePropertyValuesAs(g3));
        assertNotNull(instructorResult);
        assertThat(skiLessonResult, samePropertyValuesAs(t6));
        assertNotNull(skiLessonResult);
        try (EntityManager em = emf.createEntityManager())
        {
            Instructor foundInstructor = em.find(Instructor.class, instructorResult.getId());
            assertThat(foundInstructor, samePropertyValuesAs(g3 ,"trips"));
            assertNotNull(foundInstructor);
            SkiLesson foundSkiLesson = em.find(SkiLesson.class, skiLessonResult.getId());
            assertThat(foundSkiLesson, samePropertyValuesAs(t6));

        }

    }

    @Test
    void getById()
    {
        // Arrange
        Instructor expected = g1;

        // Act
        Instructor result = dao.getById(Instructor.class, g1.getId());

        // Assert
        assertThat(result, samePropertyValuesAs(expected, "lessons"));
        //assertThat(result.getRooms(), containsInAnyOrder(expected.getRooms().toArray()));
    }

    @Test
    void read_notFound()
    {


        // Act
        DaoException exception = assertThrows(DaoException.class, () -> dao.getById(Instructor.class, 1000L));
        //Hotel result = genericDAO.read(Hotel.class, 1000L);

        // Assert
        assertThat(exception.getMessage(), is("Error reading object from db"));
    }

    @Test
    void findAll()
    {
        // Arrange
        List<Instructor> expected = List.of(g1, g2);

        // Act
        List<Instructor> result = dao.getAll(Instructor.class);

        // Assert
        assertNotNull(result);
        assertThat(result.size(), is(2));
        assertThat(result.get(0), samePropertyValuesAs(expected.get(0), "lessons"));
        assertThat(result.get(1), samePropertyValuesAs(expected.get(1), "lessons"));
    }

    @Test
    void update()
    {
        // Arrange
        g1.setFirstName("UpdatedName");

        // Act
        Instructor result = dao.update(g1);

        // Assert
        assertThat(result, samePropertyValuesAs(g1, "lessons"));
        //assertThat(result.getRooms(), containsInAnyOrder(h1.getRooms()));

    }

    @Test
    void updateMany()
    {
        // Arrange
        g1.setFirstName("UpdatedName");
        g2.setFirstName( "UpdatedName");
        List<Instructor> testEntities = List.of(g1, g2);

        // Act
        List<Instructor> result = dao.update(testEntities);

        // Assert
        assertNotNull(result);
        assertThat(result.size(), is(2));
        assertThat(result.get(0), samePropertyValuesAs(g1, "lessons"));
        assertThat(result.get(1), samePropertyValuesAs(g2, "lessons"));
    }

    @Test
    void delete()
    {
        // Arrange
        SkiLessonDAO lessonDao = (SkiLessonDAO)dao;

        // Act
        lessonDao.deleteSkiLesson(l1.getId());

        // Assert
        try (EntityManager em = emf.createEntityManager())
        {
            Long amountInDb = em.createQuery("SELECT COUNT(t) FROM SkiLesson t", Long.class).getSingleResult();
            assertThat(amountInDb, is(4L));
            SkiLesson found = em.find(SkiLesson.class, l1.getId());
            assertNull(found);
        }
    }

}