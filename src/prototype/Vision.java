package prototype;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.highgui.VideoCapture;
import org.opencv.objdetect.CascadeClassifier;

/**
 *
 * @author Pablo Alonso
 */
public class Vision extends JPanel implements Runnable{
    
    private BufferedImage image;
    private final Mat frame;
    private final CascadeClassifier faceDetector;
    private final MatOfRect faceDetections;
    private boolean recording;
    private int scan;
    
    public Vision(){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        VideoCapture camera = new VideoCapture(0);
        frame = new Mat();
        faceDetector = new CascadeClassifier("haarcascade_frontalface_alt.xml");
        faceDetections = new MatOfRect();
        recording = false;
        scan = 0;
        //Obtener el tamaño del JPanel, si se puede; sino se usa el por defecto
        camera.read(frame);        
        if(camera.isOpened()){
            image = MatToBufferedImage();
            setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        }else{
            image = new BufferedImage(frame.width(), frame.height(), 0);
            setPreferredSize(new Dimension(630, 480));
        }
        camera.release();
    }
    
    @Override
    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, this);
    }
    
    public boolean takeAPicture(boolean grayscale){
        VideoCapture camera = new VideoCapture(0);
        camera.read(frame); 
        
        if(!camera.isOpened()){
            System.out.println("Could not open the camera");
            return false;
        }else{
            if (camera.read(frame)){       
                image = MatToBufferedImage();
                if(grayscale){
                    image = grayscaleFilter(image);
                }
                savePicture(image);
            }
            camera.release();
            repaint();
            return true;
        }
    }
    
    public String recognize(String[] allNames){
        String personName = "";
        for (String name : allNames) {
            try {
                PerceptronMulticapa perceptron = NeuralNet.inicializePerceptron(22500, name);
                //true 1, false 0
                double[] input = getFaceSnapPixels();
                if(input == null) break;
                boolean correct = perceptron.probarRed(input, new double[]{1});
                if (correct) {
                    personName = name;
                    break;
                }
            } catch (FileNotFoundException ex) {}
        }
        return personName;
    }
    
    public void scan(int numberOfPictures){
        scan = numberOfPictures;
    }
    
    public void startRecording(){
        recording = true;
    }
    
    public void stopRecording(){
        recording = false;
    }
    
    @Override
    public void run() {
        VideoCapture camera = new VideoCapture(0);
        camera.read(frame);
        if(!camera.isOpened()){
            System.out.println("No se pudo abrir la camara");
        }else{
            image = MatToBufferedImage();
            while(recording){
                if (camera.read(frame)){
                    image = MatToBufferedImage();
                    repaint();
                }
            }
            int faceFolderNumber = 0;
            if(scan > 0){
                File facePicturesFolder = new File("Face Pictures Folder");
                if(!facePicturesFolder.exists()){
                    facePicturesFolder.mkdir();
                }else{
                    faceFolderNumber = facePicturesFolder.list().length;
                }
                File faceFolder = new File("Face Pictures Folder\\Face"+faceFolderNumber);
                faceFolder.mkdir();
            }
            LinkedList<BufferedImage> faceList = new LinkedList();
            while(scan > 0){
                if (camera.read(frame)){
                    image = MatToBufferedImage();
                    faceDetector.detectMultiScale(frame, faceDetections);
                    for (Rect rect : faceDetections.toArray()) {
                        //No hace falta poner el rectangulo en la imagen
                        //Core.rectangle(frame, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),new Scalar(0, 255, 0));
                        faceList.add(image.getSubimage(rect.x, rect.y, rect.width, rect.height));
                        scan--;
                    }
                }
            }
            if(!faceList.isEmpty())saveFaces(faceList, faceFolderNumber);
            camera.release();
        }
    }
    
    private BufferedImage MatToBufferedImage() {
        int type = 0;
        if (frame.channels() == 1) {
            type = BufferedImage.TYPE_BYTE_GRAY;
        } else if (frame.channels() == 3) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        image = new BufferedImage(frame.width(), frame.height(), type);
        WritableRaster raster = image.getRaster();
        DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
        byte[] data = dataBuffer.getData();
        frame.get(0, 0, data);

        return image;
    }
    
    private BufferedImage grayscaleFilter(BufferedImage img) {
        for (int i = 0; i < img.getHeight(); i++) {
            for (int j = 0; j < img.getWidth(); j++) {
                Color c = new Color(img.getRGB(j, i));

                int red = (int) (c.getRed() * 0.299);
                int green = (int) (c.getGreen() * 0.587);
                int blue = (int) (c.getBlue() * 0.114);

                Color newColor =
                        new Color(
                        red + green + blue,
                        red + green + blue,
                        red + green + blue);

                img.setRGB(j, i, newColor.getRGB());
            }
        }
        return img;
    }
    
    private void savePicture(BufferedImage img){
        File cameraPicturesFolder = new File("C:\\Users\\"+System.getProperty("user.name")+"\\Pictures\\PrototyePictures");
        if(!cameraPicturesFolder.exists()){
            cameraPicturesFolder.mkdir();
        }
        int numberOfPictures = cameraPicturesFolder.listFiles().length;
        File outputFile = new File("C:\\Users\\"+System.getProperty("user.name")+"\\Pictures\\PrototyePictures\\Picture"+numberOfPictures+".png");
        
        try {
            ImageIO.write(img, "png", outputFile);
        } catch (IOException ex) {}
    }
    
    private void saveFaces(LinkedList<BufferedImage> faceList, int faceFolderNumber){
        try {
            int pictureNumber = faceList.size();
            for (BufferedImage faceDetected : faceList) {
                File outputfile = new File("Face Pictures Folder\\Face"+faceFolderNumber+"\\"+pictureNumber+".png");
                //Cambiarle el tamaño
                Image tmp = faceDetected.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                
                BufferedImage dimg = new BufferedImage(150, 150, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = dimg.createGraphics();
                g2d.drawImage(tmp, 0, 0, null);
                g2d.dispose();
                ImageIO.write(dimg, "png", outputfile);
                pictureNumber--;
            }
        } catch (Exception e) {}
    }
    
    private double[] getFaceSnapPixels(){
        VideoCapture camera = new VideoCapture(0);
        camera.read(frame); 
        
        if(!camera.isOpened()) return null;
        
        double[] pixels = new double[22500];
        BufferedImage cropedImage = null;
        
        if (camera.read(frame)) {
            image = MatToBufferedImage();
            faceDetector.detectMultiScale(frame, faceDetections);
            for (Rect rect : faceDetections.toArray()) {
                cropedImage = image.getSubimage(rect.x, rect.y, rect.width, rect.height);
            }
            Image tmp = cropedImage.getScaledInstance(150, 150, Image.SCALE_SMOOTH);

            BufferedImage dimg = new BufferedImage(150, 150, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = dimg.createGraphics();
            g2d.drawImage(tmp, 0, 0, null);
            g2d.dispose();
            
            for (int i = 0; i < 150; i++) {
                for (int j = 0; j < 150; j++) {
                    pixels[i] = dimg.getRGB(i, j);
                }
            }
        }
        camera.release();
        repaint();

        return pixels;
    }
}
