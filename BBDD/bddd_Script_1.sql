CREATE TABLE Usuarios (
    id_usuario INT IDENTITY PRIMARY KEY,
    nombre_usuario NVARCHAR(50) NOT NULL,
    email NVARCHAR(100) UNIQUE NOT NULL,
    contrasena NVARCHAR(100) NOT NULL,
    tipo NVARCHAR(20) CHECK (tipo IN ('jugador', 'desarrollador')) NOT NULL
);

-- Tabla de jugadores
CREATE TABLE Jugadores (
    id_jugador INT IDENTITY PRIMARY KEY,
    id_usuario INT NOT NULL,
    experiencia INT DEFAULT 0,
    nivel INT DEFAULT 1,
    FOREIGN KEY (id_usuario) REFERENCES Usuarios(id_usuario) ON DELETE CASCADE
);

-- Tabla de desarrolladores
CREATE TABLE Desarrolladores (
    id_desarrollador INT IDENTITY PRIMARY KEY,
    id_usuario INT NOT NULL,
    nombre_estudio NVARCHAR(100),
    FOREIGN KEY (id_usuario) REFERENCES Usuarios(id_usuario) ON DELETE CASCADE
);

-- Tabla de juegos
CREATE TABLE Juegos (
    id_juego INT IDENTITY PRIMARY KEY,
    id_desarrollador INT NOT NULL,
    nombre NVARCHAR(100) NOT NULL,
    descripcion NVARCHAR(255),
    genero NVARCHAR(50),
    fecha_lanzamiento DATE,
    FOREIGN KEY (id_desarrollador) REFERENCES Desarrolladores(id_desarrollador) ON DELETE CASCADE
);

-- Tabla de niveles
CREATE TABLE Niveles (
    id_nivel INT IDENTITY PRIMARY KEY,
    id_juego INT NOT NULL,
    nombre NVARCHAR(100),
    dificultad NVARCHAR(20),
    FOREIGN KEY (id_juego) REFERENCES Juegos(id_juego) ON DELETE CASCADE
);

-- Tabla de partidas
CREATE TABLE Partidas (
    id_partida INT IDENTITY PRIMARY KEY,
    id_jugador INT NOT NULL,
    id_juego INT NOT NULL,
    fecha DATETIME DEFAULT GETDATE(),
    duracion INT,
    puntaje INT,
    FOREIGN KEY (id_jugador) REFERENCES Jugadores(id_jugador) ON DELETE CASCADE,
    FOREIGN KEY (id_juego) REFERENCES Juegos(id_juego) ON DELETE NO ACTION
);

-- Tabla de inventarios
CREATE TABLE Inventarios (
    id_inventario INT IDENTITY PRIMARY KEY,
    id_jugador INT NOT NULL,
    objeto NVARCHAR(100),
    cantidad INT DEFAULT 1,
    FOREIGN KEY (id_jugador) REFERENCES Jugadores(id_jugador) ON DELETE CASCADE
);

-- Tabla de logros
CREATE TABLE Logros (
    id_logro INT IDENTITY PRIMARY KEY,
    id_juego INT NOT NULL,
    nombre NVARCHAR(100),
    descripcion NVARCHAR(255),
    FOREIGN KEY (id_juego) REFERENCES Juegos(id_juego) ON DELETE CASCADE
);

-- Relación muchos a muchos entre jugadores y logros
CREATE TABLE LogrosJugadores (
    id_jugador INT NOT NULL,
    id_logro INT NOT NULL,
    fecha_obtenido DATETIME DEFAULT GETDATE(),
    PRIMARY KEY (id_jugador, id_logro),
    FOREIGN KEY (id_jugador) REFERENCES Jugadores(id_jugador) ON DELETE CASCADE,
    FOREIGN KEY (id_logro) REFERENCES Logros(id_logro) ON DELETE NO ACTION
);

-- Tabla de compras
CREATE TABLE Compras (
    id_compra INT IDENTITY PRIMARY KEY,
    id_jugador INT NOT NULL,
    id_juego INT NOT NULL,
    fecha_compra DATETIME DEFAULT GETDATE(),
    monto DECIMAL(10,2),
    FOREIGN KEY (id_jugador) REFERENCES Jugadores(id_jugador) ON DELETE CASCADE,
    FOREIGN KEY (id_juego) REFERENCES Juegos(id_juego) ON DELETE NO ACTION
);