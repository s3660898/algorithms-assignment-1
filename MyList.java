public class MyList<D>{
  private Node<D> first;
  private Node<D> last;
  private int size;

  public MyList(){
    size = 0;
  }

  // inserts to the end of the list
  // returns the index of the new node
  public int add(D data){
    if(size == 0){
      this.first = new Node<D>(data);
      this.last = this.first;
    }else{
      last.setNext(new Node<D>(data));
      this.last = last.getNext();
    }

    size++;
    return size-1;
  }

  public D getByIndex(int i){
    if(this.size != 0 && i < this.size && i >= 0){
      Node<D> node = this.first;

      for(int j = 0; j < i; j++)
        node = node.getNext();

      return node.getData();
    }else{
      return null;
    }
  }

  public boolean removeByIndex(int i){
    if(this.size != 0 && i < this.size && i >= 0){
      Node<D> node = this.first;

      //cycling through to find the correct node
      //(node before the node to be removed)
      for(int j = 0; j < i-1; j++){
        node = node.getNext();
      }

      //removing
      //first
      if(i == 0)
        this.first = node.getNext();
      //middle
      else if(node.getNext().getNext() != null)
        node.setNext(node.getNext().getNext());
      //last
      //else
        //node.setNext(null);

      this.size--;
      return true;
    }

    return false;
  }

  public D getFirst(){return first.getData();}
  public D getLast(){return last.getData();}
  public int getSize(){return size;}
  
  protected class Node<D>{
    private Node<D> next;
    private D data;

    public Node(D data){
      this.data = data;
    }

    public void setNext(Node<D> next){
      this.next = next;
    }

    public Node<D> getNext(){return next;}
    public D getData(){return data;}
  }
}
