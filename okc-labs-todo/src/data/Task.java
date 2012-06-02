package data;

import java.io.Serializable;
import java.util.Calendar;

public class Task implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8964654511289437011L;

	public enum Priority { LOW, MEDIUM, HIGH };
	
	protected String name;
	protected String description;
	protected Priority priority;
	protected Calendar start;
	protected Calendar due;
	protected int percentComplete;
	protected Calendar completed;
	
	public Task(String name, String description, Priority priority, Calendar start, Calendar due) {
		this.name = name;
		this.description = description;
		this.priority = priority;
		this.start = start;
		this.due = due;
		this.percentComplete = 0;
		this.completed = null;
	}
	
	public Task(String name, String description, Priority priority, Calendar start, Calendar due, int percentComplete, Calendar completed) {
		this.name = name;
		this.description = description;
		this.priority = priority;
		this.start = start;
		this.due = due;
		this.percentComplete = percentComplete;
		this.completed = completed;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isDone() {
		return completed != null;
	}
	
	public void setDone(boolean value) {
		if (completed == null && value) 
			completed = Calendar.getInstance();
		if (!value)
			completed = null;
	}
	
	public Priority getPriority() {
		return priority;
	}
	
	public void setPriority(Priority priority) {
		this.priority = priority;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Calendar getStart() {
		return start;
	}
	
	public void setStart(Calendar start) {
		this.start = start;
	}
	
	public Calendar getDue() {
		return due;
	}
	
	public void setDue(Calendar due) {
		this.due = due;
	}

	public int getPercentComplete() {
		return percentComplete;
	}
	
	public void setPercentComplete(int percentComplete) {
		this.percentComplete = percentComplete;
	}
	
	public Calendar getCompleted() {
		return completed;
	}
	
	public void setCompleted(Calendar completed) {
		this.completed = completed;
	}
}
