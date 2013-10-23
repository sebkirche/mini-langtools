
public class Tree<T> {

	private TreeNode<T> root;
	
	public static void main(String[] args) {
		
		System.out.println("Test Tree");

		Tree<String> t = new Tree<String>();
		System.out.println("count = " + t.nodesCount());

		Tree<String> t2 = new Tree<String>("test");
		System.out.println("count = " + t2.nodesCount());
		System.out.println(t2.prettyPrint());
		
		t2.getRoot().addChild(new TreeNode<String>("sub1"));
		TreeNode<String> t3 = new TreeNode<String>("sub2");
		t3.addChild(new TreeNode<String>("subsub1"));
		t2.getRoot().addChild(t3);
		System.out.println("depth = " + t2.getRoot().getDepth());
		System.out.println(t2.prettyPrint());
	}
	
	public boolean isEmpty(){
		return (root == null);
	}
	
	private String prettyPrint() {
		if (isEmpty())
			return "";
		else 
			return root.prettyPrint();
	}

	Tree(){
		
	}
	
	Tree(T data){
		root = new TreeNode<T>(data);
	}
	
	public TreeNode<T> getRoot(){
		return root;
	}
	
	void setRoot(TreeNode<T> tn){
		root = tn;
	}
	
	public long nodesCount(){
		if(root == null)
			return 0;
		else
			return root.getNumberOfNodes();
	}
	
}
