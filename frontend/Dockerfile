# Frontend Farmacompara - Multi-stage Dockerfile
# For monorepo: backend/ and frontend/ are at project root

# Build stage
FROM node:20-alpine AS build
WORKDIR /app

# Copy package files and install dependencies
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci --prefer-offline

# Copy source code and build
COPY frontend/src ./src
COPY frontend/public ./public
COPY frontend/tsconfig.json frontend/vite.config.ts ./
RUN npm run build

# Runtime stage with nginx
FROM nginx:alpine AS runtime
WORKDIR /app

# Copy build output
COPY --from=build /app/dist /usr/share/nginx/html

# Copy nginx configuration
COPY frontend/nginx.conf /etc/nginx/conf.d/default.conf

# Expose port
EXPOSE 80

# Run nginx
CMD ["nginx", "-g", "daemon off;"]