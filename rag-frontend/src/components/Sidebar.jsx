import React from 'react';

const Sidebar = ({ documents, isOpen, onClose }) => {
  if (!isOpen) return null;

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
          backgroundColor: 'rgba(0, 0, 0, 0.3)',
          backdropFilter: 'blur(4px)',
          zIndex: 40,
          animation: 'fadeIn 0.3s ease-in-out'
        }}
      />
      
      <aside style={{
        position: 'fixed',
        left: 0,
        top: 0,
        bottom: 0,
        width: '340px',
        background: '#ffffff',
        borderRight: '2px solid #e5e7eb',
        zIndex: 50,
        overflowY: 'auto',
        padding: '24px',
        boxShadow: '4px 0 20px rgba(0, 0, 0, 0.1)',
        animation: 'slideInLeft 0.3s ease-in-out'
      }}>
        <div style={{ 
          display: 'flex', 
          justifyContent: 'space-between', 
          alignItems: 'center',
          marginBottom: '24px',
          paddingBottom: '16px',
          borderBottom: '2px solid #e5e7eb'
        }}>
          <div style={{
            display: 'flex',
            alignItems: 'center',
            gap: '8px'
          }}>
            <img 
              src="/images/files.png" 
              alt="Documents" 
              style={{ width: '28px', height: '28px', objectFit: 'contain' }}
            />
            <h2 style={{ 
              fontSize: '17px', 
              fontWeight: '700', 
              color: '#1f2937', 
              margin: 0 
            }}>
              Documents
            </h2>
          </div>
          <button
            onClick={onClose}
            style={{
              background: '#f3f4f6',
              border: '1px solid #e5e7eb',
              borderRadius: '8px',
              fontSize: '18px',
              cursor: 'pointer',
              color: '#6b7280',
              lineHeight: 1,
              padding: '6px 10px',
              fontWeight: 'bold',
              transition: 'all 0.2s'
            }}
            onMouseEnter={(e) => {
              e.target.style.background = '#e5e7eb';
            }}
            onMouseLeave={(e) => {
              e.target.style.background = '#f3f4f6';
            }}
          >
            ✕
          </button>
        </div>
        
        <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
          {documents.length === 0 ? (
            <div style={{
              padding: '32px 24px',
              textAlign: 'center',
              color: '#6b7280',
              fontSize: '14px',
              background: '#f9fafb',
              borderRadius: '12px',
              border: '2px dashed #e5e7eb'
            }}>
              <div style={{ fontSize: '48px', marginBottom: '12px' }}>📁</div>
              <div style={{ fontWeight: '600' }}>No documents</div>
              <div style={{ fontSize: '12px', marginTop: '4px' }}>
                Upload your first PDFs
              </div>
            </div>
          ) : (
            documents.map((doc, idx) => (
              <div 
                key={idx}
                style={{
                  padding: '14px',
                  background: '#f9fafb',
                  borderRadius: '12px',
                  border: '1px solid #e5e7eb',
                  transition: 'all 0.2s',
                  cursor: 'pointer'
                }}
                onMouseEnter={(e) => {
                  e.currentTarget.style.background = '#ffffff';
                  e.currentTarget.style.borderColor = '#93c5fd';
                  e.currentTarget.style.transform = 'translateX(4px)';
                  e.currentTarget.style.boxShadow = '0 2px 8px rgba(0,0,0,0.05)';
                }}
                onMouseLeave={(e) => {
                  e.currentTarget.style.background = '#f9fafb';
                  e.currentTarget.style.borderColor = '#e5e7eb';
                  e.currentTarget.style.transform = 'translateX(0)';
                  e.currentTarget.style.boxShadow = 'none';
                }}
              >
                <div style={{
                  display: 'flex',
                  alignItems: 'flex-start',
                  gap: '10px',
                  marginBottom: '8px'
                }}>
                  <img 
                    src="/images/file.png" 
                    alt="file" 
                    style={{ width: '20px', height: '24px', objectFit: 'contain' }}
                  />
                  <div style={{ flex: 1 }}>
                    <div style={{ 
                      fontSize: '14px', 
                      fontWeight: '600', 
                      color: '#1f2937',
                      marginBottom: '6px',
                      wordBreak: 'break-word',
                      lineHeight: '1.4'
                    }}>
                      {doc.fileName}
                    </div>
                    <div style={{ 
                      fontSize: '12px', 
                      color: '#6b7280',
                      display: 'flex',
                      gap: '10px',
                      flexWrap: 'wrap',
                      alignItems: 'center'
                    }}>
                      <span style={{
                        background: '#e0f2fe',
                        padding: '3px 8px',
                        borderRadius: '6px',
                        fontWeight: '600',
                        color: '#0c4a6e',
                        display: 'flex',
                        alignItems: 'center',
                        gap: '4px'
                      }}>
                        <img 
                          src="/images/chunks.png" 
                          alt="chunks" 
                          style={{ width: '12px', height: '12px', objectFit: 'contain' }}
                        />
                        <span>{doc.numberOfChunks || 0} chunks</span>
                      </span>
                      <span style={{
                        background: '#fee2e2',
                        padding: '3px 8px',
                        borderRadius: '6px',
                        fontWeight: '600',
                        color: '#991b1b',
                        display: 'flex',
                        alignItems: 'center',
                        gap: '4px'
                      }}>
                        <img 
                          src="/images/save.png" 
                          alt="save" 
                          style={{ width: '12px', height: '12px', objectFit: 'contain' }}
                        />
                        <span>{((doc.fileSize || 0) / 1024).toFixed(1)} KB</span>
                      </span>
                    </div>
                  </div>
                </div>
                {doc.indexDate && (
                  <div style={{
                    fontSize: '11px',
                    color: '#9ca3af',
                    marginTop: '6px'
                  }}>
                    Indexed on {new Date(doc.indexDate).toLocaleDateString('en-US')}
                  </div>
                )}
              </div>
            ))
          )}
        </div>
      </aside>
    </>
  );
};

export default Sidebar;