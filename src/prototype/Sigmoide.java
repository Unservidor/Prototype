package prototype;

/**
 *
 * @author Pablo Alonso
 */
public class Sigmoide {
    
    public static double calcular(double x){
        return (1/(1+ Math.pow(Math.E, -x)));
    }
    
    public static double calcularDerivada(double x){
        double sigmoide = calcular(x);
        return sigmoide*(1-sigmoide);
    }
}
