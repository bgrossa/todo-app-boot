package com.example.todoapp;

public class Task {
	
	private String name;
	private boolean completed;
	private String priority;
	private String deadline;
	
	public Task(String name, String priority, String deadline) {
		this.name = name;
		this.completed = false;
		this.priority = priority;
		this.deadline = deadline;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isCompleted() {
		return completed;
	}
	
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	
	public String getPriority() {
		return priority;
	}
	
	public void setPriority(String priority) {
		this.priority = priority;
	}
	
	public String getDeadline() {
		return deadline;
	}
	
	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}
}