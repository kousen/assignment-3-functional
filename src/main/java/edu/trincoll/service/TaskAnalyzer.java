 package edu.trincoll.functional;

import edu.trincoll.model.Task;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TaskAnalyzer {
    private final List<Task> tasks;

    public TaskAnalyzer(List<Task> tasks) {
        this.tasks = Objects.requireNonNullElseGet(tasks, List::of);
    }

    /* ========= Part 1: Stream Operations ========= */

    // Filter using a Predicate<Task>
    public List<Task> filterTasks(Predicate<Task> predicate) {
        return tasks.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    // Map tasks to titles
    public List<String> getTaskTitles() {
        return tasks.stream()
                .map(Task::title)
                .collect(Collectors.toList());
    }

    // Sort by priority and limit results
    public List<Task> getTopPriorityTasks(int limit) {
        return tasks.stream()
                .sorted(Comparator.comparing((Task t) -> t.priority().getWeight())
                        .reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    // Group by status
    public Map<Task.Status, List<Task>> groupByStatus() {
        return tasks.stream()
                .collect(Collectors.groupingBy(Task::status));
    }

    // Partition by overdue
    public Map<Boolean, List<Task>> partitionByOverdue() {
        return tasks.stream()
                .collect(Collectors.partitioningBy(Task::isOverdue));
    }

    // Count tasks by priority
    public Map<Task.Priority, Long> countTasksByPriority() {
        return tasks.stream()
                .collect(Collectors.groupingBy(Task::priority, Collectors.counting()));
    }

    // Total estimated hours
    public int getTotalEstimatedHours() {
        return tasks.stream()
                .map(Task::estimatedHours)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();
    }

    // Average estimated hours
    public double getAverageEstimatedHours() {
        return tasks.stream()
                .map(Task::estimatedHours)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
    }

    // Collect all unique tags
    public Set<String> getAllUniqueTags() {
        return tasks.stream()
                .filter(t -> t.tags() != null)
                .flatMap(t -> t.tags().stream())
                .collect(Collectors.toSet());
    }

    // Any overdue tasks?
    public boolean hasOverdueTasks() {
        return tasks.stream().anyMatch(Task::isOverdue);
    }

    // All tasks assigned? (no assignedTo field in Task, so we treat tags/other as separate)
    // Adjust if "assigned" is modeled differently later
    public boolean areAllTasksAssigned() {
        // If assignment is tracked elsewhere, update here
        return tasks.stream().noneMatch(t -> t.tags() == null || t.tags().isEmpty());
    }

    /* ========= Part 2: Optional Operations ========= */

    // Find by ID
    public Optional<Task> findTaskById(Long id) {
        return tasks.stream()
                .filter(t -> Objects.equals(t.id(), id))
                .findFirst();
    }

    // Total hours with Optional (safe version)
    public int getTotalEstimatedHoursOptional() {
        return tasks.stream()
                .map(t -> Optional.ofNullable(t.estimatedHours()).orElse(0))
                .mapToInt(Integer::intValue)
                .sum();
    }

    // Task summary (title - status) or "Task not found"
    public String getTaskSummary(Long id) {
        return findTaskById(id)
                .map(task -> String.format("%s - %s", task.title(), task.status()))
                .orElse("Task not found");
    }
}
