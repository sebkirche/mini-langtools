import java.util.ArrayList;
import java.util.List;


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
		
		t2.getRoot().addChild(new TreeNode<String>("inter1"));
		
		t2.getRoot().getChildAt(1).addChild(new TreeNode<String>("inter2"));
		
		TreeNode<String> t3 = new TreeNode<String>("sub2");
		t3.addChild(new TreeNode<String>("ssub1"));
		//t3.addChild(new TreeNode<String>("ssub2"));
		TreeNode<String> t4 = new TreeNode<String>("ssub3"); 
		t3.addChild(t4);
		t3.addChild(new TreeNode<String>("ssub4"));
		//t3.addChild(new TreeNode<String>("ssub5"));
		
		t2.getRoot().getChildAt(1)/*.getChildAt(0)*/.addChild(t3);
		
		t4.addChild(new TreeNode<String>("x42"));
		TreeNode<String> t5 = new TreeNode<String>("sub3");
		t2.getRoot().getChildAt(0).addChild(t5);
		t2.getRoot().getChildAt(0).addChild(new TreeNode<String>("another"));
				
		t3.getChildAt(0).addChild(new TreeNode<String>("x1"));
		t3.getChildAt(0).addChild(new TreeNode<String>("x2"));
		
		System.out.println("depth = " + t2.getRoot().getDepth());
		
		//for(String s : t3.prettyPrint())
		//	System.out.println(s);		
		
		System.out.println(t2.prettyPrint());
		System.out.println("Nodes : " + t2.nodesCount());
		
		TreeNode<String> ex = new TreeNode<String>("=");
		ex.addChild(new TreeNode<String>("+"));
		ex.getChildAt(0).addChild(new TreeNode<String>("2"));
		ex.getChildAt(0).addChild(new TreeNode<String>("*"));
		ex.getChildAt(0).getChildAt(1).addChild(new TreeNode<String>("3"));
		ex.getChildAt(0).getChildAt(1).addChild(new TreeNode<String>("4"));
		for(String s : ex.toPrettifiedStrings())
			System.out.println(s);
		System.out.println("Nodes : " + ex.getNumberOfNodes());
		
		Tree<String> tree = new Tree(ex);
		System.out.print("Prefix walk  : ");
		for(TreeNode<String> tn : tree.getTraversalList(OrderTraversal.PREFIX))
			System.out.print(tn.toString() + " ");
		System.out.println();
		System.out.print("Postfix walk : ");
		for(TreeNode<String> tn : tree.getTraversalList(OrderTraversal.POSTFIX))
			System.out.print(tn.toString() + " ");
		System.out.println();
	}
	
	public boolean isEmpty(){
		return (root == null);
	}
	
	private String prettyPrint() {
		if (isEmpty())
			return "";
		else{
			StringBuilder sb = new StringBuilder();
			for(String s : root.toPrettifiedStrings()){
				sb.append(s);
				sb.append("\n");
			}				
			return sb.toString();	
		}
		
	}

	Tree(){
		
	}
	
	Tree(TreeNode<T> tn){
		root = tn;
	}
	
	Tree(T data){
		root = new TreeNode<T>(data);
	}
	
	public TreeNode<T> getRoot(){
		return root;
	}
	
	public void setRoot(TreeNode<T> tn){
		root = tn;
	}
	
	public long nodesCount(){
		if(root == null)
			return 0;
		else
			return root.getNumberOfNodes();
	}
	
	public enum OrderTraversal{
		PREFIX, POSTFIX;
	}
	
	List<TreeNode<T>> getTraversalList(OrderTraversal order){
		List<TreeNode<T>> traversal = new ArrayList<TreeNode<T>>();
			switch(order){
			case PREFIX:
				prefixTraversal(root, traversal);
				break;
			case POSTFIX:
				postfixTraversal(root, traversal);
				break;			
			}
		return traversal;
	}
	
	void prefixTraversal(TreeNode<T> node, List<TreeNode<T>> traversal){
		traversal.add(node);
		for(TreeNode<T> tn : node.getChildren())
			prefixTraversal(tn, traversal);
	}
	
	void postfixTraversal(TreeNode<T> node, List<TreeNode<T>> traversal){
		for(TreeNode<T> tn : node.getChildren())
			postfixTraversal(tn, traversal);
		traversal.add(node);	
	}
	
}
