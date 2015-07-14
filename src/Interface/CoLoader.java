package Interface;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Alban on 23/06/2015.
 */
public class CoLoader extends JFrame {

    private Dimension dimLoader;
    private Label loaderText;

    public CoLoader() {
        getContentPane().setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        dimLoader = new Dimension(300, 200);
        setSize(dimLoader);
        setTitle("Chargement");

        loaderText = new Label("Veuillez patienter...");
        loaderText.setAlignment(Label.CENTER);
        add(loaderText, BorderLayout.CENTER);
    }

    public void updateLoaderText(String newText) {
        loaderText.setText(newText);
    }

    public void setLoading(Boolean load) {
        updateLoaderText("Chargement en cours, Veuillez patientez ...");
        setTitle("Chargement");
        setVisible(load);
    }
}
