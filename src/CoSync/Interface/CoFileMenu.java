package CoSync.Interface;

/**
 * Created by Alban on 01/07/2015.
 */

import CoSync.CoController;
import CoSync.Interface.Events.InterfaceEvents;
import CoSync.Models.CoEvent;
import CoSync.Models.Cosystem;
import CoSync.Models.Couser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

/**
 * Created by Alban on 15/06/2015.
 */
public class CoFileMenu extends CoInterface {

    private CoController controller;
    private Dimension dimInterface;

    private JPanel center;
    private JPanel east;
    private JPanel south;

    private JPanel eventsPanel;
    private JToolBar toolBar;

    private Label header;

    public CoFileMenu(CoController coController) {

        controller = coController;
        getContentPane().setLayout(new BorderLayout());
        addWindowListener(new InterfaceEvents());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        dimInterface = new Dimension(800, 600);
        setSize(dimInterface);
        setTitle("Gestion du fichier");

        setCenter();

        setVisible(true);
    }

    private void setCenter() {
        center = new JPanel();
        center.setLayout(new BorderLayout());

        header = new Label();
        center.add(header, BorderLayout.NORTH);

        eventsPanel = new JPanel();
        eventsPanel.add(new Label("Events Panel"));
        center.add(eventsPanel, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
    }

    @Override
    public void update() {

    }
}

