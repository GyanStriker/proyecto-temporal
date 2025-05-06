package Controladores;

import Modelos.OrdenTrabajoModelo;
import Modelos.ProgresoCarrosModelo;
import Modelos.ServicioModelo;
import Vistas.FacturasVista;
import Vistas.OrdenTrabajoVista;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.table.DefaultTableModel;

public class TallerControlador {
    private OrdenTrabajoVista vista;
    private OrdenTrabajoModelo modelo;
    private ServicioModelo servicioModelo;
    private String usuarioActual;
    private Timer timerCola;
    private Timer timerServicio;
    private Timer timerListo;
    private int progresoCola = 0;
    private int progresoServicio = 0;
    private int progresoListo = 0;
    private String[][] ordenesEnProceso;
    private int indiceOrdenActual = 0;
    private boolean procesoActivo = false;
    private ProgresoCarrosModelo progresoModelo;

    public TallerControlador(OrdenTrabajoVista vista, OrdenTrabajoModelo modelo, 
                           ServicioModelo servicioModelo, String usuarioActual) {
        this.vista = vista;
        this.modelo = modelo;
        this.servicioModelo = servicioModelo;
        this.usuarioActual = usuarioActual;
        this.progresoModelo = new ProgresoCarrosModelo();
        
        configurarListeners();
    }
    
    private void configurarListeners() {
        vista.BotonEnviarTallerOT.addActionListener(e -> iniciarProcesoTaller());
    }
    
    
    public void iniciarProcesoTaller() {
        // Verificar si hay un proceso activo mirando las progress bars
        boolean procesoEnCurso = vista.ColaProgressBar.getValue() > 0 || 
                              vista.EnServicioProgressBar.getValue() > 0 ||
                              vista.ListoProgressBar.getValue() > 0;

        if (procesoEnCurso) {
            JOptionPane.showMessageDialog(vista, "Ya hay un proceso de taller en curso", 
                                        "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel modeloTabla = (DefaultTableModel) vista.TablaOrdenTrabajo.getModel();
        String[][] ordenesUsuario = modelo.getOrdenesUsuario(usuarioActual);

        // Contar órdenes válidas en el modelo
        int ordenesValidas = 0;
        for (String[] orden : ordenesUsuario) {
            if (orden != null && orden[0] != null) {
                ordenesValidas++;
            }
        }

        if (modeloTabla.getRowCount() == 0 && ordenesValidas == 0) {
            JOptionPane.showMessageDialog(vista, "No hay órdenes para enviar al taller", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtener todas las órdenes válidas
        ordenesEnProceso = new String[ordenesValidas][4];
        int index = 0;
        for (String[] orden : ordenesUsuario) {
            if (orden != null && orden[0] != null) {
                ordenesEnProceso[index++] = orden;
            }
        }

        // Si hay órdenes en la tabla pero no en el modelo (caso raro)
        if (ordenesValidas == 0 && modeloTabla.getRowCount() > 0) {
            ordenesEnProceso = new String[modeloTabla.getRowCount()][4];
            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                ordenesEnProceso[i][0] = (String) modeloTabla.getValueAt(i, 0); // Placa
                ordenesEnProceso[i][1] = (String) modeloTabla.getValueAt(i, 1); // Marca
                ordenesEnProceso[i][2] = (String) modeloTabla.getValueAt(i, 2); // Modelo
                ordenesEnProceso[i][3] = (String) modeloTabla.getValueAt(i, 3); // Servicio
            }
        }

        // Limpiar tabla
        modeloTabla.setRowCount(0);

        // Iniciar proceso con la primera orden
        procesoActivo = true;
        indiceOrdenActual = 0;
        procesarSiguienteOrden();
    }
    
    private void procesarSiguienteOrden() {
        if (indiceOrdenActual >= ordenesEnProceso.length) {
            // Todas las órdenes procesadas
            procesoActivo = false;
            return;
        }
        
        String[] ordenActual = ordenesEnProceso[indiceOrdenActual];
        iniciarColaEspera(ordenActual);
    }
    
    
    private void iniciarColaEspera(String[] orden) {
        // Obtener nombre del cliente
        String nombreCliente = obtenerNombreCliente(usuarioActual);
        
        // Agregar a la cola
        progresoModelo.agregarCarroACola(orden[0], orden[1], orden[2], nombreCliente);
        
        vista.ColaPlacaLabel.setText(orden[0]);
        progresoCola = 0;
        vista.ColaProgressBar.setValue(0);
        
        // Cancelar timers anteriores si existen
        cancelarTimers();
        
        timerCola = new Timer();
        timerCola.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                progresoCola++;
                SwingUtilities.invokeLater(() -> {
                    vista.ColaProgressBar.setValue(progresoCola);
                });
                
                if (progresoCola >= 100) {
                    timerCola.cancel();
                    SwingUtilities.invokeLater(() -> {
                        vista.ColaPlacaLabel.setText("");
                        // Remover de la cola cuando pasa al siguiente estado
                        progresoModelo.removerCarroDeCola(orden[0]);
                        iniciarServicio(orden);
                    });
                }
            }
        }, 0, 110);
    }
    
    private String obtenerNombreCliente(String usuario) {
        // Implementar lógica para obtener nombre del cliente según el usuario
        // Esto dependerá de cómo tengas estructurado tu modelo de clientes
        return "Cliente"; // Ejemplo simplificado
    }
    
    private void iniciarServicio(String[] orden) {
        vista.EnServicioPlacaLabel.setText(orden[0]);
        progresoServicio = 0;
        vista.EnServicioProgressBar.setValue(0);
        
        timerServicio = new Timer();
        timerServicio.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                progresoServicio++;
                SwingUtilities.invokeLater(() -> {
                    vista.EnServicioProgressBar.setValue(progresoServicio);
                });
                
                if (progresoServicio >= 100) {
                    timerServicio.cancel();
                    SwingUtilities.invokeLater(() -> {
                        vista.EnServicioPlacaLabel.setText("");
                        iniciarListo(orden);
                    });
                }
            }
        }, 0, 50); // 5 segundos total (100 incrementos * 50ms)
    }
    
    private void iniciarListo(String[] orden) {
        vista.ListoPlacaLabel.setText(orden[0]);
        progresoListo = 0;
        vista.ListoProgressBar.setValue(0);
        
        timerListo = new Timer();
        timerListo.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                progresoListo++;
                SwingUtilities.invokeLater(() -> {
                    vista.ListoProgressBar.setValue(progresoListo);
                });
                
                if (progresoListo >= 100) {
                    timerListo.cancel();
                    SwingUtilities.invokeLater(() -> {
                        vista.ListoPlacaLabel.setText("");
                        finalizarProceso(orden);
                    });
                }
            }
        }, 0, 20); // 2 segundos total (100 incrementos * 20ms)
    }
    
    private void cancelarTimers() {
        if (timerCola != null) timerCola.cancel();
        if (timerServicio != null) timerServicio.cancel();
        if (timerListo != null) timerListo.cancel();
    }
    
    
    private void finalizarProceso(String[] orden) {
        // Generar factura por cada servicio
        String carroInfo = orden[0] + "-" + orden[1] + "-" + orden[2];
        String servicio = orden[3];

        // Buscar el precio del servicio
        double precioTotal = 0;
        for (int i = 0; i < servicioModelo.getTamaño(); i++) {
            String[] servicioInfo = servicioModelo.getServicio(i);
            if (servicioInfo != null && servicioInfo[1].equals(servicio)) {
                precioTotal = Double.parseDouble(servicioInfo[6]);
                break;
            }
        }

        // Agregar factura
        modelo.agregarFactura(usuarioActual, carroInfo, servicio, String.format("%.2f", precioTotal));

        // Pasar a la siguiente orden
        indiceOrdenActual++;

        if (indiceOrdenActual >= ordenesEnProceso.length) {
            // Todas las órdenes procesadas
            procesoActivo = false;

            // Reiniciar progress bars
            SwingUtilities.invokeLater(() -> {
                vista.ColaProgressBar.setValue(0);
                vista.EnServicioProgressBar.setValue(0);
                vista.ListoProgressBar.setValue(0);
            });
        } else {
            procesarSiguienteOrden();
        }
    }
}
