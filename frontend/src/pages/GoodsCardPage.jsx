import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import {
  getToken,
  isAdmin,
  fetchGoodsDetail,
  updateGoodsDetail,
  fetchCategories,
} from '../api/client'
import styles from './GoodsCardPage.module.css'

export default function GoodsCardPage() {
  const { t } = useTranslation()
  const navigate = useNavigate()
  const { id } = useParams()
  const admin = isAdmin()

  const [form, setForm] = useState(null)
  const [categories, setCategories] = useState([])
  const [error, setError] = useState(null)
  const [isSaving, setIsSaving] = useState(false)
  const [toast, setToast] = useState(null)

  useEffect(() => {
    if (!getToken()) {
      navigate('/login')
      return
    }
    fetchGoodsDetail(id).then(setForm).catch((err) => setError(t(err.message)))
    fetchCategories().then(setCategories).catch(() => {})
  }, [id, navigate, t])

  function setField(field, value) {
    setForm((prev) => ({ ...prev, [field]: value }))
  }

  async function handleSave(e) {
    e.preventDefault()
    setError(null)
    setIsSaving(true)
    try {
      const updated = await updateGoodsDetail(id, {
        name: form.name,
        code: form.code,
        residue: Number(String(form.residue).replace(',', '.')),
        price: Number(String(form.price).replace(',', '.')),
        priceOpt: form.priceOpt === '' || form.priceOpt === null
          ? null
          : Number(String(form.priceOpt).replace(',', '.')),
        categoryId: form.categoryId === '' ? null : Number(form.categoryId),
        marking: form.marking,
      })
      setForm(updated)
      setToast(t('residue.saved'))
      setTimeout(() => setToast(null), 2000)
    } catch (err) {
      setError(t(err.message))
    } finally {
      setIsSaving(false)
    }
  }

  return (
    <div className={styles.page}>
      <div className={styles.header}>
        <button className={styles.backLink} onClick={() => navigate(-1)}>
          ← {t('card.back')}
        </button>
      </div>

      <div className={styles.card}>
        {!form && !error && <div className={styles.loading}>{t('residue.loading')}</div>}

        {form && (
          <form onSubmit={handleSave}>
            <div className={styles.eyebrow}>
              <span>{t('card.eyebrow')}</span>
              <span>#{form.id}</span>
            </div>
            <h1 className={styles.title}>{admin ? t('card.titleEdit') : t('card.titleView')}</h1>

            <div className={styles.grid}>
              <div className={`${styles.field} ${styles.fieldFull}`}>
                <label className={styles.label}>{t('card.name')}</label>
                <input
                  className={styles.input}
                  value={form.name ?? ''}
                  onChange={(e) => setField('name', e.target.value)}
                  disabled={!admin}
                  required
                />
              </div>

              <div className={styles.field}>
                <label className={styles.label}>{t('card.code')}</label>
                <input
                  className={styles.input}
                  value={form.code ?? ''}
                  onChange={(e) => setField('code', e.target.value)}
                  disabled={!admin}
                  required
                />
              </div>

              <div className={styles.field}>
                <label className={styles.label}>{t('card.category')}</label>
                <select
                  className={styles.select}
                  value={form.categoryId ?? ''}
                  onChange={(e) => setField('categoryId', e.target.value)}
                  disabled={!admin}
                >
                  <option value="">—</option>
                  {categories.map((c) => (
                    <option key={c.id} value={c.id}>{c.name}</option>
                  ))}
                </select>
              </div>

              <div className={styles.field}>
                <label className={styles.label}>{t('card.residue')}</label>
                <input
                  className={styles.input}
                  type="text"
                  inputMode="decimal"
                  value={form.residue ?? ''}
                  onChange={(e) => setField('residue', e.target.value)}
                  disabled={!admin}
                />
              </div>

              <div className={styles.field}>
                <label className={styles.label}>{t('card.price')}</label>
                <input
                  className={styles.input}
                  type="text"
                  inputMode="decimal"
                  value={form.price ?? ''}
                  onChange={(e) => setField('price', e.target.value)}
                  disabled={!admin}
                />
              </div>

              <div className={styles.field}>
                <label className={styles.label}>{t('card.priceOpt')}</label>
                <input
                  className={styles.input}
                  type="text"
                  inputMode="decimal"
                  value={form.priceOpt ?? ''}
                  onChange={(e) => setField('priceOpt', e.target.value)}
                  disabled={!admin}
                />
              </div>

              <div className={styles.field}>
                <div className={styles.checkboxRow}>
                  <input
                    id="marking"
                    type="checkbox"
                    checked={Boolean(form.marking)}
                    onChange={(e) => setField('marking', e.target.checked)}
                    disabled={!admin}
                  />
                  <label className={styles.label} htmlFor="marking" style={{ margin: 0 }}>
                    {t('card.marking')}
                  </label>
                </div>
              </div>
            </div>

            {admin ? (
              <div className={styles.actions}>
                <button className={styles.saveBtn} type="submit" disabled={isSaving}>
                  {isSaving ? t('login.submitting') : t('residue.save')}
                </button>
              </div>
            ) : (
              <p className={styles.readOnlyHint}>{t('residue.adminOnlyHint')}</p>
            )}

            {error && <div className={styles.error} role="alert">{error}</div>}
          </form>
        )}
      </div>

      {toast && <div className={styles.toast}>{toast}</div>}
    </div>
  )
}
