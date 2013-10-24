import java.util.ArrayList;
import java.util.List;
import java.lang.Math;

public class TreeNode<T> {

	protected List<TreeNode<T>> childs;
	protected T data;

	/**
	 * default TreeNode constructor
	 */
	TreeNode(){
		data = null;
		childs = new ArrayList<TreeNode<T>>();
	}
	
	/**
	 * Constructor with a T value
	 * @param data
	 */
	TreeNode(T data){
		this.data = data;
		childs = new ArrayList<TreeNode<T>>();
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
	 * add a child to a node 
	 * @param tn a single node to add as a child
	 */
	void addChild(TreeNode<T> tn){
		childs.add(tn);
	}
	
	/**
	 * setter for the childs at once
	 * @param newChilds a list of childs
	 */
	void setChilds(List<TreeNode<T>> newChilds){
		childs = newChilds;
	}
	
	TreeNode<T> getChildAt(int index){
		return childs.get(index);
	}
	
	/**
	 * Return a prettified representation of a Node 
	 * and its childs (if any)
	 */
	public List<String> prettyPrint(){
		List<String> ls = new ArrayList<String>();
		String head = toString();

		if(childs.size() > 0){
			int maxd = 0, d;
			int totalwidth = 0;
			
			//get the highest node 
			// + the max width for formating head 
			for(int i=0; i < childs.size(); i++){
				d = childs.get(i).getDepth();
				if(d > maxd)
					maxd = d;
				totalwidth += childs.get(i).prettyfiedWidth();
				if(i < childs.size()-1)
					totalwidth++; //take separator into account
			}
			//System.out.println(String.format("totalw for %s = %d", head, totalwidth));
			
			//now format each string
			
			//first head
			if(totalwidth > head.length())
				ls.add(center(head, totalwidth));
			else
				ls.add(head);
						
			//build the connectors
			switch(childs.size()){
				case 1:
					ls.add(center("|",Math.max(totalwidth, head.length())));
					break;
				case 2:
					ls.add(center("/ \\",Math.max(totalwidth, head.length())));
					break;
				default:
					StringBuilder sb = new StringBuilder();
					{//semi connector for first child
						int leftPadding = (childs.get(0).prettyfiedWidth() - 1 /*for '+' */) / 2;
						for(int n=0; n<leftPadding; n++)
							sb.append(" ");
						sb.append('+');
						int rightPadding = childs.get(0).prettyfiedWidth() - leftPadding;
						for(int n=0; n<rightPadding; n++)
							sb.append('-');
					}
					//connectors for intermediate childs
					for(int x=1; x<childs.size()-1; x++)
						for(int y=0; y<childs.get(x).prettyfiedWidth()+1; y++)
							sb.append('-');
					{//semi connector for last child
						int leftPadding = childs.get(childs.size()-1).prettyfiedWidth() / 2;
						for(int n=0; n<leftPadding; n++)
							sb.append("-");
						sb.append('+');
						int rightPadding = childs.get(childs.size()-1).prettyfiedWidth() - 1 /*for '+' */ - leftPadding;
						for(int n=0; n<rightPadding; n++)
							sb.append(' ');
					}
					//connector sign for middle of childs
					int p = childs.get(0).prettyfiedWidth();
					for(int x=1; x<childs.size()-1; x++){
						sb.setCharAt(p + childs.get(x).prettyfiedWidth() / 2 + 1, '+');
						p += childs.get(x).prettyfiedWidth() + 1;
					}

					//connector sign for parent
					if(childs.size() % 2 == 0)
						sb.setCharAt(sb.length()/2, '^');
					
					ls.add(sb.toString());
			}
			for(int level=0; level<maxd*2-1; level++){
				StringBuilder sb = new StringBuilder();
				for(int i=0; i<childs.size(); i++){
					TreeNode<T> curNode = childs.get(i);
					List<String> prettifiedNode = curNode.prettyPrint();
					if(level < prettifiedNode.size()){
						sb.append(String.format("%"+curNode.prettyfiedWidth()+"s", prettifiedNode.get(level)));
					}
					else{
						for(int c=0; c<prettifiedNode.get(0).length(); c++)
							sb.append(" ");
					}
					if(i<childs.size()-1)
						sb.append(" ");
				}
				ls.add(sb.toString());
			}
		}
		else
			ls.add(head);
		
		return ls;
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
		for(TreeNode<T> tn : childs){
			c += tn.prettyfiedWidth();
		}
		c += childs.size() - 1; //add spaces separators
		return w > c ? w : c;
	}

	/**
	 * get the depth of a node: the number of underlying levels
	 */
	public int getDepth(){
		int depth = 0, nodedepth;
		for(TreeNode<T> tn : childs){
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
		for(TreeNode<T> tn : childs)
			nodesCount += tn.getNumberOfNodes();
		return nodesCount;
	}
}
