 package edu.trincoll.functional;

import edu.trincoll.model.Task;
import java.util.*;
import java.util.function.Function;

@FunctionalInterface
public interface TaskTransformer extends Function<Task, Task> {

    default TaskTransformer andThen(TaskTransformer after) {
        return task -> after.apply(this.apply(task));
    }

    /* ====== Static factory methods ====== */

    static TaskTransformer updateStatus(Task.Status newStatus) {
        return task -> new Task(
                task.id(),
                task.title(),
                task.description(),
                task.priority(),
                newStatus,
                task.tags(),
                task.createdAt(),
                task.dueDate(),
                task.estimatedHours()
        );
    }

    static TaskTransformer withPriority(Task.Priority newPriority) {
        return task -> new Task(
                task.id(),
                task.title(),
                task.description(),
                newPriority,
                task.status(),
                task.tags(),
                task.createdAt(),
                task.dueDate(),
                task.estimatedHours()
        );
    }

    static TaskTransformer addTag(String tag) {
        return task -> {
            Set<String> newTags = new HashSet<>(Optional.ofNullable(task.tags()).orElse(Set.of()));
            newTags.add(tag);
            return new Task(
                    task.id(),
                    task.title(),
                    task.description(),
                    task.priority(),
                    task.status(),
                    newTags,
                    task.createdAt(),
                    task.dueDate(),
                    task.estimatedHours()
            );
        };
    }

    static TaskTransformer removeTag(String tag) {
        return task -> {
            Set<String> newTags = new HashSet<>(Optional.ofNullable(task.tags()).orElse(Set.of()));
            newTags.remove(tag);
            return new Task(
                    task.id(),
                    task.title(),
                    task.description(),
                    task.priority(),
                    task.status(),
                    newTags,
                    task.createdAt(),
                    task.dueDate(),
                    task.estimatedHours()
            );
        };
    }

    static TaskTransformer updateEstimatedHours(int hours) {
        return task -> new Task(
                task.id(),
                task.title(),
                task.description(),
                task.priority(),
                task.status(),
                task.tags(),
                task.createdAt(),
                task.dueDate(),
                hours
        );
    }

    static TaskTransformer markComplete() {
        return updateStatus(Task.Status.DONE);
    }

    static TaskTransformer markInProgress() {
        return updateStatus(Task.Status.IN_PROGRESS);
    }
}
