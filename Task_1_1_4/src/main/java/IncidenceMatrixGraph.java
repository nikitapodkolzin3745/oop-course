import java.util.ArrayList;
import java.util.List;

/** Ориентированный граф, хранящийся в матрице инцидентности. */
public class IncidenceMatrixGraph extends AbstractGraph {
    private final List<DirectedEdge> edges = new ArrayList<>();
    private int[][] matrix = new int[0][0];

    public IncidenceMatrixGraph() {
    }

    /**
     * Создаёт граф с указанным числом изолированных вершин.
     * @param initialVertexCount число вершин
     */
    public IncidenceMatrixGraph(int initialVertexCount) {
        if (initialVertexCount < 0) {
            throw new IllegalArgumentException("Initial vertex count cannot be negative");
        }
        for (int i = 0; i < initialVertexCount; i++) {
            addVertex();
        }
    }

    @Override
    protected void vertexAdded(Vertex vertex) {
        int[][] expanded = new int[getVertices().size()][edges.size()];
        for (int row = 0; row < matrix.length; row++) {
            System.arraycopy(matrix[row], 0, expanded[row], 0, edges.size());
        }
        matrix = expanded;
    }

    @Override
    protected void vertexRemoved(Vertex vertex) {
        edges.removeIf(edge -> edge.from == vertex || edge.to == vertex);
        rebuildMatrix(vertex);
    }

    private void rebuildMatrix(Vertex omittedVertex) {
        int rowCount = getVertices().size() - (omittedVertex == null ? 0 : 1);
        matrix = new int[rowCount][edges.size()];
        for (int column = 0; column < edges.size(); column++) {
            DirectedEdge edge = edges.get(column);
            int from = adjustedIndex(edge.from, omittedVertex);
            int to = adjustedIndex(edge.to, omittedVertex);
            if (from == to) {
                // Значение 2 обозначает ориентированную петлю в этом представлении.
                matrix[from][column] = 2;
            } else {
                matrix[from][column] = -1;
                matrix[to][column] = 1;
            }
        }
    }

    private int adjustedIndex(Vertex vertex, Vertex omittedVertex) {
        int index = indexOf(vertex);
        return omittedVertex != null && index > indexOf(omittedVertex) ? index - 1 : index;
    }

    @Override
    protected void clearStorage() {
        edges.clear();
        matrix = new int[0][0];
    }

    @Override
    protected void addStoredEdge(Vertex from, Vertex to) {
        if (!hasStoredEdge(from, to)) {
            edges.add(new DirectedEdge(from, to));
            rebuildMatrix(null);
        }
    }

    @Override
    protected void removeStoredEdge(Vertex from, Vertex to) {
        if (edges.removeIf(edge -> edge.from == from && edge.to == to)) {
            rebuildMatrix(null);
        }
    }

    @Override
    protected boolean hasStoredEdge(Vertex from, Vertex to) {
        int fromIndex = indexOf(from);
        int toIndex = indexOf(to);
        for (int column = 0; column < edges.size(); column++) {
            if (from == to && matrix[fromIndex][column] == 2) {
                return true;
            }
            if (from != to && matrix[fromIndex][column] == -1 && matrix[toIndex][column] == 1) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected List<Vertex> storedNeighbors(Vertex vertex) {
        List<Vertex> neighbors = new ArrayList<>();
        for (Vertex candidate : getVertices()) {
            if (hasStoredEdge(vertex, candidate)) {
                neighbors.add(candidate);
            }
        }
        return neighbors;
    }

    private static final class DirectedEdge {
        private final Vertex from;
        private final Vertex to;

        private DirectedEdge(Vertex from, Vertex to) {
            this.from = from;
            this.to = to;
        }
    }
}