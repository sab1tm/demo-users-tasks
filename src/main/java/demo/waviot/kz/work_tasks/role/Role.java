package demo.waviot.kz.work_tasks.role;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
  ROLE_ADMIN, ROLE_USER;

  public String getAuthority() {
    return name();
  }

}
