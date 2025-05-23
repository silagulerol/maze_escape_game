// Stack implementation based on linked list
public class Stack {
	public class Node{
		/* ----------
		 * | string |
		 * | entry  |
		 * |  "x,y" |
		 * |--------|
		 * |        |    
		 * |  next  |
		 * |--------|      
		*/
		public String entry; 
		public Node next;
		
		public Node(String entry){
			this.entry= entry;
			this.next=null;
		}
	}
	
	public Node top;
	public int size;
    public int maxDepth; 

	public Stack() {
		top=null;
		size=0;
		maxDepth = 0;
	}
	
	public void push(int X, int Y) {
		String entry = X+","+ Y;
		Node newNode= new Node(entry);
		if(top==null) {
			top = newNode;
		}else {
			newNode.next=top;
			top=newNode;
		}
		size++;
		
		if (size > maxDepth) {
            maxDepth = size;
        }
	}
	
	public String pop() {
		if(top==null) {
			System.out.println("there is no old moves");
			return null;
		}
		Node temp = top;
		top = top.next;
		size--;
		return temp.entry;
	}
	
	public int getMaxDepth() {
        return maxDepth;
    }
	
	public boolean isEmpty() {
		return top==null;
	}
	
	public String peek() {
		if(isEmpty()) {
			return null;
		}
		return top.entry;
	}
	
	public int size() {
		return size;
	}
	
	//---------------------
	// MOVE HISTORY
	//--------------------
	public String toString() {
		String s= "";
		Node current= top;
		int i =size;
		while(current!=null) {
			s += "in the "+ i + ". position: (" + current.entry + " )";
			current=current.next;
			i--;
		}
		return s;
	}
}



















