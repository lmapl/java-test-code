package algorithm.structure;

/**
 * @author
 * 判断一棵树是否为另一棵树的子树
 */
public class ChildTreeCompare {

	public static void main(String[] args) {
		//创建树a
		TreeNode a1 = new TreeNode(10);
		TreeNode a2 = new TreeNode(9);
		TreeNode a3 = new TreeNode(8);
		TreeNode a4 = new TreeNode(7);
		TreeNode a5 = new TreeNode(6);
		TreeNode a6 = new TreeNode(5);
		TreeNode a7 = new TreeNode(3);
		TreeNode a8 = new TreeNode(8);
		a1.setLeft(a2);
		a1.setRight(a3); 
		a2.setLeft(a4);
		a1.setLeft(a2);
		a4.setLeft(a6);
		a4.setRight(a7);
		//a7.setRight(a8);
		//创建树b
		TreeNode b1 = new TreeNode(7);
		TreeNode b2 = new TreeNode(5);
		TreeNode b3 = new TreeNode(3);
		//TreeNode b3 = new TreeNode(4);
		b1.setLeft(b2);
		b1.setRight(b3);
		
		System.out.println("subTree(a,b)="+isSubTree(a1, b1));

	}

	public static boolean isSameTree(TreeNode s, TreeNode t) {
		//节点输入时，如果两个节点都是空，则返回true；
		if (s == null && t == null) {
			return true;
		}
		//两者有一个为空，另一个不为空，返回false
		if (s == null || t == null) {
			return false;
		}
		//两者都不为空时  
		// 判断 s和t的节点值是否相同；
		// 判断s和t的左子节点是否相同；递归调用，
		// 判断s和t的右子节点是否相同 递归调用，
		//当以上三者都为true时，两棵树才算相同
		return s.getValue() == t.getValue() && isSameTree(s.getLeft(), t.getLeft()) && isSameTree(s.getRight(), t.getRight());
	}
	
	public static boolean isSubTree(TreeNode tree, TreeNode contrast) {
		//如果母树tree为空，那参 contrast肯定不是tree的子树
		if (tree == null) {
			return false;
		}
		//接收isSameTree返回的值，如果为true，则contrast是tree的子树
		if(isSameTree(tree, contrast)) {
			return true;
		}
		//递归tree树所有的节点，判断以节点作为根节点时，tree树和contrast树是否相同
		//如果相同则证明b树是a树的子树
		return isSubTree(tree.getLeft(), contrast) || isSubTree(tree.getRight(), contrast);
	}
}


//节点类
class TreeNode {
	//左子树
	private TreeNode leftTreeNode;
	//右子树
	private TreeNode rightTreeNode;
	//值
	private int value;

	public TreeNode(int value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "TreeNode{" +
				"leftTreeNode=" + leftTreeNode +
				", rightTreeNode=" + rightTreeNode +
				", value=" + value +
				'}';
	}

	public TreeNode getLeft() {
		return leftTreeNode;
	}

	public void setLeft(TreeNode leftTreeNode) {
		this.leftTreeNode = leftTreeNode;
	}

	public TreeNode getRight() {
		return rightTreeNode;
	}

	public void setRight(TreeNode rightTreeNode) {
		this.rightTreeNode = rightTreeNode;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}
