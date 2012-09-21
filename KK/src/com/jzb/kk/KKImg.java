/**
 * 
 */
package com.jzb.kk;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGImageReadParam;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import com.sun.media.jai.codec.TIFFEncodeParam;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageEncoder;
import javax.media.jai.RenderedOp;
import javax.media.jai.JAI;

/**
 * @author jzarzuela
 * 
 */
public class KKImg {

    /**
     * Static Main starting method
     * 
     * @param args
     *            command line parameters
     */
    public static void main(String[] args) {
        try {
            long t1, t2;
            System.out.println("***** TEST STARTED *****");
            KKImg me = new KKImg();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** TEST FINISHED [" + (t2 - t1) + "]*****");
            System.exit(1);
        } catch (Throwable th) {
            System.out.println("***** TEST FAILED *****");
            th.printStackTrace(System.out);
            System.exit(-1);
        }
    }

    public void doIt(String[] args) throws Exception {
        convertToTiff("/Users/jzarzuela/Desktop/bot_01.TIF.tiff", "/Users/jzarzuela/Desktop/bot_01_01_x.tiff");
    }

    @SuppressWarnings("restriction")
    public static void convertToTiff(String inputFile, String outputFile) throws Exception {
        
        RenderedOp src = JAI.create("fileload", inputFile);
        BufferedImage inputImage = src.getAsBufferedImage();
        BufferedImage dst=processImage(inputImage);
            
        File outFile = new File(outputFile);
        OutputStream ios = new BufferedOutputStream(new FileOutputStream(outFile));

        TIFFEncodeParam param = new TIFFEncodeParam();
        param.setCompression(TIFFEncodeParam.COMPRESSION_GROUP4);
        param.setLittleEndian(false); // Intel

        ImageEncoder enc = ImageCodec.createImageEncoder("tiff", ios, param);
        enc.encode(dst);

        ios.close();

    }

    @SuppressWarnings("restriction")
    public static void convertToTiff2(String inputFile, String outputFile) {
        try {

            RenderedOp src = JAI.create("fileload", inputFile);

            BufferedImage dst = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
            ColorConvertOp filterObj = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
            filterObj.filter(src.getAsBufferedImage(), dst);

            File outFile = new File(outputFile);
            OutputStream ios = new BufferedOutputStream(new FileOutputStream(outFile));

            TIFFEncodeParam param = new TIFFEncodeParam();
            param.setCompression(TIFFEncodeParam.COMPRESSION_GROUP4);
            param.setLittleEndian(false); // Intel

            ImageEncoder enc = ImageCodec.createImageEncoder("tiff", ios, param);
            enc.encode(dst);

            ios.close();

        } catch (Exception e) {
            System.out.println("Failed to create output tiff file.");
            e.printStackTrace(System.out);
        }
    }

    public static BufferedImage processImage(BufferedImage inputImage) {

        // Create a binary image for the results of processing

        int w = inputImage.getWidth();
        int h = inputImage.getHeight();
        BufferedImage outputImage = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);

        // Work on a copy of input image because it is modified by diffusion

        WritableRaster input = inputImage.copyData(null);
        WritableRaster output = outputImage.getRaster();

        final int threshold = 128;
        float value, error;

        for (int y = 0; y < h; ++y)
            for (int x = 0; x < w; ++x) {

                value = input.getSample(x, y, 0);

                // Threshold value and compute error

                if (value < threshold) {
                    output.setSample(x, y, 0, 0);
                    error = value;
                } else {
                    output.setSample(x, y, 0, 1);
                    error = value - 255;
                }

                // Spread error amongst neighbouring pixels
/*
                if ((x > 0) && (y > 0) && (x < (w - 1)) && (y < (h - 1))) {
                    value = input.getSample(x + 1, y, 0);
                    input.setSample(x + 1, y, 0, clamp(value + 0.4375f * error));
                    value = input.getSample(x - 1, y + 1, 0);
                    input.setSample(x - 1, y + 1, 0, clamp(value + 0.1875f * error));
                    value = input.getSample(x, y + 1, 0);
                    input.setSample(x, y + 1, 0, clamp(value + 0.3125f * error));
                    value = input.getSample(x + 1, y + 1, 0);
                    input.setSample(x + 1, y + 1, 0, clamp(value + 0.0625f * error));
                }
*/
            }
        return outputImage;

    }

    // Forces a value to a 0-255 integer range

    public static int clamp(float value) {
        return Math.min(Math.max(Math.round(value), 0), 255);
    }

    /*
     * public Boolean JPEGtoTIFF(String InputimgFile, String OutputFileName) throws IOException { File imgFile = new File(InputimgFile); InputStream fis = new BufferedInputStream(new
     * FileInputStream(imgFile));
     * 
     * 
     * com.sun.imageio.plugins.jpeg.JPEGImageReaderSpi spi = new com.sun.imageio.plugins.jpeg.JPEGImageReaderSpi(); ImageReader rdr = spi.createReaderInstance(); ImageInputStream iis =
     * ImageIO.createImageInputStream(fis);
     * 
     * rdr.setInput(iis, true); JPEGImageReadParam rdparam = new JPEGImageReadParam(); BufferedImage bi = rdr.read(0, rdparam);
     * 
     * TIFFImageWriterSpi tiffspi = new TIFFImageWriterSpi(); ImageWriter wtr = tiffspi.createWriterInstance();
     * 
     * File newTIFF = new File(OutputFileName + ".tif");
     * 
     * ImageOutputStream ios = ImageIO.createImageOutputStream(newTIFF); wtr.setOutput(ios);
     * 
     * // TIFFImageWriteParam tiffWriteParam = new TIFFImageWriteParam(Locale.US); ImageWriteParam tiffWriteParam = wtr.getDefaultWriteParam();
     * 
     * // String [] CompTypes = tiffWriteParam.getCompressionTypes(); // for(int j=0; j System.out.println(CompTypes[j]);
     * 
     * tiffWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT); tiffWriteParam.setCompressionType("LZW"); tiffWriteParam.setCompressionQuality(0.5f);
     * 
     * try { int i = 0;
     * 
     * IIOMetadata iioImageMetadata = rdr.getImageMetadata(i);
     * 
     * int imageHeight = rdr.getHeight(i); int imageWidth = rdr.getWidth(i);
     * 
     * tiffWriteParam.setTilingMode(ImageWriteParam.MODE_EXPLICIT); tiffWriteParam.setTiling(imageWidth, imageHeight, 0, 0);
     * 
     * BufferedImage buffimg = new BufferedImage(imageWidth, imageHeight, bi.TYPE_BYTE_BINARY);
     * 
     * IIOImage image = new IIOImage(bi, null, iioImageMetadata);
     * 
     * try { wtr.write(null, image, tiffWriteParam); } catch (Exception e) { System.out.println(e.toString()); } wtr.dispose();
     * 
     * } catch (IndexOutOfBoundsException e) { rdr.dispose(); }
     * 
     * // Done writing all images for this image rdr.dispose(); }
     */
}
