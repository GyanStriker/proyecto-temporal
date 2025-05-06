package Vistas;

import javax.swing.ImageIcon;
import java.awt.Image;
import Controladores.MenuControlador;

public class MenuVista extends javax.swing.JFrame {

    public MenuVista() {
        initComponents();
        cargarImagen();
        new MenuControlador(this);
    }
    
    private void cargarImagen(){

        String rutaRepuesto = "C:/NetBeans Projects/USAC_TALLER/iconos/wrench.png";
        ImageIcon RepuestoIcon = new ImageIcon(rutaRepuesto);
        
        String rutaServicio = "C:/NetBeans Projects/USAC_TALLER/iconos/service.png";
        ImageIcon ServicioIcon = new ImageIcon(rutaServicio);

        String rutaClienteAuto = "C:/NetBeans Projects/USAC_TALLER/iconos/person.png";
        ImageIcon ClienteAutoIcon = new ImageIcon(rutaClienteAuto);
        
        String rutaProgreso = "C:/NetBeans Projects/USAC_TALLER/iconos/car.jpg";
        ImageIcon ProgresoIcon = new ImageIcon(rutaProgreso);
        
        String rutaReporte = "C:/NetBeans Projects/USAC_TALLER/iconos/file.png";
        ImageIcon ReporteIcon = new ImageIcon(rutaReporte);
        
        String rutaBitacora = "C:/NetBeans Projects/USAC_TALLER/iconos/list.png";
        ImageIcon BitacoraIcon = new ImageIcon(rutaBitacora);
        
        
        Image repuestoImagen = RepuestoIcon.getImage().getScaledInstance(
            ImageRepuesto.getWidth(),
            ImageRepuesto.getHeight(),
            Image.SCALE_SMOOTH);

        ImageRepuesto.setIcon(new ImageIcon(repuestoImagen));
        
        Image servicioImagen = ServicioIcon.getImage().getScaledInstance(
            ImageServicio.getWidth(),
            ImageServicio.getHeight(),
            Image.SCALE_SMOOTH);

        ImageServicio.setIcon(new ImageIcon(servicioImagen));
        
        Image clienteautoImagen = ClienteAutoIcon.getImage().getScaledInstance(
            ImageClienteAuto.getWidth(),
            ImageClienteAuto.getHeight(),
            Image.SCALE_SMOOTH);

        ImageClienteAuto.setIcon(new ImageIcon(clienteautoImagen));
        
        Image progresoImagen = ProgresoIcon.getImage().getScaledInstance(
            ImageProgreso.getWidth(),
            ImageProgreso.getHeight(),
            Image.SCALE_SMOOTH);

        ImageProgreso.setIcon(new ImageIcon(progresoImagen));
        
        Image reporteImagen = ReporteIcon.getImage().getScaledInstance(
            ImageReporte.getWidth(),
            ImageReporte.getHeight(),
            Image.SCALE_SMOOTH);

        ImageReporte.setIcon(new ImageIcon(reporteImagen));

        Image bitacoraImagen = BitacoraIcon.getImage().getScaledInstance(
            ImageBitacora.getWidth(),
            ImageBitacora.getHeight(),
            Image.SCALE_SMOOTH);

        ImageBitacora.setIcon(new ImageIcon(bitacoraImagen));
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        BotonCerrarSesion = new javax.swing.JButton();
        BotonRepuesto = new javax.swing.JButton();
        ImageRepuesto = new javax.swing.JLabel();
        ImageProgreso = new javax.swing.JLabel();
        ImageClienteAuto = new javax.swing.JLabel();
        ImageReporte = new javax.swing.JLabel();
        ImageServicio = new javax.swing.JLabel();
        BotonServicios = new javax.swing.JButton();
        BotonClienteAuto = new javax.swing.JButton();
        BotonProgreso = new javax.swing.JButton();
        BotonReporte = new javax.swing.JButton();
        BotonBitacora = new javax.swing.JButton();
        ImageBitacora = new javax.swing.JLabel();
        CleanReset = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        BotonCerrarSesion.setText("Cerrar Sesión");

        BotonRepuesto.setText("Repuestos");

        ImageRepuesto.setText("Repuesto");

        ImageProgreso.setText("Progreso");
        ImageProgreso.setPreferredSize(new java.awt.Dimension(40, 40));

        ImageClienteAuto.setText("ClienteAuto");
        ImageClienteAuto.setPreferredSize(new java.awt.Dimension(40, 40));

        ImageReporte.setText("Reporte");
        ImageReporte.setPreferredSize(new java.awt.Dimension(40, 40));

        ImageServicio.setText("Servicio");
        ImageServicio.setPreferredSize(new java.awt.Dimension(40, 40));

        BotonServicios.setText("Servicios");

        BotonClienteAuto.setText("<html><center>Clientes y<br>Automoviles</html>");

        BotonProgreso.setText("<html><center>Progreso<br>Autos</html>");

        BotonReporte.setText("Reportes");

        BotonBitacora.setText("Bitacora");

        ImageBitacora.setText("Bitacora");
        ImageBitacora.setPreferredSize(new java.awt.Dimension(40, 40));

        CleanReset.setText("Limpiar Todo");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(108, 108, 108)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(17, 17, 17)
                            .addComponent(ImageClienteAuto, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(115, 115, 115)
                            .addComponent(ImageProgreso, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(98, 98, 98)
                            .addComponent(ImageReporte, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addComponent(BotonClienteAuto, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(367, 367, 367))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(BotonServicios, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(BotonBitacora, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(BotonProgreso, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(55, 55, 55)
                                .addComponent(BotonReporte, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(BotonRepuesto, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(74, 74, 74))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(ImageRepuesto, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(84, 84, 84)))
                        .addGap(15, 15, 15)
                        .addComponent(ImageServicio, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(98, 98, 98)
                        .addComponent(ImageBitacora, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(122, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(BotonCerrarSesion, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
                    .addComponent(CleanReset, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(30, 30, 30))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(BotonCerrarSesion)
                .addGap(11, 11, 11)
                .addComponent(CleanReset)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ImageRepuesto, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ImageServicio, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ImageBitacora, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BotonRepuesto, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BotonServicios, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BotonBitacora, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ImageProgreso, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ImageClienteAuto, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(ImageReporte, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(BotonClienteAuto, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(BotonProgreso, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(BotonReporte, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(81, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MenuVista.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MenuVista.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MenuVista.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MenuVista.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MenuVista().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton BotonBitacora;
    public javax.swing.JButton BotonCerrarSesion;
    public javax.swing.JButton BotonClienteAuto;
    public javax.swing.JButton BotonProgreso;
    public javax.swing.JButton BotonReporte;
    public javax.swing.JButton BotonRepuesto;
    public javax.swing.JButton BotonServicios;
    public javax.swing.JButton CleanReset;
    public javax.swing.JLabel ImageBitacora;
    public javax.swing.JLabel ImageClienteAuto;
    public javax.swing.JLabel ImageProgreso;
    public javax.swing.JLabel ImageReporte;
    public javax.swing.JLabel ImageRepuesto;
    public javax.swing.JLabel ImageServicio;
    public javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
