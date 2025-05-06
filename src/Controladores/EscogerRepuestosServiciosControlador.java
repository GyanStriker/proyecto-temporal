package Controladores;

import Modelos.RepuestoModelo;
import Vistas.EscogerRepuestosServiciosVista;
import javax.swing.*;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

public class EscogerRepuestosServiciosControlador {
    private EscogerRepuestosServiciosVista vista;
    private RepuestoModelo modelo;
    private String marcaServicio;
    private String modeloServicio;
    private String repuestosSeleccionados;
    private JDialog dialog;
    private String repuestosActuales;
    
    public EscogerRepuestosServiciosControlador(EscogerRepuestosServiciosVista vista, 
            RepuestoModelo modelo, String marcaServicio, String modeloServicio, 
            boolean modoEdicion, JDialog dialog, String repuestosActuales) {
        this.vista = vista;
        this.modelo = modelo;
        this.marcaServicio = marcaServicio;
        this.modeloServicio = modeloServicio;
        this.repuestosSeleccionados = "";
        this.dialog = dialog;
        this.repuestosActuales = repuestosActuales;

        if (modoEdicion) {
            vista.AgregarRepuestoServicio.setText("Modificar Repuestos del Servicio");
        }

        cargarRepuestos();
        configurarListeners();

        vista.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    }
    
    private void cargarRepuestos() {
        DefaultListModel<JCheckBox> model = new DefaultListModel<>();

        if (modelo != null && modelo.getTamaño() > 0) {
            // Convertir repuestos actuales a un array para comparación
            String[] repuestosArray = repuestosActuales.split(";");
            
            for (int i = 0; i < modelo.getTamaño(); i++) {
                String[] repuesto = modelo.getRepuesto(i);
                if (repuesto != null && repuesto.length >= 6) {
                    // Crear el texto del checkbox
                    String textoCheckbox = repuesto[1] + " - " + repuesto[2] + " - " + repuesto[3] + " - $" + repuesto[5];
                    JCheckBox checkBox = new JCheckBox(textoCheckbox);
                    
                    // Verificar si este repuesto está en los repuestos actuales del servicio
                    if (repuestosActuales != null && !repuestosActuales.isEmpty()) {
                        for (String repuestoActual : repuestosArray) {
                            String[] partes = repuestoActual.split("-");
                            if (partes.length >= 1 && partes[0].equals(repuesto[1])) {
                                checkBox.setSelected(true);
                                break;
                            }
                        }
                    }
                    
                    model.addElement(checkBox);
                }
            }
        }

        vista.ListaEscogerRepuestos.setModel(model);
        vista.AgregarRepuestoServicio.setEnabled(model.getSize() > 0);
    }
    
    private void agregarRepuestos() {
        DefaultListModel<JCheckBox> model = (DefaultListModel<JCheckBox>) vista.ListaEscogerRepuestos.getModel();
        StringBuilder repuestosBuilder = new StringBuilder();
        boolean alMenosUnoSeleccionado = false;
        boolean errorEnSeleccion = false;

        for (int i = 0; i < model.getSize(); i++) {
            JCheckBox checkBox = model.getElementAt(i);
            if (checkBox.isSelected()) {
                alMenosUnoSeleccionado = true;
                String[] repuesto = modelo.getRepuesto(i);

                if (repuesto == null || repuesto.length < 6) continue;

                if (!marcaServicio.isEmpty() && !modeloServicio.isEmpty() && 
                    (!repuesto[2].equalsIgnoreCase(marcaServicio) || !repuesto[3].equalsIgnoreCase(modeloServicio))) {
                    JOptionPane.showMessageDialog(vista, 
                        "El repuesto " + repuesto[1] + " no coincide con la marca/modelo del servicio", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    errorEnSeleccion = true;
                    break;
                }

                if (repuestosBuilder.length() > 0) {
                    repuestosBuilder.append(";");
                }
                repuestosBuilder.append(repuesto[1]).append("-")
                             .append(repuesto[2]).append("-")
                             .append(repuesto[3]).append("-")
                             .append(repuesto[4]).append("-")
                             .append(repuesto[5]);
            }
        }

        if (!alMenosUnoSeleccionado && !errorEnSeleccion) {
            JOptionPane.showMessageDialog(vista, "Seleccione al menos un repuesto", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!errorEnSeleccion) {
            repuestosSeleccionados = repuestosBuilder.toString();
            dialog.dispose();
        }
    }

    private void configurarListeners() {
        vista.AgregarRepuestoServicio.addActionListener(e -> agregarRepuestos());
        
        vista.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                repuestosSeleccionados = "";
            }
        });
    }
    
    public String getRepuestosSeleccionados() {
        return repuestosSeleccionados;
    }
}