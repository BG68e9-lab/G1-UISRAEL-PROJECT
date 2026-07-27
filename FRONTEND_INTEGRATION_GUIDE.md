# 🚀 Guía de Integración Frontend - DRINKHOUSE API

## 🔧 CONFIGURACIÓN INICIAL DEL CLIENTE HTTP

### ⚠️ IMPORTANTE: Evitar URLs duplicadas

El backend tiene todos los endpoints bajo `/api/v1`. El frontend debe configurar su cliente HTTP (Axios, Fetch, etc.) de una de estas dos formas:

### ✅ Opción 1: baseURL con /api/v1 (RECOMENDADO)

```javascript
// axios.config.js o api.config.js
import axios from 'axios';

const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api/v1',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

export default apiClient;
```

**Entonces las llamadas son SIN /api/v1:**
```javascript
// ✅ CORRECTO
apiClient.get('/productos')
apiClient.get('/usuarios')
apiClient.post('/ordenes-compra', data)
```

