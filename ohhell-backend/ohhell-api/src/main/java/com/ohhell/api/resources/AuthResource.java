package com.ohhell.api.resources;

import com.ohhell.api.dao.UserDAO;
import com.ohhell.api.dao.PlayerDAO;
import com.ohhell.api.models.LoginRequest;
import com.ohhell.api.models.Player;
import com.ohhell.api.models.User;
import com.ohhell.api.security.JwtUtil;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    private final UserDAO userDAO = new UserDAO();
    private final PlayerDAO playerDAO = new PlayerDAO();

    @POST
    @Path("/login")
    public Response login(LoginRequest request) {

        Optional<User> optUser = userDAO.findByEmail(request.getEmail());

        if (optUser.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Credenciales inválidas")
                    .build();
        }

        User user = optUser.get();

        // ⚠️ MVP: comparación directa
        if (!user.getPasswordHash().equals(request.getPassword())) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Credenciales inválidas")
                    .build();
        }

        String token = JwtUtil.generateToken(user.getId(), user.getEmail());

        return Response.ok(Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "token", token
        )).build();
    }

    @POST
    @Path("/register")
    public Response register(Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        String nickname = body.get("nickname");

        // ==================
        // VALIDACIONES
        // ==================
        if (email == null || email.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Email requerido")
                    .build();
        }

        if (password == null || password.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Contraseña requerida")
                    .build();
        }

        if (password.length() < 6) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("La contraseña debe tener al menos 6 caracteres")
                    .build();
        }

        if (nickname == null || nickname.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Nickname requerido")
                    .build();
        }

        if (nickname.length() < 3) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El nickname debe tener al menos 3 caracteres")
                    .build();
        }

        // ==================
        // VERIFICAR DUPLICADOS
        // ==================
        if (userDAO.findByEmail(email).isPresent()) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Email ya registrado")
                    .build();
        }

        User user = null;
        Player player = null;

        try {
            // ==================
            // 1. CREAR USER (primero)
            // ==================
            user = userDAO.create(email, password);
            System.out.println("✅ User creado: " + user.getId() + " - " + email);

            // ==================
            // 2. CREAR PLAYER (con manejo de nickname duplicado)
            // ==================
            player = createPlayerWithRetry(user.getId(), nickname);
            System.out.println("✅ Player creado: " + player.getNickname() + " para user: " + user.getId());

            // ==================
            // 3. GENERAR TOKEN
            // ==================
            String token = JwtUtil.generateToken(user.getId(), user.getEmail());

            return Response.ok(Map.of(
                    "id", user.getId(),
                    "email", user.getEmail(),
                    "nickname", player.getNickname(),
                    "token", token
            )).build();

        } catch (Exception e) {
            System.err.println("❌ ERROR en registro completo:");
            e.printStackTrace();

            // Si hubo error después de crear el User, intentar limpiar
            if (user != null && player == null) {
                System.err.println("⚠️ User creado pero Player falló. User ID: " + user.getId());
                // NOTA: No borramos el user porque podría reutilizarse
            }

            return Response.status(Response.Status.CONFLICT)
                    .entity(e.getMessage())
                    .build();
        }
    }

    // =========================
    // HELPER: Crear player con reintentos
    // =========================
    private Player createPlayerWithRetry(UUID userId, String requestedNickname) {
        int maxAttempts = 3;
        String nickname = requestedNickname;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                System.out.println("🔄 Intento " + attempt + " de crear Player: " + nickname);
                return playerDAO.create(userId, nickname);

            } catch (RuntimeException e) {
                System.err.println("⚠️ Intento " + attempt + " fallado: " + e.getMessage());

                if (e.getMessage().contains("players_nickname_key")) {
                    // Nickname duplicado, generar uno alternativo
                    if (attempt < maxAttempts) {
                        nickname = generateUniqueNickname(requestedNickname, attempt);
                        System.out.println("🔄 Nuevo nickname: " + nickname);
                    } else {
                        throw new RuntimeException("No se pudo crear un nickname único después de " + maxAttempts + " intentos");
                    }
                } else if (e.getMessage().contains("players_user_id_key")) {
                    // Ya existe player para este user (caso extraño)
                    System.out.println("⚠️ Player ya existe para este usuario, recuperando...");
                    return playerDAO.findByUserId(userId)
                            .orElseThrow(() -> new RuntimeException("Player ya existe pero no se pudo recuperar"));
                } else {
                    // Otro error, relanzar
                    throw e;
                }
            }
        }

        throw new RuntimeException("No se pudo crear el Player");
    }

    // =========================
    // HELPER: Generar nickname único
    // =========================
    private String generateUniqueNickname(String baseNickname, int attempt) {
        // Limitar longitud a 40 caracteres (límite de la BD)
        if (baseNickname.length() > 30) {
            baseNickname = baseNickname.substring(0, 30);
        }

        // Generar sufijo único
        String suffix = "_" + UUID.randomUUID().toString().substring(0, 4);

        return baseNickname + suffix;
    }
}