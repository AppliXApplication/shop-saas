import { useTranslation } from 'react-i18next'
import styles from './LanguageSwitcher.module.css'

const LANGUAGES = [
  { code: 'ru', label: 'RU' },
  { code: 'en', label: 'EN' },
  { code: 'es', label: 'ES' },
  { code: 'uk', label: 'UA' },
]

export default function LanguageSwitcher() {
  const { i18n } = useTranslation()

  return (
    <div className={styles.switcher}>
      {LANGUAGES.map(({ code, label }) => (
        <button
          key={code}
          type="button"
          className={`${styles.langBtn} ${i18n.resolvedLanguage === code ? styles.langBtnActive : ''}`}
          onClick={() => i18n.changeLanguage(code)}
          aria-pressed={i18n.resolvedLanguage === code}
        >
          {label}
        </button>
      ))}
    </div>
  )
}
