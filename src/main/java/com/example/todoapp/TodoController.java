package com.example.todoapp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TodoController {
	
	ArrayList<Task> tasks = new ArrayList<>();
	
	@PostMapping("/add")
	public String addTask(
			@RequestParam String taskName,
			@RequestParam String priority,
			@RequestParam String deadline) {

			tasks.add(new Task(taskName, priority, deadline));
			
			return "redirect:/";
	}
	
	@PostMapping("/delete")
	public String deleteTask(
		@RequestParam int index
	) {
		
		tasks.remove(index);
		
		return "redirect:/";
	}
	
	@PostMapping("/complete")
	public String completeTask(@RequestParam int index) {
		
		tasks.get(index).setCompleted(true);
		
		return "redirect:/";
	}
	
	@PostMapping("/undo")
	public String undoTask(@RequestParam int index) {
		
		tasks.get(index).setCompleted(false);
		
		return "redirect:/";
	}
	
	@GetMapping("/edit")
	public String editForm(
			@RequestParam int index,
			Model model) {
		
		model.addAttribute("index", index);
		model.addAttribute("task", tasks.get(index));
		
		return "edit";
	}
	
	@PostMapping("/update")
	public String updateTask(
			@RequestParam int index,
			@RequestParam String taskName,
			@RequestParam String priority,
			@RequestParam String deadline) {
		
		tasks.get(index).setName(taskName);
		tasks.get(index).setPriority(priority);
		tasks.get(index).setDeadline(deadline);
		
		return "redirect:/";
	}
	
	@GetMapping("/")
	public String index(
			@RequestParam(required = false)
			String keyword,
			Model model) {
		
		List<Task> filteredTasks;
		
		if (keyword == null || keyword.isBlank()) {
			filteredTasks = tasks;
		} else {
			filteredTasks =tasks.stream()
				.filter(task ->
					task.getName().contains(keyword))
				.toList();
		}
		
		long completedCount =
			filteredTasks.stream()
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