import React from 'react'
import ReactDOM from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import { Toaster } from 'react-hot-toast'
import App from './App'
import './styles/index.css'

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <BrowserRouter>
      <App />
      <Toaster
        position="top-right"
        toastOptions={{
          duration: 4000,
          style: {
            background: '#1e1e3a',
            color: '#e0e9ff',
            border: '1px solid rgba(98, 113, 245, 0.3)',
            borderRadius: '12px',
            fontSize: '14px',
          },
          success: {
            iconTheme: { primary: '#6271f5', secondary: '#e0e9ff' },
          },
          error: {
            iconTheme: { primary: '#f87171', secondary: '#1e1e3a' },
          },
        }}
      />
    </BrowserRouter>
  </React.StrictMode>
)
