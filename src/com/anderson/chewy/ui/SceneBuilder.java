package com.anderson.chewy.ui;

import com.anderson.chewy.Reason;
import com.anderson.chewy.ui.component.Button;
import com.anderson.chewy.ui.component.LargeTextBox;
import com.anderson.chewy.ui.component.TextBox;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Objects;

public class SceneBuilder {

    private final Color DARK_GRAY = Color.decode("#363636");
    private final Color LIGHT_GRAY = Color.decode("#C7C7C7");
    private final Color AZURE_BLUE = Color.decode("#007AFF");
    private final Color EMERALD_GREEN = Color.decode("#35C759");
    private final Font HEADER_FONT = new Font("SF Pro Display Bold", Font.BOLD, 68);
    private final Font TEXT_FONT = new Font("SF Pro Display Semibold", Font.PLAIN, 28);

    public static Dimension displaySize() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    public JLayeredPane buildNavigation(ActionListener actionListener) {
        final var EXIT_BUTTON_IMAGE = Objects.requireNonNull(getClass().getResource(
                "/assets/exit-button.png"));
        final var BACK_BUTTON_IMAGE =  Objects.requireNonNull(getClass().getResource(
                "/assets/back-button.png"));
        final var NEXT_BUTTON_IMAGE = Objects.requireNonNull(getClass().getResource(
                "/assets/next-button.png"));

        var pane = new JLayeredPane();
        pane.setLayout(null);
        pane.setBounds(0, 0, 1920, 1080);
        pane.setOpaque(false);
        pane.setVisible(true);

        Button button;
        button = new Button(Application.Navigation.EXIT);
        button.setIcon(new ImageIcon(EXIT_BUTTON_IMAGE));
        button.setBounds(14, 14, 27,27);
        button.setBorder(null);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.addActionListener(actionListener);
        pane.add(button);

        button = new Button(Application.Navigation.BACK);
        button.setIcon(new ImageIcon(BACK_BUTTON_IMAGE));
        button.setBounds(16, 518, 27,46);
        button.setBorder(null);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.addActionListener(actionListener);
        pane.add(button);

        button = new Button(Application.Navigation.NEXT);
        button.setIcon(new ImageIcon(NEXT_BUTTON_IMAGE));
        button.setBounds(1849, 0, 81,1080);
        button.setBorder(null);
        button.setBackground(AZURE_BLUE);
        button.setToggleColor(EMERALD_GREEN);
        button.setFocusPainted(false);
        button.addActionListener(actionListener);
        pane.add(button);

        return pane;
    }

    public JPanel buildMainScene(ActionListener actionListener) {
        final Reason[] REASONS = Reason.valuesPreferredOrder();
        var panel = panel();
        panel.setLayout(new GridBagLayout());

        var grid = new GridBagConstraints();
        grid.insets = new Insets(0, 0, 34, 0);
        grid.anchor = GridBagConstraints.CENTER;
        grid.gridx = grid.gridy = 0;
        grid.gridwidth = REASONS.length;

        var header = new JLabel("How can we help?");
        header.setFont(HEADER_FONT);
        panel.add(header, grid);

        grid.gridwidth = 1;
        grid.gridy++;

        for (Reason reason : REASONS) {
            final var ICON = new ImageIcon(Objects.requireNonNull(getClass().getResource(
                    "/assets/" + reason.toString().toLowerCase() + "-button.png")));

            Button button = new Button(reason);
            button.setIcon(ICON);
            button.setBorder(BorderFactory.createCompoundBorder(
                    new EmptyBorder(0, 24, 0, 0),
                    BorderFactory.createEmptyBorder()));
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.addActionListener(actionListener);

            panel.add(button, grid);

            grid.gridx++;
        }
        return panel;
    }

    public JPanel buildTicketStatusScene(ActionListener actionListener) {
        final var YES_BUTTON_ICON = new ImageIcon(Objects.requireNonNull(getClass().getResource(
                "/assets/yes-button.png")));
        final var NO_BUTTON_ICON = new ImageIcon(Objects.requireNonNull(getClass().getResource(
                "/assets/no-button.png")));

        var panel = panel();
        panel.setLayout(new GridBagLayout());

        var grid = new GridBagConstraints();
        grid.insets = new Insets(0, 0, 34, 0);
        grid.anchor = GridBagConstraints.CENTER;
        grid.gridx = grid.gridy = 0;
        grid.gridwidth = 2;

        var header = new JLabel("Do you have a ticket?");
        header.setFont(HEADER_FONT);
        panel.add(header, grid);

        grid.gridwidth = 1;
        grid.gridy++;

        Button button;
        button = new Button(Application.Navigation.YES);
        button.setIcon(YES_BUTTON_ICON);
        button.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(0, 48, 0, 0),
                BorderFactory.createEmptyBorder()));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.addActionListener(actionListener);
        panel.add(button, grid);
        grid.gridx++;

        button = new Button(Application.Navigation.NO);
        button.setIcon(NO_BUTTON_ICON);
        button.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(0, 48, 0, 0),
                BorderFactory.createEmptyBorder()));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.addActionListener(actionListener);
        panel.add(button, grid);

        return panel;
    }

    public JPanel buildTicketInfoScene(KeyListener keyListener) {
        var panel = panel();
        panel.setLayout(new GridBagLayout());

        var header = new JLabel("<html>Enter your ticket<br/>number</html>");
        header.setFont(HEADER_FONT);
        header.setForeground(DARK_GRAY);

        var grid = verticalGrid();
        panel.add(header, grid);
        panel.add(textBox(510, 58, keyListener), grid);

        return panel;
    }

    public JPanel buildEmailScene(KeyListener keyListener) {
        var panel = panel();
        panel.setLayout(new GridBagLayout());

        var grid = new GridBagConstraints();
        grid.insets = new Insets(0, 0, 34, 0);

        var header = new JLabel("Enter your email");
        header.setFont(HEADER_FONT);
        header.setForeground(DARK_GRAY);
        grid.gridx = grid.gridy = 0;
        grid.gridwidth = 2;
        panel.add(header, grid);

        var email = new JLabel();
        email.setText("@chewy.com");
        email.setFont(TEXT_FONT);
        grid.gridy = 1;
        grid.gridwidth = 1;
        grid.anchor = GridBagConstraints.WEST;
        panel.add(textBox(301, 58, keyListener), grid);

        grid.gridx = 1;
        grid.anchor = GridBagConstraints.EAST;
        panel.add(email, grid);

        return panel;
    }

    public JPanel buildDescriptionScene(KeyListener keyListener) {
        var panel = panel();
        panel.setLayout(new GridBagLayout());

        var header = new JLabel("<html>Please describe your <br/>support request</html>");
        header.setFont(HEADER_FONT);
        header.setForeground(DARK_GRAY);

        var grid = verticalGrid();
        panel.add(header, grid);
        panel.add(largeTextBox(3, 1, keyListener), grid);

        return panel;
    }

    public JPanel buildEquipmentScene(ActionListener actionListener) {
        final var DEFAULT_ICON = new ImageIcon(Objects.requireNonNull(getClass().getResource(
                "/assets/selection-button-unchecked.png")));
        final var PRESSED_ICON = new ImageIcon(Objects.requireNonNull(getClass().getResource(
                "/assets/selection-button-checked.png")));
        final String[] EQUIPMENT_LIST =
                {"KEYBOARD",
                "MOUSE",
                "HEADSET",
                "USB HUB",
                "MONITOR",
                "CHARGER",
                "CABLE / ADAPTER"};

        var panel = panel();
        panel.setLayout(new GridBagLayout());

        var grid = new GridBagConstraints();
        grid.insets = new Insets(0, 10, 34, 10);
        grid.anchor = GridBagConstraints.CENTER;
        grid.gridx = grid.gridy = 0;
        grid.gridwidth = 2;

        var header = new JLabel("Select your equipment");
        header.setFont(HEADER_FONT);
        header.setForeground(DARK_GRAY);
        panel.add(header, grid);

        grid.insets.bottom = 20;
        grid.gridwidth = 1;
        grid.gridy++;

        for (String item : EQUIPMENT_LIST) {
            Button button = new Button(Application.Equipment.ITEM, item);
            button.setBounds(616, 453, 341, 68);
            button.setFont(TEXT_FONT);
            button.setForeground(Color.WHITE);
            button.setDefaultIcon(DEFAULT_ICON);
            button.setHorizontalTextPosition(JButton.CENTER);
            button.setBorder(null);
            button.setFocusPainted(false);
            button.setContentAreaFilled(false);
            button.addActionListener(actionListener);
            button.addActionListener(e -> {
                button.setIcon(button.getIcon().equals(DEFAULT_ICON) ? PRESSED_ICON : DEFAULT_ICON);
                button.repaint();
            });
            panel.add(button, grid);

            grid.gridx++;
            if (grid.gridx == 2) {
                grid.gridx = 0;
                grid.gridy++;
            }
        }

        return panel;
    }
    // TODO: Add checkmark icon to End scene
    public JPanel buildEndScene() {
        var panel = panel();
        panel.setLayout(new GridBagLayout());

        //var checkmark = new JLabel();
        //checkmark.setIcon(CHECKMARK);

        var header = new JLabel("Request Complete", SwingConstants.CENTER);
        header.setFont(HEADER_FONT);
        header.setForeground(DARK_GRAY);

        var subhead = new JLabel("Please wait here for a technician", SwingConstants.CENTER);
        subhead.setFont(new Font("SF Pro Display Semibold", Font.PLAIN, 38));
        subhead.setForeground(DARK_GRAY);

        var grid = verticalGrid();
        //panel.add(checkmark, grid);
        panel.add(header, grid);
        panel.add(subhead, grid);

        return panel;
    }

    private GridBagConstraints verticalGrid() {
        var grid = new GridBagConstraints();
        grid.insets = new Insets(0, 0, 34, 0);
        grid.fill = GridBagConstraints.HORIZONTAL;
        grid.gridx = 0;
        return grid;
    }

    private TextBox textBox(int width, int height, KeyListener keyListener) {
        var textbox = new TextBox(10);
        textbox.setPreferredSize(new Dimension(width, height));
        textbox.setFont(TEXT_FONT);
        textbox.setCaretColor(AZURE_BLUE);
        textbox.setForeground(DARK_GRAY);
        textbox.setBackground(LIGHT_GRAY);
        textbox.addKeyListener(keyListener);
        return textbox;
    }

    private LargeTextBox largeTextBox(int row, int column, KeyListener keyListener) {
        var textbox = new LargeTextBox(row, column, 15);
        textbox.setPreferredSize(new Dimension(510, 174));
        textbox.setFont(TEXT_FONT);
        textbox.setCaretColor(AZURE_BLUE);
        textbox.setForeground(DARK_GRAY);
        textbox.setBackground(LIGHT_GRAY);
        textbox.addKeyListener(keyListener);
        return textbox;
    }

    private JPanel panel () {
        return new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image img = null;
                try {
                    img = ImageIO.read(
                            Objects.requireNonNull(getClass().getResource("/assets/background.png")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                g.drawImage(img, 0, 0, null);
            }
        };
    }
}
