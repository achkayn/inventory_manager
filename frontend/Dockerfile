# ── Stage 1: build
FROM node:24-alpine AS build
WORKDIR /app

# Cache node_modules before copying source
COPY package*.json ./
RUN npm ci

COPY . .

# VITE_API_URL is baked in at build time; override via --build-arg if needed
ARG VITE_API_URL=http://localhost:8080/api
ENV VITE_API_URL=$VITE_API_URL

RUN npm run build

# ── Stage 2: serve
FROM nginx:stable-alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
