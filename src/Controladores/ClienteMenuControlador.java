package Controladores;

import Modelos.CarroModelo;
import Modelos.ClienteModelo;
import Modelos.OrdenTrabajoModelo;
import Modelos.RepuestoModelo;
import Modelos.ServicioModelo;
import Vistas.CarroVista;
import Vistas.ClienteMenuVista;
import Vistas.OrdenTrabajoVista;
import Vistas.FacturasVista;
import javax.swing.JOptionPane;

public class ClienteMenuControlador {
    private ClienteMenuVista vista;
    private ClienteModelo modelo;
    private int indiceCliente;
    private CarroModelo carroModelo;
    private ServicioModelo servicioModelo;
    private OrdenTrabajoModelo ordenTrabajoModelo;
    
    public ClienteMenuControlador(ClienteMenuVista vista, ClienteModelo modelo, int indiceCliente) {
        this.vista = vista;
        this.modelo = modelo;
        this.indiceCliente = indiceCliente;
        this.carroModelo = new CarroModelo();
        this.servicioModelo = new ServicioModelo(new RepuestoModelo());
        this.ordenTrabajoModelo = new OrdenTrabajoModelo();
        
        // Obtener nombre del cliente
        String nombreCliente = modelo.getCliente(indiceCliente)[1];
        vista.BienvenidoLabel.setText("¡Bienvenido " + nombreCliente + "!");
        
        // Configurar listeners
        vista.ClienteCerrarSesion.addActionListener(e -> cerrarSesion());
        vista.BotonRegistroAutomovil.addActionListener(e -> abrirRegistroAutomovil());
        vista.BotonVerProgreso.addActionListener(e -> abrirOrdenTrabajo());
        vista.BotonFacturas.addActionListener(e -> abrirFacturas());
    }
    
    /*private void abrirFacturas() {
        String usuario = modelo.getCliente(indiceCliente)[2];
        FacturasVista facturasVista = new FacturasVista();
        new FacturasControlador(facturasVista, ordenTrabajoModelo, usuario);
        facturasVista.setVisible(true);
    }*/
    
    private void abrirFacturas() {
        String usuario = modelo.getCliente(indiceCliente)[2];
        FacturasVista facturasVista = new FacturasVista();
        new FacturasControlador(facturasVista, ordenTrabajoModelo, usuario);
        facturasVista.setVisible(true);
    }
    
    private void cerrarSesion() {
        vista.dispose();
    }
    
    private void abrirRegistroAutomovil() {
        String usuario = modelo.getCliente(indiceCliente)[2];
        CarroVista carroVista = new CarroVista();
        new CarroControlador(carroVista, carroModelo, usuario);
        carroVista.setVisible(true);
    }
    
    private void abrirOrdenTrabajo() {
        String usuario = modelo.getCliente(indiceCliente)[2];
        
        // Verificar que el cliente tenga vehículos registrados
        String[][] carrosUsuario = carroModelo.getCarrosUsuario(usuario);
        if (carrosUsuario == null || carrosUsuario.length == 0 || carrosUsuario[0][0] == null) {
            JOptionPane.showMessageDialog(vista, 
                "No tienes vehículos registrados. Por favor registra un vehículo primero.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        OrdenTrabajoVista ordenVista = new OrdenTrabajoVista();
        new OrdenTrabajoControlador(ordenVista, modelo, carroModelo, servicioModelo, usuario);
        ordenVista.setVisible(true);
    }
}