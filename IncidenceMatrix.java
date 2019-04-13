import java.io.*;
import java.util.*;


/**
 * Incident matrix implementation for the AssociationGraph interface.
 *
 * Your task is to complete the implementation of this class.  You may add methods, but ensure your modified class compiles and runs.
 *
 * @author Jeffrey Chan, 2019.
 */
public class IncidenceMatrix extends AbstractAssocGraph
{
  int graph[][];                      //the actual matrix

  HashMap<String, Integer> keyVals;   //the vertex names along the vertical axis
  HashMap<Integer, String> valKey;    //opposite hashmap of kayVals

  HashMap<Edge, Integer>   edgeVals;  //the edge names
  HashMap<Integer, Edge>   valEdge;   //opposite hashmap of edgeVal

	/**
	 * Contructs empty graph.
	 */
    public IncidenceMatrix() {
      graph = new int[256][256];
      keyVals = new HashMap<String, Integer>();
      valKey = new HashMap<Integer, String>();
      edgeVals = new HashMap<Edge, Integer>();
      valEdge = new HashMap<Integer, Edge>();
    } // end of IncidentMatrix()


    public void addVertex(String vertLabel) {
      boolean exists;

      //early exit if vertex already exists
      if(keyVals.containsKey(vertLabel))
        return;

      //fiding vacancy and putting
      for(int i = 0; i < graph.length; i++){
        if(valKey.get(i) == null){
          keyVals.put(vertLabel, i);
          valKey.put(i, vertLabel);
          return;
        }
      }

      //case didn't find a vacancy, expand graph columns, preserving row legth
      int tmpGraph[][] = new int[graph.length * 2][graph[0].length];
      for(int i = 0; i < graph.length; i++)
        for(int j = 0; j < graph[0].length; j++)
          tmpGraph[i][j] = graph[i][j];
      
      //inserting new value
      keyVals.put(vertLabel, graph.length);
      valKey.put(graph.length, vertLabel);

      //swap graph reference
      graph = tmpGraph;
    } // end of addVertex()


    public void addEdge(String srcLabel, String tarLabel, int weight) {
      Integer i_src, i_tar;

      // checking verts exist
      i_src = keyVals.get(srcLabel);
      i_tar = keyVals.get(tarLabel);
      if(i_src != null && i_tar != null){

        //early exit case edge already exists
        if(edgeVals.containsKey(new Edge(srcLabel, tarLabel)))
          return;

        //fiding vacancy and putting
        for(int i = 0; i < graph[0].length; i++){
          if(valEdge.get(i) == null){
            Edge tmpEdge = new Edge(srcLabel, tarLabel);
            edgeVals.put(tmpEdge, i);
            valEdge.put(i, tmpEdge);
            graph[i_src][i] = weight;
            graph[i_tar][i] = -weight;
            //System.out.println(srcLabel + " " + tarLabel + " " + weight +" - vac found!");
            return;
          }
        }
        
        //System.out.println(srcLabel + " " + tarLabel + " " + weight +" - expanding edges!!");
        //if no vacancy, expand row length, preserving column length
        int tmpGraph[][] = new int[graph.length][graph[0].length * 2];
        for(int i = 0; i < graph.length; i++)
          for(int j = 0; j < graph[0].length; j++)
            tmpGraph[i][j] = graph[i][j];

        //inserting new edge
        Edge tmpEdge = new Edge(srcLabel, tarLabel);
        edgeVals.put(tmpEdge, graph.length);
        valEdge.put(graph.length, tmpEdge);
        tmpGraph[i_src][graph.length] = weight;
        tmpGraph[i_tar][graph.length] = -weight;

        //swap graph reference
        graph = tmpGraph;
      }
    } // end of addEdge()


	public int getEdgeWeight(String srcLabel, String tarLabel) {
    //if both verts exist
    if(keyVals.containsKey(srcLabel) && keyVals.containsKey(tarLabel)){

      //finding the an edge column
      for(Edge e : edgeVals.keySet()){
        if(e.contains(srcLabel) && e.contains(tarLabel)){
          int val = graph[keyVals.get(srcLabel)][edgeVals.get(e)];

          if(val != 0)
            return val;
          else
            return EDGE_NOT_EXIST;
        }
      }

    }

		// update return value
		return EDGE_NOT_EXIST;
	} // end of existEdge()


	public void updateWeightEdge(String srcLabel, String tarLabel, int weight) {
      Integer i_src, i_tar;

      // checking verts exist
      i_src = keyVals.get(srcLabel);
      i_tar = keyVals.get(tarLabel);
      if(i_src != null && i_tar != null){
        Edge key = new Edge(srcLabel, tarLabel);
        int j = edgeVals.get(key);

        //edge removal
        if(weight == 0){
          graph[i_src][j] = 0;
          graph[i_tar][j] = 0;
          edgeVals.remove(key);
          valEdge.remove(j);
          return;
        }

        //else updating
        graph[i_src][j] = weight;
        graph[i_tar][j] = -weight;
      }
    } // end of updateWeightEdge()


    public void removeVertex(String vertLabel) {
      Integer i = keyVals.get(vertLabel);
      //if exists
      if(i != null){

        //setting all its edges to 0
        Edge keys[] = new Edge[edgeVals.keySet().size()];
        keys = edgeVals.keySet().toArray(keys);
        for(Edge e : keys){
          if(e.contains(vertLabel)){
            Integer j = edgeVals.get(e);

            //removing from graph
            graph[keyVals.get(e.getX())][j] = 0;
            graph[keyVals.get(e.getY())][j] = 0;

            //removing from edge/index hashmaps
            edgeVals.remove(e);
            valEdge.remove(j);
          }
        }
        
        //removing the vertex
        keyVals.remove(vertLabel);
        valKey.remove(i);
      }
    } // end of removeVertex()


	public List<MyPair> inNearestNeighbours(int k, String vertLabel) {
        List<MyPair> neighbours = new ArrayList<MyPair>();
        Integer i = keyVals.get(vertLabel);

        if(i != null){
          if(k == -1){
            for(int j = 0; j < graph[i].length; j++){
              int weight = graph[i][j];

              if(weight < 0){
                Edge e = valEdge.get(j);
                if(e.getX().equals(vertLabel)){
                  neighbours.add(new MyPair(e.getY(), -weight));
                }else{
                  neighbours.add(new MyPair(e.getX(), -weight));
                }
              }
            }
          }else if(k >= 0){
            int edgeCount = 0;

            //counting edges
            for(int j = 0; j < graph[i].length; j++){
              if(graph[i][j] < 0)
                edgeCount++;
            }

            boolean checks[] = new boolean[edgeCount];
            int     jEdge[] = new int[edgeCount];

            //setting checks to false
            for(int i2 = 0; i2 < edgeCount; i2++)
              checks[i2] = false;

            //getting edge-graph locations
            for(int j = 0,  i2 = 0; j < graph[i].length; j++)
              if(graph[i][j] < 0)
                jEdge[i2++] = j;

            System.out.println("len " + jEdge.length);
            System.out.println("0 " + jEdge[0]);

            int edgesAdded = 0;
            int maxVal = 0;
            int maxJ = 0;
            int maxI = 0;

            while(edgesAdded < k){
              //finding the max value for this cycle
              maxVal = 0;
              maxJ = -1;
              maxI = -1;
              for(int i2 = 0; i2 < jEdge.length; i2++){
                if(graph[i][jEdge[i2]] <= maxVal && checks[i2] != true){
                  maxVal = graph[i][jEdge[i2]];
                  maxJ = jEdge[i2];
                  maxI = i2;
                }
              }

              //ran out of edges
              if(maxJ == -1)
                return neighbours;

              checks[maxI] = true;

              Edge e = valEdge.get(maxJ);
              if(e.getX().equals(vertLabel))
                neighbours.add(new MyPair(e.getY(), -maxVal));
              else
                neighbours.add(new MyPair(e.getX(), -maxVal));
              edgesAdded++;
            }
          }
        }
        return neighbours;
    } // end of inNearestNeighbours()


    public List<MyPair> outNearestNeighbours(int k, String vertLabel) {
        List<MyPair> neighbours = new ArrayList<MyPair>();
        Integer i = keyVals.get(vertLabel);

        if(i != null){
          if(k == -1){
            for(int j = 0; j < graph[i].length; j++){
              int weight = graph[i][j];

              if(weight > 0){
                Edge e = valEdge.get(j);
                if(e.getX().equals(vertLabel)){
                  neighbours.add(new MyPair(e.getY(), weight));
                }else{
                  neighbours.add(new MyPair(e.getX(), weight));
                }
              }
            }
          }else if(k >= 0){
            int edgeCount = 0;

            //counting edges
            for(int j = 0; j < graph[i].length; j++){
              if(graph[i][j] > 0)
                edgeCount++;
            }

            boolean checks[] = new boolean[edgeCount];
            int     jEdge[] = new int[edgeCount];

            //setting checks to false
            for(int i2 = 0; i2 < edgeCount; i2++)
              checks[i2] = false;

            //getting edge-graph locations
            for(int j = 0,  i2 = 0; j < graph[i].length; j++)
              if(graph[i][j] > 0)
                jEdge[i2++] = j;

            System.out.println("len " + jEdge.length);
            System.out.println("0 " + jEdge[0]);

            int edgesAdded = 0;
            int maxVal = 0;
            int maxJ = 0;
            int maxI = 0;

            while(edgesAdded < k){
              //finding the max value for this cycle
              maxVal = 0;
              maxJ = -1;
              maxI = -1;
              for(int i2 = 0; i2 < jEdge.length; i2++){
                if(graph[i][jEdge[i2]] >= maxVal && checks[i2] != true){
                  maxVal = graph[i][jEdge[i2]];
                  maxJ = jEdge[i2];
                  maxI = i2;
                }
              }

              //ran out of edges
              if(maxJ == -1)
                return neighbours;

              checks[maxI] = true;

              Edge e = valEdge.get(maxJ);
              if(e.getX().equals(vertLabel))
                neighbours.add(new MyPair(e.getY(), maxVal));
              else
                neighbours.add(new MyPair(e.getX(), maxVal));
              edgesAdded++;
            }
          }
        }

        return neighbours;
    } // end of outNearestNeighbours()


    public void printVertices(PrintWriter os) {
      for(String key : keyVals.keySet()){
        System.out.print(key + " ");
      }
      System.out.println();
    } // end of printVertices()


    public void printEdges(PrintWriter os) {
      int xEdgeVal, yEdgeVal;
      // for all the edge columns in the graph
      for(Edge e : edgeVals.keySet()){
        System.out.println(e.getX() + " " + e.getY() + " " + graph[keyVals.get(e.getX())][edgeVals.get(e)]);
      }

    } // end of printEdges()


    //class that represents the two verticies that
    //an edge bridges
    protected class Edge{
      private String x; // index of 
      private String y;

      public Edge(String x, String y){
        this.x = x;
        this.y = y;
      }

      public String getX(){return x;}
      public String getY(){return y;}

      public boolean contains(String i){
        if(this.x.equals(i) || this.y.equals(i))
          return true;
        return false;
      }

      //no saftey because it will only be compared to other edges
      @Override
      public boolean equals(Object o){
        Edge edge = (Edge)o;
        return edge.getX().equals(this.x) && edge.getY().equals(this.y);
      }

      //lousy hashing
      @Override
      public int hashCode(){
        return 31 * x.hashCode() + y.hashCode();
        //return 31 * x + y;
      }
    }

} // end of class IncidenceMatrix
