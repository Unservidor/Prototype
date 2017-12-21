package prototype;

import java.util.Random;

/**
 *
 * @author Pablo Alonso
 */
public class PerceptronMulticapa {
    
    private final double[] entradas;
    private final double[] pesosEntradaACapa1;
    private final double[] capaOculta1;
    private final double[] umbralesCapaOculta1;
    private final double[] pesosCapa1ACapa2;
    private final double[] capaOculta2;
    private final double[] umbralesCapaOculta2;
    private final double[] pesosCapa2ACapa3;
    private final double[] capaOculta3;
    private final double[] umbralesCapaOculta3;
    private final double[] pesosCapa3ASalida;
    private final double[] salidas;
    private final double[] umbralesSalidas;
    private final double[] salidasEsperadas;
    private int numeroDeMuestras;
    private double errorCuadratico;
    private final double[] deltaNeuronasCapaDeSalida, deltaNeuronasCapaOculta3, deltaNeuronasCapaOculta2 , deltaNeuronasCapaOculta1;
    
    public PerceptronMulticapa(int nEntradas, int nSalidas){
        entradas = new double[nEntradas];        
        pesosEntradaACapa1 = new double[nEntradas*15];        
        capaOculta1 = new double[15];
        umbralesCapaOculta1 = new double[capaOculta1.length];
        pesosCapa1ACapa2 = new double[15*10];        
        capaOculta2 = new double[10];
        umbralesCapaOculta2 = new double[capaOculta2.length];
        pesosCapa2ACapa3 = new double[10*10];        
        capaOculta3 = new double[10];
        umbralesCapaOculta3 = new double[capaOculta3.length];
        pesosCapa3ASalida = new double[10*nSalidas];        
        salidas = new double[nSalidas];
        umbralesSalidas = new double[salidas.length];
        salidasEsperadas = new double[nSalidas];
        numeroDeMuestras = 0;
        errorCuadratico = 0;
        deltaNeuronasCapaDeSalida = new double[salidas.length];
        deltaNeuronasCapaOculta3 = new double[capaOculta3.length];
        deltaNeuronasCapaOculta2 = new double[capaOculta2.length];
        deltaNeuronasCapaOculta1 = new double[capaOculta1.length];
        inicializarPesos();
        inicializaUmbrales();        
    }
    
    public PerceptronMulticapa(int nEntradas, int nSalidas, int nNeuronas1Capa, int nNeuronas2Capa, int nNeuronas3Capa, double[] pesosEntradaACapa1,
        double[] pesosCapa1ACapa2, double[] pesosCapa2ACapa3, double[] pesosCapa3ASalida, double[] umbralesCapa1, double[] umbralesCapa2,
        double[] umbralesCapa3, double[] umbralesSalidas){
        entradas = new double[nEntradas];        
        this.pesosEntradaACapa1 = pesosEntradaACapa1;        
        capaOculta1 = new double[nNeuronas1Capa];        
        umbralesCapaOculta1 = umbralesCapa1;        
        this.pesosCapa1ACapa2 = pesosCapa1ACapa2;        
        capaOculta2 = new double[nNeuronas2Capa];        
        umbralesCapaOculta2 = umbralesCapa2;        
        this.pesosCapa2ACapa3 = pesosCapa2ACapa3;        
        capaOculta3 = new double[nNeuronas3Capa];        
        umbralesCapaOculta3 = umbralesCapa3;        
        this.pesosCapa3ASalida = pesosCapa3ASalida;        
        salidas = new double[nSalidas];        
        this.umbralesSalidas = umbralesSalidas;        
        salidasEsperadas = new double[nSalidas];
        numeroDeMuestras = 0;
        errorCuadratico = 0;
        deltaNeuronasCapaDeSalida = new double[salidas.length];
        deltaNeuronasCapaOculta3 = new double[capaOculta3.length];
        deltaNeuronasCapaOculta2 = new double[capaOculta2.length];
        deltaNeuronasCapaOculta1 = new double[capaOculta1.length];
    }
    
    /**
     *
     * @param entradas Tiene que ser el mismo numero de Entradas que se
     * especifico en la creacion del Perceptron
     * @param salidas Tiene que ser el mismo numero de Salidas que se
     * especifico en la creacion del Perceptron
     */
    public void setEntradasYSalidasEsperedas(double[] entradas, double[] salidas){
        System.arraycopy(entradas, 0, this.entradas, 0, this.entradas.length);
        System.arraycopy(salidas, 0, salidasEsperadas, 0, salidasEsperadas.length);
        numeroDeMuestras++;
    }
    
    public void iniciarCiclo(){
        propagarEntrada();
        calcularErrorCuadratico();
        calcularDeltaNeuronasCapaDeSalida();
        calcularDeltaNeuronasCapaOculta3();
        calcularDeltaNeuronasCapaOculta2();
        calcularDeltaNeuronasCapaOculta1();
        actualizarPesosYUmbrales();
    }

    private void inicializarPesos() {
        //Inicializamos los pesos de 0 a 1
        Random r = new Random();
        for (int i = 0; i < pesosEntradaACapa1.length; i++) {
            int signo = (r.nextInt(2) == 0)?-1:1;
            pesosEntradaACapa1[i] = r.nextDouble()*signo;            
        }
        for (int i = 0; i < pesosCapa1ACapa2.length; i++) {
            int signo = (r.nextInt(2) == 0)?-1:1;
            pesosCapa1ACapa2[i] = r.nextDouble()*signo;
        }
        for (int i = 0; i < pesosCapa2ACapa3.length; i++) {
            int signo = (r.nextInt(2) == 0)?-1:1;
            pesosCapa2ACapa3[i] = r.nextDouble()*signo;
        }
        for (int i = 0; i < pesosCapa3ASalida.length; i++) {
            int signo = (r.nextInt(2) == 0)?-1:1;
            pesosCapa3ASalida[i] = r.nextDouble()*signo;
        }
    }

    private void inicializaUmbrales() {
        for (int i = 0; i < umbralesCapaOculta1.length; i++) {
            umbralesCapaOculta1[i] = 1;            
        }
        for (int i = 0; i < umbralesCapaOculta2.length; i++) {
            umbralesCapaOculta2[i] = 1;            
        }
        for (int i = 0; i < umbralesCapaOculta3.length; i++) {
            umbralesCapaOculta3[i] = 1;            
        }
        for (int i = 0; i < umbralesSalidas.length; i++) {
            umbralesSalidas[i] = 1;            
        }
    }

    private void propagarEntrada() {
        /*
        * Cada entrada se enlaza con cada neurona de la primera capa oculta, por
        * lo que el i-esimo enlace de cada entrada se enlaza con la i-esima
        * neurona
        */
        for (int i = 0; i < capaOculta1.length; i++) {
            double x = 0;
            for(int j = 0; j<entradas.length; j++){
                x += pesosEntradaACapa1[i+capaOculta1.length*j]*entradas[j];
            }
            capaOculta1[i] = Sigmoide.calcular(x+umbralesCapaOculta1[i]);
        }
        for (int i = 0; i < capaOculta2.length; i++) {
            double x = 0;
            for(int j = 0; j<capaOculta1.length; j++){
                x += pesosCapa1ACapa2[i+capaOculta2.length*j]*capaOculta1[j];
            }
            capaOculta2[i] = Sigmoide.calcular(x+umbralesCapaOculta2[i]);
        }
        for (int i = 0; i < capaOculta3.length; i++) {
            double x = 0;
            for(int j = 0; j<capaOculta2.length; j++){
                x += pesosCapa2ACapa3[i+capaOculta3.length*j]*capaOculta2[j];
            }
            capaOculta3[i] = Sigmoide.calcular(x+umbralesCapaOculta3[i]);
        }
        for (int i = 0; i < salidas.length; i++) {
            double x = 0;
            for(int j = 0; j<capaOculta3.length; j++){
                x += pesosCapa3ASalida[i+salidas.length*j]*capaOculta3[j];
            }
            salidas[i] = Sigmoide.calcular(x+umbralesSalidas[i]);
        }
    }

    private void calcularErrorCuadratico() {
        double diferencia = 0;
        for (int i = 0; i < salidas.length; i++) {
            diferencia += (salidasEsperadas[i]-salidas[i])*(salidasEsperadas[i]-salidas[i]);
        }
        errorCuadratico += 0.5*diferencia;
    }
    
    /**
     * Cuando se llame a este metodo, la suma de los errores de cada muestra y el
     * numero de muestras de la red pasaran a valer 0, ya que se habra completad
     * un ciclo de entrenamiento
     * @return (El sumatorio de todos los errores de cada muestra)/numero de muestras
     */
    public double getErrorDeEntrenamiento(){
        double errorCuadraticoAux = errorCuadratico;
        int numeroDeMuestrasAux = numeroDeMuestras;
        errorCuadratico = 0;
        numeroDeMuestras = 0;        
        return errorCuadraticoAux/numeroDeMuestrasAux;
    }

    private void calcularDeltaNeuronasCapaDeSalida() {
        for (int i = 0; i < deltaNeuronasCapaDeSalida.length; i++) {
            double x = 0;
            for(int j = 0; j<capaOculta3.length; j++){
                x += pesosCapa3ASalida[i+salidas.length*j]*capaOculta3[j];
            }
            deltaNeuronasCapaDeSalida[i] = -(salidasEsperadas[i]-salidas[i])*Sigmoide.calcularDerivada(x+umbralesSalidas[i]);
        }
    }

    private void calcularDeltaNeuronasCapaOculta3() {
        int punteroPeso = 0;
        for (int i = 0; i < deltaNeuronasCapaOculta3.length; i++) {
            double x = 0;
            for(int j = 0; j<capaOculta2.length; j++){
                x += pesosCapa2ACapa3[i+capaOculta3.length*j]*capaOculta2[j];
            }
            double deltaXPeso = 0;
            for (int j = 0; j < deltaNeuronasCapaDeSalida.length; j++) {
                deltaXPeso += deltaNeuronasCapaDeSalida[j]*pesosCapa3ASalida[punteroPeso];
                punteroPeso++;
            }
            deltaNeuronasCapaOculta3[i] = Sigmoide.calcularDerivada(x+umbralesCapaOculta3[i])*deltaXPeso;
        }
    }

    private void calcularDeltaNeuronasCapaOculta2() {
        int punteroPeso = 0;
        for (int i = 0; i < deltaNeuronasCapaOculta2.length; i++) {
            double x = 0;
            for(int j = 0; j<capaOculta1.length; j++){
                x += pesosCapa1ACapa2[i+capaOculta2.length*j]*capaOculta1[j];
            }
            double deltaXPeso = 0;
            for (int j = 0; j < deltaNeuronasCapaOculta3.length; j++) {
                deltaXPeso += deltaNeuronasCapaOculta3[j]*pesosCapa2ACapa3[punteroPeso];
                punteroPeso++;
            }
            deltaNeuronasCapaOculta2[i] = Sigmoide.calcularDerivada(x+umbralesCapaOculta2[i])*deltaXPeso;
        }
    }

    private void calcularDeltaNeuronasCapaOculta1() {
        int punteroPeso = 0;
        for (int i = 0; i < deltaNeuronasCapaOculta1.length; i++) {
            double x = 0;
            for(int j = 0; j<entradas.length; j++){
                x += pesosEntradaACapa1[i+capaOculta1.length*j]*entradas[j];
            }
            double deltaXPeso = 0;
            for (int j = 0; j < deltaNeuronasCapaOculta2.length; j++) {
                deltaXPeso += deltaNeuronasCapaOculta2[j]*pesosCapa1ACapa2[punteroPeso];
                punteroPeso++;
            }
            deltaNeuronasCapaOculta1[i] = Sigmoide.calcularDerivada(x+umbralesCapaOculta1[i])*deltaXPeso;
        }
    }

    private void actualizarPesosYUmbrales() {
        //Mi constante alfa (tasa de aprendizaje) sera 0.1
        double L = 0.2;
        //Actualizando los pesos de los enlaces de la capa oculta 3 a las salidas
        for (int i = 0; i < pesosCapa3ASalida.length; i++) {
            pesosCapa3ASalida[i] = (pesosCapa3ASalida[i]- L*deltaNeuronasCapaDeSalida[i%salidas.length]* capaOculta3[i/salidas.length]);            
        }
        for (int i = 0; i < umbralesSalidas.length; i++) {
            umbralesSalidas[i] = (umbralesSalidas[i]- L*deltaNeuronasCapaDeSalida[i]);        
        }
        
        //Actualizando los pesos de los enlaces de la capa oculta 2 a las neuronas de la capa oculta 3 
        for (int i = 0; i < pesosCapa2ACapa3.length; i++) {
            pesosCapa2ACapa3[i] = (pesosCapa2ACapa3[i]- L*deltaNeuronasCapaOculta3[i%capaOculta3.length]* capaOculta2[i/capaOculta3.length]);            
        }
        for (int i = 0; i < umbralesCapaOculta3.length; i++) {
            umbralesCapaOculta3[i] = (umbralesCapaOculta3[i]- L*deltaNeuronasCapaOculta3[i]);        
        }
        
        //Actualizando los pesos de los enlaces de la capa oculta 1 a las neuronas de la capa oculta 2
        for (int i = 0; i < pesosCapa1ACapa2.length; i++) {
            pesosCapa1ACapa2[i] = (pesosCapa1ACapa2[i]- L*deltaNeuronasCapaOculta2[i%capaOculta2.length]* capaOculta1[i/capaOculta2.length]);            
        }
        for (int i = 0; i < umbralesCapaOculta2.length; i++) {
            umbralesCapaOculta2[i] = (umbralesCapaOculta2[i]- L*deltaNeuronasCapaOculta2[i]);        
        }
        
        //Actualizando los pesos de los enlaces de las entradas a las neuronas de la capa oculta 1
        for (int i = 0; i < pesosEntradaACapa1.length; i++) {
            pesosEntradaACapa1[i] = (pesosEntradaACapa1[i]- L*deltaNeuronasCapaOculta1[i%capaOculta1.length]* entradas[i/capaOculta1.length]);            
        }
        for (int i = 0; i < umbralesCapaOculta1.length; i++) {
            umbralesCapaOculta1[i] = (umbralesCapaOculta1[i]- L*deltaNeuronasCapaOculta1[i]);        
        }
    }
    
    public boolean probarRed(double[] entradas, double[] salidasEsperadas){
        this.setEntradasYSalidasEsperedas(entradas, salidasEsperadas);
        this.propagarEntrada();
        for (int i = 0; i < salidas.length; i++) {
            if(salidas[i]< (this.salidasEsperadas[i]-0.03) || salidas[i]> (this.salidasEsperadas[i]+0.03)){
                return false;
            }          
        }
        return true;
    }

    public double[] getPesosEntradaACapa1() {
        return pesosEntradaACapa1;
    }

    public double[] getUmbralesCapaOculta1() {
        return umbralesCapaOculta1;
    }

    public double[] getPesosCapa1ACapa2() {
        return pesosCapa1ACapa2;
    }

    public double[] getUmbralesCapaOculta2() {
        return umbralesCapaOculta2;
    }

    public double[] getPesosCapa2ACapa3() {
        return pesosCapa2ACapa3;
    }

    public double[] getUmbralesCapaOculta3() {
        return umbralesCapaOculta3;
    }

    public double[] getPesosCapa3ASalida() {
        return pesosCapa3ASalida;
    }

    public double[] getUmbralesSalidas() {
        return umbralesSalidas;
    }
    public int getNumeroDeNeuronasCapaOculta1(){
        return capaOculta1.length;
    }
    public int getNumeroDeNeuronasCapaOculta2(){
        return capaOculta2.length;
    }
    public int getNumeroDeNeuronasCapaOculta3(){
        return capaOculta3.length;
    }
    public int getNumeroDeNeuronasCapaDeSalida(){
        return salidas.length;
    }
}
