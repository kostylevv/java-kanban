import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static class Node {
        Task item;
        Node next;
        Node prev;

        Node(Node prev, Task element, Node next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }

    private HashMap<Integer, Node> history = new HashMap<>();
    private Node first;
    private Node last;

    @Override
    public void add(Task task) {
        removeById(task.getId());
        Node node = linkLast(task);
        history.put(task.getId(), node);
    }

    @Override
    public void remove(int id) {
        removeById(id);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> result = new ArrayList<>();
        if (first != null) {
            Node node = first;
            while (node != null) {
                result.add(node.item);
                node = node.next;
            }
        }
        return result;
    }

    private Node linkLast(Task task) {
        final Node prevLast = last;
        final Node newNode = new Node(prevLast, task, null);
        last = newNode;
        if (prevLast == null) {
            first = newNode;
        } else {
            prevLast.next = newNode;
        }
        return newNode;
    }

    private void removeById(int id) {
        Node node = history.remove(id);
        if (node == null) {
            return;
        }
        removeNode(node);
    }

    private void removeNode(Node node) {
        final Node next = node.next;
        final Node prev = node.prev;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            node.prev = null;
        }

        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            node.next = null;
        }
        node.item = null;
    }
}
