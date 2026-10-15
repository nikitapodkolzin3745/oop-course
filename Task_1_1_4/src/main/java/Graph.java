import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/** Ориентированный граф. Петли разрешены. */
public interface Graph {
    /** Добавляет вершину. */
    Vertex addVertex();

    /**
     * Удаляет вершину и все её рёбра.
     * @param vertex удаляемая вершина
     */
    void removeVertex(Vertex vertex);

    /**
     * Добавляет ориентированное ребро.
     * @param from начальная вершина
     * @param to конечная вершина
     */
    void addEdge(Vertex from, Vertex to);

    /**
     * Удаляет ориентированное ребро.
     * @param from начальная вершина
     * @param to конечная вершина
     */
    void removeEdge(Vertex from, Vertex to);

    /** Возвращает список вершин. */
    List<Vertex> getVertices();

    /**
     * Возвращает исходящих соседей вершины.
     * @param vertex вершина
     */
    List<Vertex> getNeighbors(Vertex vertex);

    /**
     * Загружает граф из текстового файла.
     * @param path путь к файлу
     */
    void readFromFile(Path path) throws IOException;

    /**
     * Загружает граф из текста: число вершин, затем пары индексов рёбер.
     * @param description описание графа
     */
    void evalGraph(String description);

    /** Возвращает топологический порядок или сообщает о цикле. */
    List<Vertex> topologicalSort();
}