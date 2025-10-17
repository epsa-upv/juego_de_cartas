# Oh Hell! - Juego de Cartas

## 📖 Descripción

**Oh Hell!** (también conocido como *Oh Cielo*, *El Infierno*, *La Porra*, *Remigio*, *Contract Whist*) es un juego de bazas simple pero divertido donde los jugadores deben predecir exactamente cuántas bazas ganarán en cada ronda.

## 🎮 Reglas del Juego

### Configuración Inicial
- **Jugadores:** 3-7 jugadores (óptimo: 4 jugadores)
- **Baraja:** 52 cartas estándar (baraja francesa/inglesa)
- **Objetivo:** Predecir y ganar el número exacto de bazas apostadas

### Reparto de Cartas

El juego consta de **19 rondas** con la siguiente progresión:

| Ronda | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 |
|-------|---|---|---|---|---|---|---|---|---|-----|
| Cartas| 10| 9 | 8 | 7 | 6 | 5 | 4 | 3 | 2 | 1  |

| Ronda | 11| 12| 13| 14| 15| 16| 17| 18| 19 |
|-------|---|---|---|---|---|---|---|---|-----|
| Cartas| 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 |

### Fase de Apuestas

1. Después de repartir las cartas, se voltea la carta superior del mazo: su palo se convierte en el **palo de triunfo**.
2. Cada jugador apuesta cuántas bazas cree que ganará en esa ronda.
3. **Regla especial:** El último jugador en apostar **NO puede** hacer una apuesta que permita que todos cumplan sus predicciones. La suma de todas las apuestas debe ser diferente al número total de bazas disponibles.

**Ejemplo:** En una ronda de 10 cartas con 4 jugadores, si los primeros 3 jugadores apuestan 3+3+3=9, el último jugador NO puede apostar 1 (porque 9+1=10, el total de bazas).

### Juego

1. El jugador a la izquierda del repartidor inicia jugando una carta.
2. Los demás jugadores deben **seguir el palo** si pueden.
3. **Gana la baza:**
   - El jugador con la carta de mayor rango del palo jugado, O
   - Si alguien juega triunfo (y no se jugó el palo inicial), el triunfo más alto gana.
4. Se pueden jugar cartas de triunfo desde el principio.

### Puntuación

Después de cada ronda:
- **+1 punto** por cada baza ganada
- **+10 puntos de bonificación** si aciertas exactamente tu apuesta

**Ejemplo:**
- Apostaste 3 bazas y ganaste 3: **13 puntos** (3 + 10 bonus)
- Apostaste 3 bazas y ganaste 2: **2 puntos** (sin bonus)
- Apostaste 3 bazas y ganaste 4: **4 puntos** (sin bonus)

### Victoria

El jugador con **más puntos** después de las 19 rondas gana el juego. Es posible empatar.

## 🎨 Recursos de Cartas Digitales

### Baraja Francesa/Inglesa (52 cartas)

#### Repositorios GitHub
- **[SVG-cards](https://github.com/htdebeer/SVG-cards)** - Baraja completa en SVG y PNG de alta calidad
- **[vector-playing-cards](https://github.com/cbmeeks/vector-playing-cards)** - Cartas vectoriales editables

#### Sitios de Recursos Gratuitos
- **[OpenGameArt.org](https://opengameart.org/content/playing-cards-vector-png)** - Formato vectorial y PNG
- **[Tekeye.uk](https://www.tekeye.uk/playing_cards/svg-playing-cards)** - SVG de dominio público
- **[Wikimedia Commons](https://commons.wikimedia.org/wiki/Category:SVG_playing_cards)** - Múltiples estilos de barajas
- **[OpenClipArt](https://openclipart.org/)** - Gráficos libres de uso
- **[Imágenes interfaz](https://iconscout.com/es/illustrations/jugando-a-las-cartas-con-hijo)** - imagenes

#### Cartas de Nicu Buculei
Las cartas utilizadas en CardGames.io fueron creadas por **Nicu Buculei** y están disponibles gratuitamente.

### Formatos Disponibles
- **SVG** - Vectorial, escalable sin pérdida de calidad
- **PNG** - Imágenes rasterizadas en diferentes resoluciones
- **JSON** - Datos estructurados de cartas para desarrollo

### Licencias
La mayoría de estos recursos están bajo:
- Dominio público
- Creative Commons (CC0, CC BY)
- Licencias GPL/MIT para uso libre

## 🛠️ Implementación

### Stack Tecnológico
- **Frontend:** HTML5 + CSS3
- **Backend:** Java (JDK 17+) con Spring Boot
- **Comunicación:** REST API + WebSockets (para multijugador en tiempo real)
- **Build Tool:** Maven o Gradle
- **Testing:** JUnit 5
- **Base de Datos:** H2 (desarrollo) / PostgreSQL (producción) - opcional para estadísticas

### Estructura Básica del Proyecto
```
oh-hell-game/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── ohhell/
│   │   │           ├── OhHellApplication.java      # Main Spring Boot
│   │   │           ├── controller/
│   │   │           │   ├── GameController.java     # REST endpoints
│   │   │           │   └── WebSocketController.java # WebSocket para tiempo real
│   │   │           ├── model/
│   │   │           │   ├── Game.java               # Lógica principal del juego
│   │   │           │   ├── Deck.java               # Manejo de la baraja
│   │   │           │   ├── Card.java               # Clase de carta
│   │   │           │   ├── Player.java             # Clase de jugador
│   │   │           │   ├── Round.java              # Manejo de rondas
│   │   │           │   └── Trick.java              # Manejo de bazas
│   │   │           ├── service/
│   │   │           │   ├── GameService.java        # Lógica de negocio
│   │   │           │   └── ScoringService.java     # Sistema de puntuación
│   │   │           └── config/
│   │   │               └── WebSocketConfig.java    # Configuración WebSocket
│   │   └── resources/
│   │       ├── static/
│   │       │   ├── css/
│   │       │   │   └── styles.css                  # Estilos del juego
│   │       │   ├── js/
│   │       │   │   └── game.js                     # Lógica frontend
│   │       │   └── assets/
│   │       │       ├── cards/                      # Imágenes de cartas
│   │       │       ├── sounds/                     # Efectos de sonido
│   │       │       └── images/                     # Otros gráficos
│   │       ├── templates/
│   │       │   └── index.html                      # Página principal
│   │       └── application.properties              # Configuración Spring
│   └── test/
│       └── java/
│           └── com/
│               └── ohhell/
│                   ├── GameTest.java                # Tests del juego
│                   └── ScoringTest.java             # Tests de puntuación
├── pom.xml                                          # Maven dependencies
└── README.md
```

### Dependencias Maven Recomendadas
```xml
<dependencies>
    <!-- Spring Boot Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- WebSocket para multijugador en tiempo real -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-websocket</artifactId>
    </dependency>
    
    <!-- Thymeleaf para templates HTML -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
    
    <!-- Lombok para reducir boilerplate -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    
    <!-- Testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## 📚 Referencias

- [CardGames.io - Oh Hell!](https://cardgames.io/ohell/) - Versión online jugable
- [Wikipedia - Oh Hell](https://en.wikipedia.org/wiki/Oh_Hell) - Historia y variantes

## 📝 Licencia

Este proyecto es de código abierto. Las cartas utilizadas deben cumplir con sus respectivas licencias (ver sección de recursos).

---

¿Tienes preguntas o sugerencias? ¡Abre un issue o envía un pull request!
##REQUERIMIENTOS
1. Requerimientos Funcionales (RF)
   RF1. Gestión de Jugadores

RF1.1: El sistema debe permitir que un jugador cree una partida nueva.

   RF1.2: El sistema debe permitir que otros jugadores se unan a una partida existente mediante una invitación o código.
   
   RF1.3: El sistema debe permitir entre 3 y 7 jugadores por partida (óptimo: 4).
   
   RF1.4: El sistema debe asignar turnos de forma automática, comenzando por el jugador a la izquierda del repartidor.

RF2. Gestión de la Baraja
   
   RF2.1: El sistema debe generar y mezclar una baraja francesa estándar de 52 cartas.
   
   RF2.2: El sistema debe repartir las cartas de acuerdo con el número de la ronda (de 10 a 1 y luego de 2 a 10).
   
   RF2.3: El sistema debe mostrar la carta superior del mazo para definir el palo de triunfo.

RF3. Sistema de Apuestas

   RF3.1: El sistema debe permitir que cada jugador apueste cuántas bazas cree que ganará en esa ronda.
   
   RF3.2: El sistema debe restringir al último jugador para que no pueda apostar un número que haga coincidir la suma                total de apuestas con el número de bazas disponibles.

RF4. Desarrollo del Juego

   RF4.1: El sistema debe permitir que el jugador que inicia la ronda juegue una carta.
   
   RF4.2: Los jugadores deben seguir el palo si pueden; si no, pueden jugar cualquier carta.
   
   RF4.3: El sistema debe determinar automáticamente el ganador de cada baza según las reglas:
   
         Gana la carta más alta del palo inicial.
         
         Si hay triunfo, gana el triunfo más alto.
         
         RF4.4: El sistema debe registrar las bazas ganadas por cada jugador.

RF5. Sistema de Puntuación

   RF5.1: El sistema debe calcular los puntos de cada jugador al finalizar la ronda:
   
   +1 punto por cada baza ganada.
   
   +10 puntos adicionales si el jugador acierta su apuesta.
   
   RF5.2: El sistema debe acumular los puntos de todas las rondas.
   
   RF5.3: El sistema debe mostrar un marcador actualizado después de cada ronda.
   
   RF5.4: Al finalizar las 19 rondas, el sistema debe mostrar el ganador final (o declarar empate si aplica).

RF6. Comunicación en Tiempo Real (Multijugador)

   RF6.1: El sistema debe utilizar WebSockets para sincronizar jugadas y actualizaciones entre jugadores en tiempo real.
   
   RF6.2: El sistema debe enviar notificaciones visuales o sonoras cuando:
   
   Es el turno de un jugador.
   
   Se gana una baza.
   
   Termina una ronda.

RF7. Interfaz de Usuario (Frontend)

   RF7.1: La interfaz debe mostrar las cartas del jugador y el estado actual de la partida (ronda, palo de triunfo, apuestas, puntuaciones).
   
   RF7.2: El sistema debe ofrecer efectos visuales y de sonido para mejorar la experiencia de juego.
   
   RF7.3: El usuario debe poder acceder al juego desde un navegador (HTML5/CSS3/JS).

RF8. Persistencia y Datos (opcional)

   RF8.1: El sistema debe guardar estadísticas de partidas y jugadores en una base de datos (PostgreSQL o H2 en desarrollo).
   
   RF8.2: El sistema debe permitir la consulta del historial de partidas y puntuaciones.
   
2. Requerimientos No Funcionales (RNF)
   
RNF1. Rendimiento

   RNF1.1: El sistema debe soportar al menos 7 jugadores conectados simultáneamente en una misma partida sin pérdida de rendimiento.
   
   RNF1.2: La latencia de comunicación en tiempo real (WebSocket) no debe superar los 500 ms entre acción y actualización visual.

RNF2. Usabilidad

   RNF2.1: La interfaz debe ser intuitiva, con elementos visuales claros (cartas, marcador, palo de triunfo).
   
   RNF2.2: El juego debe ser jugable tanto en escritorio como en dispositivos móviles (diseño responsive).
   
   RNF2.3: Se deben incluir mensajes de ayuda o tutorial básico.

RNF3. Fiabilidad

   RNF3.1: El sistema debe poder recuperarse de una desconexión momentánea sin perder el estado del juego.
   
   RNF3.2: Los datos de puntuación y ronda deben conservarse en caso de reinicio del servidor (si se usa persistencia).

RNF4. Seguridad

   RNF4.1: Las comunicaciones deben realizarse mediante canales seguros (HTTPS/WSS).
   
   RNF4.2: El sistema no debe permitir que un jugador ejecute acciones fuera de su turno.
   
   RNF4.3: Debe validarse la integridad de las jugadas en el servidor, no solo en el cliente.

RNF5. Escalabilidad

   RNF5.1: El sistema debe poder alojar múltiples partidas simultáneamente en el servidor.
   
   RNF5.2: La arquitectura debe permitir agregar nuevas funcionalidades (por ejemplo, chat, ranking global, IA) sin reestructurar el sistema principal.

RNF6. Mantenibilidad

   RNF6.1: El código debe estar estructurado en capas (controlador, servicio, modelo).
   
   RNF6.2: Deben incluirse pruebas unitarias con JUnit 5 para las clases principales (Game, ScoringService).
   
   RNF6.3: El sistema debe usar Lombok y convenciones de nombre claras para reducir complejidad.

RNF7. Portabilidad

   RNF7.1: La aplicación debe poder ejecutarse en cualquier entorno con JDK 17+ y navegador moderno.
   
   RNF7.2: El sistema debe poder desplegarse en servidores Spring Boot estándar o en contenedores Docker.

RNF8. Licencias y Recursos

   RNF8.1: Todos los recursos gráficos (cartas, íconos, sonidos) deben estar bajo licencias libres (CC0, CC BY, GPL, MIT).
   
   RNF8.2: Debe mantenerse un archivo LICENSE y créditos a los autores originales de los recursos utilizados.
[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/rxu1MK89)
rongheng xu - ronghengx@gmail.com
Wang Wenjie - xiaozhu9728@126.com
Gabriel Alexander Morales Aldana - gamorald@epsa.upv.es
Joan Torregrosa Alonso - jtoralo@epsa.upv.es
Tomás Engonga Ovono Nsuga - teovonsu@upv.edu.es
