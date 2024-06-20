/*package uk.ac.soton.comp1206.game;

import java.util.ArrayList;
import java.util.List;

public class Queue {

  private List<Integer> queue;
  private int count;

  public Queue(int max) {
    queue = new ArrayList<>(max);
    count = 0;
  }

  public boolean isEmpty() {
    return count == 0;
  }

  public boolean isFull() {
    return count == queue.size();
  }

  public void push(int number) throws Exception {
    if (!isFull()) {
      queue.add(number);
      count++;
    }
    else {
      throw new Exception("Queue is full.");
    }
  }

  public int pull() throws Exception {
    if (!isEmpty()) {
      count--;
      return queue.remove(0);
    }
    else {
      throw new Exception("Queue is empty.");
    }
  }

  public List<Integer> getQueueList() {
    return queue;
  }

}*/
