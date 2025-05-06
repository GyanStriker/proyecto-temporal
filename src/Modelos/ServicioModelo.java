package Modelos;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
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
import java.util.HashMap;
import java.util.Map;

public class ServicioModelo implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String ARCHIVO_SERVICIOS = "servicios.dat";
    
    private String[][] servicios;
    private int capacidad;
    private int tamaño;
    private Map<String, Integer> contadorIds;
    private transient RepuestoModelo repuestoModelo; // Transient porque no necesitamos serializarlo
    
    public ServicioModelo(RepuestoModelo repuestoModelo) {
        this.repuestoModelo = repuestoModelo;
        cargarDesdeArchivo();
        
        // Si no se pudo cargar, inicializar valores por defecto
        if (servicios == null) {
            capacidad = 10;
            servicios = new String[capacidad][7];
            tamaño = 0;
            contadorIds = new HashMap<>();
            
            // Agregar servicio de diagnóstico por defecto
            agregarServicio("Diagnóstico", "", "", "", 425);
        }
    }
    
    private void cargarDesdeArchivo() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARCHIVO_SERVICIOS))) {
            ServicioModelo temp = (ServicioModelo) ois.readObject();
            this.servicios = temp.servicios;
            this.capacidad = temp.capacidad;
            this.tamaño = temp.tamaño;
            this.contadorIds = temp.contadorIds;
        } catch (FileNotFoundException e) {
            // Archivo no existe aún, es normal en primera ejecución
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar servicios: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void guardarEnArchivo() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_SERVICIOS))) {
            oos.writeObject(this);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar servicios: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void expandirCapacidad() {
        int nuevaCapacidad = capacidad * 2;
        String[][] nuevoArray = new String[nuevaCapacidad][7];
        System.arraycopy(servicios, 0, nuevoArray, 0, tamaño);
        servicios = nuevoArray;
        capacidad = nuevaCapacidad;
    }
    
    public String[] getServicio(int indice) {
        if (indice >= 0 && indice < tamaño) {
            return servicios[indice];
        }
        return null;
    }
    
    public void modificarServicio(int indice, String nombre, String marca, String modelo, String repuestos, double manoObra) {
        if (indice >= 0 && indice < tamaño && !servicios[indice][1].equals("Diagnóstico")) {
            // Mantener el ID original al modificar
            servicios[indice][1] = nombre;
            servicios[indice][2] = marca;
            servicios[indice][3] = modelo;
            servicios[indice][4] = repuestos;
            servicios[indice][5] = String.format("%.2f", manoObra);
            servicios[indice][6] = calcularPrecioTotal(repuestos, manoObra);
        }
    }
    
    public void eliminarServicio(int indice) {
        if (indice >= 0 && indice < tamaño && !servicios[indice][1].equals("Diagnóstico")) {
            for (int i = indice; i < tamaño - 1; i++) {
                servicios[i] = servicios[i + 1];
            }
            tamaño--;
        }
    }
    
    public int getTamaño() {
        return tamaño;
    }
    
    /*public boolean validarCampos(String nombre, String marca, String modelo, String manoObra) {
        try {
            double manoObraVal = Double.parseDouble(manoObra);
            if (manoObraVal < 0) {
                return false;
            }
            
            return !nombre.isEmpty() && (!marca.isEmpty() || nombre.equals("Diagnóstico")) && 
                   (!modelo.isEmpty() || nombre.equals("Diagnóstico"));
        } catch (NumberFormatException e) {
            return false;
        }
    }*/
    
    public boolean validarCampos(String nombre, String marca, String modelo, String manoObra) {
        try {
            double manoObraVal = Double.parseDouble(manoObra);
            if (manoObraVal < 0) {
                return false;
            }

            // El diagnóstico es una excepción - no requiere marca ni modelo
            if (nombre.equalsIgnoreCase("Diagnóstico")) {
                return !nombre.isEmpty();
            }

            return !nombre.isEmpty() && !marca.isEmpty() && !modelo.isEmpty();
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public void agregarServicio(String nombre, String marca, String modelo, String repuestos, double manoObra) {
        if (tamaño == capacidad) {
            expandirCapacidad();
        }

        // Generar ID después de verificar capacidad para asegurar que tenemos espacio
        String id = generarId(nombre, marca, modelo, manoObra);

        servicios[tamaño][0] = id;
        servicios[tamaño][1] = nombre;
        servicios[tamaño][2] = marca;
        servicios[tamaño][3] = modelo;
        servicios[tamaño][4] = repuestos;
        servicios[tamaño][5] = String.format("%.2f", manoObra);
        servicios[tamaño][6] = calcularPrecioTotal(repuestos, manoObra);
        tamaño++;
    }

    
    private String calcularPrecioTotal(String repuestos, double manoObra) {
        if (repuestos == null || repuestos.isEmpty()) {
            return String.format("%.2f", manoObra);
        }

        double total = manoObra;
        String[] repuestosArray = repuestos.split(";");

        for (String repuesto : repuestosArray) {
            String[] partes = repuesto.split("-");
            if (partes.length >= 5) { // Asegurarnos que tenga al menos 5 partes (incluyendo precio)
                try {
                    // Eliminar el símbolo $ si está presente y cualquier otro carácter no numérico
                    String precioStr = partes[4].replaceAll("[^\\d.]", "");
                    total += Double.parseDouble(precioStr);
                } catch (NumberFormatException e) {
                    // Ignorar repuestos mal formados
                    System.err.println("Error al parsear precio: " + partes[5]);
                }
            }
        }

        return String.format("%.2f", total);
    }
    
    private String generarId(String nombre, String marca, String modelo, double manoObra) {
        StringBuilder baseId = new StringBuilder();

        // Primeras letras de nombre, marca y modelo (si existen)
        if (nombre != null && !nombre.isEmpty()) baseId.append(nombre.charAt(0));
        if (marca != null && !marca.isEmpty()) baseId.append(marca.charAt(0));
        if (modelo != null && !modelo.isEmpty()) baseId.append(modelo.charAt(0));

        // Primer dígito del precio (sin punto decimal)
        String manoObraStr = String.format("%.2f", manoObra).replace(".", "");
        if (!manoObraStr.isEmpty()) baseId.append(manoObraStr.charAt(0));

        String idBase = baseId.toString().toUpperCase();

        // Buscar el máximo contador existente para esta base
        int maxContador = 0;
        for (int i = 0; i < tamaño; i++) {
            if (servicios[i][0] != null && servicios[i][0].startsWith(idBase)) {
                try {
                    String numStr = servicios[i][0].substring(idBase.length());
                    if (numStr.isEmpty()) {
                        maxContador = Math.max(maxContador, 1);
                    } else {
                        maxContador = Math.max(maxContador, Integer.parseInt(numStr));
                    }
                } catch (NumberFormatException e) {
                    // Ignorar si no es un número válido
                }
            }
        }

        // Si encontramos servicios con la misma base, incrementar el contador
        if (maxContador > 0) {
            return idBase + (maxContador + 1);
        }

        // Si no hay servicios con la misma base, usar la base sin número
        return idBase;
    }
    
    public void actualizarTabla(DefaultTableModel modelo) {
        modelo.setRowCount(0);
        for (int i = 0; i < tamaño; i++) {
            modelo.addRow(servicios[i]);
        }
    }
    
    public boolean esDiagnostico(int indice) {
        return indice >= 0 && indice < tamaño && servicios[indice][1].equals("Diagnóstico");
    }
    
    public void limpiarDatos() {
        capacidad = 10;
        servicios = new String[capacidad][7];
        tamaño = 0;
        contadorIds = new HashMap<>();

        // Agregar servicio de diagnóstico por defecto
        agregarServicio("Diagnóstico", "", "", "", 425);
        guardarEnArchivo();
    }
    
    public boolean cargarServiciosDesdeArchivo(File archivo, DefaultTableModel modeloTabla, RepuestoModelo repuestoModelo) {
        if (!archivo.getName().toLowerCase().endsWith(".tms")) {
            JOptionPane.showMessageDialog(null, "Solo se permiten archivos con extensión .tms", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean errorEnArchivo = false;
            int serviciosAgregados = 0;

            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) {
                    continue; // Ignorar líneas vacías
                }

                String[] partes = linea.split("-");
                if (partes.length != 5) {
                    errorEnArchivo = true;
                    continue;
                }

                try {
                    String nombre = partes[0].trim();
                    String marca = partes[1].trim();
                    String modeloStr = partes[2].trim();
                    String repuestosIds = partes[3].trim();
                    double manoObra = Double.parseDouble(partes[4].trim());

                    if (manoObra < 0) {
                        errorEnArchivo = true;
                        continue;
                    }

                    // Validar repuestos
                    StringBuilder repuestosBuilder = new StringBuilder();
                    boolean repuestosValidos = true;

                    String[] idsRepuestos = repuestosIds.split(";");
                    for (String id : idsRepuestos) {
                        id = id.trim();
                        if (id.isEmpty()) continue;

                        boolean repuestoEncontrado = false;
                        for (int i = 0; i < repuestoModelo.getTamaño(); i++) {
                            String[] repuesto = repuestoModelo.getRepuesto(i);
                            if (repuesto != null && repuesto.length > 0 && repuesto[0].equals(id)) {
                                // Verificar que marca y modelo coincidan
                                if (!repuesto[2].equalsIgnoreCase(marca) || !repuesto[3].equalsIgnoreCase(modeloStr)) {
                                    repuestosValidos = false;
                                    break;
                                }

                                if (repuestosBuilder.length() > 0) {
                                    repuestosBuilder.append(";");
                                }
                                repuestosBuilder.append(repuesto[1]).append("-")
                                             .append(repuesto[2]).append("-")
                                             .append(repuesto[3]).append("-")
                                             .append(repuesto[4]).append("-")
                                             .append(repuesto[5]);
                                repuestoEncontrado = true;
                                break;
                            }
                        }

                        if (!repuestoEncontrado || !repuestosValidos) {
                            repuestosValidos = false;
                            break;
                        }
                    }

                    if (!repuestosValidos) {
                        errorEnArchivo = true;
                        continue;
                    }

                    // Si todo está bien, agregar el servicio
                    agregarServicio(nombre, marca, modeloStr, repuestosBuilder.toString(), manoObra);
                    serviciosAgregados++;

                } catch (NumberFormatException e) {
                    errorEnArchivo = true;
                }
            }

            if (errorEnArchivo) {
                JOptionPane.showMessageDialog(null, 
                    "No se han agregado algunos servicios. Revise el archivo", 
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            }

            if (serviciosAgregados > 0) {
                actualizarTabla(modeloTabla);
                return true;
            } else {
                JOptionPane.showMessageDialog(null, 
                    "No se pudo agregar ningún servicio válido", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al leer el archivo", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}