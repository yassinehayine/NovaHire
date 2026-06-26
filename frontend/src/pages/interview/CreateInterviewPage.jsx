import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Zap, ChevronRight, ChevronLeft, Check, Clock, Globe, Layers, Target, Cpu } from 'lucide-react'
import toast from 'react-hot-toast'
import { useAuth } from '../../context/AuthContext'
import { interviewsApi } from '../../api/interviews'
import FormField from '../../components/ui/FormField'

// ── Constants ──────────────────────────────────────────────────────────────────

const EXPERIENCE_LEVELS = [
  { value: 'JUNIOR', label: 'Junior',  sub: '0–2 years', color: 'text-green-400',  bg: 'hover:border-green-500/50' },
  { value: 'MID',    label: 'Mid',     sub: '2–5 years', color: 'text-blue-400',   bg: 'hover:border-blue-500/50' },
  { value: 'SENIOR', label: 'Senior',  sub: '5–10 yrs',  color: 'text-purple-400', bg: 'hover:border-purple-500/50' },
  { value: 'LEAD',   label: 'Lead',    sub: '10+ years', color: 'text-yellow-400', bg: 'hover:border-yellow-500/50' },
]

const TECH_CATEGORIES = {
  'Languages': ['Java', 'Python', 'JavaScript', 'TypeScript', 'Go', 'Rust', 'C#', 'C++', 'PHP', 'Kotlin', 'Swift', 'Ruby'],
  'Frameworks': ['Spring Boot', 'React', 'Angular', 'Vue.js', 'Next.js', 'Node.js', 'Django', 'FastAPI', 'Laravel', '.NET', 'Flutter'],
  'Databases': ['PostgreSQL', 'MySQL', 'MongoDB', 'Redis', 'Elasticsearch', 'Oracle', 'SQLite', 'Cassandra'],
  'DevOps & Cloud': ['Docker', 'Kubernetes', 'AWS', 'Azure', 'GCP', 'Terraform', 'Jenkins', 'GitHub Actions', 'Linux'],
  'Concepts': ['REST API', 'GraphQL', 'Microservices', 'System Design', 'Algorithms & DS', 'OOP', 'Clean Architecture', 'TDD', 'CI/CD'],
}

const DURATIONS = [
  { value: 15,  label: '15 min',  sub: '~3 questions' },
  { value: 30,  label: '30 min',  sub: '~6 questions' },
  { value: 45,  label: '45 min',  sub: '~9 questions' },
  { value: 60,  label: '1 hour',  sub: '~12 questions' },
  { value: 90,  label: '90 min',  sub: '15 questions (max)' },
]

const LANGUAGES = [
  { value: 'ENGLISH', label: 'English', flag: '🇬🇧' },
  { value: 'FRENCH',  label: 'French',  flag: '🇫🇷' },
  { value: 'GERMAN',  label: 'German',  flag: '🇩🇪' },
]

// ── Step components ────────────────────────────────────────────────────────────

const StepRole = ({ form, onChange, errors }) => (
  <div className="space-y-6">
    <FormField
      label="Target Role"
      error={errors.targetRole}
      hint="Be specific — e.g. 'Backend Java Developer' gives better questions than 'Developer'"
      required
    >
      <input
        type="text"
        name="targetRole"
        value={form.targetRole}
        onChange={onChange}
        placeholder="e.g. Full-Stack Developer, Data Engineer, DevOps Engineer…"
        className={`input-field text-base ${errors.targetRole ? 'border-red-500/50' : ''}`}
        autoFocus
      />
    </FormField>

    <div>
      <p className="label">Experience Level <span className="text-red-400">*</span></p>
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
        {EXPERIENCE_LEVELS.map(({ value, label, sub, color, bg }) => (
          <button
            key={value}
            type="button"
            onClick={() => onChange({ target: { name: 'experienceLevel', value } })}
            className={`p-4 rounded-xl border text-left transition-all duration-150 ${bg}
              ${form.experienceLevel === value
                ? 'border-nova-500 bg-nova-600/20'
                : 'border-white/10 bg-white/5 hover:bg-white/8'}`}
          >
            <p className={`font-semibold text-sm ${form.experienceLevel === value ? 'text-nova-200' : 'text-slate-200'}`}>{label}</p>
            <p className="text-xs text-slate-500 mt-0.5">{sub}</p>
          </button>
        ))}
      </div>
      {errors.experienceLevel && <p className="text-red-400 text-xs mt-1.5">{errors.experienceLevel}</p>}
    </div>
  </div>
)

const StepTech = ({ form, onToggleTech }) => (
  <div className="space-y-6">
    <p className="text-slate-400 text-sm">
      Select the technologies relevant to this session.
      <span className="text-slate-500 ml-2">({form.technologies.length} selected)</span>
    </p>

    {Object.entries(TECH_CATEGORIES).map(([category, techs]) => (
      <div key={category}>
        <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider mb-2">{category}</p>
        <div className="flex flex-wrap gap-2">
          {techs.map((tech) => {
            const selected = form.technologies.includes(tech)
            return (
              <button
                key={tech}
                type="button"
                onClick={() => onToggleTech(tech)}
                className={`px-3 py-1.5 rounded-lg text-sm font-medium border transition-all duration-100
                  ${selected
                    ? 'bg-nova-600/30 border-nova-500/60 text-nova-200'
                    : 'bg-white/5 border-white/10 text-slate-400 hover:border-white/25 hover:text-slate-300'}`}
              >
                {selected && <Check size={11} className="inline mr-1.5 -mt-0.5" />}
                {tech}
              </button>
            )
          })}
        </div>
      </div>
    ))}
  </div>
)

const StepSettings = ({ form, onChange, errors }) => (
  <div className="space-y-8">
    <div>
      <p className="label flex items-center gap-2">
        <Clock size={15} className="text-nova-400" />
        Session Duration
      </p>
      <div className="grid grid-cols-2 sm:grid-cols-5 gap-3">
        {DURATIONS.map(({ value, label, sub }) => (
          <button
            key={value}
            type="button"
            onClick={() => onChange({ target: { name: 'durationMinutes', value } })}
            className={`p-3 rounded-xl border text-center transition-all duration-150
              ${form.durationMinutes === value
                ? 'border-nova-500 bg-nova-600/20'
                : 'border-white/10 bg-white/5 hover:bg-white/8 hover:border-white/20'}`}
          >
            <p className={`font-semibold text-sm ${form.durationMinutes === value ? 'text-nova-200' : 'text-slate-200'}`}>{label}</p>
            <p className="text-xs text-slate-500 mt-0.5">{sub}</p>
          </button>
        ))}
      </div>
      {errors.durationMinutes && <p className="text-red-400 text-xs mt-1.5">{errors.durationMinutes}</p>}
    </div>

    <div>
      <p className="label flex items-center gap-2">
        <Globe size={15} className="text-nova-400" />
        Interview Language
      </p>
      <div className="grid grid-cols-3 gap-3">
        {LANGUAGES.map(({ value, label, flag }) => (
          <button
            key={value}
            type="button"
            onClick={() => onChange({ target: { name: 'language', value } })}
            className={`p-3 rounded-xl border text-center transition-all duration-150
              ${form.language === value
                ? 'border-nova-500 bg-nova-600/20'
                : 'border-white/10 bg-white/5 hover:bg-white/8 hover:border-white/20'}`}
          >
            <p className="text-xl mb-1">{flag}</p>
            <p className={`text-sm font-medium ${form.language === value ? 'text-nova-200' : 'text-slate-300'}`}>{label}</p>
          </button>
        ))}
      </div>
    </div>
  </div>
)

// ── Summary ────────────────────────────────────────────────────────────────────

const ReviewSummary = ({ form }) => {
  const level = EXPERIENCE_LEVELS.find(l => l.value === form.experienceLevel)
  const lang  = LANGUAGES.find(l => l.value === form.language)
  const dur   = DURATIONS.find(d => d.value === form.durationMinutes)
  const questionCount = Math.max(3, Math.min(15, Math.floor(form.durationMinutes / 5)))

  return (
    <div className="space-y-4">
      <p className="text-slate-400 text-sm">Review your configuration before the AI generates your questions.</p>

      <div className="bg-surface-2 rounded-xl border border-white/8 divide-y divide-white/5">
        {[
          { icon: Target,  label: 'Role',         value: form.targetRole },
          { icon: Layers,  label: 'Level',        value: level?.label ?? '—' },
          { icon: Cpu,     label: 'Technologies', value: form.technologies.length > 0 ? form.technologies.join(', ') : 'None selected' },
          { icon: Clock,   label: 'Duration',     value: `${dur?.label} · ${dur?.sub}` },
          { icon: Globe,   label: 'Language',     value: `${lang?.flag} ${lang?.label}` },
          { icon: Zap,     label: 'Questions',    value: `~${questionCount} questions (generated by AI in next step)` },
        ].map(({ icon: Icon, label, value }) => (
          <div key={label} className="flex items-start gap-3 px-4 py-3">
            <Icon size={15} className="text-nova-400 flex-shrink-0 mt-0.5" />
            <span className="text-sm text-slate-500 w-24 flex-shrink-0">{label}</span>
            <span className="text-sm text-slate-200 flex-1">{value}</span>
          </div>
        ))}
      </div>
    </div>
  )
}

// ── Wizard steps ───────────────────────────────────────────────────────────────

const STEPS = [
  { id: 'role',     label: 'Role',         icon: Target },
  { id: 'tech',     label: 'Technologies', icon: Cpu },
  { id: 'settings', label: 'Settings',     icon: Clock },
  { id: 'review',   label: 'Review',       icon: Check },
]

// ── Main page ──────────────────────────────────────────────────────────────────

const CreateInterviewPage = () => {
  const navigate = useNavigate()
  const { user } = useAuth()
  const [currentStep, setCurrentStep] = useState(0)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [errors, setErrors] = useState({})

  const [form, setForm] = useState({
    targetRole:        user?.targetRole ?? '',
    experienceLevel:   user?.experienceLevel ?? '',
    technologies:      [],
    durationMinutes:   30,
    language:          user?.preferredLanguage ?? 'ENGLISH',
  })

  const handleChange = (e) => {
    const { name, value } = e.target
    setForm(prev => ({ ...prev, [name]: value }))
    if (errors[name]) setErrors(prev => ({ ...prev, [name]: '' }))
  }

  const toggleTech = (tech) => {
    setForm(prev => ({
      ...prev,
      technologies: prev.technologies.includes(tech)
        ? prev.technologies.filter(t => t !== tech)
        : [...prev.technologies, tech],
    }))
  }

  const validateStep = () => {
    const stepErrors = {}
    if (currentStep === 0) {
      if (!form.targetRole.trim())   stepErrors.targetRole = 'Target role is required'
      if (!form.experienceLevel)     stepErrors.experienceLevel = 'Please select an experience level'
    }
    if (currentStep === 2) {
      if (!form.durationMinutes)     stepErrors.durationMinutes = 'Please select a duration'
    }
    setErrors(stepErrors)
    return Object.keys(stepErrors).length === 0
  }

  const handleNext = () => {
    if (!validateStep()) return
    setCurrentStep(s => s + 1)
  }

  const handleBack = () => {
    setCurrentStep(s => s - 1)
    setErrors({})
  }

  const handleSubmit = async () => {
    setIsSubmitting(true)
    try {
      const response = await interviewsApi.create({
        targetRole:      form.targetRole.trim(),
        experienceLevel: form.experienceLevel,
        technologies:    form.technologies,
        durationMinutes: form.durationMinutes,
        language:        form.language,
      })
      const interviewId = response.data.id
      toast.success('Interview created! AI questions ready in Sprint 3 🚀')
      navigate(`/interview/${interviewId}`)
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to create interview. Please try again.'
      toast.error(msg)
    } finally {
      setIsSubmitting(false)
    }
  }

  const stepContent = [
    <StepRole     key="role"     form={form} onChange={handleChange} errors={errors} />,
    <StepTech     key="tech"     form={form} onToggleTech={toggleTech} />,
    <StepSettings key="settings" form={form} onChange={handleChange} errors={errors} />,
    <ReviewSummary key="review"  form={form} />,
  ]

  const isLastStep = currentStep === STEPS.length - 1

  return (
    <div className="max-w-2xl animate-fade-in">
      <div className="mb-8">
        <h1 className="font-display text-3xl font-bold text-white">New Interview</h1>
        <p className="text-slate-400 text-sm mt-1">Configure your session — the AI will tailor questions to your setup</p>
      </div>

      {/* ── Progress bar ───────────────────────────────────────────── */}
      <div className="flex items-center gap-1 mb-8">
        {STEPS.map(({ id, label, icon: Icon }, i) => {
          const done    = i < currentStep
          const active  = i === currentStep
          return (
            <React.Fragment key={id}>
              <div className={`flex items-center gap-2 px-3 py-1.5 rounded-lg text-xs font-medium transition-all
                ${active ? 'bg-nova-600/20 text-nova-300 border border-nova-500/40'
                : done   ? 'text-slate-500'
                :          'text-slate-600'}`}
              >
                {done
                  ? <Check size={12} className="text-nova-500" />
                  : <Icon size={12} />}
                <span className="hidden sm:inline">{label}</span>
              </div>
              {i < STEPS.length - 1 && (
                <div className={`flex-1 h-px ${i < currentStep ? 'bg-nova-700' : 'bg-white/10'}`} />
              )}
            </React.Fragment>
          )
        })}
      </div>

      {/* ── Step content ───────────────────────────────────────────── */}
      <div className="card min-h-[360px]">
        <h2 className="font-display text-lg font-semibold text-white mb-6">
          {STEPS[currentStep].label}
        </h2>
        {stepContent[currentStep]}
      </div>

      {/* ── Navigation ─────────────────────────────────────────────── */}
      <div className="flex items-center justify-between mt-6">
        <button
          type="button"
          onClick={handleBack}
          disabled={currentStep === 0}
          className="btn-secondary flex items-center gap-2 disabled:opacity-30"
        >
          <ChevronLeft size={16} />
          Back
        </button>

        {isLastStep ? (
          <button
            type="button"
            onClick={handleSubmit}
            disabled={isSubmitting}
            className="btn-primary flex items-center gap-2"
          >
            {isSubmitting ? (
              <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
            ) : (
              <Zap size={16} />
            )}
            {isSubmitting ? 'Creating…' : 'Create Interview'}
          </button>
        ) : (
          <button
            type="button"
            onClick={handleNext}
            className="btn-primary flex items-center gap-2"
          >
            Next
            <ChevronRight size={16} />
          </button>
        )}
      </div>
    </div>
  )
}

export default CreateInterviewPage
