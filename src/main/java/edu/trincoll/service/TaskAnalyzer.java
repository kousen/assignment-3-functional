package edu.trincoll.service;

import edu.trincoll.functional.TaskPredicate;
import edu.trincoll.model.Task;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * AI Collaboration Report:
 * Tool: Cline with Gemini integration
 *
 * Usage: Assisted with integrating Member 3 contributions and verifying all implementations
 * Used for code review, debugging compilation issues (missing imports), and ensuring full test coverage
 * Prompts included: "How to implement function composition in Java", "Verify Optional chaining patterns"
 *
 * Concepts Learned:
 * - Advanced function composition using reduce() with Function::andThen
 * - Comparator chaining for multi-criteria sorting
 * - Proper integration of custom functional interfaces with streams
 */
public class TaskAnalyzer {
    private final List<Task> tasks;

    public TaskAnalyzer(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Filters tasks using the provided predicate.
     * Uses Stream API with filter operation.
     *
     * @param predicate the condition to filter tasks
     * @return list of tasks matching the predicate
     */
    public List<Task> filterTasks(Predicate<Task> predicate) {
    return tasks.stream()
        .filter(Objects.requireNonNull(predicate))
        .collect(Collectors.toList());
    }

    /**
     * Finds a task by its ID.
     * Uses Stream API with filter and findFirst for Optional handling.
     *
     * @param id the task ID to search for
     * @return Optional containing the task if found, empty otherwise
     */
    public Optional<Task> findTaskById(Long id) {
    return tasks.stream()
        .filter(Objects::nonNull)
        .filter(t -> Objects.equals(t.id(), id))
        .findFirst();
    }

    /**
     * Gets the top priority tasks up to the specified limit.
     * Uses Stream API with sorted (by priority weight descending) and limit.
     *
     * @param limit maximum number of tasks to return
     * @return list of top priority tasks
     */
    public List<Task> getTopPriorityTasks(int limit) {
    return tasks.stream()
        .filter(Objects::nonNull)
        .sorted(Comparator.comparingInt((Task t) -> t.priority().getWeight()).reversed())
        .limit(Math.max(0, limit))
        .collect(Collectors.toList());
    }

    /**
     * Groups tasks by their status.
     * Uses Stream API with groupingBy collector.
     *
     * @return map of status to list of tasks
     */
    public Map<Task.Status, List<Task>> groupByStatus() {
    return tasks.stream()
        .filter(Objects::nonNull)
        .collect(Collectors.groupingBy(Task::status));
    }

    /**
     * Partitions tasks into overdue and not overdue.
     * Uses Stream API with partitioningBy collector.
     *
     * @return map with true key for overdue tasks and false for others
     */
    public Map<Boolean, List<Task>> partitionByOverdue() {
    return tasks.stream()
        .filter(Objects::nonNull)
        .collect(Collectors.partitioningBy(Task::isOverdue));
    }

    /**
     * Gets all unique tags from all tasks.
     * Uses Stream API with flatMap to flatten tags and collect to Set.
     *
     * @return set of all unique tags
     */
    public Set<String> getAllUniqueTags() {
    return tasks.stream()
        .filter(Objects::nonNull)
        .flatMap(t -> Optional.ofNullable(t.tags()).orElse(Set.of()).stream())
        .collect(Collectors.toSet());
    }

    /**
     * Calculates the total estimated hours for all tasks.
     * Uses Stream API with filter (to exclude null values), map, and reduce.
     *
     * @return Optional containing total hours if any tasks have estimates
     */
    public Optional<Integer> getTotalEstimatedHours() {
    return tasks.stream()
        .filter(Objects::nonNull)
        .map(Task::estimatedHours)
        .filter(Objects::nonNull)
        .reduce(Integer::sum);
    }

    /**
     * Calculates the average estimated hours for tasks that have estimates.
     * Uses Stream API with filter, mapToInt, and average.
     *
     * @return OptionalDouble containing average hours
     */
    public OptionalDouble getAverageEstimatedHours() {
    return tasks.stream()
        .filter(Objects::nonNull)
        .map(Task::estimatedHours)
        .filter(Objects::nonNull)
        .mapToInt(Integer::intValue)
        .average();
    }

    /**
     * Gets all task titles.
     * Uses Stream API with method reference for mapping.
     *
     * @return list of task titles
     */
    public List<String> getTaskTitles() {
    return tasks.stream()
        .filter(Objects::nonNull)
        .map(Task::title)
        .collect(Collectors.toList());
    }

    /**
     * Filters tasks using a custom TaskPredicate functional interface.
     * Demonstrates use of custom functional interfaces with Stream API.
     *
     * @param predicate the custom predicate to filter tasks
     * @return list of tasks matching the custom predicate
     */
    public List<Task> filterWithCustomPredicate(TaskPredicate predicate) {
    return tasks.stream()
        .filter(Objects.requireNonNull(predicate))
        .collect(Collectors.toList());
    }

    /**
     * Gets all tags from all tasks, sorted alphabetically.
     * Uses Stream API with flatMap to flatten all tags and sorted.
     *
     * @return sorted list of all tags (including duplicates)
     */
    public List<String> getAllTagsSorted() {
    return tasks.stream()
        .filter(Objects::nonNull)
        .flatMap(t -> Optional.ofNullable(t.tags()).orElse(Set.of()).stream())
        .sorted()
        .collect(Collectors.toList());
    }

    /**
     * Counts tasks grouped by priority.
     * Uses Stream API with groupingBy and counting collectors.
     *
     * @return map of priority to count of tasks
     */
    public Map<Task.Priority, Long> countTasksByPriority() {
    return tasks.stream()
        .filter(Objects::nonNull)
        .collect(Collectors.groupingBy(Task::priority, Collectors.counting()));
    }

    /**
     * Gets a summary string for a task by ID.
     * Uses Optional operations with map and orElse.
     *
     * @param taskId the ID of the task
     * @return task summary or "Task not found" message
     */
    public String getTaskSummary(Long taskId) {
        return findTaskById(taskId)
                .map(t -> String.format("%s (id=%d) - %s", t.title(), t.id(),
                        Optional.ofNullable(t.description()).orElse("No description")))
                .orElse("Task not found");
    }

    /**
     * Checks if there are any overdue tasks.
     * Uses Stream API with anyMatch terminal operation.
     *
     * @return true if any task is overdue, false otherwise
     */
    public boolean hasOverdueTasks() {
    return tasks.stream()
        .filter(Objects::nonNull)
        .anyMatch(Task::isOverdue);
    }

    /**
     * Checks if all tasks have been assigned (have estimated hours).
     * Uses Stream API with allMatch terminal operation.
     *
     * @return true if all tasks have estimated hours, false otherwise
     */
    public boolean areAllTasksAssigned() {
        return tasks.stream()
                .filter(Objects::nonNull)
                .allMatch(t -> t.estimatedHours() != null);
    }
}
