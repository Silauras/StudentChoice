package khai.edu.StudChoice.test;

public class TestLambdaClass {

    private int a;
    private int b;

    public TestLambdaClass(int a, int b) {
        this.a = a;
        this.b = b;
    }

    public void test(TestFunctionalInterface testFunctionalInterface) {
        System.out.println("test :" + testFunctionalInterface.calculate(a, b));
    }
}
