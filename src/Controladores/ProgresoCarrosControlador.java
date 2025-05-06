package Controladores;

import Modelos.ProgresoCarrosModelo;
import Vistas.ProgresoCarrosVista;
import javax.swing.table.DefaultTableModel;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.SwingUtilities;

public class ProgresoCarrosControlador {
    private ProgresoCarrosVista vista;
    private ProgresoCarrosModelo modelo;
    private Timer timerActualizacion;
    
    public ProgresoCarrosControlador(ProgresoCarrosVista vista, ProgresoCarrosModelo modelo) {
        this.vista = vista;
        this.modelo = modelo;
        
        configurarVista();
        iniciarActualizacionAutomatica();
        
        vista.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                detenerActualizacion();
            }
        });
    }
    
    private void configurarVista() {
        // Configurar modelo de tabla para carros en espera
        DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"Orden", "Automovil", "Cliente"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        vista.TablaCarrosEspera.setModel(modeloTabla);
        
        // Actualizar datos iniciales
        actualizarTablaEspera();
    }
    
    private void actualizarTablaEspera() {
        DefaultTableModel modeloTabla = (DefaultTableModel) vista.TablaCarrosEspera.getModel();
        modeloTabla.setRowCount(0);
        
        String[][] carrosEnCola = modelo.getCarrosEnCola();
        for (String[] carro : carrosEnCola) {
            if (carro != null) {
                String orden = carro[0];
                String automovil = carro[1] + " - " + carro[2] + " " + carro[3];
                String cliente = carro[4];
                modeloTabla.addRow(new Object[]{orden, automovil, cliente});
            }
        }
    }
    
    private void iniciarActualizacionAutomatica() {
        timerActualizacion = new Timer();
        timerActualizacion.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    actualizarTablaEspera();
                });
            }
        }, 0, 1000); // Actualizar cada segundo
    }
    
    private void detenerActualizacion() {
        if (timerActualizacion != null) {
            timerActualizacion.cancel();
        }
    }
}