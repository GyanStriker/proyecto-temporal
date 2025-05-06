package Modelos;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class CarroModelo implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String ARCHIVO_CARROS = "carros.dat";
    
    private Map<String, String[][]> carrosPorUsuario; // Mapa de usuario -> array de carros
    private Map<String, Map<String, ImageIcon>> imagenesPorUsuario; // Mapa de usuario -> mapa de placas -> imágenes
    
    public CarroModelo() {
        carrosPorUsuario = new HashMap<>();
        imagenesPorUsuario = new HashMap<>();
        cargarDesdeArchivo();
    }
    
    private void cargarDesdeArchivo() {
        File archivo = new File(ARCHIVO_CARROS);
        if (!archivo.exists()) {
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARCHIVO_CARROS))) {
            CarroModelo temp = (CarroModelo) ois.readObject();
            this.carrosPorUsuario = temp.carrosPorUsuario;
            this.imagenesPorUsuario = temp.imagenesPorUsuario;
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar carros: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            carrosPorUsuario = new HashMap<>();
            imagenesPorUsuario = new HashMap<>();
        }
    }
    
    public void guardarEnArchivo() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_CARROS))) {
            oos.writeObject(this);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar carros: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void agregarCarro(String usuario, String placa, String marca, String modelo, ImageIcon imagen) {
        // Crear array para el usuario si no existe
        if (!carrosPorUsuario.containsKey(usuario)) {
            carrosPorUsuario.put(usuario, new String[10][4]);
            imagenesPorUsuario.put(usuario, new HashMap<>());
        }
        
        String[][] carrosUsuario = carrosPorUsuario.get(usuario);
        Map<String, ImageIcon> imagenesUsuario = imagenesPorUsuario.get(usuario);
        
        // Buscar posición vacía
        int pos = -1;
        for (int i = 0; i < carrosUsuario.length; i++) {
            if (carrosUsuario[i][0] == null) {
                pos = i;
                break;
            }
        }
        
        // Si no hay espacio, expandir array
        if (pos == -1) {
            String[][] nuevoArray = new String[carrosUsuario.length * 2][4];
            System.arraycopy(carrosUsuario, 0, nuevoArray, 0, carrosUsuario.length);
            carrosUsuario = nuevoArray;
            carrosPorUsuario.put(usuario, carrosUsuario);
            pos = carrosUsuario.length / 2;
        }
        
        // Guardar datos del carro
        carrosUsuario[pos][0] = placa;
        carrosUsuario[pos][1] = marca;
        carrosUsuario[pos][2] = modelo;
        carrosUsuario[pos][3] = ""; // Dejamos vacío porque mostraremos la imagen directamente
        
        // Guardar imagen
        imagenesUsuario.put(placa, imagen);
        
        guardarEnArchivo();
    }
    
    public String[][] getCarrosUsuario(String usuario) {
        if (!carrosPorUsuario.containsKey(usuario)) {
            return new String[0][4];
        }

        // Contar cuántos carros tiene el usuario
        int contador = 0;
        String[][] carrosUsuario = carrosPorUsuario.get(usuario);
        for (String[] carro : carrosUsuario) {
            if (carro != null && carro[0] != null) {
                contador++;
            }
        }

        // Crear un nuevo array solo con los carros existentes
        String[][] resultado = new String[contador][4];
        int index = 0;
        for (String[] carro : carrosUsuario) {
            if (carro != null && carro[0] != null) {
                resultado[index++] = carro;
            }
        }

        return resultado;
    }
    
    public ImageIcon getImagenCarro(String usuario, String placa) {
        if (imagenesPorUsuario.containsKey(usuario)) {
            return imagenesPorUsuario.get(usuario).get(placa);
        }
        return null;
    }
    
    public void actualizarTabla(DefaultTableModel modelo, String usuario) {
        modelo.setRowCount(0);
        if (carrosPorUsuario.containsKey(usuario)) {
            String[][] carrosUsuario = carrosPorUsuario.get(usuario);
            for (String[] carro : carrosUsuario) {
                if (carro[0] != null) {
                    modelo.addRow(carro);
                }
            }
        }
    }
    
    public void limpiarDatos() {
        carrosPorUsuario = new HashMap<>();
        imagenesPorUsuario = new HashMap<>();
        guardarEnArchivo();
    }
}