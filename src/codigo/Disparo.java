package codigo;

import java.awt.Image;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author igorr objeto nave no se extiende a nada pero tiene su propio
 * constructor
 */
public class Disparo {
    Image disparoOriginal=null;
    Image imagenDisparo = null;
    public int posX = 0;
    public int posY = 0;
    Clip sonidoDisparo=null;
    
    public Disparo() {
        try {
            //en este caso la imagen se lee desde el disco duro no desde netebeans
            imagenDisparo = ImageIO.read(getClass().getResource("/imagenes/disparo.png"));
            sonidoDisparo=AudioSystem.getClip();
            sonidoDisparo.open(AudioSystem.getAudioInputStream(getClass().getResource("/sonidos/laser.wav")));
            
        } catch (Exception ex) {
           
        }
    }

    public void mueve() {
        posY=posY-3;
    }
    public void posicionADisparo(Nave _nave){
        posX = _nave.posX + _nave.imagenNave.getWidth(null)/2 - imagenDisparo.getWidth(null)/2;
        posY = _nave.posY -_nave.imagenNave.getHeight(null)/2;
        
    }
}
