import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class Processor {

    private int currentTime;
    private Task currentTask;
    private ArrayList<Task> allTasks;
    private Queue<Task> waitingTasks;

    public Processor(List<Task> tasks) {
        this.allTasks = new ArrayList<Task>(tasks);
        this.waitingTasks = new PriorityQueue<>();
        this.currentTime = 0;
        this.currentTask = null;
    }

    public void tick() {

        // Prints current time and task status
        System.out.print("Time " + currentTime + ": ");
        if (currentTask != null) {
            System.out.println("Processing Task " + currentTask.getId() + " (Remaining: " + currentTask.getRemainingTime() + ")");
        } else {
            System.out.println("Idle");
        }


        // Add newly arrived tasks to the waiting queue
        for (Task task : allTasks) {
            if (task.getArrivalTime() == currentTime) {
                IO.println("Task " + task.getId() + " has arrived.");
                waitingTasks.add(task);
            }
        }

        // If no current task, pick the next one from the waiting queue
        if (currentTask == null && !waitingTasks.isEmpty()) {
            currentTask = waitingTasks.poll();
        }

        // Process the current task
        if (currentTask != null) {
            currentTask.processOneUnit();
            if (currentTask.isComplete()) {
                IO.println("Task " + currentTask.getId() + " has completed.");
                currentTask = null; // Task completed
            }
        }

        // Increment time
        currentTime++;
    }




}
