import React from 'react';

function Topbar() {
  const topbarStyle = {
    backgroundColor: '#f0f0f0',
    padding: '10px',
    textAlign: 'center',
  };

  return (
    <div style={topbarStyle}>
      <h1>Dental Clinic Management</h1>
      {/* Add user info or logout button later */}
    </div>
  );
}

export default Topbar;