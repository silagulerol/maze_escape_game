public class CircularQueue{
	public Main.Agent[] queue;
	public int front=-1;
	public int rear=-1;
	public int size;
	public int count=0;
	
	public CircularQueue(int Size){
		this.size= Size;
		queue = new Main.Agent[size];
	}
	
	public void enqueue(Main.Agent agent) {
		if(front==-1)
			front=0;
		
		if(rear == size-1) {
			rear=0;
			queue[rear]=agent;
		}else {
			queue[++rear]=agent;
		}
		count++;
	}
	
	public Main.Agent dequeue() {
		Main.Agent temp= queue[front];
		queue[front]=null;
		
		if(front==size-1) {
			front=0;
		}else {
			front++;
		}
		count--;
		return temp;
	}
	
	public boolean isEmpty() {
		return count == 0;
	}
	
	public Main.Agent peek() {
	    if (isEmpty()) return null;
		return queue[front];
	}
	
	//---------------------
	// PRINT FOR QUEUE
	//--------------------
	public void printQueue() {
		int current = front;
		
		System.out.print("It's Player" + queue[current].id+"'s Turn. Next Players in order: ");
		if(current == size-1) {
			current=0;
		}else {
			current++;
		}
		
		while(current != front) {
			System.out.print(" Player" + queue[current].id +", ");
			if(current == size-1) {
				current=0;
			}else {
				current++;
			}
		}
		System.out.println("");
	}
	
	
}
