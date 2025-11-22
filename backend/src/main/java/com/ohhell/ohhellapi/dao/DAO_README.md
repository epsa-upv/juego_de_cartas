# DAOs - Oh Hell! API

**Proyecto:** Oh Hell! Card Game  
**Universidad:** Universitat Politècnica de València  
**Autor:** Tomás Criado García  
**Sprint:** 3

---

## 📚 Contenido

Este paquete contiene los Data Access Objects (DAOs) para el proyecto Oh Hell! API. Los DAOs proporcionan una capa de abstracción entre la lógica de negocio y la base de datos PostgreSQL.

### Archivos Incluidos

1. **DatabaseConnection.java** - Gestión centralizada de conexión a PostgreSQL
2. **PlayerDAO.java** - Operaciones CRUD para jugadores
3. **GameDAO.java** - Operaciones CRUD para partidas
4. **RoundDAO.java** - Operaciones CRUD para rondas
5. **BidDAO.java** - Operaciones CRUD para apuestas
6. **TrickDAO.java** - Operaciones CRUD para bazas

---

## 🚀 Instalación

### 1. Copiar archivos al proyecto

Copia todos los archivos `.java` al directorio:
```
src/main/java/com/ohhell/ohhellapi/dao/
```

### 2. Estructura final

```
src/main/java/com/ohhell/ohhellapi/
├── dao/
│   ├── DatabaseConnection.java
│   ├── PlayerDAO.java
│   ├── GameDAO.java
│   ├── RoundDAO.java
│   ├── BidDAO.java
│   └── TrickDAO.java
├── models/
│   ├── Player.java
│   ├── Game.java
│   ├── Round.java
│   ├── Bid.java
│   ├── Trick.java
│   └── Card.java
└── resources/
    ├── PlayerResource.java
    ├── GameResource.java
    ├── RoundResource.java
    ├── BidResource.java
    └── TrickResource.java
```

---

## 💻 Uso de los DAOs

### DatabaseConnection

Gestiona la conexión singleton a PostgreSQL en Render.

```java
// Obtener conexión
Connection conn = DatabaseConnection.getConnection();

// Verificar si está activa
boolean isActive = DatabaseConnection.isConnectionActive();

// Cerrar conexión
DatabaseConnection.closeConnection();
```

**Nota:** No necesitas gestionar la conexión manualmente, los DAOs la gestionan automáticamente.

---

### PlayerDAO

Gestiona operaciones CRUD con jugadores.

#### Obtener todos los jugadores
```java
PlayerDAO playerDAO = new PlayerDAO();
List<Player> players = playerDAO.getAllPlayers();
```

#### Obtener jugador por ID
```java
Player player = playerDAO.getPlayerById(1L);
```

#### Crear nuevo jugador
```java
Player newPlayer = new Player();
newPlayer.setName("Tomás");
newPlayer.setLives(3);
newPlayer.setStatus(Player.PlayerStatus.ACTIVE);

Player created = playerDAO.createPlayer(newPlayer);
System.out.println("Jugador creado con ID: " + created.getId());
```

#### Actualizar jugador
```java
player.setLives(2);
boolean updated = playerDAO.updatePlayer(player);
```

#### Eliminar jugador
```java
boolean deleted = playerDAO.deletePlayer(1L);
```

#### Actualizar solo las vidas
```java
playerDAO.updatePlayerLives(1L, 1);
```

#### Obtener jugadores por estado
```java
List<Player> activePlayers = playerDAO.getPlayersByStatus("ACTIVE");
```

---

### GameDAO

Gestiona operaciones CRUD con partidas.

#### Crear nueva partida
```java
GameDAO gameDAO = new GameDAO();

Game newGame = new Game();
newGame.setName("Partida de prueba");
newGame.setMaxPlayers(4);
newGame.setInitialLives(3);
newGame.setStatus(Game.GameStatus.WAITING);

Game created = gameDAO.createGame(newGame);
```

#### Obtener partida por ID
```java
Game game = gameDAO.getGameById(1L);
```

#### Actualizar estado de partida
```java
gameDAO.updateGameStatus(1L, Game.GameStatus.IN_PROGRESS);
```

#### Finalizar partida
```java
gameDAO.finishGame(1L, winnerId);
```

#### Verificar si está llena
```java
boolean isFull = gameDAO.isGameFull(1L);
```

#### Contar jugadores
```java
int playerCount = gameDAO.countPlayersInGame(1L);
```

#### Obtener partidas por estado
```java
List<Game> activeGames = gameDAO.getGamesByStatus("IN_PROGRESS");
```

---

### RoundDAO

Gestiona operaciones CRUD con rondas.

#### Crear nueva ronda
```java
RoundDAO roundDAO = new RoundDAO();

Round newRound = new Round();
newRound.setGameId(1L);
newRound.setRoundNumber(1);
newRound.setNumCards(5);
newRound.setDealerId(2L);

Round created = roundDAO.createRound(newRound);
```

#### Obtener rondas de una partida
```java
List<Round> rounds = roundDAO.getRoundsByGameId(1L);
```

#### Obtener ronda actual
```java
Round currentRound = roundDAO.getCurrentRound(1L);
```

#### Actualizar estado de ronda
```java
roundDAO.updateRoundStatus(1L, "PLAYING");
```

#### Contar rondas
```java
int roundCount = roundDAO.countRoundsInGame(1L);
```

---

### BidDAO

Gestiona operaciones CRUD con apuestas.

#### Crear nueva apuesta
```java
BidDAO bidDAO = new BidDAO();

Bid newBid = new Bid();
newBid.setPlayerId(1L);
newBid.setRoundId(1L);
newBid.setBidAmount(3);
newBid.setTricksWon(0);

Bid created = bidDAO.createBid(newBid);
```

#### Obtener apuestas de una ronda
```java
List<Bid> bids = bidDAO.getBidsByRoundId(1L);
```

#### Obtener apuesta de un jugador
```java
Bid bid = bidDAO.getBidByPlayerAndRound(1L, 1L);
```

#### Incrementar bazas ganadas
```java
bidDAO.incrementTricksWon(1L);
```

#### Calcular suma total de apuestas
```java
int totalBids = bidDAO.getTotalBidsForRound(1L);
```

#### Verificar si todos apostaron
```java
boolean allBid = bidDAO.allPlayersHaveBid(1L, 4);
```

---

### TrickDAO

Gestiona operaciones CRUD con bazas.

#### Crear nueva baza
```java
TrickDAO trickDAO = new TrickDAO();

Trick newTrick = new Trick();
newTrick.setRoundId(1L);
newTrick.setTrickNumber(1);
newTrick.setCompleted(false);

Trick created = trickDAO.createTrick(newTrick);
```

#### Obtener bazas de una ronda
```java
List<Trick> tricks = trickDAO.getTricksByRoundId(1L);
```

#### Obtener baza actual
```java
Trick currentTrick = trickDAO.getCurrentTrick(1L);
```

#### Completar baza
```java
trickDAO.completeTrick(1L, winnerId);
```

#### Contar bazas completadas
```java
int completed = trickDAO.countCompletedTricksInRound(1L);
```

#### Obtener bazas ganadas por jugador
```java
int tricksWon = trickDAO.getTricksWonByPlayer(1L, 1L);
```

---

## 🔗 Integración con Resources

### Ejemplo: PlayerResource con PlayerDAO

```java
@Path("v1/players")
public class PlayerResource {
    
    private PlayerDAO playerDAO = new PlayerDAO();
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllPlayers() {
        try {
            List<Player> players = playerDAO.getAllPlayers();
            return Response.ok(players).build();
        } catch (SQLException e) {
            return Response.status(500)
                .entity("Error: " + e.getMessage())
                .build();
        }
    }
    
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPlayer(@PathParam("id") Long id) {
        try {
            Player player = playerDAO.getPlayerById(id);
            if (player == null) {
                return Response.status(404)
                    .entity("Player not found")
                    .build();
            }
            return Response.ok(player).build();
        } catch (SQLException e) {
            return Response.status(500)
                .entity("Error: " + e.getMessage())
                .build();
        }
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createPlayer(Player player) {
        try {
            Player created = playerDAO.createPlayer(player);
            return Response.status(201)
                .entity(created)
                .build();
        } catch (SQLException e) {
            return Response.status(500)
                .entity("Error: " + e.getMessage())
                .build();
        }
    }
    
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePlayer(@PathParam("id") Long id, Player player) {
        try {
            player.setId(id);
            boolean updated = playerDAO.updatePlayer(player);
            if (!updated) {
                return Response.status(404).build();
            }
            return Response.ok().build();
        } catch (SQLException e) {
            return Response.status(500)
                .entity("Error: " + e.getMessage())
                .build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    public Response deletePlayer(@PathParam("id") Long id) {
        try {
            boolean deleted = playerDAO.deletePlayer(id);
            if (!deleted) {
                return Response.status(404).build();
            }
            return Response.status(204).build();
        } catch (SQLException e) {
            return Response.status(500)
                .entity("Error: " + e.getMessage())
                .build();
        }
    }
}
```

---

## ⚠️ Manejo de Errores

Todos los DAOs lanzan `SQLException` que debe ser capturada en los Resources:

```java
try {
    // Operación con DAO
    Player player = playerDAO.getPlayerById(1L);
} catch (SQLException e) {
    // Log del error
    System.err.println("Error en BD: " + e.getMessage());
    e.printStackTrace();
    
    // Respuesta HTTP 500
    return Response.status(500)
        .entity("Error de base de datos")
        .build();
}
```

---

## 🔒 Seguridad

### SQL Injection Prevention

Todos los DAOs usan **PreparedStatement** para prevenir SQL Injection:

```java
// ✅ CORRECTO - Uso de PreparedStatement
String query = "SELECT * FROM players WHERE id = ?";
PreparedStatement pstmt = conn.prepareStatement(query);
pstmt.setLong(1, playerId);

// ❌ INCORRECTO - Vulnerable a SQL Injection
String query = "SELECT * FROM players WHERE id = " + playerId;
Statement stmt = conn.createStatement();
```

---

## 🧪 Testing

### Ejemplo de test unitario con JUnit

```java
@Test
public void testCreatePlayer() throws SQLException {
    PlayerDAO playerDAO = new PlayerDAO();
    
    Player player = new Player();
    player.setName("Test Player");
    player.setLives(3);
    player.setStatus(Player.PlayerStatus.ACTIVE);
    
    Player created = playerDAO.createPlayer(player);
    
    assertNotNull(created.getId());
    assertEquals("Test Player", created.getName());
    assertEquals(3, created.getLives());
}
```

---

## 📊 Diagrama de Dependencias

```
Resources (JAX-RS)
    ↓
   DAOs
    ↓
DatabaseConnection
    ↓
PostgreSQL (Render)
```

---

## ✅ Checklist de Integración

- [ ] Copiar todos los archivos DAO a `src/main/java/com/ohhell/ohhellapi/dao/`
- [ ] Verificar que los modelos existen en `src/main/java/com/ohhell/ohhellapi/models/`
- [ ] Actualizar `DatabaseConnection.java` con credenciales de PostgreSQL
- [ ] Integrar DAOs en los Resources existentes
- [ ] Probar endpoints con Postman
- [ ] Manejar excepciones SQLException
- [ ] Implementar logging de errores

---

## 📞 Soporte

**Universidad:** Universitat Politècnica de València  
**Proyecto:** Oh Hell! Card Game  
**Sprint 3:** Recursos y DAOs

Para más información, consulta:
- Documentación API REST
- Diagramas UML del proyecto
- Esquema de base de datos PostgreSQL

---

**¡Los DAOs están listos para usar!** 🎉
