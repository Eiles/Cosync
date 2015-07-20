package Models;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.io.File;

/**
 * Created by apaczkowski on 07/07/2015.
 */
public class CoFileTreeModel implements TreeModel{

    protected File root;
    public CoFileTreeModel(File root) { this.root = root; }

    public Object getRoot() { return root; }

    public boolean isLeaf(Object node) {  return ((File)node).isFile(); }

    public int getChildCount(Object parent) {
        String[] children = ((File)parent).list();
        if (children == null) return 0;
        return children.length;
    }

    public Object getChild(Object parent, int index) {
        String[] children = ((File)parent).list();
        if ((children == null) || (index >= children.length)) return null;
        return new File((File) parent, children[index]);
    }

    public int getIndexOfChild(Object parent, Object child) {
        String[] children = ((File)parent).list();
        if (children == null) return -1;
        String childname = ((File)child).getName();
        for(int i = 0; i < children.length; i++) {
            if (childname.equals(children[i])) return i;
        }
        return -1;
    }

    public void valueForPathChanged(TreePath path, Object newvalue) {}

    public void addTreeModelListener(TreeModelListener l) {}
    public void removeTreeModelListener(TreeModelListener l) {}
}
