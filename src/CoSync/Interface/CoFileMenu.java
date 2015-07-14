package CoSync.Interface;

/**
 * Created by Alban on 01/07/2015.
 */

import CoSync.CoController;
import CoSync.Config;
import CoSync.Interface.Events.InterfaceEvents;
import CoSync.Models.CoFileTreeModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by Alban on 15/06/2015.
 */
public class CoFileMenu extends CoInterface {

    private Dimension dimInterface;

    private JPanel north;
    private JPanel center;
    private JPanel east;
    private JPanel south;

    private JPanel filesPanel;
    private JTree filesTree;

    private JToolBar toolBar;
    private JToolBar fileBar;

    private Label header;

    public CoFileMenu(CoController coController) {

        super(coController);

        getContentPane().setLayout(new BorderLayout());
        addWindowListener(new InterfaceEvents());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        dimInterface = new Dimension(800, 600);
        setSize(dimInterface);
        setTitle("Gestion du fichier");

        setNorth();
        setCenter();
        setSouth();
    }

    private void setNorth() {
        north = new JPanel();
        north.setLayout(new BorderLayout());

        final JButton add = new JButton("Ajout");
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    controller.logOut();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        });
        add.setAlignmentX(RIGHT_ALIGNMENT);

        final JButton edit = new JButton("Edition");
        edit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    controller.switchView("main");
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        });
        edit.setAlignmentX(LEFT_ALIGNMENT);

        final JButton delete = new JButton("Suppression");
        edit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    controller.switchView("main");
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        });
        edit.setAlignmentX(LEFT_ALIGNMENT);


        fileBar = new JToolBar();
        fileBar.add(add);
        fileBar.add(delete);
        fileBar.add(edit);

        north.add(fileBar);
        add(north, BorderLayout.NORTH);
    }

    private void setCenter() {
        center = new JPanel();
        center.setLayout(new BorderLayout());

        header = new Label();
        center.add(header, BorderLayout.NORTH);

        filesPanel = new JPanel();

        File root = new File(Config.root);
        CoFileTreeModel model = new CoFileTreeModel(root);

        filesPanel.add(new Label("Events Panel"));

        filesTree = new JTree();
        filesTree.setModel(model);

        filesTree.clearSelection();
        center.add(filesTree, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
    }

    private void setSouth() {
        south = new JPanel();
        south.setLayout(new BoxLayout(south, BoxLayout.X_AXIS));
        south.setPreferredSize(new Dimension(dimInterface.width,  dimInterface.height / 20));

        final JButton quit = new JButton("Déconnexion");
        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    controller.logOut();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        });
        quit.setAlignmentX(RIGHT_ALIGNMENT);

        final JButton mainMenu = new JButton("Menu principal");
        mainMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    controller.switchView("main");
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        });
        mainMenu.setAlignmentX(LEFT_ALIGNMENT);

        toolBar = new JToolBar();
        toolBar.add(mainMenu);
        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(quit);

        south.add(toolBar);

        add(south, BorderLayout.SOUTH);
    }

    private void updateFileTree() {
        File root = new File(Config.root);
        CoFileTreeModel model = new CoFileTreeModel(root);

        filesTree.clearSelection();
        filesTree.setModel(model);

        filesTree.revalidate();
    }

    @Override
    public void update() {
        updateFileTree();

        revalidate();
    }
}

