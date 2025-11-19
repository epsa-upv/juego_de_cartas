create database juego;
use juego;
-- Tabla Usuarios
CREATE TABLE Usuarios (
                          id_usuario INT AUTO_INCREMENT PRIMARY KEY,
                          nombre_usuario VARCHAR(50) NOT NULL,
                          email VARCHAR(100) UNIQUE NOT NULL,
                          contrasena VARCHAR(100) NOT NULL,
                          tipo VARCHAR(20) NOT NULL,
                          CONSTRAINT chk_tipo CHECK (tipo IN ('jugador', 'desarrollador'))
);

-- Tabla Jugadores
CREATE TABLE Jugadores (
                           id_jugador INT AUTO_INCREMENT PRIMARY KEY,
                           id_usuario INT NOT NULL,
                           experiencia INT DEFAULT 0,
                           nivel INT DEFAULT 1,
                           FOREIGN KEY (id_usuario) REFERENCES Usuarios(id_usuario) ON DELETE CASCADE
);

-- Tabla Desarrolladores
CREATE TABLE Desarrolladores (
                                 id_desarrollador INT AUTO_INCREMENT PRIMARY KEY,
                                 id_usuario INT NOT NULL,
                                 nombre_estudio VARCHAR(100),
                                 FOREIGN KEY (id_usuario) REFERENCES Usuarios(id_usuario) ON DELETE CASCADE
);

-- Tabla Juegos
CREATE TABLE Juegos (
                        id_juego INT AUTO_INCREMENT PRIMARY KEY,
                        id_desarrollador INT NOT NULL,
                        nombre VARCHAR(100) NOT NULL,
                        descripcion VARCHAR(255),
                        genero VARCHAR(50),
                        fecha_lanzamiento DATE,
                        FOREIGN KEY (id_desarrollador) REFERENCES Desarrolladores(id_desarrollador) ON DELETE CASCADE
);

-- Tabla Niveles
CREATE TABLE Niveles (
                         id_nivel INT AUTO_INCREMENT PRIMARY KEY,
                         id_juego INT NOT NULL,
                         nombre VARCHAR(100),
                         dificultad VARCHAR(20),
                         FOREIGN KEY (id_juego) REFERENCES Juegos(id_juego) ON DELETE CASCADE
);

-- Tabla Partidas
CREATE TABLE Partidas (
                          id_partida INT AUTO_INCREMENT PRIMARY KEY,
                          id_jugador INT NOT NULL,
                          id_juego INT NOT NULL,
                          fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
                          duracion INT,
                          puntaje INT,
                          FOREIGN KEY (id_jugador) REFERENCES Jugadores(id_jugador) ON DELETE CASCADE,
                          FOREIGN KEY (id_juego) REFERENCES Juegos(id_juego)
);

-- Tabla Inventarios
CREATE TABLE Inventarios (
                             id_inventario INT AUTO_INCREMENT PRIMARY KEY,
                             id_jugador INT NOT NULL,
                             objeto VARCHAR(100),
                             cantidad INT DEFAULT 1,
                             FOREIGN KEY (id_jugador) REFERENCES Jugadores(id_jugador) ON DELETE CASCADE
);

-- Tabla Logros
CREATE TABLE Logros (
                        id_logro INT AUTO_INCREMENT PRIMARY KEY,
                        id_juego INT NOT NULL,
                        nombre VARCHAR(100),
                        descripcion VARCHAR(255),
                        FOREIGN KEY (id_juego) REFERENCES Juegos(id_juego) ON DELETE CASCADE
);

-- Relación muchos a muchos entre jugadores y logros
CREATE TABLE LogrosJugadores (
                                 id_jugador INT NOT NULL,
                                 id_logro INT NOT NULL,
                                 fecha_obtenido DATETIME DEFAULT CURRENT_TIMESTAMP,
                                 PRIMARY KEY (id_jugador, id_logro),
                                 FOREIGN KEY (id_jugador) REFERENCES Jugadores(id_jugador) ON DELETE CASCADE,
                                 FOREIGN KEY (id_logro) REFERENCES Logros(id_logro)
);

-- Tabla Compras
CREATE TABLE Compras (
                         id_compra INT AUTO_INCREMENT PRIMARY KEY,
                         id_jugador INT NOT NULL,
                         id_juego INT NOT NULL,
                         fecha_compra DATETIME DEFAULT CURRENT_TIMESTAMP,
                         monto DECIMAL(10,2),
                         FOREIGN KEY (id_jugador) REFERENCES Jugadores(id_jugador) ON DELETE CASCADE,
                         FOREIGN KEY (id_juego) REFERENCES Juegos(id_juego)
);
