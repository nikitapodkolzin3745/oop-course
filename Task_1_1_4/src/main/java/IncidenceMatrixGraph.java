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
        int vertexCount = getVertices().size();
        if (vertexCount > matrix.length) {
            int columnCapacity = matrix.length == 0 ? 0 : matrix[0].length;
            resizeMatrix(nextCapacity(matrix.length, vertexCount), columnCapacity,
                    vertexCount - 1, edges.size());
        }
    }

    @Override
    protected void vertexRemoved(Vertex vertex) {
        int vertexCount = getVertices().size();
        int column = 0;
        while (column < edges.size()) {
            DirectedEdge edge = edges.get(column);
            if (edge.from == vertex || edge.to == vertex) {
                removeColumn(column, vertexCount);
            } else {
                column++;
            }
        }

        for (int row = 0; row < vertexCount - 1; row++) {
            if (row >= indexOf(vertex)) {
                System.arraycopy(matrix[row + 1], 0, matrix[row], 0, edges.size());
            }
        }
        int newVertexCount = vertexCount - 1;
        for (int columnIndex = 0; columnIndex < matrix[0].length; columnIndex++) {
            matrix[newVertexCount][columnIndex] = 0;
        }
        shrinkIfSparse(newVertexCount, edges.size());
    }

    @Override
    protected void clearStorage() {
        edges.clear();
        matrix = new int[0][0];
    }

    @Override
    protected void addStoredEdge(Vertex from, Vertex to) {
        if (!hasStoredEdge(from, to)) {
            ensureCapacity(getVertices().size(), edges.size() + 1);
            edges.add(new DirectedEdge(from, to));
            writeEdgeColumn(edges.size() - 1, from, to);
        }
    }

    @Override
    protected void removeStoredEdge(Vertex from, Vertex to) {
        for (int column = 0; column < edges.size(); column++) {
            DirectedEdge edge = edges.get(column);
            if (edge.from == from && edge.to == to) {
                removeColumn(column, getVertices().size());
                shrinkIfSparse(getVertices().size(), edges.size());
                return;
            }
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

    private void writeEdgeColumn(int column, Vertex from, Vertex to) {
        int fromIndex = indexOf(from);
        int toIndex = indexOf(to);
        if (fromIndex == toIndex) {
            // Значение 2 обозначает ориентированную петлю в этом представлении.
            matrix[fromIndex][column] = 2;
        } else {
            matrix[fromIndex][column] = -1;
            matrix[toIndex][column] = 1;
        }
    }

    private void removeColumn(int column, int vertexCount) {
        edges.remove(column);
        for (int row = 0; row < vertexCount; row++) {
            for (int currentColumn = column; currentColumn < edges.size(); currentColumn++) {
                matrix[row][currentColumn] = matrix[row][currentColumn + 1];
            }
            matrix[row][edges.size()] = 0;
        }
    }

    private void ensureCapacity(int requiredRows, int requiredColumns) {
        int rowCapacity = matrix.length;
        int columnCapacity = matrix[0].length;
        if (requiredRows > rowCapacity || requiredColumns > columnCapacity) {
            int newRowCapacity = requiredRows > rowCapacity
                    ? nextCapacity(rowCapacity, requiredRows) : rowCapacity;
            int newColumnCapacity = requiredColumns > columnCapacity
                    ? nextCapacity(columnCapacity, requiredColumns) : columnCapacity;
            resizeMatrix(newRowCapacity, newColumnCapacity,
                    getVertices().size(), edges.size());
        }
    }

    private void resizeMatrix(int rowCapacity, int columnCapacity,
            int rowCount, int columnCount) {
        int[][] resized = new int[rowCapacity][columnCapacity];
        for (int row = 0; row < rowCount; row++) {
            System.arraycopy(matrix[row], 0, resized[row], 0, columnCount);
        }
        matrix = resized;
    }

    private void shrinkIfSparse(int rowCount, int columnCount) {
        int rowCapacity = matrix.length;
        int columnCapacity = matrix[0].length;
        int newRowCapacity = rowCapacity > 0 && rowCount <= rowCapacity / 4
                ? Math.max(rowCount, rowCapacity / 2) : rowCapacity;
        int newColumnCapacity = columnCapacity > 0 && columnCount <= columnCapacity / 4
                ? Math.max(columnCount, columnCapacity / 2) : columnCapacity;
        if (newRowCapacity != rowCapacity || newColumnCapacity != columnCapacity) {
            resizeMatrix(newRowCapacity, newColumnCapacity, rowCount, columnCount);
        }
    }

    private static int nextCapacity(int currentCapacity, int requiredCapacity) {
        return Math.max(requiredCapacity, currentCapacity == 0 ? 1 : currentCapacity * 2);
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