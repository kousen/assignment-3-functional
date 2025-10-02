package edu.trincoll.processor;

import edu.trincoll.functional.TaskPredicate;
import edu.trincoll.functional.TaskProcessor;
import edu.trincoll.functional.TaskTransformer;
import edu.trincoll.model.Task;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TaskProcessingEngine {

    public List<Task> processPipeline(
            List<Task> tasks,
            List<Function<List<Task>, List<Task>>> operations) {
    if (operations == null || operations.isEmpty()) return tasks;

    Function<List<Task>, List<Task>> pipeline = operations.stream()
        .filter(Objects::nonNull)
        .reduce(Function.identity(), Function::andThen);

    return pipeline.apply(tasks);
    }

    public Task getOrCreateDefault(Optional<Task> taskOpt, Supplier<Task> defaultSupplier) {
        return taskOpt.orElseGet(Objects.requireNonNull(defaultSupplier));
    }

    public void processTasksWithSideEffects(
            List<Task> tasks,
            Consumer<Task> sideEffect) {
        Objects.requireNonNull(sideEffect);
        Optional.ofNullable(tasks).orElse(List.of()).forEach(sideEffect);
    }

    public Task mergeTasks(Task task1, Task task2, BiFunction<Task, Task, Task> merger) {
        return Objects.requireNonNull(merger).apply(task1, task2);
    }

    public List<Task> transformAll(List<Task> tasks, UnaryOperator<Task> transformer) {
        return Optional.ofNullable(tasks).orElse(List.of()).stream()
                .map(Objects.requireNonNull(transformer))
                .collect(Collectors.toList());
    }

    public List<Task> filterAndTransform(
            List<Task> tasks,
            TaskPredicate filter,
            TaskTransformer transformer) {
    return Optional.ofNullable(tasks).orElse(List.of()).stream()
        .filter(Objects.requireNonNull(filter))
        .map(Objects.requireNonNull(transformer))
        .collect(Collectors.toList());
    }

    public void batchProcess(
            List<Task> tasks,
            int batchSize,
            TaskProcessor processor) {
        Objects.requireNonNull(processor);
    Objects.requireNonNull(tasks);
    if (tasks.isEmpty() || batchSize <= 0) return;

    int total = tasks.size();
    int batches = (total + batchSize - 1) / batchSize;

    java.util.stream.IntStream.range(0, batches)
        .mapToObj(i -> tasks.subList(i * batchSize, Math.min((i + 1) * batchSize, total)))
        .forEach(processor::process);
    }

    public Optional<String> getHighestPriorityTaskTitle(List<Task> tasks) {
        return Optional.ofNullable(tasks).orElse(List.of()).stream()
                .filter(Objects::nonNull)
                .max(Comparator.comparingInt(t -> t.priority().getWeight()))
                .map(Task::title);
    }

    public Stream<Task> generateTaskStream(Supplier<Task> taskSupplier) {
        return Stream.generate(Objects.requireNonNull(taskSupplier));
    }

    public List<Task> sortByMultipleCriteria(
            List<Task> tasks,
            List<Comparator<Task>> comparators) {
    if (tasks == null) return List.of();
    Comparator<Task> combined = comparators.stream()
        .filter(Objects::nonNull)
        .reduce(Comparator::thenComparing)
        .orElse((a, b) -> 0);

    return tasks.stream()
        .sorted(combined)
        .collect(Collectors.toList());
    }
}
