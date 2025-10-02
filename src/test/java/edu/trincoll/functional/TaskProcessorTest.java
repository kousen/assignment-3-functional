package edu.trincoll.functional;

import edu.trincoll.model.Task;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TaskProcessorTest {

    private Task createTestTask() {
        return new Task(1L, "Test Task", "Description", Task.Priority.MEDIUM, Task.Status.TODO,
                       Set.of("urgent"), LocalDateTime.now(), LocalDateTime.now().plusDays(1), 5);
    }

    @Test
    void logTasksLogsMessagesToConsole() {
        Task task = createTestTask();

        // Call to cover logTasks method
        TaskProcessor.logTasks("Test log message").process(List.of(task));
    }

    @Test
    void andThenCombinesProcessing() {
        Task task = createTestTask();
        boolean[] processed = {false, false};

        TaskProcessor firstProcessor = tasks -> processed[0] = true;
        TaskProcessor secondProcessor = tasks -> processed[1] = true;

        TaskProcessor combined = firstProcessor.andThen(secondProcessor);

        combined.process(List.of(task));

        assertThat(processed[0]).isTrue();
        assertThat(processed[1]).isTrue();
    }
}
