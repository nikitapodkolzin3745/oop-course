import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraphTest {
    private final List<Supplier<Graph>> graphFactories = Arrays.asList(
            AdjacencyMatrixGraph::new,
            IncidenceMatrixGraph::new,
            AdjacencyListGraph::new
    );

    @Test
    void supportsVertexAndDirectedEdgeOperationsInEveryRepresentation() {
        for (Supplier<Graph> factory : graphFactories) {
            Graph graph = factory.get();
            Vertex first = graph.addVertex();
            Vertex middle = graph.addVertex();
            Vertex last = graph.addVertex();

            graph.addEdge(first, middle);
            graph.addEdge(first, middle); // Duplicate edges are ignored.
            graph.addEdge(last, middle);

            assertTrue(graph.getNeighbors(middle).isEmpty());
            assertEquals(Arrays.asList(middle), graph.getNeighbors(first));
            assertTrue(graph.getNeighbors(last).contains(middle));
            assertEquals(3, graph.getVertices().size());

            graph.removeEdge(first, middle);
            assertTrue(graph.getNeighbors(first).isEmpty());
            graph.removeEdge(first, middle); // Removing a missing edge is harmless.

            graph.removeVertex(middle);
            assertEquals(Arrays.asList(first, last), graph.getVertices());
            assertTrue(graph.getNeighbors(last).isEmpty());
            Vertex addedLater = graph.addVertex();
            graph.addEdge(first, addedLater);
            assertEquals(Arrays.asList(addedLater), graph.getNeighbors(first));
        }
    }

    @Test
    void neighborsAreReturnedInVertexInsertionOrder() {
        for (Supplier<Graph> factory : graphFactories) {
            Graph graph = factory.get();
            Vertex source = graph.addVertex();
            Vertex firstNeighbor = graph.addVertex();
            Vertex secondNeighbor = graph.addVertex();
            graph.addEdge(source, secondNeighbor);
            graph.addEdge(source, firstNeighbor);

            assertEquals(Arrays.asList(firstNeighbor, secondNeighbor), graph.getNeighbors(source));
        }
    }

    @Test
    void computesTopologicalOrderAndDetectsCycles() {
        for (Supplier<Graph> factory : graphFactories) {
            Graph graph = factory.get();
            Vertex a = graph.addVertex();
            Vertex b = graph.addVertex();
            Vertex c = graph.addVertex();
            final Vertex isolated = graph.addVertex();
            graph.addEdge(a, c);
            graph.addEdge(b, c);

            List<Vertex> order = Graph.topologicalSort(graph);
            assertEquals(4, order.size());
            assertTrue(order.indexOf(a) < order.indexOf(c));
            assertTrue(order.indexOf(b) < order.indexOf(c));
            assertTrue(order.contains(isolated));

            graph.addEdge(c, a);
            assertThrows(IllegalStateException.class, () -> Graph.topologicalSort(graph));
            graph.removeEdge(c, a);
            graph.addEdge(isolated, isolated);
            assertThrows(IllegalStateException.class, () -> Graph.topologicalSort(graph));
        }
    }

    @Test
    void topologicalOrderPlacesEverySourceBeforeItsDestination() {
        for (Supplier<Graph> factory : graphFactories) {
            Graph graph = factory.get();
            Vertex a = graph.addVertex();
            Vertex b = graph.addVertex();
            Vertex c = graph.addVertex();
            Vertex d = graph.addVertex();
            graph.addEdge(a, b);
            graph.addEdge(a, c);
            graph.addEdge(b, d);
            graph.addEdge(c, d);

            List<Vertex> order = Graph.topologicalSort(graph);
            assertEquals(4, order.size());
            for (Vertex source : graph.getVertices()) {
                for (Vertex destination : graph.getNeighbors(source)) {
                    assertTrue(order.indexOf(source) < order.indexOf(destination));
                }
            }
        }

        Graph empty = new AdjacencyMatrixGraph();
        assertTrue(Graph.topologicalSort(empty).isEmpty());
    }

    @Test
    void removingVertexRemovesAllIncidentEdgesInEveryRepresentation() {
        for (Supplier<Graph> factory : graphFactories) {
            Graph graph = factory.get();
            Vertex source = graph.addVertex();
            Vertex removed = graph.addVertex();
            Vertex destination = graph.addVertex();
            graph.addEdge(source, removed);
            graph.addEdge(removed, destination);
            graph.addEdge(removed, removed);
            graph.addEdge(source, destination);

            graph.removeVertex(removed);

            assertEquals(Arrays.asList(source, destination), graph.getVertices());
            assertEquals(Arrays.asList(destination), graph.getNeighbors(source));
            assertTrue(graph.getNeighbors(destination).isEmpty());
            assertThrows(IllegalArgumentException.class, () -> graph.getNeighbors(removed));
            graph.addEdge(source, destination);
            assertEquals(Arrays.asList(destination), graph.getNeighbors(source));
        }
    }

    @Test
    void parsesTextAndComparesDifferentRepresentationsStructurally() {
        Graph matrix = new AdjacencyMatrixGraph();
        Graph incidence = new IncidenceMatrixGraph();
        Graph list = new AdjacencyListGraph();
        String description = "# three vertices\n3\n0 1\n1 2\n";

        matrix.evalGraph(description);
        incidence.evalGraph(description);
        list.evalGraph(description);

        assertEquals(matrix, incidence);
        assertEquals(incidence, list);
        assertEquals(matrix.hashCode(), list.hashCode());
        assertEquals("Graph{vertices=3, edges=[0->1, 1->2]}", matrix.toString());
        assertNotEquals(matrix, new AdjacencyMatrixGraph());
        assertEquals(matrix, matrix);
    }

    @Test
    void readsUtf8FileAndAllowsEmptyGraph() throws IOException {
        Path file = Files.createTempFile("graph", ".txt");
        try {
            Files.writeString(file, "2\n0 1\n");
            Graph graph = new IncidenceMatrixGraph();
            graph.readFromFile(file);
            assertEquals(1, graph.getNeighbors(graph.getVertices().get(0)).size());

            Files.writeString(file, "0\n");
            graph.readFromFile(file);
            assertTrue(graph.getVertices().isEmpty());
            assertEquals("Graph{vertices=0, edges=[]}", graph.toString());
        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    void rejectsInvalidVerticesAndKeepsGraphWhenDescriptionIsInvalid() {
        for (Supplier<Graph> factory : graphFactories) {
            Graph graph = factory.get();
            Vertex existing = graph.addVertex();
            Vertex foreign = new AdjacencyListGraph().addVertex();

            assertThrows(IllegalArgumentException.class, () -> graph.addEdge(existing, foreign));
            assertThrows(IllegalArgumentException.class, () -> graph.getNeighbors(null));
            assertThrows(IllegalArgumentException.class, () -> graph.removeVertex(foreign));
            assertThrows(IllegalArgumentException.class, () -> graph.addEdge(null, existing));
            assertThrows(UnsupportedOperationException.class, () -> graph.getVertices().clear());
            assertThrows(UnsupportedOperationException.class,
                    () -> graph.getNeighbors(existing).clear());

            assertThrows(IllegalArgumentException.class, () -> graph.evalGraph(""));
            assertThrows(IllegalArgumentException.class, () -> graph.evalGraph("not-a-number"));
            assertThrows(IllegalArgumentException.class, () -> graph.evalGraph("-1"));
            assertThrows(IllegalArgumentException.class, () -> graph.evalGraph("2 3"));
            assertThrows(IllegalArgumentException.class, () -> graph.evalGraph("2\n0"));
            assertThrows(IllegalArgumentException.class, () -> graph.evalGraph("2\n0 3"));
            assertThrows(IllegalArgumentException.class, () -> graph.evalGraph("2\nzero 1"));
            assertThrows(IllegalArgumentException.class, () -> graph.evalGraph(null));
            assertEquals(Arrays.asList(existing), graph.getVertices());
        }
        assertThrows(IllegalArgumentException.class, () -> new IncidenceMatrixGraph(-1));
    }

    @Test
    void invalidDescriptionDoesNotPartiallyReplaceExistingEdges() {
        for (Supplier<Graph> factory : graphFactories) {
            Graph graph = factory.get();
            graph.evalGraph("3\n0 1\n1 2");
            String before = graph.toString();
            List<Vertex> verticesBefore = graph.getVertices();

            assertThrows(IllegalArgumentException.class, () -> graph.evalGraph("2\n0 1\n1 2"));

            assertEquals(before, graph.toString());
            assertEquals(verticesBefore, graph.getVertices());
            assertEquals(Arrays.asList(verticesBefore.get(1)),
                    graph.getNeighbors(verticesBefore.get(0)));
            assertEquals(Arrays.asList(verticesBefore.get(2)),
                    graph.getNeighbors(verticesBefore.get(1)));
        }
    }

    @Test
    void parserAcceptsWhitespaceCommentsAndDuplicateEdges() {
        for (Supplier<Graph> factory : graphFactories) {
            Graph graph = factory.get();
            graph.evalGraph(" # graph\r\n 3 # vertices\r\n0   1 # edge\r\n0 1\r\n2 2");

            assertEquals("Graph{vertices=3, edges=[0->1, 2->2]}", graph.toString());
            assertEquals(Arrays.asList(graph.getVertices().get(1)),
                    graph.getNeighbors(graph.getVertices().get(0)));
            assertFalse(graph.getNeighbors(graph.getVertices().get(1))
                    .contains(graph.getVertices().get(0)));
        }
    }

    @Test
    void selfLoopsAreNeighbors() {
        for (Supplier<Graph> factory : graphFactories) {
            Graph graph = factory.get();
            final Vertex retained = graph.addVertex();
            Vertex removed = graph.addVertex();
            Vertex after = graph.addVertex();
            graph.addEdge(after, after);
            graph.removeVertex(removed);

            assertEquals(Arrays.asList(after), graph.getNeighbors(after));
            graph.addEdge(retained, after);
            assertEquals(Arrays.asList(after), graph.getNeighbors(retained));
        }
    }

    @Test
    void parserReplacesPreviousEdgesAndRemovesPreviousVertices() {
        Graph graph = new AdjacencyMatrixGraph();
        graph.evalGraph("2\n0 1");
        graph.evalGraph("1");
        assertEquals(1, graph.getVertices().size());
        assertTrue(graph.getNeighbors(graph.getVertices().get(0)).isEmpty());
    }
}