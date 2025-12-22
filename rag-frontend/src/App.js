import React, { useState, useEffect, useRef } from 'react';
import './App.css';

import api from './services/api';
import Header from './components/Header';
import Sidebar from './components/Sidebar';
import Message from './components/Message';
import UploadModal from './components/UploadModal';
import WelcomeScreen from './components/WelcomeScreen';
import ChatInput from './components/ChatInput';
import LoadingMessage from './components/LoadingMessage';

function App() {
  const [messages, setMessages] = useState([]);
  const [inputValue, setInputValue] = useState('');
  const [loading, setLoading] = useState(false);
  const [documents, setDocuments] = useState([]);
  const [documentsCount, setDocumentsCount] = useState(0);
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [uploadModalOpen, setUploadModalOpen] = useState(false);
  const messagesEndRef = useRef(null);

  useEffect(() => {
    const init = async () => {
      await loadStats();
      const docs = await api.listDocuments();
      setDocuments(docs);
      
      const needsReindex = docs.length > 0 && docs.every(doc => !doc.numberOfChunks);
      
      if (needsReindex) {
        console.log(' Documents need reindexing...');
        try {
          await api.forceReindex();
          console.log(' Reindex completed');
          const updatedDocs = await api.listDocuments();
          setDocuments(updatedDocs);
        } catch (error) {
          console.error(' Reindex failed:', error);
        }
      }
    };
    
    init();
  }, []);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const loadDocuments = async () => {
    try {
      const docs = await api.listDocuments();
      setDocuments(docs);
    } catch (error) {
      console.error('Error loading documents:', error);
    }
  };

  const loadStats = async () => {
    try {
      const stats = await api.getStats();
      setDocumentsCount(stats.indexedDocuments);
    } catch (error) {
      console.error('Error loading stats:', error);
    }
  };

  const handleSendMessage = async (questionText = inputValue) => {
    if (!questionText.trim() || loading) return;

    const userMessage = {
      role: 'user',
      content: questionText,
      timestamp: new Date()
    };

    setMessages(prev => [...prev, userMessage]);
    setInputValue('');
    setLoading(true);

    try {
      const response = await api.askQuestion({
        question: questionText,
        includeSources: true
      });

      const assistantMessage = {
        role: 'assistant',
        content: response.answer,
        sources: response.sources,
        processingTime: response.processingTimeMs,
        timestamp: new Date()
      };

      setMessages(prev => [...prev, assistantMessage]);
    } catch (error) {
      const errorMessage = {
        role: 'error',
        content: error.message || 'Communication error with the server',
        timestamp: new Date()
      };
      setMessages(prev => [...prev, errorMessage]);
    } finally {
      setLoading(false);
    }
  };

  const handleSuggestionClick = (suggestionText) => {
    setInputValue(suggestionText);
    handleSendMessage(suggestionText);
  };

  const handleGenerateSummary = async (content) => {
    if (loading) return;

    const summaryMessage = {
      role: 'user',
      content: ' Generate a concise summary',
      timestamp: new Date()
    };

    setMessages(prev => [...prev, summaryMessage]);
    setLoading(true);

    try {
      const response = await api.askQuestion({
        question: `Please provide a concise summary in bullet points of this text:\n\n${content}`,
        includeSources: false,
        language: 'en'
      });

      const assistantMessage = {
        role: 'assistant',
        content: response.answer,
        processingTime: response.processingTimeMs,
        timestamp: new Date()
      };

      setMessages(prev => [...prev, assistantMessage]);
    } catch (error) {
      const errorMessage = {
        role: 'error',
        content: 'Error generating summary',
        timestamp: new Date()
      };
      setMessages(prev => [...prev, errorMessage]);
    } finally {
      setLoading(false);
    }
  };

  const handleImproveAnswer = async (content) => {
    if (loading) return;

    const improveMessage = {
      role: 'user',
      content: ' Improve and expand this answer',
      timestamp: new Date()
    };

    setMessages(prev => [...prev, improveMessage]);
    setLoading(true);

    try {
      const response = await api.askQuestion({
        question: `Please improve and expand this answer with more details, examples, and comprehensive information:\n\n${content}`,
        includeSources: true,
        language: 'en'
      });

      const assistantMessage = {
        role: 'assistant',
        content: response.answer,
        sources: response.sources,
        processingTime: response.processingTimeMs,
        timestamp: new Date()
      };

      setMessages(prev => [...prev, assistantMessage]);
    } catch (error) {
      const errorMessage = {
        role: 'error',
        content: 'Error improving answer',
        timestamp: new Date()
      };
      setMessages(prev => [...prev, errorMessage]);
    } finally {
      setLoading(false);
    }
  };

  const handleUpload = async (file) => {
    setLoading(true);
    try {
      const result = await api.uploadDocument(file);
      alert(` Document "${file.name}" uploaded and indexed successfully!\n📊 ${result.numberOfChunks} chunks created.`);
      setUploadModalOpen(false);
      await loadDocuments();
      await loadStats();
    } catch (error) {
      alert(` Upload error: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const handleIndexAll = async () => {
    if (loading) return;
    
    setLoading(true);
    try {
      const result = await api.indexAllDocuments();
      alert(` ${result.documentsIndexed} documents indexed successfully!`);
      await loadDocuments();
      await loadStats();
    } catch (error) {
      alert(` Indexing error: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const handleBackToWelcome = () => {
    setMessages([]);
    setInputValue('');
  };

  return (
    <div style={{
      display: 'flex',
      flexDirection: 'column',
      height: '100vh',
      backgroundColor: '#f9fafb'
    }}>
      <Header 
        documentsCount={documentsCount}
        onUploadClick={() => setUploadModalOpen(true)}
        onIndexClick={handleIndexAll}
      />

      <Sidebar 
        documents={documents}
        isOpen={sidebarOpen}
        onClose={() => setSidebarOpen(false)}
      />

      <UploadModal
        isOpen={uploadModalOpen}
        onClose={() => setUploadModalOpen(false)}
        onUpload={handleUpload}
        loading={loading}
      />

      <div style={{
        flex: 1,
        overflowY: 'auto',
        backgroundColor: '#ffffff'
      }}>
        {messages.length === 0 ? (
          <WelcomeScreen 
            documentsCount={documentsCount}
            onShowDocuments={() => setSidebarOpen(true)}
            onSuggestionClick={handleSuggestionClick}
          />
        ) : (
          <>
            {messages.map((msg, idx) => (
              <Message 
                key={idx} 
                message={msg}
                onGenerateSummary={handleGenerateSummary}
                onImproveAnswer={handleImproveAnswer}
              />
            ))}
            {loading && <LoadingMessage />}
            <div ref={messagesEndRef} />
          </>
        )}
      </div>

      <ChatInput
        value={inputValue}
        onChange={(e) => setInputValue(e.target.value)}
        onSend={() => handleSendMessage()}
        loading={loading}
        disabled={false}
        showBackButton={messages.length > 0}
        onBack={handleBackToWelcome}
      />
    </div>
  );
}

export default App;