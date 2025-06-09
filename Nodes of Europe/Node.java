import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Node extends Circle {

	private String name;
	private boolean selectColor = false;

	public Node(String name, double x, double y) {

		super(x, y, 8);
		this.name = name;
		this.setId(name);
		setFill(Color.BLUE);

	}
	
	public void setColor(boolean color) {
		this.selectColor = color;
		if(color) {
			this.setFill(Color.RED);
		} else {
			this.setFill(Color.BLUE);
		}
	}

	public boolean isColor() {
		return selectColor;
	}

	public String getName() {
		return name;
	}
	
	public String toString() {
		return name;
	}

}