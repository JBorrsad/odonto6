import { useState, useEffect, useCallback } from 'react';

/**
 * Hook personalizado para realizar peticiones a la API con gestión de estado
 * 
 * @param {Function} apiFunction - Función del servicio API a ejecutar
 * @param {Array} dependencies - Dependencias para recarga automática (opcional)
 * @param {Boolean} loadOnMount - Si debe cargar al montar el componente (por defecto: true)
 * @returns {Object} Objeto con datos, estado de carga, error y función para recargar
 */
export const useApi = (apiFunction, dependencies = [], loadOnMount = true) => {
  const [data, setData] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  const executeRequest = useCallback(async (...args) => {
    setIsLoading(true);
    setError(null);
    
    try {
      const result = await apiFunction(...args);
      setData(result);
      return result;
    } catch (err) {
      setError(err);
      throw err;
    } finally {
      setIsLoading(false);
    }
  }, [apiFunction]);

  useEffect(() => {
    if (loadOnMount) {
      executeRequest();
    }
  }, [...dependencies, loadOnMount, executeRequest]);

  return { data, isLoading, error, execute: executeRequest };
};

/**
 * Hook personalizado para obtener una lista de recursos
 * 
 * @param {Function} getAllFunction - Función del servicio API para obtener todos los recursos
 * @returns {Object} Objeto con datos, estado de carga, error y funciones CRUD
 */
export const useApiList = (getAllFunction) => {
  const { data, isLoading, error, execute: loadData } = useApi(getAllFunction);

  const addItem = useCallback(async (createFunction, item) => {
    try {
      const result = await createFunction(item);
      loadData(); // Recargar lista después de añadir
      return result;
    } catch (err) {
      throw err;
    }
  }, [loadData]);

  const updateItem = useCallback(async (updateFunction, id, item) => {
    try {
      const result = await updateFunction(id, item);
      loadData(); // Recargar lista después de actualizar
      return result;
    } catch (err) {
      throw err;
    }
  }, [loadData]);

  const deleteItem = useCallback(async (deleteFunction, id) => {
    try {
      await deleteFunction(id);
      loadData(); // Recargar lista después de eliminar
      return true;
    } catch (err) {
      throw err;
    }
  }, [loadData]);

  return {
    items: data || [],
    isLoading,
    error,
    refresh: loadData,
    addItem,
    updateItem,
    deleteItem,
  };
};

/**
 * Hook personalizado para gestionar un único recurso
 * 
 * @param {Function} getByIdFunction - Función del servicio API para obtener un recurso por ID
 * @param {String|Number} id - ID del recurso a cargar
 * @returns {Object} Objeto con datos, estado de carga, error y funciones para actualizar/eliminar
 */
export const useApiItem = (getByIdFunction, id) => {
  const { data, isLoading, error, execute: loadData } = useApi(
    () => getByIdFunction(id),
    [id],
    !!id
  );

  const updateItem = useCallback(async (updateFunction, updatedData) => {
    try {
      const result = await updateFunction(id, updatedData);
      loadData(); // Recargar después de actualizar
      return result;
    } catch (err) {
      throw err;
    }
  }, [id, loadData]);

  const deleteItem = useCallback(async (deleteFunction) => {
    try {
      await deleteFunction(id);
      return true;
    } catch (err) {
      throw err;
    }
  }, [id]);

  return {
    item: data,
    isLoading,
    error,
    refresh: loadData,
    updateItem,
    deleteItem,
  };
}; 