function statusBadge(medicine) {
  const today = new Date();
  const expiry = new Date(medicine.expiryDate);
  const daysToExpiry = Math.ceil((expiry - today) / (1000 * 60 * 60 * 24));

  if (medicine.quantity < medicine.reorderLevel) {
    return <span className="badge badge-low">Low Stock</span>;
  }
  if (daysToExpiry <= 30) {
    return <span className="badge badge-expiry">Near Expiry</span>;
  }
  return <span className="badge badge-ok">OK</span>;
}

export default function MedicineTable({ medicines, onEdit, onDelete, isAdmin }) {
  if (!medicines.length) {
    return <p>No medicines found.</p>;
  }

  return (
    <table>
      <thead>
        <tr>
          <th>Name</th>
          <th>Category</th>
          <th>Batch</th>
          <th>Qty</th>
          <th>Reorder Lvl</th>
          <th>Expiry</th>
          <th>Price</th>
          <th>Status</th>
          {isAdmin && <th>Actions</th>}
        </tr>
      </thead>
      <tbody>
        {medicines.map((m) => (
          <tr key={m.id}>
            <td>{m.name}</td>
            <td>{m.categoryName}</td>
            <td>{m.batchNumber}</td>
            <td>{m.quantity}</td>
            <td>{m.reorderLevel}</td>
            <td>{m.expiryDate}</td>
            <td>{m.price != null ? `$${m.price}` : '-'}</td>
            <td>{statusBadge(m)}</td>
            {isAdmin && (
              <td>
                <button className="btn btn-secondary" style={{ marginRight: 6 }} onClick={() => onEdit(m)}>
                  Edit
                </button>
                <button className="btn btn-danger" onClick={() => onDelete(m.id)}>
                  Delete
                </button>
              </td>
            )}
          </tr>
        ))}
      </tbody>
    </table>
  );
}
