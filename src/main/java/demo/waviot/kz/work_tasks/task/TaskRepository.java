package demo.waviot.kz.work_tasks.task;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
  List<TaskEntity> findByUserId(Long userId);
}
