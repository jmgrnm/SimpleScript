package lib.src.parseutil;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;
import java.util.ArrayList;

public class ASTNode {
    private String value;  // The value of the node (e.g., the non-terminal or terminal)
    private List<ASTNode> children;

    public ASTNode(String value) {
        this.value = value;
        this.children = new ArrayList<>();
    }

    // Add a child to this node
    public void addChild(ASTNode child) {
        children.add(child);
    }

    // Convert this ASTNode to a JTree node (DefaultMutableTreeNode)
    public DefaultMutableTreeNode toTreeNode() {
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(this.value);
        for (ASTNode child : children) {
            treeNode.add(child.toTreeNode());
        }
        return treeNode;
    }

    // Getters for the AST node value and children
    public String getValue() {
        return value;
    }

    public List<ASTNode> getChildren() {
        return children;
    }
}

