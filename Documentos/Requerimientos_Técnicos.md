# Requerimientos Técnicos - Oh Hell! Game (Alineado con ERS v1.0)

## 1. Stack Tecnológico

### 1.1 Frontend (Capa de Presentación)
- **HTML5** - Estructura semántica.
- **CSS3** - Diseño visual.
- **JavaScript (Vanilla)** - Lógica del cliente, manejo de DOM y peticiones `fetch` al API.
- **Cliente HTTP** - Uso de Fetch API estándar para consumir JSON.

### 1.2 Backend (Capa de Servicios y Negocio)
- [cite_start]**Lenguaje:** Java 21 (Entorno de ejecución JVM).
- **Framework:** Jakarta EE 10.
- **API REST:** JAX-RS (Jakarta RESTful Web Services) para exponer endpoints.
- **Inyección de Dependencias:** CDI (Contexts and Dependency Injection) para lógica de negocio.
- **Mapeo JSON:** JSON-B o Jackson.

### 1.3 Capa de Persistencia (Data)
- **ORM:** JPA (Jakarta Persistence API).
- **Driver:** JDBC PostgreSQL Driver.
- **Base de Datos:** PostgreSQL (Alojada en Render).

### 1.4 Infraestructura de Servidor
- **Servidor de Aplicaciones:** Apache TomEE (Web Profile o Plus).
- **Artefacto de Despliegue:** Archivo `.war` (Web Archive).

## 2. Arquitectura del Sistema

### 2.1 Patrón Arquitectónico
El sistema sigue un patrón en capas estricto sobre MVC desacoplado:
1.  **Frontend (Vista):** Archivos estáticos en navegador.
2.  **API (Controlador):** JAX-RS Endpoints (`com.ohhell.ohhellapi.resources`).
3.  **Core (Modelo/Negocio):** Servicios CDI (`com.ohhell.ohhellapi.services`).
4.  **Datos (DAO):** Repositorios JPA (`com.ohhell.ohhellapi.dao`).

### 2.2 Diagrama de Despliegue Lógico
### Esquema de Despliegue y Comunicación
```
| Nodo / Capa | Entorno / Artefacto | Componentes Internos | Comunicación (Salida) |
| :--- | :--- | :--- | :--- |
| **Navegador Cliente** | Cliente Web | • Interfaz de Usuario (HTML/JS) | **JSON / HTTP(S)** <br>*(Hacia Apache TomEE)* |
| **Servidor de Aplicaciones** | **Apache TomEE** | **[WAR: OhHellAPI]**<br>• **JAX-RS:** Router / API REST<br>• **CDI:** Lógica de Negocio (Game, Round)<br>• **JPA:** Persistencia de Datos | **JDBC sobre SSL** <br>*(Hacia PostgreSQL)* |
| **Servidor de Base de Datos** | **PostgreSQL** (@Render) | • Tablas Relacionales<br>• Esquema de Persistencia | N/A |
```

## 3. Estructura del Proyecto (Maven)

### 3.1 Estructura de Paquetes (Según ERS 3.1)
```
src/main/java/

└── com/

└── ohhell/

└── ohhellapi/

├── resources/    # Controladores JAX-RS (@Path)

├── services/     # Lógica de Negocio (@RequestScoped/Singleton)

├── models/       # Modelos de dominio

│   ├── entities/ # Entidades JPA (@Entity: Game, Player, Round)

│   └── dtos/     # Data Transfer Objects (Para JSON)

├── dao/          # Data Access Objects (EntityManager)

└── utils/        # Barajado, Constantes
```

## 5. Diseño de API y Seguridad
### 5.1 Seguridad
Gestión de Sesiones: Uso de cookies o tokens personalizados para identificar al Player en cada petición REST, ya que JAX-RS es stateless por defecto.

Validación: Toda entrada (Bid, Card) se valida en la capa services antes de afectar las entidades.
```
@Path("/game")
public class GameResource {
    
    @POST
    @Path("/create")
    public Response createGame(NewGameDTO config) { ... }

    @GET
    @Path("/{id}/state")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGameState(@PathParam("id") Long gameId) { 
        // Retorna DTO con estado actual para polling del frontend
    }

    @POST
    @Path("/{id}/play")
    public Response playCard(@PathParam("id") Long gameId, CardDTO card) { ... }
}
```

## 6. Configuración de Base de Datos (persistence.xml)
Ubicación: src/main/resources/META-INF/persistence.xml

```
<persistence version="3.0" xmlns="[https://jakarta.ee/xml/ns/persistence](https://jakarta.ee/xml/ns/persistence)">
    <persistence-unit name="OhHellPU">
        <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>
        <class>com.ohhell.ohhellapi.models.entities.Game</class>
        <class>com.ohhell.ohhellapi.models.entities.Player</class>
        <class>com.ohhell.ohhellapi.models.entities.Round</class>
        <properties>
            <property name="javax.persistence.jdbc.driver" value="org.postgresql.Driver"/>
            <property name="javax.persistence.jdbc.url" value="jdbc:postgresql://hostname:5432/ohhell_db?sslmode=require"/>
            <property name="javax.persistence.jdbc.user" value="${ENV_DB_USER}"/>
            <property name="javax.persistence.jdbc.password" value="${ENV_DB_PASS}"/>
        </properties>
    </persistence-unit>
</persistence>
```

## 7. Herramientas y Entorno

IDE: IntelliJ IDEA (Soporte para Jakarta EE).

Servidor Local: Descargar y configurar Apache TomEE 9/10 localmente.

Compilación: mvn clean package genera OhHellAPI.war.

Despliegue Local: Copiar el .war a la carpeta webapps de TomEE.
