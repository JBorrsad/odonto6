// Utilidad para combinar nombres de clases
export function cn(...classes) {
  return classes.filter(Boolean).join(' ');
} 