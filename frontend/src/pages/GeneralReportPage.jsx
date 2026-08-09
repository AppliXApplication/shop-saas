import { useEffect, useState, useCallback } from 'react'
import { useNavigate, useSearchParams, Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import {
  getToken,
  getRole,
  clearSession,
  fetchGeneralReport,
  fetchCategories,
} from '../api/client'
import LanguageSwitcher from '../components/LanguageSwitcher'
import styles from './GeneralReportPage.module.css'

const PAGE_SIZE = 50

function todayStr() {
  const d = new Date()
  const mm = String(d.getMonth() + 1).padStart(2, '0')
  const dd = String(d.getDate()).padStart(2, '0')
  return `${d.getFullYear()}-${mm}-${dd}`
}

export default function GeneralReportPage() {
  const { t } = useTranslation()
  const navigate = useNavigate()

  const [searchParams, setSearchParams] = useSearchParams()
  const from = searchParams.get('from') || todayStr()
  const to = searchParams.get('to') || todayStr()
  const categoryId = searchParams.get('categoryId') || ''
  const page = Number(searchParams.get('page') ?? 0)

  const [rows, setRows] = useState(null)
  const [pageInfo, setPageInfo] = useState({ number: 0, totalPages: 0, totalElements: 0 })
  const [categories, setCategories] = useState([])
  const [error, setError] = useState(null)

  const updateParams = useCallback((next) => {
    const params = new URLSearchParams(searchParams)
    Object.entries(next).forEach(([key, value]) => {
      if (value === '' || value === null || value === undefined) {
        params.delete(key)
      } else {
        params.set(key, String(value))
      }
    })
    setSearchParams(params, { replace: true })
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [searchParams])

  useEffect(() => {
    if (!getToken()) {
      navigate('/login')
      return
    }
    fetchCategories().then(setCategories).catch(() => {})
  }, [navigate])

  useEffect(() => {
    if (!getToken()) return
    const catId = categoryId ? Number(categoryId) : undefined
    fetchGeneralReport(from, to, catId, page, PAGE_SIZE)
      .then((data) => {
        setRows(data.content)
        setPageInfo({
          number: data.number,
          totalPages: data.totalPages,
          totalElements: data.totalElements,
        })
      })
      .catch((err) => {
        setError(t(err.message))
        if (err.message === 'errors.sessionExpired') {
          clearSession()
          setTimeout(() => navigate('/login'), 1200)
        }
      })
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [from, to, categoryId, page])

  function handleLogout() {
    clearSession()
    navigate('/login')
  }

  const canGoPrev = pageInfo.number > 0
  const canGoNext = pageInfo.number < pageInfo.totalPages - 1

  return (
    <div className={styles.page}>
      <div className={styles.header}>
        <div className={styles.headerLeft}>
          <span className={styles.eyebrow}>Shop SaaS</span>
          <h1 className={styles.title}>{t('general.title')}</h1>
        </div>
        <div className={styles.headerRight}>
          <Link className={styles.navLink} to="/">{t('residue.title')}</Link>
          <Link className={styles.navLink} to="/cashier">{t('cashier.title')}</Link>
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
            value={from}
            onChange={(e) => updateParams({ from: e.target.value, page: 0 })}
          />
          <span className={styles.dateSep}>—</span>
          <input
            className={styles.dateInput}
            type="date"
            value={to}
            onChange={(e) => updateParams({ to: e.target.value, page: 0 })}
          />

          <select
            className={styles.categorySelect}
            value={categoryId}
            onChange={(e) => updateParams({ categoryId: e.target.value, page: 0 })}
          >
            <option value="">{t('residue.allCategories')}</option>
            {categories.map((c) => (
              <option key={c.id} value={c.id}>{c.name}</option>
            ))}
          </select>
        </div>

        <div className={styles.tableWrap}>
          {rows === null && <div className={styles.loading}>{t('residue.loading')}</div>}

          {rows !== null && rows.length === 0 && (
            <div className={styles.empty}>{t('residue.empty')}</div>
          )}

          {rows !== null && rows.length > 0 && (
            <table>
              <thead>
                <tr>
                  <th>{t('general.columns.name')}</th>
                  <th className={styles.numeric}>{t('general.columns.residue')}</th>
                  <th className={styles.numeric}>{t('general.columns.soldQty')}</th>
                  <th className={styles.numeric}>{t('general.columns.receivedQty')}</th>
                  <th className={styles.numeric}>{t('general.columns.writtenOffQty')}</th>
                  <th className={styles.numeric}>{t('general.columns.salesSum')}</th>
                  <th className={styles.numeric}>{t('general.columns.arrivalSum')}</th>
                  <th className={styles.numeric}>{t('general.columns.writeoffSum')}</th>
                  <th className={styles.numeric}>{t('general.columns.profit')}</th>
                </tr>
              </thead>
              <tbody>
                {rows.map((row) => (
                  <tr key={row.goodsId}>
                    <td className={styles.name}>{row.name}</td>
                    <td className={`${styles.mono} ${styles.numeric}`}>{row.residue}</td>
                    <td className={`${styles.mono} ${styles.numeric}`}>{row.soldQty}</td>
                    <td className={`${styles.mono} ${styles.numeric}`}>{row.receivedQty}</td>
                    <td className={`${styles.mono} ${styles.numeric}`}>{row.writtenOffQty}</td>
                    <td className={`${styles.mono} ${styles.numeric}`}>{row.salesSum}</td>
                    <td className={`${styles.mono} ${styles.numeric}`}>{row.arrivalSum}</td>
                    <td className={`${styles.mono} ${styles.numeric}`}>{row.writeoffSum}</td>
                    <td className={`${styles.mono} ${styles.numeric} ${Number(row.profit) < 0 ? styles.profitNegative : styles.profitPositive}`}>
                      {row.profit}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>

        {rows !== null && rows.length > 0 && (
          <div className={styles.pagination}>
            <button
              className={styles.pageBtn}
              disabled={!canGoPrev}
              onClick={() => updateParams({ page: page - 1 })}
            >
              ←
            </button>
            <span className={styles.pageInfo}>
              {pageInfo.number + 1} / {Math.max(pageInfo.totalPages, 1)}
              {' · '}{pageInfo.totalElements}
            </span>
            <button
              className={styles.pageBtn}
              disabled={!canGoNext}
              onClick={() => updateParams({ page: page + 1 })}
            >
              →
            </button>
          </div>
        )}
      </div>
    </div>
  )
}
