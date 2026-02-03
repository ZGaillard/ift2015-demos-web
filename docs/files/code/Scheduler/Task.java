public class Task implements Comparable<Task>{
    private int duration;
    private int remainingTime;
    private int arrivalTime;
    private String id;
    private int priority;

    public Task(String id, int duration, int arrivalTime) {
        this.id = id;
        this.duration = duration;
        this.remainingTime = duration;
        this.arrivalTime = arrivalTime;
        this.priority=0;
    }

    public Task(String id, int duration, int arrivalTime, int priority) {
        this.id = id;
        this.duration = duration;
        this.remainingTime = duration;
        this.arrivalTime = arrivalTime;
        this.priority=priority;
    }



    public int getDuration() {

        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public void processOneUnit() {
        if (remainingTime > 0) {
            remainingTime--;
        }
    }

    public boolean isComplete() {
        return remainingTime == 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int compareTo(Task other) {
        return Integer.compare(this.priority, other.priority);
    }
}
