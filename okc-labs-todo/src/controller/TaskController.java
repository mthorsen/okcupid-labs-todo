package controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import data.Task;
import data.TaskSet;
import data.Task.Priority;

public class TaskController {
	private TaskSet tasks;
	
	public TaskController() {
		tasks = new TaskSet();
	}
	
	public TaskController(TaskSet tasks) {
		this.tasks = tasks;
	}
	
	public void setTasks(TaskSet tasks) {
		this.tasks = tasks;
	}
	
	public Set<String> getTaskNames() {
		return tasks.keySet();
	}
	
	public boolean addTask(String name, String description, Priority priority, Calendar start, Calendar due) {
		if (tasks.containsKey(name))
			return false;
		Task task = new Task(name, description, priority, start, due);
		tasks.put(name, task);
		return true;
	}
	
	public boolean addTask(String name, String description, Priority priority, Calendar start, Calendar due, int percentComplete, Calendar completed) {
		if (tasks.containsKey(name))
			return false;
		Task task = new Task(name, description, priority, start, due, percentComplete, completed);
		tasks.put(name, task);
		return true;
	}
	
	public boolean addTask(String name, String description, Priority priority, Date start, Date due) {
		Calendar startCal = null;
		Calendar dueCal = null;
		if (start != null) {
			startCal = Calendar.getInstance();
			startCal.setTime(start);
		}
		if (due != null) {
			dueCal = Calendar.getInstance();
			dueCal.setTime(due);
		}
		return addTask(name, description, priority, startCal, dueCal);
	}
	
	public boolean addTask(String name, String description, Priority priority, Date start, Date due, int percentComplete, boolean isDone) {
		Calendar startCal = null;
		Calendar dueCal = null;
		Calendar completed = null;
		if (start != null) {
			startCal = Calendar.getInstance();
			startCal.setTime(start);
		}
		if (due != null) {
			dueCal = Calendar.getInstance();
			dueCal.setTime(due);
		}
		if (isDone) {
			completed = Calendar.getInstance();
		}
		return addTask(name, description, priority, startCal, dueCal, percentComplete, completed);
	}
	
	public boolean updateTask(String name, String description, Priority priority, Date start, Date due, int percentComplete, boolean isDone) {
		Task t = tasks.get(name);
		
		t.setDescription(description);
		t.setPriority(priority);
		
		Calendar startCal = null;
		if (start != null) {
			startCal = Calendar.getInstance();
			startCal.setTime(start);
		}
		t.setStart(startCal);

		Calendar dueCal = null;
		if (due != null) {
			dueCal = Calendar.getInstance();
			dueCal.setTime(due);
		}
		t.setDue(dueCal);
		
		t.setPercentComplete(percentComplete);
		
		if (t.getCompleted() == null && isDone) {
			t.setCompleted(Calendar.getInstance());
		} else if (t.getCompleted() != null && !isDone) {
			t.setCompleted(null);
		}
		
		return true;
	}
	
	public boolean deleteTask(String name) {
		Task t = tasks.remove(name);
		return (t != null);
	}
	
	public ArrayList<String> clearCompleted() {
		Iterator<String> it = tasks.keySet().iterator();
		ArrayList<String> toClear = new ArrayList<String>();
		while (it.hasNext()) {
			String name = it.next();
			if (tasks.get(name).isDone()) {
				toClear.add(name);
			}
		}
		for (String name : toClear) {
			deleteTask(name);
		}
		return toClear;
		/*
		int numCleared = 0;
		while (it.hasNext()) {
			String name = it.next();
			if (tasks.get(name).isDone()) {
				tasks.remove(name);
				numCleared++;
			}
		}
		return numCleared;
		*/
	}
	
	public String getDescription(String name) {
		return tasks.get(name).getDescription();
	}

	public boolean isDone(String name) {
		return tasks.get(name).isDone();
	}
	
	public void setDone(String name, boolean value) {
		tasks.get(name).setDone(value);
	}
	
	public Priority getPriority(String name) {
		return tasks.get(name).getPriority();
	}

	public void setPriority(String name, Priority priority) {
		tasks.get(name).setPriority(priority);
	}

	public Calendar getStart(String name) {
		return tasks.get(name).getStart();
	}
	
	public Calendar getDue(String name) {
		return tasks.get(name).getDue();
	}
	
	public int getPercentComplete(String name) {
		return tasks.get(name).getPercentComplete();
	}

	public void writeTasks(ObjectOutputStream out) throws IOException {
		out.writeObject(tasks);
	}

	public void readTasks(ObjectInputStream in) throws IOException, ClassNotFoundException {
		tasks = (TaskSet)in.readObject();
	}
}
