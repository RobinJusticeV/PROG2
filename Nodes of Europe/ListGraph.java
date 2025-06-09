import java.io.Serializable;
import java.util.*;

public class ListGraph<T> implements Graph<T>, Serializable {

	private Map<T, Set<Edge<T>>> adjacencyList = new HashMap<>();

	@Override
	public void add(T node) {
		adjacencyList.putIfAbsent(node, new HashSet<>());
	}

	@Override
	public void connect(T node1, T node2, String name, int weight) {
		if (!adjacencyList.containsKey(node1) || !adjacencyList.containsKey(node2)) {
			throw new NoSuchElementException("Node missing!");
		}
		if (weight < 0) {
			throw new IllegalArgumentException("Weight cannot be negative!");
		}
		if (getEdgeBetween(node1, node2) != null) {
			throw new IllegalStateException("Edge already exists!");
		}

		Edge<T> edge1 = new Edge<T>(name, weight, node2);
		Edge<T> edge2 = new Edge<T>(name, weight, node1);

		adjacencyList.get(node1).add(edge1);
		adjacencyList.get(node2).add(edge2);

	}

	@Override
	public void setConnectionWeight(T node1, T node2, int weight) {
		if (weight < 0) {
			throw new IllegalArgumentException("Weight cannot be negative!");
		}
		if (!adjacencyList.containsKey(node1) || !adjacencyList.containsKey(node2)
				|| getEdgeBetween(node1, node2) == null) {
			throw new NoSuchElementException("Node missing!");
		}
		Edge<T> edgeBetween1 = getEdgeBetween(node1, node2);
		Edge<T> edgeBetween2 = getEdgeBetween(node2, node1);
		edgeBetween1.setWeight(weight);
		edgeBetween2.setWeight(weight);
	}

	@Override
	public Set<T> getNodes() {
		return adjacencyList.keySet();
	}

	@Override
	public void disconnect(T node1, T node2) {
		if (!adjacencyList.containsKey(node1) || !adjacencyList.containsKey(node2)) {
			throw new NoSuchElementException("Node missing! ");
		}

		if (getEdgeBetween(node1, node2) == null) {
			throw new IllegalStateException("Edge not found!");
		}
		Set<Edge<T>> valueSet = adjacencyList.get(node1);
		valueSet.removeIf(e -> e.getDestination().equals(node2));
		valueSet = adjacencyList.get(node2);
		valueSet.removeIf(e -> e.getDestination().equals(node1));
	}

	@Override
	public void remove(T toRemove) {
		if (!adjacencyList.containsKey(toRemove)) {
			throw new NoSuchElementException("Node missing!");
		}

		for (Edge<T> edge : adjacencyList.get(toRemove)) {
			T node = edge.getDestination();
			Set<Edge<T>> edgeSet = new HashSet<>(adjacencyList.get(node));
			for (Edge<T> edge1 : edgeSet) {
				if (edge1.getDestination().equals(toRemove)) {
					adjacencyList.get(node).remove(edge1);
					break;
				}
			}
		}
		adjacencyList.remove(toRemove);
	}

	@Override
	public boolean pathExists(T from, T to) {
		if (!adjacencyList.containsKey(from) || !adjacencyList.containsKey(to)) {
			return false;
		}
		Set<T> visited = new HashSet<>();
		dfs(from, to, visited);
		return visited.contains(to);
	}

	private void dfs(T current, T searchedFor, Set<T> visited) {
		visited.add(current);
		if (current.equals(searchedFor)) {
			return;
		}
		for (Edge<T> edge : adjacencyList.get(current)) {
			if (!visited.contains(edge.getDestination())) {
				dfs(edge.getDestination(), searchedFor, visited);
			}
		}
	}

	@Override
	public Collection<Edge<T>> getEdgesFrom(T node) {
		if (!adjacencyList.containsKey(node)) {
			throw new NoSuchElementException("Node missing!");
		}
		return adjacencyList.get(node);
	}

	@Override
	public Edge<T> getEdgeBetween(T node1, T node2) {
		if (!adjacencyList.containsKey(node1) || !adjacencyList.containsKey(node2)) {
			throw new NoSuchElementException("Node missing!");
		}
		for (Edge<T> edge : adjacencyList.get(node1)) {
			if (edge.getDestination().equals(node2)) {
				return edge;
			}
		}

		return null;
	}

	@Override
	public List<Edge<T>> getPath(T from, T to) {
		Set<T> visited = new HashSet<>();
		Stack<Edge<T>> path = new Stack<>();
		depthFirstSearch(from, to, visited, path);
		List<Edge<T>> list = new ArrayList<Edge<T>>(path);
		Collections.reverse(list);
		if (list.isEmpty()) {
			return null;
		} else {
			return list;
		}

	}

	private Stack<Edge<T>> depthFirstSearch(T current, T searchedFor, Set<T> visited, Stack<Edge<T>> pathSoFar) {
		visited.add(current);
		if (current.equals(searchedFor)) {
			return pathSoFar;
		}
		for (Edge<T> edge : adjacencyList.get(current)) {
			T n = edge.getDestination();
			if (!visited.contains(n)) {
				pathSoFar.push(edge);
				Stack<Edge<T>> p = depthFirstSearch(n, searchedFor, visited, pathSoFar);
				if (!p.isEmpty()) {
					return p;
				} else {
					pathSoFar.pop();
				}
			}
		}
		return new Stack<Edge<T>>();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (T node : adjacencyList.keySet()) {
			sb.append(node).append(":").append(adjacencyList.get(node)).append("\n");
		}
		return sb.toString();
	}
}