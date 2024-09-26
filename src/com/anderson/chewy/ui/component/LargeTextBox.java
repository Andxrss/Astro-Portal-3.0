package com.anderson.chewy.ui.component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class LargeTextBox extends JTextArea {

    private Shape shape;
    private int radius;
    private boolean error;

    public LargeTextBox(int row, int column, int radius) {
        super(row, column);
        this.radius = radius;
        this.error = false;
        setOpaque(false);
        setLineWrap(true);
        setWrapStyleWord(true);
        setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(0, 18, 0, 18),
                BorderFactory.createEmptyBorder()));
    }

    public void setCornerRadius(int radius) {
        this.radius = radius;
        repaint();
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

    @Override
    public Insets getInsets() {
        int value = radius / 2;
        return new Insets(value, value, value, value);
    }
}
