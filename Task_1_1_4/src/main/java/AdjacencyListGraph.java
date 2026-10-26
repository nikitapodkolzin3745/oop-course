import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Ориентированный граф, хранящийся как соответствие вершин их исходящим соседям. */
public class AdjacencyListGraph extends AbstractGraph {
    private final Map<Vertex, Set<Vertex>> adjacency = new LinkedHashMap<>();

    @Override
    protected void vertexAdded(Vertex vertex) {
        adjacency.put(vertex, new LinkedHashSet<>());
    }

    @Override
    protected void vertexRemoved(Vertex vertex) {
        adjacency.remove(vertex);
        for (Set<Vertex> neighbors : adjacency.values()) {
            neighbors.remove(vertex);
        }
    }

    @Override
    protected void clearStorage() {
        adjacency.clear();
    }

    @Override
    protected void addStoredEdge(Vertex from, Vertex to) {
        adjacency.get(from).add(to);
    }

    @Override
    protected void removeStoredEdge(Vertex from, Vertex to) {
        adjacency.get(from).remove(to);
    }

    @Override
    protected boolean hasStoredEdge(Vertex from, Vertex to) {
        return adjacency.get(from).contains(to);
    }

    @Override
    protected List<Vertex> storedNeighbors(Vertex vertex) {
        Set<Vertex> outgoing = adjacency.get(vertex);
        List<Vertex> neighbors = new ArrayList<>();
        for (Vertex candidate : getVertices()) {
            if (outgoing.contains(candidate)) {
                neighbors.add(candidate);
            }
        }
        return neighbors;
    }
}