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

[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/rxu1MK89)
rongheng xu - ronghengx@gmail.com
Wang Wenjie - xiaozhu9728@126.com
Gabriel Alexander Morales Aldana - gamorald@epsa.upv.es
Joan Torregrosa Alonso - jtoralo@epsa.upv.es
Tomás Engonga Ovono Nsuga - teovonsu@upv.edu.es
