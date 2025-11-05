# Requerimientos Técnicos y de Software - Oh Hell! Game

## 1. Stack Tecnológico

### 1.1 Frontend
- **HTML5** - Estructura de la aplicación web
- **CSS3** - Estilos y animaciones
- **JavaScript (Vanilla o Framework)** - Lógica del cliente
  - Opcional: React/Vue.js para componentes reactivos
- **WebSocket Client** - Comunicación en tiempo real

### 1.2 Backend
- **Java 17+** (LTS recomendado: Java 17 o 21)
- **Spring Boot 3.4.10** - Framework principal
- **Maven** - Gestión de dependencias y build
- **WebSocket (STOMP)** - Comunicación bidireccional

### 1.3 Base de Datos (Opcional)
- **H2** - Base de datos en memoria para desarrollo
- **PostgreSQL** - Base de datos para producción (estadísticas, usuarios)

## 2. Arquitectura del Sistema

### 2.1 Arquitectura General
```
┌─────────────────────────────────────────────┐
│           Navegador Web (Cliente)           │
│  ┌────────────┐  ┌──────────┐  ┌─────────┐ │
│  │   HTML5    │  │   CSS3   │  │   JS    │ │
│  └────────────┘  └──────────┘  └─────────┘ │
└─────────────────────────────────────────────┘
                    │ HTTP/WebSocket
┌─────────────────────────────────────────────┐
│          Spring Boot Server (Java)          │
│  ┌──────────────────────────────────────┐  │
│  │      Controller Layer (REST)         │  │
│  ├──────────────────────────────────────┤  │
│  │      Service Layer (Lógica)          │  │
│  ├──────────────────────────────────────┤  │
│  │      Model Layer (Entidades)         │  │
│  └──────────────────────────────────────┘  │
└─────────────────────────────────────────────┘
                    │ (Opcional)
┌─────────────────────────────────────────────┐
│          Base de Datos (PostgreSQL)         │
└─────────────────────────────────────────────┘
```

### 2.2 Patrones de Diseño
- **MVC (Model-View-Controller)** - Separación de responsabilidades
- **Repository Pattern** - Acceso a datos (si se usa BD)
- **Observer Pattern** - Para notificaciones en tiempo real
- **Singleton** - Gestión de salas de juego

## 3. Estructura del Proyecto

### 3.1 Estructura de Directorios
```
oh-hell-game/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── ohhell/
│   │   │           ├── OhHellApplication.java
│   │   │           ├── controller/
│   │   │           │   ├── GameController.java
│   │   │           │   └── WebSocketController.java
│   │   │           ├── model/
│   │   │           │   ├── Card.java
│   │   │           │   ├── Deck.java
│   │   │           │   ├── Player.java
│   │   │           │   ├── Game.java
│   │   │           │   ├── Round.java
│   │   │           │   └── Trick.java
│   │   │           ├── service/
│   │   │           │   ├── GameService.java
│   │   │           │   ├── DeckService.java
│   │   │           │   └── ScoringService.java
│   │   │           ├── dto/
│   │   │           │   ├── GameStateDTO.java
│   │   │           │   └── PlayerActionDTO.java
│   │   │           ├── exception/
│   │   │           │   └── InvalidMoveException.java
│   │   │           └── config/
│   │   │               └── WebSocketConfig.java
│   │   └── resources/
│   │       ├── static/
│   │       │   ├── css/
│   │       │   │   └── styles.css
│   │       │   ├── js/
│   │       │   │   └── game.js
│   │       │   └── assets/
│   │       │       ├── cards/
│   │       │       └── images/
│   │       ├── templates/
│   │       │   └── index.html
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/
│               └── ohhell/
│                   ├── GameServiceTest.java
│                   ├── ScoringServiceTest.java
│                   └── DeckServiceTest.java
├── pom.xml
└── README.md
```

## 4. Dependencias Maven

### 4.1 pom.xml Básico
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.4.10</version>
        <relativePath/>
    </parent>
    
    <groupId>com.ohhell</groupId>
    <artifactId>oh-hell-game</artifactId>
    <version>1.0.0</version>
    <name>Oh Hell Game</name>
    <description>Card game where players predict exact tricks</description>
    
    <properties>
        <java.version>17</java.version>
    </properties>
    
    <dependencies>
        <!-- Spring Boot Web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <!-- WebSocket para multijugador -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-websocket</artifactId>
        </dependency>
        
        <!-- Thymeleaf para templates HTML (opcional) -->
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
        
        <!-- DevTools para desarrollo -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
        
        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        
        <!-- JUnit 5 (incluido en spring-boot-starter-test) -->
        
        <!-- H2 Database para desarrollo (opcional) -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

## 5. Configuración de Spring Boot

### 5.1 application.properties
```properties
# Configuración del servidor
server.port=8080
spring.application.name=oh-hell-game

# WebSocket
spring.websocket.allowed-origins=*

# Logging
logging.level.com.ohhell=DEBUG
logging.level.org.springframework.web=INFO

# H2 Console (solo desarrollo)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Thymeleaf
spring.thymeleaf.cache=false

# Recursos estáticos
spring.web.resources.static-locations=classpath:/static/
```

## 6. Requerimientos No Funcionales

### 6.1 Rendimiento
- **Tiempo de respuesta:** < 200ms para acciones de juego
- **Latencia WebSocket:** < 100ms para actualizaciones en tiempo real
- **Capacidad:** Soportar mínimo 10 partidas simultáneas
- **Concurrencia:** Manejar 4-7 jugadores por partida sin degradación

### 6.2 Escalabilidad
- Arquitectura preparada para escalado horizontal
- Sesiones de juego en memoria (considerar Redis para producción)
- Stateless REST API donde sea posible

### 6.3 Seguridad
- Validación de entrada en servidor (nunca confiar en cliente)
- Prevención de trampas (validación server-side de todas las jugadas)
- CORS configurado apropiadamente
- Rate limiting para prevenir spam

### 6.4 Usabilidad
- Interfaz responsive (móvil y escritorio)
- Tiempo de carga inicial < 3 segundos
- Feedback visual inmediato para todas las acciones
- Mensajes de error claros y útiles

### 6.5 Mantenibilidad
- Código documentado (JavaDoc)
- Tests unitarios con cobertura > 70%
- Convenciones de código consistentes
- Git con commits descriptivos

### 6.6 Compatibilidad
- **Navegadores soportados:**
  - Chrome 90+
  - Firefox 88+
  - Safari 14+
  - Edge 90+
- **Dispositivos:**
  - Desktop (Windows, macOS, Linux)
  - Tablets (iPad, Android)
  - Móviles (iOS 14+, Android 10+)

## 7. APIs y Endpoints

### 7.1 REST API Endpoints

#### Gestión de Partidas
```
POST   /api/games              - Crear nueva partida
GET    /api/games/{id}         - Obtener estado de partida
POST   /api/games/{id}/join    - Unirse a partida
DELETE /api/games/{id}         - Eliminar partida
GET    /api/games              - Listar partidas activas
```

#### Acciones de Juego
```
POST   /api/games/{id}/bet     - Realizar apuesta
POST   /api/games/{id}/play    - Jugar carta
GET    /api/games/{id}/state   - Obtener estado actual
POST   /api/games/{id}/start   - Iniciar partida
```

### 7.2 WebSocket Topics
```
/topic/game/{gameId}            - Actualizaciones de estado
/topic/game/{gameId}/players    - Eventos de jugadores
/topic/game/{gameId}/chat       - Mensajes de chat (opcional)

/app/game/{gameId}/action       - Enviar acción de jugador
```

### 7.3 Ejemplo de Mensajes WebSocket

#### GameStateUpdate
```json
{
  "type": "GAME_STATE_UPDATE",
  "gameId": "abc123",
  "currentRound": 1,
  "cardsPerPlayer": 10,
  "trumpSuit": "HEARTS",
  "currentTurn": "player1",
  "phase": "BETTING",
  "players": [
    {
      "id": "player1",
      "name": "Alice",
      "bet": null,
      "tricksWon": 0,
      "totalScore": 0
    }
  ]
}
```

#### PlayerAction
```json
{
  "type": "PLAY_CARD",
  "playerId": "player1",
  "card": {
    "suit": "SPADES",
    "rank": "ACE"
  }
}
```

## 8. Recursos Digitales

### 8.1 Cartas (Assets)

#### Repositorios Recomendados
1. **[SVG-cards](https://github.com/htdebeer/SVG-cards)** - SVG de alta calidad
2. **[OpenGameArt](https://opengameart.org/content/playing-cards-vector-png)** - Múltiples estilos

#### Formato de Archivos
- **Desarrollo:** SVG (escalable, ligero)
- **Producción:** PNG optimizadas (mejor rendimiento en navegador)
- **Tamaños recomendados:** 
  - Thumbnail: 71x96px
  - Normal: 142x192px
  - HD: 284x384px

#### Estructura de Assets
```
assets/
├── cards/
│   ├── svg/
│   │   ├── hearts_ace.svg
│   │   ├── hearts_2.svg
│   │   └── ...
│   ├── png/
│   │   ├── hearts_ace.png
│   │   └── ...
│   └── back.png
├── icons/
│   ├── heart.svg
│   ├── diamond.svg
│   ├── club.svg
│   └── spade.svg
└── sounds/ (opcional)
    ├── card_play.mp3
    ├── card_shuffle.mp3
    └── win.mp3
```

## 9. Herramientas de Desarrollo

### 9.1 IDEs Recomendados
- **IntelliJ IDEA** (Community o Ultimate)
- **Eclipse IDE for Java Developers**
- **Visual Studio Code** (con extensiones de Java)

### 9.2 Extensiones/Plugins Útiles
- Spring Tools
- Lombok Plugin
- SonarLint (calidad de código)
- GitLens (para Git)

### 9.3 Herramientas de Testing
- **JUnit 5** - Tests unitarios
- **Mockito** - Mocking de dependencias
- **Spring Boot Test** - Tests de integración
- **Postman** - Testing de API REST
- **WebSocket King** - Testing de WebSocket

### 9.4 Control de Versiones
- **Git** - Sistema de control de versiones
- **GitHub/GitLab** - Repositorio remoto
- **Estrategia de branching:** GitFlow o GitHub Flow

### 9.5 CI/CD (Opcional)
- GitHub Actions
- Jenkins
- GitLab CI

## 10. Entorno de Desarrollo

### 10.1 Requisitos Mínimos
- **Java JDK:** 17 o superior
- **Maven:** 3.8+
- **RAM:** 8GB mínimo (16GB recomendado)
- **Espacio en disco:** 2GB para proyecto + dependencias
- **Sistema Operativo:** Windows 10+, macOS 11+, Linux (cualquier distribución moderna)

### 10.2 Configuración Inicial
```bash
# 1. Clonar repositorio
git clone https://github.com/tu-repo/oh-hell-game.git
cd oh-hell-game

# 2. Compilar proyecto
mvn clean install

# 3. Ejecutar aplicación
mvn spring-boot:run

# 4. Acceder a la aplicación
# Navegador: http://localhost:8080
```

### 10.3 Variables de Entorno
```bash
# Desarrollo
export SPRING_PROFILES_ACTIVE=dev
export SERVER_PORT=8080

# Producción
export SPRING_PROFILES_ACTIVE=prod
export DATABASE_URL=jdbc:postgresql://localhost:5432/ohhell
export DATABASE_USERNAME=user
export DATABASE_PASSWORD=password
```

## 11. Testing

### 11.1 Estrategia de Testing
- **Tests Unitarios:** > 70% cobertura
- **Tests de Integración:** Endpoints críticos
- **Tests E2E:** Flujo completo de juego (opcional)

### 11.2 Estructura de Tests
```java
// Ejemplo de test unitario
@Test
void shouldCalculateScoreCorrectly() {
    Player player = new Player("Alice");
    player.setBet(3);
    player.setTricksWon(3);
    
    int score = scoringService.calculateScore(player);
    
    assertEquals(13, score); // 3 + 10 bonus
}
```

## 12. Despliegue

### 12.1 Empaquetado
```bash
# Crear JAR ejecutable
mvn clean package

# Resultado: target/oh-hell-game-1.0.0.jar
```

### 12.2 Opciones de Despliegue

#### Opción 1: Servidor Local
```bash
java -jar target/oh-hell-game-1.0.0.jar
```

#### Opción 2: Docker
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/oh-hell-game-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

#### Opción 3: Cloud Platforms
- **Heroku** - Fácil despliegue con Git
- **AWS Elastic Beanstalk** - Escalabilidad automática
- **Google Cloud Run** - Serverless containers
- **Railway.app** - Despliegue simple y gratuito

## 13. Monitoreo y Logs

### 13.1 Spring Boot Actuator
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

### 13.2 Endpoints de Monitoreo
- `/actuator/health` - Estado de la aplicación
- `/actuator/metrics` - Métricas de rendimiento
- `/actuator/info` - Información de la aplicación

### 13.3 Logging
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class GameService {
    private static final Logger logger = LoggerFactory.getLogger(GameService.class);
    
    public void startGame(String gameId) {
        logger.info("Starting game: {}", gameId);
        // ...
    }
}
```

## 14. Documentación

### 14.1 Documentación Requerida
- README.md (instrucciones de instalación)
- JavaDoc en clases y métodos públicos
- Swagger/OpenAPI para documentar API REST
- Diagramas de arquitectura (opcional)

### 14.2 Swagger Configuration
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.2.0</version>
</dependency>
```

Acceso: `http://localhost:8080/swagger-ui.html`

---

## 15. Checklist de Calidad

### Antes de cada commit:
- [ ] Código compila sin errores
- [ ] Tests pasan exitosamente
- [ ] Código formateado correctamente
- [ ] Sin código comentado innecesario
- [ ] Variables con nombres descriptivos

### Antes de cada merge:
- [ ] Code review completado
- [ ] Cobertura de tests mantenida/mejorada
- [ ] Documentación actualizada
- [ ] Sin conflictos de merge
- [ ] Aplicación funciona en local

### Antes de desplegar:
- [ ] Todas las features funcionan
- [ ] Tests E2E pasando
- [ ] Performance aceptable
- [ ] Variables de entorno configuradas
- [ ] Backup de datos (si aplica)
