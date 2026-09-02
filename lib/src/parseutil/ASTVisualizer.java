package lib.src.parseutil;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

public class ASTVisualizer {

    // Method to create and display a Swing window with the AST
    public static void visualizeAST(ASTNode rootNode) {
        DefaultMutableTreeNode root = rootNode.toTreeNode();
        JTree tree = new JTree(root);

        // Set the appearance of the tree (optional)
        tree.setShowsRootHandles(true);
        tree.setRootVisible(true);

        // Create a JScrollPane to make the tree scrollable
        JScrollPane scrollPane = new JScrollPane(tree);

        for (int row = 0; row < tree.getRowCount(); row++) {
            tree.expandRow(row);  // Expands all nodes
        }

        // Create the JFrame to display the tree
        JFrame frame = new JFrame("AST Visualizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setVisible(true);

    }

}