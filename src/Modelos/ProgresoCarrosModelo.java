package Modelos;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;

public class ProgresoCarrosModelo implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String ARCHIVO_PROGRESO = "progreso.dat";
    
    private Map<String, String[]> carrosEnCola; // Placa -> [orden, placa, marca, modelo, cliente]
    private int contadorOrden = 1;
    
    public ProgresoCarrosModelo() {
        carrosEnCola = new HashMap<>();
        cargarDesdeArchivo();
    }
    
    private void cargarDesdeArchivo() {
        File archivo = new File(ARCHIVO_PROGRESO);
        if (!archivo.exists()) {
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARCHIVO_PROGRESO))) {
            ProgresoCarrosModelo temp = (ProgresoCarrosModelo) ois.readObject();
            this.carrosEnCola = temp.carrosEnCola;
            this.contadorOrden = temp.contadorOrden;
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar progreso de carros: " + e.getMessage(), 
                                       "Error", JOptionPane.ERROR_MESSAGE);
            carrosEnCola = new HashMap<>();
            contadorOrden = 1;
        }
    }
    
    public void guardarEnArchivo() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_PROGRESO))) {
            oos.writeObject(this);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar progreso de carros: " + e.getMessage(), 
                                       "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public synchronized void agregarCarroACola(String placa, String marca, String modelo, String cliente) {
        String orden = String.format("%03d", contadorOrden++);
        carrosEnCola.put(placa, new String[]{orden, placa, marca, modelo, cliente});
        guardarEnArchivo();
    }
    
    public synchronized void removerCarroDeCola(String placa) {
        carrosEnCola.remove(placa);
        guardarEnArchivo();
    }
    
    public synchronized String[][] getCarrosEnCola() {
        String[][] resultado = new String[carrosEnCola.size()][5];
        int i = 0;
        for (String[] datos : carrosEnCola.values()) {
            resultado[i++] = datos;
        }
        return resultado;
    }
    
    public synchronized void limpiarDatos() {
        carrosEnCola.clear();
        contadorOrden = 1;
        guardarEnArchivo();
    }
}