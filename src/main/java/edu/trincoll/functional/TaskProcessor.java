package edu.trincoll.functional;

import edu.trincoll.model.Task;
import java.util.*;
import java.util.stream.Collectors;

@FunctionalInterface
public interface TaskProcessor {
    List<Task> process(List<Task> tasks);

    default TaskProcessor andThen(TaskProcessor after) {
        return tasks -> after.process(this.process(tasks));
    }

    static TaskProcessor filter(TaskPredicate predicate) {
        return tasks -> tasks.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    static TaskProcessor transform(TaskTransformer transformer) {
        return tasks -> tasks.stream()
                .map(transformer)
                .collect(Collectors.toList());
    }

    static TaskProcessor sortByPriority() {
        return tasks -> tasks.stream()
                .sorted(Comparator.comparing((Task t) -> t.priority().getWeight()).reversed())
                .collect(Collectors.toList());
    }

    static TaskProcessor sortByDueDate() {
        return tasks -> tasks.stream()
                .sorted(Comparator.comparing(Task::dueDate, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    static TaskProcessor limit(int maxSize) {
        return tasks -> tasks.stream()
                .limit(maxSize)
                .collect(Collectors.toList());
    }

    static TaskProcessor removeDuplicates() {
        return tasks -> tasks.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    static TaskProcessor batchUpdateStatus(Task.Status status) {
        return tasks -> tasks.stream()
                .map(task -> task.withStatus(status))
                .collect(Collectors.toList());
    }

    // Removed batchAssign since Task has no assignedTo field
}
