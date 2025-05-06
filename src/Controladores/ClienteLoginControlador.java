package Controladores;

import Vistas.ClienteLoginVista;
import Vistas.ClienteMenuVista;
import Modelos.ClienteModelo;
import javax.swing.JOptionPane;

public class ClienteLoginControlador {
    private ClienteLoginVista vista;
    private ClienteModelo modelo;
    
    public ClienteLoginControlador(ClienteLoginVista vista, ClienteModelo modelo) {
        this.vista = vista;
        this.modelo = modelo;
        
        vista.BotonClienteIngresarLogin.addActionListener(e -> validarLogin());
    }
    
    private void validarLogin() {
        String usuario = vista.ClienteTextUsuario.getText();
        String password = new String(vista.ClienteTextPassword.getPassword());
        
        if (modelo.validarCredenciales(usuario, password)) {
            int indiceCliente = modelo.buscarClientePorUsuario(usuario);
            if (indiceCliente >= 0) {
                vista.dispose();
                ClienteMenuVista menuVista = new ClienteMenuVista();
                new ClienteMenuControlador(menuVista, modelo, indiceCliente);
                menuVista.setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(vista, "Credenciales incorrectas", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}