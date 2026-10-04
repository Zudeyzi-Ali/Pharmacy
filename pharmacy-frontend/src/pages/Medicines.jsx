import { useEffect, useState, useCallback } from 'react';
import medicineService from '../api/medicineService';
import MedicineTable from '../components/MedicineTable';
import MedicineForm from '../components/MedicineForm';
import { useAuth } from '../context/AuthContext';

export default function Medicines() {
  const { user } = useAuth();
  const isAdmin = user?.role === 'ADMIN';
  const [medicines, setMedicines] = useState([]);
  const [categories, setCategories] = useState([]);
  const [searchName, setSearchName] = useState('');
  const [filterCategory, setFilterCategory] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [editingMedicine, setEditingMedicine] = useState(null);
  const [loading, setLoading] = useState(true);

  const loadMedicines = useCallback(() => {
    setLoading(true);
    medicineService
      .getAll(searchName || undefined, filterCategory || undefined)
      .then(setMedicines)
      .catch(() => setMedicines([]))
      .finally(() => setLoading(false));
  }, [searchName, filterCategory]);

  useEffect(() => {
    medicineService.getCategories().then(setCategories).catch(() => setCategories([]));
  }, []);

  useEffect(() => {
    loadMedicines();
  }, [loadMedicines]);

  const handleAdd = () => {
    setEditingMedicine(null);
    setShowForm(true);
  };

  const handleEdit = (medicine) => {
    setEditingMedicine(medicine);
    setShowForm(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm('Delete this medicine?')) {
      await medicineService.remove(id);
      loadMedicines();
    }
  };

  const handleSubmit = async (data) => {
    if (editingMedicine) {
      await medicineService.update(editingMedicine.id, data);
    } else {
      await medicineService.create(data);
    }
    setShowForm(false);
    setEditingMedicine(null);
    loadMedicines();
  };

  return (
    <div className="module-page">
      <h1>Medicines</h1>

      <div className="filters">
        <input
          placeholder="Search by name..."
          value={searchName}
          onChange={(e) => setSearchName(e.target.value)}
        />
        <select value={filterCategory} onChange={(e) => setFilterCategory(e.target.value)}>
          <option value="">All categories</option>
          {categories.map((c) => (
            <option key={c.id} value={c.id}>{c.name}</option>
          ))}
        </select>
        {isAdmin && (
          <button className="btn btn-primary" onClick={handleAdd}>+ Add Medicine</button>
        )}
      </div>

      {isAdmin && showForm && (
        <MedicineForm
          categories={categories}
          initialData={editingMedicine}
          onSubmit={handleSubmit}
          onCancel={() => setShowForm(false)}
        />
      )}

      <div className="card">
        {loading ? <p>Loading medicines...</p> : (
          <MedicineTable medicines={medicines} onEdit={handleEdit} onDelete={handleDelete} isAdmin={isAdmin} />
        )}
      </div>
    </div>
  );
}
