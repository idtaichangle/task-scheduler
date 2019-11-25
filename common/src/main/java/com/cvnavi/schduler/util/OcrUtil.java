package com.cvnavi.schduler.util;

import lombok.extern.log4j.Log4j2;
import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.LoadLibs;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Log4j2
public class OcrUtil {

    public static String doOcr(BufferedImage img) throws IOException {

        ITesseract te=new Tesseract();
        File tessDataFolder = LoadLibs.extractTessResources("tessdata");
        te.setDatapath(tessDataFolder.getAbsolutePath());
        te.setLanguage("eng");
        te.setTessVariable("tessedit_char_whitelist", "0123456789");
        te.setPageSegMode(ITessAPI.TessPageSegMode.PSM_SINGLE_LINE);

        try {
            String s=te.doOCR(img);
            return  s;
        } catch (TesseractException e) {
            e.printStackTrace();
        }
        return null;
    }
}
