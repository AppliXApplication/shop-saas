# Shop SaaS — фронтенд (React + Vite)

## Запуск

```bash
npm install
npm run dev
```

Откроется на `http://localhost:5173`. Запросы к `/api/*` автоматически
проксируются на бэкенд `http://localhost:8080` (см. `vite.config.js`) —
убедитесь, что Spring Boot приложение запущено.

Откройте `http://localhost:5173/login` — должна открыться страница входа.
После успешного логина токен сохраняется в `localStorage` и происходит
переход на `/`, где через `/api/auth/me` подтверждается, что токен рабочий.

## Структура

```
src/
  api/client.js       — обёртка над fetch: login(), работа с токеном
  pages/LoginPage.jsx  — страница входа
  pages/DashboardPage.jsx — заглушка после входа (замените на реальный дашборд)
  styles/global.css    — дизайн-токены (цвета, шрифты)
  App.jsx              — роутинг
```
