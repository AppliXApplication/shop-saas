const TOKEN_KEY = 'shop_auth_token'
const ROLE_KEY = 'shop_auth_role'

export function saveSession(token, role) {
  localStorage.setItem(TOKEN_KEY, token)
  localStorage.setItem(ROLE_KEY, role)
}

export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export function getRole() {
  return localStorage.getItem(ROLE_KEY)
}

export function isAdmin() {
  return getRole() === 'ROLE_ADMIN'
}

export function clearSession() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(ROLE_KEY)
}

function authHeaders() {
  const token = getToken()
  return token ? { Authorization: `Bearer ${token}` } : {}
}

/**
 * @returns {Promise<{token: string, login: string, role: string}>}
 */
export async function login(login_, password) {
  const response = await fetch('/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ login: login_, password }),
  })

  if (response.status === 401) {
    throw new Error('errors.invalidCredentials')
  }
  if (!response.ok) {
    throw new Error('errors.loginFailed')
  }

  return response.json()
}

export async function fetchGoodsResidue(search, categoryId, page = 0, size = 50) {
  const params = new URLSearchParams({ page: String(page), size: String(size) })
  if (search) params.set('search', search)
  if (categoryId) params.set('categoryId', String(categoryId))
  const response = await fetch(`/api/reports/goods-residue?${params}`, { headers: authHeaders() })
  if (response.status === 401) {
    throw new Error('errors.sessionExpired')
  }
  if (!response.ok) {
    throw new Error('errors.loadFailed')
  }
  return response.json() // Spring Page: { content, totalElements, totalPages, number, ... }
}

export async function fetchGoodsResidueSum(categoryId) {
  const params = new URLSearchParams()
  if (categoryId) params.set('categoryId', String(categoryId))
  const response = await fetch(`/api/reports/goods-residue/sum?${params}`, { headers: authHeaders() })
  if (!response.ok) {
    throw new Error('errors.loadFailed')
  }
  return response.json()
}

export async function fetchCategories() {
  const response = await fetch('/api/goods/categories', { headers: authHeaders() })
  if (!response.ok) {
    throw new Error('errors.loadFailed')
  }
  return response.json()
}

export async function fetchCaseRecordReport(date) {
  const params = new URLSearchParams()
  if (date) params.set('date', date) // формат YYYY-MM-DD
  const response = await fetch(`/api/reports/case-record?${params}`, { headers: authHeaders() })
  if (response.status === 401) {
    throw new Error('errors.sessionExpired')
  }
  if (!response.ok) {
    throw new Error('errors.loadFailed')
  }
  return response.json()
}
export async function fetchGoodsDetail(id) {
  const response = await fetch(`/api/goods/${id}`, { headers: authHeaders() })
  if (response.status === 401) {
    throw new Error('errors.sessionExpired')
  }
  if (!response.ok) {
    throw new Error('errors.loadFailed')
  }
  return response.json()
}

export async function updateGoodsDetail(id, data) {
  const response = await fetch(`/api/goods/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json', ...authHeaders() },
    body: JSON.stringify(data),
  })
  if (response.status === 403) {
    throw new Error('errors.forbidden')
  }
  if (!response.ok) {
    throw new Error('errors.saveFailed')
  }
  return response.json()
}

export async function updateGoodsResidue(goodsId, residue) {
  const response = await fetch(`/api/goods/${goodsId}/residue`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json', ...authHeaders() },
    body: JSON.stringify({ residue }),
  })
  if (response.status === 403) {
    throw new Error('errors.forbidden')
  }
  if (!response.ok) {
    throw new Error('errors.saveFailed')
  }
}

