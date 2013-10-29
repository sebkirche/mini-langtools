
/** Démonstration de visiteur
 * 
 * @author Sebastien
 * @see {@link http://fr.wikibooks.org/wiki/Patrons_de_conception/Visiteur}
 *
 */

public class VisitorDemo {
	static public void main(String[] args) {
		Car car = new Car();

		CarElementVisitor printVisitor = new CarElementPrintVisitor();
		CarElementVisitor doVisitor = new CarElementDoVisitor();

		printVisitor.visitCar(car);
		doVisitor.visitCar(car);
	}
}

interface CarElementVisitor {
	void visit(Wheel wheel);

	void visit(Engine engine);

	void visit(Body body);

	void visitCar(Car car);
}

interface CarElement {
	void accept(CarElementVisitor visitor);
	// Méthode à définir par les classes implémentant CarElements
}

class Wheel implements CarElement {
	private String name;

	Wheel(String name) {
		this.name = name;
	}

	String getName() {
		return this.name;
	}

	public void accept(CarElementVisitor visitor) {
		visitor.visit(this);
	}
}

class Engine implements CarElement {
	public void accept(CarElementVisitor visitor) {
		visitor.visit(this);
	}
}

class Body implements CarElement {
	public void accept(CarElementVisitor visitor) {
		visitor.visit(this);
	}
}

class Car {
	CarElement[] elements;

	public CarElement[] getElements() {
		return elements.clone(); // Retourne une copie du tableau de références.
	}

	public Car() {
		this.elements = new CarElement[] { new Wheel("front left"),
				new Wheel("front right"), new Wheel("back left"),
				new Wheel("back right"), new Body(), new Engine() };
	}
}

class CarElementPrintVisitor implements CarElementVisitor {
	public void visit(Wheel wheel) {
		System.out.println("Visiting " + wheel.getName() + " wheel");
	}

	public void visit(Engine engine) {
		System.out.println("Visiting engine");
	}

	public void visit(Body body) {
		System.out.println("Visiting body");
	}

	public void visitCar(Car car) {
		System.out.println("\nVisiting car");
		for (CarElement element : car.getElements()) {
			element.accept(this);
		}
		System.out.println("Visited car");
	}
}

class CarElementDoVisitor implements CarElementVisitor {
	public void visit(Wheel wheel) {
		System.out.println("Kicking my " + wheel.getName());
	}

	public void visit(Engine engine) {
		System.out.println("Starting my engine");
	}

	public void visit(Body body) {
		System.out.println("Moving my body");
	}

	public void visitCar(Car car) {
		System.out.println("\nStarting my car");
		for (CarElement carElement : car.getElements()) {
			carElement.accept(this);
		}
		System.out.println("Started car");
	}
}
