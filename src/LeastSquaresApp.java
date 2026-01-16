import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class LeastSquaresApp extends JFrame 
{
    // Initialize GUI Properties
    private JSpinner objRows, objCols;
    private JTable objMatrixTable, objVectorTable;
    private DefaultTableModel matrixModel, vectorModel;
    private JTextArea resultArea;
    private JButton solveButton, clearButton;
    
    public LeastSquaresApp() {
        setTitle("Least-Squares Solution Finder");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        setLayout(new BorderLayout(10, 10));
        
        // Top panel for dimension selection
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        JLabel rowLabel1 = new JLabel("Rows (m): ");
        JLabel rowLabel2 = new JLabel("Columns (n): ");

        rowLabel1.setFont(new Font("Cambria", Font.BOLD, 15));
        rowLabel2.setFont(new Font("Cambria", Font.BOLD, 15));

        topPanel.add(rowLabel1);
        objRows = new JSpinner(new SpinnerNumberModel(3, 1, 5, 1));
        topPanel.add(objRows);
        
        topPanel.add(rowLabel2);
        objCols = new JSpinner(new SpinnerNumberModel(2, 1, 5, 1));
        topPanel.add(objCols);
        
        JButton createButton = new JButton("Create Matrix");
        createButton.setPreferredSize(new Dimension(150, 35));
        createButton.addActionListener(e -> createMatrixTables());
        createButton.setFont(new Font("Calibri", Font.BOLD, 14));
        createButton.setForeground(Color.black);
        createButton.setBackground(new Color(211, 211, 211));
        createButton.setBorder(BorderFactory.createEtchedBorder());
        topPanel.add(createButton);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel for matrix input
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // right matrix panel
        JPanel matrixPanel = new JPanel(new BorderLayout(5, 5));
        matrixPanel.setBorder(BorderFactory.createTitledBorder("Augmented Matrix [A|b]"));
        objMatrixTable = new JTable();
        objMatrixTable.setRowHeight(30);
        JScrollPane matrixScroll = new JScrollPane(objMatrixTable);
        matrixPanel.add(matrixScroll, BorderLayout.CENTER);
        centerPanel.add(matrixPanel);
        
        // left results panel
        JPanel resultsPanel = new JPanel(new BorderLayout(5, 5));
        resultsPanel.setBorder(BorderFactory.createTitledBorder("Solution & Error"));
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane resultScroll = new JScrollPane(resultArea);
        resultsPanel.add(resultScroll, BorderLayout.CENTER);
        centerPanel.add(resultsPanel);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom panel for buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        solveButton = new JButton("Solve Least-Squares");
        solveButton.setPreferredSize(new Dimension(160, 35));
        solveButton.addActionListener(e -> solveLeastSquares());
        solveButton.setFont(new Font("Calibri", Font.BOLD, 14));
        solveButton.setForeground(Color.black);
        solveButton.setBackground(new Color(211, 211, 211));
        solveButton.setBorder(BorderFactory.createEtchedBorder());
    
        bottomPanel.add(solveButton);
        
        clearButton = new JButton("Clear Results");
        clearButton.setPreferredSize(new Dimension(160, 35));
        clearButton.addActionListener(e -> resultArea.setText(""));
        clearButton.setFont(new Font("Calibri", Font.BOLD, 14));
        clearButton.setForeground(Color.black); 
        clearButton.setBackground(new Color(211, 211, 211));
        clearButton.setBorder(BorderFactory.createEtchedBorder());
        bottomPanel.add(clearButton);
        
        add(bottomPanel, BorderLayout.SOUTH);
        
        createMatrixTables(); // initial matrix
        
        setSize(900, 600);
        setLocationRelativeTo(null);
    }
    
    private void createMatrixTables() {
        int mRows = (Integer) objRows.getValue();
        int nCols = (Integer) objCols.getValue();
        
        String[] colNames = new String[nCols + 1];
        for (int i = 0; i < nCols; i++) {
            colNames[i] = "X" + (i + 1);
        }
        colNames[nCols] = "b";
        
        matrixModel = new DefaultTableModel(colNames, mRows);
        objMatrixTable.setModel(matrixModel);
        
        // Set default values 0 fro the matrix
        for (int i = 0; i < mRows; i++) {
            for (int j = 0; j <= nCols; j++) {
                matrixModel.setValueAt("0", i, j);
            }
        }
        
        resultArea.setText("");
    }
    
    private void solveLeastSquares() {
        try {
            int m = matrixModel.getRowCount();
            int n = matrixModel.getColumnCount() - 1;
            
            // Read augmented matrix
            double[][] A = new double[m][n];
            double[] b = new double[m];
            
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    String val = matrixModel.getValueAt(i, j).toString();
                    A[i][j] = Double.parseDouble(val);
                }
                String val = matrixModel.getValueAt(i, n).toString();
                b[i] = Double.parseDouble(val);
            }
            
            
            double[][] AtA = matrixMultiply(transpose(A), A);
            double[] Atb = matrixVectorMultiply(transpose(A), b);
            
            double[] x = gaussianElimination(AtA, Atb);
            
            double[] Ax = matrixVectorMultiply(A, x);
            double[] residual = new double[m];
            double errorSquared = 0;
            
            for (int i = 0; i < m; i++) {
                residual[i] = b[i] - Ax[i];
                errorSquared += residual[i] * residual[i];
            }
            
            double error = Math.sqrt(errorSquared);
            
            // Display results
            StringBuilder result = new StringBuilder();
            result.append("LEAST-SQUARES SOLUTION:\n");
            result.append("======================\n\n");
            
            for (int i = 0; i < n; i++) {
                result.append(String.format("X%d = %.2f\n", i + 1, x[i]));
            }
            
            result.append("\n\nRESIDUAL VECTOR (b - Ax):\n");
            result.append("=========================\n");
            for (int i = 0; i < m; i++) {
                result.append(String.format("r%d = %.2f\n", i + 1, residual[i]));
            }
            
            result.append("\n\nLEAST-SQUARES ERROR:\n");
            result.append("====================\n");
            result.append(String.format("||b - Ax|| = %.2f\n", error));
            result.append(String.format("||b - Ax||² = %.2f", errorSquared));
            
            resultArea.setText(result.toString());
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Please enter valid numeric values in all cells.", 
                "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error solving system: " + ex.getMessage(), 
                "Computation Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private double[][] transpose(double[][] matrix) {
        int m = matrix.length;
        int n = matrix[0].length;
        double[][] result = new double[n][m];
        
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                result[j][i] = matrix[i][j];
            }
        }
        return result;
    }
    
    private double[][] matrixMultiply(double[][] A, double[][] B) {
        int m = A.length;
        int n = B[0].length;
        int p = A[0].length;
        double[][] result = new double[m][n];
        
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < p; k++) {
                    result[i][j] += A[i][k] * B[k][j];
                }
            }
        }
        return result;
    }
    
    private double[] matrixVectorMultiply(double[][] A, double[] x) {
        int m = A.length;
        int n = A[0].length;
        double[] result = new double[m];
        
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                result[i] += A[i][j] * x[j];
            }
        }
        return result;
    }
    
    private double[] gaussianElimination(double[][] A, double[] b) {
        int n = A.length;
        double[][] augmented = new double[n][n + 1];
        
        // Create augmented matrix
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                augmented[i][j] = A[i][j];
            }
            augmented[i][n] = b[i];
        }
        

        for (int k = 0; k < n; k++) {

            int maxRow = k;
            for (int i = k + 1; i < n; i++) {
                if (Math.abs(augmented[i][k]) > Math.abs(augmented[maxRow][k])) {
                    maxRow = i;
                }
            }
            

            double[] temp = augmented[k];
            augmented[k] = augmented[maxRow];
            augmented[maxRow] = temp;
            
            for (int i = k + 1; i < n; i++) {
                double factor = augmented[i][k] / augmented[k][k];
                for (int j = k; j <= n; j++) {
                    augmented[i][j] -= factor * augmented[k][j];
                }
            }
        }
        
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            x[i] = augmented[i][n];
            for (int j = i + 1; j < n; j++) {
                x[i] -= augmented[i][j] * x[j];
            }
            x[i] /= augmented[i][i];
        }
        
        return x;
    }
    
}