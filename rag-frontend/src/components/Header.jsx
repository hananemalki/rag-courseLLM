import React from 'react';

const Header = ({ documentsCount, onUploadClick, onIndexClick }) => {
  return (
    <header style={{
      backgroundColor: '#ffffff',
      borderBottom: '2px solid #e5e7eb',
      padding: '16px 24px',
      display: 'flex',
      justifyContent: 'space-between',
      alignItems: 'center',
      boxShadow: '0 1px 3px rgba(0,0,0,0.05)'
    }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
        <div style={{
          width: '40px',
          height: '40px',
          borderRadius: '8px',
          background: '#ffffff',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          border: '2px solid #e5e7eb'
        }}>
          <img 
            src="/images/logo.png" 
            alt="Logo" 
            style={{ width: '32px', height: '32px', objectFit: 'contain' }}
          />
        </div>
        <div>
          <h1 style={{ 
            fontSize: '20px', 
            fontWeight: '600', 
            color: '#1f2937',
            margin: 0,
            marginBottom: '2px'
          }}>
            AskMyCourses
          </h1>
          <span style={{
            fontSize: '12px',
            color: '#6b7280',
            fontWeight: '500'
          }}>
            {documentsCount} indexed documents
          </span>
        </div>
      </div>
      
      <div style={{ display: 'flex', gap: '8px' }}>
        <button
          onClick={onUploadClick}
          style={{
            padding: '10px 18px',
            background: '#93c5fd',
            border: 'none',
            borderRadius: '8px',
            cursor: 'pointer',
            fontSize: '14px',
            color: '#1e3a8a',
            fontWeight: '600',
            transition: 'all 0.2s',
            boxShadow: '0 1px 2px rgba(0,0,0,0.05)'
          }}
          onMouseEnter={(e) => {
            e.target.style.background = '#60a5fa';
            e.target.style.transform = 'translateY(-1px)';
          }}
          onMouseLeave={(e) => {
            e.target.style.background = '#93c5fd';
            e.target.style.transform = 'translateY(0)';
          }}
        >
          Upload
        </button>
        
        <button
          onClick={onIndexClick}
          style={{
            padding: '10px 18px',
            background: '#fda4af',
            border: 'none',
            borderRadius: '8px',
            cursor: 'pointer',
            fontSize: '14px',
            color: '#881337',
            fontWeight: '600',
            transition: 'all 0.2s',
            boxShadow: '0 1px 2px rgba(0,0,0,0.05)'
          }}
          onMouseEnter={(e) => {
            e.target.style.background = '#fb7185';
            e.target.style.transform = 'translateY(-1px)';
          }}
          onMouseLeave={(e) => {
            e.target.style.background = '#fda4af';
            e.target.style.transform = 'translateY(0)';
          }}
        >
          Index
        </button>
      </div>
    </header>
  );
};

export default Header;