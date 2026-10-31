import { Routes, Route, Navigate } from 'react-router-dom';
import Navbar from './components/Navbar.jsx';
import ProtectedRoute from './components/ProtectedRoute.jsx';
import CoursesPage from './pages/CoursesPage.jsx';
import CourseDetailPage from './pages/CourseDetailPage.jsx';
import LoginPage from './pages/LoginPage.jsx';
import RegisterPage from './pages/RegisterPage.jsx';
import DashboardPage from './pages/DashboardPage.jsx';
import QuizPage from './pages/QuizPage.jsx';
import CreateCoursePage from './pages/CreateCoursePage.jsx';

export default function App() {
  return (
    <>
      <Navbar />
      <main className="container">
        <Routes>
          <Route path="/" element={<CoursesPage />} />
          <Route path="/courses/:id" element={<CourseDetailPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route
            path="/dashboard"
            element={<ProtectedRoute><DashboardPage /></ProtectedRoute>}
          />
          <Route
            path="/quizzes/:id"
            element={<ProtectedRoute><QuizPage /></ProtectedRoute>}
          />
          <Route
            path="/create-course"
            element={<ProtectedRoute role="INSTRUCTOR"><CreateCoursePage /></ProtectedRoute>}
          />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </main>
      <footer className="footer">
        <div className="container footer-inner">
          <span><span className="brand-logo brand-logo-sm">L</span> LearnHub</span>
          <span>Built with React · Spring MVC · MySQL</span>
        </div>
      </footer>
    </>
  );
}
