package CoSync.Interface;

import CoSync.CoController;

import javax.swing.*;

/**
 * Created by Alban on 01/07/2015.
 */
public abstract class CoInterface extends JFrame {
    private CoController controller;

    public abstract void update();
}
