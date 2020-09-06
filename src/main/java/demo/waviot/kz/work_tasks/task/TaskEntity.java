package demo.waviot.kz.work_tasks.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import demo.waviot.kz.work_tasks.user.UserEntity;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tasks")
public class TaskEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id")
  private UserEntity user;

  private String name;
  private String description;

  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date startDateTime;

  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date endDateTime;

  @ElementCollection
  @CollectionTable(name = "tasks_files", joinColumns = @JoinColumn(name = "task_id"))
  @Column(name = "file_name")
  private List<String> files;

  private byte status;

  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date created;

  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date updated;

  public TaskEntity() {
    this.setCreated(new Date());
    this.setStatus((byte) 0);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Date getStartDateTime() {
    return startDateTime;
  }

  public void setStartDateTime(Date startDateTime) {
    this.startDateTime = startDateTime;
  }

  public Date getEndDateTime() {
    return endDateTime;
  }

  public void setEndDateTime(Date endDateTime) {
    this.endDateTime = endDateTime;
  }

  public List<String> getFiles() {
    return files;
  }

  public void setFiles(List<String> files) {
    this.files = files;
  }

  public byte getStatus() {
    return status;
  }

  public void setStatus(byte status) {
    this.status = status;
  }

  public UserEntity getUser() {
    return user;
  }

  public void setUser(UserEntity user) {
    this.user = user;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public Date getUpdated() {
    return updated;
  }

  public void setUpdated(Date updated) {
    this.updated = updated;
  }

}
