package Controladores;

import Modelos.RepuestoModelo;
import Modelos.ServicioModelo;
import Vistas.EscogerRepuestosServiciosVista;
import Vistas.ServicioVista;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

public class ServicioControlador {
    private ServicioVista vista;
    private ServicioModelo modelo;
    private RepuestoModelo repuestoModelo;
    private DefaultTableModel modeloTabla;
    private boolean enModoEdicion = false;
    private int filaEditando = -1;
    private volatile boolean enProceso = false; // Variable para controlar ejecuciones duplicadas
    
    public ServicioControlador(ServicioVista vista, ServicioModelo modelo, RepuestoModelo repuestoModelo) {
        this.vista = vista;
        this.modelo = modelo;
        this.repuestoModelo = repuestoModelo;
        
        modeloTabla = (DefaultTableModel) vista.TablaServicio.getModel();
        configurarFiltros();
        
        // Configuración segura del listener
        configurarListenerUnico();
        
        modelo.actualizarTabla(modeloTabla);
        
        vista.AgregarServicio.setEnabled(false);
        
        vista.TablaServicio.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                validarBotonesTabla();
            }
        });
        
        vista.ModificarServicio.addActionListener(e -> iniciarModificacion());
        vista.EliminarServicio.addActionListener(e -> eliminarServicios());
        vista.ServicioCargaMasiva.addActionListener(e -> cargarServiciosMasivos());
        
        vista.ModificarServicio.setEnabled(false);
        vista.EliminarServicio.setEnabled(false);
        
        vista.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                modelo.guardarEnArchivo();
            }
        });
    }

    private void configurarListenerUnico() {
        // Eliminar todos los listeners existentes primero
        ActionListener[] listeners = vista.AgregarServicio.getActionListeners();
        
        for (ActionListener listener : listeners) {
            vista.AgregarServicio.removeActionListener(listener);
        }
        
        // Agregar nuestro listener seguro
        vista.AgregarServicio.addActionListener(e -> {
            agregarServicioManual();
        });
      
    }

    private void agregarServicioManual() {
        // Deshabilitar el botón inmediatamente para prevenir dobles clics
        vista.AgregarServicio.setEnabled(false);
        
        if (enProceso) {
            return;
        }
        
        try {
            enProceso = true;
            
            String repuestos = abrirVentanaRepuestos(false);
            if (repuestos == null) {
                return;
            }

            // Obtener datos del formulario
            String nombre = vista.TextServicioNombre.getText().trim();
            String marca = vista.TextServicioMarca.getText().trim();
            String modeloStr = vista.TextServicioModelo.getText().trim();
            double manoObra = Double.parseDouble(vista.TextServicioManoObra.getText());

            // Validar que no sea un servicio duplicado
            if (servicioYaExiste(nombre, marca, modeloStr)) {
                JOptionPane.showMessageDialog(vista, 
                    "Ya existe un servicio con estos datos", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Agregar el servicio al modelo
            modelo.agregarServicio(nombre, marca, modeloStr, repuestos, manoObra);
            modelo.actualizarTabla(modeloTabla);

            // Limpiar campos
            SwingUtilities.invokeLater(() -> {
                vista.TextServicioNombre.setText("");
                vista.TextServicioMarca.setText("");
                vista.TextServicioModelo.setText("");
                vista.TextServicioManoObra.setText("");
                vista.AgregarServicio.setEnabled(false);
            });
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista, 
                "Error en los valores numéricos", 
                "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            enProceso = false;
            
            // Volver a habilitar el botón después de un breve retraso
            SwingUtilities.invokeLater(() -> {
                if (!enModoEdicion) {
                    vista.AgregarServicio.setEnabled(true);
                }
            });
        }
    }

    private boolean servicioYaExiste(String nombre, String marca, String modeloStr) {
        for (int i = 0; i < modelo.getTamaño(); i++) {
            String[] servicio = modelo.getServicio(i);
            if (servicio != null && servicio.length > 3 &&
                servicio[1].equalsIgnoreCase(nombre) &&
                servicio[2].equalsIgnoreCase(marca) &&
                servicio[3].equalsIgnoreCase(modeloStr)) {
                return true;
            }
        }
        return false;
    }
    
    private void validarBotonesTabla() {
        int filaSeleccionada = vista.TablaServicio.getSelectedRow();
        boolean filaValida = filaSeleccionada >= 0 && filaSeleccionada < modelo.getTamaño();
        
        if (!enModoEdicion) {
            vista.ModificarServicio.setEnabled(filaValida && !modelo.esDiagnostico(filaSeleccionada));
            vista.EliminarServicio.setEnabled(filaValida && !modelo.esDiagnostico(filaSeleccionada));
        } else {
            vista.ModificarServicio.setEnabled(false);
            vista.EliminarServicio.setEnabled(false);
        }
    }
    
    private void iniciarModificacion() {
        filaEditando = vista.TablaServicio.getSelectedRow();
        if (filaEditando >= 0) {
            enModoEdicion = true;
            
            vista.LabelServicio.setText("[Modifica el Servicio]");
            vista.LabelServicio.setForeground(Color.BLUE);
            vista.AgregarServicio.setText("MODIFICAR");
            
            vista.ModificarServicio.setEnabled(false);
            vista.EliminarServicio.setEnabled(false);
            vista.ServicioCargaMasiva.setEnabled(false);
            
            String[] servicio = modelo.getServicio(filaEditando);
            vista.TextServicioNombre.setText(servicio[1]);
            vista.TextServicioMarca.setText(servicio[2]);
            vista.TextServicioModelo.setText(servicio[3]);
            vista.TextServicioManoObra.setText(servicio[5]);
            
            for (ActionListener al : vista.AgregarServicio.getActionListeners()) {
                vista.AgregarServicio.removeActionListener(al);
            }
            vista.AgregarServicio.addActionListener(e -> confirmarModificacion());
        }
    }
    
    private void confirmarModificacion() {
        String repuestos = abrirVentanaRepuestos(true);

        // Si repuestos es null, significa que hubo un error o se canceló la selección
        if (repuestos == null) {
            return; // No hacer nada, no actualizar la tabla
        }

        try {
            String nombre = vista.TextServicioNombre.getText();
            String marca = vista.TextServicioMarca.getText();
            String modeloStr = vista.TextServicioModelo.getText();
            double manoObra = Double.parseDouble(vista.TextServicioManoObra.getText());

            // Solo si todos los datos son válidos, modificamos el servicio
            modelo.modificarServicio(filaEditando, nombre, marca, modeloStr, repuestos, manoObra);
            modelo.actualizarTabla(modeloTabla); // Actualizar la tabla solo aquí

            terminarModoEdicion();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista, "Error en los valores numéricos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void terminarModoEdicion() {
        enModoEdicion = false;
        filaEditando = -1;
        
        vista.LabelServicio.setText("Añade un Servicio");
        vista.LabelServicio.setForeground(vista.jLabel2.getForeground());
        vista.AgregarServicio.setText("AGREGAR");
        vista.ServicioCargaMasiva.setEnabled(true);
        
        vista.TextServicioNombre.setText("");
        vista.TextServicioMarca.setText("");
        vista.TextServicioModelo.setText("");
        vista.TextServicioManoObra.setText("");
        
        for (ActionListener al : vista.AgregarServicio.getActionListeners()) {
            vista.AgregarServicio.removeActionListener(al);
        }
        vista.AgregarServicio.addActionListener(e -> agregarServicioManual());
        
        validarBotonesTabla();
    }
    
    private void eliminarServicios() {
        int filaSeleccionada = vista.TablaServicio.getSelectedRow();
        if (filaSeleccionada >= 0 && !modelo.esDiagnostico(filaSeleccionada)) {
            modelo.eliminarServicio(filaSeleccionada);
            modelo.actualizarTabla(modeloTabla);
            vista.EliminarServicio.setEnabled(false);
        }
    }
    
    private void validarCampos() {
        boolean camposCompletos = !vista.TextServicioNombre.getText().isEmpty() &&
                                (!vista.TextServicioMarca.getText().isEmpty() || 
                                 vista.TextServicioNombre.getText().equals("Diagnóstico")) &&
                                (!vista.TextServicioModelo.getText().isEmpty() || 
                                 vista.TextServicioNombre.getText().equals("Diagnóstico")) &&
                                !vista.TextServicioManoObra.getText().isEmpty();
        
        if (!camposCompletos) {
            vista.AgregarServicio.setEnabled(false);
            return;
        }
        
        try {
            double manoObra = Double.parseDouble(vista.TextServicioManoObra.getText());
            vista.AgregarServicio.setEnabled(manoObra >= 0);
        } catch (NumberFormatException e) {
            vista.AgregarServicio.setEnabled(false);
        }
    }
    
    private String abrirVentanaRepuestos(boolean modoEdicion) {
        JDialog dialog = new JDialog(vista, "Seleccionar Repuestos", true);
        EscogerRepuestosServiciosVista ventanaRepuestos = new EscogerRepuestosServiciosVista();

        // Obtener los repuestos actuales si estamos en modo edición
        String repuestosActuales = "";
        if (modoEdicion && filaEditando >= 0) {
            String[] servicio = modelo.getServicio(filaEditando);
            if (servicio != null && servicio.length > 4) {
                repuestosActuales = servicio[4];
            }
        }

        EscogerRepuestosServiciosControlador controlador = new EscogerRepuestosServiciosControlador(
            ventanaRepuestos, repuestoModelo, 
            vista.TextServicioMarca.getText(), 
            vista.TextServicioModelo.getText(), 
            modoEdicion,
            dialog,
            repuestosActuales  // Pasar los repuestos actuales
        );

        dialog.setContentPane(ventanaRepuestos.getContentPane());
        dialog.pack();
        dialog.setLocationRelativeTo(vista);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);

        String repuestos = controlador.getRepuestosSeleccionados();
        return (repuestos == null || repuestos.isEmpty()) ? null : repuestos;
    }
    
    private void configurarFiltros() {
        ((PlainDocument) vista.TextServicioManoObra.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(DocumentFilter.FilterBypass fb, int offset, String text, AttributeSet attr) 
                    throws BadLocationException {
                String current = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = current.substring(0, offset) + text + current.substring(offset);
                
                if (newText.matches("^\\d*\\.?\\d{0,2}$") && 
                    newText.chars().filter(ch -> ch == '.').count() <= 1) {
                    super.insertString(fb, offset, text, attr);
                    validarCampos();
                }
            }
            
            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) 
                    throws BadLocationException {
                String current = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = current.substring(0, offset) + text + current.substring(offset + length);
                
                if (newText.matches("^\\d*\\.?\\d{0,2}$") && 
                    newText.chars().filter(ch -> ch == '.').count() <= 1) {
                    super.replace(fb, offset, length, text, attrs);
                    validarCampos();
                }
            }
        });
        
        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validarCampos();
            }
        };
        
        vista.TextServicioNombre.addKeyListener(keyAdapter);
        vista.TextServicioMarca.addKeyListener(keyAdapter);
        vista.TextServicioModelo.addKeyListener(keyAdapter);
        vista.TextServicioManoObra.addKeyListener(keyAdapter);
    }
    
    private void cargarServiciosMasivos() {
        JFileChooser fileChooser = new JFileChooser();
        int resultado = fileChooser.showOpenDialog(vista);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            if (!modelo.cargarServiciosDesdeArchivo(archivo, modeloTabla, repuestoModelo)) {
                JOptionPane.showMessageDialog(vista, "Error en el archivo, revise su contenido", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}