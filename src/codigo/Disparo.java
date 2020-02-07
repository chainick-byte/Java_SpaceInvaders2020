package codigo;

import java.awt.Image;
import javax.imageio.ImageIO;

/**
 *
 * @author igorr objeto nave no se extiende a nada pero tiene su propio
 * constructor
 */
public class Disparo {

    Image imagenDisparo = null;
    public int posX = 0;
    public int posY = 0;
    
    public Disparo() {
        try {
            //en este caso la imagen se lee desde el disco duro no desde netebeans
            imagenDisparo = ImageIO.read(getClass().getResource("/imagenes/disparo.png"));

        } catch (Exception e) {
        }
    }

    public void mueve() {
        posY--;
    }
    public void posicionADisparo(Nave _nave){
        posX = _nave.posX + _nave.imagenNave.getWidth(null)/2 - imagenDisparo.getWidth(null)/2;
        posY = _nave.posY -_nave.imagenNave.getHeight(null)/2;
        
    }
}
