/**
 * Orie Shaked
 */


/**
 * BinomialHeap
 *
 * An implementation of binomial heap over non-negative integers.
 * Based on exercise from previous semester.
 */
public class BinomialHeap
{
	public int size;
	public HeapNode last;
	public HeapNode min;

	/**
	 * 
	 * pre: key > 0
	 *
	 * Insert (key,info) into the heap and return the newly generated HeapItem.
	 * time complexity: O(logn)
	 */
	public HeapItem insert(int key, String info) 
	{   
		// create new binomial heap with one node for the inserted item, with rank 0
		HeapItem newItem = new HeapItem(null, key, info);

		HeapNode newNode = new HeapNode(newItem, null, null, null, 0);
		// the new node is its own next node
		newNode.next = newNode;

		// the HeapNode in which the HeapItem is stored in
		newItem.node = newNode;

		// create new binomial heap with the new node as the last node
		BinomialHeap newHeap = new BinomialHeap();
		newHeap.last = newNode;
		newHeap.size = 1;
		newHeap.min = newNode;

		// meld the new heap with the current heap
		meld(newHeap); 
		return newItem;
	}

	/**
	 * 
	 * Delete the minimal item
	 * time complexity: O(logn)
	 */ 
	public void deleteMin()
	{
		// if tree is empty we do nothing
		if (empty()) {
			return; // Heap is empty, nothing to delete
		}
		//Find the tree before the minimum Tree
		HeapNode beforeMin = min.next;
		while (beforeMin.next != min) {
			beforeMin = beforeMin.next;
		}
		//if minimum Tree is the only tree in the heap:
		if (beforeMin==min){
			//if minimum Tree is of rank 0:
			if (min.child == null) {
				min = null;
				last = null;
				min = null;
				size -= 1;
			}
			//if minTree had children:
			else{
				last = min.child;
				min.child.parent = null;
				//min.child = null;
				findNewMin();
				size -= 1;
			}	
		}
		//there is more than one tree in the heap
		else {
			//if minTree is rank 0:
			if (min.child == null) {
				beforeMin.next = min.next;
				findNewMin();
				size -= 1;
			}
			//if minTree had children:
			else {
				beforeMin.next = min.next;
				//if the min tree is also last
                if (this.last == this.min){
					HeapNode temp = this.last.next;
                    this.last = beforeMin;
					this.last.next = temp;
                }
				// we create new BinomialHeap to represent the children of deleted root
				BinomialHeap newHeap = new BinomialHeap();
				newHeap.last = min.child;
				newHeap.size = (int)(Math.pow(2, min.rank)) - 1;
                this.size -= (int)(Math.pow(2, min.rank));
                newHeap.findNewMin();
				min.child.parent = null;
                this.findNewMin();
				// we meld together the children of min and the rest of the original heap
				meld(newHeap);
			}	
		}
	}
	
	/**
	 * 
	 * Return the minimal HeapItem of the new Tree
	 * time complexity: O(logn)
	 */
	private void findNewMin()
	{
		// if our tree is empty we do nothing
		if (this.empty()) {
			return;
		}
		// initialize new HeapNofe to traverse the roots of our heap
		HeapNode currentNode = new HeapNode();
		currentNode = last;
		// our temp min is the last root
		this.min = last;
		while (currentNode.next != last){
			currentNode = currentNode.next;
			if (currentNode.item.key < min.item.key){
				this.min = currentNode;
			}	
		}
	} 

	/**
	 * 
	 * Return the minimal HeapItem
	 * time complexity: O(1)
	 */
	public HeapItem findMin()
	{
		return min.item;
	} 

	/**
	 * 
	 * pre: 0 < diff < item.key
	 * 
	 * Decrease the key of item by diff and fix the heap. 
	 * time complexity: O(logn)
	 */
	public void decreaseKey(HeapItem item, int diff) 
	{    
		item.key -= diff; // Decrease the key
		// Fix the heap if necessary
		HeapNode currentNode = item.node;
		while (currentNode.parent != null && currentNode.item.key < currentNode.parent.item.key) {
			// Swap the item with its parent
			HeapItem tempItem = currentNode.item;
			currentNode.item = currentNode.parent.item;
			currentNode.parent.item = tempItem;

			// Update the reference in the nodes
			HeapNode tempNode = currentNode.item.node;
			currentNode.item.node = currentNode.parent.item.node;
			currentNode.parent.item.node = tempNode;
		
			currentNode = currentNode.parent;
		}
		//change the min if necessary
		if (item.key < min.item.key) {
			min.item = item; // Update the min node if necessary
		}
	}

	/**
	 * 
	 * Delete the item from the heap.
	 * time complexity: O(logn)
	 */
	public void delete(HeapItem item) 
	{   
		// decrease the key of the item to the new minimum value and delete the minimal item
		int minValue = min.item.key;
		int difference = item.key - minValue;
		decreaseKey(item, difference + 1);
		deleteMin();
	}
	
	/**
	 * 
	 * meld heap with heap2
	 * time complexity: O(logn)
	 */
	public void meld(BinomialHeap heap2)
	{
		//if both trees are empty or just heap2 is empty we do nothing: 
		if ((this.empty() && heap2.empty()) || heap2.empty()) {
			return;
		}
		//if only this is empty
		if (this.empty()) {
			this.last = heap2.last;
			this.min = heap2.min;
			this.size = heap2.size;
			return;
		}
		
		// we initialize 3 new heaps, the first is our result and the others are helpers
		BinomialHeap meldedHeap = new BinomialHeap();
		BinomialHeap lowerHeap = (this.last.rank <= heap2.last.rank) ? this : heap2;
		BinomialHeap higherHeap = (heap2.last.rank >= this.last.rank) ? heap2 : this;

		//we save our size of this
		int newSize = this.size + heap2.size;

		// lets inititalize a few pointers:
		HeapNode currLower = lowerHeap.last.next;
		HeapNode currHigher = higherHeap.last.next;
		HeapNode nextHigher = null;
		HeapNode nextLower = null;

		//lets initialize a remainder:
		HeapNode remainder = null;

		// we traverse as long as we have roots in the lowerHeap
		while (!lowerHeap.empty()) {
			//if we have a remainder
			if (remainder != null) {
				// if the ranks of currLower and currHigher are equal
				if (currLower.rank == currHigher.rank) {
					// we move both pointers of low and high up
					nextLower = currLower.next;
					nextHigher = currHigher.next;
					meldedHeap.addTreeToHeap(remainder);

					//we decrease the size of the trees from the heaps
					lowerHeap.size -= (int)Math.pow(2, currLower.rank);
					higherHeap.size -= (int)Math.pow(2, currHigher.rank);
					// we make new remainder
					remainder = HeapNode.link(currLower, currHigher);

					currLower = nextLower;
					currHigher = nextHigher;
				}
				// if the rank of remainder equals to the rank of currLower
				else if (currLower.rank == remainder.rank) {
					// we only move the pointer of low up
					nextLower = currLower.next;
					//we decrease the size of the tree from the heap
					lowerHeap.size -= (int)Math.pow(2, currLower.rank);
					// we make new remainder
					remainder = HeapNode.link(currLower, remainder);

					currLower = nextLower;
				}
				// if the rank of remainder equals to the rank of currHigher
				else if (currHigher.rank == remainder.rank) {
					// we only move up the pointer of nextHigher
					nextHigher = currHigher.next;
					higherHeap.size -= (int)Math.pow(2, currHigher.rank);
					// we make new remainder
					remainder = HeapNode.link(currHigher, remainder);

					currHigher = nextHigher;
				}
				// if the ranks of low and high are both bigger than remainder
				else { 
					meldedHeap.addTreeToHeap(remainder);
					remainder = null;
				}
			}
			else { // there is no remainder
				// the ranks of low and high are the same
				if (currLower.rank == currHigher.rank) {
					//we move up the pointers of both low and high
					nextLower = currLower.next;
					nextHigher = currHigher.next;
					lowerHeap.size -= (int)Math.pow(2, currLower.rank);
					higherHeap.size -= (int)Math.pow(2, currHigher.rank);
					// we make new remainder
					remainder = HeapNode.link(currLower, currHigher);

					currLower = nextLower;
					currHigher = nextHigher;
				}
				// the rank of low is smaller than that of high
				else if (currLower.rank < currHigher.rank) {
					nextLower = currLower.next;
					lowerHeap.size -= (int)Math.pow(2, currLower.rank);
					meldedHeap.addTreeToHeap(currLower);
					currLower = nextLower;
				}
				else { //rank of lower is bigger than higher
					nextHigher = currHigher.next;
					higherHeap.size -= (int)Math.pow(2, currHigher.rank);
					meldedHeap.addTreeToHeap(currHigher);
					currHigher = nextHigher;
				}
			}
		}
		// we now traverse higherHeap until it is empty
		while (!higherHeap.empty()) {
			//if we have a remainder
			if (remainder != null) {
				// the ranks of the remainder and currHigher are same
				if (remainder.rank == currHigher.rank) {
					nextHigher = currHigher.next;
					higherHeap.size -= (int)Math.pow(2, currHigher.rank);
					//we make new remainder
					remainder = HeapNode.link(remainder,currHigher);

					currHigher = nextHigher;
				}
				else { // rank of remainder is smaller of that of currHigher
					meldedHeap.addTreeToHeap(remainder);
					remainder = null;
				}
			}
			else { //there is no remainder
				nextHigher = currHigher.next;
				higherHeap.size -= (int)Math.pow(2, currHigher.rank);
				meldedHeap.addTreeToHeap(currHigher);
				currHigher = nextHigher;
			}
		}
		// if after everything we still have a remainder:
		if (remainder != null) {
			meldedHeap.addTreeToHeap(remainder);
		}

		// reset info about our heap:
		this.last = meldedHeap.last;
		this.min = meldedHeap.min;
		this.size = newSize;
	}

	/**
	 * 
	 * insert a tree in a heap
	 * time complexity: O(1)
	 */
	private void addTreeToHeap(HeapNode treeToAdd) {
		//if the heap was initially empty
		if (this.empty()) {
			this.last = treeToAdd;
			this.last.next = this.last;
			this.min = treeToAdd;	
		}
		// there are other trees in the heap
		else{
			HeapNode temp = this.last.next;
			this.last.next = treeToAdd;
			treeToAdd.next = temp;
			this.last = treeToAdd;
			//update min if neccessary:
			if(treeToAdd.item.key < min.item.key) {
				this.min = treeToAdd;
			}
		}
		//update the size of the heap
		this.size += (int)Math.pow(2,treeToAdd.rank);
	}


	/**
	 * 
	 * Return the number of elements in the heap
	 * time complexity: O(1)
	 */
	public int size()
	{
		return size;
	}

	/**
	 * 
	 * The method returns true if and only if the heap
	 * is empty.
	 * time complexity: O(1)  
	 */
	public boolean empty()
	{
		return size() == 0;
	}

	/**
	 * 
	 * Return the number of trees in the heap.
	 * time complexity: O(logn)
	 */
	public int numTrees()
	{
		// if the heap is empty, return 0
		if(empty()) return 0;

		// start from the last node and count the number of trees by following the next pointers
		int numOftrees = 1;
		HeapNode currNode = last;

		while(currNode.next != last){
			numOftrees++;
			currNode = currNode.next;
		}
		return numOftrees;
	}

	
	/**
	 * Class implementing a node in a Binomial Heap.
	 *  
	 */
	public class HeapNode{
		public HeapItem item;
		public HeapNode child;
		public HeapNode next;
		public HeapNode parent;
		public int rank;

		// default constructer
		public HeapNode() {
		}

		/**
		 * Constructor for HeapNode class with parameters.
		 * time complexity: O(1)
		 */
		public HeapNode(HeapItem item, HeapNode child, HeapNode next, HeapNode parent, int rank) {
			this.item = item;
			this.child = child;
			this.next = next;
			this.parent = parent;
			this.rank = rank;
		}

		/**
		 * method two link together two different HeapNodes of similar rank
		 * time complexity: O(1)
		 */
		public static HeapNode link(HeapNode tree1, HeapNode tree2){
			//we make sure that both trees dont have nexts
			tree1.next = null;
			tree2.next = null;
			if (tree1.item.key > tree2.item.key) {
				// swap the trees so that tree1 is the smaller one
				HeapNode temp = tree1;
				tree1 = tree2;
				tree2 = temp;
			}
			// make tree2 the child of tree1
			if (tree1.child == null){  //two of the trees are of rank 0
				tree2.next = tree2;
			}
			// if the ranks are higher than 0
			else{
				tree2.next = tree1.child.next;
				tree1.child.next = tree2;
			}
			tree1.child = tree2;
			tree1.rank++; 
			tree2.parent = tree1;

			return tree1;
		}
	}

	/**
	 * Class implementing an item in a Binomial Heap.
	 *  
	 */
	public class HeapItem{
		public HeapNode node;
		public int key;
		public String info;

		/**
		 * Constructor for HeapItem class with parameters.
		 * time complexity: O(1)
		 */
		public HeapItem(HeapNode node, int key, String info) {
			this.node = node;
			this.key = key;
			this.info = info;
		}
	}
}
