import React from 'react';

const ChatInput = ({ 
  value, 
  onChange, 
  onSend, 
  loading, 
  disabled,
  showBackButton,
  onBack
}) => {
  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      onSend();
    }
  };

  return (
    <div 
      style={{
        borderTop: '1px solid #e5e7eb',
        padding: '16px 24px',
        backgroundColor: '#fff'
      }}
    >
      <div style={{ 
        maxWidth: '800px', 
        margin: '0 auto',
        display: 'flex',
        gap: '12px',
        alignItems: 'center'
      }}>
        {showBackButton && (
          <button
            onClick={onBack}
            disabled={loading}
            style={{
              padding: '12px 20px',
              backgroundColor: '#f3f4f6',
              border: '1px solid #e5e7eb',
              borderRadius: '8px',
              fontSize: '15px',
              fontWeight: '600',
              cursor: loading ? 'not-allowed' : 'pointer',
              color: '#374151',
              transition: 'all 0.2s',
              whiteSpace: 'nowrap',
              display: 'flex',
              alignItems: 'center',
              gap: '6px'
            }}
            onMouseOver={(e) => {
              if (!loading) {
                e.target.style.backgroundColor = '#e5e7eb';
                e.target.style.transform = 'translateX(-2px)';
              }
            }}
            onMouseOut={(e) => {
              if (!loading) {
                e.target.style.backgroundColor = '#f3f4f6';
                e.target.style.transform = 'translateX(0)';
              }
            }}
          >
            <span>Back</span>
          </button>
        )}
        
        <input
          type="text"
          value={value}
          onChange={onChange}
          onKeyPress={handleKeyPress}
          placeholder="Ask your question..."
          disabled={disabled || loading}
          style={{
            flex: 1,
            padding: '12px 16px',
            border: '1px solid #e2e8f0',
            borderRadius: '8px',
            fontSize: '15px',
            outline: 'none',
            transition: 'all 0.2s',
            backgroundColor: disabled || loading ? '#f8fafc' : '#fff'
          }}
          onFocus={(e) => {
            if (!disabled && !loading) {
              e.target.style.borderColor = '#3b82f6';
              e.target.style.boxShadow = '0 0 0 3px rgba(59, 130, 246, 0.1)';
            }
          }}
          onBlur={(e) => {
            e.target.style.borderColor = '#e2e8f0';
            e.target.style.boxShadow = 'none';
          }}
        />
        
        <button
          onClick={onSend}
          disabled={disabled || loading || !value.trim()}
          style={{
            padding: '12px 24px',
            backgroundColor: disabled || loading || !value.trim() ? '#e2e8f0' : '#3b82f6',
            color: disabled || loading || !value.trim() ? '#94a3b8' : '#fff',
            border: 'none',
            borderRadius: '8px',
            fontSize: '15px',
            fontWeight: '500',
            cursor: disabled || loading || !value.trim() ? 'not-allowed' : 'pointer',
            transition: 'all 0.2s',
            minWidth: '100px'
          }}
          onMouseOver={(e) => {
            if (!disabled && !loading && value.trim()) {
              e.target.style.backgroundColor = '#2563eb';
              e.target.style.transform = 'translateY(-1px)';
            }
          }}
          onMouseOut={(e) => {
            if (!disabled && !loading && value.trim()) {
              e.target.style.backgroundColor = '#3b82f6';
              e.target.style.transform = 'translateY(0)';
            }
          }}
        >
          {loading ? 'Sending...' : 'Send'}
        </button>
      </div>
    </div>
  );
};

export default ChatInput;