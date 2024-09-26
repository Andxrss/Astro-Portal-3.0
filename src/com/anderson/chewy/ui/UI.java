package com.anderson.chewy.ui;

import com.anderson.chewy.Request;

import javax.swing.*;
import java.awt.*;
import java.util.Stack;

abstract class UI extends JFrame {

    private final Stack<Scene> scenes;
    private final CardLayout cardManager;
    private final JPanel cardsContainer;
    protected Request request;
    public boolean hasTicket = false;
    public boolean canMoveForward = true;

    abstract void build();
    abstract void initiateReset();

    public UI() {
        scenes = new Stack<>();
        cardsContainer = new JPanel(new CardLayout());
        cardManager = (CardLayout) cardsContainer.getLayout();
        cardsContainer.setSize(SceneBuilder.displaySize());
    }

    public JPanel getScenesContainer() {
        return cardsContainer;
    }

    public Scene getCurrentScene() {
        if (scenes.isEmpty()) {
            return null;
        }
        return scenes.peek();
    }

    public JPanel getCurrentPanel() {
        for (Component c: getScenesContainer().getComponents()) {
            if (c.isVisible()) {
                return (JPanel) c;
            }
        }
        return null;
    }

    public void exit() {
        if (scenes.isEmpty()) {
            return;
        }
        scenes.clear();
        scenes.push(Scene.MAIN_SCENE);
        cardManager.first(cardsContainer);
        canMoveForward = true;
    }

    public void back() {
        if (scenes.size() <= 1) {
            return;
        }
        scenes.pop();
        cardManager.show(cardsContainer, getCurrentScene().toString());
        canMoveForward = true;
    }

    public void addScene(Scene scene, JPanel panel) {
        if (panel.getParent() == cardsContainer) {
            throw new IllegalStateException("UI already contains a scene for " + scene.toString());
        }

        cardsContainer.add(panel, scene.toString());
    }

    public void setScene(Scene scene) {
        if (scene == null) {
            throw new NullPointerException("Scene cannot be null");
        }
        else if (scenes.contains(scene)) {
            throw new IllegalStateException(getCurrentScene() == scene
                    ? scene + " is currently being displayed" : scene + "has already been displayed");
        }

        scenes.push(scene);
        cardManager.show(cardsContainer, scene.toString());
    }

    public void next() {
        if (!canMoveForward) return;

        if (getCurrentScene() == Scene.MAIN_SCENE) {
            switch (request.getReason()) {
                case APPOINTMENT, PICKUP -> setScene(Scene.TICKET_INFO_SCENE);
                case SUPPORT -> setScene(Scene.TICKET_STATUS_SCENE);
                case RETURN -> setScene(Scene.EMAIL_SCENE);
            }
        }
        else if (getCurrentScene() == Scene.TICKET_STATUS_SCENE) {
            setScene(hasTicket ? Scene.TICKET_INFO_SCENE : Scene.EMAIL_SCENE);
        }
        else if (getCurrentScene() == Scene.TICKET_INFO_SCENE) {
            setScene(Scene.EMAIL_SCENE);
        }
        else if (getCurrentScene() == Scene.EMAIL_SCENE) {
            switch (request.getReason()) {
                case PICKUP -> setScene(Scene.EQUIPMENT_SCENE);
                case SUPPORT -> setScene(Scene.DESCRIPTION_SCENE);
                default -> setScene(Scene.END_SCENE);
            }
        }
        else if (getCurrentScene() == Scene.DESCRIPTION_SCENE
                || getCurrentScene() == Scene.EQUIPMENT_SCENE) {
            setScene(Scene.END_SCENE);
        }

        canMoveForward = false;
    }
}
