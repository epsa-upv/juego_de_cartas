# Plan Scrum - Oh Hell! Game
## Equipo de 5 Personas

[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/rxu1MK89)

---

## 📋 Información del Proyecto

- **Nombre del Proyecto:** Oh Hell! Card Game
- **Duración Estimada:** 5 Sprints (10 semanas)
- **Tamaño del Equipo:** 5 personas
- **Duración del Sprint:** 2 semanas
- **Metodología:** Scrum

---

## 👥 Equipo del Proyecto

| Rol | Nombre | Email |
|-----|--------|-------|
| **Product Owner** | Rongheng Xu | ronghengx@gmail.com |
| **Scrum Master** | Wang Wenjie | xiaozhu9728@126.com |
| **Developer Backend** | Gabriel Alexander Morales Aldana | gamorald@epsa.upv.es |
| **Developer Frontend** | Joan Torregrosa Alonso | jtoralo@epsa.upv.es |
| **QA/Tester + DevOps** | Tomás Engonga Ovono Nsuga | teovonsu@upv.edu.es |

### Responsabilidades por Rol

#### Product Owner - Rongheng Xu
- Define y prioriza el Product Backlog
- Acepta o rechaza el trabajo completado
- Representa la visión del producto
- Disponible para aclarar dudas del equipo
- Toma decisiones sobre features

#### Scrum Master - Wang Wenjie
- Facilita las ceremonias Scrum
- Elimina impedimentos
- Protege al equipo de interrupciones
- Asegura que se sigan las prácticas ágiles
- Mentoriza al equipo en Scrum

#### Developer Backend - Gabriel Alexander
- Implementación de lógica de negocio en Java
- Desarrollo de API REST
- Gestión de WebSockets
- Tests unitarios del backend
- Integración con base de datos (opcional)

#### Developer Frontend - Joan Torregrosa
- Implementación de interfaz con HTML/CSS
- Lógica del cliente en JavaScript
- Integración con API backend
- Diseño responsive
- Animaciones y UX

#### QA/Tester + DevOps - Tomás Engonga
- Testing manual y automatizado
- Reporte y seguimiento de bugs
- Configuración de CI/CD
- Despliegue y documentación
- Control de calidad

**Nota:** En Scrum, todos pueden colaborar en todas las tareas. Estos son roles de enfoque principal.

---

## 📅 Ceremonias Scrum

### Sprint Planning (Inicio de cada Sprint)
- **Duración:** 4 horas
- **Frecuencia:** Primera mañana del Sprint
- **Participantes:** Todo el equipo
- **Formato:**
  1. Revisar objetivos del Sprint
  2. Seleccionar historias del Backlog
  3. Descomponer en tareas
  4. Estimar esfuerzo
  5. Comprometerse con el Sprint Goal

### Daily Stand-up
- **Duración:** 15 minutos máximo
- **Frecuencia:** Todos los días laborables a las 9:00 AM
- **Participantes:** Todo el equipo
- **Formato:** Cada persona responde:
  1. ¿Qué hice ayer?
  2. ¿Qué haré hoy?
  3. ¿Tengo algún impedimento?

### Sprint Review (Final del Sprint)
- **Duración:** 2 horas
- **Frecuencia:** Último día del Sprint (viernes tarde)
- **Participantes:** Todo el equipo + stakeholders
- **Objetivo:** Demostrar el incremento funcional
- **Formato:**
  1. Demo del trabajo completado
  2. Feedback de stakeholders
  3. Actualizar Product Backlog

### Sprint Retrospective (Final del Sprint)
- **Duración:** 1.5 horas
- **Frecuencia:** Después del Sprint Review (viernes)
- **Participantes:** Solo el equipo de desarrollo
- **Objetivo:** Mejora continua del proceso
- **Formato - Start/Stop/Continue:**
  - ¿Qué empezar a hacer?
  - ¿Qué dejar de hacer?
  - ¿Qué continuar haciendo?

### Backlog Refinement (Opcional)
- **Duración:** 1 hora
- **Frecuencia:** Mitad del Sprint (miércoles)
- **Objetivo:** Preparar historias para el próximo Sprint

---

## 📊 Product Backlog Inicial

### Épicas del Proyecto
1. **Setup del Proyecto** (Sprint 1)
2. **Gestión de Jugadores y Partidas** (Sprint 1-2)
3. **Mecánica de Apuestas** (Sprint 2)
4. **Mecánica de Juego y Bazas** (Sprint 3)
5. **Sistema de Vidas y Puntuación** (Sprint 3-4)
6. **Multijugador en Tiempo Real** (Sprint 4)
7. **Pulido y Optimización** (Sprint 5)

---

## 🎯 Sprint 1: Fundamentos del Juego
**Duración:** Semanas 1-2  
**Objetivo:** Configurar infraestructura y desarrollar funcionalidades básicas del juego

### Historias de Usuario

#### US-1.1: Configuración del Proyecto
**Como** desarrollador  
**Quiero** configurar el proyecto Spring Boot con todas las dependencias  
**Para** comenzar a desarrollar funcionalidades

**Criterios de Aceptación:**
- [ ] Proyecto Spring Boot inicializado desde Spring Initializr
- [ ] Estructura de carpetas según arquitectura MVC
- [ ] Dependencias Maven configuradas (Web, WebSocket, Lombok, DevTools, Test)
- [ ] Proyecto compila sin errores
- [ ] Git repository configurado en GitHub Classroom
- [ ] README.md básico creado
- [ ] .gitignore configurado

**Puntos:** 3  
**Asignado a:** Gabriel (Backend) + Joan (Frontend)

---

#### US-1.2: Crear Baraja de Cartas
**Como** sistema  
**Quiero** tener una baraja francesa de 52 cartas  
**Para** poder repartirlas en las partidas

**Criterios de Aceptación:**
- [ ] Enum `Suit` con 4 palos (HEARTS, DIAMONDS, CLUBS, SPADES)
- [ ] Enum `Rank` con 13 valores (ACE, 2-10, JACK, QUEEN, KING)
- [ ] Clase `Card` con suit, rank y métodos equals/hashCode
- [ ] Clase `Deck` con lista de 52 cartas
- [ ] Método `shuffle()` usando algoritmo Fisher-Yates
- [ ] Tests unitarios: barajado, creación correcta

**Puntos:** 5  
**Asignado a:** Gabriel (Backend)

---

#### US-1.3: Sistema de Reparto de Cartas
**Como** sistema  
**Quiero** repartir el número correcto de cartas según la ronda  
**Para** seguir las reglas del juego (5→4→3→2→1)

**Criterios de Aceptación:**
- [ ] Clase `Round` con número de ronda (1-5)
- [ ] Método `dealCards(numPlayers)` en Deck
- [ ] Ronda 1: 5 cartas, Ronda 2: 4 cartas, ..., Ronda 5: 1 carta
- [ ] Validación de cartas suficientes en mazo
- [ ] Remover cartas del deck al repartir
- [ ] Tests con diferentes números de jugadores

**Puntos:** 5  
**Asignado a:** Gabriel (Backend)

---

#### US-1.4: Gestión de Jugadores
**Como** jugador  
**Quiero** poder crear y unirme a una partida  
**Para** jugar con otros

**Criterios de Aceptación:**
- [ ] Clase `Player` con id, name, lives (inicialmente 5)
- [ ] Clase `Game` con lista de jugadores
- [ ] Método `addPlayer(name)` con validación de nombres únicos
- [ ] Validación: mínimo 3, máximo 7 jugadores
- [ ] Método `assignFirstDealer()` aleatorio
- [ ] Tests unitarios de validaciones

**Puntos:** 5  
**Asignado a:** Gabriel (Backend)

---

#### US-1.5: Interfaz Básica HTML
**Como** jugador  
**Quiero** ver una página web básica  
**Para** acceder al juego

**Criterios de Aceptación:**
- [ ] Página index.html con estructura semántica
- [ ] CSS básico con reset y variables
- [ ] Layout responsive con Flexbox/Grid
- [ ] Secciones: header, área de juego, controles, puntuaciones
- [ ] Assets de cartas integrados (SVG downloaded)
- [ ] Funciona en móvil y desktop

**Puntos:** 8  
**Asignado a:** Joan (Frontend)

---

**Total Puntos Sprint 1:** 26 puntos  
**Velocity objetivo:** 25-30 puntos

---

## 🎯 Sprint 2: Apuestas y Triunfo
**Duración:** Semanas 3-4  
**Objetivo:** Implementar sistema de apuestas con la regla especial y determinación de triunfo

### Historias de Usuario

#### US-2.1: Determinación del Triunfo
**Como** sistema  
**Quiero** determinar el palo de triunfo cada ronda  
**Para** aplicar las reglas correctamente

**Criterios de Aceptación:**
- [ ] Después de repartir, revelar carta superior del mazo
- [ ] Atributo `trumpSuit` en Round
- [ ] Las cartas de triunfo vencen a otros palos
- [ ] Mostrar triunfo en logs/consola
- [ ] Tests unitarios

**Puntos:** 3  
**Asignado a:** Gabriel (Backend)

---

#### US-2.2: Sistema de Apuestas
**Como** jugador  
**Quiero** apostar cuántas bazas ganaré  
**Para** intentar acertar y no perder vidas

**Criterios de Aceptación:**
- [ ] Endpoint POST `/api/games/{id}/bet` con BetDTO
- [ ] El jugador a la derecha del dealer apuesta primero
- [ ] Validación de rango (0 a numCartas)
- [ ] Guardar apuesta en objeto Player
- [ ] Orden correcto de apuestas
- [ ] Response con estado actualizado

**Puntos:** 5  
**Asignado a:** Gabriel (Backend)

---

#### US-2.3: Validación de Última Apuesta (Regla Especial)
**Como** sistema  
**Quiero** validar la apuesta del último jugador  
**Para** que la suma total NO sea igual al número de bazas

**Criterios de Aceptación:**
- [ ] Calcular suma de apuestas previas
- [ ] Calcular valor prohibido: `numBazas - sumaActual`
- [ ] Validar en backend antes de aceptar
- [ ] Lanzar `InvalidBetException` con mensaje claro
- [ ] Tests con múltiples escenarios:
  - 4 jugadores, 5 cartas: 2+1+1, último NO puede apostar 1
  - 3 jugadores, 3 cartas: 1+1, último NO puede apostar 1
  - Casos válidos también testeados

**Puntos:** 8  
**Asignado a:** Gabriel (Backend)

---

#### US-2.4: Interfaz de Apuestas
**Como** jugador  
**Quiero** ver las apuestas de todos y hacer la mía  
**Para** tomar decisiones informadas

**Criterios de Aceptación:**
- [ ] Mostrar lista de jugadores con sus apuestas
- [ ] Indicar turno actual con highlight
- [ ] Input numérico para ingresar apuesta
- [ ] Botones 0-N para selección rápida
- [ ] Validación en frontend (deshabilitar valor prohibido para último)
- [ ] Feedback visual de apuestas inválidas
- [ ] Llamar a API POST /bet al confirmar
- [ ] Actualizar UI con respuesta

**Puntos:** 8  
**Asignado a:** Joan (Frontend)

---

#### US-2.5: Visualización del Triunfo
**Como** jugador  
**Quiero** ver claramente qué palo es triunfo  
**Para** planificar mis jugadas

**Criterios de Aceptación:**
- [ ] Área destacada para mostrar triunfo
- [ ] Icono del palo de triunfo (♥♦♣♠)
- [ ] Carta de triunfo visible
- [ ] Color distintivo (rojo para ♥♦, negro para ♣♠)
- [ ] Animación al revelar (fade in)
- [ ] Responsive en móvil y desktop

**Puntos:** 3  
**Asignado a:** Joan (Frontend)

---

#### US-2.6: WebSocket Setup Básico
**Como** desarrollador  
**Quiero** configurar WebSocket  
**Para** preparar comunicación en tiempo real

**Criterios de Aceptación:**
- [ ] Clase `WebSocketConfig` con STOMP
- [ ] Topics definidos: `/topic/game/{gameId}`
- [ ] Cliente JS se conecta exitosamente
- [ ] Prueba de envío/recepción de mensaje simple
- [ ] Manejo de errores de conexión

**Puntos:** 5  
**Asignado a:** Gabriel (Backend) + Joan (Frontend)

---

**Total Puntos Sprint 2:** 32 puntos

---

## 🎯 Sprint 3: Mecánica de Juego y Bazas
**Duración:** Semanas 5-6  
**Objetivo:** Implementar el juego de cartas, determinación de ganadores y sistema de vidas

### Historias de Usuario

#### US-3.1: Jugar Carta
**Como** jugador  
**Quiero** jugar una carta de mi mano  
**Para** participar en la baza

**Criterios de Aceptación:**
- [ ] Endpoint POST `/api/games/{id}/play` con cardId
- [ ] Validar que sea el turno del jugador
- [ ] Validar que la carta esté en mano del jugador
- [ ] Remover carta de la mano
- [ ] Agregar carta a la baza actual
- [ ] Avanzar al siguiente jugador
- [ ] Tests de validaciones

**Puntos:** 5  
**Asignado a:** Gabriel (Backend)

---

#### US-3.2: Validación de Seguir Palo
**Como** sistema  
**Quiero** validar que se siga el palo  
**Para** hacer cumplir las reglas

**Criterios de Aceptación:**
- [ ] Identificar palo inicial de la baza (primera carta jugada)
- [ ] Verificar si jugador tiene cartas de ese palo
- [ ] Si tiene el palo, DEBE jugar ese palo
- [ ] Si NO tiene, puede jugar cualquier carta
- [ ] Lanzar `InvalidMoveException` si rompe regla
- [ ] Tests exhaustivos:
  - Tiene palo, juega palo → OK
  - Tiene palo, juega otro → ERROR
  - NO tiene palo, juega cualquiera → OK

**Puntos:** 8  
**Asignado a:** Gabriel (Backend)

---

#### US-3.3: Determinar Ganador de Baza
**Como** sistema  
**Quiero** determinar quién gana cada baza  
**Para** actualizar el contador de bazas ganadas

**Criterios de Aceptación:**
- [ ] Lógica: Si hay triunfo → gana triunfo más alto
- [ ] Si NO hay triunfo → gana carta más alta del palo inicial
- [ ] Jerarquía correcta: A>K>Q>J>10>9>8>7>6>5>4>3>2
- [ ] Método `determineTrickWinner()` en TrickService
- [ ] Actualizar `tricksWon` del jugador ganador
- [ ] El ganador inicia la siguiente baza
- [ ] Tests con múltiples escenarios (triunfo, sin triunfo, empates imposibles)

**Puntos:** 8  
**Asignado a:** Gabriel (Backend)

---

#### US-3.4: Sistema de Vidas
**Como** sistema  
**Quiero** calcular pérdida de vidas al final de cada ronda  
**Para** eliminar jugadores

**Criterios de Aceptación:**
- [ ] Clase `LivesService` con método `calculateLivesLost()`
- [ ] Fórmula: `livesLost = |bet - tricksWon|`
- [ ] Si acierta (bet == tricksWon) → 0 vidas perdidas
- [ ] Si NO acierta → restar diferencia
- [ ] Actualizar lives en Player
- [ ] Tests con ejemplos:
  - Bet 3, won 3 → -0 vidas ✅
  - Bet 3, won 1 → -2 vidas
  - Bet 2, won 5 → -3 vidas
- [ ] No permitir vidas negativas (mínimo 0)

**Puntos:** 5  
**Asignado a:** Gabriel (Backend)

---

#### US-3.5: Detección de Eliminación
**Como** sistema  
**Quiero** detectar cuando un jugador llega a 0 vidas  
**Para** eliminarlo del juego

**Criterios de Aceptación:**
- [ ] Verificar lives después de cada ronda
- [ ] Si lives <= 0, marcar jugador como eliminado
- [ ] Atributo `isEliminated` en Player
- [ ] Notificar al jugador eliminado
- [ ] Notificar a jugadores restantes
- [ ] Jugadores eliminados no participan en siguientes rondas

**Puntos:** 3  
**Asignado a:** Gabriel (Backend)

---

#### US-3.6: Interfaz de Mesa de Juego
**Como** jugador  
**Quiero** ver las cartas jugadas en la mesa  
**Para** saber qué se ha jugado

**Criterios de Aceptación:**
- [ ] Área central mostrando cartas jugadas en baza actual
- [ ] Cada carta muestra qué jugador la jugó
- [ ] Animación al jugar carta (desde mano a mesa)
- [ ] Resaltar ganador de la baza (borde dorado)
- [ ] Limpiar mesa entre bazas (animación de recolección)
- [ ] Responsive en móvil y desktop

**Puntos:** 13  
**Asignado a:** Joan (Frontend)

---

#### US-3.7: Visualización de Mano
**Como** jugador  
**Quiero** ver mis cartas ordenadas  
**Para** elegir qué jugar fácilmente

**Criterios de Aceptación:**
- [ ] Mostrar cartas en mano del jugador (solo las suyas)
- [ ] Ordenar por palo y valor
- [ ] Resaltar cartas jugables (las que puede jugar legalmente)
- [ ] Deshabilitar cartas no jugables (opacidad reducida)
- [ ] Click en carta para jugar (confirmación opcional)
- [ ] Animación hover en cartas jugables
- [ ] Fan de cartas en desktop, stack en móvil

**Puntos:** 8  
**Asignado a:** Joan (Frontend)

---

**Total Puntos Sprint 3:** 50 puntos

---

## 🎯 Sprint 4: Multijugador y Progresión
**Duración:** Semanas 7-8  
**Objetivo:** Sincronización en tiempo real y progresión completa de rondas

#### US-4.1: Progresión de Rondas
**Como** sistema  
**Quiero** gestionar la transición entre rondas  
**Para** completar las 5 rondas del juego

**Criterios de Aceptación:**
- [ ] Detectar fin de ronda (todas las bazas jugadas)
- [ ] Calcular vidas perdidas para cada jugador
- [ ] Actualizar vidas totales
- [ ] Cambiar dealer en sentido horario
- [ ] Ajustar número de cartas (5→4→3→2→1)
- [ ] Reiniciar apuestas y bazas ganadas
- [ ] Mantener vidas totales
- [ ] Detectar fin de juego (ronda 5 o solo 1 jugador vivo)

**Puntos:** 8  
**Asignado a:** Gabriel (Backend)

---

#### US-4.2: Sincronización WebSocket
**Como** jugador  
**Quiero** ver las acciones de otros en tiempo real  
**Para** una experiencia fluida

**Criterios de Aceptación:**
- [ ] Enviar `GameStateUpdate` vía WebSocket cuando cambia estado
- [ ] Actualizar UI de todos los clientes automáticamente
- [ ] Eventos: apuesta realizada, carta jugada, baza ganada, ronda terminada
- [ ] Sin necesidad de refrescar página
- [ ] Latencia < 500ms
- [ ] Manejo de reconexión

**Puntos:** 13  
**Asignado a:** Gabriel (Backend) + Joan (Frontend)

---

#### US-4.3: Notificaciones de Turno
**Como** jugador  
**Quiero** saber cuándo es mi turno  
**Para** no retrasar el juego

**Criterios de Aceptación:**
- [ ] Indicador visual "TU TURNO" cuando corresponde
- [ ] Highlight del jugador activo
- [ ] Notificación browser (opcional, con permiso)
- [ ] Sonido sutil (opcional, configurable)
- [ ] Timer countdown opcional (30 segundos)

**Puntos:** 5  
**Asignado a:** Joan (Frontend)

---

#### US-4.4: Tabla de Vidas y Puntuaciones
**Como** jugador  
**Quiero** ver las vidas de todos  
**Para** saber el estado del juego

**Criterios de Aceptación:**
- [ ] Panel lateral/superior con lista de jugadores
- [ ] Mostrar: nombre, vidas actuales, apuesta, bazas ganadas
- [ ] Indicar jugadores eliminados (crossed out o gris)
- [ ] Resaltar líder (más vidas)
- [ ] Actualización en tiempo real
- [ ] Responsive: sidebar en desktop, collapse en móvil

**Puntos:** 8  
**Asignado a:** Joan (Frontend)

---

#### US-4.5: Pantalla de Victoria
**Como** jugador  
**Quiero** ver quién ganó al final  
**Para** celebrar o analizar resultados

**Criterios de Aceptación:**
- [ ] Detectar fin de juego (1 jugador vivo o 5 rondas)
- [ ] Modal/pantalla de victoria
- [ ] Mostrar ganador con animación
- [ ] Ranking final de todos los jugadores
- [ ] Estadísticas: rondas jugadas, apuestas acertadas
- [ ] Botón "Jugar de Nuevo"
- [ ] Compartir resultado (opcional)

**Puntos:** 8  
**Asignado a:** Joan (Frontend)

---

#### US-4.6: Sala de Espera
**Como** jugador  
**Quiero** esperar a que se unan suficientes jugadores  
**Para** iniciar la partida

**Criterios de Aceptación:**
- [ ] Pantalla de sala de espera antes de iniciar
- [ ] Mostrar código de partida para compartir
- [ ] Lista de jugadores conectados en tiempo real
- [ ] Botón "INICIAR" habilitado solo con 3+ jugadores
- [ ] Solo el anfitrión puede iniciar
- [ ] Indicador de "Esperando jugadores..." si < 3

**Puntos:** 8  
**Asignado a:** Joan (Frontend) + Gabriel (Backend)

---

**Total Puntos Sprint 4:** 50 puntos

---

## 🎯 Sprint 5: Pulido, Testing y Despliegue
**Duración:** Semanas 9-10  
**Objetivo:** Pulir la aplicación, testing exhaustivo y preparar para producción

### Historias de Usuario

#### US-5.1: Testing E2E Completo
**Como** QA  
**Quiero** realizar tests de extremo a extremo  
**Para** asegurar que todo funciona correctamente

**Criterios de Aceptación:**
- [ ] Flujo completo: crear partida → unirse → apostar → jugar → fin
- [ ] Test con 3, 4, 5, 6, 7 jugadores
- [ ] Validar todas las reglas (seguir palo, apuesta última, vidas)
- [ ] Casos edge: empates, eliminaciones, reconexiones
- [ ] Matriz de compatibilidad browsers (Chrome, Firefox, Safari, Edge)
- [ ] Testing en móvil (iOS, Android)
- [ ] Documentar bugs encontrados
- [ ] Informe de testing final

**Puntos:** 13  
**Asignado a:** Tomás (QA)

---

#### US-5.2: Manejo de Errores
**Como** desarrollador  
**Quiero** manejar todos los errores gracefully  
**Para** evitar crashes y mejorar UX

**Criterios de Aceptación:**
- [ ] Try-catch en operaciones críticas
- [ ] GlobalExceptionHandler en Spring
- [ ] Mensajes de error user-friendly (no stack traces)
- [ ] Logging estructurado con niveles (INFO, WARN, ERROR)
- [ ] Página 404 personalizada
- [ ] Manejo de desconexiones WebSocket
- [ ] Validaciones consistentes backend/frontend

**Puntos:** 8  
**Asignado a:** Gabriel (Backend) + Joan (Frontend)

---

#### US-5.3: Optimización de Performance
**Como** usuario  
**Quiero** que el juego sea rápido  
**Para** una mejor experiencia

**Criterios de Aceptación:**
- [ ] Tiempo de carga inicial < 3s
- [ ] Respuesta de API < 200ms (P95)
- [ ] Optimización de assets (minify CSS/JS)
- [ ] Lazy loading de imágenes de cartas
- [ ] WebSocket con compression
- [ ] Tests de carga: 10 partidas simultáneas sin degradación
- [ ] Lighthouse score > 85

**Puntos:** 8  
**Asignado a:** Todos

---

#### US-5.4: Responsive Design Final
**Como** usuario móvil  
**Quiero** jugar desde mi teléfono  
**Para** jugar en cualquier lugar

**Criterios de Aceptación:**
- [ ] Breakpoints: 320px, 768px, 1024px, 1440px
- [ ] Cartas legibles en pantallas pequeñas
- [ ] Botones con touch targets > 44px
- [ ] Layout adaptativo (vertical en móvil, horizontal en desktop)
- [ ] Testing en iPhone SE, iPhone 12, iPad, Android
- [ ] Orientación portrait y landscape

**Puntos:** 8  
**Asignado a:** Joan (Frontend)

---

#### US-5.5: Documentación Final
**Como** desarrollador futuro  
**Quiero** documentación completa  
**Para** entender y mantener el proyecto

**Criterios de Aceptación:**
- [ ] README.md actualizado con instrucciones de instalación
- [ ] JavaDoc en todas las clases públicas
- [ ] Comentarios en lógica compleja
- [ ] API documentada (Swagger opcional)
- [ ] Guía de despliegue
- [ ] CREDITS.md con recursos utilizados
- [ ] LICENSE file

**Puntos:** 5  
**Asignado a:** Tomás (QA) + Todos

---

#### US-5.6: CI/CD y Despliegue
**Como** DevOps  
**Quiero** pipeline de CI/CD  
**Para** automatizar testing y despliegue

**Criterios de Aceptación:**
- [ ] GitHub Actions workflow configurado
- [ ] Tests automáticos en cada push
- [ ] Build automático del JAR
- [ ] Despliegue a Heroku/Railway/Cloud
- [ ] Health check endpoint
- [ ] Rollback plan documentado

**Puntos:** 8  
**Asignado a:** Tomás (DevOps)

---

#### US-5.7: Características Extra (Opcional)
**Como** jugador  
**Quiero** características adicionales  
**Para** mejor experiencia

**Criterios de Aceptación:**
- [ ] Variante "Carta en la Frente" (ronda de 1 carta)
- [ ] Sonidos opcionales (jugar carta, ganar baza)
- [ ] Animaciones suaves de transiciones
- [ ] Modo oscuro/claro
- [ ] Tutorial interactivo básico
- [ ] Historial de últimas jugadas

**Puntos:** 13  
**Asignado a:** Joan (Frontend) + Gabriel (Backend)

---

**Total Puntos Sprint 5:** 63 puntos (con opcionales 50 sin opcionales)

---

## 📈 Resumen de Velocidad del Equipo

### Capacidad por Sprint
- **Sprint 1:** 26 puntos (setup inicial, más lento)
- **Sprint 2:** 32 puntos (equipo acelerando)
- **Sprint 3:** 50 puntos (velocidad máxima)
- **Sprint 4:** 50 puntos (velocidad sostenida)
- **Sprint 5:** 50 puntos (pulido y extras)

**Total del proyecto:** ~208 puntos  
**Velocity promedio:** 40-50 puntos/sprint

---

## 🎯 Definition of Done (DoD)

Una historia de usuario se considera **"Done"** cuando:

### Para Código
- [ ] Código implementado y funcional
- [ ] Tests unitarios escritos y pasando (coverage > 70%)
- [ ] Code review completado por al menos 1 compañero
- [ ] Sin warnings críticos de compilación
- [ ] Código mergeado a rama `develop`
- [ ] Documentación JavaDoc/comentarios actualizada

### Para Frontend
- [ ] UI implementada según diseño acordado
- [ ] Responsive en móvil (320px+) y desktop (1024px+)
- [ ] Sin errores en consola del navegador
- [ ] Accesibilidad básica: contraste WCAG AA, HTML semántico
- [ ] Testeado en Chrome y Firefox mínimo
- [ ] Integración con API funcional

### Para Backend
- [ ] API endpoints documentados
- [ ] Validaciones implementadas (server-side)
- [ ] Manejo de errores apropiado con excepciones custom
- [ ] Logs informativos (INFO para operaciones, ERROR para fallos)
- [ ] Tests de integración para endpoints críticos (opcional según sprint)
- [ ] Respuesta API < 200ms

### Para la Historia Completa
- [ ] Todos los criterios de aceptación cumplidos
- [ ] Demo funcional preparada
- [ ] Aceptada por el Product Owner (Rongheng)
- [ ] Sin bugs críticos conocidos (P0/P1)
- [ ] Documentación actualizada si aplica

---

## 📊 Tablero Kanban

### Herramienta: GitHub Projects (integrado con GitHub Classroom)

### Columnas del Tablero
```
┌──────────────┬──────────────┬──────────────┬──────────────┬──────────────┐
│   Backlog    │    To Do     │ In Progress  │   Review     │     Done     │
├──────────────┼──────────────┼──────────────┼──────────────┼──────────────┤
│ Historias    │ Sprint       │ Trabajando   │ En code      │ Completado   │
│ futuras      │ actual       │ ahora        │ review       │ y aceptado   │
│ priorizadas  │ comprometido │ (max 2/pers) │ PR abierto   │ mergeado     │
└──────────────┴──────────────┴──────────────┴──────────────┴──────────────┘
```

### Etiquetas (Labels)
- `backend` - Trabajo de Gabriel
- `frontend` - Trabajo de Joan
- `testing` - Trabajo de Tomás
- `bug` - Defecto encontrado
- `enhancement` - Mejora
- `documentation` - Documentación
- `priority-high` - Prioridad alta
- `priority-medium` - Prioridad media
- `priority-low` - Prioridad baja
- `blocked` - Bloqueado por impedimento

### Reglas del Tablero
- **Límite WIP:** Máximo 2 tareas "In Progress" por persona
- **Asignación:** Cada issue debe tener un assignee
- **Actualización:** Mover cards al menos 1 vez al día en Daily
- **Pull Requests:** Vincular PR con issue correspondiente (#número)

---

## 🔄 Gestión de Riesgos

### Riesgos Identificados

| ID | Riesgo | Probabilidad | Impacto | Mitigación | Responsable |
|----|--------|--------------|---------|------------|-------------|
| R1 | Complejidad de WebSocket | Media | Alto | Empezar en Sprint 2, tutoriales, pair programming | Gabriel + Joan |
| R2 | Validación de reglas compleja | Alta | Medio | Tests exhaustivos, revisión en equipo | Gabriel |
| R3 | Sincronización multijugador | Media | Alto | Testing con múltiples navegadores, logs | Gabriel + Joan |
| R4 | Miembro ausente | Baja | Medio | Documentación clara, pair programming | Wang (SM) |
| R5 | Scope creep | Media | Alto | Product Owner firme en prioridades | Rongheng (PO) |
| R6 | Problemas de despliegue | Baja | Alto | Empezar config CI/CD en Sprint 4 | Tomás |
| R7 | Bugs críticos cerca del deadline | Media | Alto | Testing continuo desde Sprint 1 | Tomás |

---

## 📝 Git Workflow

### Estructura de Ramas
```
main (producción, protegida)
  └── develop (integración continua)
       ├── feature/US-1.1-setup-proyecto
       ├── feature/US-1.2-crear-baraja
       ├── feature/US-2.3-validacion-apuesta
       ├── bugfix/fix-validacion-seguir-palo
       └── hotfix/critical-websocket-error
```

### Convención de Nombres de Rama
```
feature/US-X.X-descripcion-corta
bugfix/descripcion-del-bug
hotfix/critical-issue
```

### Convención de Commits
```
tipo(alcance): descripción corta en español

Descripción más detallada si es necesario.
Explicar el "por qué" y el "cómo".

Closes #123
```

**Tipos de commit:**
- `feat`: Nueva funcionalidad
- `fix`: Corrección de bug
- `docs`: Documentación
- `style`: Formato (sin cambio de lógica)
- `refactor`: Refactorización de código
- `test`: Agregar/modificar tests
- `chore`: Tareas de mantenimiento

**Ejemplos:**
```
feat(deck): implementar barajado Fisher-Yates

Agrega método shuffle() que mezcla cartas aleatoriamente
usando el algoritmo Fisher-Yates para garantizar 
distribución uniforme.

Closes #15

---

fix(betting): validar última apuesta correctamente

Corrige bug donde el último jugador podía apostar
el valor prohibido. Ahora se valida en backend
antes de aceptar la apuesta.

Fixes #34
```

### Pull Request Template

```markdown
## Descripción
[Breve descripción de qué hace este PR]

## Historia de Usuario
Closes #[número de issue]

## Tipo de cambio
- [ ] Nueva funcionalidad (feature)
- [ ] Corrección de bug (bugfix)
- [ ] Refactorización
- [ ] Documentación

## Cambios realizados
- Cambio 1
- Cambio 2
- Cambio 3

## Tests
- [ ] Tests unitarios agregados/actualizados
- [ ] Tests pasan localmente
- [ ] Testeado manualmente

## Checklist
- [ ] Mi código sigue las convenciones del proyecto
- [ ] He realizado self-review del código
- [ ] He comentado código complejo si es necesario
- [ ] He actualizado la documentación si aplica
- [ ] Mis cambios no generan nuevos warnings
- [ ] He agregado tests que prueban mi cambio
- [ ] Todos los tests nuevos y existentes pasan

## Screenshots (si aplica)
[Capturas de pantalla o GIFs]

## Notas adicionales
[Información adicional para el reviewer]
```

### Proceso de Pull Request
1. **Crear PR** desde feature → develop
2. **Self-review:** Revisar tus propios cambios
3. **Solicitar review:** Asignar a al menos 1 compañero
4. **Esperar aprobación:** Mínimo 1 aprobación requerida
5. **Resolver comentarios:** Responder feedback
6. **Merge:** Solo después de aprobación y tests pasando
7. **Borrar rama:** Después de merge exitoso

**Tiempo de respuesta esperado:** 
- Revisión de PR: < 24 horas
- Correcciones post-review: < 12 horas

---

## 🤝 Pair Programming

### Cuándo hacer Pair Programming
- Tareas complejas (validación de reglas, WebSocket)
- Onboarding de nuevas tecnologías
- Resolución de bugs críticos
- Code review en vivo para aprender

### Técnica Recomendada: Pomodoro
1. **Driver** (escribe código) + **Navigator** (revisa y guía)
2. Trabajar 25 minutos
3. Cambiar roles
4. Break de 5 minutos
5. Repetir

### Parejas Sugeridas por Sprint
- **Sprint 1-2:** Gabriel + Joan (WebSocket)
- **Sprint 3:** Gabriel + Tomás (Tests de validación)
- **Sprint 4:** Joan + Tomás (Tests E2E)
- **Sprint 5:** Rotativo según necesidad

---

## 📊 Métricas y Seguimiento

### Métricas Clave

#### Velocity (Velocidad del Equipo)
- **Objetivo:** 40-50 puntos por sprint
- **Tracking:** Puntos completados vs planificados
- **Revisión:** En cada Sprint Review

#### Burndown Chart
- **Actualización:** Diaria después del Daily Stand-up
- **Objetivo:** Línea ideal vs línea real
- **Alerta:** Si estamos > 20% por encima de la línea ideal a mitad de sprint

#### Code Coverage
- **Objetivo mínimo:** 70%
- **Tracking:** Jacoco reports en cada build
- **Prioridad:** Service layer > 80%, Controllers > 60%

#### Bug Rate
- **Tracking:** Bugs encontrados por sprint
- **Categorías:** P0 (crítico), P1 (alto), P2 (medio), P3 (bajo)
- **Objetivo:** 0 bugs P0/P1 al final de cada sprint

#### Sprint Goal Achievement
- **Métrica:** % de historias completadas (Done)
- **Objetivo:** > 85% de historias Done
- **Revisión:** En Sprint Review

### Herramientas de Seguimiento
- **Tablero Kanban:** GitHub Projects
- **Burndown:** GitHub Projects + Spreadsheet manual
- **Code Coverage:** JaCoCo (configurado en pom.xml)
- **Retrospective Board:** Miro o FunRetro

---

## 🎓 Recursos y Documentación

### Documentación Técnica
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [WebSocket with Spring](https://spring.io/guides/gs/messaging-stomp-websocket/)
- [MDN Web Docs - JavaScript](https://developer.mozilla.org/)
- [Flexbox Guide](https://css-tricks.com/snippets/css/a-guide-to-flexbox/)

### Tutoriales Útiles
- Baeldung: Spring Boot REST API
- YouTube: Spring WebSocket Tutorial
- Scrum Guide (español): https://scrumguides.org/docs/scrumguide/v2020/2020-Scrum-Guide-Spanish-Latin-South-American.pdf

### Recursos del Proyecto
- **GitHub Classroom:** [Link al repositorio]
- **Slack/Discord:** [Canal del equipo]
- **Drive compartido:** [Carpeta de documentos]
- **Miro Board:** [Retrospectivas y brainstorming]

### Convenciones de Código
- **Java:** Google Java Style Guide
- **JavaScript:** Airbnb JavaScript Style Guide
- **HTML/CSS:** BEM methodology para clases CSS

---

## 🔧 Impediments Log

### Cómo Reportar Impedimentos
1. **Mencionar en Daily Stand-up** - Primera línea de defensa
2. **Agregar label "blocked"** al issue en GitHub
3. **Notificar al Scrum Master (Wang)** vía Slack/Discord
4. **Documentar en Impediments Log** (Google Sheet compartido)

### Template de Impedimento
```markdown
**ID:** IMP-001
**Fecha:** 2025-11-15
**Reportado por:** Gabriel
**Sprint:** Sprint 2
**Descripción:** No puedo testear WebSocket localmente, error de CORS
**Impacto:** Alto - bloquea US-2.6
**Historia afectada:** US-2.6
**Acciones tomadas:** 
  - Revisé configuración de CorsConfig
  - Busqué en Stack Overflow
**Estado:** Open
**Asignado a resolver:** Wang (SM) + Gabriel
**Fecha resolución:** [Pendiente]
**Solución aplicada:** [A completar]
```

### Responsabilidades del Scrum Master
- Dar seguimiento a impedimentos en < 24h
- Escalar a Product Owner si es necesario
- Facilitar soluciones con el equipo
- Documentar soluciones para futuros casos

---

## 🎉 Sprint Retrospective - Formato

### Formato "Start/Stop/Continue"

#### START (Empezar a hacer)
- ¿Qué no estamos haciendo que deberíamos empezar?
- Ideas nuevas que podrían mejorar el equipo
- **Ejemplo:** "Empezar a hacer code review en parejas para aprender más rápido"

#### STOP (Dejar de hacer)
- ¿Qué estamos haciendo que no agrega valor?
- Prácticas que nos frenan
- **Ejemplo:** "Dejar de hacer commits directos a develop sin PR"

#### CONTINUE (Seguir haciendo)
- ¿Qué está funcionando bien y debemos mantener?
- Celebrar éxitos
- **Ejemplo:** "Continuar con los Daily Stand-ups puntuales, son muy útiles"

### Proceso de Retrospectiva
1. **Check-in (5 min):** Cada uno comparte cómo se siente (emoji)
2. **Recopilar datos (15 min):** Post-its con Start/Stop/Continue
3. **Agrupar insights (10 min):** Temas comunes
4. **Votar prioridades (10 min):** Cada uno 3 votos
5. **Definir acciones (15 min):** Máximo 3 acciones concretas
6. **Asignar responsables (5 min):** Quién lidera cada acción
7. **Revisión anterior (10 min):** ¿Qué pasó con acciones del sprint pasado?

### Acciones de Mejora - Template
```markdown
**Acción #1:** Implementar peer code review obligatorio
**Responsable:** Wang (SM)
**Fecha límite:** Antes de Sprint 3
**Éxito se mide en:** Todos los PRs tienen al menos 1 approval antes de merge
**Estado:** [In Progress / Done]
```

**Reglas:**
- Máximo 3 acciones por retrospectiva
- Cada acción tiene responsable claro
- Revisar acciones anteriores al inicio de cada retro
- Celebrar acciones completadas

---

## 📞 Comunicación del Equipo

### Canales de Comunicación

| Canal | Uso | Tiempo de respuesta |
|-------|-----|---------------------|
| **Slack/Discord** | Chat diario, preguntas rápidas | < 4 horas |
| **Email** | Temas formales, documentos importantes | < 24 horas |
| **GitHub Issues** | Reportar bugs, nuevas features | Revisión en Daily |
| **GitHub PR Comments** | Code review, feedback técnico | < 24 horas |
| **Videollamada** | Ceremonias Scrum, pair programming | Según agenda |

### Horarios de Trabajo
- **Daily Stand-up:** 9:00 AM (obligatorio para todos)
- **Core Hours:** 10:00 AM - 4:00 PM (disponibilidad esperada)
- **Flexible:** Fuera de core hours según preferencia individual
- **Pair Programming:** Coordinar con anticipación

### Estados de Disponibilidad
- 🟢 **Disponible:** Puedo responder inmediatamente
- 🟡 **Ocupado:** En focus time, responderé en < 2 horas
- 🔴 **No disponible:** Fuera de horario, responderé al día siguiente
- 🔵 **En reunión:** Respondré al terminar

### Buenas Prácticas
- ✅ Actualizar estado en Slack/Discord
- ✅ Usar threads para conversaciones largas
- ✅ @mencionar para urgencias
- ✅ Documentar decisiones importantes en GitHub wiki
- ❌ No spamear con mensajes repetidos
- ❌ No usar DMs para temas de equipo (usar canal público)

---

## 🏁 Criterios de Éxito del Proyecto

Al finalizar el Sprint 5, el proyecto será exitoso si cumple:

### ✅ Funcionalidad Completa
- [ ] Juego completo jugable con 3-7 jugadores
- [ ] Todas las reglas implementadas correctamente:
  - [ ] Sistema de vidas (5 iniciales, pérdida por diferencia)
  - [ ] Apuestas con regla de último jugador
  - [ ] Seguir palo obligatorio
  - [ ] Determinación correcta de ganador de baza
  - [ ] Progresión de rondas (5→4→3→2→1)
- [ ] Multijugador en tiempo real con WebSocket funcionando
- [ ] Detección de eliminación y victoria
- [ ] 5 rondas completables sin errores

### ✅ Calidad del Código
- [ ] Cobertura de tests > 70%
- [ ] Sin bugs críticos (P0/P1) conocidos
- [ ] Code reviews completados en todos los PRs
- [ ] Código sigue convenciones establecidas
- [ ] Documentación JavaDoc en clases públicas

### ✅ Performance y UX
- [ ] Tiempo de carga < 3 segundos
- [ ] Respuesta de API < 200ms (P95)
- [ ] Latencia WebSocket < 500ms
- [ ] Funciona en Chrome, Firefox, Safari, Edge
- [ ] Responsive en móvil (320px+) y desktop (1024px+)
- [ ] Interfaz intuitiva (usuario nuevo puede jugar sin manual)

### ✅ Entrega y Despliegue
- [ ] Código en repositorio GitHub Classroom
- [ ] README.md completo con instrucciones de instalación
- [ ] Aplicación desplegada en cloud (Heroku/Railway)
- [ ] CI/CD pipeline configurado y funcionando
- [ ] Demo funcional presentada en Sprint Review final

### ✅ Trabajo en Equipo
- [ ] Todos los miembros participaron activamente
- [ ] 5 Sprint Retrospectives completadas con acciones
- [ ] Velocity estable en Sprints 2-5
- [ ] Comunicación efectiva mantenida
- [ ] Definition of Done respetado en todas las historias

### 📊 Métricas de Éxito
- **Velocity:** Promedio de 40-50 puntos por sprint
- **Sprint Goal Achievement:** > 85% de historias Done por sprint
- **Bug Rate:** < 5 bugs P2/P3 acumulados al final
- **Team Happiness:** ≥ 4/5 en retrospectiva final
- **Code Coverage:** > 70% en Service layer

---

## 📚 Anexos

### A. Glosario de Términos

**Términos del Juego:**
- **Baza/Trick:** Conjunto de cartas jugadas en una ronda por todos los jugadores. El ganador "gana la baza".
- **Triunfo/Trump:** Palo especial que vence a cualquier otro palo en esa ronda.
- **Apuesta/Bet:** Predicción de cuántas bazas ganará un jugador en la ronda.
- **Vida/Life:** Cada jugador tiene 5 vidas. Pierde vidas si no acierta su apuesta.
- **Seguir palo:** Regla que obliga a jugar una carta del mismo palo que la carta inicial si la tienes.

**Términos Scrum:**
- **Sprint:** Período de tiempo fijo (2 semanas) para completar trabajo.
- **Story Points:** Medida relativa de complejidad/esfuerzo de una historia.
- **Velocity:** Cantidad de story points completados por sprint.
- **WIP (Work In Progress):** Trabajo en curso, limitado a 2 tareas por persona.
- **Burndown Chart:** Gráfico que muestra trabajo restante vs tiempo.
- **Definition of Done (DoD):** Criterios que debe cumplir una historia para considerarse completada.
- **Sprint Goal:** Objetivo principal del sprint, resumen de lo que se quiere lograr.
- **Product Backlog:** Lista priorizada de todas las funcionalidades deseadas.
- **Sprint Backlog:** Subset del Product Backlog seleccionado para el sprint actual.

### B. Enlaces Útiles

#### Proyecto
- **GitHub Classroom:** [Link pendiente]
- **Aplicación desplegada:** [Link pendiente]
- **Documentación API:** [Link pendiente]

#### Comunicación
- **Slack/Discord:** [Link al workspace]
- **Google Drive:** [Carpeta compartida]
- **Miro Board:** [Tablero de retrospectivas]

#### Recursos Técnicos
- **Spring Initializr:** https://start.spring.io/
- **Card Assets:** https://github.com/htdebeer/SVG-cards
- **Scrum Guide:** https://scrumguides.org/
- **Git Flow Guide:** https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow

### C. Plantillas

#### Template de Historia de Usuario
```markdown
### US-X.X: [Título Descriptivo]

**Como** [rol]  
**Quiero** [funcionalidad]  
**Para** [beneficio/objetivo]

**Prioridad:** Alta/Media/Baja  
**Puntos:** [número]  
**Sprint:** [número]  
**Asignado a:** [Nombre]

#### Criterios de Aceptación
- [ ] Criterio 1
- [ ] Criterio 2
- [ ] Criterio 3

#### Notas Técnicas
- Consideración técnica relevante
- Dependencias o limitaciones

#### Dependencias
- Depende de: US-X.X
- Bloquea: US-X.X

#### Testing
- Casos de prueba específicos
```

#### Template de Bug Report
```markdown
**Título:** [Descripción corta del bug]

**Prioridad:** P0 (Crítico) / P1 (Alto) / P2 (Medio) / P3 (Bajo)

**Descripción:**
[Explicación detallada del problema]

**Pasos para Reproducir:**
1. Paso 1
2. Paso 2
3. Paso 3

**Comportamiento Esperado:**
[Qué debería pasar]

**Comportamiento Actual:**
[Qué está pasando]

**Entorno:**
- Navegador: [Chrome 120]
- OS: [Windows 11]
- Resolución: [1920x1080]

**Screenshots:**
[Adjuntar capturas si aplica]

**Logs:**
```
[Copiar logs relevantes]
```

**Asignado a:** [Nombre]
**Sprint:** [Sprint X]
```

---

## 🚀 ¡Manos a la Obra!

### Primeros Pasos (Sprint 1 - Día 1)

1. **Configurar entorno de desarrollo**
   - Instalar JDK 17+
   - Instalar Maven
   - Instalar IDE (IntelliJ IDEA recomendado)
   - Clonar repositorio de GitHub Classroom

2. **Primera reunión de equipo**
   - Conocerse y presentarse
   - Revisar este plan Scrum juntos
   - Acordar horarios de Daily Stand-up
   - Configurar canales de comunicación

3. **Sprint Planning 1**
   - Revisar Product Backlog
   - Seleccionar historias para Sprint 1
   - Descomponer en tareas técnicas
   - Comprometerse con Sprint Goal

4. **¡Empezar a codear!**
   - Crear primera rama feature
   - Implementar US-1.1 en equipo
   - Hacer primer commit
   - Abrir primer Pull Request

---

## 💪 Mantras del Equipo

> **"Done is better than perfect"**  
> Entregar valor funcionando es mejor que perfección no terminada.

> **"Fail fast, learn faster"**  
> Los errores tempranos son oportunidades de aprendizaje.

> **"Ask when in doubt"**  
> No hay preguntas tontas. Preguntar ahorra tiempo.

> **"We over I"**  
> El éxito es del equipo, no individual.

> **"Celebrate small wins"**  
> Cada historia completada es un logro.

---

## 📞 Contactos de Emergencia

En caso de problemas críticos que bloqueen el proyecto:

- **Product Owner (Rongheng):** ronghengx@gmail.com
- **Scrum Master (Wang):** xiaozhu9728@126.com
- **Profesor/Instructor:** [Email del profesor]

---

**Última actualización:** 2025-11-04  
**Versión:** 1.0  
**Mantenido por:** Wang Wenjie (Scrum Master)

---

## 🎊 ¡Éxito en el Proyecto!

Recuerda que este es un proyecto de aprendizaje. Lo más importante no es solo el código final, sino:

- 🤝 Trabajar en equipo efectivamente
- 📚 Aprender nuevas tecnologías (Spring Boot, WebSocket)
- 🔄 Practicar metodología Scrum
- 🐛 Resolver problemas técnicos colaborativamente
- 🎯 Entregar valor incremental cada sprint

**¡Mucha suerte equipo! 🚀🃏**