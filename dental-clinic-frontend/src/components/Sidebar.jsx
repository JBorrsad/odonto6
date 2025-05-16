import React from 'react';
import { Link } from 'react-router-dom';

function Sidebar() {
  return (
    <div className="sidebar" style={{ width: '200px', backgroundColor: '#e0e0e0', padding: '10px' }}>
      <ul style={{ listStyle: 'none', padding: 0 }}>
        <li style={{ marginBottom: '5px' }}>
          <Link to="/patients">Patients</Link>
        </li>
        <li style={{ marginBottom: '5px' }}>
          <Link to="/schedule">Schedule</Link>
        </li>
        {/* Add links for other sections later */}
      </ul>
    </div>
  );
}

export default Sidebar;