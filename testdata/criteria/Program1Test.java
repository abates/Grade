import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class Program1Test {
  class TestData {
    int i1;
    int i2;
    int result;

    TestData(int i1, int i2, int result) {
      this.i1 = i1;
      this.i2 = i2;
      this.result = result; 
    }
  }

  @Test
  public void testAllLess() {
    TestData[] tests = new TestData[] {
      new TestData(1, 1, 2),
      new TestData(1, 2, 3),
      new TestData(100, 200, 300),
    };

    for (TestData test : tests) {
      assertEquals(test.result, Program1.sum(test.i1, test.i2));
    }
  }
}

