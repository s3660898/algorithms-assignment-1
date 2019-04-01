public class Vertex{

  //List related
  private Node first;
  private Node last;
  private int size;

  public Vertex(){
  }

  public int getSize(){return size;}
  public Node getFirst(){return first;}

  public int add(String tarLabel, int weight){
    if(size == 0){
      this.first = new Node(tarLabel, weight);
      this.last = this.first;
    }else{
      last.setNext(new Node(tarLabel, weight));
      this.last = last.getNext();
    }

    this.size++;
    return this.size-1;
  }

  public Node getNodeByTarLabel(String tarLabel){
    Node node = this.first;

    if(node != null){
      do{
        if(node.getPair().getKey().equals(tarLabel))
          return node;

        node = node.getNext();
      }while(node != null);
    }
    return null;
  }

  public boolean removeByTarLabel(String tarLabel){
    Node node = this.first;

    if(node != null){
      // if first
      if(node.getPair().getKey().equals(tarLabel)){
        this.first = node.getNext();
        this.size--;
        return true;
      }else{
        do{
          //middle and end cases
          if(node.getNext() != null && node.getNext().getPair().getKey().equals(tarLabel)){
            node.setNext(node.getNext().getNext());
            this.size--;
            return true;
          }

          node = node.getNext();
        }while(node != null);
      }
    }

    return false;
  }


  protected class Node{
    private Node next;
    private MyPair pair;

    public Node(String tarLabel, int weight){
      pair = new MyPair(tarLabel, weight);
    }

    public void setNext(Node next){
      this.next = next;
    }

    public Node   getNext(){return next;}
    public MyPair getPair(){return pair;}
  }
}
