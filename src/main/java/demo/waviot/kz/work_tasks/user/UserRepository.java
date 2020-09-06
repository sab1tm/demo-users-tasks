package demo.waviot.kz.work_tasks.user;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

  boolean existsByUsername(String username);

  UserEntity findByUsername(String username);

  UserEntity findByEmail(String email);

  @Transactional
  void deleteByUsername(String username);

}
