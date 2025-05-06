package Modelos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.util.HashMap;
import java.util.Map;

public class RepuestoModelo implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String ARCHIVO_REPUESTOS = "repuestos.dat";
    
    private String[][] repuestos;
    private int capacidad;
    private int tamaño;
    private Map<String, Integer> contadorIds;

    public RepuestoModelo() {
        // Inicializar con valores por defecto
        capacidad = 10;
        repuestos = new String[capacidad][6];
        tamaño = 0;
        contadorIds = new HashMap<>();
        
        // Cargar datos existentes
        cargarDesdeArchivo();
    }
    
    private void cargarDesdeArchivo() {
        File archivo = new File(ARCHIVO_REPUESTOS);
        if (!archivo.exists()) {
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARCHIVO_REPUESTOS))) {
            RepuestoModelo temp = (RepuestoModelo) ois.readObject();
            this.repuestos = temp.repuestos;
            this.capacidad = temp.capacidad;
            this.tamaño = temp.tamaño;
            this.contadorIds = temp.contadorIds;
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar repuestos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            // Si hay error, mantener los valores por defecto
            capacidad = 10;
            repuestos = new String[capacidad][6];
            tamaño = 0;
            contadorIds = new HashMap<>();
        }
    }
    
    public void guardarEnArchivo() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_REPUESTOS))) {
            oos.writeObject(this);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar repuestos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public String[] getRepuesto(int indice) {
        if (indice >= 0 && indice < tamaño) {
            return repuestos[indice];
        }
        return null;
    }
    
    public void modificarRepuesto(int indice, String nombre, String marca, String modelo, int existencias, double precio) {
        if (indice >= 0 && indice < tamaño) {
            String id = generarId(nombre, marca, modelo, existencias, precio);
            repuestos[indice][0] = id;
            repuestos[indice][1] = nombre;
            repuestos[indice][2] = marca;
            repuestos[indice][3] = modelo;
            repuestos[indice][4] = String.valueOf(existencias);
            repuestos[indice][5] = String.format("%.2f", precio);
        }
    }
    
    public void eliminarRepuesto(int indice) {
        if (indice >= 0 && indice < tamaño) {
            // Mover todos los elementos una posición hacia atrás
            for (int i = indice; i < tamaño - 1; i++) {
                repuestos[i] = repuestos[i + 1];
            }
            tamaño--;
        }
    }
    
    public int getTamaño() {
        return tamaño;
    }

    private void expandirCapacidad() {
        int nuevaCapacidad = capacidad * 2;
        String[][] nuevoArray = new String[nuevaCapacidad][6];
        System.arraycopy(repuestos, 0, nuevoArray, 0, tamaño);
        repuestos = nuevoArray;
        capacidad = nuevaCapacidad;
    }

    public boolean validarCampos(String nombre, String marca, String modelo, String existencias, String precio) {
        try {
            int exis = Integer.parseInt(existencias);
            double prec = Double.parseDouble(precio);
            
            if (exis < 0 || prec < 0) {
                return false;
            }
            
            return !nombre.isEmpty() && !marca.isEmpty() && !modelo.isEmpty();
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void agregarRepuesto(String nombre, String marca, String modelo, int existencias, double precio) {
        if (tamaño == capacidad) {
            expandirCapacidad();
        }
        
        String id = generarId(nombre, marca, modelo, existencias, precio);
        repuestos[tamaño][0] = id;
        repuestos[tamaño][1] = nombre;
        repuestos[tamaño][2] = marca;
        repuestos[tamaño][3] = modelo;
        repuestos[tamaño][4] = String.valueOf(existencias);
        repuestos[tamaño][5] = String.format("%.2f", precio);
        tamaño++;
    }

    public boolean cargarRepuestosDesdeArchivo(File archivo, DefaultTableModel modeloTabla) {
        if (!archivo.getName().toLowerCase().endsWith(".tmr")) {
            JOptionPane.showMessageDialog(null, "Solo se permiten archivos con extensión .tmr", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean errorEnArchivo = false;
            
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split("-");
                if (partes.length != 5) {
                    errorEnArchivo = true;
                    continue;
                }
                
                try {
                    String nombre = partes[0].trim();
                    String marca = partes[1].trim();
                    String modelo = partes[2].trim();
                    int existencias = Integer.parseInt(partes[3].trim());
                    double precio = Double.parseDouble(partes[4].trim());
                    
                    if (existencias < 0 || precio < 0) {
                        errorEnArchivo = true;
                        continue;
                    }
                    
                    agregarRepuesto(nombre, marca, modelo, existencias, precio);
                } catch (NumberFormatException e) {
                    errorEnArchivo = true;
                }
            }
            
            if (errorEnArchivo) {
                JOptionPane.showMessageDialog(null, "Algunos repuestos en el archivo no se pudieron cargar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
            
            actualizarTabla(modeloTabla);
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al leer el archivo", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private String generarId(String nombre, String marca, String modelo, int existencias, double precio) {
        StringBuilder baseId = new StringBuilder();

        // Primeras letras de nombre, marca y modelo (si existen)
        if (nombre != null && !nombre.isEmpty()) baseId.append(nombre.charAt(0));
        if (marca != null && !marca.isEmpty()) baseId.append(marca.charAt(0));
        if (modelo != null && !modelo.isEmpty()) baseId.append(modelo.charAt(0));

        // Primer dígito de existencias
        String existenciasStr = String.valueOf(existencias);
        if (!existenciasStr.isEmpty()) baseId.append(existenciasStr.charAt(0));

        // Primer dígito del precio (sin punto decimal)
        String precioStr = String.format("%.2f", precio).replace(".", "");
        if (!precioStr.isEmpty()) baseId.append(precioStr.charAt(0));

        String idBase = baseId.toString().toUpperCase();

        // Contador para IDs duplicados
        int contador = contadorIds.getOrDefault(idBase, 0) + 1;
        contadorIds.put(idBase, contador);

        return contador == 1 ? idBase : idBase + contador;
    }

    public void actualizarTabla(DefaultTableModel modelo) {
        modelo.setRowCount(0);
        for (int i = 0; i < tamaño; i++) {
            modelo.addRow(repuestos[i]);
        }
    }
    
    public void limpiarDatos() {
        capacidad = 10;
        repuestos = new String[capacidad][6];
        tamaño = 0;
        contadorIds = new HashMap<>();
        guardarEnArchivo();
    }
}