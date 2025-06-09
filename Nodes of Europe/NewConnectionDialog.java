import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

public class NewConnectionDialog extends Alert {
	private TextField nameField = new TextField();
	private TextField timeField = new TextField();
	
	public enum Val { NAME, WEIGHT, BOTH, NONE }

	public NewConnectionDialog(Node from, Node to, String name, int weight, Val val) {
		super(AlertType.CONFIRMATION);
		
		if(val != Val.NAME && val != Val.BOTH) {
			nameField.setEditable(false);
		} else {
			nameField.setText(name);
		}
		if(val != Val.WEIGHT && val != Val.BOTH) {
			timeField.setEditable(false);
		} else {
			timeField.setText("" + weight);
		}
		if(val == Val.NONE) {
			nameField.setEditable(false);
			timeField.setEditable(false);
			nameField.setText(name);
			timeField.setText("" + weight);
			
		} 
		if(val == Val.BOTH) {
			nameField.setEditable(true);
			timeField.setEditable(true);
		}
		if(val == Val.WEIGHT) {
			nameField.setEditable(false);
			timeField.setEditable(true);
			nameField.setText(name);
			timeField.setText("" + weight);
		}
		
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setPadding(new Insets(10));
		grid.setHgap(5);
		grid.setVgap(10);
		grid.addRow(0, new Label("Name:"), nameField);
		grid.addRow(1, new Label("Time:"), timeField);
		getDialogPane().setContent(grid);
		setTitle("Connection");
		setHeaderText("Connection from " + from.getName() + " to " + to.getName());
	}

	public NewConnectionDialog(Node from, Node to, List<Edge<Node>> getPath) {
		super(AlertType.INFORMATION);
		BorderPane pane = new BorderPane();
		
		getDialogPane().setContent(pane);
		setHeaderText("The Path from " + from.getName() + " to " + to.getName() + ":");
		
		StringBuilder pathStringBuilder = new StringBuilder();
		
		int totalWeight = 0;
		
		for(Edge<Node> edge : getPath) {
			pathStringBuilder.append(edge.toString() + "\n");
			totalWeight += edge.getWeight();
		}
		pathStringBuilder.append("Total ").append(totalWeight);
		
		TextArea area = new TextArea(pathStringBuilder.toString());
		pane.setCenter(area);
		area.setEditable(false);
	}
	
	public NewConnectionDialog(Node from, Node to, String name, int weight) {
		this(from, to, name, weight, Val.NONE);
	}
	
	public NewConnectionDialog(Node from, Node to) {
		this(from, to, null, 0, Val.BOTH);
	}
	
	public NewConnectionDialog(Node from, Node to, int weight, String name) {
		this(from, to, name, weight, Val.WEIGHT);
	}

	public String getName() {
		return nameField.getText();
	}

	public int getTime() {
		return Integer.parseInt(timeField.getText());
	}
	
	public void setName(String newName) {
		nameField.setText(newName);
		nameField.setEditable(false);
	}
	
}
