package source.modeles;

public class Graph {
    private int verticesNum;
    private double[][] matrix;

    public Graph(int verticesNum, double[][] matrix) {
        this.verticesNum = verticesNum;
        this.matrix = matrix;
    }

    // Getters et Setters
    public int getVerticesNum() { return verticesNum; }
    public void setVerticesNum(int verticesNum) { this.verticesNum = verticesNum; }

    public double[][] getMatrix() { return matrix; }
    public void setMatrix(double[][] matrix) { this.matrix = matrix; }
}
