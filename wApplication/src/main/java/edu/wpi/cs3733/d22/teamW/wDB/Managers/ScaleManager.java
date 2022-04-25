package edu.wpi.cs3733.d22.teamW.wDB.Managers;

import edu.wpi.cs3733.d22.teamW.wApp.controllers.customControls.AutoCompleteInput;
import edu.wpi.cs3733.d22.teamW.wApp.controllers.customControls.EmergencyButton;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class ScaleManager {

    // Singleton Pattern:
    private static class Instance {
        public static final ScaleManager instance = new ScaleManager();
    }

    private ScaleManager() {
        constructor();
    }


    public static ScaleManager getInstance() {
        return Instance.instance;
    }

    //------------------------Class Impl.-------------------------
    private final ArrayList<Class> defaultTrueTypes = new ArrayList<>();

    private void constructor() {
        defaultTrueTypes.add(Button.class);
        defaultTrueTypes.add(ComboBox.class);
        defaultTrueTypes.add(EmergencyButton.class);
        defaultTrueTypes.add(AutoCompleteInput.class);
        defaultTrueTypes.add(TextField.class);
        defaultTrueTypes.add(Label.class);
        defaultTrueTypes.add(Text.class);
        defaultTrueTypes.add(ImageView.class);
    }

    //------------------------WIDTH CHANGE------------------------
    public void setTrueX(Pane parent, double o, double n) {
        scaleChildrenX(parent, o == 0 ? 1 : n / o);
    }

    private void scaleChildrenX(Pane parent, double scale) {
        for (Node child : parent.getChildren()) {
            if (isPane(child)) {
                scaleChildrenX((Pane) child, scale);
            }
            else if (shouldScale(child)) {
                child.setScaleX(child.getScaleX() * scale);

                //Maximum and Minimum bounding:
                if (child.getScaleX() <= 1) {
                    child.setScaleX(1);
                }
                if (child.getScaleX() >= 2) {
                    child.setScaleX(2);
                }
            }
        }
    }

    //------------------------LENGTH CHANGE------------------------
    public void setTrueY(Pane parent, double o, double n) {
        scaleChildrenY(parent, o == 0 ? 1 : n / o);
    }

    private void scaleChildrenY(Pane parent, double scale) {
        for (Node child : parent.getChildren()) {
            if (isPane(child)) {
                scaleChildrenY((Pane) child, scale);
            }
            else if (shouldScale(child)) {
                child.setScaleY(child.getScaleY() * scale);

                //Maximum and Minimum bounding:
                if (child.getScaleY() <= 1) {
                    child.setScaleY(1);
                }
                if (child.getScaleY() >= 2) {
                    child.setScaleY(2);
                }
            }
        }
    }

    //----------------------HELPER FUNCTIONS-----------------------

    private boolean isPane(Node node) {
        return node.getClass().getSuperclass().equals(Pane.class);
    }

    private boolean shouldScale(Node node) {
        Object scalable = node.getProperties().get("isScalable");
        boolean shouldCheckDefault = true;
        if (scalable != null) {
            if (scalable.equals("false")) {
                return false;
            }
            if (scalable.equals("true")) {
                shouldCheckDefault = false;
            }
        }
        if (shouldCheckDefault && !defaultTrueTypes.contains(node.getClass())) {
            return false;
        }
        return true;
    }
}