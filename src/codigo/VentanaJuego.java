/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codigo;

import com.sun.glass.events.KeyEvent;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Timer;

/**
 *
 * @author jorgecisneros
 */
public class VentanaJuego extends javax.swing.JFrame {

    static int ANCHO_PANTALLA = 800;
    static int ALTO_PANTALLA = 600;
    int posY = 100;
    int contador = 0;
    int filasMarcianos = 5;
    int columnaMarcianos = 10;
    int separacionEntreMarcianoH = 15;
    int separacionEntreMarcianoV = 10;
    boolean direccionMarcianos = false;
    BufferedImage buffer = null;
    //buffer para guardar las imagenes de todos los marcianos
    BufferedImage plantilla = null;
    BufferedImage espacio = null;
    Image disparoOriginal;
    Image[] imagenes = new Image[30];

    //bucle animacion del juego 
    //en este caso , es un hilo de ejecucion nuevo que se encarga de refrescar la pantalla
    Timer temporizador = new Timer(10, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            //Todo:codigo de animacion
            bucleDelJuego();
        }
    });

    Marciano[][] listaMarciano = new Marciano[filasMarcianos][columnaMarcianos];

    Nave miNave = new Nave();
    Disparo miDisparo = new Disparo();
    ArrayList<Disparo> listaDisparos = new ArrayList();
    ArrayList<Explosion> listaExplosiones = new ArrayList();

    /**
     * Creates new form VentanaJuego
     */
    public VentanaJuego() {

        initComponents();
        try {
            plantilla = ImageIO.read(getClass().getResource("/imagenes/invaders2.png"));
        } catch (IOException ex) {
            Logger.getLogger(VentanaJuego.class.getName()).log(Level.SEVERE, null, ex);
        }
        //cargo las 30 imagenes del spritesheet en el array bufferedimage
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
                imagenes[i * 4 + j] = plantilla.getSubimage(j * 64, i * 64, 64, 64).
                        getScaledInstance(32, 32, Image.SCALE_SMOOTH);

            }
        }

        imagenes[20] = plantilla.getSubimage(0, 320, 66, 32);//sprite de la nave nueva
        imagenes[21] = plantilla.getSubimage(66, 320, 64, 32);//otra nave antigua
        imagenes[22] = plantilla.getSubimage(255, 320, 32, 32);//explosion de la nave
        imagenes[23] = plantilla.getSubimage(255, 289, 32, 32);//explsoion del marciano
        imagenes[24] = plantilla.getSubimage(194, 320, 64, 32); //3ª explosion
        imagenes[25] = plantilla.getSubimage(128, 256, 32, 32); //disparo original
        disparoOriginal = imagenes[25];

        setSize(ANCHO_PANTALLA, ALTO_PANTALLA);
        buffer = (BufferedImage) jPanel1.createImage(ANCHO_PANTALLA, ALTO_PANTALLA);
        buffer.createGraphics();
        temporizador.start();
        miNave.imagenNave = imagenes[20];
        miNave.posX = ANCHO_PANTALLA / 2 - miNave.imagenNave.getWidth(this) / 2;
        miNave.posY = ALTO_PANTALLA / 2 + 150;
        for (int i = 0; i < filasMarcianos; i++) {
            for (int j = 0; j < columnaMarcianos; j++) {
                listaMarciano[i][j] = new Marciano(ANCHO_PANTALLA);
                listaMarciano[i][j].imagen1 = imagenes[2 * i];
                listaMarciano[i][j].imagen2 = imagenes[2 * i + 1];
                listaMarciano[i][j].posX = j * (separacionEntreMarcianoH + listaMarciano[i][j].imagen1.getWidth(null));
                listaMarciano[i][j].posY = i * (separacionEntreMarcianoV + listaMarciano[i][j].imagen1.getHeight(null));
            }
        }
        //damos posicion al disparo
        miDisparo.posY = -2000;
    }

    private void pintaMarcianos(Graphics2D _g2) {
        for (int i = 0; i < filasMarcianos; i++) {
            for (int j = 0; j < columnaMarcianos; j++) {
                listaMarciano[i][j].mueve(direccionMarcianos);
                if (listaMarciano[i][j].posX >= ANCHO_PANTALLA - listaMarciano[i][j].imagen1.getWidth(null)
                        || listaMarciano[i][j].posX <= 0) {
                    direccionMarcianos = !direccionMarcianos;
                    //hago que todos los marcianos salten a la siguiente columna
                    for (int k = 0; k < filasMarcianos; k++) {
                        for (int m = 0; m < columnaMarcianos; m++) {
                            listaMarciano[k][m].posY = listaMarciano[k][m].posY + listaMarciano[k][m].imagen1.getHeight(null) / 4;
                        }
                    }

                }
//                if(listaMarciano[i][j].posX<=ANCHO_PANTALLA){
//                    direccionMarcianos=!direccionMarcianos; 
//                }     
                if (contador < 50) {
                    _g2.drawImage(listaMarciano[i][j].imagen1, listaMarciano[i][j].posX, listaMarciano[i][j].posY, null);
                } else if (contador < 100) {
                    _g2.drawImage(listaMarciano[i][j].imagen2, listaMarciano[i][j].posX, listaMarciano[i][j].posY, null);
                } else {
                    contador = 0;
                }

            }
        }
    }

    //pinta todos los disparos posibles
    private void pintaDisparos(Graphics2D g2) {
        Disparo auxiliar;
        for (int i = 0; i < listaDisparos.size(); i++) {
            auxiliar = listaDisparos.get(i);
            auxiliar.mueve();
            if (auxiliar.posY < 0) {
                listaDisparos.remove(i);
            } else {
                g2.drawImage(auxiliar.imagenDisparo, auxiliar.posX, auxiliar.posY, null);
            }

        }
    }

    private void pintaExplosiones(Graphics2D g2) {
        Explosion auxiliar;
        for (int i = 0; i < listaExplosiones.size(); i++) {
            auxiliar = listaExplosiones.get(i);
            auxiliar.tiempoDeVida--;
            if (auxiliar.tiempoDeVida < 60 && auxiliar.tiempoDeVida > 40) {
                g2.drawImage(auxiliar.imagenExplosion3, auxiliar.posX, auxiliar.posY, null);
            }
            if (auxiliar.tiempoDeVida > 20 && auxiliar.tiempoDeVida < 40) {
                g2.drawImage(auxiliar.imagenExplosion1, auxiliar.posX, auxiliar.posY, null);
            } else {
                g2.drawImage(auxiliar.imagenExplosion2, auxiliar.posX, auxiliar.posY, null);
            }
            //si tiempo de explosion es igual o menor a 0 eliminamos todo
            if (auxiliar.tiempoDeVida <= 0) {
                listaExplosiones.remove(i);
            }
        }
    }

    public void bucleDelJuego() {
        //este metodo gobierno en el redibujado de los bjetos en el jpanel1

        //primero borro todo
        Graphics2D g2 = (Graphics2D) buffer.getGraphics();
       
        g2.setColor(Color.black);
        try {
            espacio = ImageIO.read(getClass().getResource("/imagenes/fondo3.jpg"));
        } catch (IOException ex) {
            Logger.getLogger(VentanaJuego.class.getName()).log(Level.SEVERE, null, ex);
        }
         g2.drawImage(espacio, 0, 0,ANCHO_PANTALLA,ALTO_PANTALLA, null);

       // g2.fillRect(0, 0, ANCHO_PANTALLA, ALTO_PANTALLA);
        //
        contador++;
        //////////////////////////////////////////////////////
        pintaMarcianos(g2);

        //dibuja la nave
        g2.drawImage(miNave.imagenNave, miNave.posX, miNave.posY, null);
        pintaDisparos(g2);
        pintaExplosiones(g2);
        miNave.mueve();
        chequeColision();
        //////////////////////////////////////////////////////
        //dibujo de golpe tod el buffer sobre el jpanel 
        g2 = (Graphics2D) jPanel1.getGraphics();
        g2.drawImage(buffer, 0, 0, null);

        // System.out.println(contador);
    }

    //cheque si un disparo y un marciano colisionan
    private void chequeColision() {
        Rectangle2D.Double rectanguloMarciano = new Rectangle2D.Double();
        Rectangle2D.Double rectanguloDisparo = new Rectangle2D.Double();

        //calculo el rectangulo que contiene el disparo
        for (int k = 0; k < listaDisparos.size(); k++) {
            rectanguloDisparo.setFrame(listaDisparos.get(k).posX, listaDisparos.get(k).posY, listaDisparos.get(k).imagenDisparo.getWidth(null),
                    miDisparo.imagenDisparo.getHeight(null));

            //calculo el rectangulo correspondiente al marciano que estoy comprobando
            for (int i = 0; i < filasMarcianos; i++) {
                for (int j = 0; j < columnaMarcianos; j++) {

                    rectanguloMarciano.setFrame(listaMarciano[i][j].posX, listaMarciano[i][j].posY,
                            listaMarciano[i][j].imagen1.getWidth(null),
                            listaMarciano[i][j].imagen1.getHeight(null));
                    //interects es un metodo booleano,aqui no entra compilador hasta que no se chocan los dos rectangulos
                    if (rectanguloDisparo.intersects(rectanguloMarciano)) {
                        Explosion e = new Explosion();
                        e.posX = listaMarciano[i][j].posX;
                        e.posY = listaMarciano[i][j].posY;
                        e.imagenExplosion2 = imagenes[24];
                        e.imagenExplosion1 = imagenes[23];
                        e.imagenExplosion2 = imagenes[22];
                        listaExplosiones.add(e);
                        e.sonidoExplosion.start();//sonido ha sonadooooo                       
                        listaMarciano[i][j].posY = 2000;
                        listaDisparos.remove(k);

                    }
                }
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formKeyReleased(evt);
            }
        });

        jPanel1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jPanel1KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jPanel1KeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 677, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 659, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jPanel1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPanel1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel1KeyPressed

    private void jPanel1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPanel1KeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel1KeyReleased

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        switch (evt.getExtendedKeyCode()) {
            case KeyEvent.VK_LEFT:
                miNave.setNaveGiraIzquierda(true);
                break;
            case KeyEvent.VK_RIGHT:
                miNave.setNaveGiraDerecha(true);
                break;
            //aqui va disparotmb
            case KeyEvent.VK_SPACE:
                Disparo d = new Disparo();
                d.sonidoDisparo.start();
                d.posicionADisparo(miNave);
                //agregamos el disparo al arraylist
                listaDisparos.add(d);
                break;
        }
    }//GEN-LAST:event_formKeyPressed

    private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased
        switch (evt.getExtendedKeyCode()) {
            case KeyEvent.VK_LEFT:
                miNave.setNaveGiraIzquierda(false);
                break;
            case KeyEvent.VK_RIGHT:
                miNave.setNaveGiraDerecha(false);
                break;

        }
    }//GEN-LAST:event_formKeyReleased

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
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VentanaJuego().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
