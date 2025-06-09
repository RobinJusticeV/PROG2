//Robin Akyuz #roak7366
//Ludvig Albertsson #lual2073
//Grupp 132

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.shape.Line;

public class PathFinder extends Application {

	private ListGraph<Node> graph = new ListGraph<>();

	private boolean changed = false;
	private Node from;
	private Node to;
	private BorderPane root;
	private Pane outputArea;
	private Stage primaryStage;
	private VBox vbox;
	private ClickHandler handler = new ClickHandler();
	private Button btnFindPath = new Button("Find Path");
	private Button btnShowConnection = new Button("Show Connection");
	private Button btnNewPlace = new Button("New Place");
	private Button btnNewConnection = new Button("New Connection");
	private Button btnChangeConnection = new Button("Change Connection");
	private ImageView imageView;

	@Override
	public void start(Stage primaryStage) {

		this.primaryStage = primaryStage;

		primaryStage.setTitle("PathFinder");

		root = new BorderPane();
		vbox = new VBox();
		root.setTop(vbox);
		outputArea = new Pane();
		root.setCenter(outputArea);
		outputArea.setId("outputArea");

		MenuBar menu = new MenuBar();
		menu.setId("menu");
		vbox.getChildren().add(menu);

		Menu menuFile = new Menu("File");
		menu.getMenus().add(menuFile);
		menuFile.setId("menuFile");
		MenuItem menuNewMap = new MenuItem("New Map");
		menuFile.getItems().add(menuNewMap);
		menuNewMap.setId("menuNewMap");
		menuNewMap.setOnAction(new NewHandler());
		MenuItem menuOpenFile = new MenuItem("Open");
		menuFile.getItems().add(menuOpenFile);
		menuOpenFile.setId("menuOpenFile");
		menuOpenFile.setOnAction(new OpenHandler());
		MenuItem menuSaveFile = new MenuItem("Save");
		menuFile.getItems().add(menuSaveFile);
		menuSaveFile.setId("menuSaveFile");
		menuSaveFile.setOnAction(new SaveHandler());
		MenuItem menuSaveImage = new MenuItem("Save Image");
		menuFile.getItems().add(menuSaveImage);
		menuSaveImage.setId("menuSaveImage");
		menuSaveImage.setOnAction(new ImageHandler());
		MenuItem menuExit = new MenuItem("Exit");
		menuFile.getItems().add(menuExit);
		menuExit.setId("menuExit");
		menuExit.setOnAction(new ExitItemHandler());

		FlowPane flow = new FlowPane();
		flow.getChildren().add(btnFindPath);
		btnFindPath.setId("btnFindPath");
		flow.getChildren().add(btnShowConnection);
		btnShowConnection.setId("btnShowConnection");
		flow.getChildren().add(btnNewPlace);
		btnNewPlace.setId("btnNewPlace");
		flow.getChildren().add(btnNewConnection);
		btnNewConnection.setId("btnNewConnection");
		flow.getChildren().add(btnChangeConnection);
		btnChangeConnection.setId("btnChangeConnection");
		flow.setAlignment(Pos.CENTER);
		flow.setHgap(10);
		flow.setPadding(new Insets(10, 10, 10, 10));

		btnNewPlace.setOnAction(new NewPlaceHandler());
		btnNewConnection.setOnAction(new NewConnectionHandler());
		btnShowConnection.setOnAction(new ShowConnectionHandler());
		btnFindPath.setOnAction(new FindPathHandler());
		btnChangeConnection.setOnAction(new ChangeConnectionHandler());
 
		imageView = new ImageView();
		outputArea.getChildren().add(imageView);

		vbox.getChildren().add(flow);

		Scene scene = new Scene(root, 600, 75);
		primaryStage.setScene(scene);
		primaryStage.setOnCloseRequest(new ExitHandler());
		primaryStage.show();

	}

	class SaveHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			try {
				FileWriter outfile = new FileWriter("europa.graph");
				PrintWriter out = new PrintWriter(outfile);
				out.println("file:europa.gif");
//				out.println(imageView.getImage().getUrl());
	
				for (Node node : graph.getNodes()) {
					out.print(node.getName() + ";" + node.getCenterX() + ";" + node.getCenterY() + ";");
				}
				out.println();

				ArrayList<Node> cityNode = new ArrayList<>();

				for (Node node : graph.getNodes()) {
					if (!cityNode.contains(node)) {
						cityNode.add(node);
					}
					for (Edge<Node> edge : graph.getEdgesFrom(node)) {
						out.println(node.getName() + ";" + edge.getDestination().getName() + ";" + edge.getName() + ";" + edge.getWeight());
					}
				}
				outfile.close();
				out.close();

			} catch (FileNotFoundException e) {
				Alert alert = new Alert(Alert.AlertType.ERROR, "File not found!");
				alert.showAndWait();
			} catch (IOException e) {
				Alert alert = new Alert(Alert.AlertType.ERROR, "IO-error " + e.getMessage());
				alert.showAndWait();
				e.printStackTrace();
			}
			changed = false;
		}
	}

	class NewPlaceHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {

			outputArea.setOnMouseClicked(handler);

			btnNewPlace.setDisable(true);

			outputArea.setCursor(Cursor.CROSSHAIR);
			
//			changed = true;
		}
	}

	class NewConnectionHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			
			if (from == null || to == null) {
				Alert alert = new Alert(Alert.AlertType.ERROR, "Two places must be selected!");
				alert.showAndWait();
			} else if (graph.pathExists(from, to)) {
				Alert alert = new Alert(Alert.AlertType.ERROR, "Connection already exists between selected nodes");
				alert.showAndWait();
			} else {
				try {
					NewConnectionDialog newCon = new NewConnectionDialog(from, to);
					Optional<ButtonType> result = newCon.showAndWait();
					if (result.isPresent() && result.get() == ButtonType.OK) {
						String name = newCon.getName();
						if (name.strip().isEmpty()) {
							Alert alert = new Alert(Alert.AlertType.ERROR, "Name must be entered!");
							alert.showAndWait();
						}
						int time = newCon.getTime();
						graph.connect(to, from, name, time);
						Line line = new Line(from.getCenterX(), from.getCenterY(), to.getCenterX(), to.getCenterY());
						outputArea.getChildren().add(line);
						line.setDisable(true);
						changed = true;
					}
				} catch (NumberFormatException e) {
					Alert alert = new Alert(Alert.AlertType.ERROR, "Time must be entered in numerals!");
					alert.showAndWait();
				}
			}
		}
	}

	class ShowConnectionHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			if (from == null || to == null) {
				Alert alert = new Alert(Alert.AlertType.ERROR, "Two places must be selected!");
				alert.showAndWait();
			}
			if (graph.getEdgeBetween(from, to) == null) {
				Alert alert = new Alert(Alert.AlertType.ERROR, "Edge doesn't exist between selected cities");
				alert.showAndWait();
			} else {
				Edge edge = graph.getEdgeBetween(from, to);

				NewConnectionDialog showCon = new NewConnectionDialog(from, to, edge.getName(), edge.getWeight());
				Optional<ButtonType> result = showCon.showAndWait();
			}

		}
	}

	class FindPathHandler implements EventHandler<ActionEvent> {

		public void handle(ActionEvent event) {
			if (from == null || to == null) {
				Alert alert = new Alert(Alert.AlertType.ERROR, "Two places must be selected!");
				alert.showAndWait();
			}
			if (!graph.pathExists(from, to)) {
				Alert alert = new Alert(Alert.AlertType.ERROR, "Connection doesn't exist");
				alert.showAndWait();
			} 
			
			List<Edge<Node>> path = graph.getPath(from, to);
			
			NewConnectionDialog pathCon = new NewConnectionDialog(from, to, graph.getPath(from, to));
			Optional<ButtonType> result = pathCon.showAndWait();
		}
	}

	class ChangeConnectionHandler implements EventHandler<ActionEvent> {

		public void handle(ActionEvent event) {
			if (from == null || to == null) {
				Alert alert = new Alert(Alert.AlertType.ERROR, "Two places must be selected!");
				alert.showAndWait();
			}

			if (graph.getEdgeBetween(from, to) == null) {
				Alert alert = new Alert(Alert.AlertType.ERROR, "Edge doesn't exist between selected cities");
				alert.showAndWait();
			} else {
				try {
					Edge<Node> edge = graph.getEdgeBetween(from, to);
					NewConnectionDialog changeCon = new NewConnectionDialog(from, to, edge.getWeight(), edge.getName());
					Optional<ButtonType> result = changeCon.showAndWait();

					changeCon.setName(edge.getName());

					int time = changeCon.getTime();
					graph.setConnectionWeight(from, to, time);
					
					changed = true;

				} catch (NumberFormatException e) {
					Alert alert = new Alert(Alert.AlertType.ERROR, "Time must be entered in numerals!");
					alert.showAndWait();
				}
			}
		}

	}

	class ClickHandler implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			TextInputDialog txt = new TextInputDialog();

			txt.setTitle("Name");
			txt.setContentText("Name of place:");
			txt.setHeaderText(null);

			Optional<String> input = txt.showAndWait();
			if (input.isEmpty()) {
				return;
			}

			double x = event.getX();
			double y = event.getY();

			Node node = new Node(input.get(), x, y);
			
			Label placeName = new Label(input.get());
			
			placeName.setLayoutX(x);
			placeName.setLayoutY(y);
			placeName.setDisable(true);
			placeName.setLabelFor(node);
			placeName.setOpacity(100);

			graph.add(node);

			outputArea.getChildren().add(node);

			outputArea.getChildren().add(placeName);
			
			outputArea.setOnMouseClicked(null);

			btnNewPlace.setDisable(false);

			outputArea.setCursor(Cursor.DEFAULT);

			node.setOnMouseClicked(new NodeClickHandler());

			changed = true;
		}

	}

	class NewHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			if (changed) {
				Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
				alert.setTitle("Warning!");
				alert.setHeaderText("Unsaved changes, exit anyway?");
				alert.setContentText(null);
				Optional<ButtonType> answer = alert.showAndWait();
				if (answer.isPresent() && answer.get() != ButtonType.OK) {
					event.consume();
					return;
				}
			}
			outputArea.getChildren().clear();
			graph = new ListGraph<>();
			from = null;
			to = null;
			Image image = new Image("file:europa.gif");
			
			outputArea.getChildren().add(imageView);
			
			imageView.setImage(image);
			
			primaryStage.setWidth(image.getWidth());

			primaryStage.setHeight(image.getHeight() + vbox.getHeight() + 20);

			changed = true;
		}
	}

	// HL

	class OpenHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			if (changed) {
				Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
				alert.setTitle("Warning!");
				alert.setHeaderText("Unsaved changes, open anyway?");
				alert.setContentText(null);
				Optional<ButtonType> answer = alert.showAndWait();
				if (answer.isPresent() && answer.get() != ButtonType.OK) {
					event.consume();
					return;
				}
			}
			try {
				outputArea.getChildren().clear();
				graph = new ListGraph<>();
				from = null;
				to = null;
				FileReader infile = new FileReader("europa.graph");
				BufferedReader in = new BufferedReader(infile);
				String url = in.readLine();
				imageView = new ImageView();
				Image image = new Image(url);
				imageView.setImage(image);
				outputArea.getChildren().add(imageView);
				
				String line = in.readLine();
				
				String[] lines = line.split(";");
				
				ArrayList<Node> nodes = new ArrayList<>();
				Map<String, Node> nodeByName = new HashMap<>();
				
				for(int i = 0; i < lines.length; i += 3) {
					String name = "";
					name = lines[i];
					double x;
					x = Double.parseDouble(lines[i + 1]);
					double y;
					y = Double.parseDouble(lines[i + 2]);
					Node node = new Node(name, x, y);
					node.setOnMouseClicked(new NodeClickHandler());
					nodes.add(node);
					nodeByName.put(name, node);
				}
				for(Node node : nodes) {
					graph.add(node);
					outputArea.getChildren().add(node);
					Label placeName = new Label(node.getName());
					
					placeName.setLayoutX(node.getCenterX());
					placeName.setLayoutY(node.getCenterY());
					placeName.setDisable(true);
					placeName.setLabelFor(node);
					placeName.setOpacity(100);
					outputArea.getChildren().add(placeName);
				}
				line = in.readLine();
				while(line != null) {
					String[] tokens = line.split(";");
					Node node1 = nodeByName.get(tokens[0]);
					Node node2 = nodeByName.get(tokens[1]);
					try {
					graph.connect(node1, node2, tokens[2], Integer.parseInt(tokens[3]));
					Line drawnLine = new Line(node1.getCenterX(), node1.getCenterY(), node2.getCenterX(), node2.getCenterY());
					drawnLine.setDisable(true);
					outputArea.getChildren().add(drawnLine);
					} catch (Exception e) {
						
					}
					line = in.readLine();
					
				}
				
				primaryStage.setWidth(image.getWidth());

				primaryStage.setHeight(image.getHeight() + vbox.getHeight() + 20);
				
				changed = false;
				
			} catch (Exception e) {
				Alert alert = new Alert(Alert.AlertType.ERROR, "File not found!");
				alert.setTitle("Error");
				alert.setHeaderText("Error");
				alert.showAndWait();
			}
		}
	}

	class ImageHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			try {
				WritableImage writeImage = outputArea.snapshot(null, null);
				BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writeImage, null);
				ImageIO.write(bufferedImage, "png", new File("capture.png"));
			} catch (IOException e) {
				Alert alert = new Alert(Alert.AlertType.ERROR, "IO-error " + e.getMessage());
				alert.showAndWait();
			}
		}
	}

	class ExitHandler implements EventHandler<WindowEvent> {

		@Override
		public void handle(WindowEvent event) {
			if (changed) {
				Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
				alert.setTitle("Warning!");
				alert.setHeaderText("Unsaved changes, exit anyway?");
				alert.setContentText("Unsaved changes");
				Optional<ButtonType> answer = alert.showAndWait();
				if (answer.isPresent() && answer.get() != ButtonType.OK) {
					event.consume();
				}
			}
		}
	}

	class ExitItemHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST));
		}
	}

	class NodeClickHandler implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			Node cityNode = (Node) event.getSource();
			if (cityNode.isColor()) {
				cityNode.setColor(false);
				if (cityNode.equals(from)) {
					from = null;
				} else {
					to = null;
				}
			} else if (from == null) {
				from = cityNode;
				from.setColor(true);
			} else if (to == null) {
				to = cityNode;
				to.setColor(true);
			}
		}

	}

	public static void main(String[] args) {
		launch(args);

	}
}
