import React, { useState } from 'react';

const UploadModal = ({ isOpen, onClose, onUpload, loading }) => {
  const [file, setFile] = useState(null);
  const [dragActive, setDragActive] = useState(false);

  if (!isOpen) return null;

  const handleSubmit = () => {
    if (file) {
      onUpload(file);
      setFile(null);
    }
  };

  const handleDrag = (e) => {
    e.preventDefault();
    e.stopPropagation();
    if (e.type === "dragenter" || e.type === "dragover") {
      setDragActive(true);
    } else if (e.type === "dragleave") {
      setDragActive(false);
    }
  };

  const handleDrop = (e) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(false);
    
    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      const droppedFile = e.dataTransfer.files[0];
      if (droppedFile.name.toLowerCase().endsWith('.pdf')) {
        setFile(droppedFile);
      } else {
        alert('Only PDF files are accepted');
      }
    }
  };

  const handleFileChange = (e) => {
    if (e.target.files && e.target.files[0]) {
      setFile(e.target.files[0]);
    }
  };

  return (
    <>
      <div 
        onClick={onClose}
        style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          backgroundColor: 'rgba(0,0,0,0.3)',
          zIndex: 40,
          animation: 'fadeIn 0.2s ease-in-out'
        }}
      />
      
      <div style={{
        position: 'fixed',
        top: '50%',
        left: '50%',
        transform: 'translate(-50%, -50%)',
        backgroundColor: '#fff',
        padding: '24px',
        borderRadius: '12px',
        boxShadow: '0 10px 40px rgba(0,0,0,0.1)',
        zIndex: 50,
        width: '90%',
        maxWidth: '400px',
        animation: 'scaleIn 0.2s ease-in-out'
      }}>
        <h3 style={{ 
          fontSize: '18px', 
          fontWeight: '600', 
          color: '#1e293b',
          marginBottom: '16px',
          marginTop: 0
        }}>
          Document Upload
        </h3>
        
        <div
          onDragEnter={handleDrag}
          onDragLeave={handleDrag}
          onDragOver={handleDrag}
          onDrop={handleDrop}
          style={{
            width: '100%',
            padding: '32px',
            border: dragActive ? '2px dashed #3b82f6' : '2px dashed #e2e8f0',
            borderRadius: '8px',
            marginBottom: '16px',
            textAlign: 'center',
            backgroundColor: dragActive ? '#f0f9ff' : '#f8fafc',
            cursor: 'pointer',
            transition: 'all 0.2s'
          }}
          onClick={() => !loading && document.getElementById('fileInput').click()}
        >
          <input
            id="fileInput"
            type="file"
            accept=".pdf"
            onChange={handleFileChange}
            disabled={loading}
            style={{ display: 'none' }}
          />
          <div style={{
            fontSize: '14px',
            color: '#64748b'
          }}>
            {file ? (
              <span style={{ color: '#3b82f6', fontWeight: '500' }}>
                {file.name}
              </span>
            ) : (
              <>
                <div style={{ marginBottom: '8px', fontSize: '16px' }}>
                  Click or drag a file
                </div>
                <div style={{ fontSize: '12px', color: '#94a3b8' }}>
                  PDF only
                </div>
              </>
            )}
          </div>
        </div>
        
        {file && (
          <div style={{
            padding: '12px',
            backgroundColor: '#f8fafc',
            borderRadius: '8px',
            marginBottom: '16px',
            fontSize: '14px',
            color: '#475569',
            border: '1px solid #e2e8f0'
          }}>
            <div style={{ 
              display: 'flex', 
              justifyContent: 'space-between',
              alignItems: 'center'
            }}>
              <span>{file.name}</span>
              <button
                onClick={(e) => {
                  e.stopPropagation();
                  setFile(null);
                }}
                disabled={loading}
                style={{
                  background: 'none',
                  border: 'none',
                  color: '#64748b',
                  cursor: loading ? 'not-allowed' : 'pointer',
                  fontSize: '18px',
                  padding: '0 4px'
                }}
              >
                ×
              </button>
            </div>
            <div style={{ 
              fontSize: '12px', 
              color: '#94a3b8',
              marginTop: '4px'
            }}>
              {(file.size / 1024 / 1024).toFixed(2)} MB
            </div>
          </div>
        )}
        
        <div style={{ 
          display: 'flex', 
          gap: '8px', 
          justifyContent: 'flex-end' 
        }}>
          <button
            onClick={onClose}
            disabled={loading}
            style={{
              padding: '8px 16px',
              backgroundColor: '#f8fafc',
              border: '1px solid #e2e8f0',
              borderRadius: '8px',
              cursor: loading ? 'not-allowed' : 'pointer',
              fontSize: '14px',
              color: '#475569',
              fontWeight: '500',
              transition: 'all 0.2s'
            }}
          >
            Cancel
          </button>
          <button
            onClick={handleSubmit}
            disabled={!file || loading}
            style={{
              padding: '8px 16px',
              backgroundColor: !file || loading ? '#e2e8f0' : '#3b82f6',
              border: 'none',
              borderRadius: '8px',
              cursor: !file || loading ? 'not-allowed' : 'pointer',
              fontSize: '14px',
              color: '#fff',
              fontWeight: '500',
              transition: 'all 0.2s'
            }}
          >
            {loading ? 'Uploading...' : 'Confirm'}
          </button>
        </div>
      </div>
    </>
  );
};

export default UploadModal;