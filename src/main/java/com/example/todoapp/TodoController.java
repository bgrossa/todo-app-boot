package com.example.todoapp;

import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TodoController {
	
	ArrayList<Task> tasks = new ArrayList<>();

	@GetMapping("/")
	public String index(Model model) {
		
		model.addAttribute("tasks", tasks);
		
		return "index";
	}
	
	@PostMapping("/add")
	public String addTask(
			@RequestParam String taskName
		) {
			
			tasks.add(new Task(taskName));
			
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
}