import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.lang.Math;

public class TreeNode<T> {

	protected List<TreeNode<T>> children;
	protected TreeNode<T> parent;
	protected T data;

	/**
	 * default TreeNode constructor
	 */
	TreeNode(){
		data = null;
		parent = null;
		children = new ArrayList<TreeNode<T>>();
	}
	
	/**
	 * Constructor with a T value
	 * @param data
	 */
	TreeNode(T data){
		this.data = data;
		parent = null;
		children = new ArrayList<TreeNode<T>>();
	}
	
	/**
	 * get the value of the node
	 * @return a T value
	 */
	T getData(){
		return data;
	}
	
	/**
	 * setter for the current value
	 * @param val the value to store in the node
	 */
	void setData(T val){
		data = val;
	}
	
	/**
	 * Getter for parent node
	 * @return the parent (if any) 
	 */
	public TreeNode<T> getParent(){
		return parent;
	}
	
	/**
	 * Setter for the parent node
	 * @param tn the node that became the parent
	 */
	void setParent(TreeNode<T> tn){
		this.parent = tn;
	}
	
	/** 
	 * add a child to a node 
	 * @param tn a single node to add as a child
	 */
	void addChild(TreeNode<T> tn){
		children.add(tn);
		tn.setParent(this);
	}
	
	/**
	 * setter for the childs at once
	 * @param newChildren a list of childs
	 */
	void setChildren(List<TreeNode<T>> newChildren){
		children = newChildren;
		for(TreeNode<T> tn : children)
			tn.setParent(this);
	}
	
	TreeNode<T> getChildAt(int index){
		return children.get(index);
	}
	
	List<TreeNode<T>> getChildren(){
		return children;
	}
	
	/**
	 * remove a child
	 * @param index to delete
	 * @return the removed child
	 */
	TreeNode<T> removeChildAt(int index){
		return children.remove(index);
	}
	
	/**
	 * Delete all the children
	 */
	void removeChildren(){
		children.clear();
	}
	
	/**
	 * Return a prettified representation of a Node 
	 * and its children (if any)
	 * 
	 * @return a list of formatted strings
	 * 
	 * <p>
	 * The general structure is
	 * <pre>
	 * +-----------------------------+
	 * |     Title (current node)    |
	 * +-----------------------------+ 
	 * |        Connector zone       | \
	 * +---------+---------+---------+  |
	 * |         |         | child n |  | This part is only generated 
	 * | child 1 |         |---------+  | if there are some children.
     * |         | child 2 |            | Notice that children can have 
 	 * +---------+         |            | different various heights
 	 *           |         |            |
 	 *           +---------+           /
 	 * </pre>
 	 * That structure is recursively repeated
 	 * and a node has the witdh of the max value between
 	 * its own label and the cumulated width of each child 
	 */
	public String[] toPrettifiedStrings(){
		List<String> ls = new ArrayList<String>();
		String head = toString();

		if(children.size() > 0){
			int maxd = 0, d;
			int totalwidth = 0;
			
			//get the highest node 
			// + the max width for formating head 
			for(int i=0; i < children.size(); i++){
				d = children.get(i).getDepth();
				if(d > maxd)
					maxd = d;
				totalwidth += children.get(i).prettyfiedWidth();
				if(i < children.size()-1)
					totalwidth++; //take separator into account
			}
			
			//now format each string
			
			//first head
			if(totalwidth > head.length())
				ls.add(center(head, totalwidth));
			else
				ls.add(head);
						
			//build the connectors
			if(children.size() == 1){
				ls.add(center("|",Math.max(totalwidth, head.length())));
			} else {
				StringBuilder sb = new StringBuilder();
				{//semi connector for first child
					int leftPadding = (children.get(0).prettyfiedWidth() - 1 /*for '+' */) / 2;
					for(int n=0; n<leftPadding; n++)
						sb.append(" ");
					sb.append('+');
					int rightPadding = children.get(0).prettyfiedWidth() - leftPadding;
					for(int n=0; n<rightPadding; n++)
						sb.append('-');
				}
				//connectors for intermediate childs
				for(int x=1; x<children.size()-1; x++)
					for(int y=0; y<children.get(x).prettyfiedWidth()+1; y++)
						sb.append('-');
				{//semi connector for last child
					int leftPadding = children.get(children.size()-1).prettyfiedWidth() / 2;
					for(int n=0; n<leftPadding; n++)
						sb.append("-");
					sb.append('+');
					int rightPadding = children.get(children.size()-1).prettyfiedWidth() - 1 /*for '+' */ - leftPadding;
					for(int n=0; n<rightPadding; n++)
						sb.append(' ');
				}
				//connector sign for middle of childs
				int p = children.get(0).prettyfiedWidth();
				for(int x=1; x<children.size()-1; x++){
					sb.setCharAt(p + children.get(x).prettyfiedWidth() / 2 + 1, '+');
					p += children.get(x).prettyfiedWidth() + 1;
				}

				//connector sign for parent
				if(children.size() % 2 == 0)
					sb.setCharAt(sb.length()/2, '^');
				
				ls.add(sb.toString());
			}
			//the height of a child is 2 lines per depth minus 1 
			for(int level=0; level<maxd*2-1; level++){
				StringBuilder sb = new StringBuilder();
				//for each line until the height of the greatest child,
				//and for each child
				//put the child content or an empty line
				for(int i=0; i<children.size(); i++){
					TreeNode<T> curNode = children.get(i);
					String[] prettifiedNode = curNode.toPrettifiedStrings();
					if(level < prettifiedNode.length){
						sb.append(String.format("%"+curNode.prettyfiedWidth()+"s", prettifiedNode[level]));
					}
					else{
						for(int c=0; c<prettifiedNode[0].length(); c++)
							sb.append(" ");
					}
					if(i<children.size()-1)
						sb.append(" ");
				}
				ls.add(sb.toString());
			}
		}
		else
			ls.add(head);
		
		return ls.toArray(new String[ls.size()]); //convert the list to an array
	}
	
	public String center (String s, int length) {
	    if (s.length() > length) {
	        return s.substring(0, length);
	    } else if (s.length() == length) {
	        return s;
	    } else {
	        int leftPadding = (length - s.length()) / 2; 
	        StringBuilder leftBuilder = new StringBuilder();
	        for (int i = 0; i < leftPadding; i++)
	            leftBuilder.append(" ");

	        int rightPadding = length - s.length() - leftPadding;
	        StringBuilder rightBuilder = new StringBuilder();
	        for (int i = 0; i < rightPadding; i++) 
	            rightBuilder.append(" ");

	        return leftBuilder.toString() + s 
	                + rightBuilder.toString();
	    }
	}
	
	public String toString(){
		return '(' + data.toString() + ')';
	}
	
	/**
	 * Pretty-printed height of a Node
	 * @return The max width of the Node
	 */
	public int prettyfiedWidth(){ 
		//FIXME: too many calls to this function, need some memoize
		int w = toString().length();
		int c = 0;
		for(TreeNode<T> tn : children){
			c += tn.prettyfiedWidth();
		}
		c += children.size() - 1; //add spaces separators
		return w > c ? w : c;
	}

	/**
	 * get the depth of a node: the number of underlying levels
	 */
	public int getDepth(){
		int depth = 0, nodedepth;
		for(TreeNode<T> tn : children){
			nodedepth = tn.getDepth(); 
			if(nodedepth > depth)
				depth = nodedepth; 
		}
		return depth + 1; //+1 for current
	}

	/**
	 * Get the number of nodes in the current sub tree.
	 * The current node counts for 1. Any child count for 1.
	 */
	public int getNumberOfNodes() {
		int nodesCount = 1;
		for(TreeNode<T> tn : children)
			nodesCount += tn.getNumberOfNodes();
		return nodesCount;
	}
	
	/**
	 * if 2 TreeNode have same data, the 2 TreeNode are equal
	 */
	public boolean equals(Object obj){
		if(obj==null)
			return false;
		if(obj instanceof TreeNode)
			if(((TreeNode<?>)obj).getData().equals(this.data))
				return true;
		return false;
	}
}
