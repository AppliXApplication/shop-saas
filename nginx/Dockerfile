# Стадия 1: собираем React в статические файлы
FROM node:20-alpine AS frontend-build
WORKDIR /frontend
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ ./
RUN npm run build

# Стадия 2: сама раздача — nginx с собранной статикой внутри образа
FROM nginx:alpine
COPY --from=frontend-build /frontend/dist /usr/share/nginx/html
RUN rm -f /etc/nginx/conf.d/default.conf
COPY nginx/conf.d /etc/nginx/conf.d
