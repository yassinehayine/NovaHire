import React from 'react'

const FormField = ({ label, error, hint, children, required = false }) => (
  <div>
    {label && (
      <label className="label">
        {label}
        {required && <span className="text-red-400 ml-1">*</span>}
      </label>
    )}
    {children}
    {hint && !error && <p className="text-xs text-slate-500 mt-1">{hint}</p>}
    {error && <p className="text-xs text-red-400 mt-1">{error}</p>}
  </div>
)

export default FormField
