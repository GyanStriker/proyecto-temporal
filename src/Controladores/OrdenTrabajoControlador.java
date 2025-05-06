package Controladores;

import Modelos.CarroModelo;
import Modelos.ClienteModelo;
import Modelos.OrdenTrabajoModelo;
import Modelos.ServicioModelo;
import Vistas.OrdenTrabajoVista;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class OrdenTrabajoControlador {
    private OrdenTrabajoVista vista;
    private ClienteModelo clienteModelo;
    private CarroModelo carroModelo;
    private ServicioModelo servicioModelo;
    private OrdenTrabajoModelo ordenTrabajoModelo;
    private String usuarioActual;
    private TallerControlador tallerControlador;
    
    public OrdenTrabajoControlador(OrdenTrabajoVista vista, ClienteModelo clienteModelo, 
                                 CarroModelo carroModelo, ServicioModelo servicioModelo, 
                                 String usuarioActual) {
        this.vista = vista;
        this.clienteModelo = clienteModelo;
        this.carroModelo = carroModelo;
        this.servicioModelo = servicioModelo;
        this.usuarioActual = usuarioActual;
        this.ordenTrabajoModelo = new OrdenTrabajoModelo();
        
        configurarVista();
        configurarListeners();
        
        // Inicializar controlador del taller
        this.tallerControlador = new TallerControlador(vista, ordenTrabajoModelo, servicioModelo, usuarioActual);
    }
    
    /*private void enviarAlTaller() {
        DefaultTableModel modeloTabla = (DefaultTableModel) vista.TablaOrdenTrabajo.getModel();
        int totalOrdenes = modeloTabla.getRowCount();
        
        if (totalOrdenes == 0) {
            JOptionPane.showMessageDialog(vista, "No hay órdenes para enviar al taller", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Mostrar confirmación
        int confirmacion = JOptionPane.showConfirmDialog(
            vista,
            "¿Está seguro que desea enviar " + totalOrdenes + " vehículo(s) al taller?",
            "Confirmar envío al taller",
            JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            // Iniciar el proceso del taller
            tallerControlador.iniciarProcesoTaller();
            
            // Limpiar tabla después de enviar
            modeloTabla.setRowCount(0);
            ordenTrabajoModelo.getOrdenesPorUsuario().remove(usuarioActual);
            ordenTrabajoModelo.guardarEnArchivo();
            
            JOptionPane.showMessageDialog(vista, 
                totalOrdenes + " vehículo(s) enviados al taller correctamente",
                "Éxito", 
                JOptionPane.INFORMATION_MESSAGE);
            
            actualizarEstadoBotones();
        }
    }*/
    
    private void enviarAlTaller() {
        // Verificar órdenes tanto en la tabla como en el modelo
        DefaultTableModel modeloTabla = (DefaultTableModel) vista.TablaOrdenTrabajo.getModel();
        String[][] ordenesUsuario = ordenTrabajoModelo.getOrdenesUsuario(usuarioActual);

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

        // Iniciar el proceso del taller inmediatamente
        tallerControlador.iniciarProcesoTaller();

        // Limpiar tabla después de enviar
        modeloTabla.setRowCount(0);
        ordenTrabajoModelo.getOrdenesPorUsuario().remove(usuarioActual);
        ordenTrabajoModelo.guardarEnArchivo();

        actualizarEstadoBotones();
    }
    
    private void configurarVista() {
        // Configurar modelo de tabla
        DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"Placa", "Marca", "Modelo", "Servicio"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        vista.TablaOrdenTrabajo.setModel(modeloTabla);
        vista.TablaOrdenTrabajo.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        
        // Inicialmente deshabilitar botones
        vista.BotonAgregarOrdenTrabajo.setEnabled(false);
        vista.BotonEliminarOT.setEnabled(false);
        vista.BotonEnviarTallerOT.setEnabled(false);
        
        cargarVehiculosCliente();
        cargarServiciosDisponibles();
        cargarOrdenesUsuario();
        
        // Verificar estado inicial de botones
        actualizarEstadoBotones();
    }
    
    private void cargarVehiculosCliente() {
        DefaultComboBoxModel<String> modeloCombo = new DefaultComboBoxModel<>();
        String[][] carrosUsuario = carroModelo.getCarrosUsuario(usuarioActual);
        
        for (String[] carro : carrosUsuario) {
            if (carro != null && carro[0] != null) {
                modeloCombo.addElement(carro[0] + " - " + carro[1] + " - " + carro[2]);
            }
        }
        
        vista.VehiculoComboBox.setModel(modeloCombo);
    }
    
    private void cargarServiciosDisponibles() {
        DefaultComboBoxModel<String> modeloCombo = new DefaultComboBoxModel<>();

        for (int i = 0; i < servicioModelo.getTamaño(); i++) {
            String[] servicio = servicioModelo.getServicio(i);
            if (servicio != null && servicio[1] != null) {
                // Formato especial para diagnóstico
                if (servicio[1].equalsIgnoreCase("Diagnóstico")) {
                    modeloCombo.addElement("Diagnóstico");
                } else {
                    modeloCombo.addElement(servicio[1] + " - " + servicio[2] + " - " + servicio[3]);
                }
            }
        }

        vista.ServicioComboBox.setModel(modeloCombo);
    }
    
    private void cargarOrdenesUsuario() {
        DefaultTableModel modeloTabla = (DefaultTableModel) vista.TablaOrdenTrabajo.getModel();
        modeloTabla.setRowCount(0);
        
        String[][] ordenesUsuario = ordenTrabajoModelo.getOrdenesUsuario(usuarioActual);
        for (String[] orden : ordenesUsuario) {
            if (orden != null && orden[0] != null) {
                modeloTabla.addRow(orden);
            }
        }
    }
    
    private void configurarListeners() {
        // Listener para combobox
        ItemListener itemListener = e -> {
            boolean vehiculoSeleccionado = vista.VehiculoComboBox.getSelectedItem() != null;
            boolean servicioSeleccionado = vista.ServicioComboBox.getSelectedItem() != null;
            vista.BotonAgregarOrdenTrabajo.setEnabled(vehiculoSeleccionado && servicioSeleccionado);
        };
        
        vista.VehiculoComboBox.addItemListener(itemListener);
        vista.ServicioComboBox.addItemListener(itemListener);
        
        // Listener para botón agregar
        vista.BotonAgregarOrdenTrabajo.addActionListener(e -> {
            agregarOrdenTrabajo();
            actualizarEstadoBotones();
        });
        
        // Listener para botón eliminar
        vista.BotonEliminarOT.addActionListener(e -> {
            eliminarOrdenSeleccionada();
            actualizarEstadoBotones();
        });
        
        // Listener para botón enviar al taller
        vista.BotonEnviarTallerOT.addActionListener(e -> enviarAlTaller());
        
        // Listener para selección en tabla
        vista.TablaOrdenTrabajo.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarEstadoBotones();
            }
        });
    }
    
    /*private void actualizarEstadoBotones() {
        // Habilitar botón eliminar si hay fila seleccionada
        int filaSeleccionada = vista.TablaOrdenTrabajo.getSelectedRow();
        vista.BotonEliminarOT.setEnabled(filaSeleccionada >= 0);
        
        // Habilitar botón enviar al taller si hay órdenes en la tabla
        DefaultTableModel modeloTabla = (DefaultTableModel) vista.TablaOrdenTrabajo.getModel();
        vista.BotonEnviarTallerOT.setEnabled(modeloTabla.getRowCount() > 0);
    }*/
    
    private void actualizarEstadoBotones() {
        // Habilitar botón eliminar si hay fila seleccionada
        int filaSeleccionada = vista.TablaOrdenTrabajo.getSelectedRow();
        vista.BotonEliminarOT.setEnabled(filaSeleccionada >= 0);

        // Habilitar botón enviar al taller si hay órdenes en la tabla o en el modelo
        DefaultTableModel modeloTabla = (DefaultTableModel) vista.TablaOrdenTrabajo.getModel();
        String[][] ordenesUsuario = ordenTrabajoModelo.getOrdenesUsuario(usuarioActual);

        boolean hayOrdenes = modeloTabla.getRowCount() > 0;
        if (!hayOrdenes) {
            for (String[] orden : ordenesUsuario) {
                if (orden != null && orden[0] != null) {
                    hayOrdenes = true;
                    break;
                }
            }
        }

        vista.BotonEnviarTallerOT.setEnabled(hayOrdenes);
    }
    
    private void eliminarOrdenSeleccionada() {
        int filaSeleccionada = vista.TablaOrdenTrabajo.getSelectedRow();
        if (filaSeleccionada >= 0) {
            DefaultTableModel modeloTabla = (DefaultTableModel) vista.TablaOrdenTrabajo.getModel();
            
            // Obtener datos de la fila a eliminar
            String placa = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
            String marca = (String) modeloTabla.getValueAt(filaSeleccionada, 1);
            String modelo = (String) modeloTabla.getValueAt(filaSeleccionada, 2);
            String servicio = (String) modeloTabla.getValueAt(filaSeleccionada, 3);
            
            // Eliminar del modelo
            eliminarOrdenDelModelo(placa, marca, modelo, servicio);
            
            // Eliminar de la tabla
            modeloTabla.removeRow(filaSeleccionada);
            
            JOptionPane.showMessageDialog(vista, "Orden eliminada correctamente", 
                                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void eliminarOrdenDelModelo(String placa, String marca, String modelo, String servicio) {
        String[][] ordenesUsuario = ordenTrabajoModelo.getOrdenesPorUsuario().get(usuarioActual);
        if (ordenesUsuario != null) {
            for (int i = 0; i < ordenesUsuario.length; i++) {
                if (ordenesUsuario[i] != null && 
                    placa.equals(ordenesUsuario[i][0]) &&
                    marca.equals(ordenesUsuario[i][1]) &&
                    modelo.equals(ordenesUsuario[i][2]) &&
                    servicio.equals(ordenesUsuario[i][3])) {
                    
                    // Marcar como eliminado (poner null)
                    ordenesUsuario[i] = null;
                    break;
                }
            }
            ordenTrabajoModelo.guardarEnArchivo();
        }
    }
    
    
    private void agregarOrdenTrabajo() {
        String vehiculoSeleccionado = (String) vista.VehiculoComboBox.getSelectedItem();
        String servicioSeleccionado = (String) vista.ServicioComboBox.getSelectedItem();

        if (vehiculoSeleccionado == null || servicioSeleccionado == null) {
            return;
        }

        // Extraer datos del vehículo
        String[] partesVehiculo = vehiculoSeleccionado.split(" - ");
        if (partesVehiculo.length != 3) {
            mostrarError("Formato de vehículo incorrecto");
            return;
        }

        String placa = partesVehiculo[0];
        String marcaVehiculo = partesVehiculo[1];
        String modeloVehiculo = partesVehiculo[2];


        // Extraer datos del servicio seleccionado
        String[] partesServicio = servicioSeleccionado.split(" - ");

        // Manejo especial para diagnóstico
        if (servicioSeleccionado.startsWith("Diagnóstico")) {
            procesarDiagnostico(placa, marcaVehiculo, modeloVehiculo);
            return;
        }

        // Validación para servicios normales
        if (partesServicio.length != 3) {
            mostrarError("Formato de servicio incorrecto");
            return;
        }

        String nombreServicio = partesServicio[0];
        String marcaServicio = partesServicio[1];
        String modeloServicio = partesServicio[2];


        // Validar coincidencia para servicios normales
        if (!marcaVehiculo.equalsIgnoreCase(marcaServicio) || !modeloVehiculo.equalsIgnoreCase(modeloServicio)) {
            mostrarError("La marca y modelo del vehículo no coinciden con los requeridos por el servicio");
            return;
        }

        agregarOrdenATabla(placa, marcaVehiculo, modeloVehiculo, nombreServicio);
    }

    private void procesarDiagnostico(String placa, String marcaVehiculo, String modeloVehiculo) {
        // Buscar servicios compatibles (excluyendo diagnóstico)
        String[][] serviciosCompatibles = buscarServiciosCompatibles(marcaVehiculo, modeloVehiculo);

        if (serviciosCompatibles == null || serviciosCompatibles.length == 0) {
            mostrarError("No hay servicios disponibles para este vehículo");
            return;
        }

        // Seleccionar servicio aleatorio
        int indiceAleatorio = (int)(Math.random() * serviciosCompatibles.length);
        String nombreServicio = serviciosCompatibles[indiceAleatorio][1];

        // Mostrar confirmación al usuario
        int respuesta = JOptionPane.showConfirmDialog(vista, 
            "Su carro necesita el servicio: " + nombreServicio + ", ¿desea proceder?",
            "Resultado de diagnóstico",
            JOptionPane.YES_NO_OPTION);

        if (respuesta == JOptionPane.YES_OPTION) {
            agregarOrdenATabla(placa, marcaVehiculo, modeloVehiculo, nombreServicio);
        }
    }

    private String[][] buscarServiciosCompatibles(String marcaVehiculo, String modeloVehiculo) {
        // Primero contar cuántos servicios compatibles hay
        int contador = 0;
        for (int i = 0; i < servicioModelo.getTamaño(); i++) {
            String[] servicio = servicioModelo.getServicio(i);
            if (servicio != null && servicio[1] != null && 
                !servicio[1].equalsIgnoreCase("Diagnóstico") &&
                servicio[2].equalsIgnoreCase(marcaVehiculo) && 
                servicio[3].equalsIgnoreCase(modeloVehiculo)) {
                contador++;
            }
        }

        if (contador == 0) {
            return new String[0][];
        }

        // Crear array con el tamaño exacto
        String[][] serviciosCompatibles = new String[contador][];
        int index = 0;

        // Llenar el array con los servicios compatibles
        for (int i = 0; i < servicioModelo.getTamaño(); i++) {
            String[] servicio = servicioModelo.getServicio(i);
            if (servicio != null && servicio[1] != null && 
                !servicio[1].equalsIgnoreCase("Diagnóstico") &&
                servicio[2].equalsIgnoreCase(marcaVehiculo) && 
                servicio[3].equalsIgnoreCase(modeloVehiculo)) {
                serviciosCompatibles[index++] = servicio;
            }
        }

        return serviciosCompatibles;
    }

    private void agregarOrdenATabla(String placa, String marca, String modelo, String servicio) {
        // Agregar a la tabla
        DefaultTableModel modeloTabla = (DefaultTableModel) vista.TablaOrdenTrabajo.getModel();
        modeloTabla.addRow(new Object[]{placa, marca, modelo, servicio});

        // Agregar al modelo
        ordenTrabajoModelo.agregarOrden(usuarioActual, placa, marca, modelo, servicio);

        JOptionPane.showMessageDialog(vista, "Vehículo agregado al servicio correctamente", 
                                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(vista, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}