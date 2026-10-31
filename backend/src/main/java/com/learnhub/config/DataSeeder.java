package com.learnhub.config;

import com.learnhub.dto.CourseDtos.CreateCourseRequest;
import com.learnhub.dto.CourseDtos.LessonRequest;
import com.learnhub.dto.CourseDtos.QuestionRequest;
import com.learnhub.dto.CourseDtos.QuizRequest;
import com.learnhub.model.Role;
import com.learnhub.model.User;
import com.learnhub.repository.UserRepository;
import com.learnhub.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/** Inserts demo users and courses the first time the app starts with an empty database. */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CourseService courseService;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder, CourseService courseService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.courseService = courseService;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        User instructor = userRepository.save(new User("Dr. Anita Sharma", "instructor@learnhub.com",
                passwordEncoder.encode("password123"), Role.INSTRUCTOR));
        userRepository.save(new User("Ravi Kumar", "student@learnhub.com",
                passwordEncoder.encode("password123"), Role.STUDENT));

        courseService.create(new CreateCourseRequest(
                "React JS for Beginners",
                "Learn to build modern, component-based user interfaces with React. Covers JSX, components, props, state, hooks and routing.",
                "Web Development", "Beginner", 12,
                List.of(
                        new LessonRequest("Introduction to React",
                                "React is a JavaScript library for building user interfaces. It lets you compose complex UIs from small, isolated pieces of code called components. React uses a virtual DOM to update only the parts of the page that change.", 15),
                        new LessonRequest("Components and Props",
                                "Components are reusable building blocks. A function component receives 'props' (properties) as its argument and returns JSX. Props flow from parent to child and are read-only inside the child.", 20),
                        new LessonRequest("State and the useState Hook",
                                "State is data that changes over time. The useState hook returns the current value and a setter function: const [count, setCount] = useState(0). Calling the setter re-renders the component.", 25),
                        new LessonRequest("Side Effects with useEffect",
                                "useEffect runs code after render, such as fetching data from an API. Its dependency array controls when the effect runs again: [] means only once after the first render.", 25)),
                new QuizRequest("React Fundamentals Quiz", List.of(
                        new QuestionRequest("What is React mainly used for?",
                                List.of("Building user interfaces", "Managing databases", "Writing server code", "Styling pages"), 0),
                        new QuestionRequest("How does a parent pass data to a child component?",
                                List.of("Through state", "Through props", "Through the DOM", "Through CSS"), 1),
                        new QuestionRequest("Which hook adds state to a function component?",
                                List.of("useEffect", "useContext", "useState", "useRef"), 2),
                        new QuestionRequest("An empty dependency array in useEffect means the effect runs...",
                                List.of("On every render", "Never", "Only once after the first render", "Before rendering"), 2)))),
                instructor);

        courseService.create(new CreateCourseRequest(
                "Spring MVC and REST APIs",
                "Build robust server-side applications with the Spring Framework. Understand the MVC architecture, controllers, services, repositories and security.",
                "Backend Development", "Intermediate", 16,
                List.of(
                        new LessonRequest("The MVC Architecture",
                                "MVC separates an application into Model (data), View (presentation) and Controller (request handling). In Spring MVC, the DispatcherServlet receives every request and routes it to the right controller method.", 20),
                        new LessonRequest("Controllers and Request Mapping",
                                "@RestController classes handle HTTP requests. @GetMapping, @PostMapping and friends map URLs to methods. @PathVariable and @RequestBody bind request data to method parameters.", 25),
                        new LessonRequest("Services and Repositories",
                                "Business logic lives in @Service classes. Data access is done through Spring Data JPA repositories, which generate SQL queries from method names such as findByEmail.", 25),
                        new LessonRequest("Securing APIs with Spring Security",
                                "Spring Security protects endpoints with a filter chain. Stateless APIs commonly use JWT tokens: the client logs in once, receives a token and sends it in the Authorization header.", 30)),
                new QuizRequest("Spring MVC Quiz", List.of(
                        new QuestionRequest("Which component receives every request in Spring MVC?",
                                List.of("DispatcherServlet", "EntityManager", "ApplicationContext", "JdbcTemplate"), 0),
                        new QuestionRequest("Which annotation marks a class that handles REST requests?",
                                List.of("@Service", "@Repository", "@RestController", "@Entity"), 2),
                        new QuestionRequest("Where should business logic usually live?",
                                List.of("In the controller", "In a @Service class", "In the database", "In the view"), 1)))),
                instructor);

        courseService.create(new CreateCourseRequest(
                "MySQL Database Essentials",
                "Design and query relational databases with MySQL. Learn tables, keys, joins, indexes and normalization through practical examples.",
                "Databases", "Beginner", 10,
                List.of(
                        new LessonRequest("Relational Databases and Tables",
                                "A relational database stores data in tables made of rows and columns. Each table should have a primary key that uniquely identifies every row.", 15),
                        new LessonRequest("Writing SQL Queries",
                                "SELECT reads data, INSERT adds rows, UPDATE changes rows and DELETE removes them. WHERE filters rows and ORDER BY sorts the result.", 25),
                        new LessonRequest("Joins and Relationships",
                                "Foreign keys link tables together. An INNER JOIN returns rows that have matches in both tables; a LEFT JOIN keeps all rows from the left table.", 25)),
                new QuizRequest("MySQL Basics Quiz", List.of(
                        new QuestionRequest("Which SQL statement reads data from a table?",
                                List.of("INSERT", "SELECT", "UPDATE", "DROP"), 1),
                        new QuestionRequest("What uniquely identifies each row in a table?",
                                List.of("Foreign key", "Index", "Primary key", "View"), 2),
                        new QuestionRequest("Which join keeps all rows from the left table?",
                                List.of("INNER JOIN", "LEFT JOIN", "CROSS JOIN", "SELF JOIN"), 1)))),
                instructor);

        courseService.create(new CreateCourseRequest(
                "Java Object-Oriented Programming",
                "Master the core ideas of object-oriented programming in Java: classes, objects, inheritance, polymorphism, abstraction and encapsulation.",
                "Programming", "Beginner", 14,
                List.of(
                        new LessonRequest("Classes and Objects",
                                "A class is a blueprint; an object is an instance created from it with the new keyword. Fields hold state and methods define behaviour.", 20),
                        new LessonRequest("Inheritance and Polymorphism",
                                "A subclass extends a parent class and inherits its members. Polymorphism lets a parent-type reference call the overridden method of the actual object at runtime.", 25),
                        new LessonRequest("Encapsulation and Abstraction",
                                "Encapsulation hides fields behind private access and exposes getters/setters. Abstraction uses abstract classes and interfaces to define what an object does, not how.", 25)),
                new QuizRequest("OOP Concepts Quiz", List.of(
                        new QuestionRequest("Which keyword creates a new object?",
                                List.of("class", "new", "this", "static"), 1),
                        new QuestionRequest("Hiding fields with private access is called...",
                                List.of("Inheritance", "Polymorphism", "Encapsulation", "Overloading"), 2),
                        new QuestionRequest("Which keyword makes a class inherit from another?",
                                List.of("implements", "extends", "inherits", "super"), 1)))),
                instructor);

        log.info("Demo data created. Logins: student@learnhub.com / password123, instructor@learnhub.com / password123");
    }
}
