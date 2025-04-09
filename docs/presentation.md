Jeg har lavet et REST API der kan vise informationer om ski instruktører og ski lektioner med følgende endpoints
# Endpoints
| Method | Route                                             | Description                                             | Task # |
|--------|---------------------------------------------------|---------------------------------------------------------|--------|
| GET    | /skilessons                                       | Get all ski lessons.                                    | 3.3.1  |
| GET    | /skilessons/{id}?withInstructions=[true/false]    | Get a ski lesson by its id.                             | 6.3    |
| POST   | /skilessons                                       | Create a new ski lesson. Add instructor later.          | 3.3.1  |
| PUT    | /skilessons/{id}                                  | Update information about a ski lesson.                  | 3.3.1  |
| DELETE | /skilessons/{id}                                  | Delete a ski lesson.                                    | 3.3.1  |
| PUT    | /skilessons/{lessonId}/instructors/{instructorId} | Add an existing instructor to an existing ski lesson.   | 3.3.1  |
| POST   | /skilessons/populate                              | Populate the database with ski lessons and instructors. | 3.3.1  |
| GET    | /skilessons/level/{level}                         | Get ski lessons by level                                | 5.1    |
| GET    | /instructors/{id}/skilessons                      | Get ski lessons by instructor                           | 2.4.4  |
| GET    | /instructors/{id}/totalsumprice                   | Get the total sum price of lessons by instructor        |        |                                                   
| GET    | /instructors/totalsumprice                        | Get the total sum price of all lessons                  | 5.2    |
| GET    | /instructors/{id}/totalsumduration                | Get the sum of lesson times by instructor               |        |                                                |                                                 
| GET    | /instructors/totalsumduration                     | Get the sum of all lesson times                         | 5.2    |
| POST   | /auth/login                                       | Authenticate a user                                     | 8.1    |
| POST   | /auth/register                                    | Register a new user                                     | 8.1    |

### Manglende implementeringer
* Task 6.4 - Total duration of instructions for a ski lesson based on level
* Task 8.3 - Auth token i Rest Assured Test

## Bidirectinal relation i SkiLesson og Instructor
Jeg har valgt at instructor klassen indeholder en liste af alle de lektioner som instruktøren er tilknyttet.
Se [`Instructor`](../src/main/java/dat/entities/Instructor.java) klassen i `entities` pakken.

## Position er en embedded class i SkiLesson
Se [`SkiLesson`](../src/main/java/dat/entities/SkiLesson.java) klassen i `entities` pakken.

## Hvorfor PUT i stedet for POST i add instructor to ski lesson endpoint
Jeg mener at det mest korrekte vil være en PATCH.  
**POST** bruges til at submitte data til serveren, som vil skabe en ny ressource, eller tilføje til en eksisterende ressource.  
**PUT** bruges til at skabe en ny ressource eller erstatte en eksisterende ressource.  
**PATCH** bruges til at opdatere en eksisterende ressource med nye data.

Når vi tilføjer en instruktør til en lesson (og derved også tilføjer lektionen til instruktørens liste af lektioner) så opdaterer vi værdien i instruktør feltet. Det er altså kun en del af eniteten som ændres, ikke hele SkiLesson entiteten som overskrives.

## Error Handling
Jeg har brugt to Exception Handlers til at gribe exceptions og konvertere dem til JSON fejlmeddelser med HTTP statuskoder. Se [`ApplicationConfig`](../src/main/java/dat/config/ApplicationConfig.java) klassen i `config` pakken.
Jeg har en som griber ApiExceptions, en custom exception som jeg bruger til at kaste fejl i controllerne, og en som griber alle andre exceptions.

I Controlleren var det min intention at gribe forskellige slags Exceptions og så konvertere dem til ApiExceptions med relevante fejlmeddelelser og statuskoder. Nogen steder er det gået godt, andre steder er det ikke helt lykkedes. Se f.eks. [`SkiLessonController`](../src/main/java/dat/controllers/LessonController.java) klassen i `controllers` pakken. Se f.eks.
* getByLevel()
* getLessonById()

## Refactor fetchInstructionData ud i en service klasse
Hvis jeg havde haft mere tid vill jeg gerne have refactored fetchInstructionData ud i en service klasse, for at holde controlleren fri for forretningslogik. Tilsvarende ville jeg flytte koden i getLessonById ud i DAO klassen.

## Refactor ud i InstructorController
Hvis jeg havde haft mere tid ville jeg lave en InstructorController, hvor jeg flyttede de relevante endpoints hen.

