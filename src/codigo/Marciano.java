/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codigo;

import java.awt.Image;
import javax.imageio.ImageIO;

/**
 *
 * @author chainick clase marciano propia no extiende a nada le podemos hacer
 * nuestros propios constructores
 *
 */
public class Marciano {
    
    public Image imagen1 = null;
    public Image imagen2 = null;
    public int posX=0;
    public int posY=0;
    //se utiliza pa que el marciano al moverse por la pantalla ,sepa cuanto tenga de ancho
    private int anchoPantalla;

    public int vida = 50;

    //lo unico que queremos que este constructor se inicialice con el valor que le pasamos 
    public Marciano(int _anchoPantalla) {
        anchoPantalla = _anchoPantalla;
    }
    public void mueve(boolean direccion){
        if(direccion){
            posX++;
           // posY=posY+2;
        }else{
            posX--;
           // posY=posY+2;
        }
    }

}
