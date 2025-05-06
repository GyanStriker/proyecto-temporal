package Controladores;

import Modelos.CarroModelo;
import Modelos.ClienteModelo;
import Vistas.ClienteVista;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

public class ClienteControlador {
    private ClienteVista vista;
    private ClienteModelo modelo;
    private DefaultTableModel modeloTabla;
    private boolean enModoEdicion = false;
    private int filaEditando = -1;

    public ClienteControlador(ClienteVista vista, ClienteModelo modelo) {
        this.vista = vista;
        this.modelo = modelo;
        this.modeloTabla = (DefaultTableModel) vista.TablaClientes.getModel();
        
        configurarFiltros();
        modelo.actualizarTabla(modeloTabla);
        
        vista.AgregarCliente.setEnabled(false);
        
        vista.TablaClientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                validarBotonesTabla();
            }
        });
        
        vista.AgregarCliente.addActionListener(e -> agregarCliente());
        vista.ModificarCliente.addActionListener(e -> iniciarModificacion());
        vista.EliminarCliente.addActionListener(e -> eliminarCliente());
        vista.ClienteCargaMasiva.addActionListener(e -> cargarClientesMasivos());
        
        vista.ModificarCliente.setEnabled(false);
        vista.EliminarCliente.setEnabled(false);
        
        vista.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                modelo.guardarEnArchivo();
            }
        });
    }
    
    private void validarBotonesTabla() {
        int filaSeleccionada = vista.TablaClientes.getSelectedRow();
        boolean filaValida = filaSeleccionada >= 0 && filaSeleccionada < modelo.getTamaño();

        if (!enModoEdicion) {
            vista.ModificarCliente.setEnabled(filaValida);
            vista.EliminarCliente.setEnabled(filaValida);
        } else {
            vista.ModificarCliente.setEnabled(false);
            vista.EliminarCliente.setEnabled(false);
        }
    }
    
    private void agregarCliente() {
        try {
            String dpi = vista.TextClienteDPI.getText();
            String nombre = vista.TextClienteNombre.getText();
            String usuario = vista.TextClienteUsuario.getText();
            String password = vista.TextClientePassword.getText();

            if (enModoEdicion) {
                // Obtener el cliente actual antes de modificar
                String[] clienteActual = modelo.getCliente(filaEditando);

                // Validaciones para modificación (ignorando el cliente actual)
                if (!dpi.equals(clienteActual[0]) && modelo.existeOtroDPI(dpi, filaEditando)) {
                    JOptionPane.showMessageDialog(vista, "Ya existe un cliente con este DPI", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!usuario.equals(clienteActual[2]) && modelo.existeOtroUsuario(usuario, filaEditando)) {
                    JOptionPane.showMessageDialog(vista, "Este nombre de usuario ya está en uso", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!nombre.equals(clienteActual[1]) && modelo.existeOtroNombre(nombre, filaEditando)) {
                    JOptionPane.showMessageDialog(vista, "Ya existe un cliente con este nombre", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                modelo.modificarCliente(filaEditando, dpi, nombre, usuario, password);
                terminarModoEdicion();
                
            } else {
                // Validaciones para nuevo cliente
                if (modelo.existeDPI(dpi)) {
                    JOptionPane.showMessageDialog(vista, "Ya existe un cliente con este DPI", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (modelo.existeUsuario(usuario)) {
                    JOptionPane.showMessageDialog(vista, "Este nombre de usuario ya está en uso", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (modelo.existeNombre(nombre)) {
                    JOptionPane.showMessageDialog(vista, "Ya existe un cliente con este nombre", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                modelo.agregarCliente(dpi, nombre, usuario, password);

                vista.TextClienteDPI.setText("");
                vista.TextClienteNombre.setText("");
                vista.TextClienteUsuario.setText("");
                vista.TextClientePassword.setText("");
                vista.AgregarCliente.setEnabled(false);
            }

            modelo.actualizarTabla(modeloTabla);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista, "El DPI debe contener solo números", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al procesar el cliente", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void iniciarModificacion() {
        filaEditando = vista.TablaClientes.getSelectedRow();
        if (filaEditando >= 0) {
            enModoEdicion = true;
            
            vista.jLabel7.setText("[Modifica el Cliente]");
            vista.jLabel7.setForeground(Color.BLUE);
            vista.AgregarCliente.setText("MODIFICAR");
            
            vista.ModificarCliente.setEnabled(false);
            vista.EliminarCliente.setEnabled(false);
            vista.ClienteCargaMasiva.setEnabled(false);
            
            String[] cliente = modelo.getCliente(filaEditando);
            vista.TextClienteDPI.setText(cliente[0]);
            vista.TextClienteNombre.setText(cliente[1]);
            vista.TextClienteUsuario.setText(cliente[2]);
            vista.TextClientePassword.setText(cliente[3]);
        }
    }
    
    private void terminarModoEdicion() {
        enModoEdicion = false;
        filaEditando = -1;
        
        vista.jLabel7.setText("Añade un Cliente");
        vista.jLabel7.setForeground(vista.jLabel2.getForeground());
        vista.AgregarCliente.setText("AGREGAR");
        vista.ClienteCargaMasiva.setEnabled(true);
        
        vista.TextClienteDPI.setText("");
        vista.TextClienteNombre.setText("");
        vista.TextClienteUsuario.setText("");
        vista.TextClientePassword.setText("");
    }
    
    private void eliminarCliente() {
        int filaSeleccionada = vista.TablaClientes.getSelectedRow();
        if (filaSeleccionada >= 0) {
            modelo.eliminarCliente(filaSeleccionada);
            modelo.actualizarTabla(modeloTabla);
            vista.EliminarCliente.setEnabled(false);
        }
    }
    
    private void cargarClientesMasivos() {
        JFileChooser fileChooser = new JFileChooser();
        int resultado = fileChooser.showOpenDialog(vista);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();

            if (!archivo.getName().toLowerCase().endsWith(".tmca")) {
                JOptionPane.showMessageDialog(vista, 
                    "Solo se permiten archivos con extensión .tmca", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                String linea;
                int clientesAgregados = 0;
                int clientesRechazados = 0;
                CarroModelo carroModelo = new CarroModelo();
                StringBuilder detallesError = new StringBuilder();

                while ((linea = br.readLine()) != null) {
                    linea = linea.trim();
                    if (linea.isEmpty()) continue;

                    // Usar una expresión regular para dividir solo los primeros 5 guiones
                    String[] partes = linea.split("-(?=(?:[^-]*-[^-]*)*$)", 6);
                    if (partes.length != 6) {
                        detallesError.append("Línea rechazada - Formato incorrecto: ").append(linea).append("\n");
                        clientesRechazados++;
                        continue;
                    }

                    try {
                        // Extraer datos
                        String dpi = partes[0].trim();
                        String nombre = partes[1].trim();
                        String usuario = partes[2].trim();
                        String password = partes[3].trim();
                        String tipoCliente = partes[4].trim();
                        String automovilesStr = partes[5].trim();

                        // Validar cliente
                        if (!dpi.matches("\\d+")) {
                            detallesError.append("Cliente rechazado - DPI inválido: ").append(dpi).append("\n");
                            clientesRechazados++;
                            continue;
                        }
                        if (modelo.existeDPI(dpi)) {
                            detallesError.append("Cliente rechazado - DPI ya existe: ").append(dpi).append("\n");
                            clientesRechazados++;
                            continue;
                        }
                        if (modelo.existeUsuario(usuario)) {
                            detallesError.append("Cliente rechazado - Usuario ya existe: ").append(usuario).append("\n");
                            clientesRechazados++;
                            continue;
                        }

                        // Procesar automóviles
                        String[] automoviles = automovilesStr.split(";");
                        int autosValidos = 0;

                        for (String auto : automoviles) {
                            String autoTrim = auto.trim();
                            if (autoTrim.isEmpty()) continue;

                            String[] datosAuto = autoTrim.split(",");
                            if (datosAuto.length != 4) {
                                detallesError.append("Auto inválido - Formato incorrecto: ").append(autoTrim).append("\n");
                                continue;
                            }

                            String placa = datosAuto[0].trim().toUpperCase();
                            String marca = datosAuto[1].trim();
                            String modeloAuto = datosAuto[2].trim();
                            String rutaImagen = datosAuto[3].trim().replace("\\", "/");

                            // Validar placa
                            if (!placa.matches("[A-Z0-9]{6}")) {
                                detallesError.append("Auto inválido - Placa incorrecta: ").append(placa).append("\n");
                                continue;
                            }

                            // Validar ruta
                            File archivoImagen = new File(rutaImagen);
                            if (!archivoImagen.exists()) {
                                detallesError.append("Auto inválido - Imagen no encontrada: ").append(rutaImagen).append("\n");
                                continue;
                            }

                            autosValidos++;
                        }

                        if (autosValidos == 0) {
                            detallesError.append("Cliente rechazado - No tiene autos válidos: ").append(usuario).append("\n");
                            clientesRechazados++;
                            continue;
                        }

                        // Si llegamos aquí, todo es válido
                        // 1. Agregar los autos
                        for (String auto : automoviles) {
                            String autoTrim = auto.trim();
                            if (autoTrim.isEmpty()) continue;

                            String[] datosAuto = autoTrim.split(",");
                            if (datosAuto.length != 4) continue;

                            String placa = datosAuto[0].trim().toUpperCase();
                            String marca = datosAuto[1].trim();
                            String modeloAuto = datosAuto[2].trim();
                            String rutaImagen = datosAuto[3].trim().replace("\\", "/");

                            try {
                                ImageIcon icono = new ImageIcon(rutaImagen);
                                Image imagen = icono.getImage().getScaledInstance(150, 100, Image.SCALE_SMOOTH);
                                carroModelo.agregarCarro(usuario, placa, marca, modeloAuto, new ImageIcon(imagen));
                            } catch (Exception e) {
                                continue;
                            }
                        }

                        // 2. Agregar el cliente
                        modelo.agregarCliente(dpi, nombre, usuario, password);

                        // 3. Actualizar tipo de cliente
                        int indiceCliente = modelo.buscarClientePorUsuario(usuario);
                        String[] cliente = modelo.getCliente(indiceCliente);
                        cliente[4] = modelo.formatearTipoCliente(tipoCliente);

                        clientesAgregados++;

                    } catch (Exception e) {
                        detallesError.append("Error procesando línea: ").append(linea)
                                   .append(" - ").append(e.getMessage()).append("\n");
                        clientesRechazados++;
                    }
                }

                // Guardar cambios
                carroModelo.guardarEnArchivo();
                modelo.guardarEnArchivo();

                // Mostrar resultados
                String mensaje = "Resultado de carga:\n\n" +
                               "Clientes agregados: " + clientesAgregados + "\n" +
                               "Clientes rechazados: " + clientesRechazados + "\n\n";

                if (clientesRechazados > 0) {
                    mensaje += "Detalles de errores:\n" + detallesError.toString();
                }

                JOptionPane.showMessageDialog(vista, mensaje, "Resultado", JOptionPane.INFORMATION_MESSAGE);
                modelo.actualizarTabla(modeloTabla);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(vista, 
                    "Error al leer archivo: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void validarCampos() {
        boolean camposCompletos = !vista.TextClienteDPI.getText().isEmpty() &&
                                !vista.TextClienteNombre.getText().isEmpty() &&
                                !vista.TextClienteUsuario.getText().isEmpty() &&
                                !vista.TextClientePassword.getText().isEmpty();

        vista.AgregarCliente.setEnabled(camposCompletos);
    }
    
    private void configurarFiltros() {
        // Filtro para solo números en DPI
        ((PlainDocument) vista.TextClienteDPI.getDocument()).setDocumentFilter(new DocumentFilter() {
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
        
        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validarCampos();
            }
        };
        
        vista.TextClienteNombre.addKeyListener(keyAdapter);
        vista.TextClienteUsuario.addKeyListener(keyAdapter);
        vista.TextClientePassword.addKeyListener(keyAdapter);
    }
}