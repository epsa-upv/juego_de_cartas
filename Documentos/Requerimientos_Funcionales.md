# Requerimientos Funcionales - Oh Hell! Game

## 1. Gestión de Jugadores

### RF-01: Registro de Jugadores
- **Descripción:** El sistema debe permitir registrar jugadores para una partida.
- **Prioridad:** Alta
- **Criterios de Aceptación:**
  - Mínimo 3 jugadores, máximo 4 jugadores (Para ajustar al límite de 13 cartas con baraja de 52: 52/4 = 13).
  - Cada jugador debe tener un nombre único ("Player") asociado a la entidad "Game".

### RF-02: Asignación del Primer Repartidor
- **Descripción:** El sistema asigna aleatoriamente el primer repartidor al iniciar el estado WAITING.
- **Prioridad:** Media

## 2. Gestión de la Baraja

### RF-03: Inicialización de la Baraja
- **Descripción:** El sistema utiliza una baraja francesa estándar de 52 cartas.
- **Prioridad:** Alta
- **Entidades:** Se utilizan objetos de valor "Card" (Palo y Valor).

### RF-04: Barajado
- **Descripción:** El módulo de utilidades baraja las cartas antes de instanciar una nueva "Round".
- **Prioridad:** Alta

### RF-05: Reparto de Cartas (Alineado con ERS)
- **Descripción:** El sistema reparte cartas según la ronda actual hasta un máximo de 13.
- **Prioridad:** Alta
- **Criterios de Aceptación:**
  - La "Round" gestiona el número de cartas.
  - El máximo de bazas ("Tricks") por ronda es 13.
  - Secuencia lógica: Rondas ascendentes o descendentes según configuración del "Game".

## 3. Gestión de Triunfo

### RF-06: Determinación del Palo de Triunfo
- **Descripción:** Se determina el triunfo al inicio de la ronda.
- **Prioridad:** Alta

## 4. Sistema de Apuestas (Bidding)

### RF-07: Registro de Apuestas
- **Descripción:** Cada "Player" realiza un "Bid" indicando bazas a ganar.
- **Prioridad:** Alta
- **Criterios:** El estado del juego pasa a "BETTING".

### RF-08: Validación de la regla "Oh Hell"
- **Descripción:** La suma de apuestas no puede igualar el número de cartas repartidas.
- **Prioridad:** Alta

## 5. Mecánica de Juego

### RF-9: Flujo de Bazas (Tricks)
- **Descripción:** El juego gestiona una secuencia de "Tricks" dentro de una "Round".
- **Prioridad:** Alta
- **Criterios:** Una ronda contiene máximo 13 tricks.

### RF-10: Ganador de Baza
- **Descripción:** El Core del Backend determina el ganador basado en reglas de palo y triunfo.
- **Prioridad:** Alta

## 6. Sistema de Puntuación

### RF-11: Cálculo de Puntuación
- **Descripción:** Al finalizar la ronda (Estado SCORING), se calculan los puntos.
- **Prioridad:** Alta
- **Lógica:** Se compara "Bid" vs "Tricks" ganados.

### RF-12: Ciclo de Vida de la Partida
- **Descripción:** El "Game" gestiona los estados WAITING -> IN_PROGRESS -> FINISHED.
- **Prioridad:** Alta
- **Criterios:** Al finalizar todas las rondas configuradas, el estado pasa a FINISHED y se declara ganador.

## 7. Interfaz de Usuario (Frontend)

### RF-13: Visualización (Vista)
- **Descripción:** El Frontend (HTML5/JS) renderiza el estado del juego recibido del API.
- **Prioridad:** Alta

### RF-14: Actualización de Estado
- **Descripción:** La vista debe refrescar la mesa de juego consultando el endpoint REST del controlador.
- **Prioridad:** Alta

## 8. Arquitectura y Red

### RF-15: Sincronización de Estado (Modelo Pull)
- **Descripción:** El cliente debe consultar periódicamente el estado del juego para reflejar cambios de otros jugadores.
- **Prioridad:** Alta
- **Nota Técnica:** Debido a la arquitectura REST pura (sin WebSockets), el cliente hace peticiones frecuentes (polling) para simular tiempo real.

### RF-16: Persistencia
- **Descripción:** El estado del juego se persiste en base de datos PostgreSQL mediante transacciones JPA.
- **Prioridad:** Alta
