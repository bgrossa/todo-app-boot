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
			
		String cleanedTaskName = taskName.strip();
		
		if (cleanedTaskName.isBlank()) {
			return "redirect:/";
		}
		
		taskRepository.save(new Task(cleanedTaskName, priority, deadline));
		
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
				
		String cleanedTaskName = taskName.strip();
		
		if (cleanedTaskName.isBlank()) {
			return "redirect:/";
		}

		Task task = taskRepository.findById(id).orElseThrow();
		
		task.setName(cleanedTaskName);
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
			@RequestParam(required = false, defaultValue = "all") String status,
			Model model) {
		
		List<Task> filteredTasks;
		boolean hasKeyword = keyword != null && !keyword.isBlank();
		
		if ("completed".equals(status)) {
			filteredTasks = hasKeyword
					? taskRepository.findByNameContainingAndCompletedOrderByDeadlineAsc(keyword, true)
					: taskRepository.findByCompletedOrderByDeadlineAsc(true);

		} else if ("active".equals(status)) {
			filteredTasks = hasKeyword
					? taskRepository.findByNameContainingAndCompletedOrderByDeadlineAsc(keyword, false)
					: taskRepository.findByCompletedOrderByDeadlineAsc(false);
		} else {
			filteredTasks = hasKeyword
					? taskRepository.findByNameContainingOrderByDeadlineAsc(keyword)
					: taskRepository.findAllByOrderByDeadlineAsc();
		}
		
		long completedCount = taskRepository.findByCompletedOrderByDeadlineAsc(true).size();

		model.addAttribute("tasks", filteredTasks);
		model.addAttribute("displayCount", filteredTasks.size());
		model.addAttribute("completedCount", completedCount);
		model.addAttribute("keyword", keyword);
		model.addAttribute("status", status);
		model.addAttribute("today", LocalDate.now().toString());
		
		return "index";
	}
	
}