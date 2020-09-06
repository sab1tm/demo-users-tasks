package demo.waviot.kz.work_tasks.user;

import demo.waviot.kz.work_tasks.exception.CustomException;
import demo.waviot.kz.work_tasks.mail.MailService;
import demo.waviot.kz.work_tasks.role.Role;
import demo.waviot.kz.work_tasks.security.JwtTokenProvider;
import demo.waviot.kz.work_tasks.task.TaskEntity;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private MailService mailService;

  public String registration(UserEntity user) {
    if (!existsByUsername(user.getUsername())) {
      user.setUsername(user.getUsername().trim().toLowerCase());
      user.setEmail(user.getEmail().trim().toLowerCase());
      user.setPassword(passwordEncoder.encode(user.getPassword().trim()));
      user.setRoles(Collections.singletonList(Role.ROLE_USER));
      save(user);
      return jwtTokenProvider.createToken(user.getUsername(), user.getRoles());
    } else {
      throw new CustomException("Username is already in use", HttpStatus.UNPROCESSABLE_ENTITY);
    }
  }

  public Boolean existsByUsername(String username) {
    return userRepository.existsByUsername(username);
  }

  public UserEntity save(UserEntity user) {
    user.setUpdated(new Date());
    return userRepository.save(user);
  }

  public String login(String username, String password) {
    try {
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
      return jwtTokenProvider.createToken(username, getByUsername(username).getRoles());
    } catch (AuthenticationException e) {
      throw new CustomException("Invalid username/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
    }
  }

  public UserEntity me(HttpServletRequest req) {
    return getByUsername(jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req)));
  }

  public UserEntity updateMe(UserEntity userInfo) {
    if (userInfo == null)
      throw new CustomException("Invalid data", HttpStatus.BAD_REQUEST);
    UserEntity foundUser = userRepository.findById(userInfo.getId()).orElse(null);
    if (foundUser == null)
      throw new CustomException("User not found", HttpStatus.NOT_FOUND);
    userInfo.setRoles(foundUser.getRoles());
    userInfo.setUsername(foundUser.getUsername());
    userInfo.setPassword(foundUser.getPassword());
    userInfo.setTasks(foundUser.getTasks());
    return save(userInfo);
  }

  public UserEntity search(String username) {
    UserEntity user = getByUsername(username);
    if (user == null) {
      throw new CustomException("The user doesn't exist", HttpStatus.NOT_FOUND);
    }
    return user;
  }

  public void delete(String username) {
    userRepository.deleteByUsername(username);
  }

  public Boolean passwordResetRequest(String email) throws TemplateException, IOException, MessagingException {
    UserEntity foundUser = getUserByEmail(email);
    mailService.sendPasswordResetCode(passwordResetCode(foundUser), foundUser.getEmail());
    return true;
  }

  public Boolean passwordReset(String email, String checkCode, String newPassword)  {
    if (checkCode == null || checkCode.isEmpty() || newPassword == null || newPassword.isEmpty())
      throw new CustomException("Invalid email supplied", HttpStatus.BAD_REQUEST);
    UserEntity foundUser = getUserByEmail(email);
    if (passwordResetCode(foundUser).equals(checkCode.toUpperCase())) {
      foundUser.setPassword(passwordEncoder.encode(newPassword));
      save(foundUser);
      return true;
    }
    throw new CustomException("The verification code is incorrect", HttpStatus.BAD_REQUEST);
  }

  private String passwordResetCode(UserEntity user) {
    String password = user.getPassword();
    String code = password.substring(password.length()-4, password.length());
    return code.toUpperCase();
  }

  public UserEntity getByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  private UserEntity getUserByEmail(String email) {
    if (email == null || email.isEmpty())
      throw new CustomException("Invalid email supplied", HttpStatus.BAD_REQUEST);
    UserEntity foundUser = userRepository.findByEmail(email);
    if (foundUser == null)
      throw new CustomException("User not found", HttpStatus.NOT_FOUND);
    return foundUser;
  }

  public UserEntity getById(Long userId) {
    return userRepository.findById(userId).orElse(null);
  }

  public Long getAuthUserId() {
    UserDetails authUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (authUser != null) {
      UserEntity foundUser = getByUsername(authUser.getUsername());
      if (foundUser != null) {
        return foundUser.getId();
      }
    }
    return null;
  }

  public List<UserEntity> getUsersList() {
    return userRepository.findAll();
  }

  public String refresh(String username) {
    return jwtTokenProvider.createToken(username, userRepository.findByUsername(username).getRoles());
  }

}
