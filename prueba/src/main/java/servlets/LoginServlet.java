package servlets;

import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Aquí puedes validar el usuario/contraseña si lo deseas
        // String usuario = request.getParameter("usuario");
        // String clave = request.getParameter("clave");

        // Por ahora redirige directamente a home.html
        response.sendRedirect(request.getContextPath() + "/home.html");
    }
}
