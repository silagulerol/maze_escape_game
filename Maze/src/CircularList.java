
public class CircularList {
	public class Node{
		public char id;
		public Node next;
		
		public Node(char id){
			this.id=id;
			this.next=null;
		}
	}
	
	public Node head;
	public Node tail;
	public int size;
	
	public CircularList(){
		head=null;
		tail=null;
		size=0;
	}
	
	public boolean isEmpty() {
		return head==null && tail==null;
	}
	
	public void add(char id) {
		Node newNode= new Node(id);
		if(isEmpty()) {
			head= newNode;
			tail= newNode;
			tail.next=head;
			size++;
		}else {
			tail.next=newNode;
			tail=newNode;
			tail.next=head;
			size++;
		}
	}
	
	public char remove() {
		Node current= head;
		while(current.next != tail) {
			current= current.next;
		}
		char id= tail.id;
		tail=current;
		current.next=head;
		size--;
		return id;
	}
	
	public char[] rotate() {
		tail= head;
		head=head.next;
		
		char[] array = new char[size];
		array[0]= head.id;
		Node current=head.next;
		int i=1;
		while(current != head) {
			array[i]= current.id;
			current=current.next;
			i++;
		}
		return array;
	}
	
	//---------------------
	// PRINT CircularList
	//--------------------
	public void tostring() {
		System.out.println(head.id);
		Node current=head.next;
		while(current!=head) {
			System.out.println(current.id);
			current=current.next;
		}
	}
	
	public static void main(String[] args) 
	{
		/*
		CircularList c = new CircularList();
		c.add('1');
		c.add('2');
		c.add('3');
		c.add('4');
		c.add('5');
		c.add('6');
		c.tostring();
		System.out.println("");
		char[] arr= c.rotate();
		c.tostring();
		for(int i=0;i<arr.length;i++) {
			System.out.println(arr[i]);
		}
		*/
	}
}

























