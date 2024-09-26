package com.anderson.chewy.ui.component;

import javax.swing.*;
import java.awt.*;

public class Button extends JButton {

    private Enum value;
    private Color defaultColor;
    private Color toggleColor;
    private ImageIcon defaultIcon;

    public Button() {
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public Button(String text) {
        this.setText(text);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public Button(Enum value) {
        this.value = value;
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public Button(Enum value, String text) {
        super();
        this.value = value;
        this.setText(text);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public Enum getValue() {
        return value;
    }

    public void setValue(Enum value) {
        this.value = value;
    }

    public void setToggled(Boolean toggled) {
        if (toggled && toggleColor == null) {
            throw new IllegalStateException("Button does not have a toggle color");
        }
        super.setBackground(toggled ? toggleColor : defaultColor);
    }

    public void setBackground(Color color) {
        super.setBackground(color);
        defaultColor = color;
    }

    public void setToggleColor(Color color) {
        toggleColor = color;
    }

    public ImageIcon getDefaultIcon() {
        return defaultIcon;
    }
    public void setDefaultIcon(ImageIcon defaultIcon) {
        this.defaultIcon = defaultIcon;
        setIcon(defaultIcon);
    }
}

