import i18n from 'i18next'
import { initReactI18next } from 'react-i18next'
import LanguageDetector from 'i18next-browser-languagedetector'

import ru from './locales/ru.json'
import en from './locales/en.json'
import es from './locales/es.json'
import uk from './locales/uk.json'

i18n
  .use(LanguageDetector)
  .use(initReactI18next)
  .init({
    resources: {
      ru: { translation: ru },
      en: { translation: en },
      es: { translation: es },
      uk: { translation: uk },
    },
    fallbackLng: 'en',
    supportedLngs: ['ru', 'en', 'es', 'uk'],
    interpolation: { escapeValue: false },
    detection: {
      order: ['localStorage', 'navigator'],
      caches: ['localStorage'],
      lookupLocalStorage: 'shop_lang',
    },
  })

export default i18n
