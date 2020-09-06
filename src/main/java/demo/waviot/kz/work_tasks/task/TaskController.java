package demo.waviot.kz.work_tasks.task;

import io.swagger.annotations.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/tasks")
@Api(tags = "tasks")
public class TaskController {

  private final TaskService taskService;

  @Autowired
  private ModelMapper modelMapper;

  public TaskController(TaskService taskService) {
    this.taskService = taskService;
  }

  @GetMapping(value = "/su/all")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @ApiOperation(value = "Запрос списка всех задач")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
      @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
  public List<TaskEntity> getAllTaskList() {
    List<TaskEntity> tasks = taskService.getAllTaskList();
    return tasks;
  }

  @GetMapping(value = "/su/pagination/")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @ApiOperation(value = "Запрос порционного списка всех задач (с поддержкой пагинации)")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
      @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
  public Page<TaskEntity> getMyPaginationTasksList(@RequestParam("page") Optional<Integer> page,
                                                   @RequestParam("size") Optional<Integer> size) {
    int currentPage = page.orElse(1);
    int pageSize = size.orElse(20);
    Page<TaskEntity> tasks = taskService.getMyPaginationTasksList(PageRequest.of(currentPage - 1, pageSize));
    return tasks;
  }

  @GetMapping(value = "/su/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @ApiOperation(value = "Запрос конкретной задачи любого пользователя (по id)")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
      @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
  public TaskEntity getTaskById(@PathVariable("id") Long id) {
    TaskEntity task = taskService.getTaskById(id);
    return task;
  }

  @PostMapping("/su/add/{userId}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @ApiOperation(value = "Регистрация новой задачи для определенного пользователя")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
      @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
  public TaskEntity createTaskForAnotherUser(@ApiParam("Create a task for another user") @RequestBody TaskEntity task,
                                             @PathVariable("userId") Long userId) {
    return taskService.createTask(modelMapper.map(task, TaskEntity.class), userId);
  }

  @PutMapping("/su/update/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @ApiOperation(value = "Обновление конкретной задачи любого пользователя (по id)")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
      @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
  public TaskEntity adminUpdatedTask(@ApiParam("Admin updates task") @RequestBody TaskEntity task,
                                     @PathVariable("id") Long id) {
    return taskService.updatedTask(modelMapper.map(task, TaskEntity.class), id, false);
  }

  @PostMapping("/su/changeStatus/{statusId}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @ApiOperation(value = "Изменение статуса конкретной задачи любого пользователя (по id)")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
      @ApiResponse(code = 403, message = "Access denied"),
      @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
  public TaskEntity adminChangeTaskStatus(@ApiParam("Admin change status a task") @RequestBody TaskEntity task,
                                          @PathVariable("statusId") Byte statusId) {
    return taskService.changeTaskStatus(modelMapper.map(task, TaskEntity.class), statusId, false);
  }

  @DeleteMapping(value = "/su/delete/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @ApiOperation(value = "Удаление конкретной задачи любого пользователя (по id)")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
      @ApiResponse(code = 404, message = "The task not found"),
      @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
  public Boolean adminDeletedTask(@ApiParam("id") @PathVariable Long id) {
    return taskService.deletedTask(id, false);
  }

  @GetMapping(value = "/my")
  @PreAuthorize("hasRole('ROLE_USER')")
  @ApiOperation(value = "Запрос своего списка задач")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
      @ApiResponse(code = 403, message = "Access denied"),
      @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
  public List<TaskEntity> getMyTaskList() {
    List<TaskEntity> tasks = taskService.getMyTaskList();
    return tasks;
  }

  @PostMapping("/add")
  @PreAuthorize("hasRole('ROLE_USER')")
  @ApiOperation(value = "Регистрация новой своей задачи")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
      @ApiResponse(code = 403, message = "Access denied"),
      @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
  public TaskEntity createTaskForYouSelf(@ApiParam("Create a task for yourself") @RequestBody TaskEntity task) {
    return taskService.createTask(modelMapper.map(task, TaskEntity.class), null);
  }

  @PutMapping("/update/{id}")
  @PreAuthorize("hasRole('ROLE_USER')")
  @ApiOperation(value = "Обновление своей задачи (по id)")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
      @ApiResponse(code = 403, message = "Access denied"),
      @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
  public TaskEntity userUpdatedTask(@ApiParam("User updates task") @RequestBody TaskEntity task,
                                    @PathVariable("id") Long id) {
    return taskService.updatedTask(modelMapper.map(task, TaskEntity.class), id, true);
  }

  @PostMapping("/changeStatus/{statusId}")
  @PreAuthorize("hasRole('ROLE_USER')")
  @ApiOperation(value = "Изменение статуса своей задачи (по id)")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
      @ApiResponse(code = 403, message = "Access denied"),
      @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
  public TaskEntity userChangeTaskStatus(@ApiParam("Change status a task for yourself") @RequestBody TaskEntity task,
                                         @PathVariable("statusId") Byte statusId) {
    return taskService.changeTaskStatus(modelMapper.map(task, TaskEntity.class), statusId, true);
  }

  @DeleteMapping(value = "/delete/{id}")
  @PreAuthorize("hasRole('ROLE_USER')")
  @ApiOperation(value = "Удаление своей задачи (по id)")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
      @ApiResponse(code = 403, message = "Access denied"),
      @ApiResponse(code = 404, message = "The task not found"),
      @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
  public Boolean userDeletedTask(@ApiParam("id") @PathVariable Long id) {
    return taskService.deletedTask(id, true);
  }

}