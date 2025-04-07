package dat.utils;

import dat.entities.Instructor;
import dat.entities.SkiLesson;
import dat.enums.LessonLevel;
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

    private Instructor instructor1, instructor2;
    private SkiLesson skiLesson1, skiLesson2, skiLesson3, skiLesson4, skiLesson5;

    public Populator()
    {
        instructor1 = new Instructor(null,
                "Jesper",
                "Jesperson",
                "jesper@cph.dk",
                "44554455",
                12, new HashSet<>());
        instructor2 = new Instructor(null,
                "Jon",
                "",
                "jon@cph.dk",
                "22332233",
                15, new HashSet<>());
        skiLesson1 = new SkiLesson("Trip to the mountains",
                600.0,
                LessonLevel.INTERMEDIATE,
                LocalTime.of(8,0),
                LocalTime.of(19,30),
                new SkiLesson.Position("Copenhagen", 55.6761, 12.5683));
        skiLesson2 = new SkiLesson("Trip to the beach",
                300.0,
                LessonLevel.BEGINNER,
                LocalTime.of(9,0),
                LocalTime.of(18,0),
                new SkiLesson.Position("Copenhagen", 55.6761, 12.5683));
        skiLesson3 = new SkiLesson("Trip to the city",
                200.0,
                LessonLevel.INTERMEDIATE,
                LocalTime.of(10,0),
                LocalTime.of(17,0),
                new SkiLesson.Position("Copenhagen", 55.6761, 12.5683));
        skiLesson4 = new SkiLesson("Trip to Silkeborgsøerne",
                400.0,
                LessonLevel.ADVANCED,
                LocalTime.of(6,43),
                LocalTime.of(17,03),
                new SkiLesson.Position("Silkeborg", 56.1629, 9.5459));
        skiLesson5 = new SkiLesson("Trip to the mountains",
                1600.0,
                LessonLevel.BEGINNER,
                LocalTime.of(5,15),
                LocalTime.of(21,30),
                new SkiLesson.Position("Amager Bakke", 55.6759, 12.5655));

    }

    public List<Instructor> getInstructors()
    {
        return List.of(instructor1, instructor2);
    }

    public List<SkiLesson> getLessons()
    {
        return List.of(skiLesson1, skiLesson2, skiLesson3, skiLesson4, skiLesson5);
    }

    public void populate(EntityManagerFactory emf)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM SkiLesson ").executeUpdate();
            em.createQuery("DELETE FROM Instructor ").executeUpdate();

            em.persist(instructor1);
            em.persist(instructor2);
            instructor1.addLesson(skiLesson1);
            instructor1.addLesson(skiLesson2);
            instructor1.addLesson(skiLesson3);
            instructor2.addLesson(skiLesson4);
            instructor2.addLesson(skiLesson5);
            em.persist(skiLesson1);
            em.persist(skiLesson2);
            em.persist(skiLesson3);
            em.persist(skiLesson4);
            em.persist(skiLesson5);
            instructor1 = em.merge(instructor1);
            instructor2 = em.merge(instructor2);

            em.getTransaction().commit();
        }
        catch (Exception e)
        {
            logger.error("Error populating database", e);
        }
    }


}
