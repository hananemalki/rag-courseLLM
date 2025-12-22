import React, { useState } from 'react';

const Message = ({ message, onGenerateSummary, onImproveAnswer }) => {
  const isUser = message.role === 'user';
  const isError = message.role === 'error';
  const [showActions, setShowActions] = useState(false);
  
  const bgColor = isUser 
    ? '#f9fafb' 
    : isError 
    ? '#fee2e2' 
    : '#eff6ff';
    
  const avatarBg = isUser 
    ? '#fde68a'
    : isError
    ? '#fca5a5'
    : '#93c5fd';
  
  return (
    <div 
      style={{
        padding: '24px',
        background: bgColor,
        borderBottom: '1px solid #e5e7eb'
      }}
      onMouseEnter={() => !isUser && !isError && setShowActions(true)}
      onMouseLeave={() => setShowActions(false)}
    >
      <div style={{ maxWidth: '800px', margin: '0 auto' }}>
        <div style={{ display: 'flex', alignItems: 'flex-start', gap: '12px', marginBottom: '12px' }}>
          <div style={{
            width: '36px',
            height: '36px',
            borderRadius: '8px',
            background: avatarBg,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            fontSize: '18px',
            flexShrink: 0,
            boxShadow: '0 1px 2px rgba(0,0,0,0.05)'
          }}>
            {isUser ? (
              <img src="/images/user.png" alt="User" style={{ width: '20px', height: '20px', objectFit: 'contain' }} />
            ) : isError ? (
              '!'
            ) : (
              <img src="/images/ia.png" alt="IA" style={{ width: '20px', height: '20px', objectFit: 'contain' }} />
            )}
          </div>
          
          <div style={{ flex: 1 }}>
            <div style={{
              fontSize: '13px',
              fontWeight: '600',
              color: isError ? '#991b1b' : '#374151',
              marginBottom: '8px',
              textTransform: 'uppercase',
              letterSpacing: '0.5px'
            }}>
              {isUser ? 'You' : isError ? 'Error' : 'AI Assistant'}
            </div>
            
            <div style={{
              fontSize: '15px',
              lineHeight: '1.7',
              color: isError ? '#991b1b' : '#1f2937',
              whiteSpace: 'pre-wrap',
              background: '#ffffff',
              padding: '12px 16px',
              borderRadius: '8px',
              border: '1px solid #e5e7eb'
            }}>
              {message.content}
            </div>

            {!isUser && !isError && showActions && (
              <div style={{
                marginTop: '12px',
                display: 'flex',
                gap: '8px',
                flexWrap: 'wrap'
              }}>
                <button
                  onClick={() => onGenerateSummary && onGenerateSummary(message.content)}
                  style={{
                    padding: '6px 12px',
                    background: '#fde68a',
                    border: 'none',
                    borderRadius: '6px',
                    cursor: 'pointer',
                    fontSize: '13px',
                    color: '#92400e',
                    fontWeight: '500',
                    transition: 'all 0.2s'
                  }}
                  onMouseEnter={(e) => {
                    e.target.style.background = '#fcd34d';
                    e.target.style.transform = 'translateY(-1px)';
                  }}
                  onMouseLeave={(e) => {
                    e.target.style.background = '#fde68a';
                    e.target.style.transform = 'translateY(0)';
                  }}
                >
                   Summarize
                </button>
                
                <button
                  onClick={() => onImproveAnswer && onImproveAnswer(message.content)}
                  style={{
                    padding: '6px 12px',
                    background: '#fda4af',
                    border: 'none',
                    borderRadius: '6px',
                    cursor: 'pointer',
                    fontSize: '13px',
                    color: '#881337',
                    fontWeight: '500',
                    transition: 'all 0.2s'
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
                   Improve
                </button>
                
                <button
                  onClick={() => navigator.clipboard.writeText(message.content)}
                  style={{
                    padding: '6px 12px',
                    background: '#93c5fd',
                    border: 'none',
                    borderRadius: '6px',
                    cursor: 'pointer',
                    fontSize: '13px',
                    color: '#1e3a8a',
                    fontWeight: '500',
                    transition: 'all 0.2s'
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
                  Copy
                </button>
              </div>
            )}

            {message.sources && message.sources.length > 0 && (
              <div style={{ marginTop: '16px' }}>
                <div style={{
                  fontSize: '13px',
                  fontWeight: '600',
                  color: '#6b7280',
                  marginBottom: '10px'
                }}>
                <img 
                        src="/images/files.png" 
                        alt="files" 
                        style={{ width: '12px', height: '12px', objectFit: 'contain' }}
                    />
                    <span> </span>
                   Sources ({message.sources.length})
                </div>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                  {message.sources.map((source, idx) => (
                    <div 
                      key={idx}
                      style={{
                        padding: '12px',
                        background: '#ffffff',
                        border: '1px solid #e5e7eb',
                        borderRadius: '8px',
                        transition: 'all 0.2s'
                      }}
                      onMouseEnter={(e) => {
                        e.currentTarget.style.borderColor = '#93c5fd';
                        e.currentTarget.style.transform = 'translateX(4px)';
                      }}
                      onMouseLeave={(e) => {
                        e.currentTarget.style.borderColor = '#e5e7eb';
                        e.currentTarget.style.transform = 'translateX(0)';
                      }}
                    >
                      <div style={{
                        display: 'flex',
                        justifyContent: 'space-between',
                        alignItems: 'center',
                        marginBottom: '8px',
                        gap: '8px'
                      }}>
                        <span style={{ 
                          fontSize: '13px', 
                          fontWeight: '600',
                          color: '#1f2937',
                          flex: 1
                        }}>
                        <img 
                          src="/images/file.png" 
                          alt="file" 
                          style={{ width: '12px', height: '12px', objectFit: 'contain' }}
                        />
                        {source.fileName}
                        </span>
                        <span style={{
                          fontSize: '12px',
                          color: '#6b7280',
                          background: '#f3f4f6',
                          padding: '4px 10px',
                          borderRadius: '6px',
                          fontWeight: '600',
                          whiteSpace: 'nowrap'
                        }}>
                          {(source.relevanceScore * 100).toFixed(0)}%
                        </span>
                      </div>
                      <div style={{
                        fontSize: '13px',
                        color: '#4b5563',
                        lineHeight: '1.6',
                        fontStyle: 'italic'
                      }}>
                        "{source.excerpt}"
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {message.processingTime && (
              <div style={{
                marginTop: '10px',
                fontSize: '12px',
                color: '#9ca3af'
              }}>
                Generated in {(message.processingTime / 1000).toFixed(2)}s
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Message;