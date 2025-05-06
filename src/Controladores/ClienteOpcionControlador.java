package Controladores;

import Vistas.ClienteOpcionVista;
import Vistas.ClienteVista;
import Vistas.ClienteLoginVista;
import Modelos.ClienteModelo;

public class ClienteOpcionControlador {
    private ClienteOpcionVista vista;
    private ClienteModelo modelo;
    
    public ClienteOpcionControlador(ClienteOpcionVista vista, ClienteModelo modelo) {
        this.vista = vista;
        this.modelo = modelo;
        
        vista.BotonRegistrarCliente.addActionListener(e -> abrirRegistroCliente());
        vista.BotonLoginCliente.addActionListener(e -> abrirLoginCliente());
    }
    
    private void abrirRegistroCliente() {
        ClienteVista clienteVista = new ClienteVista();
        new ClienteControlador(clienteVista, modelo);
        clienteVista.setVisible(true);
    }
    
    private void abrirLoginCliente() {
        ClienteLoginVista loginVista = new ClienteLoginVista();
        new ClienteLoginControlador(loginVista, modelo);
        loginVista.setVisible(true);
    }
}