package Interface;

import Controllers.CoController;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Alban on 01/07/2015.
 */
public abstract class CoInterface extends JFrame {
    protected CoController controller;

    public CoInterface(CoController controller) throws HeadlessException {
        this.controller = controller;
    }

    public abstract void update();
}
