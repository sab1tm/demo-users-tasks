package demo.waviot.kz.work_tasks.user;

import freemarker.template.TemplateException;
import io.swagger.annotations.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/users")
@Api(tags = "users")
public class UserController {

  @Autowired
  private UserService userService;

  @Autowired
  private ModelMapper modelMapper;

  @PostMapping("/login")
  @ApiOperation(value = "Аутентификация (логин, пароль) и генерации токена JWT")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
      @ApiResponse(code = 422, message = "Invalid username/password supplied")})
  public String login(
      @ApiParam("Username") @RequestParam String username,
      @ApiParam("Password") @RequestParam String password) {
    return userService.login(username, password);
  }

  @PostMapping("/registration")
  @ApiOperation(value = "Регистрация пользователя (логин, почта, пароль)")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
      @ApiResponse(code = 403, message = "Access denied"),
      @ApiResponse(code = 422, message = "Username is already in use")})
  public String registration(@ApiParam("Registration user") @RequestBody UserEntity user) {
    return userService.registration(modelMapper.map(user, UserEntity.class));
  }

  @GetMapping(value = "/su/all")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @ApiOperation(value = "Запрос списка всех пользователей", response = UserEntity.class,
      authorizations = { @Authorization(value="apiKey") })
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
      @ApiResponse(code = 404, message = "The user doesn't exist"),
      @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
  public List<UserEntity> getUsersList() {
    return userService.getUsersList();
  }

  @GetMapping(value = "/su/{username}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @ApiOperation(value = "Запрос профиля конкретного пользователя (по логину)", response = UserEntity.class,
      authorizations = { @Authorization(value="apiKey") })
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
      @ApiResponse(code = 403, message = "Access denied"),
      @ApiResponse(code = 404, message = "The user doesn't exist"),
      @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
  public UserEntity search(@ApiParam("Username") @PathVariable String username) {
    return modelMapper.map(userService.search(username), UserEntity.class);
  }

  @DeleteMapping(value = "/su/{username}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @ApiOperation(value = "Удалние конкретного пользователя (по логину)",
      authorizations = { @Authorization(value="apiKey") })
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
      @ApiResponse(code = 403, message = "Access denied"),
      @ApiResponse(code = 404, message = "The user doesn't exist"),
      @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
  public String delete(@ApiParam("Username") @PathVariable String username) {
    userService.delete(username);
    return username;
  }

  @GetMapping(value = "/me")
  @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
  @ApiOperation(value = "Запрос своего профиля", response = UserEntity.class,
      authorizations = { @Authorization(value="apiKey") })
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
      @ApiResponse(code = 403, message = "Access denied"),
      @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
  public UserEntity me(HttpServletRequest req) {
    return modelMapper.map(userService.me(req), UserEntity.class);
  }

  @PutMapping(value = "/me")
  @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
  @ApiOperation(value = "Обновление своего профиля", response = UserEntity.class,
      authorizations = { @Authorization(value="apiKey") })
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
      @ApiResponse(code = 403, message = "Access denied"),
      @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
  public UserEntity updateMe(@ApiParam("User update profile") @RequestBody UserEntity userInfo) {
    return userService.updateMe(modelMapper.map(userInfo, UserEntity.class));
  }

  @GetMapping(value = "/passwordResetRequest")
  @ApiOperation(value = "Запрос 4-х значного кода для сброса своего пароля")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
      @ApiResponse(code = 404, message = "The email doesn't exist"),
      @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
  public Boolean passwordResetRequest(@ApiParam("email") @RequestParam String email)
      throws TemplateException, IOException, MessagingException {
    userService.passwordResetRequest(email);
    return true;
  }

  @PostMapping(value = "/passwordReset")
  @ApiOperation(value = "Сброс своего пароля (по 4-х значному коду)")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
      @ApiResponse(code = 404, message = "The email doesn't exist"),
      @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
  public Boolean passwordReset(@ApiParam("email") @RequestParam String email,
                               @ApiParam("code") @RequestParam String code,
                               @ApiParam("newPassword") @RequestParam String newPassword) {
    return userService.passwordReset(email, code, newPassword);
  }

  @GetMapping("/refresh")
  @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
  public String refresh(HttpServletRequest req) {
    return userService.refresh(req.getRemoteUser());
  }

}
