package package1;

public class Main {
	private int result = 0;
	public int shared = 1;

	public static void main(String[] args) {
		System.out.println("Main method started.");
	}

	public void stupidMethod() {
		int a = 0;
		int b = 0;
		System.out.println(a + " " + b);
	}
}
