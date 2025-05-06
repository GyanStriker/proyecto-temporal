package Controladores;

import Modelos.CarroModelo;
import Modelos.ClienteModelo;
import Modelos.ProgresoCarrosModelo;
import Modelos.RepuestoModelo;
import Modelos.ServicioModelo;
import Vistas.ClienteOpcionVista;
import Vistas.LoginVista;
import Vistas.MenuVista;
import Vistas.ProgresoCarrosVista;
import Vistas.RepuestoVista;
import Vistas.ServicioVista;
import javax.swing.JOptionPane;

public class MenuControlador {
    private MenuVista vista;
    private RepuestoModelo repuestoModelo;
    private ServicioModelo servicioModelo;
    private ClienteModelo clienteModelo;
    
    public MenuControlador(MenuVista vista) {
        this.vista = vista;
        this.repuestoModelo = new RepuestoModelo();
        this.servicioModelo = new ServicioModelo(repuestoModelo);
        this.clienteModelo = new ClienteModelo();

        // Configurar listeners
        this.vista.BotonCerrarSesion.addActionListener(e -> cerrarSesion());
        this.vista.BotonRepuesto.addActionListener(e -> abrirRepuestos());
        this.vista.BotonServicios.addActionListener(e -> abrirServicios());
        this.vista.BotonClienteAuto.addActionListener(e -> abrirOpcionesCliente());
        this.vista.BotonProgreso.addActionListener(e -> abrirProgresoCarros());
        this.vista.CleanReset.addActionListener(e -> limpiarTodo());
    }

    private void abrirOpcionesCliente() {
        ClienteOpcionVista opcionVista = new ClienteOpcionVista();
        new ClienteOpcionControlador(opcionVista, clienteModelo);
        opcionVista.setVisible(true);
    }
    
    private void abrirProgresoCarros() {
        ProgresoCarrosVista progresoVista = new ProgresoCarrosVista();
        ProgresoCarrosModelo progresoModelo = new ProgresoCarrosModelo();
        new ProgresoCarrosControlador(progresoVista, progresoModelo);
        progresoVista.setVisible(true);
    }
    
    private void cerrarSesion() {
        // Guardar datos antes de cerrar
        repuestoModelo.guardarEnArchivo();
        servicioModelo.guardarEnArchivo();
        
        vista.dispose();
        new LoginVista().setVisible(true);
    }
    
    private void abrirRepuestos() {
        RepuestoVista repuestoVista = new RepuestoVista(repuestoModelo); // Pasar el modelo
        new RepuestoControlador(repuestoVista, repuestoModelo);
        repuestoVista.setVisible(true);
    }
    
    private void abrirServicios() {
        if (repuestoModelo.getTamaño() == 0) {
            JOptionPane.showMessageDialog(vista, "No hay Repuestos en el Programa", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        ServicioVista serviciosVista = new ServicioVista();
        new ServicioControlador(serviciosVista, servicioModelo, repuestoModelo);
        serviciosVista.setVisible(true);
    }
    
// En MenuControlador.java
    private void limpiarTodo() {
        int confirmacion = JOptionPane.showConfirmDialog(vista, 
            "¿Está seguro que desea eliminar TODOS los repuestos, servicios, clientes y carros?\nEsta acción no se puede deshacer.", 
            "Confirmar limpieza", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            repuestoModelo.limpiarDatos();
            servicioModelo.limpiarDatos();
            clienteModelo.limpiarDatos();

            // Limpiar también los carros
            CarroModelo carroModelo = new CarroModelo();
            carroModelo.limpiarDatos();

            JOptionPane.showMessageDialog(vista, "Todos los datos han sido eliminados", 
                "Limpieza completada", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}