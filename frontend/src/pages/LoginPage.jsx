import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { login, saveSession } from '../api/client'
import LanguageSwitcher from '../components/LanguageSwitcher'
import styles from './LoginPage.module.css'

export default function LoginPage() {
  const { t, i18n } = useTranslation()
  const [loginValue, setLoginValue] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState(null)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const navigate = useNavigate()

  async function handleSubmit(e) {
    e.preventDefault()
    setError(null)
    setIsSubmitting(true)
    try {
      const result = await login(loginValue, password)
      saveSession(result.token, result.role)
      navigate('/')
    } catch (err) {
      setError(t(err.message))
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <div className={styles.screen}>
      <div className={styles.brand}>
        <span className={styles.brandMark}>#01</span>
        <span className={styles.brandName}>Shop SaaS</span>
      </div>

      <div className={styles.langSwitcherWrap}>
        <LanguageSwitcher />
      </div>

      <div className={styles.ticket}>
        <div className={styles.ticketBody}>
          <div className={styles.eyebrow}>
            <span>{t('login.eyebrow')}</span>
            <span>{new Date().toLocaleDateString(i18n.resolvedLanguage)}</span>
          </div>

          <h1 className={styles.title}>{t('login.title')}</h1>
          <p className={styles.subtitle}>{t('login.subtitle')}</p>

          <form onSubmit={handleSubmit} noValidate>
            <div className={styles.field}>
              <label className={styles.label} htmlFor="login">{t('login.loginLabel')}</label>
              <input
                id="login"
                className={styles.input}
                type="text"
                autoComplete="username"
                value={loginValue}
                onChange={(e) => setLoginValue(e.target.value)}
                required
                autoFocus
              />
            </div>

            <div className={styles.field}>
              <label className={styles.label} htmlFor="password">{t('login.passwordLabel')}</label>
              <input
                id="password"
                className={styles.input}
                type="password"
                autoComplete="current-password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>

            <button className={styles.submit} type="submit" disabled={isSubmitting}>
              {isSubmitting ? t('login.submitting') : t('login.submit')}
            </button>

            {error && <div className={styles.error} role="alert">{error}</div>}
          </form>
        </div>
        <div className={styles.ticketTear} aria-hidden="true" />
      </div>

      <div className={styles.footer}>{t('login.footer')}</div>
    </div>
  )
}

