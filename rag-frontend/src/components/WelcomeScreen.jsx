import React from 'react';

const WelcomeScreen = ({ documentsCount, onShowDocuments, onSuggestionClick }) => {
  const suggestions = [
    { text: 'What is an LLM?', color: '#93c5fd' },
    { text: 'Explain RAG to me', color: '#fda4af' },
    { text: 'How does chunking work?', color: '#fde68a' },
    { text: 'What is an embedding?', color: '#d8b4fe' }
  ];

  return (
    <div style={{
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center',
      height: '100%',
      padding: '24px',
      textAlign: 'center',
      backgroundColor: '#ffffff'
    }}>
      <div style={{
        maxWidth: '700px'
      }}>
        <div style={{
          width: '80px',
          height: '80px',
          margin: '0 auto 24px',
          borderRadius: '12px',
          background: '#ffffff',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          border: '2px solid #e5e7eb'
        }}>
          <img 
            src="/images/logo.png" 
            alt="Logo" 
            style={{ width: '64px', height: '64px', objectFit: 'contain' }}
          />
        </div>

        <h2 style={{ 
          fontSize: '36px', 
          fontWeight: '700', 
          color: '#1f2937',
          marginBottom: '12px',
          marginTop: 0
        }}>
          Welcome to AskMyCourses
        </h2>
        
        <p style={{ 
          fontSize: '17px', 
          color: '#6b7280',
          marginBottom: '32px',
          lineHeight: '1.6',
          fontWeight: '500'
        }}>
          Ask questions and get accurate answers based on your documents
        </p>

        <div style={{
          display: 'flex',
          gap: '16px',
          justifyContent: 'center',
          marginBottom: '32px',
          flexWrap: 'wrap'
        }}>
          <div style={{
            padding: '16px 20px',
            background: '#f9fafb',
            borderRadius: '12px',
            border: '1px solid #e5e7eb',
            minWidth: '140px',
            transition: 'all 0.2s'
          }}
          onMouseEnter={(e) => {
            e.currentTarget.style.transform = 'translateY(-2px)';
            e.currentTarget.style.boxShadow = '0 4px 6px rgba(0,0,0,0.1)';
          }}
          onMouseLeave={(e) => {
            e.currentTarget.style.transform = 'translateY(0)';
            e.currentTarget.style.boxShadow = 'none';
          }}>
            <div style={{ fontSize: '28px', marginBottom: '8px' }}>
              <img 
                src="/images/search.png" 
                alt="semantic search" 
                style={{ width: '32px', height: '32px', objectFit: 'contain' }}
              />
            </div>
            <div style={{ fontSize: '13px', color: '#4b5563', fontWeight: '600' }}>
              Semantic Search
            </div>
          </div>

          <div style={{
            padding: '16px 20px',
            background: '#f9fafb',
            borderRadius: '12px',
            border: '1px solid #e5e7eb',
            minWidth: '140px',
            transition: 'all 0.2s'
          }}
          onMouseEnter={(e) => {
            e.currentTarget.style.transform = 'translateY(-2px)';
            e.currentTarget.style.boxShadow = '0 4px 6px rgba(0,0,0,0.1)';
          }}
          onMouseLeave={(e) => {
            e.currentTarget.style.transform = 'translateY(0)';
            e.currentTarget.style.boxShadow = 'none';
          }}>
            <div style={{ fontSize: '28px', marginBottom: '8px' }}>
              <img 
                src="/images/files.png" 
                alt="sources" 
                style={{ width: '32px', height: '32px', objectFit: 'contain' }}
              />
            </div>
            <div style={{ fontSize: '13px', color: '#4b5563', fontWeight: '600' }}>
              Cited Sources
            </div>
          </div>

          <div style={{
            padding: '16px 20px',
            background: '#f9fafb',
            borderRadius: '12px',
            border: '1px solid #e5e7eb',
            minWidth: '140px',
            transition: 'all 0.2s'
          }}
          onMouseEnter={(e) => {
            e.currentTarget.style.transform = 'translateY(-2px)';
            e.currentTarget.style.boxShadow = '0 4px 6px rgba(0,0,0,0.1)';
          }}
          onMouseLeave={(e) => {
            e.currentTarget.style.transform = 'translateY(0)';
            e.currentTarget.style.boxShadow = 'none';
          }}>
            <div style={{ fontSize: '28px', marginBottom: '8px' }}>
              <img 
                src="/images/reponse.png" 
                alt="quick responses" 
                style={{ width: '32px', height: '32px', objectFit: 'contain' }}
              />
            </div>
            <div style={{ fontSize: '13px', color: '#4b5563', fontWeight: '600' }}>
              Quick Responses
            </div>
          </div>
        </div>

        <div style={{ marginBottom: '32px' }}>
          <h3 style={{
            fontSize: '15px',
            color: '#6b7280',
            fontWeight: '600',
            marginBottom: '16px'
          }}>
            Suggested Questions
          </h3>
          <div style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
            gap: '12px'
          }}>
            {suggestions.map((suggestion, idx) => (
              <button
                key={idx}
                onClick={() => onSuggestionClick && onSuggestionClick(suggestion.text)}
                style={{
                  padding: '14px 18px',
                  background: suggestion.color,
                  border: 'none',
                  borderRadius: '8px',
                  cursor: 'pointer',
                  fontSize: '14px',
                  color: '#1f2937',
                  fontWeight: '600',
                  transition: 'all 0.2s',
                  textAlign: 'left',
                  boxShadow: '0 1px 2px rgba(0,0,0,0.05)'
                }}
                onMouseEnter={(e) => {
                  e.target.style.transform = 'translateY(-2px)';
                  e.target.style.boxShadow = '0 4px 6px rgba(0,0,0,0.1)';
                  e.target.style.opacity = '0.9';
                }}
                onMouseLeave={(e) => {
                  e.target.style.transform = 'translateY(0)';
                  e.target.style.boxShadow = '0 1px 2px rgba(0,0,0,0.05)';
                  e.target.style.opacity = '1';
                }}
              >
                {suggestion.text}
              </button>
            ))}
          </div>
        </div>

        <button
          onClick={onShowDocuments}
          style={{
            padding: '14px 28px',
            background: '#3b82f6',
            color: 'white',
            border: 'none',
            borderRadius: '8px',
            fontSize: '16px',
            fontWeight: '600',
            cursor: 'pointer',
            transition: 'all 0.2s',
            boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
          }}
          onMouseEnter={(e) => {
            e.target.style.transform = 'translateY(-2px)';
            e.target.style.boxShadow = '0 4px 8px rgba(0,0,0,0.15)';
            e.target.style.background = '#2563eb';
          }}
          onMouseLeave={(e) => {
            e.target.style.transform = 'translateY(0)';
            e.target.style.boxShadow = '0 2px 4px rgba(0,0,0,0.1)';
            e.target.style.background = '#3b82f6';
          }}
        >
          View documents ({documentsCount})
        </button>
      </div>
    </div>
  );
};

export default WelcomeScreen;