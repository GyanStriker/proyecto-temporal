package Controladores;

import Modelos.RepuestoModelo;
import Vistas.RepuestoVista;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.SwingUtilities;

public class RepuestoControlador {
    private RepuestoVista vista;
    private RepuestoModelo modelo;
    private DefaultTableModel modeloTabla;
    private boolean enModoEdicion = false;
    private int filaEditando = -1;

    public RepuestoControlador(RepuestoVista vista, RepuestoModelo modelo) {
        this.vista = vista;
        this.modelo = modelo;
        this.modeloTabla = (DefaultTableModel) vista.TablaRepuestos.getModel();
        actualizarTabla();
        
        // Configurar modelo de tabla
        modeloTabla = (DefaultTableModel) vista.TablaRepuestos.getModel();
            // Configurar filtros y validación
        
        configurarFiltros();
        
        // Configurar listeners
        vista.AgregarRepuesto.addActionListener(e -> agregarRepuestoManual());
        vista.RepuestoCargaMasiva.addActionListener(e -> cargarRepuestosMasivos());
        
        // Deshabilitar botón agregar inicialmente
        vista.AgregarRepuesto.setEnabled(false);
        
        vista.TablaRepuestos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                validarBotonesTabla();
            }
        });
        
        // Botón Modificar
        vista.ModificarRepuesto.addActionListener(e -> iniciarModificacion());
        
        // Botón Eliminar
        vista.EliminarRepuesto.addActionListener(e -> eliminarRepuestos());
        
        // Deshabilitar botones inicialmente
        vista.ModificarRepuesto.setEnabled(false);
        vista.EliminarRepuesto.setEnabled(false);
        
        vista.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                modelo.guardarEnArchivo();
            }
        });
        
    }
    
    private void validarBotonesTabla() {
        int filaSeleccionada = vista.TablaRepuestos.getSelectedRow();
        boolean filaValida = filaSeleccionada >= 0 && filaSeleccionada < modelo.getTamaño();

        if (!enModoEdicion) {
            vista.ModificarRepuesto.setEnabled(filaValida);
            vista.EliminarRepuesto.setEnabled(filaValida); // <-- Solo habilitar si no estamos editando
        } else {
            vista.ModificarRepuesto.setEnabled(false);
            vista.EliminarRepuesto.setEnabled(false); // <-- Asegurar que esté deshabilitado durante edición
        }
    }
    
    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        for (int i = 0; i < modelo.getTamaño(); i++) {
            String[] repuesto = modelo.getRepuesto(i);
            if (repuesto != null) {
                modeloTabla.addRow(repuesto);
            }
        }
    }
    
    private void iniciarModificacion() {
        filaEditando = vista.TablaRepuestos.getSelectedRow();
        if (filaEditando >= 0) {
            enModoEdicion = true;

            // Cambiar interfaz a modo edición
            vista.jLabel7.setText("[Modifica el Repuesto]");
            vista.jLabel7.setForeground(Color.BLUE);
            vista.AgregarRepuesto.setText("MODIFICAR");

            // Deshabilitar botones que no deben usarse durante edición
            vista.ModificarRepuesto.setEnabled(false);
            vista.EliminarRepuesto.setEnabled(false); // <-- Nueva línea
            vista.RepuestoCargaMasiva.setEnabled(false); // También deshabilitar carga masiva

            // Llenar campos con datos del repuesto seleccionado
            String[] repuesto = modelo.getRepuesto(filaEditando);
            vista.TextRepuestoNombre.setText(repuesto[1]);
            vista.TextRepuestoMarca.setText(repuesto[2]);
            vista.TextRepuestoModelo.setText(repuesto[3]);
            vista.TextRepuestoExistencias.setText(repuesto[4]);
            vista.TextRepuestoPrecio.setText(repuesto[5]);

            // Cambiar acción del botón Agregar a Modificar
            for (ActionListener al : vista.AgregarRepuesto.getActionListeners()) {
                vista.AgregarRepuesto.removeActionListener(al);
            }
            vista.AgregarRepuesto.addActionListener(e -> confirmarModificacion());
        }
    }
    
    private void confirmarModificacion() {
        try {
            String nombre = vista.TextRepuestoNombre.getText();
            String marca = vista.TextRepuestoMarca.getText();
            String modeloStr = vista.TextRepuestoModelo.getText();
            int existencias = Integer.parseInt(vista.TextRepuestoExistencias.getText());
            double precio = Double.parseDouble(vista.TextRepuestoPrecio.getText());
            
            modelo.modificarRepuesto(filaEditando, nombre, marca, modeloStr, existencias, precio);
            modelo.actualizarTabla(modeloTabla);
            
            // Restaurar interfaz
            terminarModoEdicion();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista, "Error en los valores numéricos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void terminarModoEdicion() {
        enModoEdicion = false;
        filaEditando = -1;

        // Restaurar interfaz
        vista.jLabel7.setText("Añade un Repuesto");
        vista.jLabel7.setForeground(vista.jLabel2.getForeground()); // Color original
        vista.AgregarRepuesto.setText("AGREGAR");

        // Volver a habilitar botones
        vista.RepuestoCargaMasiva.setEnabled(true); // <-- Habilitar carga masiva nuevamente

        // Limpiar campos
        vista.TextRepuestoNombre.setText("");
        vista.TextRepuestoMarca.setText("");
        vista.TextRepuestoModelo.setText("");
        vista.TextRepuestoExistencias.setText("");
        vista.TextRepuestoPrecio.setText("");

        // Restaurar acción original del botón Agregar
        for (ActionListener al : vista.AgregarRepuesto.getActionListeners()) {
            vista.AgregarRepuesto.removeActionListener(al);
        }
        vista.AgregarRepuesto.addActionListener(e -> agregarRepuestoManual());

        // Validar botones según selección actual
        validarBotonesTabla();
    }
    
    private void eliminarRepuestos() {
        int[] filasSeleccionadas = vista.TablaRepuestos.getSelectedRows();
        
        // Eliminar de mayor a menor para evitar problemas con los índices
        for (int i = filasSeleccionadas.length - 1; i >= 0; i--) {
            int fila = filasSeleccionadas[i];
            if (fila >= 0 && fila < modelo.getTamaño()) {
                modelo.eliminarRepuesto(fila);
            }
        }
        
        modelo.actualizarTabla(modeloTabla);
        vista.EliminarRepuesto.setEnabled(false);
    }
    
    private void validarCampos() {
        boolean camposCompletos = !vista.TextRepuestoNombre.getText().isEmpty() &&
                                !vista.TextRepuestoMarca.getText().isEmpty() &&
                                !vista.TextRepuestoModelo.getText().isEmpty() &&
                                !vista.TextRepuestoExistencias.getText().isEmpty() &&
                                !vista.TextRepuestoPrecio.getText().isEmpty();

        if (!camposCompletos) {
            vista.AgregarRepuesto.setEnabled(false);
            return;
        }

        // Validar formatos numéricos
        try {
            int existencias = Integer.parseInt(vista.TextRepuestoExistencias.getText());
            double precio = Double.parseDouble(vista.TextRepuestoPrecio.getText());

            vista.AgregarRepuesto.setEnabled(existencias >= 0 && precio >= 0);
        } catch (NumberFormatException e) {
            vista.AgregarRepuesto.setEnabled(false);
        }
    }
    
    private void agregarRepuestoManual() {
        try {
            String nombre = vista.TextRepuestoNombre.getText();
            String marca = vista.TextRepuestoMarca.getText();
            String modeloStr = vista.TextRepuestoModelo.getText();
            int existencias = Integer.parseInt(vista.TextRepuestoExistencias.getText());
            double precio = Double.parseDouble(vista.TextRepuestoPrecio.getText());

            modelo.agregarRepuesto(nombre, marca, modeloStr, existencias, precio);
            modelo.actualizarTabla(modeloTabla);

            // Limpiar todos los campos - FORMA CORRECTA
            SwingUtilities.invokeLater(() -> {
                vista.TextRepuestoNombre.setText("");
                vista.TextRepuestoMarca.setText("");
                vista.TextRepuestoModelo.setText("");
                vista.TextRepuestoExistencias.setText("");
                vista.TextRepuestoPrecio.setText("");

                // Forzar el foco al primer campo
                vista.TextRepuestoNombre.requestFocusInWindow();

                // Deshabilitar el botón
                vista.AgregarRepuesto.setEnabled(false);
            });

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista, "Error en los valores numéricos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarRepuestosMasivos() {
        JFileChooser fileChooser = new JFileChooser();
        int resultado = fileChooser.showOpenDialog(vista);
        
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            if (!modelo.cargarRepuestosDesdeArchivo(archivo, modeloTabla)) {
                JOptionPane.showMessageDialog(vista, "Error en el archivo, revise su contenido", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void configurarFiltros() {
        // DocumentFilter para existencias (solo números enteros positivos)
        ((PlainDocument) vista.TextRepuestoExistencias.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr) 
                    throws BadLocationException {
                if (text.matches("\\d+")) {
                    super.insertString(fb, offset, text, attr);
                    validarCampos();
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) 
                    throws BadLocationException {
                String current = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = current.substring(0, offset) + text + current.substring(offset + length);

                if (newText.matches("\\d*")) {
                    super.replace(fb, offset, length, text, attrs);
                    validarCampos();
                }
            }
        });

        // DocumentFilter para precio (números con hasta 2 decimales)
        ((PlainDocument) vista.TextRepuestoPrecio.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr) 
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
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) 
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

        // KeyListener para validación adicional
        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validarCampos();
            }
        };

        vista.TextRepuestoNombre.addKeyListener(keyAdapter);
        vista.TextRepuestoMarca.addKeyListener(keyAdapter);
        vista.TextRepuestoModelo.addKeyListener(keyAdapter);
        vista.TextRepuestoExistencias.addKeyListener(keyAdapter);
        vista.TextRepuestoPrecio.addKeyListener(keyAdapter);
    }
}
