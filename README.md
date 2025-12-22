# 🎮 Oh Hell! Card Game

Proyecto de Ingeniería del Software - Universidad Politécnica de València

## 👥 Equipo de Desarrollo

**Equipo: Caos Controlado** 🎯

| Nombre | Email |
|--------|-------|
| **Tomás Engonga Ovono Nsuga** | teovonsu@upv.edu.es |
| **Rongheng Xu** | ronghengx@gmail.com |
| **Wang Wenjie** | xiaozhu9728@126.com |
| **Gabriel Alexander Morales Aldana** | gamorald@epsa.upv.es |
| **Joan Torregrosa Alonso** | jtoralo@epsa.upv.es |

---
                     
**Curso:** 2025-2026

---

## 📋 Descripción

Implementación del juego de cartas **Oh Hell!** usando:
- **Backend:** Java 25 + Jakarta EE + JAX-RS
- **Base de Datos:** PostgreSQL (Render - Frankfurt)
- **Frontend:** HTML5 + CSS3 + JavaScript Vanilla
- **API:** REST (JSON)
- **Estado:** 75% completado

---

## 🚀 Inicio Rápido (Desarrollo Local)

### 1. Configurar Variables de Entorno

```bash
source config_env.sh
```

### 2. Opción A: Usar Script de Prueba

```bash
./scripts/test-frontend.sh
```

Este script verifica automáticamente:
- ✅ Todos los archivos HTML
- ✅ Archivos CSS y JavaScript
- ✅ Conexión con base de datos
- ✅ Estado del servidor frontend

### 3. Opción B: Iniciar Manualmente

**Terminal 1 - Frontend:**
```bash
cd app/src/main/webapp
python3 -m http.server 8000
# Acceso: http://localhost:8000
```

**Terminal 2 - Backend (Opcional, requiere TomEE):**
```bash
cd app
source ../config_env.sh
mvn clean package -DskipTests
# Desplegar WAR en TomEE si está instalado
```

---

## 🌐 URLs Disponibles

| Componente | URL | Estado |
|-----------|-----|--------|
| **Frontend** | http://localhost:8000 | ✅ Desarrollo |
| **Login** | http://localhost:8000/login.html | ✅ Disponible |
| **Juego** | http://localhost:8000/game.html | 🟡 En desarrollo |
| **Backend API** | http://localhost:8080/api/v1/ | ✅ Compilado |
| **Test BD** | http://localhost:8080/api/v1/testdb | 🟡 Requiere TomEE |

---

## 📁 Estructura del Proyecto

```
juego_de_cartas/
├── app/                        # Aplicación completa (Backend + Frontend)
│   ├── BBDD/                  # Scripts SQL de la base de datos
│   │   ├── 01_schema.sql     # Esquema principal
│   │   └── 02_test_data.sql  # Datos de prueba
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/ohhell/ohhellapi/
│   │   │   │   ├── dao/              # Data Access Objects
│   │   │   │   │   ├── BidDAO.java
│   │   │   │   │   ├── GameDAO.java
│   │   │   │   │   ├── PlayerDAO.java
│   │   │   │   │   ├── RoundDAO.java
│   │   │   │   │   └── TrickDAO.java
│   │   │   │   ├── models/           # Modelos de dominio
│   │   │   │   │   ├── Bid.java
│   │   │   │   │   ├── Card.java
│   │   │   │   │   ├── Game.java
│   │   │   │   │   ├── Player.java
│   │   │   │   │   ├── Round.java
│   │   │   │   │   └── Trick.java
│   │   │   │   ├── resources/        # Endpoints REST
│   │   │   │   │   ├── BidResource.java
│   │   │   │   │   ├── GameResource.java
│   │   │   │   │   ├── PlayerResource.java
│   │   │   │   │   ├── RoundResource.java
│   │   │   │   │   ├── TestDatabaseResource.java
│   │   │   │   │   └── TrickResource.java
│   │   │   │   └── utils/            # Utilidades
│   │   │   ├── resources/            # Configuración
│   │   │   └── webapp/               # Frontend
│   │   │       ├── WEB-INF/
│   │   │       ├── assets/           # Recursos estáticos
│   │   │       │   ├── cards/        # Imágenes de cartas
│   │   │       │   ├── images/       # Imágenes generales
│   │   │       │   └── sounds/       # Efectos de sonido
│   │   │       ├── css/
│   │   │       │   └── main.css
│   │   │       ├── js/
│   │   │       │   ├── api-client.js
│   │   │       │   ├── game-state.js
│   │   │       │   ├── ui-manager.js
│   │   │       │   └── main.js
│   │   │       ├── index.html        # Página de inicio
│   │   │       ├── login.html
│   │   │       ├── home.html
│   │   │       ├── game.html         # Interfaz del juego
│   │   │       ├── waiting-room.html
│   │   │       └── results.html
│   │   └── test/
│   └── pom.xml                       # Maven configuration
├── docs/                             # Documentación técnica
│   ├── INFORME_SETUP.md             # Guía de configuración
│   ├── POSTMAN_README.md            # Guía de pruebas API
│   ├── postman_collection_ohhell.json
│   └── HISTORICO.md                 # Histórico de documentación
├── docs/                             # Documentación académica
│   ├── L4_Modelo de datos.md
│   ├── L5_Servicios.md
│   ├── L6_GUI_Logica.md
│   ├── REGLAS_DEL_JUEGO.md
│   └── ...
├── scripts/                          # Scripts de automatización
│   └── deploy-tomee.sh              # Despliegue automático
├── .github/workflows/               # CI/CD
├── config_env.sh                    # Variables de entorno
└── README.md
```

---

## 🗄️ Base de Datos

**Proveedor:** Render (PostgreSQL 16)  
**Ubicación:** Frankfurt, Alemania  
**Conexión:**

```bash
Host: dpg-d4u525idbo4c73faglm0-a.frankfurt-postgres.render.com
Database: ohhell_db_mqyx
User: database_tomas
```

### Tablas Principales

- `games` - Partidas
- `players` - Jugadores
- `game_players` - Relación N:M
- `rounds` - Rondas de cada partida
- `bids` - Apuestas de los jugadores
- `tricks` - Bazas jugadas
- `played_cards` - Cartas jugadas en cada baza

---

## 🔌 API REST

Base URL: `http://localhost:8080/api/v1`

### Endpoints Principales

#### Games
- `GET /games` - Listar todas las partidas
- `POST /games` - Crear nueva partida
- `GET /games/{id}` - Obtener partida específica
- `PUT /games/{id}` - Actualizar partida
- `DELETE /games/{id}` - Eliminar partida
- `POST /games/{id}/start` - Iniciar partida

#### Players
- `GET /players` - Listar jugadores
- `POST /players` - Crear jugador
- `GET /players/{id}` - Obtener jugador
- `POST /games/{gameId}/players/{playerId}` - Unir jugador a partida

#### Rounds
- `POST /games/{gameId}/rounds` - Crear nueva ronda
- `GET /rounds/{id}` - Obtener ronda
- `POST /rounds/{id}/deal` - Repartir cartas

#### Bids
- `POST /rounds/{roundId}/bids` - Hacer apuesta
- `GET /rounds/{roundId}/bids` - Ver apuestas de la ronda

#### Tricks
- `POST /rounds/{roundId}/tricks` - Crear nueva baza
- `POST /tricks/{trickId}/cards` - Jugar carta

Ver documentación completa en `docs/POSTMAN_README.md`

---

## 🎮 Reglas del Juego

Ver `docs/REGLAS_DEL_JUEGO.md` para las reglas completas.

**Resumen:**
1. Cada jugador recibe un número de cartas que varía por ronda
2. Los jugadores apuestan cuántas bazas ganarán
3. Se juegan las bazas según las reglas de triunfo
4. Se puntúa según aciertos/fallos en la apuesta

---

## 🛠️ Tecnologías

### Backend
- Java 25 (OpenJDK)
- Jakarta EE 10
- JAX-RS (REST API)
- JDBC (PostgreSQL)
- Maven 3.8.7

### Frontend
- HTML5
- CSS3 (Variables CSS, Flexbox, Grid)
- JavaScript ES6+ (Vanilla)
- Arquitectura modular (API Client, State, UI Manager)

### Infraestructura
- TomEE 10.0.0-M3
- PostgreSQL 16 (Render)
- DataGrip (gestión BD)
- Postman (pruebas API)

---

## 📚 Documentación Adicional

- **Setup completo:** `docs/INFORME_SETUP.md`
- **Pruebas API:** `docs/POSTMAN_README.md`
- **Análisis académico:** `docs/ANALISIS_L4_L5_L6.md`
- **Histórico:** `docs/HISTORICO.md`

---

## 👥 Equipo

**Desarrolladores:** Tomás Engonga Ovono Nsuga,
                     Joan Torregrosa Alonso,
                     Rongheng Xu,
                     Wang Wenjie,
                     Gabriel Alexander Morales Aldana.
                     
**Universidad:** Politécnica de València  
**Asignatura:** Ingeniería del Software

---

## 📝 Licencia

Proyecto académico - UPV 2025-2026
