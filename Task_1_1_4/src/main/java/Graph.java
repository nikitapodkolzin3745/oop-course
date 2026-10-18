import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Ориентированный граф. Петли разрешены. */
public interface Graph {
    /**
     * Добавляет новую изолированную вершину.
     * @return созданная вершина, которой ранее не было в графе
     */
    Vertex addVertex();

    /**
     * Удаляет вершину и все входящие и исходящие рёбра.
     * @param vertex удаляемая вершина
     * @throws IllegalArgumentException если {@code vertex} равна {@code null} или
     * не принадлежит графу
     */
    void removeVertex(Vertex vertex);

    /**
     * Добавляет ориентированное ребро. Повторное добавление уже существующего ребра
     * ничего не меняет.
     * @param from начальная вершина
     * @param to конечная вершина
     * @throws IllegalArgumentException если хотя бы одна вершина равна {@code null} или
     * не принадлежит графу
     */
    void addEdge(Vertex from, Vertex to);

    /**
     * Удаляет ориентированное ребро. Если такого ребра нет, граф не меняется.
     * @param from начальная вершина
     * @param to конечная вершина
     * @throws IllegalArgumentException если хотя бы одна вершина равна {@code null} или
     * не принадлежит графу
     */
    void removeEdge(Vertex from, Vertex to);

    /**
     * Возвращает неизменяемый снимок вершин в порядке их добавления.
     * Изменение возвращённого списка не влияет на граф.
     */
    List<Vertex> getVertices();

    /**
     * Возвращает неизменяемый снимок исходящих соседей вершины
     * в порядке добавления вершин.
     * @param vertex вершина
     * @throws IllegalArgumentException если {@code vertex} равна {@code null} или
     * не принадлежит графу
     */
    List<Vertex> getNeighbors(Vertex vertex);

    /**
     * Заменяет содержимое графа графом, описанным в текстовом файле в кодировке UTF-8.
     * @param path путь к файлу
     * @throws IOException если файл не удалось прочитать
     * @throws NullPointerException если {@code path} равен {@code null}
     * @throws IllegalArgumentException если файл содержит некорректное описание графа
     */
    void readFromFile(Path path) throws IOException;

    /**
     * Заменяет содержимое графа описанием: число вершин, затем пары индексов рёбер.
     * Некорректное описание отклоняется до изменения графа.
     * @param description описание графа
     * @throws IllegalArgumentException если описание равно {@code null} или имеет неверный формат
     */
    void evalGraph(String description);

    /**
     * Вычисляет топологический порядок вершин графа алгоритмом Кана.
     * Порядок среди одновременно доступных вершин не специфицирован.
     * @param graph граф для сортировки
     * @return новый список вершин в топологическом порядке; для пустого графа
     * возвращает пустой список
     * @throws IllegalArgumentException если {@code graph} равен {@code null}
     * @throws IllegalStateException если граф содержит цикл
     */
    static List<Vertex> topologicalSort(Graph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("Graph cannot be null");
        }

        List<Vertex> vertices = graph.getVertices();
        Map<Vertex, Integer> inDegree = new HashMap<>();
        ArrayDeque<Vertex> ready = new ArrayDeque<>();
        for (Vertex vertex : vertices) {
            inDegree.put(vertex, 0);
        }
        for (Vertex source : vertices) {
            for (Vertex neighbor : graph.getNeighbors(source)) {
                Integer degree = inDegree.get(neighbor);
                if (degree == null) {
                    throw new IllegalArgumentException(
                            "Graph contains an edge to a non-member vertex");
                }
                inDegree.put(neighbor, degree + 1);
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
            for (Vertex neighbor : graph.getNeighbors(vertex)) {
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

    /**
     * Сортирует этот граф; эквивалентно {@code Graph.topologicalSort(this)}.
     * @return новый список вершин в топологическом порядке
     * @throws IllegalStateException если граф содержит цикл
     */
    default List<Vertex> topologicalSort() {
        return Graph.topologicalSort(this);
    }
}