import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Общая логика графа. Подклассы реализуют хранение рёбер. */
abstract class AbstractGraph implements Graph {
    protected final List<Vertex> vertices = new ArrayList<>();

    protected abstract void vertexAdded();

    protected abstract void vertexRemoved(Vertex vertex);

    protected abstract void clearStorage();

    protected abstract void addStoredEdge(Vertex from, Vertex to);

    protected abstract void removeStoredEdge(Vertex from, Vertex to);

    protected abstract boolean hasStoredEdge(Vertex from, Vertex to);

    protected abstract List<Vertex> storedNeighbors(Vertex vertex);

    @Override
    public final Vertex addVertex() {
        Vertex vertex = new Vertex();
        vertices.add(vertex);
        vertexAdded();
        return vertex;
    }

    @Override
    public final void removeVertex(Vertex vertex) {
        requireVertex(vertex);
        vertexRemoved(vertex);
        vertices.remove(vertex);
    }

    @Override
    public final void addEdge(Vertex from, Vertex to) {
        requireVertex(from);
        requireVertex(to);
        addStoredEdge(from, to);
    }

    @Override
    public final void removeEdge(Vertex from, Vertex to) {
        requireVertex(from);
        requireVertex(to);
        removeStoredEdge(from, to);
    }

    @Override
    public final List<Vertex> getVertices() {
        return Collections.unmodifiableList(new ArrayList<>(vertices));
    }

    @Override
    public final List<Vertex> getNeighbors(Vertex vertex) {
        requireVertex(vertex);
        return Collections.unmodifiableList(new ArrayList<>(storedNeighbors(vertex)));
    }

    protected final void requireVertex(Vertex vertex) {
        if (vertex == null || !vertices.contains(vertex)) {
            throw new IllegalArgumentException("Vertex does not belong to this graph");
        }
    }

    protected final int indexOf(Vertex vertex) {
        return vertices.indexOf(vertex);
    }

    @Override
    public final List<Vertex> topologicalSort() {
        // Алгоритм Кана: сначала обрабатываем вершины с нулевой входящей степенью.
        Map<Vertex, Integer> inDegree = new HashMap<>();
        ArrayDeque<Vertex> ready = new ArrayDeque<>();
        for (Vertex vertex : vertices) {
            inDegree.put(vertex, 0);
        }
        for (Vertex source : vertices) {
            for (Vertex neighbor : storedNeighbors(source)) {
                inDegree.put(neighbor, inDegree.get(neighbor) + 1);
            }
        }
        for (Vertex vertex : vertices) {
            if (inDegree.get(vertex) == 0) {
                ready.addLast(vertex);
            }
        }

        List<Vertex> order = new ArrayList<>(vertices.size());
        while (!ready.isEmpty()) {
            Vertex vertex = ready.removeFirst();
            order.add(vertex);
            for (Vertex neighbor : storedNeighbors(vertex)) {
                int degree = inDegree.get(neighbor) - 1;
                inDegree.put(neighbor, degree);
                if (degree == 0) {
                    ready.addLast(neighbor);
                }
            }
        }
        if (order.size() != vertices.size()) {
            throw new IllegalStateException("Topological sort is undefined for a cyclic graph");
        }
        return order;
    }

    @Override
    public final void readFromFile(Path path) throws IOException {
        evalGraph(Files.readString(path, StandardCharsets.UTF_8));
    }

    @Override
    public final void evalGraph(String description) {
        if (description == null) {
            throw new IllegalArgumentException("Graph description cannot be null");
        }
        List<String> lines = new ArrayList<>();
        for (String line : description.split("\\R")) {
            String content = line.split("#", 2)[0].trim();
            if (!content.isEmpty()) {
                lines.add(content);
            }
        }
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("Missing vertex count");
        }

        final int vertexCount;
        try {
            String[] header = lines.get(0).split("\\s+");
            if (header.length != 1) {
                throw new IllegalArgumentException(
                        "The first line must contain only the vertex count");
            }
            vertexCount = Integer.parseInt(header[0]);
            if (vertexCount < 0) {
                throw new IllegalArgumentException("Vertex count cannot be negative");
            }
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid vertex count", exception);
        }

        List<int[]> edges = new ArrayList<>();
        for (int lineIndex = 1; lineIndex < lines.size(); lineIndex++) {
            String[] endpoints = lines.get(lineIndex).split("\\s+");
            if (endpoints.length != 2) {
                throw new IllegalArgumentException(
                        "Each edge line must contain exactly two vertex indexes");
            }
            try {
                int from = Integer.parseInt(endpoints[0]);
                int to = Integer.parseInt(endpoints[1]);
                if (from < 0 || from >= vertexCount || to < 0 || to >= vertexCount) {
                    throw new IllegalArgumentException(
                            "Edge endpoint is outside the declared vertex range");
                }
                edges.add(new int[]{from, to});
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("Invalid edge endpoint", exception);
            }
        }

        clearStorage();
        vertices.clear();
        for (int i = 0; i < vertexCount; i++) {
            addVertex();
        }
        for (int[] edge : edges) {
            addEdge(vertices.get(edge[0]), vertices.get(edge[1]));
        }
    }

    @Override
    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AbstractGraph)) {
            return false;
        }
        AbstractGraph graph = (AbstractGraph) other;
        if (vertices.size() != graph.vertices.size()) {
            return false;
        }
        for (int from = 0; from < vertices.size(); from++) {
            for (int to = 0; to < vertices.size(); to++) {
                if (hasStoredEdge(vertices.get(from), vertices.get(to))
                        != graph.hasStoredEdge(graph.vertices.get(from), graph.vertices.get(to))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public final int hashCode() {
        int hash = vertices.size();
        for (Vertex from : vertices) {
            for (Vertex to : vertices) {
                hash = 31 * hash + (hasStoredEdge(from, to) ? 1 : 0);
            }
        }
        return hash;
    }

    @Override
    public final String toString() {
        StringBuilder result = new StringBuilder("Graph{vertices=")
            .append(vertices.size())
            .append(", edges=[");
        boolean first = true;
        for (int from = 0; from < vertices.size(); from++) {
            for (int to = 0; to < vertices.size(); to++) {
                if (hasStoredEdge(vertices.get(from), vertices.get(to))) {
                    if (!first) {
                        result.append(", ");
                    }
                    result.append(from).append("->").append(to);
                    first = false;
                }
            }
        }
        return result.append("]}").toString();
    }
}