import React from 'react'
import { Link } from 'react-router-dom'
import Logo from '../components/common/Logo'

const NotFoundPage = () => (
  <div className="min-h-screen flex flex-col items-center justify-center bg-surface text-center p-6">
    <Logo size="md" />
    <h1 className="font-display text-7xl font-bold gradient-text mt-8 mb-4">404</h1>
    <p className="text-slate-300 text-xl font-medium mb-2">Page not found</p>
    <p className="text-slate-500 text-sm mb-8">The page you're looking for doesn't exist.</p>
    <Link to="/dashboard" className="btn-primary">Back to Dashboard</Link>
  </div>
)

export default NotFoundPage
