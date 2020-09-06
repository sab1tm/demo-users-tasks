package demo.waviot.kz.work_tasks.task;

import demo.waviot.kz.work_tasks.exception.CustomException;
import demo.waviot.kz.work_tasks.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class TaskService {

  @PersistenceContext
  private EntityManager em;

  private final TaskRepository taskRepository;
  private final UserService userService;

  public TaskService(TaskRepository taskRepository,
                     UserService userService) {
    this.taskRepository = taskRepository;
    this.userService = userService;
  }

  public List<TaskEntity> getAllTaskList() {
    return taskRepository.findAll();
  }

  public TaskEntity getTaskById(Long id) {
    return taskRepository.findById(id).orElse(null);
  }

  public List<TaskEntity> getMyTaskList() {
    return taskRepository.findByUserId(userService.getAuthUserId());
  }

  public Page<TaskEntity> getMyPaginationTasksList(Pageable pageable) {
    List<TaskEntity> tasks = taskRepository.findAll();
    int pageSize = pageable.getPageSize();
    int currentPage = pageable.getPageNumber();
    int startItem = currentPage * pageSize;
    List<TaskEntity> list;

    if (tasks.size() < startItem) {
      list = Collections.emptyList();
    } else {
      int toIndex = Math.min(startItem + pageSize, tasks.size());
      list = tasks.subList(startItem, toIndex);
    }

    Page<TaskEntity> postsPage
        = new PageImpl<TaskEntity>(list, PageRequest.of(currentPage, pageSize), tasks.size());

    return postsPage;
  }

  public TaskEntity createTask(TaskEntity taskInfo, Long userId) {
    TaskEntity newTask = new TaskEntity();
    newTask.setName(taskInfo.getName());
    newTask.setDescription(taskInfo.getDescription());
    newTask.setStartDateTime(taskInfo.getStartDateTime());
    newTask.setEndDateTime(taskInfo.getEndDateTime());
    newTask.setFiles(taskInfo.getFiles());
    if (userId != null)
      newTask.setUser(userService.getById(userId));
    else
      newTask.setUser(userService.getById(userService.getAuthUserId()));
    return save(newTask);
  }

  public TaskEntity updatedTask(TaskEntity taskInfo, Long id, boolean updateOnlyOwnTask) {
    if (taskInfo == null || id == null || id == 0)
      throw new CustomException("Invalid data", HttpStatus.BAD_REQUEST);
    TaskEntity foundTask = taskRepository.findById(id).orElse(null);
    if (foundTask == null)
      throw new CustomException("Task not found", HttpStatus.NOT_FOUND);

    if (updateOnlyOwnTask) {
      if (!foundTask.getUser().getId().equals(userService.getById(userService.getAuthUserId()).getId()))
        throw new CustomException("Forbidden", HttpStatus.FORBIDDEN);
    }
    taskInfo.setId(id);
    taskInfo.setCreated(foundTask.getCreated());
    taskInfo.setUser(foundTask.getUser());
    taskInfo.setFiles(foundTask.getFiles());
    save(taskInfo);
    return taskInfo;
}

  public TaskEntity changeTaskStatus(TaskEntity task, Byte statusId, boolean changeStatusOnlyOwnTask) {
    if (task == null || task.getId() == null || statusId == null || statusId < 0 || statusId > 2)
      throw new CustomException("Invalid data", HttpStatus.BAD_REQUEST);
    TaskEntity foundTask = taskRepository.findById(task.getId()).orElse(null);
    if (foundTask == null)
      throw new CustomException("Task not found", HttpStatus.NOT_FOUND);

    if (changeStatusOnlyOwnTask) {
      if (!foundTask.getUser().getId().equals(userService.getById(userService.getAuthUserId()).getId()))
        throw new CustomException("Forbidden", HttpStatus.FORBIDDEN);
    }
    foundTask.setStatus(statusId);
    save(foundTask);
    return foundTask;
  }

  public Boolean deletedTask(Long id, boolean deleteOnlyOwnTask) {
    if (id == null || id == 0)
      throw new CustomException("Invalid data", HttpStatus.BAD_REQUEST);
    TaskEntity foundTask = taskRepository.findById(id).orElse(null);
    if (foundTask == null)
      throw new CustomException("Task not found", HttpStatus.NOT_FOUND);

    if (deleteOnlyOwnTask) {
      if (!foundTask.getUser().getId().equals(userService.getById(userService.getAuthUserId()).getId()))
        throw new CustomException("Forbidden", HttpStatus.FORBIDDEN);
    }
    delete(foundTask);
    return true;
  }

  private TaskEntity save(TaskEntity task) {
    task.setUpdated(new Date());
    return taskRepository.save(task);
  }

  private void delete(TaskEntity task) {
      taskRepository.delete(task);
  }

}
