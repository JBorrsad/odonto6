/**
 * Formatea una fecha en formato local (DD/MM/YYYY)
 * @param {Date|string} date - Fecha a formatear
 * @returns {string} Fecha formateada
 */
export const formatDate = (date) => {
  if (!date) return '';
  
  const d = new Date(date);
  if (isNaN(d.getTime())) return '';
  
  return d.toLocaleDateString('es-ES');
};

/**
 * Formatea una hora en formato local (HH:MM)
 * @param {Date|string} date - Fecha/hora a formatear
 * @returns {string} Hora formateada
 */
export const formatTime = (date) => {
  if (!date) return '';
  
  const d = new Date(date);
  if (isNaN(d.getTime())) return '';
  
  return d.toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' });
};

/**
 * Formatea una fecha y hora en formato local (DD/MM/YYYY HH:MM)
 * @param {Date|string} date - Fecha/hora a formatear
 * @returns {string} Fecha y hora formateada
 */
export const formatDateTime = (date) => {
  if (!date) return '';
  
  const d = new Date(date);
  if (isNaN(d.getTime())) return '';
  
  return d.toLocaleString('es-ES', { 
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};

/**
 * Calcula la edad en años a partir de una fecha de nacimiento
 * @param {Date|string} birthDate - Fecha de nacimiento
 * @returns {number} Edad en años
 */
export const calculateAge = (birthDate) => {
  if (!birthDate) return 0;
  
  const today = new Date();
  const birthDateObj = new Date(birthDate);
  
  let age = today.getFullYear() - birthDateObj.getFullYear();
  const monthDiff = today.getMonth() - birthDateObj.getMonth();
  
  // Ajustar la edad si aún no ha cumplido años en este año
  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDateObj.getDate())) {
    age--;
  }
  
  return age;
};

/**
 * Obtiene el primer día de la semana (lunes) para una fecha dada
 * @param {Date|string} date - Fecha de referencia
 * @returns {Date} Primer día de la semana
 */
export const getFirstDayOfWeek = (date) => {
  const d = new Date(date);
  const day = d.getDay();
  const diff = d.getDate() - day + (day === 0 ? -6 : 1); // Ajustar para que el primer día sea lunes
  
  return new Date(d.setDate(diff));
};

/**
 * Obtiene una lista de días para una semana dada
 * @param {Date|string} date - Fecha dentro de la semana
 * @returns {Array} Array con los 7 días de la semana
 */
export const getWeekDays = (date) => {
  const firstDay = getFirstDayOfWeek(date);
  const week = [];
  
  for (let i = 0; i < 7; i++) {
    const day = new Date(firstDay);
    day.setDate(day.getDate() + i);
    week.push(day);
  }
  
  return week;
};

/**
 * Verifica si dos rangos de fechas se solapan
 * @param {Date|string} start1 - Fecha inicio rango 1
 * @param {Date|string} end1 - Fecha fin rango 1
 * @param {Date|string} start2 - Fecha inicio rango 2
 * @param {Date|string} end2 - Fecha fin rango 2
 * @returns {boolean} true si hay solapamiento
 */
export const dateRangesOverlap = (start1, end1, start2, end2) => {
  const s1 = new Date(start1).getTime();
  const e1 = new Date(end1).getTime();
  const s2 = new Date(start2).getTime();
  const e2 = new Date(end2).getTime();
  
  return (s1 < e2 && e1 > s2);
};

/**
 * Verifica si una fecha está entre un rango
 * @param {Date|string} date - Fecha a comprobar
 * @param {Date|string} start - Fecha inicio del rango
 * @param {Date|string} end - Fecha fin del rango
 * @returns {boolean} true si la fecha está en el rango
 */
export const isDateInRange = (date, start, end) => {
  const d = new Date(date).getTime();
  const s = new Date(start).getTime();
  const e = new Date(end).getTime();
  
  return (d >= s && d <= e);
}; 