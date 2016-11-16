package application;

public class Test {

	public static void main(String[] args) {
		Adamar a = new Adamar(2,false);
		a.printMatrix();
		a = new Adamar(2,true);
		a.printMatrix();
		System.out.println("---------------------------------------------------");
		a = new Adamar(4,false);
		a.printMatrix();
		a = new Adamar(4,true);
		a.printMatrix();
		System.out.println("---------------------------------------------------");
		a = new Adamar(8,false);
		a.printMatrix();
		a = new Adamar(8,true);
		a.printMatrix();
		System.out.println("---------------------------------------------------");
	}

}
