import React from 'react';

const LoadingMessage = () => {
  return (
    <div style={{ 
      padding: '24px',
      background: '#eff6ff',
      borderBottom: '1px solid #e5e7eb'
    }}>
      <div style={{
        maxWidth: '800px',
        margin: '0 auto'
      }}>
        <div style={{ display: 'flex', alignItems: 'flex-start', gap: '12px' }}>
          <div style={{
            width: '36px',
            height: '36px',
            borderRadius: '8px',
            background: '#93c5fd',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            fontSize: '20px',
            flexShrink: 0,
            boxShadow: '0 1px 2px rgba(0,0,0,0.05)',
            animation: 'pulse 2s ease-in-out infinite'
          }}>
            <img 
              src="/images/ia.png" 
              alt="AI" 
              style={{ width: '20px', height: '20px', objectFit: 'contain' }}
            />
          </div>
          
          <div style={{ flex: 1 }}>
            <div style={{
              fontSize: '13px',
              fontWeight: '600',
              color: '#374151',
              marginBottom: '12px',
              textTransform: 'uppercase',
              letterSpacing: '0.5px'
            }}>
              AI Assistant
            </div>
            
            <div style={{
              display: 'flex',
              alignItems: 'center',
              gap: '12px',
              background: '#ffffff',
              padding: '14px 18px',
              borderRadius: '8px',
              border: '1px solid #e5e7eb'
            }}>
              <div style={{
                display: 'flex',
                gap: '4px',
                alignItems: 'center'
              }}>
                <div style={{
                  width: '10px',
                  height: '10px',
                  borderRadius: '50%',
                  background: '#93c5fd',
                  animation: 'bounce 1.4s ease-in-out 0s infinite'
                }} />
                <div style={{
                  width: '10px',
                  height: '10px',
                  borderRadius: '50%',
                  background: '#fda4af',
                  animation: 'bounce 1.4s ease-in-out 0.2s infinite'
                }} />
                <div style={{
                  width: '10px',
                  height: '10px',
                  borderRadius: '50%',
                  background: '#fde68a',
                  animation: 'bounce 1.4s ease-in-out 0.4s infinite'
                }} />
              </div>
              
              <span style={{
                fontSize: '14px',
                color: '#6b7280',
                fontWeight: '600'
              }}>
                Searching in your documents...
              </span>
            </div>
          </div>
        </div>
      </div>
      
      <style>{`
        @keyframes bounce {
          0%, 80%, 100% { 
            transform: translateY(0);
            opacity: 0.5;
          }
          40% { 
            transform: translateY(-10px);
            opacity: 1;
          }
        }
        
        @keyframes pulse {
          0%, 100% {
            transform: scale(1);
            opacity: 1;
          }
          50% {
            transform: scale(1.05);
            opacity: 0.9;
          }
        }
      `}</style>
    </div>
  );
};

export default LoadingMessage;