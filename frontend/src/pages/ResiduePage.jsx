import { useEffect, useState, useCallback } from 'react'
import { useNavigate, useSearchParams, Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import {
  getToken,
  getRole,
  isAdmin,
  clearSession,
  fetchGoodsResidue,
  fetchGoodsResidueSum,
  fetchCategories,
  updateGoodsResidue,
} from '../api/client'
import LanguageSwitcher from '../components/LanguageSwitcher'
import styles from './ResiduePage.module.css'

const PAGE_SIZE = 50

export default function ResiduePage() {
  const { t } = useTranslation()
  const navigate = useNavigate()

  // Фильтр/страница живут в URL (?search=&categoryId=&page=), поэтому при
  // переходе на карточку товара и возврате назад состояние не теряется.
  const [searchParams, setSearchParams] = useSearchParams()
  const search = searchParams.get('search') ?? ''
  const categoryId = searchParams.get('categoryId') ?? ''
  const page = Number(searchParams.get('page') ?? 0)

  const [searchInput, setSearchInput] = useState(search)
  const [rows, setRows] = useState(null)
  const [pageInfo, setPageInfo] = useState({ number: 0, totalPages: 0, totalElements: 0 })
  const [sum, setSum] = useState(null)
  const [categories, setCategories] = useState([])
  const [error, setError] = useState(null)
  const [editingId, setEditingId] = useState(null)
  const [editValue, setEditValue] = useState('')
  const [toast, setToast] = useState(null)

  const admin = isAdmin()

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
    fetchGoodsResidue(search || undefined, catId, page, PAGE_SIZE)
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

    fetchGoodsResidueSum(catId).then(setSum).catch(() => {})
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [search, categoryId, page])

  // debounce поля поиска -> в URL
  useEffect(() => {
    const handle = setTimeout(() => {
      if (searchInput !== search) {
        updateParams({ search: searchInput, page: 0 })
      }
    }, 300)
    return () => clearTimeout(handle)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [searchInput])

  function handleCategoryChange(e) {
    updateParams({ categoryId: e.target.value, page: 0 })
  }

  function handleLogout() {
    clearSession()
    navigate('/login')
  }

  function startEdit(row) {
    setEditingId(row.goodsId)
    setEditValue(String(row.residue ?? 0))
  }

  function cancelEdit() {
    setEditingId(null)
    setEditValue('')
  }

  async function saveEdit(row) {
    const parsed = Number(editValue.replace(',', '.'))
    if (Number.isNaN(parsed)) return

    try {
      await updateGoodsResidue(row.goodsId, parsed)
      setRows((prev) =>
        prev.map((r) => (r.goodsId === row.goodsId ? { ...r, residue: parsed } : r))
      )
      setEditingId(null)
      setToast(t('residue.saved'))
      setTimeout(() => setToast(null), 2000)
      // Сумма могла измениться — пересчитываем
      fetchGoodsResidueSum(categoryId ? Number(categoryId) : undefined).then(setSum).catch(() => {})
    } catch (err) {
      setError(t(err.message))
    }
  }

  const canGoPrev = pageInfo.number > 0
  const canGoNext = pageInfo.number < pageInfo.totalPages - 1

  return (
    <div className={styles.page}>
      <div className={styles.header}>
        <div className={styles.headerLeft}>
          <span className={styles.eyebrow}>Shop SaaS</span>
          <h1 className={styles.title}>{t('residue.title')}</h1>
        </div>
        <div className={styles.headerRight}>
          <Link className={styles.navLink} to="/cashier">{t('residue.toCashier')}</Link>
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
            className={styles.search}
            type="text"
            placeholder={t('residue.searchPlaceholder')}
            value={searchInput}
            onChange={(e) => setSearchInput(e.target.value)}
          />

          <select
            className={styles.categorySelect}
            value={categoryId}
            onChange={handleCategoryChange}
          >
            <option value="">{t('residue.allCategories')}</option>
            {categories.map((c) => (
              <option key={c.id} value={c.id}>{c.name}</option>
            ))}
          </select>

          {!admin && <span className={styles.adminHint}>{t('residue.adminOnlyHint')}</span>}
        </div>

        {sum !== null && (
          <div className={styles.sumBar}>
            <span>{t('residue.sumLabel')}</span>
            <span className={styles.sumValue}>{sum}</span>
          </div>
        )}

        <div className={styles.tableWrap}>
          {rows === null && <div className={styles.loading}>{t('residue.loading')}</div>}

          {rows !== null && rows.length === 0 && (
            <div className={styles.empty}>{t('residue.empty')}</div>
          )}

          {rows !== null && rows.length > 0 && (
            <table>
              <thead>
                <tr>
                  <th>{t('residue.columns.name')}</th>
                  <th>{t('residue.columns.code')}</th>
                  <th>{t('residue.columns.category')}</th>
                  <th className={styles.numeric}>{t('residue.columns.residue')}</th>
                  <th className={styles.numeric}>{t('residue.columns.price')}</th>
                </tr>
              </thead>
              <tbody>
                {rows.map((row) => (
                  <tr key={row.goodsId}>
                    <td className={styles.name}>
                      <Link
                        className={styles.nameLink}
                        to={`/goods/${row.goodsId}?${searchParams.toString()}`}
                      >
                        {row.name}
                      </Link>
                    </td>
                    <td className={styles.mono}>{row.code}</td>
                    <td>{row.categoryName ?? '—'}</td>
                    <td className={styles.numeric}>
                      <div className={styles.residueCell}>
                        {editingId === row.goodsId ? (
                          <>
                            <input
                              className={styles.editInput}
                              type="text"
                              inputMode="decimal"
                              value={editValue}
                              onChange={(e) => setEditValue(e.target.value)}
                              autoFocus
                            />
                            <button className={styles.saveBtn} onClick={() => saveEdit(row)}>
                              {t('residue.save')}
                            </button>
                            <button className={styles.cancelBtn} onClick={cancelEdit}>
                              {t('residue.cancel')}
                            </button>
                          </>
                        ) : (
                          <>
                            <span className={styles.residueValue}>{row.residue}</span>
                            {admin && (
                              <button className={styles.editBtn} onClick={() => startEdit(row)}>
                                {t('residue.edit')}
                              </button>
                            )}
                          </>
                        )}
                      </div>
                    </td>
                    <td className={`${styles.mono} ${styles.numeric}`}>{row.price}</td>
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

      {toast && <div className={styles.toast}>{toast}</div>}
    </div>
  )
}
