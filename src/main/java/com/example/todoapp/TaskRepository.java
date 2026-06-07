package com.example.todoapp;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TaskRepository extends JpaRepository<Task, Long>{
	
	@Query("""
		SELECT t FROM Task t
		ORDER BY
			t.completed ASC,
			CASE
				WHEN t.deadline IS NULL OR t.deadline = '' THEN 1
				ELSE 0
			END,
			t.deadline ASC,
			CASE
				WHEN t.priority = '高' THEN 1
				WHEN t.priority = '中' THEN 2
				WHEN t.priority = '低' THEN 3
				ELSE 4
			END ASC
		""")
	List<Task> findAllOrderByDeadlineWithEmptyLast();
	
	@Query("""
		SELECT t FROM Task t WHERE t.name LIKE %:keyword%
		ORDER BY
			t.completed ASC,
			CASE
				WHEN t.deadline IS NULL OR t.deadline = '' THEN 1
				ELSE 0
			END,
			t.deadline ASC,
			CASE
				WHEN t.priority = '高' THEN 1
				WHEN t.priority = '中' THEN 2
				WHEN t.priority = '低' THEN 3
				ELSE 4
			END ASC				
		""")
	List<Task> findByNameContainingOrderByDeadlineWithEmptyLast(String keyword);
	
	@Query("""
		SELECT t FROM Task t WHERE t.completed = :completed
		ORDER BY
			CASE
				WHEN t.deadline IS NULL OR t.deadline = '' THEN 1
				ELSE 0
			END,
			t.deadline ASC,
			CASE
				WHEN t.priority = '高' THEN 1
				WHEN t.priority = '中' THEN 2
				WHEN t.priority = '低' THEN 3
				ELSE 4
			END ASC				
		""")
	List<Task> findByCompletedOrderByDeadlineWithEmptyLast(boolean completed);
	
	@Query("""
		SELECT t FROM Task t WHERE t.name LIKE %:keyword% AND t.completed = :completed
		ORDER BY
			CASE
				WHEN t.deadline IS NULL OR t.deadline = '' THEN 1
				ELSE 0
			END,
			t.deadline ASC,
			CASE
				WHEN t.priority = '高' THEN 1
				WHEN t.priority = '中' THEN 2
				WHEN t.priority = '低' THEN 3
				ELSE 4
			END ASC				
		""")
	List<Task> findByNameContainingAndCompletedOrderByDeadlineWithEmptyLast(String keyword, boolean completed);
}