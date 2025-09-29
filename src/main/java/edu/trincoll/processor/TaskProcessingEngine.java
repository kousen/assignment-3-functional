 package edu.trincoll.functional;

import edu.trincoll.model.Task;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class TaskProcessingEngine {

    // Apply a pipeline of processors
    public List<Task> processPipeline(List<Task> tasks, List<TaskProcessor> processors) {
        TaskProcessor pipeline = processors.stream()
                .reduce(TaskProcessor::andThen)
                .orElse(list -> list);
        return pipeline.process(tasks);
    }

    // Filter + Transform
    public List<Task> filterAndTransform(List<Task> tasks,
                                         TaskPredicate predicate,
                                         TaskTransformer transformer) {
        return tasks.stream()
                .filter(predicate)
                .map(transformer)
                .collect(Collectors.toList());
    }

    // Transform all with a UnaryOperator
    public List<Task> transformAll(List<Task> tasks, UnaryOperator<Task> operator) {
        return tasks.stream()
                .map(operator)
                .collect(Collectors.toList());
    }

    // Apply consumer for side effects
    public void processTasksWithSideEffects(List<Task> tasks, Consumer<Task> consumer) {
        tasks.forEach(consumer);
    }

    // Get or create default
    public Task getOrCreateDefault(Optional<Task> optionalTask, Supplier<Task> supplier) {
        return optionalTask.orElseGet(supplier);
    }

    // Generate infinite stream
    public Stream<Task> generateTaskStream(Supplier<Task> supplier) {
        return Stream.generate(supplier);
    }

    // Merge two tasks with BiFunction
    public Task mergeTasks(Task t1, Task t2, BiFunction<Task, Task, Task> merger) {
        return merger.apply(t1, t2);
    }

    // Sort with multiple criteria
    public List<Task> sortByMultipleCriteria(List<Task> tasks,
                                             Comparator<Task>... comparators) {
        Comparator<Task> combined = Arrays.stream(comparators)
                .reduce(Comparator::thenComparing)
                .orElse((a, b) -> 0);
        return tasks.stream()
                .sorted(combined)
                .collect(Collectors.toList());
    }
}
