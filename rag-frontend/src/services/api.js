const API_BASE_URL = 'http://localhost:8080/api';

class ApiService {
  
    async askQuestion(data) {
        try {
            const response = await fetch(`${API_BASE_URL}/chat/ask`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data),
            });

            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.error || error.errorMessage || 'Server error');
            }

            return await response.json();
        } catch (error) {
            console.error('Error askQuestion:', error);
            throw error;
        }
    }

    async getStats() {
        try {
            const response = await fetch(`${API_BASE_URL}/chat/stats`);
            
            if (!response.ok) {
                throw new Error('Error loading statistics');
            }

            return await response.json();
        } catch (error) {
            console.error('Error getStats:', error);
            return { indexedDocuments: 0, status: 'error' };
        }
    }

    async listDocuments() {
        try {
            const response = await fetch(`${API_BASE_URL}/documents/indexed`);
            
            if (!response.ok) {
                throw new Error('Error loading list');
            }

            return await response.json();
        } catch (error) {
            console.error('Error listDocuments:', error);
            return [];
        }
    }

    async uploadDocument(file) {
        try {
            const formData = new FormData();
            formData.append('file', file);

            const response = await fetch(`${API_BASE_URL}/documents/upload`, {
                method: 'POST',
                body: formData,
            });

            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.error || 'Upload error');
            }

            return await response.json();
        } catch (error) {
            console.error('Error uploadDocument:', error);
            throw error;
        }
    }

    async indexAllDocuments() {
        try {
            const response = await fetch(`${API_BASE_URL}/documents/index-all`, {
                method: 'POST',
            });

            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.error || 'Indexing error');
            }

            return await response.json();
        } catch (error) {
            console.error('Error indexAllDocuments:', error);
            throw error;
        }
    }

    async forceReindex() {
        try {
            const response = await fetch(`${API_BASE_URL}/documents/force-reindex`, {
                method: 'POST',
            });

            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.error || 'Force reindex error');
            }

            return await response.json();
        } catch (error) {
            console.error('Error forceReindex:', error);
            throw error;
        }
    }

    async checkDocumentIndexed(filename) {
        try {
            const response = await fetch(`${API_BASE_URL}/documents/check/${encodeURIComponent(filename)}`);
            
            if (!response.ok) {
                throw new Error('Check error');
            }

            return await response.json();
        } catch (error) {
            console.error('Error checkDocumentIndexed:', error);
            return { indexed: false };
        }
    }

    async healthCheck() {
        try {
            const response = await fetch(`${API_BASE_URL}/chat/health`);
            return await response.json();
        } catch (error) {
            console.error('Error healthCheck:', error);
            return { status: 'DOWN' };
        }
    }
}

const api = new ApiService();
export default api;