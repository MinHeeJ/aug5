FROM node:20.11.0-alpine AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY tailwind.config.js postcss.config.js vite.config.ts vitest.config.ts tsconfig*.json index.html ./
COPY src ./src
RUN npm run build
FROM nginx:1.27.3-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
RUN addgroup -S appgroup \
    && adduser -S appuser -G appgroup \
    && mkdir -p /var/cache/nginx /var/run /var/log/nginx /var/lib/nginx \
    && touch /var/run/nginx.pid \
    && chown -R appuser:appgroup /etc/nginx/conf.d /usr/share/nginx/html /var/cache/nginx /var/run/nginx.pid /var/log/nginx /var/lib/nginx
USER appuser
EXPOSE 8080
HEALTHCHECK --interval=10s --timeout=3s CMD wget -qO- http://127.0.0.1:8080/ || exit 1
