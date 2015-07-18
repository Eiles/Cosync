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

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);
        this.setLocation(x, y);
    }

    public abstract void update();
}
