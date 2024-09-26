package com.anderson.chewy.ui.component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class TextBox extends JTextField {

    private Shape shape;
    private int radius;
    private boolean error;

    public TextBox(int radius) {
        super();
        setOpaque(false);
        this.radius = radius;
        this.error = false;
        setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(0, 18, 0, 18), BorderFactory.createEmptyBorder()));
    }

    protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRoundRect(0, 0, getWidth() - 1 , getHeight() - 1, radius, radius);
        super.paintComponent(g);
    }

    protected void paintBorder(Graphics g) {
        if (error) {
            g.setColor(Color.red);
        }
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
    }

    public boolean contains(int x, int y) {
        if (shape == null || !shape.getBounds().equals(getBounds())) {
            shape = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        }
        return shape.contains(x, y);
    }

    public void setError(boolean error) {
        this.error = error;
        repaint();
        revalidate();
    }
}
