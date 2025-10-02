package edu.trincoll.functional;

import edu.trincoll.model.Task;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TaskTransformerTest {

    private Task createTestTask(Task.Status status, Task.Priority priority, Set<String> tags, LocalDateTime dueDate) {
        return new Task(1L, "Test Task", "Description", priority, status, tags, LocalDateTime.now(), dueDate, 5);
    }

    @Test
    void withStatusTransformsTaskStatus() {
        Task task = createTestTask(Task.Status.TODO, Task.Priority.MEDIUM, Set.of("urgent"), null);

        Task transformed = TaskTransformer.withStatus(Task.Status.DONE).apply(task);

        assertThat(transformed.status()).isEqualTo(Task.Status.DONE);
        assertThat(transformed.title()).isEqualTo(task.title());
        assertThat(transformed.priority()).isEqualTo(task.priority());
    }

    @Test
    void withPriorityTransformsTaskPriority() {
        Task task = createTestTask(Task.Status.IN_PROGRESS, Task.Priority.LOW, Set.of("backend"), null);

        Task transformed = TaskTransformer.withPriority(Task.Priority.CRITICAL).apply(task);

        assertThat(transformed.priority()).isEqualTo(Task.Priority.CRITICAL);
        assertThat(transformed.status()).isEqualTo(task.status());
        assertThat(transformed.tags()).isEqualTo(task.tags());
    }

    @Test
    void andThenChainsTransformers() {
        Task task = createTestTask(Task.Status.TODO, Task.Priority.LOW, Set.of("urgent"), null);

        TaskTransformer changeStatus = TaskTransformer.withStatus(Task.Status.IN_PROGRESS);
        TaskTransformer changePriority = TaskTransformer.withPriority(Task.Priority.HIGH);
        TaskTransformer chain = changeStatus.andThen(changePriority);

        Task transformed = chain.apply(task);

        assertThat(transformed.status()).isEqualTo(Task.Status.IN_PROGRESS);
        assertThat(transformed.priority()).isEqualTo(Task.Priority.HIGH);
    }
}
