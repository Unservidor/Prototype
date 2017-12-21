package prototype;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Scanner;

/**
 *
 * @author Pablo Alonso
 */
public class NeuralNet {
    
    public static PerceptronMulticapa inicializePerceptron(int nEntradas, String name) throws FileNotFoundException {
        File saves = new File("config.txt");
        if (!saves.exists()) return null;
        int ciclosEfectuados = 0;
        int numeroNeuronasCapaOculta1 = 0;
        int numeroNeuronasCapaOculta2 = 0;
        int numeroNeuronasCapaOculta3 = 0;
        int numeroNeuronasSalida = 0;
        double[] pesosEntradasAPrimeraCapaOculta = new double[0];
        double[] pesosPrimeraCapaOcultaASegundaCapaOculta = new double[0];
        double[] pesosSegundaCapaOcultaATereceraCapaOculta = new double[0];
        double[] pesosTerceraCapaOcultaASalida = new double[0];
        double[] umbralesCapaOculta1 = new double[0];
        double[] umbralesCapaOculta2= new double[0];
        double[] umbralesCapaOculta3 = new double[0];
        double[] umbralesSalidas = new double[0];
        try (Scanner input = new Scanner(new BufferedReader(new FileReader(saves)))) {
            int cont = 0;
            boolean flag = false;
            while (input.hasNextLine()) {
                String linea = input.nextLine();
                String[] todo = linea.split("//");
                if (todo[0].contains("Person: "+ name)){
                    flag = true;
                    continue;
                }
                if(!flag) continue;
                
                switch (cont) {
                    case 0:
                        numeroNeuronasCapaOculta1 = Integer.parseInt(todo[0]);
                        numeroNeuronasCapaOculta2 = Integer.parseInt(todo[1]);
                        numeroNeuronasCapaOculta3 = Integer.parseInt(todo[2]);
                        numeroNeuronasSalida = Integer.parseInt(todo[3]);
                        pesosEntradasAPrimeraCapaOculta = new double[nEntradas*numeroNeuronasCapaOculta1];
                        pesosPrimeraCapaOcultaASegundaCapaOculta = new double[numeroNeuronasCapaOculta1*numeroNeuronasCapaOculta2];
                        pesosSegundaCapaOcultaATereceraCapaOculta = new double[numeroNeuronasCapaOculta2*numeroNeuronasCapaOculta3];
                        pesosTerceraCapaOcultaASalida = new double[numeroNeuronasCapaOculta3*numeroNeuronasSalida];
                        umbralesCapaOculta1 = new double[numeroNeuronasCapaOculta1];
                        umbralesCapaOculta2 = new double[numeroNeuronasCapaOculta2];
                        umbralesCapaOculta3 = new double[numeroNeuronasCapaOculta3];
                        umbralesSalidas = new double[numeroNeuronasSalida];
                        break;
                    case 1:
                        ciclosEfectuados = Integer.parseInt(todo[0]);
                        break;
                    case 2:
                        for(int i = 0; i< todo.length; i++){
                            pesosEntradasAPrimeraCapaOculta[i] = Double.parseDouble(todo[i]);
                        }
                        break;
                    case 3:
                        for(int i = 0; i< todo.length; i++){
                            pesosPrimeraCapaOcultaASegundaCapaOculta[i] = Double.parseDouble(todo[i]);
                        }
                        break;
                    case 4:
                        for(int i = 0; i< todo.length; i++){
                            pesosSegundaCapaOcultaATereceraCapaOculta[i] = Double.parseDouble(todo[i]);
                        }
                        break;
                    case 5:
                        for(int i = 0; i< todo.length; i++){
                            pesosTerceraCapaOcultaASalida[i] = Double.parseDouble(todo[i]);
                        }
                        break;
                    case 6:
                        for(int i = 0; i< todo.length; i++){
                            umbralesCapaOculta1[i] = Double.parseDouble(todo[i]);
                        }
                        break;
                    case 7:
                        for(int i = 0; i< todo.length; i++){
                            umbralesCapaOculta2[i] = Double.parseDouble(todo[i]);
                        }
                        break;
                    case 8:
                        for(int i = 0; i< todo.length; i++){
                            umbralesCapaOculta3[i] = Double.parseDouble(todo[i]);
                        }
                        break;
                    case 9:
                        for(int i = 0; i< todo.length; i++){
                            umbralesSalidas[i] = Double.parseDouble(todo[i]);
                        }
                        break;
                }
                cont++;
            }
        }
        return new PerceptronMulticapa(nEntradas, numeroNeuronasSalida, numeroNeuronasCapaOculta1, numeroNeuronasCapaOculta2,
                numeroNeuronasCapaOculta3, pesosEntradasAPrimeraCapaOculta, pesosPrimeraCapaOcultaASegundaCapaOculta, pesosSegundaCapaOcultaATereceraCapaOculta,
                pesosTerceraCapaOcultaASalida, umbralesCapaOculta1, umbralesCapaOculta2, umbralesCapaOculta3, umbralesSalidas);
    }
    
    public static void saveWeights(PerceptronMulticapa perceptron, String personName, int ciclosEfectuados) throws FileNotFoundException{
        PrintStream linea = new PrintStream(new FileOutputStream("config.txt", true));
        linea.append("Person: "+ personName +"\n");
        String aux = ""+perceptron.getNumeroDeNeuronasCapaOculta1()+"//"+perceptron.getNumeroDeNeuronasCapaOculta2()+"//"+perceptron.getNumeroDeNeuronasCapaOculta3()+"//"+perceptron.getNumeroDeNeuronasCapaDeSalida();
        linea.append(aux+"\n");
        linea.append(""+ciclosEfectuados+"\n");
        
        copyToFile(linea, perceptron.getPesosEntradaACapa1());
        copyToFile(linea, perceptron.getPesosCapa1ACapa2());
        copyToFile(linea, perceptron.getPesosCapa2ACapa3());
        copyToFile(linea, perceptron.getPesosCapa3ASalida());
        copyToFile(linea, perceptron.getUmbralesCapaOculta1());
        copyToFile(linea, perceptron.getUmbralesCapaOculta2());
        copyToFile(linea, perceptron.getUmbralesCapaOculta3());
        copyToFile(linea, perceptron.getUmbralesSalidas());
    }
    
    private static void copyToFile(PrintStream linea, double[] datos){
        String aux = ""+datos[0];
        for (int i = 1; i < datos.length; i++) {
            aux += "//"+datos[i];       
        }
        linea.append(aux+"\n");
    }
    
    public static void trainNetwork(PerceptronMulticapa perceptron, int numberOfCycles, double[][] imagesPixelMatrix, double[] labels) {
        for (int i = 0; i < numberOfCycles; i++) {
            for (int j = 0; j < imagesPixelMatrix.length; j++) {
                perceptron.setEntradasYSalidasEsperedas(imagesPixelMatrix[j], new double[]{labels[j]});
                perceptron.iniciarCiclo();
            }
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    private void readAndTrainNewData(String imageDataFolderPath) {
        File dataFolder = new File(imageDataFolderPath);
        if(dataFolder.isDirectory()){
            LinkedList<Double> trainingData;
            LinkedList<Double> testData;
        }else{
            System.out.println("ERROR INCORRECT NEW DATA PATH");
        }
    }
}
