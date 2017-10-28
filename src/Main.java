import syntaxtree.*;
import visitor.*;

public class Main {
    public static void main(String [] args)
    {
      try
      {
          Node root = new microIRParser(System.in).Goal();
          GJDepthFirst v1 = new GJDepthFirst();
          root.accept(v1,null);
          Pass_1 v2 = new Pass_1();
          root.accept(v2,null);
          System.out.println("Program parsed successfully");
      }
      catch (ParseException e)
      {
            System.out.println(e.toString());
      }
   }
}