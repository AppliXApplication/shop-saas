import { useEffect, useState, useCallback } from 'react'
import { useNavigate, useSearchParams, Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { getToken, getRole, clearSession, fetchCaseRecordReport } from '../api/client'
import LanguageSwitcher from '../components/LanguageSwitcher'
import styles from './CaseRecordPage.module.css'

function todayStr() {
  const d = new Date()
  const mm = String(d.getMonth() + 1).padStart(2, '0')
  const dd = String(d.getDate()).padStart(2, '0')
  return `${d.getFullYear()}-${mm}-${dd}`
}

export default function CaseRecordPage() {
  const { t, i18n } = useTranslation()
  const navigate = useNavigate()

  const [searchParams, setSearchParams] = useSearchParams()
  const date = searchParams.get('date') || todayStr()

  const [data, setData] = useState(null)
  const [error, setError] = useState(null)

  const load = useCallback((d) => {
    fetchCaseRecordReport(d)
      .then(setData)
      .catch((err) => {
        setError(t(err.message))
        if (err.message === 'errors.sessionExpired') {
          clearSession()
          setTimeout(() => navigate('/login'), 1200)
        }
      })
  }, [navigate, t])

  useEffect(() => {
    if (!getToken()) {
      navigate('/login')
      return
    }
    load(date)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [date])

  function handleDateChange(e) {
    const params = new URLSearchParams(searchParams)
    params.set('date', e.target.value)
    setSearchParams(params, { replace: true })
  }

  function goToday() {
    const params = new URLSearchParams(searchParams)
    params.set('date', todayStr())
    setSearchParams(params, { replace: true })
  }

  function handleLogout() {
    clearSession()
    navigate('/login')
  }

  function fmtTime(iso) {
    if (!iso) return '—'
    return new Date(iso).toLocaleTimeString(i18n.resolvedLanguage, { hour: '2-digit', minute: '2-digit' })
  }

  function fmtNum(v) {
    return v ?? '—'
  }

  const rows = data?.rows ?? null
  const totals = data?.totals

  return (
    <div className={styles.page}>
      <div className={styles.header}>
        <div className={styles.headerLeft}>
          <span className={styles.eyebrow}>Shop SaaS</span>
          <h1 className={styles.title}>{t('cashier.title')}</h1>
        </div>
        <div className={styles.headerRight}>
          <Link className={styles.navLink} to="/">{t('cashier.toResidue')}</Link>
          <LanguageSwitcher />
          <span className={styles.userInfo}>
            {t('nav.loggedInAs')}: {getRole()}
          </span>
          <button className={styles.logoutBtn} onClick={handleLogout}>
            {t('nav.logout')}
          </button>
        </div>
      </div>

      {error && <div className={styles.errorBanner} role="alert">{error}</div>}

      <div className={styles.panel}>
        <div className={styles.toolbar}>
          <input
            className={styles.dateInput}
            type="date"
            value={date}
            onChange={handleDateChange}
          />
          <button className={styles.todayBtn} onClick={goToday}>
            {t('cashier.today')}
          </button>
        </div>

        {totals && (
          <div className={styles.totals}>
            <div className={styles.totalCell}>
              <span className={styles.totalLabel}>{t('cashier.totals.shifts')}</span>
              <span className={styles.totalValue}>{totals.recordCount}</span>
            </div>
            <div className={styles.totalCell}>
              <span className={styles.totalLabel}>{t('cashier.columns.cashMustBe')}</span>
              <span className={styles.totalValue}>{totals.cashMustBeTotal}</span>
            </div>
            <div className={styles.totalCell}>
              <span className={styles.totalLabel}>{t('cashier.columns.cashIn')}</span>
              <span className={styles.totalValue}>{totals.cashInTotal}</span>
            </div>
            <div className={styles.totalCell}>
              <span className={styles.totalLabel}>{t('cashier.columns.cashOut')}</span>
              <span className={styles.totalValue}>{totals.cashOutTotal}</span>
            </div>
            <div className={styles.totalCell}>
              <span className={styles.totalLabel}>{t('cashier.columns.arrival')}</span>
              <span className={styles.totalValue}>{totals.arrivalTotal}</span>
            </div>
            <div className={styles.totalCell}>
              <span className={styles.totalLabel}>{t('cashier.columns.writeoff')}</span>
              <span className={styles.totalValue}>{totals.writeoffTotal}</span>
            </div>
          </div>
        )}

        <div className={styles.tableWrap}>
          {rows === null && <div className={styles.loading}>{t('residue.loading')}</div>}

          {rows !== null && rows.length === 0 && (
            <div className={styles.empty}>{t('cashier.empty')}</div>
          )}

          {rows !== null && rows.length > 0 && (
            <table>
              <thead>
                <tr>
                  <th>{t('cashier.columns.time')}</th>
                  <th>{t('cashier.columns.cashier')}</th>
                  <th className={styles.numeric}>{t('cashier.columns.cashMustBe')}</th>
                  <th className={styles.numeric}>{t('cashier.columns.cashIn')}</th>
                  <th className={styles.numeric}>{t('cashier.columns.cashOut')}</th>
                  <th className={styles.numeric}>{t('cashier.columns.arrival')}</th>
                  <th className={styles.numeric}>{t('cashier.columns.writeoff')}</th>
                </tr>
              </thead>
              <tbody>
                {rows.map((row) => (
                  <tr key={row.id}>
                    <td className={styles.mono}>{fmtTime(row.date)}</td>
                    <td>{row.cashierLogin ?? '—'}</td>
                    <td className={`${styles.mono} ${styles.numeric}`}>{fmtNum(row.cashMustBe)}</td>
                    <td className={`${styles.mono} ${styles.numeric}`}>{fmtNum(row.cashInSum)}</td>
                    <td className={`${styles.mono} ${styles.numeric}`}>{fmtNum(row.cashOutSum)}</td>
                    <td className={`${styles.mono} ${styles.numeric}`}>{fmtNum(row.arrivalSum)}</td>
                    <td className={`${styles.mono} ${styles.numeric}`}>{fmtNum(row.writeoffSum)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>
    </div>
  )
}
