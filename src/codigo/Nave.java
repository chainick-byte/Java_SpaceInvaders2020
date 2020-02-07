
package codigo;

import java.awt.Image;
import javax.imageio.ImageIO;

/**
 *
 * @author igorr
 * objeto nave no se extiende a nada pero tiene su propio constructor
 */
public class Nave {
    Image imagenNave=null;
    public int posX=0;
    public int posY=0;
    
    private boolean naveGiraDerecha=false;
    private boolean naveGiraIzquierda=false;
    public Nave(){
         try {
            //en este caso la imagen se lee desde el disco duro no desde netebeans
            imagenNave = ImageIO.read(getClass().getResource("/imagenes/nave.png"));
           
        } catch (Exception e) {
    }
}
    public void mueve(){
        if(naveGiraIzquierda && posX>0){
            posX--;
        }
        if(naveGiraDerecha && posY<VentanaJuego.ANCHO_PANTALLA - imagenNave.getWidth(null)){
            posX++;
        }
    }
//consecuencia del bean devuelve 4 metodos, 2 setters y 2 getters
    public boolean isNaveGiraDerecha() {
        return naveGiraDerecha;
    }

    public void setNaveGiraDerecha(boolean naveGiraDerecha) {
       
        this.naveGiraDerecha = naveGiraDerecha;
        this.naveGiraIzquierda=false;
    }

    public boolean isNaveGiraIzquierda() {
        return naveGiraIzquierda;
    }

    public void setNaveGiraIzquierda(boolean naveGiraIzquierda) {
        this.naveGiraIzquierda = naveGiraIzquierda;
        this.naveGiraDerecha=false;
    }
}
