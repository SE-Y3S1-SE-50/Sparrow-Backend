FROM astrocrpublic.azurecr.io/runtime:3.0-10

# ====== Build stage ======
FROM python:3.11-slim AS base

WORKDIR /app

# Prevent Python from writing pyc files & use unbuffered logs
ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

# Install system dependencies (customize if needed, e.g., libpq-dev for Postgres, etc.)
RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Copy dependency file (requirements.txt or pyproject.toml/poetry)
COPY requirements.txt .

# Install dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Copy service code
COPY . .

# Expose port (e.g., FastAPI / Flask / Django app)
EXPOSE 8080

# Healthcheck (adjust URL depending on your framework)
HEALTHCHECK --interval=20s --timeout=3s --retries=5 --start-period=20s \
  CMD curl -f http://localhost:8080/health || exit 1

# Command (adjust depending on your framework)
# Example: Flask
# CMD ["python", "app.py"]

# Example: FastAPI with Uvicorn
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8080"]
