# LearnHub – Online Learning Platform

Full-stack case study:

| Layer    | Technology                                                    |
|----------|---------------------------------------------------------------|
| Frontend | **React JS** 18 (Vite, React Router) – reusable components     |
| Backend  | **Spring Framework – Spring MVC** (Spring Boot 3, Spring Security + JWT, Spring Data JPA) |
| Database | **MySQL** (stores user profiles, course data, learning progress) |

> **No MySQL installed?** The backend runs by default on an embedded **H2 database in MySQL
> compatibility mode** (nothing to install). When you install MySQL, switch with one flag –
> see [Using real MySQL](#using-real-mysql). The code doesn't change.

---

## Features

- **User authentication** – register / login with BCrypt-hashed passwords and JWT tokens; two roles: Student and Instructor.
- **Course listings** – search and filter by category.
- **Course enrollment management** – enroll / leave a course; lesson content is locked until you enroll.
- **Learning progress** – mark lessons complete; progress % is stored per enrollment.
- **Interactive quizzes** – one question at a time, server-side scoring, answer review, best score tracking.
- **User dashboards** – stats, enrolled courses with progress bars, recent quiz attempts; instructors also see the courses they teach.
- **Instructors** can create courses (lessons + quiz) and delete their own courses.

## Project structure

```
FSD_Case_Study/
├── backend/                         Spring Boot / Spring MVC
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/learnhub/
│       │   ├── controller/          @RestController – HTTP layer (the "C" in MVC)
│       │   ├── service/             Business logic (auth, courses, enrollment, quizzes, dashboard)
│       │   ├── repository/          Spring Data JPA repositories (database access)
│       │   ├── model/               JPA entities = MySQL tables (the "M" in MVC)
│       │   ├── dto/                 Request / response objects (JSON "views")
│       │   ├── security/            JWT generation + request filter
│       │   ├── config/              Security configuration, demo data seeder
│       │   └── exception/           Global error handling
│       └── resources/
│           ├── application.properties         default (H2 in MySQL mode)
│           └── application-mysql.properties   real MySQL
├── frontend/                        React JS (Vite)
│   └── src/
│       ├── components/              Reusable UI: CourseCard, CourseList, QuizPlayer, LessonList,
│       │                            ProgressBar, StatCard, Navbar, ProtectedRoute
│       ├── pages/                   Courses, CourseDetail, Quiz, Dashboard, Login, Register, CreateCourse
│       ├── context/AuthContext.jsx  Logged-in user state
│       └── api/client.js            REST API calls
└── database/schema.sql              MySQL schema (reference / ER diagram)
```

## Prerequisites

- **Java 17+** (JDK) – check with `java -version`
- **Maven** – *not required*: `backend\mvnw.cmd` downloads Maven automatically on first use
- **Node.js 18+** – check with `node -v`

## How to run

**1. Start the backend** (terminal 1):

```bash
cd backend
.\mvnw.cmd spring-boot:run
```

Wait for `Started LearnHubApplication`. The API runs at http://localhost:8080.
On the first start it creates the tables and inserts 4 demo courses and 2 demo users.

(In IntelliJ: open the `backend` folder, then run `LearnHubApplication.java`.)

**2. Start the frontend** (terminal 2):

```bash
cd frontend
npm install
npm run dev
```

Open **http://localhost:5173**.

### Demo accounts

| Role       | Email                     | Password      |
|------------|---------------------------|---------------|
| Student    | `student@learnhub.com`    | `password123` |
| Instructor | `instructor@learnhub.com` | `password123` |

### Viewing the database without MySQL

While running on the default profile, open http://localhost:8080/h2-console and use:

- JDBC URL: `jdbc:h2:file:./data/learnhub;MODE=MySQL;DATABASE_TO_LOWER=TRUE`
- User: `sa`, password: *(empty)*

You can run normal SQL there, e.g. `SELECT * FROM users;`.

## Using real MySQL

1. Install **MySQL Community Server 8** (https://dev.mysql.com/downloads/installer/) – or XAMPP / WAMP –
   and note the `root` password you choose.
2. Start the backend with the `mysql` profile:

   ```bash
   cd backend
   # Windows PowerShell
   $env:DB_PASSWORD="your_mysql_password"; .\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=mysql"
   ```

   The database `learnhub_db` and all tables are created automatically. (Username defaults to `root`;
   override with `DB_USERNAME`.) In IntelliJ, set *Active profiles* to `mysql` in the run configuration.
3. `database/schema.sql` contains the same schema as plain MySQL DDL for your report or to create it by hand.

## Deployment (free: Render + Aiven MySQL)

In production the React build is bundled inside the Spring Boot app (see `Dockerfile`), so the whole
platform is **one service with one URL**. The database is a free cloud MySQL from Aiven.

### 1. Put the code on GitHub
```bash
git init
git add .
git commit -m "LearnHub online learning platform"
git branch -M main
git remote add origin https://github.com/<your-username>/learnhub.git
git push -u origin main
```
(Create the empty `learnhub` repository on github.com first.)

### 2. Create a free MySQL database (Aiven)
1. Sign up at https://aiven.io → **Create service** → **MySQL** → **Free plan** → Create.
2. When it is *Running*, open it and copy from **Connection information**: Host, Port, User (`avnadmin`), Password.

### 3. Deploy on Render
1. Sign up at https://render.com with GitHub → **New +** → **Web Service** → pick the repository.
2. Render detects the `Dockerfile`. Choose the **Free** instance type.
3. Add **Environment Variables**:

   | Key           | Value                                                          |
   |---------------|----------------------------------------------------------------|
   | `DB_URL`      | `jdbc:mysql://<HOST>:<PORT>/defaultdb?sslMode=REQUIRED`        |
   | `DB_USERNAME` | `avnadmin`                                                     |
   | `DB_PASSWORD` | *(Aiven password)*                                             |
   | `JWT_SECRET`  | any random text of 32+ characters                              |

4. **Create Web Service**. The first build takes ~5–10 minutes. Your site is then live at
   `https://<your-service>.onrender.com`. Tables and demo data are created automatically on first start.

Notes: the free Render service sleeps after 15 minutes without visitors, so the first visit afterwards takes
about a minute to wake up. Every `git push` to `main` redeploys automatically.

## REST API

| Method | Endpoint                                   | Auth        | Description                         |
|--------|--------------------------------------------|-------------|-------------------------------------|
| POST   | `/api/auth/register`                       | –           | Create account, returns JWT         |
| POST   | `/api/auth/login`                          | –           | Log in, returns JWT                 |
| GET    | `/api/auth/me`                             | user        | Current user                        |
| GET    | `/api/courses?search=&category=`           | –           | List / search courses               |
| GET    | `/api/courses/categories`                  | –           | Distinct categories                 |
| GET    | `/api/courses/{id}`                        | optional    | Course details, lessons, quizzes    |
| POST   | `/api/courses`                             | instructor  | Create course with lessons + quiz   |
| DELETE | `/api/courses/{id}`                        | instructor  | Delete own course                   |
| POST   | `/api/enrollments/{courseId}`              | user        | Enroll                              |
| DELETE | `/api/enrollments/{courseId}`              | user        | Leave course                        |
| GET    | `/api/enrollments/my`                      | user        | My enrollments with progress        |
| POST   | `/api/enrollments/lessons/{id}/complete`   | user        | Mark lesson complete                |
| GET    | `/api/quizzes/{id}`                        | enrolled    | Quiz questions (no answers)         |
| POST   | `/api/quizzes/{id}/submit`                 | enrolled    | Submit answers, get score           |
| GET    | `/api/dashboard`                           | user        | Dashboard statistics                |

Protected endpoints need the header `Authorization: Bearer <token>`.

## Database tables

`users`, `courses`, `lessons`, `quizzes`, `questions`, `enrollments`, `lesson_progress`, `quiz_attempts`
– see `database/schema.sql` for columns and foreign keys.

## How the Spring MVC request flow works

```
React (fetch /api/...)  →  JwtAuthFilter (validates token)
   →  DispatcherServlet  →  @RestController  →  @Service  →  JpaRepository  →  MySQL
   ←  DTO serialized to JSON  ←───────────────────────────────────────────────┘
```
