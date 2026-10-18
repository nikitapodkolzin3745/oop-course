import java.util.ArrayList;
import java.util.List;

/** Ориентированный граф, хранящийся в виде булевой матрицы смежности. */
public class AdjacencyMatrixGraph extends AbstractGraph {
    private boolean[][] matrix = new boolean[0][0];

    @Override
    protected void vertexAdded(Vertex vertex) {
        int vertexCount = getVertices().size();
        if (vertexCount > matrix.length) {
            int capacity = nextCapacity(matrix.length, vertexCount);
            boolean[][] expanded = new boolean[capacity][capacity];
            for (int row = 0; row < vertexCount - 1; row++) {
                System.arraycopy(matrix[row], 0, expanded[row], 0, vertexCount - 1);
            }
            matrix = expanded;
        }
    }

    @Override
    protected void vertexRemoved(Vertex vertex) {
        int removed = indexOf(vertex);
        int vertexCount = getVertices().size();
        for (int row = 0; row < vertexCount; row++) {
            for (int column = removed; column < vertexCount - 1; column++) {
                matrix[row][column] = matrix[row][column + 1];
            }
        }
        for (int row = removed; row < vertexCount - 1; row++) {
            System.arraycopy(matrix[row + 1], 0, matrix[row], 0, vertexCount - 1);
        }

        int newVertexCount = vertexCount - 1;
        for (int index = 0; index < vertexCount; index++) {
            matrix[newVertexCount][index] = false;
            matrix[index][newVertexCount] = false;
        }
        shrinkIfSparse(newVertexCount);
    }

    @Override
    protected void clearStorage() {
        matrix = new boolean[0][0];
    }

    @Override
    protected void addStoredEdge(Vertex from, Vertex to) {
        matrix[indexOf(from)][indexOf(to)] = true;
    }

    @Override
    protected void removeStoredEdge(Vertex from, Vertex to) {
        matrix[indexOf(from)][indexOf(to)] = false;
    }

    @Override
    protected boolean hasStoredEdge(Vertex from, Vertex to) {
        return matrix[indexOf(from)][indexOf(to)];
    }

    @Override
    protected List<Vertex> storedNeighbors(Vertex vertex) {
        int row = indexOf(vertex);
        List<Vertex> vertices = getVertices();
        List<Vertex> neighbors = new ArrayList<>();
        for (int column = 0; column < vertices.size(); column++) {
            if (matrix[row][column]) {
                neighbors.add(vertices.get(column));
            }
        }
        return neighbors;
    }

    private static int nextCapacity(int currentCapacity, int requiredCapacity) {
        return Math.max(requiredCapacity, currentCapacity == 0 ? 1 : currentCapacity * 2);
    }

    private void shrinkIfSparse(int vertexCount) {
        if (matrix.length > 0 && vertexCount <= matrix.length / 4) {
            int capacity = Math.max(vertexCount, matrix.length / 2);
            boolean[][] reduced = new boolean[capacity][capacity];
            for (int row = 0; row < vertexCount; row++) {
                System.arraycopy(matrix[row], 0, reduced[row], 0, vertexCount);
            }
            matrix = reduced;
        }
    }
}
