package Modelos;

public class LoginModelo {
    private final String USUARIO_CORRECTO = "Administrador";
    private final String CONTRASENA_CORRECTA = "123";
    
    public boolean verificarCredenciales(String usuario, String contrasena) {
        return USUARIO_CORRECTO.equals(usuario) && CONTRASENA_CORRECTA.equals(contrasena);
    }
}
