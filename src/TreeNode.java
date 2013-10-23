import java.util.ArrayList;
import java.util.List;


public class TreeNode<T> {

	protected List<TreeNode<T>> childs;
	protected T data;

	
	TreeNode(){
		data = null;
		childs = new ArrayList<TreeNode<T>>();
	}
	
	TreeNode(T data){
		this.data = data;
		childs = new ArrayList<TreeNode<T>>();
	}
	
	T getData(){
		return data;
	}
	
	void addChild(TreeNode<T> tn){
		childs.add(tn);
	}
	
	void setChilds(List<TreeNode<T>> newChilds){
		childs = newChilds;
	}
	
	public String prettyPrint(){
		StringBuilder pp = new StringBuilder();
		String cur = toString();
		
		
		pp.append(cur);
		if(childs.size() > 0){
			for(TreeNode tn : childs)
				pp.append(tn.prettyPrint());
		}
		
		return pp.toString();
	}
	
	public String toString(){
		return '(' + data.toString() + ')';
	}
	
	protected long ppWidth(){
		long w = toString().length();
		long c = 0, n;
		if(childs.size() > 0){
			for(TreeNode<T> tn : childs){
				n = tn.ppWidth();
				if(n > c)
					c = n;
			}
			c += childs.size() - 1; //add spaces separators
		}
		return w > c ? w : c;
	}

	public long getNumberOfNodes() {
		long nodesCount = 1;
		for(TreeNode tn : childs)
			nodesCount += tn.getNumberOfNodes();
		return nodesCount;
	}
	
	public long getDepth(){
		long depth = 0, nodedepth;
		for(TreeNode<T> tn : childs){
			nodedepth = tn.getDepth(); 
			if(nodedepth > depth)
				depth = nodedepth; 
		}
		return depth + 1; //+1 for current
	}
}
