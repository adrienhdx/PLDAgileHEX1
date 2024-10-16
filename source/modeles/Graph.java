package source.modeles;

public class Graph {
    private int verticesNum;
    private int[][] matrix;

    public Graph(int verticesNum, int[][] matrix) {
        this.verticesNum = verticesNum;
        this.matrix = matrix;
    }

    // Getters et Setters
    public int getVerticesNum() { return verticesNum; }
    public void setVerticesNum(int verticesNum) { this.verticesNum = verticesNum; }

    public int[][] getMatrix() { return matrix; }
    public void setMatrix(int[][] matrix) { this.matrix = matrix; }
}
