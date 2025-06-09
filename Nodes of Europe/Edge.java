public class Edge<T> {
	
    private String name;
    private int weight;

    private T destination;

    public Edge(String name, int weight, T destination) {
        this.name = name;
        this.weight = weight;
        this.destination = destination;
    }

    public T getDestination(){
        return destination;
    }
    public int getWeight() {
        return weight;
    }

    public void setWeight(int newWeight) {
        if (newWeight < 0) {
            throw new IllegalArgumentException("Weight must be positive");
        }
        this.weight = newWeight;
    }

    public String getName(){
        return this.name;
    }

    @Override
    public String toString() {
        return "to " + destination + " by " + name + " takes " + weight;
    }

}