import java.util.Random;
import java.util.ArrayList;

public class DataGenerator{
  Random rand;

  ArrayList<Integer> vertList;
  ArrayList<Edge>    edgeList;

  public static void main(String[] args){
    int vertsNo, iterations;
    float edgeDensity;
    String command;

    try{
      if(args.length != 4)
        throw new Exception();

      vertsNo     = Integer.parseInt(args[0]);
      edgeDensity = Float.parseFloat(args[1]);

      command = args[2].toLowerCase();
      if(!(command.equals("re") || command.equals("rv") || command.equals("in") || command.equals("on") || command.equals("u")))
        throw new Exception();

      iterations  = Integer.parseInt(args[3]);

      if(vertsNo <= 0 || edgeDensity < 0 || iterations < 0)
        throw new Exception();

      if(command.equals("re") && iterations > vertsNo * edgeDensity)
        throw new Exception();
      else if(command.equals("rv") && iterations > vertsNo)
        throw new Exception();
      
    }catch(Exception e){
      System.out.print("usage: DataGenerator <no. of verts> <edge density> <command> <no. of iterations>\n\n" +
                       "       <no. of verts>:  Simply the number of verticies in the graph.\n" +
                       "       <edge density>:  The number of edges will equal (no. verts) * (edge density),\n" +
                       "                        edges will be randomly distributed throughout the vertices.\n" +
                       "            <command>:  (re/rv/in/on/u) remove edge, remove verts, nearest in, nearest out, update edge\n" +
                       "                        Choose a command even if no commands are intended to be generated, it doesn't matter.\n" +
                       "         <iterations>:  The number of iterations of the chosen command\n" + 
                       "                        Can't be more than the number of verts/ edges generated\n" +
                       "\n  Pipe the output of this program into a file then pipe that into the main graph program.\n");
      return;
    }

    DataGenerator dataGen = new DataGenerator();
    dataGen.generateData(vertsNo, edgeDensity, command, iterations);

    System.out.print("pv\npe\n");
  }

  public DataGenerator(){
    rand = new Random();
    vertList = new ArrayList<Integer>();
    edgeList = new ArrayList<Edge>();
  }

  public void generateData(int verticies, float density, String command, int iterations){
    int vertA, vertB;

    //making verts
    for(int i = 0; i < verticies; i++){
      vertList.add(i);
      System.out.println("av " + i);
    }

    //making random edges
    for(int i = 0; i < verticies * density; i++){
      //naiively getting two different random edges

      //making sure no dupe edges
      Edge tmpEdge;
      do{
        //getting unique verts
        do{
          vertA = rand.nextInt(verticies);
          vertB = rand.nextInt(verticies);
        }while(vertA == vertB);

        tmpEdge = new Edge(vertA, vertB);
      }while(edgeList.contains(tmpEdge));

      edgeList.add(new Edge(vertA, vertB));
      System.out.println("ae " + vertA + " " + vertB + " " + (rand.nextInt(49) + 1));
    }

    //iterations
    if(iterations > 0){
      switch(command){
        case "re":
          for(int i = 0; i < iterations; i++){
            Edge edge;
            try{
              edge = edgeList.get(0);
            }catch(Exception e){
              break;
            }

            edgeList.remove(0);
            System.out.println("u " + edge.getSrc() + " " + edge.getTar() + " 0");
          }
          break;

        case "rv":
          int vert;
          for(int i = 0; i < iterations; i++){
            try{
              vert = vertList.get(0);
            }catch(Exception e){
              break;
            }

            vertList.remove(0);
            System.out.println("rv " + vert);
          }
          break;

        case "in":
          for(int i = 0; i < iterations; i++){
            int vertName, num;
            try{
              vertName = vertList.get(0);
            }catch(Exception e){
              break;
            }

            num = rand.nextInt(10);
            if(num == 0)
              System.out.println("in -1 " + vertName);
            else
              System.out.println("in " + num + " " + vertName);

            vertList.remove(0);
          }
          break;

        case "on":
          for(int i = 0; i < iterations; i++){
            int vertName, num;
            try{
              vertName = vertList.get(0);
            }catch(Exception e){
              break;
            }

            num = rand.nextInt(density+1);
            if(num == 0)
              System.out.println("on -1 " + vertName);
            else
              System.out.println("on " + num + " " + vertName);

            vertList.remove(0);
          }
          break;

        case "u":
          for(int i = 0; i < iterations; i++){
            int num;
            Edge edge;
            try{
              edge = edgeList.get(0);
            }catch(Exception e){
              break;
            }

            System.out.println("u " + edge.getSrc() + " " + edge.getTar() + " " + (rand.nextInt(49) + 1));
            edgeList.remove(0);
          }
          break;
      }
    }
  }

  protected class Edge{

    private int src;
    private int tar;

    public Edge(int src, int tar){
      this.src = src;
      this.tar = tar;
    }

    public int getSrc(){return src;}
    public int getTar(){return tar;}

    @Override
    public boolean equals(Object o){
      Edge edge = (Edge)o;
      return edge.getSrc() == this.src && edge.getTar() == this.tar;
    }
  }
}
