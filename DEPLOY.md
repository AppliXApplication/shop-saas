# Деплой Shop SaaS на сервер

## Важно: на этом сервере уже есть общий Caddy

На сервере `srv1785944` уже работает `newsbot-caddy` — общий реверс-прокси на
портах 80/443 для нескольких проектов. Наш `nginx` НЕ публикует 80/443 наружу,
а подключается к сети `newsbot-backend_default` — Caddy проксирует на него
по имени контейнера `shop-nginx`. SSL получает и продлевает Caddy, отдельный
certbot не нужен.

## Структура

```
.
├── docker-compose.prod.yml
├── .env.prod.example
├── backend/
├── frontend/
└── nginx/
    ├── Dockerfile
    └── conf.d/shop.conf
```

## Шаг 1-3 (уже сделаны)

Архив распакован, `.env.prod` заполнен, домен `izob.applix.top` прописан в
`nginx/conf.d/shop.conf`.

## Шаг 4. Запуск нашего стека

```bash
docker compose -f docker-compose.prod.yml up -d --build
```

Проверка изнутри сети (напрямую к nginx-контейнеру, ещё без Caddy):
```bash
docker exec shop-nginx wget -qO- http://localhost/actuator/health
```

## Шаг 5. Регистрация домена в существующем Caddy

Откройте `Caddyfile` вашего проекта `newsbot` (там же, где лежит
`docker-compose.yml`, запускающий `newsbot-caddy`) и добавьте новый блок:

```
izob.applix.top {
    reverse_proxy shop-nginx:80
}
```

Сохраните и перечитайте конфиг у Caddy — если `Caddyfile` смонтирован как volume,
достаточно рестарта контейнера:
```bash
docker restart newsbot-caddy
```

Проверьте:
```bash
curl -I https://izob.applix.top
```
Должно вернуть `HTTP/2 200` — Caddy сам получит сертификат Let's Encrypt при
первом обращении к домену (может занять несколько секунд).

Откройте `https://izob.applix.top` в браузере — должна открыться страница логина
уже с рабочим HTTPS.

## Обновление после изменений в коде

```bash
docker compose -f docker-compose.prod.yml up -d --build
```
Caddy трогать не нужно — конфиг для домена меняется, только если меняется
имя контейнера/сети.

## Диагностика

```bash
docker compose -f docker-compose.prod.yml logs -f app     # логи бэкенда
docker compose -f docker-compose.prod.yml logs -f nginx   # логи nginx
docker logs newsbot-caddy                                  # логи Caddy
```

