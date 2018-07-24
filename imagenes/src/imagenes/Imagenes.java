/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagenes;

import com.sun.webkit.graphics.WCImage;
import static com.sun.webkit.graphics.WCImage.getImage;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.ImageIO;

import java.io.FileReader;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import static java.util.regex.Pattern.compile;
import jdk.nashorn.internal.objects.NativeArray;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

/**
 *
 * @author Florcita
 */
public class Imagenes {

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws Exception {
        Object obj = new JSONParser().parse(new FileReader("C:\\Users\\Florcita\\Documents\\NetBeansProjects\\imagenes\\src\\datos\\data.json"));
        JSONObject jo = (JSONObject) obj;
        //System.out.println(jo);        
        JSONArray fotos = (JSONArray) jo.get("photos");
        ArrayList<datos> infoComparar = PrimeraVerificacion(fotos);
        System.out.println("resultados");
        SegundaVerificacion(fotos, infoComparar);
//        for (Object fo : fotos) {
//            JSONObject f = (JSONObject) fo;
//            String url = (String) f.get("url");
//            Long w = (Long) f.get("width_small");
//            Long h = (Long) f.get("height_small");
//            WCImage img = getImage(new URL(url));
//            //System.out.println(url);
//           
//            //BufferedImage img1 = ImageIO.read(new URL(url));
//            //System.out.println(img1.getHeight());
//            //System.out.println(w);
//            //System.out.println(h);
//        }

        // TODO code application logic here
        //img1 = ImageIO.read(new File("C:\\Users\\Florcita\\Documents\\NetBeansProjects\\imagenes\\src\\imagenes\\fo.jpg"));
        //img2 = ImageIO.read(new File("C:\\Users\\Florcita\\Documents\\NetBeansProjects\\imagenes\\src\\imagenes\\fo1.jpg"));
        //double p = getDifferencePercent(img1, img2);
//        if (p > 0) {
//            System.out.println("Son imagenes diferentes");
//            System.out.println("Porcentaje de diferenca: " + p);
//        } else {
//            System.out.println("Son imagenes iguales");
//        }
    }

    private static ArrayList<datos> PrimeraVerificacion(JSONArray fotos) {
        //Definiendo que tipo de dato me aceptar la lista.
        ArrayList<datos> resp = new ArrayList();
        //Obtenemos el tamaño del objeto.
        System.out.println("Tamaño: " + fotos.size());
        int i, j, c = 0;
        for (i = 0; i < fotos.size() - 1; i++) {
            JSONObject datoi = (JSONObject) fotos.get(i);
            for (j = i + 1; j < fotos.size(); j++) {
                JSONObject datoj = (JSONObject) fotos.get(j);
                if (Objects.equals((Long) datoi.get("width_small"), (Long) datoj.get("width_small"))
                        && Objects.equals((Long) datoi.get("height_small"), (Long) datoj.get("height_small"))
                        && Objects.equals((Long) datoi.get("width_medium"), (Long) datoj.get("width_medium"))
                        && Objects.equals((Long) datoi.get("height_medium"), (Long) datoj.get("height_medium"))
                        && Objects.equals((Long) datoi.get("width_large"), (Long) datoj.get("width_large"))
                        && Objects.equals((Long) datoi.get("height_large"), (Long) datoj.get("height_large"))) {
                    c++;
                    //creamos una instancia de la clase datos.
                    datos dato = new datos();
                    //Asignando valores a variables de la clase instanciada.
                    //En el i guardamos la posición de la primera imagen
                    dato.i = i;
                    //En el j guardamos la posición de la segunda imagen
                    dato.j = j;
                    //Estamos agregando al arrray con info de tipo dato. que anteriormente lo creamos anteriomente.
                    resp.add(dato);
                    //System.out.println("Indice imagen 2: " + j);
                }
            }
        }
        System.out.println("Indice imagen 1: " + c);
        return resp;
    }

    private static ArrayList SegundaVerificacion(JSONArray fotos, ArrayList<datos> candidatos) throws IOException {
        //Este metodo comenzamos la segunda verificación.
        ArrayList resp = new ArrayList();
        //Hacemos el recorrido en el array de tipo datos que contienen todos los que cumplieron con la primera verificación
        for (datos indiceImagenes : candidatos) {
            //Estamos obteniendo el objeto del json donde vamos a realizar las comparaciones
            //El get indice imagenes i es la posicion de donde vamos a recuperar del json. para la posterior comparación
            JSONObject i1 = (JSONObject) fotos.get(indiceImagenes.i);
            //El get indice imagenes j es la posicion de donde vamos a recuperar del json. para la posterior comparación
            JSONObject i2 = (JSONObject) fotos.get(indiceImagenes.j);
            //Este try es para cuando no se pueda leer la imagen nos muestra un mensaje de error al leer la imagen.
            try {
                //Leemos la iamgen con el buffer para poder utilizar el metodo getDifferencePercent
                BufferedImage img1 = ImageIO.read(new URL((String) i1.get("url")));
                BufferedImage img2 = ImageIO.read(new URL((String) i2.get("url")));
                //Utilizamos el metodo para obtener el porcentaje de diferencia para asi analizar si es similar.
                double p = getDifferencePercent(img1, img2);
                //Comparamos el número d eprocentaje para ver si e similar o no.
                if (p < 5) {
                    System.out.println("URL Imagen 1: "+i1.get("url"));
                    System.out.println("URL Imagen 2: "+i2.get("url"));
                }
            } catch (Exception e) {
                System.out.println("Error al leer la imagen");
            }

        }
        return resp;
    }

    private static double getDifferencePercent(BufferedImage img1, BufferedImage img2) {
        int width = img1.getWidth();
        int height = img1.getHeight();
        int width2 = img2.getWidth();
        int height2 = img2.getHeight();
        if (width != width2 || height != height2) {
            throw new IllegalArgumentException(String.format("Images must have the same dimensions: (%d,%d) vs. (%d,%d)", width, height, width2, height2));
        }

        long diff = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                diff += pixelDiff(img1.getRGB(x, y), img2.getRGB(x, y));
            }
        }
        long maxDiff = 3L * 255 * width * height;

        return 100.0 * diff / maxDiff;
    }

    private static int pixelDiff(int rgb1, int rgb2) {
        int r1 = (rgb1 >> 16) & 0xff;
        int g1 = (rgb1 >> 8) & 0xff;
        int b1 = rgb1 & 0xff;
        int r2 = (rgb2 >> 16) & 0xff;
        int g2 = (rgb2 >> 8) & 0xff;
        int b2 = rgb2 & 0xff;
        return Math.abs(r1 - r2) + Math.abs(g1 - g2) + Math.abs(b1 - b2);
    }

    static class datos {

        public int i;
        public int j;
    }
}
