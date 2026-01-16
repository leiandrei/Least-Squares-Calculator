import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class Main {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            LeastSquaresApp newApp = new LeastSquaresApp();
            newApp.setVisible(true);

        });

    }
}
