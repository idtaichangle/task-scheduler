package com.cvnavi.scheduler.util;

import lombok.extern.log4j.Log4j2;
import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.LoadLibs;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Log4j2
public class OcrUtil {

    public static String doOcr(BufferedImage img) throws IOException {
        img=optimizeImg(img);
        ITesseract te=new Tesseract();
        File tessDataFolder = LoadLibs.extractTessResources("tessdata");
        te.setDatapath(tessDataFolder.getAbsolutePath());
        te.setLanguage("eng");
        te.setTessVariable("tessedit_char_whitelist", "0123456789");
        te.setPageSegMode(ITessAPI.TessPageSegMode.PSM_SINGLE_LINE);

        try {
            String s=te.doOCR(img);
            log.info("ocr result:"+s);
            return  s;
        } catch (TesseractException e) {
            log.error(e.getMessage(),e);
        }
        return null;
    }

    public static BufferedImage optimizeImg(BufferedImage src){

        BufferedImage sub=new BufferedImage(src.getWidth(),src.getHeight(),src.getType());

        for(int x=0;x<sub.getWidth();x++){
            for(int y=0;y<sub.getHeight();y++){
                int r=getRed(src.getRGB(x,y));
                int g=getGreen(src.getRGB(x,y));
                int b=getBlue(src.getRGB(x,y));
                if(r>240 && g>240 && b>240){
                    sub.setRGB(x,y,0xFFFFFF);
                }else{
                    sub.setRGB(x,y,src.getRGB(x,y));
                }
            }
        }

        return sub;
    }

    public static void fillBlue(BufferedImage sub){
        for(int x=1;x<sub.getWidth()-1;x++){
            for(int y=1;y<sub.getHeight()-1;y++){

                if(sub.getRGB(x,y)!=-1&&
                        almostBlackOrBlue(sub.getRGB(x,y-1))&&
                        almostBlackOrBlue(sub.getRGB(x,y+1))&&
                        !almostBlue(sub.getRGB(x,y))){
                    sub.setRGB(x,y,sub.getRGB(x,y)&0xFF0000FF);
                }

                if(sub.getRGB(x,y)!=-1&&
                        almostBlackOrBlue(sub.getRGB(x-1,y))&&
                        almostBlackOrBlue(sub.getRGB(x+1,y))&&
                        !almostBlue(sub.getRGB(x,y))){
                    sub.setRGB(x,y,sub.getRGB(x,y)&0xFF0000FF);
                }
            }
        }
    }


    public static int getRed(int rgb){
        return (rgb&0xFF0000)>>16;
    }

    public  static int getGreen(int rgb){
        return (rgb&0xFF00)>>8;
    }

    public  static int getBlue(int rgb){
        return (rgb&0xFF);
    }

    public static boolean almostBlue(int rgb){
        int r=getRed(rgb);
        int g=getGreen(rgb);
        int b=getBlue(rgb);
        return b>80&&((r/(float)b)<0.2)&&((g/(float)b)<0.2);
    }

    public static boolean almostBlack(int rgb){
        int r=getRed(rgb);
        int g=getGreen(rgb);
        int b=getBlue(rgb);
        return r<70&&g<70&b<70;
    }

    public static boolean almostBlackOrBlue(int rgb){
        return almostBlue(rgb)||almostBlack(rgb);
    }
}
