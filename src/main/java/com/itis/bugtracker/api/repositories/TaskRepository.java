package com.itis.bugtracker.api.repositories;

import com.itis.bugtracker.impl.models.data.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query(nativeQuery = true,
        value = "select * " +
                "from task as t " +
                "where ((:title is null) or upper(t.title) like upper(concat('%', :title, '%'))) " +
                "   and ((:description is null) or upper(t.description) like upper(concat('%', :description, '%'))) " +
                "   and (:type is null or t.type = cast(:type as text)) " +
                "   and (:status is null or t.status = cast(:status as text)) " +
                "   and (:author = 0 or t.author_id = :author) " +
                "   and (:executor = 0 or t.executor_id = :executor) " +
                "   and (:number = 0 or t.id = :number) " +
                "order by last_modified desc")
    List<Task> search(@Param("title") String title,
                      @Param("description") String description,
                      @Param("type") String type,
                      @Param("status") String status,
                      @Param("author") Long author,
                      @Param("executor") Long executor,
                      @Param("number") Long number);
}
