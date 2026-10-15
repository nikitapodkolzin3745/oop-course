import java.util.ArrayList;
import java.util.List;

/** Ориентированный граф, хранящийся в виде булевой матрицы смежности. */
public class AdjacencyMatrixGraph extends AbstractGraph {
    private boolean[][] matrix = new boolean[0][0];

    @Override
    protected void vertexAdded() {
        boolean[][] expanded = new boolean[vertices.size()][vertices.size()];
        for (int row = 0; row < matrix.length; row++) {
            System.arraycopy(matrix[row], 0, expanded[row], 0, matrix.length);
        }
        matrix = expanded;
    }

    @Override
    protected void vertexRemoved(Vertex vertex) {
        int removed = indexOf(vertex);
        boolean[][] reduced = new boolean[matrix.length - 1][matrix.length - 1];
        for (int oldRow = 0, newRow = 0; oldRow < matrix.length; oldRow++) {
            if (oldRow == removed) {
                continue;
            }
            for (int oldColumn = 0, newColumn = 0; oldColumn < matrix.length; oldColumn++) {
                if (oldColumn != removed) {
                    reduced[newRow][newColumn++] = matrix[oldRow][oldColumn];
                }
            }
            newRow++;
        }
        matrix = reduced;
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
        List<Vertex> neighbors = new ArrayList<>();
        for (int column = 0; column < matrix.length; column++) {
            if (matrix[row][column]) {
                neighbors.add(vertices.get(column));
            }
        }
        return neighbors;
    }
}
