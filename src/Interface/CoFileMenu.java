package Interface;

/**
 * Created by Alban on 01/07/2015.
 */

import Controllers.CoController;
import Controllers.Config;
import Interface.Events.FilesEvents;
import Interface.Events.InterfaceEvents;
import Models.CoFileTreeModel;
import com.sun.org.apache.xpath.internal.SourceTree;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Created by Alban on 15/06/2015.
 */
public class CoFileMenu extends CoInterface {

    private FilesEvents events;
    private Dimension dimInterface;

    private AddMenu addMenu;

    private JPanel north;
    private JPanel center;
    private JPanel east;
    private JPanel south;

    private JTree filesTree;
    private JScrollPane versionPanel;
    private JList<String> versionsList;

    private JToolBar toolBar;

    private Label header;
    private File selected;

    public CoFileMenu(CoController coController) {

        super(coController);

        getContentPane().setLayout(new BorderLayout());
        addWindowListener(new InterfaceEvents());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        events = new FilesEvents(this, coController);
        addMenu = new AddMenu();
        selected = null;

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

        add(north, BorderLayout.NORTH);
    }

    private void setCenter() {
        center = new JPanel();
        center.setLayout(new BorderLayout());

        header = new Label();
        center.add(header, BorderLayout.NORTH);

        File root = new File(Config.root);
        CoFileTreeModel model = new CoFileTreeModel(root);

        filesTree = new JTree();
        filesTree.setModel(model);
        filesTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        filesTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                //This method is useful only when the selection model allows a single selection.
                File node = (File) filesTree.getLastSelectedPathComponent();

                if (node == null)
                    //Nothing is selected.
                    return;

                if (node.isFile()) {
                    selected = node;
                    updateVersions();
                }
            }
        });

        filesTree.clearSelection();

        versionsList = new JList<String>();
        versionsList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        versionsList.setLayoutOrientation(JList.VERTICAL);

        versionPanel = new JScrollPane(versionsList);
        versionPanel.setPreferredSize(new Dimension(130, this.getHeight()));
        versionPanel.setBorder(new LineBorder(Color.black.darkGray));

        center.add(filesTree, BorderLayout.CENTER);
        center.add(versionPanel, BorderLayout.EAST);

        add(center, BorderLayout.CENTER);
    }

    private void setVersions() {
        if (selected != null) {
            if (controller.getOldVersionsOfFile(Paths.get(Config.root).relativize(selected.toPath()).toString()) != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                List versions = controller.getOldVersionsOfFile(Paths.get(Config.root).relativize(selected.toPath()).toString());
                LinkedList<String> oldDate = new LinkedList<>();

                String version;
                for (int i = 0; i < versions.size(); i++) {
                    version = (String) versions.get(i);

                    Date date = new Date();
                    date.setTime(Long.parseLong(version.substring(version.indexOf("_") + 1, version.lastIndexOf("-"))));
                    oldDate.add(sdf.format(date));
                }

                versionsList.setListData(new Vector<String>(oldDate));

                versionsList.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent evt) {
                        JList list = (JList) evt.getSource();
                        if (evt.getClickCount() == 2) {
                            JFileChooser chooser = new JFileChooser();
                            chooser.setDialogTitle("Choix de la destination du fichier");

                            int returnVal = chooser.showSaveDialog(null);
                            if (returnVal == JFileChooser.APPROVE_OPTION) {
                                controller.getRevision(Paths.get(Config.root).relativize(selected.toPath()).toString(), chooser.getSelectedFile().getAbsolutePath(), versions, list.locationToIndex(evt.getPoint()));
                            }
                        }
                    }
                });
            }
        }
    }

    private void updateVersions() {

        setVersions();
        versionsList.revalidate();
        versionsList.repaint();
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

    public File getSelected() {
        return selected;
    }

    public void setSelected(File selected) {
        this.selected = selected;
    }

    public void setAddMenuVisibility(Boolean visible) {
        addMenu.setVisible(visible);
    }
}

