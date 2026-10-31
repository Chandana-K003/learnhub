// Visual theme per course category: gradient + icon, shared by cards and the course page.
const CATEGORY_THEMES = {
  'Web Development': { gradient: 'linear-gradient(135deg, #6366f1 0%, #a855f7 100%)', icon: '⚛️' },
  'Backend Development': { gradient: 'linear-gradient(135deg, #0ea5e9 0%, #2563eb 100%)', icon: '🍃' },
  Databases: { gradient: 'linear-gradient(135deg, #f59e0b 0%, #ef4444 100%)', icon: '🗄️' },
  Programming: { gradient: 'linear-gradient(135deg, #10b981 0%, #0d9488 100%)', icon: '☕' },
};

const DEFAULT_THEME = { gradient: 'linear-gradient(135deg, #ec4899 0%, #8b5cf6 100%)', icon: '📘' };

export function categoryTheme(category) {
  return CATEGORY_THEMES[category] || DEFAULT_THEME;
}

export function initials(name = '') {
  return name
    .split(' ')
    .filter(Boolean)
    .slice(-2)
    .map((part) => part[0].toUpperCase())
    .join('');
}
