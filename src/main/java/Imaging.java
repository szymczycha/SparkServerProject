import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Imaging {
    public static void flipVerticalImage(String path){
        File sourceFile = new File(path);
        BufferedImage originalImage = null;
        try {
            originalImage = ImageIO.read(sourceFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        BufferedImage targetImage = Scalr.rotate(originalImage, Scalr.Rotation.FLIP_HORZ);
        BufferedImage targetImage = Scalr.rotate(originalImage, Scalr.Rotation.FLIP_VERT);

        File targetFile = new File(path);
        try {
            ImageIO.write(targetImage, "jpg", targetFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        originalImage.flush();
        targetImage.flush();
    }
    public static void flipHorizontalImage(String path){
        File sourceFile = new File(path);
        BufferedImage originalImage = null;
        try {
            originalImage = ImageIO.read(sourceFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BufferedImage targetImage = Scalr.rotate(originalImage, Scalr.Rotation.FLIP_HORZ);
//        BufferedImage targetImage = Scalr.rotate(originalImage, Scalr.Rotation.FLIP_VERT);

        File targetFile = new File(path);
        try {
            ImageIO.write(targetImage, "jpg", targetFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        originalImage.flush();
        targetImage.flush();
    }
    public static void rotateImage(String path){
        File sourceFile = new File(path);
        System.out.println(sourceFile.getTotalSpace());
        BufferedImage originalImage = null;
        try {
            originalImage = ImageIO.read(sourceFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BufferedImage targetImage = Scalr.rotate(originalImage, Scalr.Rotation.CW_90);

        File targetFile = new File(path);
        try {
            ImageIO.write(targetImage, "jpg", targetFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        originalImage.flush();
        targetImage.flush();
        System.out.println("rotated");
    }
    public static void cropImage(String path, int width, int height, int x, int y){
        File sourceFile = new File(path);
        BufferedImage originalImage = null;
        try {
            originalImage = ImageIO.read(sourceFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BufferedImage targetImage = Scalr.crop(originalImage, x, y, width, height);
        File targetFile = new File(path);

        try {
            ImageIO.write(targetImage, "jpg", targetFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        originalImage.flush();
        targetImage.flush();
    }
}
