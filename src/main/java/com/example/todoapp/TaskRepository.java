package com.example.todoapp;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long>{
	
	List<Task> findAllByOrderByDeadlineAsc();
	
	List<Task> findByNameContainingOrderByDeadlineAsc(String keyword);
	
	List<Task> findByCompletedOrderByDeadlineAsc(boolean completed);
	
	List<Task> findByNameContainingAndCompletedOrderByDeadlineAsc(String keyword, boolean completed);
}