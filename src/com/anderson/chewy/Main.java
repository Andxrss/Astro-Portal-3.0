package com.anderson.chewy;

import com.anderson.chewy.ui.Application;

import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Main {

    private final String VERSION = "3.0.0";

    public static void main(String[] args) {
        loadFonts();
        new Application();
    }

    private static void loadFonts() {
        GraphicsEnvironment GE = GraphicsEnvironment.getLocalGraphicsEnvironment();
        List<String> AVAILABLE_FONTS = Arrays.asList(GE.getAvailableFontFamilyNames());
        List<File> fontsToImport = Arrays.asList(
                new File("src/font/SF-Pro-Display-Bold.otf"),
                new File("src/font/SF-Pro-Display-Semibold.otf"));
        try {
            for (File fontFile : fontsToImport) {
                if (fontFile.exists()) {
                    Font font = Font.createFont(Font.TRUETYPE_FONT, fontFile);

                    if (!AVAILABLE_FONTS.contains(font.getFontName())) {
                        GE.registerFont(font);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}