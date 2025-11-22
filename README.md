# 🃏 Oh Hell! - Juego de Cartas Multijugador

[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/rxu1MK89)

## 📖 Descripción

**Oh Hell!** es un juego de cartas multijugador en tiempo real donde los jugadores deben predecir exactamente cuántas bazas (rondas) ganarán. El desafío está en acertar tu predicción: si fallas, pierdes vidas. ¡El último jugador en pie gana!

Este proyecto implementa una versión web completa del juego clásico, permitiendo jugar de 3 a 7 jugadores simultáneamente desde cualquier navegador.

---

## 🎮 Características Principales

- 🃏 **Juego completo de cartas** con baraja francesa de 52 cartas
- 👥 **Multijugador en tiempo real** (3-7 jugadores) usando WebSockets
- 💔 **Sistema de vidas**: Cada jugador comienza con 5 vidas
- 🎯 **Apuestas estratégicas** con regla especial del último jugador
- 📱 **Responsive design**: Juega desde móvil, tablet o desktop
- ⚡ **API REST completa** con Java + Jakarta EE
- 🗄️ **Base de datos PostgreSQL** en la nube (Render)
- 🎨 **Interfaz intuitiva** con animaciones y feedback visual

---

## 📁 Estructura del Proyecto
```
iso-2025-proyect-caos-controlado-1/
├── backend/                    # 🔧 API REST (Java + Jakarta EE)
│   ├── src/main/java/          
│   │   └── com/ohhell/ohhellapi/
│   │       ├── models/         # Entidades del juego
│   │       ├── dao/            # Acceso a datos (PostgreSQL)
│   │       ├── resources/      # Endpoints REST
│   │       └── utils/          # Utilidades
│   ├── src/main/webapp/        # Configuración web + docs API
│   ├── pom.xml                 # Dependencias Maven
│   └── target/                 # WAR compilado
│
├── src/main/resources/static/  # 🎨 Frontend del juego
│   ├── assets/                 
│   │   ├── cards/              # Cartas SVG/PNG profesionales
│   │   ├── sounds/             # Efectos de sonido
│   │   └── images/             # Imágenes del juego
│   ├── css/                    # Estilos
│   ├── js/                     # Lógica del cliente
│   ├── home.html               # Página principal
│   ├── game.html               # Interfaz del juego
│   ├── login.html              # Autenticación
│   └── waiting-room.html       # Sala de espera
│
└── Docs/                       # 📚 Documentación completa
    ├── REGLAS_DEL_JUEGO.md
    ├── PLAN_SCRUM.md
    ├── Requerimientos_*.md
    ├── Documentacion_*.docx     # Docs técnicas Sprint 3
    └── meeting_*.pdf            # Actas de reuniones
```

---

## 🚀 Instalación y Despliegue

### Requisitos previos:
- **Java 21** o superior
- **Maven 3.8+**
- **Apache TomEE WebProfile 10.1.2**
- **PostgreSQL** (base de datos en Render)

### 1️⃣ Compilar el Backend:
```bash
cd backend
./mvnw clean package -DskipTests
```

Esto genera: `target/OhHellAPI-1.0-SNAPSHOT.war`

### 2️⃣ Desplegar en TomEE:
```bash
# Copiar WAR a TomEE
cp backend/target/OhHellAPI-1.0-SNAPSHOT.war \
   ~/Programacion/servidores/apache-tomee-webprofile-10.1.2/webapps/ROOT.war

# Iniciar servidor
cd ~/Programacion/servidores/apache-tomee-webprofile-10.1.2
./bin/catalina.sh run
```

### 3️⃣ Acceder a la aplicación:

Abre tu navegador en:
- 🎮 **Juego**: http://localhost:8080/home.html
- 📖 **API Docs**: http://localhost:8080/
- 🔌 **API REST**: http://localhost:8080/api/v1/

---

## 🌐 API REST - Endpoints Disponibles

### 📡 Sistema
- `GET /api/v1/hello` - Test de conexión
- `GET /api/v1/testdb` - Verificar PostgreSQL

### 👥 Jugadores
- `GET /api/v1/players` - Listar jugadores
- `GET /api/v1/players/{id}` - Obtener jugador
- `POST /api/v1/players` - Crear jugador
- `PUT /api/v1/players/{id}` - Actualizar jugador
- `DELETE /api/v1/players/{id}` - Eliminar jugador

### 🎮 Partidas
- `GET /api/v1/games` - Listar partidas
- `GET /api/v1/games/{id}` - Obtener partida
- `POST /api/v1/games` - Crear partida
- `POST /api/v1/games/{id}/join` - Unirse a partida
- `PUT /api/v1/games/{id}` - Actualizar partida

### 🔄 Rondas, 💰 Apuestas, 🎴 Bazas
Ver documentación completa en: http://localhost:8080/

---

## 🗄️ Base de Datos

**PostgreSQL en Render.com:**
- Host: `dpg-ct3g5bdsvqrc73874o10-a.oregon-postgres.render.com`
- Database: `ohhell_db`
- Puerto: `5432`
- Conexión SSL habilitada

**Tablas principales:**
- `players` - Información de jugadores
- `games` - Partidas activas/históricas
- `rounds` - Rondas de cada partida
- `bids` - Apuestas de los jugadores
- `tricks` - Bazas jugadas

---

## 📚 Stack Tecnológico

### Backend:
- **Java 21**
- **Jakarta EE 10** (JAX-RS, CDI)
- **Apache TomEE WebProfile 10.1.2**
- **PostgreSQL** con JDBC
- **Maven** para gestión de dependencias

### Frontend:
- **HTML5**, **CSS3**, **JavaScript** (Vanilla)
- **WebSockets** (próximamente)
- **SVG Cards** profesionales

### Herramientas:
- **Git** + GitHub Classroom
- **IntelliJ IDEA** / VS Code
- **Postman** para testing API
- **DataGrip** para gestión BD

---

## 🎯 Cómo Jugar

1. **Crear o unirte a una partida**
2. **Esperar a que se unan entre 3-7 jugadores**
3. **Apostar** cuántas bazas crees que ganarás
4. **Jugar tus cartas** siguiendo las reglas
5. **¡Sobrevivir!** No pierdas todas tus vidas

Para reglas detalladas: [REGLAS_DEL_JUEGO.md](Docs/REGLAS_DEL_JUEGO.md)

---

## 📚 Documentación Completa

En la carpeta `Docs/`:

- **[REGLAS_DEL_JUEGO.md](Docs/REGLAS_DEL_JUEGO.md)** - Reglas oficiales con ejemplos
- **[Requerimientos_Funcionales.md](Docs/Requerimientos_Funcionales.md)** - 30 casos de uso
- **[Requerimientos_Técnicos.md](Docs/Requerimientos_Técnicos.md)** - Arquitectura del sistema
- **[PLAN_SCRUM.md](Docs/PLAN_SCRUM.md)** - Metodología ágil y sprints
- **Documentacion_*.docx** - Documentación técnica detallada
- **Diagramas UML** - Casos de uso, clases, secuencia

---

## 👥 Equipo de Desarrollo - Sprint 3

**Equipo: Caos Controlado** 🎯

| Nombre | Rol | Email |
|--------|-----|-------|
| **Tomás Engonga Ovono Nsuga** | Product Owner & Developer | teovonsu@upv.edu.es |
| **Rongheng Xu** | Scrum Master | ronghengx@gmail.com |
| **Wang Wenjie** | Developer | xiaozhu9728@126.com |
| **Gabriel Alexander Morales Aldana** | Developer | gamorald@epsa.upv.es |
| **Joan Torregrosa Alonso** | Developer | jtoralo@epsa.upv.es |

---

## 🏆 Progreso del Proyecto

- ✅ **Sprint 1**: Definición y planificación
- ✅ **Sprint 2**: Diseño de interfaz y reglas
- ✅ **Sprint 3**: Backend API REST + Integración BD (Actual)
- ⏳ **Sprint 4**: WebSockets y tiempo real
- ⏳ **Sprint 5**: Testing y despliegue final

---

## 📝 Licencia

Este proyecto es de código abierto para uso educativo.  
**Universitat Politècnica de València - Ingeniería del Software**

---

## 🙏 Agradecimientos

- **Cartas SVG**: [htdebeer/SVG-cards](https://github.com/htdebeer/SVG-cards)
- **Render.com**: Hosting de base de datos PostgreSQL
- **Apache TomEE**: Servidor de aplicaciones

---

**¡Desarrollado con ❤️ por el equipo Caos Controlado!** 🃏🎮
