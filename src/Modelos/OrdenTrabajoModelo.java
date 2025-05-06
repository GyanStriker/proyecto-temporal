package Modelos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class OrdenTrabajoModelo implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String ARCHIVO_ORDENES = "ordenes.dat";
    
    // Mapa para guardar las órdenes de trabajo (usuario -> lista de órdenes)
    private Map<String, String[][]> ordenesPorUsuario;
    private static final String ARCHIVO_FACTURAS = "facturas.dat";
    private Map<String, String[][]> facturasPorUsuario; // Mapa de usuario -> array de facturas
    
    public OrdenTrabajoModelo() {
        this.ordenesPorUsuario = new HashMap<>();
        this.facturasPorUsuario = new HashMap<>();
        cargarDesdeArchivo();
        cargarFacturasDesdeArchivo();
    }
    
    private void cargarFacturasDesdeArchivo() {
        File archivo = new File(ARCHIVO_FACTURAS);
        if (!archivo.exists()) {
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARCHIVO_FACTURAS))) {
            OrdenTrabajoModelo temp = (OrdenTrabajoModelo) ois.readObject();
            this.facturasPorUsuario = temp.facturasPorUsuario;
        } catch (Exception e) {
            System.err.println("Error al cargar facturas: " + e.getMessage());
            facturasPorUsuario = new HashMap<>();
        }
    }
    
    public void guardarFacturasEnArchivo() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_FACTURAS))) {
            oos.writeObject(this);
        } catch (Exception e) {
            System.err.println("Error al guardar facturas: " + e.getMessage());
        }
    }
    
    public void agregarFactura(String usuario, String carroInfo, String servicio, String total) {
        if (!facturasPorUsuario.containsKey(usuario)) {
            facturasPorUsuario.put(usuario, new String[10][3]);
        }
        
        String[][] facturasUsuario = facturasPorUsuario.get(usuario);
        int pos = -1;
        
        for (int i = 0; i < facturasUsuario.length; i++) {
            if (facturasUsuario[i][0] == null) {
                pos = i;
                break;
            }
        }
        
        if (pos == -1) {
            String[][] nuevoArray = new String[facturasUsuario.length * 2][3];
            System.arraycopy(facturasUsuario, 0, nuevoArray, 0, facturasUsuario.length);
            facturasUsuario = nuevoArray;
            facturasPorUsuario.put(usuario, facturasUsuario);
            pos = facturasUsuario.length / 2;
        }
        
        facturasUsuario[pos][0] = carroInfo;
        facturasUsuario[pos][1] = servicio;
        facturasUsuario[pos][2] = total;
        
        guardarFacturasEnArchivo();
    }
    
    public String[][] getFacturasUsuario(String usuario) {
        return facturasPorUsuario.getOrDefault(usuario, new String[0][3]);
    }
    
    public void cargarDesdeArchivo() {
        File archivo = new File(ARCHIVO_ORDENES);
        if (!archivo.exists()) {
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARCHIVO_ORDENES))) {
            OrdenTrabajoModelo temp = (OrdenTrabajoModelo) ois.readObject();
            this.ordenesPorUsuario = temp.ordenesPorUsuario;
        } catch (Exception e) {
            System.err.println("Error al cargar órdenes de trabajo: " + e.getMessage());
            ordenesPorUsuario = new HashMap<>();
        }
    }
    
    public void guardarEnArchivo() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_ORDENES))) {
            oos.writeObject(this);
        } catch (Exception e) {
            System.err.println("Error al guardar órdenes de trabajo: " + e.getMessage());
        }
    }
    
    /*public String[][] getOrdenesUsuario(String usuario) {
        return ordenesPorUsuario.getOrDefault(usuario, new String[0][4]);
    }*/
    
    public String[][] getOrdenesUsuario(String usuario) {
        String[][] ordenesUsuario = ordenesPorUsuario.get(usuario);
        if (ordenesUsuario == null) {
            return new String[0][4];
        }

        // Contar órdenes válidas
        int contador = 0;
        for (String[] orden : ordenesUsuario) {
            if (orden != null && orden[0] != null) {
                contador++;
            }
        }

        // Crear nuevo array solo con órdenes válidas
        String[][] resultado = new String[contador][4];
        int index = 0;
        for (String[] orden : ordenesUsuario) {
            if (orden != null && orden[0] != null) {
                resultado[index++] = orden;
            }
        }

        return resultado;
    }
    
    public void agregarOrden(String usuario, String placa, String marca, String modelo, String servicio) {
        if (!ordenesPorUsuario.containsKey(usuario)) {
            ordenesPorUsuario.put(usuario, new String[10][4]);
        }
        
        String[][] ordenesUsuario = ordenesPorUsuario.get(usuario);
        int pos = -1;
        
        // Buscar posición vacía
        for (int i = 0; i < ordenesUsuario.length; i++) {
            if (ordenesUsuario[i][0] == null) {
                pos = i;
                break;
            }
        }
        
        // Si no hay espacio, expandir array
        if (pos == -1) {
            String[][] nuevoArray = new String[ordenesUsuario.length * 2][4];
            System.arraycopy(ordenesUsuario, 0, nuevoArray, 0, ordenesUsuario.length);
            ordenesUsuario = nuevoArray;
            ordenesPorUsuario.put(usuario, ordenesUsuario);
            pos = ordenesUsuario.length / 2;
        }
        
        // Guardar la orden
        ordenesUsuario[pos][0] = placa;
        ordenesUsuario[pos][1] = marca;
        ordenesUsuario[pos][2] = modelo;
        ordenesUsuario[pos][3] = servicio;
        
        guardarEnArchivo();
    }
    
    public Map<String, String[][]> getOrdenesPorUsuario() {
        return ordenesPorUsuario;
    }
    
    public void limpiarFacturasUsuario(String usuario) {
        if (facturasPorUsuario.containsKey(usuario)) {
            facturasPorUsuario.put(usuario, new String[10][3]);
            guardarFacturasEnArchivo();
        }
    }
    
}