package demo.waviot.kz.work_tasks.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import demo.waviot.kz.work_tasks.role.Role;
import demo.waviot.kz.work_tasks.task.TaskEntity;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
public class UserEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String firstName;
	private String lastName;

	@Column(unique = true, nullable = false)
	private String email;

	private String phone;
	private String logo;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name="users_roles", joinColumns={@JoinColumn(name="user_id")})
	@Column(name = "role")
	List<Role> roles;

	@Column(unique = true, nullable = false)
	private String username;

	private String password;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date created;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updated;

	@JsonIgnore
	@OneToMany(mappedBy = "user")
	private List<TaskEntity> tasks = new ArrayList<TaskEntity>();

	public UserEntity() {
		this.setCreated(new Date());
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public List<TaskEntity> getTasks() {
		return tasks;
	}

	public void setTasks(List<TaskEntity> tasks) {
		this.tasks = tasks;
	}
}