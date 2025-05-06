package Controladores;

import Modelos.LoginModelo;
import Vistas.LoginVista;
import Vistas.MenuVista;
import javax.swing.JOptionPane;

public class LoginControlador {
    private LoginVista vista;
    private LoginModelo modelo;
    
    public LoginControlador(LoginVista vista, LoginModelo modelo) {
        this.vista = vista;
        this.modelo = modelo;
        
        // Agregar acción al botón de ingresar
        this.vista.jButton1.addActionListener(e -> autenticarUsuario());
    }
    
    private void autenticarUsuario() {
        String usuario = vista.TextUsuario.getText();
        String contrasena = new String(vista.TextPassword.getPassword());
        
        if (modelo.verificarCredenciales(usuario, contrasena)) {
            // Credenciales correctas
            vista.dispose(); // Cierra la ventana de login
            MenuVista menu = new MenuVista();
            menu.setVisible(true);
        } else {
            // Credenciales incorrectas
            JOptionPane.showMessageDialog(vista, "Credenciales incorrectos", "Error", JOptionPane.ERROR_MESSAGE);
            // Limpiar campos
            vista.TextUsuario.setText("");
            vista.TextPassword.setText("");
            // Poner foco en el campo de usuario
            vista.TextUsuario.requestFocus();
        }
    }
}
