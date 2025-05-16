import React, { useEffect, useState } from 'react';
import { getDoctors, createDoctor, updateDoctor, deleteDoctor } from '../services/api';
import Card from '../components/common/Card';
import Button from '../components/common/Button';
import DoctorForm from '../components/doctor/DoctorForm';
import Modal from '../components/common/Modal';

function DoctorsPage() {
  const [doctors, setDoctors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState("");
  const [showForm, setShowForm] = useState(false);
  const [editingDoctor, setEditingDoctor] = useState(null);
  const [formLoading, setFormLoading] = useState(false);
  const [deleteLoading, setDeleteLoading] = useState(null);

  useEffect(() => {
    fetchDoctors();
  }, []); // Se ejecuta una vez al montar el componente

  const fetchDoctors = async () => {
    try {
      setLoading(true);
      const data = await getDoctors();
      setDoctors(data);
    } catch (err) {
      setError(err.message || "Error al cargar los doctores");
    } finally {
      setLoading(false);
    }
  };

  const filteredDoctors = doctors.filter(doctor => 
    doctor.nombreCompleto?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    doctor.especialidad?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const handleAddDoctor = () => {
    setEditingDoctor(null);
    setShowForm(true);
  };

  const handleEditDoctor = (doctor) => {
    setEditingDoctor(doctor);
    setShowForm(true);
  };

  const handleDeleteDoctor = async (doctorId) => {
    if (window.confirm('¿Estás seguro de que deseas eliminar este doctor? Esta acción no se puede deshacer.')) {
      try {
        setDeleteLoading(doctorId);
        await deleteDoctor(doctorId);
        setDoctors(prevDoctors => prevDoctors.filter(doc => doc.id !== doctorId));
      } catch (err) {
        setError(err.message || "Error al eliminar el doctor");
      } finally {
        setDeleteLoading(null);
      }
    }
  };

  const handleSubmitForm = async (formData) => {
    setFormLoading(true);
    try {
      if (editingDoctor) {
        // Actualizar doctor
        const updatedDoctor = await updateDoctor(editingDoctor.id, formData);
        setDoctors(prevDoctors => prevDoctors.map(doc => 
          doc.id === editingDoctor.id ? updatedDoctor : doc
        ));
      } else {
        // Crear doctor
        const newDoctor = await createDoctor(formData);
        setDoctors(prevDoctors => [...prevDoctors, newDoctor]);
      }
      setShowForm(false);
    } catch (err) {
      setError(err.message || `Error al ${editingDoctor ? 'actualizar' : 'crear'} el doctor`);
    } finally {
      setFormLoading(false);
    }
  };

  return (
    <div className="p-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold text-gray-900">Doctores</h1>
        <Button onClick={handleAddDoctor} variant="primary">
          Nuevo Doctor
        </Button>
      </div>

      {error && (
        <div className="mb-4 p-4 bg-red-50 border-l-4 border-red-500 text-red-700">
          <p>{error}</p>
        </div>
      )}

      <Card className="mb-6">
        <div className="p-4">
          <input
            type="text"
            placeholder="Buscar doctores..."
            className="w-full p-2 border rounded"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
      </Card>

      {loading ? (
        <div className="text-center py-10">
          <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-gray-900"></div>
          <p className="mt-2">Cargando doctores...</p>
        </div>
      ) : filteredDoctors.length === 0 ? (
        <Card>
          <div className="p-4 text-center text-gray-500">
            No se encontraron doctores
          </div>
        </Card>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {filteredDoctors.map(doctor => (
            <Card key={doctor.id} className="h-full">
              <div className="p-4">
                <div className="font-semibold text-lg text-gray-900 mb-2">
                  {doctor.nombreCompleto}
                </div>
                <div className="text-sm text-gray-500 mb-4">
                  {doctor.especialidad}
                </div>
                <div className="flex space-x-2 mt-auto pt-2">
                  <Button 
                    onClick={() => handleEditDoctor(doctor)} 
                    variant="secondary"
                    className="text-sm"
                  >
                    Editar
                  </Button>
                  <Button 
                    onClick={() => handleDeleteDoctor(doctor.id)} 
                    variant="danger"
                    className="text-sm"
                    loading={deleteLoading === doctor.id}
                  >
                    Eliminar
                  </Button>
                </div>
              </div>
            </Card>
          ))}
        </div>
      )}

      {showForm && (
        <Modal
          title={editingDoctor ? "Editar Doctor" : "Crear Doctor"}
          onClose={() => setShowForm(false)}
        >
          <DoctorForm 
            doctor={editingDoctor} 
            onSubmit={handleSubmitForm} 
            onCancel={() => setShowForm(false)}
            loading={formLoading}
          />
        </Modal>
      )}
    </div>
  );
}

export default DoctorsPage; 