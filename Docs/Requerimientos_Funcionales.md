# Requerimientos Funcionales - Oh Hell! Game

## 1. Gestión de Jugadores

### RF-01: Registro de Jugadores
- **Descripción:** El sistema debe permitir registrar entre 3 y 7 jugadores para una partida.
- **Prioridad:** Alta
- **Criterios de Aceptación:**
  - Mínimo 3 jugadores, máximo 7 jugadores
  - Cada jugador debe tener un nombre único en la partida
  - El sistema debe validar que no haya nombres duplicados

### RF-02: Asignación del Primer Repartidor
- **Descripción:** El sistema debe asignar aleatoriamente quién será el primer repartidor.
- **Prioridad:** Media
- **Criterios de Aceptación:**
  - La selección debe ser aleatoria
  - El repartidor debe cambiar en sentido horario cada ronda

## 2. Gestión de la Baraja

### RF-03: Inicialización de la Baraja
- **Descripción:** El sistema debe crear una baraja francesa estándar de 52 cartas.
- **Prioridad:** Alta
- **Criterios de Aceptación:**
  - 4 palos: Corazones, Diamantes, Tréboles, Picas
  - 13 cartas por palo: As, 2-10, J, Q, K
  - Total: 52 cartas

### RF-04: Barajado de Cartas
- **Descripción:** El sistema debe barajar las cartas de forma aleatoria antes de cada ronda.
- **Prioridad:** Alta
- **Criterios de Aceptación:**
  - Algoritmo de barajado aleatorio (ej: Fisher-Yates)
  - Cada carta debe tener igual probabilidad de aparecer en cualquier posición

### RF-05: Reparto de Cartas
- **Descripción:** El sistema debe repartir el número correcto de cartas según la ronda actual.
- **Prioridad:** Alta
- **Criterios de Aceptación:**
  - Rondas 1-10: 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 cartas
  - Rondas 11-19: 2, 3, 4, 5, 6, 7, 8, 9, 10 cartas
  - Las cartas se reparten una a una en sentido horario

## 3. Gestión de Triunfo

### RF-06: Determinación del Palo de Triunfo
- **Descripción:** El sistema debe voltear la carta superior del mazo restante para determinar el triunfo.
- **Prioridad:** Alta
- **Criterios de Aceptación:**
  - Después de repartir, se voltea la siguiente carta
  - El palo de esa carta es el triunfo de la ronda
  - Todos los jugadores deben ver qué palo es triunfo

## 4. Sistema de Apuestas

### RF-07: Registro de Apuestas
- **Descripción:** El sistema debe permitir a cada jugador apostar cuántas bazas cree que ganará.
- **Prioridad:** Alta
- **Criterios de Aceptación:**
  - El jugador a la izquierda del repartidor apuesta primero
  - Continúa en sentido horario
  - Las apuestas válidas son de 0 al número de cartas en mano

### RF-08: Validación de Apuesta del Último Jugador
- **Descripción:** El sistema debe validar que el último jugador no haga una apuesta que equilibre el total.
- **Prioridad:** Alta
- **Criterios de Aceptación:**
  - Calcular suma de apuestas previas
  - Prohibir al último jugador apostar el valor que complete el total de bazas
  - Mostrar mensaje de error si intenta apostar ese valor
  - Sugerir valores válidos

### RF-09: Visualización de Apuestas
- **Descripción:** El sistema debe mostrar las apuestas de todos los jugadores durante la ronda.
- **Prioridad:** Media
- **Criterios de Aceptación:**
  - Mostrar nombre del jugador y su apuesta
  - Indicar quién aún no ha apostado
  - Resaltar apuestas no permitidas para el último jugador

## 5. Mecánica de Juego

### RF-10: Inicio de Baza
- **Descripción:** El jugador a la izquierda del repartidor debe iniciar la primera baza.
- **Prioridad:** Alta
- **Criterios de Aceptación:**
  - El sistema indica quién debe jugar primero
  - El jugador puede elegir cualquier carta de su mano
  - El palo de esa carta establece el palo a seguir

### RF-11: Validación de Cartas Jugadas
- **Descripción:** El sistema debe validar que las cartas jugadas cumplan las reglas.
- **Prioridad:** Alta
- **Criterios de Aceptación:**
  - Si el jugador tiene cartas del palo inicial, DEBE jugar ese palo
  - Si no tiene el palo, puede jugar cualquier carta
  - Mostrar error si intenta jugar carta inválida

### RF-12: Determinación del Ganador de Baza
- **Descripción:** El sistema debe determinar automáticamente quién gana cada baza.
- **Prioridad:** Alta
- **Criterios de Aceptación:**
  - Si se jugó triunfo: gana el triunfo más alto
  - Si no hay triunfo: gana la carta más alta del palo inicial
  - Jerarquía: As > K > Q > J > 10 > 9 > 8 > 7 > 6 > 5 > 4 > 3 > 2

### RF-13: Contador de Bazas Ganadas
- **Descripción:** El sistema debe llevar el conteo de bazas ganadas por cada jugador en la ronda.
- **Prioridad:** Alta
- **Criterios de Aceptación:**
  - Actualizar contador después de cada baza
  - Mostrar bazas ganadas vs bazas apostadas
  - Indicar visualmente si el jugador está en camino de cumplir su apuesta

## 6. Sistema de Puntuación

### RF-14: Cálculo de Puntuación por Ronda
- **Descripción:** El sistema debe calcular automáticamente los puntos de cada jugador al final de la ronda.
- **Prioridad:** Alta
- **Criterios de Aceptación:**
  - Si acierta: Puntos = Bazas ganadas + 10 bonus
  - Si NO acierta: Puntos = Bazas ganadas (sin bonus)
  - Actualizar puntuación total del jugador

### RF-15: Tabla de Puntuaciones
- **Descripción:** El sistema debe mostrar una tabla con las puntuaciones actuales de todos los jugadores.
- **Prioridad:** Media
- **Criterios de Aceptación:**
  - Mostrar puntuación total de cada jugador
  - Indicar la posición/ranking actual
  - Mostrar historial de puntos por ronda (opcional)

### RF-16: Determinación del Ganador
- **Descripción:** El sistema debe determinar el ganador después de las 19 rondas.
- **Prioridad:** Alta
- **Criterios de Aceptación:**
  - Identificar al jugador(es) con mayor puntuación
  - Manejar empates (múltiples ganadores)
  - Mostrar pantalla de victoria con el ranking final

## 7. Interfaz de Usuario

### RF-17: Visualización de Cartas en Mano
- **Descripción:** El sistema debe mostrar las cartas del jugador activo de forma clara.
- **Prioridad:** Alta
- **Criterios de Aceptación:**
  - Mostrar solo las cartas del jugador activo
  - Cartas ordenadas por palo y valor
  - Indicar qué cartas son jugables (resaltado)

### RF-18: Visualización de Mesa de Juego
- **Descripción:** El sistema debe mostrar el estado actual del juego.
- **Prioridad:** Alta
- **Criterios de Aceptación:**
  - Cartas jugadas en la baza actual
  - Palo de triunfo visible
  - Turno actual indicado
  - Apuestas y bazas ganadas de cada jugador

### RF-19: Historial de Jugadas
- **Descripción:** El sistema debe permitir revisar las bazas anteriores de la ronda.
- **Prioridad:** Baja
- **Criterios de Aceptación:**
  - Ver quién ganó cada baza anterior
  - Ver qué cartas se jugaron en cada baza

## 8. Flujo de Juego

### RF-20: Progresión de Rondas
- **Descripción:** El sistema debe gestionar automáticamente la transición entre rondas.
- **Prioridad:** Alta
- **Criterios de Aceptación:**
  - Mostrar resumen de puntuación al final de cada ronda
  - Permitir continuar a la siguiente ronda
  - Cambiar repartidor en sentido horario
  - Ajustar número de cartas según ronda actual

### RF-21: Persistencia de Partida
- **Descripción:** El sistema debe permitir guardar y reanudar partidas.
- **Prioridad:** Baja
- **Criterios de Aceptación:**
  - Guardar estado completo del juego
  - Recuperar partidas guardadas
  - Validar integridad de datos al cargar

## 9. Multijugador

### RF-22: Sala de Espera
- **Descripción:** El sistema debe proporcionar una sala de espera para que los jugadores se unan.
- **Prioridad:** Media
- **Criterios de Aceptación:**
  - Mostrar jugadores conectados
  - Permitir al creador iniciar la partida
  - Mínimo 3 jugadores para iniciar

### RF-23: Sincronización en Tiempo Real
- **Descripción:** El sistema debe sincronizar el estado del juego entre todos los jugadores.
- **Prioridad:** Alta
- **Criterios de Aceptación:**
  - Actualizar interfaz cuando otro jugador realiza una acción
  - Mostrar notificaciones de turno
  - Manejar desconexiones de jugadores

### RF-24: Chat (Opcional)
- **Descripción:** El sistema puede incluir un chat para comunicación entre jugadores.
- **Prioridad:** Baja
- **Criterios de Aceptación:**
  - Enviar mensajes de texto
  - Ver mensajes de otros jugadores
  - Moderación básica (sin spam)

## 10. Configuración y Opciones

### RF-25: Configuración de Partida
- **Descripción:** El sistema debe permitir configurar opciones antes de iniciar.
- **Prioridad:** Baja
- **Criterios de Aceptación:**
  - Activar/desactivar variantes (carta en frente, sin triunfo)
  - Ajustar velocidad de animaciones
  - Configurar número de rondas (si se desea juego más corto)

### RF-26: Tutorial/Ayuda
- **Descripción:** El sistema debe proporcionar ayuda sobre las reglas.
- **Prioridad:** Media
- **Criterios de Aceptación:**
  - Acceso a las reglas desde cualquier pantalla
  - Tutorial interactivo para nuevos jugadores (opcional)
  - Tooltips explicativos en la interfaz

---

## Priorización de Sprints (Sugerencia)

### Sprint 1 (Funcionalidad Básica)
- RF-01, RF-03, RF-04, RF-05, RF-06, RF-07

### Sprint 2 (Mecánica de Juego)
- RF-08, RF-10, RF-11, RF-12, RF-13

### Sprint 3 (Puntuación e Interfaz)
- RF-14, RF-15, RF-16, RF-17, RF-18, RF-20

### Sprint 4 (Multijugador)
- RF-22, RF-23

### Sprint 5 (Pulido y Extras)
- RF-09, RF-19, RF-21, RF-24, RF-25, RF-26
