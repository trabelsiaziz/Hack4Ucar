# ── Stage 1: Install deps ─────────────────────────────────────────────────────
FROM node:20-alpine AS deps

WORKDIR /app

# Install pnpm (matches the lockfile present in the repo)
RUN corepack enable && corepack prepare pnpm@latest --activate

COPY package.json pnpm-lock.yaml ./
RUN pnpm install --frozen-lockfile

# ── Stage 2: Build ────────────────────────────────────────────────────────────
FROM node:20-alpine AS builder

WORKDIR /app

RUN corepack enable && corepack prepare pnpm@latest --activate

COPY --from=deps /app/node_modules ./node_modules
COPY . .

# NEXT_PUBLIC_* vars must be set at build time
ARG NEXT_PUBLIC_API_URL=http://backend:8080
ENV NEXT_PUBLIC_API_URL=$NEXT_PUBLIC_API_URL

RUN pnpm build

# ── Stage 3: Runtime ──────────────────────────────────────────────────────────
FROM node:20-alpine AS runner

WORKDIR /app

ENV NODE_ENV=production
ENV PORT=3000

# Non-root user
RUN addgroup -S ucar && adduser -S ucar -G ucar

# Copy only the files Next.js needs at runtime (standalone output)
COPY --from=builder /app/public        ./public
COPY --from=builder /app/.next/static  ./.next/static
COPY --from=builder /app/.next/standalone ./

RUN chown -R ucar:ucar /app
USER ucar

EXPOSE 3000

CMD ["node", "server.js"]
