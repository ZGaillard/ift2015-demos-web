void main() {


    Task task1 = new Task("a", 20000, 0, 9999);
    Task task2 = new Task("b" , 2,  1, 1);
    Task task3 = new Task("c", 1,  2, 2);
    Task task4 = new Task("d", 4,  3, 3);
    Task task5 = new Task("e", 2,  4, 1);


    List<Task> tasks = new ArrayList<>();
    tasks.add(task1);
    tasks.add(task2);
    tasks.add(task3);
    tasks.add(task4);
    tasks.add(task5);

    Processor processor = new Processor(tasks);

    // Simulate ticks
    for (int i = 0; i < 60; i++) {
        processor.tick();
    }

}
