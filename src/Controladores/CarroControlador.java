package Controladores;

import Modelos.CarroModelo;
import Vistas.CarroVista;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

public class CarroControlador {
    private CarroVista vista;
    private CarroModelo modelo;
    private String usuarioActual;
    private ImageIcon imagenSeleccionada;
    
    public CarroControlador(CarroVista vista, CarroModelo modelo, String usuarioActual) {
        this.vista = vista;
        this.modelo = modelo;
        this.usuarioActual = usuarioActual;
        
        configurarRadioButtons();
        configurarFiltros();
        inicializarVista();
        configurarListeners();
    }
    
    private void configurarRadioButtons() {
        ButtonGroup grupoRadio = new ButtonGroup();
        grupoRadio.add(vista.OrdenarAscendente);
        grupoRadio.add(vista.OrdenarDescendente);
        
        // Establecer ascendente como seleccionado por defecto
        vista.OrdenarAscendente.setSelected(true);
    }
    
    private void inicializarVista() {
        // Configurar el alto de las filas
        vista.TablaCarros.setRowHeight(100);

        // Configurar el renderer para la columna de imagen
        vista.TablaCarros.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                // Obtener la placa del carro
                String placa = (String) table.getModel().getValueAt(row, 0);
                // Obtener la imagen del modelo
                ImageIcon imagen = modelo.getImagenCarro(usuarioActual, placa);

                if (imagen != null) {
                    JLabel label = new JLabel();
                    label.setHorizontalAlignment(JLabel.CENTER);
                    // Escalar la imagen para que quepa en la celda
                    Image img = imagen.getImage().getScaledInstance(150, 80, Image.SCALE_SMOOTH);
                    label.setIcon(new ImageIcon(img));
                    return label;
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        });

        modelo.actualizarTabla((DefaultTableModel) vista.TablaCarros.getModel(), usuarioActual);
        vista.AgregarCarro.setEnabled(false);
    }
    
    private void configurarListeners() {
        vista.BuscarImagenCarro.addActionListener(e -> seleccionarImagen());
        vista.AgregarCarro.addActionListener(e -> agregarCarro());
        vista.BotonOrdenCarro.addActionListener(e -> ordenarTabla());
        
        // Validar campos al escribir
        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validarCampos();
            }
        };
        
        vista.TextPlacaCarro.addKeyListener(keyAdapter);
        vista.TextMarcaCarro.addKeyListener(keyAdapter);
        vista.TextModeloCarro.addKeyListener(keyAdapter);
    }
    
    private void ordenarTabla() {
        DefaultTableModel modeloTabla = (DefaultTableModel) vista.TablaCarros.getModel();
        int rowCount = modeloTabla.getRowCount();
        
        if (rowCount == 0) return;
        
        // Convertir la tabla a un array de objetos para ordenar
        Object[][] datos = new Object[rowCount][4];
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < 4; j++) {
                datos[i][j] = modeloTabla.getValueAt(i, j);
            }
        }
        
        // Ordenar usando Shellsort
        boolean ascendente = vista.OrdenarAscendente.isSelected();
        shellSort(datos, ascendente);
        
        // Actualizar la tabla con los datos ordenados
        modeloTabla.setRowCount(0);
        for (Object[] fila : datos) {
            modeloTabla.addRow(fila);
        }
    }
    
    private void shellSort(Object[][] datos, boolean ascendente) {
        int n = datos.length;

        // Secuencia de gaps para Shellsort
        for (int gap = n/2; gap > 0; gap /= 2) {
            for (int i = gap; i < n; i++) {
                Object[] temp = datos[i];
                String placaActual = (String) temp[0];

                int j;
                for (j = i; j >= gap && compararPlacasASCII(
                        (String) datos[j - gap][0], 
                        placaActual, 
                        ascendente) > 0; j -= gap) {
                    datos[j] = datos[j - gap];
                }
                datos[j] = temp;
            }
        }
    }

    private int compararPlacasASCII(String placa1, String placa2, boolean ascendente) {
        // Convertimos ambas placas a mayúsculas para comparación insensible a mayúsculas/minúsculas
        placa1 = placa1.toUpperCase();
        placa2 = placa2.toUpperCase();

        int minLength = Math.min(placa1.length(), placa2.length());

        // Comparación carácter por carácter
        for (int i = 0; i < minLength; i++) {
            char c1 = placa1.charAt(i);
            char c2 = placa2.charAt(i);

            if (c1 != c2) {
                // Los números (0-9) tienen valores ASCII 48-57
                // Las letras (A-Z) tienen valores ASCII 65-90
                // Por lo tanto, los números son menores que las letras en ASCII
                int result = Character.compare(c1, c2);
                return ascendente ? result : -result;
            }
        }

        // Si todos los caracteres coinciden hasta la longitud mínima,
        // la placa más corta va primero en orden ascendente
        int lengthComparison = Integer.compare(placa1.length(), placa2.length());
        return ascendente ? lengthComparison : -lengthComparison;
    }
    
    // Los demás métodos permanecen igual (seleccionarImagen, validarCampos, agregarCarro, etc.)
    private void seleccionarImagen() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Imágenes JPG", "jpg", "jpeg"));
        
        int resultado = fileChooser.showOpenDialog(vista);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            try {
                ImageIcon icono = new ImageIcon(archivo.getPath());
                Image imagen = icono.getImage().getScaledInstance(
                    vista.jLabel8.getWidth(), 
                    vista.jLabel8.getHeight(), 
                    Image.SCALE_SMOOTH);
                imagenSeleccionada = new ImageIcon(imagen);
                vista.jLabel8.setIcon(imagenSeleccionada);
                validarCampos();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(vista, "Error al cargar la imagen", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void validarCampos() {
        boolean camposCompletos = !vista.TextPlacaCarro.getText().isEmpty() &&
                                !vista.TextMarcaCarro.getText().isEmpty() &&
                                !vista.TextModeloCarro.getText().isEmpty() &&
                                imagenSeleccionada != null &&
                                vista.TextPlacaCarro.getText().length() == 6;
        
        vista.AgregarCarro.setEnabled(camposCompletos);
    }
    
    private void agregarCarro() {
        String placa = vista.TextPlacaCarro.getText();
        String marca = vista.TextMarcaCarro.getText();
        String modeloStr = vista.TextModeloCarro.getText();
        
        modelo.agregarCarro(usuarioActual, placa, marca, modeloStr, imagenSeleccionada);
        
        // Limpiar campos
        vista.TextPlacaCarro.setText("");
        vista.TextMarcaCarro.setText("");
        vista.TextModeloCarro.setText("");
        vista.jLabel8.setIcon(null);
        imagenSeleccionada = null;
        vista.AgregarCarro.setEnabled(false);
        
        // Actualizar tabla
        modelo.actualizarTabla((DefaultTableModel) vista.TablaCarros.getModel(), usuarioActual);
    }
    
    private void configurarFiltros() {
        // Filtro para placa (solo letras y números, máximo 6 caracteres)
        ((PlainDocument) vista.TextPlacaCarro.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr) 
                    throws BadLocationException {
                String newText = fb.getDocument().getText(0, fb.getDocument().getLength()) + text;
                if (newText.length() <= 6 && text.matches("[a-zA-Z0-9]+")) {
                    super.insertString(fb, offset, text, attr);
                    validarCampos();
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) 
                    throws BadLocationException {
                String current = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = current.substring(0, offset) + text + current.substring(offset + length);
                
                if (newText.length() <= 6 && text.matches("[a-zA-Z0-9]*")) {
                    super.replace(fb, offset, length, text, attrs);
                    validarCampos();
                }
            }
        });
    }
}