// Small wrapper around fetch that adds the JWT token and turns error responses into exceptions.
const BASE_URL = '/api';

export async function request(path, { method = 'GET', body } = {}) {
  const token = localStorage.getItem('token');
  const headers = { 'Content-Type': 'application/json' };
  if (token) headers.Authorization = `Bearer ${token}`;

  const response = await fetch(BASE_URL + path, {
    method,
    headers,
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });

  const text = await response.text();
  let data = null;
  try {
    data = text ? JSON.parse(text) : null;
  } catch {
    data = null;
  }

  if (!response.ok) {
    if (response.status === 401 && token) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.dispatchEvent(new Event('auth-expired'));
    }
    throw new Error(data?.message || `Request failed (${response.status})`);
  }
  return data;
}

export const api = {
  // Auth
  login: (email, password) => request('/auth/login', { method: 'POST', body: { email, password } }),
  register: (payload) => request('/auth/register', { method: 'POST', body: payload }),
  me: () => request('/auth/me'),

  // Courses
  getCourses: (search = '', category = '') => {
    const params = new URLSearchParams();
    if (search) params.set('search', search);
    if (category) params.set('category', category);
    const query = params.toString();
    return request(`/courses${query ? `?${query}` : ''}`);
  },
  getCategories: () => request('/courses/categories'),
  getCourse: (id) => request(`/courses/${id}`),
  createCourse: (payload) => request('/courses', { method: 'POST', body: payload }),
  deleteCourse: (id) => request(`/courses/${id}`, { method: 'DELETE' }),

  // Enrollment & progress
  enroll: (courseId) => request(`/enrollments/${courseId}`, { method: 'POST' }),
  unenroll: (courseId) => request(`/enrollments/${courseId}`, { method: 'DELETE' }),
  completeLesson: (lessonId) => request(`/enrollments/lessons/${lessonId}/complete`, { method: 'POST' }),

  // Quizzes
  getQuiz: (id) => request(`/quizzes/${id}`),
  submitQuiz: (id, answers) => request(`/quizzes/${id}/submit`, { method: 'POST', body: { answers } }),

  // Dashboard
  getDashboard: () => request('/dashboard'),
};
