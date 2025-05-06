package Modelos;

import java.io.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class ClienteModelo implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String ARCHIVO_CLIENTES = "clientes.dat";
    
    private String[][] clientes;
    private int capacidad;
    private int tamaño;
    
    public ClienteModelo() {
        capacidad = 10;
        clientes = new String[capacidad][5];
        tamaño = 0;
        cargarDesdeArchivo();
    }
    
    private void cargarDesdeArchivo() {
        File archivo = new File(ARCHIVO_CLIENTES);
        if (!archivo.exists()) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARCHIVO_CLIENTES))) {
            ClienteModelo temp = (ClienteModelo) ois.readObject();
            this.capacidad = temp.capacidad;
            this.tamaño = temp.tamaño;
            this.clientes = new String[capacidad][5];

            // Copiar los datos creando nuevos arrays
            for (int i = 0; i < tamaño; i++) {
                this.clientes[i] = new String[] {
                    temp.clientes[i][0],
                    temp.clientes[i][1],
                    temp.clientes[i][2],
                    temp.clientes[i][3],
                    temp.clientes[i][4]
                };
            }
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar clientes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            capacidad = 10;
            clientes = new String[capacidad][5];
            tamaño = 0;
        }
    }
    
    public void guardarEnArchivo() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_CLIENTES))) {
            oos.writeObject(this);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar clientes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void expandirCapacidad() {
        int nuevaCapacidad = capacidad * 2;
        String[][] nuevoArray = new String[nuevaCapacidad][5];
        System.arraycopy(clientes, 0, nuevoArray, 0, tamaño);
        clientes = nuevoArray;
        capacidad = nuevaCapacidad;
    }
    
    public void agregarCliente(String dpi, String nombre, String usuario, String password) {
        if (tamaño == capacidad) {
            expandirCapacidad();
        }

        // Crear un nuevo array para el cliente en lugar de reutilizar referencias
        clientes[tamaño] = new String[5];
        clientes[tamaño][0] = dpi;
        clientes[tamaño][1] = nombre;
        clientes[tamaño][2] = usuario;
        clientes[tamaño][3] = password;
        clientes[tamaño][4] = "Normal";
        tamaño++;

        ordenarClientesPorDPI();
        guardarEnArchivo();
    }
    
    public void modificarCliente(int indice, String dpi, String nombre, String usuario, String password) {
        if (indice >= 0 && indice < tamaño) {
            // Crear un nuevo array para el cliente modificado
            String[] clienteModificado = new String[5];
            clienteModificado[0] = dpi;
            clienteModificado[1] = nombre;
            clienteModificado[2] = usuario;
            clienteModificado[3] = password;
            clienteModificado[4] = "Normal";

            clientes[indice] = clienteModificado;

            ordenarClientesPorDPI();
            guardarEnArchivo();
        }
    }
    
    public void eliminarCliente(int indice) {
        if (indice >= 0 && indice < tamaño) {
            for (int i = indice; i < tamaño - 1; i++) {
                // Crear una copia del cliente en lugar de copiar la referencia
                clientes[i] = new String[] {
                    clientes[i + 1][0],
                    clientes[i + 1][1],
                    clientes[i + 1][2],
                    clientes[i + 1][3],
                    clientes[i + 1][4]
                };
            }
            tamaño--;
            guardarEnArchivo();
        }
    }
    
    public String[] getCliente(int indice) {
        if (indice >= 0 && indice < tamaño) {
            return clientes[indice];
        }
        return null;
    }
    
    public int getTamaño() {
        return tamaño;
    }
    
    public boolean validarCredenciales(String usuario, String password) {
        for (int i = 0; i < tamaño; i++) {
            if (clientes[i][2].equals(usuario)) {  // Falta el paréntesis de cierre aquí
                return clientes[i][3].equals(password);
            }
        }
        return false;
    }
    
   
    public boolean validarTipoCliente(String tipoCliente) {
        if (tipoCliente == null) return false;
        String tipo = tipoCliente.trim().toLowerCase();
        return tipo.equals("normal") || tipo.equals("oro");
    }

    public String formatearTipoCliente(String tipoCliente) {
        if (tipoCliente == null) return "Normal";
        String tipo = tipoCliente.trim().toLowerCase();
        if (tipo.equals("oro")) {
            return "Oro";
        }
        return "Normal"; // Por defecto o si es "normal"
    }
    
    public int buscarClientePorUsuario(String usuario) {
        for (int i = 0; i < tamaño; i++) {
            if (clientes[i][2].equals(usuario)) {
                return i;
            }
        }
        return -1;
    }
    
    private void ordenarClientesPorDPI() {
        // Método de burbuja mejorado para ordenar numéricamente por DPI
        for (int i = 0; i < tamaño - 1; i++) {
            for (int j = 0; j < tamaño - i - 1; j++) {
                long dpi1 = Long.parseLong(clientes[j][0]);
                long dpi2 = Long.parseLong(clientes[j + 1][0]);
                if (dpi1 > dpi2) {
                    // Intercambiar
                    String[] temp = clientes[j];
                    clientes[j] = clientes[j + 1];
                    clientes[j + 1] = temp;
                }
            }
        }
    }
    
    public boolean existeDPI(String dpi) {
        for (int i = 0; i < tamaño; i++) {
            if (clientes[i][0].equals(dpi)) {
                return true;
            }
        }
        return false;
    }

    public boolean existeUsuario(String usuario) {
        for (int i = 0; i < tamaño; i++) {
            if (clientes[i][2].equalsIgnoreCase(usuario)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean existeOtroDPI(String dpi, int indiceExcluir) {
    for (int i = 0; i < tamaño; i++) {
        if (i != indiceExcluir && clientes[i][0].equals(dpi)) {
            return true;
        }
    }
    return false;
}

    public boolean existeOtroUsuario(String usuario, int indiceExcluir) {
        for (int i = 0; i < tamaño; i++) {
            if (i != indiceExcluir && clientes[i][2].equalsIgnoreCase(usuario)) {
                return true;
            }
        }
        return false;
    }

    public boolean existeOtroNombre(String nombre, int indiceExcluir) {
        for (int i = 0; i < tamaño; i++) {
            if (i != indiceExcluir && clientes[i][1].equalsIgnoreCase(nombre)) {
                return true;
            }
        }
        return false;
    }

    public boolean existeNombre(String nombre) {
        for (int i = 0; i < tamaño; i++) {
            if (clientes[i][1].equalsIgnoreCase(nombre)) {
                return true;
            }
        }
        return false;
    }
    
    public void actualizarTabla(DefaultTableModel modelo) {
        modelo.setRowCount(0);
        for (int i = 0; i < tamaño; i++) {
            modelo.addRow(clientes[i]);
        }
    }
    
    public void limpiarDatos() {
        capacidad = 10;
        clientes = new String[capacidad][5];
        tamaño = 0;
        guardarEnArchivo();
    }
}