package edu.trincoll.functional;

import edu.trincoll.model.Task;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TaskPredicateTest {

    private Task createTestTask(Task.Status status, Task.Priority priority, Set<String> tags, LocalDateTime dueDate) {
        return new Task(1L, "Test Task", "Description", priority, status, tags, LocalDateTime.now(), dueDate, 5);
    }

    @Test
    void byStatusReturnsCorrectPredicate() {
        Task pendingTask = createTestTask(Task.Status.TODO, Task.Priority.MEDIUM, Set.of(), null);
        Task doneTask = createTestTask(Task.Status.DONE, Task.Priority.MEDIUM, Set.of(), null);

        assertThat(TaskPredicate.byStatus(Task.Status.TODO).test(pendingTask)).isTrue();
        assertThat(TaskPredicate.byStatus(Task.Status.TODO).test(doneTask)).isFalse();
        assertThat(TaskPredicate.byStatus(Task.Status.DONE).test(doneTask)).isTrue();
    }

    @Test
    void byPriorityReturnsCorrectPredicate() {
        Task highPriority = createTestTask(Task.Status.TODO, Task.Priority.HIGH, Set.of(), null);
        Task lowPriority = createTestTask(Task.Status.TODO, Task.Priority.LOW, Set.of(), null);

        assertThat(TaskPredicate.byPriority(Task.Priority.HIGH).test(highPriority)).isTrue();
        assertThat(TaskPredicate.byPriority(Task.Priority.HIGH).test(lowPriority)).isFalse();
        assertThat(TaskPredicate.byPriority(Task.Priority.LOW).test(lowPriority)).isTrue();
    }

    @Test
    void hasTagReturnsCorrectPredicate() {
        Task taggedTask = createTestTask(Task.Status.TODO, Task.Priority.MEDIUM, Set.of("urgent"), null);
        Task untaggedTask = createTestTask(Task.Status.TODO, Task.Priority.MEDIUM, Set.of(), null);

        assertThat(TaskPredicate.hasTag("urgent").test(taggedTask)).isTrue();
        assertThat(TaskPredicate.hasTag("urgent").test(untaggedTask)).isFalse();
        assertThat(TaskPredicate.hasTag("missing").test(taggedTask)).isFalse();
    }

    @Test
    void isOverdueReturnsCorrectPredicate() {
        Task overdueTask = createTestTask(Task.Status.TODO, Task.Priority.MEDIUM, Set.of(),
                                          LocalDateTime.now().minusDays(1));
        Task notOverdueTask = createTestTask(Task.Status.TODO, Task.Priority.MEDIUM, Set.of(),
                                             LocalDateTime.now().plusDays(1));

        assertThat(TaskPredicate.isOverdue().test(overdueTask)).isTrue();
        assertThat(TaskPredicate.isOverdue().test(notOverdueTask)).isFalse();
    }

    @Test
    void isActiveReturnsCorrectPredicate() {
        Task activeTask = createTestTask(Task.Status.IN_PROGRESS, Task.Priority.MEDIUM, Set.of(), null);
        Task completedTask = createTestTask(Task.Status.DONE, Task.Priority.MEDIUM, Set.of(), null);
        Task cancelledTask = createTestTask(Task.Status.CANCELLED, Task.Priority.MEDIUM, Set.of(), null);

        assertThat(TaskPredicate.isActive().test(activeTask)).isTrue();
        assertThat(TaskPredicate.isActive().test(completedTask)).isFalse();
        assertThat(TaskPredicate.isActive().test(cancelledTask)).isFalse();
    }

    @Test
    void andCombinesPredicates() {
        Task highPriorityActive = createTestTask(Task.Status.IN_PROGRESS, Task.Priority.HIGH, Set.of(), null);
        Task lowPriorityActive = createTestTask(Task.Status.IN_PROGRESS, Task.Priority.LOW, Set.of(), null);
        Task highPriorityCancelled = createTestTask(Task.Status.CANCELLED, Task.Priority.HIGH, Set.of(), null);

        TaskPredicate combined = TaskPredicate.byPriority(Task.Priority.HIGH).and(TaskPredicate.isActive());

        assertThat(combined.test(highPriorityActive)).isTrue();
        assertThat(combined.test(lowPriorityActive)).isFalse();
        assertThat(combined.test(highPriorityCancelled)).isFalse();
    }

    @Test
    void orCombinesPredicates() {
        Task highPriorityCancelled = createTestTask(Task.Status.CANCELLED, Task.Priority.HIGH, Set.of(), null);
        Task lowPriorityActive = createTestTask(Task.Status.IN_PROGRESS, Task.Priority.LOW, Set.of(), null);

        TaskPredicate combined = TaskPredicate.byPriority(Task.Priority.HIGH).or(TaskPredicate.isActive());

        assertThat(combined.test(highPriorityCancelled)).isTrue();
        assertThat(combined.test(lowPriorityActive)).isTrue();
    }

    @Test
    void negateInvertsPredicate() {
        Task activeTask = createTestTask(Task.Status.IN_PROGRESS, Task.Priority.MEDIUM, Set.of(), null);
        Task cancelledTask = createTestTask(Task.Status.CANCELLED, Task.Priority.MEDIUM, Set.of(), null);

        TaskPredicate activePred = TaskPredicate.isActive();

        assertThat(activePred.negate().test(activeTask)).isFalse();
        assertThat(activePred.negate().test(cancelledTask)).isTrue();
    }
}
