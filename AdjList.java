import java.io.*;
import java.util.*;

/**
 * Adjacency list implementation for the AssociationGraph interface.
 *
 * Your task is to complete the implementation of this class.  You may add methods, but ensure your modified class compiles and runs.
 *
 * @author Jeffrey Chan, 2019.
 */
public class AdjList extends AbstractAssocGraph
{
  //hashmap only here because of the slides on the assignment
  private HashMap<String, Integer> keyVals;
  private Vertex[] verts;

  //for testing/analysis

  public int edges;

  public static final int OP_RV = 0;
  public static final int OP_RE = 1;
  public static final int OP_IN = 2;
  public static final int OP_ON = 3;
  public static final int OP_U  = 4;

  static public void record(int operation, long startNs, long endNs, int verts, int edges){
    FileWriter fw;
    PrintWriter pw;

    float density = (float)edges/(float)verts;
    long timeNs = endNs - startNs;

    switch(operation){
      case OP_RV:
        try{fw = new FileWriter("results/a_rv.csv", true);}catch(Exception E){break;}
        pw = new PrintWriter(fw);
        pw.println(density + "," + timeNs);
        pw.close();
        break;
      case OP_RE:
        try{fw = new FileWriter("results/a_re.csv", true);}catch(Exception E){break;}
        pw = new PrintWriter(fw);
        pw.println(density + "," + timeNs);
        pw.close();
        break;
      case OP_IN:
        try{fw = new FileWriter("results/a_in.csv", true);}catch(Exception E){break;}
        pw = new PrintWriter(fw);
        pw.println(density + "," + timeNs);
        pw.close();
        break;
      case OP_ON:
        try{fw = new FileWriter("results/a_on.csv", true);}catch(Exception E){break;}
        pw = new PrintWriter(fw);
        pw.println(density + "," + timeNs);
        pw.close();
        break;
      case OP_U:
        try{fw = new FileWriter("results/a_u.csv", true);}catch(Exception E){break;}
        pw = new PrintWriter(fw);
        pw.println(density + "," + timeNs);
        pw.close();
        break;
    }
  }

  /**
 * Contructs empty graph.
 */
  public AdjList() {
    keyVals = new HashMap<String, Integer>();
    verts = new Vertex[256];
    edges = 0;
  } // end of AdjList()


  public void addVertex(String vertLabel) {
    //finds first empty slot
    for(int i = 0; i < this.verts.length; i++){
      if(verts[i] == null){
        verts[i] = new Vertex();
        keyVals.put(vertLabel, i);
        return;
      }
    }

    //no free space found: doubling array
    Vertex[] tmpVerts = this.verts;
    this.verts = new Vertex[this.verts.length * 2];

    //copying the array across
    for(int i = 0; i < tmpVerts.length; i++)
      this.verts[i] = tmpVerts[i];

    //new vertex
    verts[tmpVerts.length] = new Vertex();
    keyVals.put(vertLabel, tmpVerts.length);
  } // end of addVertex()


  public void addEdge(String srcLabel, String tarLabel, int weight) {
    //given both verts exist in the lookup table
    if(keyVals.containsKey(srcLabel) && keyVals.containsKey(tarLabel) && weight != 0){

      //looking for dupes
      Vertex.Node node = verts[keyVals.get(srcLabel)].getFirst();
      if(node != null){
        do{
          if(node.getPair().getKey().equals(tarLabel))
            return;

          node = node.getNext();
        }while(node != null);
      }
      
      verts[keyVals.get(srcLabel)].add(tarLabel, weight);
      edges++;
    }
  } // end of addEdge()


  public int getEdgeWeight(String srcLabel, String tarLabel) {
    if(keyVals.containsKey(srcLabel)){
      Vertex.Node node = verts[keyVals.get(srcLabel)].getNodeByTarLabel(tarLabel);

      if(node != null)
        return node.getPair().getValue();
    }

    // update return value
    return EDGE_NOT_EXIST;
  } // end of existEdge()


  public void updateWeightEdge(String srcLabel, String tarLabel, int weight) {
    int vertNo = keyVals.size();
    long startNs = System.nanoTime();

    if(weight == 0){
      verts[keyVals.get(srcLabel)].removeByTarLabel(tarLabel);
      edges--;
      record(OP_RE, startNs, System.nanoTime(), vertNo, edges);
    }else if(weight > 0 && keyVals.containsKey(srcLabel)){
      Vertex.Node node = verts[keyVals.get(srcLabel)].getNodeByTarLabel(tarLabel);

      if(node != null)
        node.getPair().setValue(weight);

        record(OP_U, startNs, System.nanoTime(), vertNo, edges);
    }
  } // end of updateWeightEdge()


  public void removeVertex(String vertLabel) {
    int vertNo = keyVals.size();
    long startNs = System.nanoTime();

    if(keyVals.containsKey(vertLabel)){
      //removing vert
      edges -= verts[keyVals.get(vertLabel)].getSize();
      verts[keyVals.get(vertLabel)] = null;
      keyVals.remove(vertLabel);

      //removing related edges (inwards pointing)
      for(int i = 0; i < this.verts.length; i++)
        if(verts[i] != null){
          if(verts[i].removeByTarLabel(vertLabel))
            edges--;
        }

      record(OP_RV, startNs, System.nanoTime(), vertNo, edges);
    }
  } // end of removeVertex()


  public List<MyPair> inNearestNeighbours(int k, String vertLabel) {
    int vertNo = keyVals.size();
    long startNs = System.nanoTime();
    List<MyPair> neighbours = new ArrayList<MyPair>();

    //counting in neighbours
    int count = 0;
    for(int i : keyVals.values())
      if(verts[i].getNodeByTarLabel(vertLabel) != null)
        count++;


    //adding neighbour MyPairs to temporary array
    MyPair[] tmpArr = new MyPair[count];
    Boolean[] tmpChecks = new Boolean[count];
    int j = 0;

    Vertex.Node node;
    for(String key: keyVals.keySet()){
      node = verts[keyVals.get(key)].getNodeByTarLabel(vertLabel);
      if(node != null){
        MyPair tmpPair = new MyPair(key, node.getPair().getValue());
        tmpArr[j] = tmpPair;
        tmpChecks[j++] = false;
      }
    }

    //k switching and list assembling
    if(k == -1)
      for(MyPair pair : tmpArr)
        neighbours.add(pair);
    else if(k > 0){
      while(neighbours.size() < k){
        //finding the next largest weight
        int weightMax = 0, largestPairIndex = 0;
        for(int i = 0; i < count; i++){
          if(tmpArr[i].getValue() >= weightMax && tmpChecks[i] == false){
            weightMax = tmpArr[i].getValue();
            largestPairIndex = i;
          }
        }

        //early finish if all available edges added
        if(weightMax == 0)
          break;

        //adding the next largest to the returned list
        neighbours.add(tmpArr[largestPairIndex]);
        tmpChecks[largestPairIndex] = true;
      }
    }

    record(OP_IN, startNs, System.nanoTime(), 1, k);
    return neighbours;
  } // end of inNearestNeighbours()


  public List<MyPair> outNearestNeighbours(int k, String vertLabel) {
    int vertNo = keyVals.size();
    long startNs = System.nanoTime();
    List<MyPair> neighbours = new ArrayList<MyPair>();

    //getting the relavent vertex
    if(keyVals.containsKey(vertLabel)){

      //k switching
      if(k == -1){
        Vertex.Node node = verts[keyVals.get(vertLabel)].getFirst();

        //printing
        if(node != null){
          //returning all
          do{
            neighbours.add(node.getPair());
            node = node.getNext();
          }while(node != null);
        }

      }else if(k >= 0){
        int count = 0;

        //counting edges
        Vertex.Node firstNode = verts[keyVals.get(vertLabel)].getFirst();
        Vertex.Node node = firstNode;

        if(node != null){
          //counting edges
          do{
            count++;
            node = node.getNext();
          }while(node != null);

          //copying to temporary array
          MyPair[] tmpArr = new MyPair[count];
          Boolean[] tmpChecks = new Boolean[count];

          node = firstNode;
          for(int i = 0; i < count; i++){
            tmpArr[i] = node.getPair();
            tmpChecks[i] = false;
            node = node.getNext();
          }

          //assembling list
          while(neighbours.size() < k){
            //finding the next largest weight
            int weightMax = 0, largestPairIndex = 0;
            for(int i = 0; i < count; i++){
              if(tmpArr[i].getValue() >= weightMax && tmpChecks[i] == false){
                weightMax = tmpArr[i].getValue();
                largestPairIndex = i;
              }
            }

            //early finish if all available edges added
            if(weightMax == 0)
              break;

            //adding the next largest to the returned list
            neighbours.add(tmpArr[largestPairIndex]);
            tmpChecks[largestPairIndex] = true;
          }

        }
      }
    }

    record(OP_ON, startNs, System.nanoTime(), 1, k);
    return neighbours;
  } // end of outNearestNeighbours()


  public void printVertices(PrintWriter os) {
    for(String key : keyVals.keySet())
      os.print(key + " ");
    os.println();
  } // end of printVertices()


  public void printEdges(PrintWriter os) {
    Vertex.Node node;

    for(String srcLabel : keyVals.keySet()){
      node = verts[keyVals.get(srcLabel)].getFirst();
 
      if(node != null){
        do{
          os.println(srcLabel + " " + node.getPair().getKey() + " " + node.getPair().getValue());
          node = node.getNext();
        }while(node != null);
      }
    }
  } // end of printEdges()

} // end of class AdjList
