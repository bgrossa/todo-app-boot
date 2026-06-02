package com.example.todoapp;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TodoController {
	
	private final TaskRepository taskRepository;
	
	public TodoController(TaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}
	
	
	@PostMapping("/add")
	public String addTask(
			@RequestParam String taskName,
			@RequestParam String priority,
			@RequestParam String deadline) {

			taskRepository.save(new Task(taskName, priority, deadline));
			
			return "redirect:/";
	}
	
	@PostMapping("/delete")
	public String deleteTask(@RequestParam Long id) {
		
		taskRepository.deleteById(id);
		
		return "redirect:/";
	}
	
	@PostMapping("/complete")
	public String completeTask(@RequestParam Long id) {
		
		Task task = taskRepository.findById(id).orElseThrow();
		task.setCompleted(true);
		taskRepository.save(task);
		
		return "redirect:/";
	}
	
	@PostMapping("/undo")
	public String undoTask(@RequestParam Long id) {
		
		Task task = taskRepository.findById(id).orElseThrow();
		task.setCompleted(false);
		taskRepository.save(task);

		return "redirect:/";
	}
	
	@GetMapping("/edit")
	public String editForm(
			@RequestParam Long id,
			Model model) {
		
		Task task = taskRepository.findById(id).orElseThrow();
		
		model.addAttribute("task", task);
		
		return "edit";
	}
	
	@PostMapping("/update")
	public String updateTask(
			@RequestParam Long id,
			@RequestParam String taskName,
			@RequestParam(required = false) String priority,
			@RequestParam String deadline) {
		
		Task task = taskRepository.findById(id).orElseThrow();
		
		task.setName(taskName);
		task.setDeadline(deadline);
		
		if (priority != null) {
			task.setPriority(priority);
		}
		
		taskRepository.save(task);
		
		return "redirect:/";
	}
	
	@GetMapping("/")
	public String index(
			@RequestParam(required = false)	String keyword,
			Model model) {
		
		List<Task> tasks = taskRepository.findAllByOrderByDeadlineAsc();
		List<Task> filteredTasks;
		
		if (keyword == null || keyword.isBlank()) {
			filteredTasks = tasks;
		} else {
			filteredTasks =tasks.stream()
				.filter(task ->	task.getName().contains(keyword))
				.toList();
		}
		
		long completedCount = filteredTasks.stream()
				.filter(Task::isCompleted)
				.count();

		model.addAttribute("tasks", filteredTasks);
		model.addAttribute("taskCount", filteredTasks.size());
		model.addAttribute("completedCount", completedCount);
		model.addAttribute("keyword", keyword);
		model.addAttribute("today", LocalDate.now().toString());
		
		return "index";
	}
	
}