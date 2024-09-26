package com.anderson.chewy.ui;

import com.anderson.chewy.Reason;
import com.anderson.chewy.Request;
import com.anderson.chewy.ui.component.Button;
import com.anderson.chewy.ui.component.LargeTextBox;
import com.anderson.chewy.ui.component.TextBox;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;

public class Application extends UI implements ActionListener, KeyListener {

    public enum Navigation { BACK, EXIT, NEXT, NO, YES }
    public enum Equipment { ITEM }

    private final InactivityManager inactivityManager;

    private Button nextButton;
    private Button backButton;
    private Button exitButton;
    private TextBox emailField;
    private TextBox ticketField;
    private LargeTextBox descriptionField;
    private boolean canSubmit = false;

    public Application() {
        super();
        build();
        hideNavigation();
        setScene(Scene.MAIN_SCENE);
        inactivityManager = new InactivityManager(this);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof Button button) {
            switch (button.getValue()) {
                case Reason.APPOINTMENT,
                     Reason.PICKUP,
                     Reason.SUPPORT,
                     Reason.RETURN -> {
                    request = new Request((Reason) button.getValue());
                    launchForm();
                    requestFocus();
                }
                case Navigation.EXIT -> reset();
                case Navigation.BACK -> handleBackAction();
                case Equipment.ITEM -> {
                    handleItemSelection(button);
                    toggleNextButton();
                }
                case Navigation.NEXT -> handleNextAction();
                case Navigation.NO, Navigation.YES -> {
                    hasTicket = button.getValue() == Navigation.YES;
                    canMoveForward = true;
                    handleNextAction();
                }
                default -> throw new IllegalStateException("Unexpected button value: " + button.getValue());
            }
        }
    }

    private void handleBackAction() {
        clearInput();
        back();

        if (getCurrentScene() == Scene.MAIN_SCENE) {
            reset();
        } else {
            toggleNextButton();
        }
    }

    private void handleNextAction() {
        if (!appendUserInputToRequest()
                || getCurrentScene() == Scene.MAIN_SCENE) {
            showError(true);
            return;
        }

        next();

        if (canSubmit) {
            request.submit();
            initiateReset();
            hideNavigation();
        } else {
            showError(false);
            requestFocus();
            toggleNextButton();
            revalidate();
            repaint();
        }
    }

    private void handleItemSelection(Button itemButton) {
        String item = itemButton.getText();

        if (request.getEquipment().contains(item)) {
            request.removeEquipment(item);
        } else {
            request.addEquipment(item);
        }
    }
    /* Append text information to the request if the inputs are valid */
    private boolean appendUserInputToRequest() {
        var scene = getCurrentScene();

        if (scene == Scene.TICKET_INFO_SCENE) {
            return request.setTicket(ticketField.getText().toUpperCase().trim());
        }
        else if (scene == Scene.EMAIL_SCENE) {
            return request.setEmail(emailField.getText().toLowerCase().trim() + "@chewy.com");
        }
        else if (scene == Scene.DESCRIPTION_SCENE) {
            var text = descriptionField.getText().trim();
            request.addDescription(text.substring(0, 1).toUpperCase() + text.substring(1));
            return true;
        }

        return true;
    }

    private void showError(boolean showError) {
        if (getCurrentScene() == Scene.TICKET_INFO_SCENE) {
            ticketField.setError(showError);
        }
        else if (getCurrentScene() == Scene.EMAIL_SCENE) {
            emailField.setError(showError);
        }
    }

    private void showNavigation() {
        exitButton.setEnabled(true);
        exitButton.setVisible(true);
        backButton.setEnabled(true);
        backButton.setVisible(true);
        revalidate();
        repaint();
        /* Next button is conditionally displayed based on user input in
        the 'keyReleased' and 'actionPerformed' events */
    }

    private void hideNavigation() {
        exitButton.setEnabled(false);
        exitButton.setVisible(false);
        backButton.setEnabled(false);
        backButton.setVisible(false);
        nextButton.setEnabled(false);
        nextButton.setVisible(false);
        revalidate();
        repaint();
    }

    private void toggleNextButton() {
        var scene = getCurrentScene();
        canMoveForward = canProceed();
        canSubmit = switch (scene) {
            case DESCRIPTION_SCENE, EQUIPMENT_SCENE -> canMoveForward;
            case EMAIL_SCENE -> canMoveForward
                    && (request.getReason() == Reason.APPOINTMENT || request.getReason() == Reason.RETURN);
            default -> false;
        };

        if (canMoveForward && !nextButton.isEnabled()) {
            if (scene == Scene.MAIN_SCENE || scene == Scene.TICKET_STATUS_SCENE) {
                return;
            }

            nextButton.setEnabled(true);
            nextButton.setVisible(true);
            nextButton.setToggled(canSubmit);
            revalidate();
            repaint();
        }
        else if (!canMoveForward && nextButton.isEnabled()) {
            nextButton.setEnabled(false);
            nextButton.setVisible(false);
            revalidate();
            repaint();
        }
    }

    public void keyTyped(KeyEvent e) {
        if (fieldExceedsLimit() || e.getKeyChar() == ' ' && !e.getSource().equals(descriptionField)) {
            e.consume();
        }
    }

    public void keyPressed(KeyEvent e) {
        var scene = getCurrentScene();
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE && scene != Scene.MAIN_SCENE) {
            reset();
        }
        else if (e.getKeyCode() == KeyEvent.VK_ENTER &&
                (scene == Scene.TICKET_INFO_SCENE
                || scene == Scene.EMAIL_SCENE
                || scene == Scene.DESCRIPTION_SCENE)) {

            handleNextAction();
        }
    }

    public void keyReleased(KeyEvent e) {
        toggleNextButton();
    }

    private boolean canProceed() {
        return switch (getCurrentScene()) {
            case MAIN_SCENE, TICKET_STATUS_SCENE -> true;
            case TICKET_INFO_SCENE ->
                    ticketField.getText().length() >= Request.MIN_TICKET_LENGTH;
            case EMAIL_SCENE ->
                    emailField.getText().length() >= Request.MIN_EMAIL_LENGTH;
            case DESCRIPTION_SCENE ->
                    descriptionField.getText().length() >= Request.MIN_DESCRIPTION_LENGTH;
            case EQUIPMENT_SCENE ->
                    !request.getEquipment().isEmpty();
            default -> false;
        };
    }

    private boolean fieldExceedsLimit() {
        if (getCurrentScene() == Scene.TICKET_INFO_SCENE) {
            return ticketField.getText().length() >= Request.MAX_TICKET_LENGTH;
        }
        else if (getCurrentScene() == Scene.DESCRIPTION_SCENE) {
            return descriptionField.getText().length() >= Request.MAX_DESCRIPTION_LENGTH;
        }
        return false;
    }
    @Override
    public void initiateReset() {
        var restartDelay = 8 * 1000;
        var timer = new Timer(restartDelay, e -> reset());
        timer.setRepeats(false);
        timer.start();
    }

    private void clearInput() {
        var scene = getCurrentScene();

        if (scene == Scene.TICKET_INFO_SCENE) {
            ticketField.setText(null);
            ticketField.setError(false);
            request.setTicket(null);
        }
        else if (scene == Scene.EMAIL_SCENE) {
            emailField.setText(null);
            emailField.setError(false);
            request.clearUserInfo();
        }
        else if (scene == Scene.DESCRIPTION_SCENE) {
            descriptionField.setText(null);
            descriptionField.setError(false);
            request.addDescription(null);
        }
        else if (scene == Scene.EQUIPMENT_SCENE) {
            request.getEquipment().clear();

            var panel = getCurrentPanel();
            for(Component c : panel.getComponents()) {
                if (c instanceof Button button) {
                    button.setIcon(button.getDefaultIcon());
                }
            }
        }
    }

    private void launchForm() {
        inactivityManager.start();
        next();
        showNavigation();
    }

    public void reset() {
        request = null;
        ticketField.setText(null);
        emailField.setText(null);
        descriptionField.setText(null);
        canSubmit = false;
        inactivityManager.stop();
        hideNavigation();

        if (getCurrentScene() != Scene.MAIN_SCENE) {
            exit();
        }
    }
    @Override
    public void requestFocus() {
        var panel = getCurrentPanel();
        for (Component c : panel.getComponents()) {
            if (c instanceof JTextComponent) {
                c.requestFocus();
                return;
            }
        }
    }
    @Override
    public void build() {
        SceneBuilder sb = new SceneBuilder();
        var navigationPane = sb.buildNavigation(this);
        var ticketInfoPanel = sb.buildTicketInfoScene(this);
        var emailPanel = sb.buildEmailScene(this);
        var descriptionPanel = sb.buildDescriptionScene(this);

        exitButton = (Button) navigationPane.getComponent(0);
        backButton = (Button) navigationPane.getComponent(1);
        nextButton = (Button) navigationPane.getComponent(2);
        ticketField = (TextBox) ticketInfoPanel.getComponent(1);
        emailField = (TextBox) emailPanel.getComponent(1);
        descriptionField = (LargeTextBox) descriptionPanel.getComponent(1);

        addScene(Scene.MAIN_SCENE, sb.buildMainScene(this));
        addScene(Scene.TICKET_STATUS_SCENE, sb.buildTicketStatusScene(this));
        addScene(Scene.TICKET_INFO_SCENE, ticketInfoPanel);
        addScene(Scene.EMAIL_SCENE, emailPanel);
        addScene(Scene.DESCRIPTION_SCENE, descriptionPanel);
        addScene(Scene.EQUIPMENT_SCENE, sb.buildEquipmentScene(this));
        addScene(Scene.END_SCENE, sb.buildEndScene());

        setContentPane(new JPanel(null));
        getContentPane().add(navigationPane);
        getContentPane().add(getScenesContainer());

        setSize(SceneBuilder.displaySize());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        setVisible(true);
    }
}

