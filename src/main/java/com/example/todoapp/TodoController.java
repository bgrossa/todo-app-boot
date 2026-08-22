package com.example.todoapp;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
			@RequestParam String deadline,
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false, defaultValue = "all") String status,
			RedirectAttributes redirectattributes) {
			
		String cleanedTaskName = taskName.strip();
		
		if (cleanedTaskName.isBlank()) {
			redirectattributes.addFlashAttribute("errorMessage", "タスク名を入力してください");
			return redirectToIndex(keyword, status);
		}
		
		taskRepository.save(new Task(cleanedTaskName, priority, deadline));
		
		return redirectToIndex(keyword, status);
	}
	
	@PostMapping("/delete")
	public String deleteTask(
			@RequestParam Long id,
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false, defaultValue = "all") String status) {
		
		taskRepository.deleteById(id);
		
		return redirectToIndex(keyword, status);
	}
	
	@PostMapping("/delete-completed")
	public String deleteCompletedTasks(
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false, defaultValue = "all") String status) {
		
		taskRepository.deleteByCompletedTrue();
		
		return redirectToIndex(keyword, status);
	}
	
	@PostMapping("/complete")
	public String completeTask(
			@RequestParam Long id,
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false, defaultValue = "all") String status) {
		
		Task task = taskRepository.findById(id).orElseThrow();
		task.setCompleted(true);
		taskRepository.save(task);
		
		return redirectToIndex(keyword, status);
	}
	
	@PostMapping("/undo")
	public String undoTask(
			@RequestParam Long id,
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false, defaultValue = "all") String status) {
		
		Task task = taskRepository.findById(id).orElseThrow();
		task.setCompleted(false);
		taskRepository.save(task);

		return redirectToIndex(keyword, status);
	}
	
	@GetMapping("/edit")
	public String editForm(
			@RequestParam Long id,
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false, defaultValue = "all") String status,
			Model model) {
		
		Task task = taskRepository.findById(id).orElseThrow();
		
		model.addAttribute("task", task);
		model.addAttribute("keyword", keyword);
		model.addAttribute("status", status);
		
		return "edit";
	}
	
	@PostMapping("/update")
	public String updateTask(
			@RequestParam Long id,
			@RequestParam String taskName,
			@RequestParam(required = false) String priority,
			@RequestParam String deadline,
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false, defaultValue = "all") String status,
			RedirectAttributes redirectAttributes) {
			
		String cleanedTaskName = taskName.strip();
		
		if (cleanedTaskName.isBlank()) {
			redirectAttributes.addFlashAttribute("errorMessage", "タスク名を入力してください");
			return redirectToEdit(id, keyword, status);
		}

		Task task = taskRepository.findById(id).orElseThrow();
		
		task.setName(cleanedTaskName);
		task.setDeadline(deadline);
		
		if (priority != null) {
			task.setPriority(priority);
		}
		
		taskRepository.save(task);
		
		return redirectToIndex(keyword, status);
	}
	
	private String redirectToEdit(Long id, String keyword, String status) {
		status = normalizeStatus(status);
		keyword = normalizeKeyword(keyword);
		
		String redirectUrl = "redirect:/edit?id=" + id + "&status=" + status;
		
		if (!keyword.isBlank()) {
			redirectUrl += "&keyword=" + encodeKeyword(keyword);
		}
		
		return redirectUrl;
	}
	
	@GetMapping("/")
	public String index(
			@RequestParam(required = false)	String keyword,
			@RequestParam(required = false, defaultValue = "all") String status,
			Model model) {
		
		status = normalizeStatus(status);
		
		keyword = normalizeKeyword(keyword);
		
		List<Task> filteredTasks;
		boolean hasKeyword = !keyword.isBlank();
		
		if ("completed".equals(status)) {
			filteredTasks = hasKeyword
					? taskRepository.findByNameContainingAndCompletedOrderByDeadlineWithEmptyLast(keyword, true)
					: taskRepository.findByCompletedOrderByDeadlineWithEmptyLast(true);

		} else if ("active".equals(status)) {
			filteredTasks = hasKeyword
					? taskRepository.findByNameContainingAndCompletedOrderByDeadlineWithEmptyLast(keyword, false)
					: taskRepository.findByCompletedOrderByDeadlineWithEmptyLast(false);
		} else {
			filteredTasks = hasKeyword
					? taskRepository.findByNameContainingOrderByDeadlineWithEmptyLast(keyword)
					: taskRepository.findAllOrderByDeadlineWithEmptyLast();
		}
		
		long completedCount = taskRepository.countByCompletedTrue();

		model.addAttribute("tasks", filteredTasks);
		model.addAttribute("displayCount", filteredTasks.size());
		model.addAttribute("completedCount", completedCount);
		model.addAttribute("keyword", keyword);
		model.addAttribute("status", status);
		model.addAttribute("today", LocalDate.now().toString());
		
		return "index";
	}
	
	private String redirectToIndex(String keyword, String status) {
		status = normalizeStatus(status);
		keyword = normalizeKeyword(keyword);
		
		String redirectUrl = "redirect:/?status=" + status;
		
		if (!keyword.isBlank()) {
			redirectUrl += "&keyword=" + encodeKeyword(keyword);
		}
		
		return redirectUrl;
	}
	
	private String encodeKeyword(String keyword) {
		return URLEncoder.encode(keyword, StandardCharsets.UTF_8);
	}
	
	private String normalizeStatus(String status) {
		if ("active".equals(status) || "completed".equals(status)){
			return status;
		}
		
		return "all";
	}
	
	private String normalizeKeyword(String keyword) {
		if (keyword == null) {
			return "";
		}
		
		return keyword.strip();
	}
	
}