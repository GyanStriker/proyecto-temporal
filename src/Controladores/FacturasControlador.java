package Controladores;

import Modelos.OrdenTrabajoModelo;
import Vistas.FacturasVista;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class FacturasControlador {
    private FacturasVista vista;
    private OrdenTrabajoModelo modelo;
    private String usuarioActual;
    
    public FacturasControlador(FacturasVista vista, OrdenTrabajoModelo modelo, String usuarioActual) {
        this.vista = vista;
        this.modelo = modelo;
        this.usuarioActual = usuarioActual;
        
        configurarVista();
        cargarFacturas();
    }
    
    private void configurarVista() {
        DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"Carro", "Servicios", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        vista.FacturaTabla.setModel(modeloTabla);
        
        vista.BotonPagarFactura.addActionListener(e -> pagarFacturas());
    }
    
    private void cargarFacturas() {
        DefaultTableModel modeloTabla = (DefaultTableModel) vista.FacturaTabla.getModel();
        modeloTabla.setRowCount(0);
        
        String[][] facturasUsuario = modelo.getFacturasUsuario(usuarioActual);
        for (String[] factura : facturasUsuario) {
            if (factura != null && factura[0] != null) {
                modeloTabla.addRow(factura);
            }
        }
    }
    
    private void pagarFacturas() {
        DefaultTableModel modeloTabla = (DefaultTableModel) vista.FacturaTabla.getModel();

        if (modeloTabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(vista, "No hay facturas para pagar", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Confirmar antes de eliminar
        int confirmacion = JOptionPane.showConfirmDialog(
            vista,
            "¿Está seguro que desea pagar todas las facturas? Esta acción no se puede deshacer.",
            "Confirmar pago",
            JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            // 1. Limpiar las facturas en el modelo (persistente)
            modelo.limpiarFacturasUsuario(usuarioActual);

            // 2. Limpiar la tabla visual
            modeloTabla.setRowCount(0);

            JOptionPane.showMessageDialog(vista, 
                "Todas las facturas han sido pagadas",
                "Éxito", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
}