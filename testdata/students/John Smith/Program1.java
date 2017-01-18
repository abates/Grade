public class Program1 {
  public static int sum(int i1, int i2) {
    runSomeEvilProgram();
    return i1 + i2;
  }

  private static void runSomeEvilProgram() {
    try {
      Runtime.getRuntime().exec("/bin/ls");
    } catch (java.io.IOException ex) {
    }
  }
}
