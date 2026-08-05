package kr.ac.knue.common.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiController {
  private final CommonAdminService service;
  private final AuthenticationManager authenticationManager;
  private final SecurityContextRepository securityContexts;

  public ApiController(CommonAdminService service, AuthenticationManager authenticationManager,
                       SecurityContextRepository securityContexts) {
    this.service = service;
    this.authenticationManager = authenticationManager;
    this.securityContexts = securityContexts;
  }

  @GetMapping("/health")
  Map<String, Object> health() {
    return Map.of("status", "UP");
  }

  @PostMapping("/auth/login")
  Map<String, Object> login(@Valid @RequestBody LoginRequest body, HttpServletRequest request,
                            HttpServletResponse response) {
    Map<String, Object> user = service.findLoginUser(body.userId())
        .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호를 확인하세요"));
    if (!body.password().equals(String.valueOf(user.get("password")))
        || !Boolean.TRUE.equals(user.get("system_enabled"))) {
      throw new IllegalArgumentException("아이디 또는 비밀번호를 확인하세요");
    }
    String roleCode = String.valueOf(user.getOrDefault("role_code", "R09"));
    Authentication authentication = new UsernamePasswordAuthenticationToken(
        body.userId(), null, List.of(new SimpleGrantedAuthority("ROLE_" + roleCode)));
    ((UsernamePasswordAuthenticationToken) authentication)
        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context);
    securityContexts.saveContext(context, request, response);
    return Map.of("userId", authentication.getName(), "roleCode", roleCode);
  }

  @GetMapping("/auth/me")
  Map<String, Object> me(Authentication authentication) {
    return Map.of("userId", authentication.getName(), "authenticated", true);
  }

  @PostMapping("/auth/logout")
  ResponseEntity<Void> logout(HttpServletRequest request) {
    var session = request.getSession(false);
    if (session != null) session.invalidate();
    SecurityContextHolder.clearContext();
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasRole('R09')")
  @GetMapping("/admin/users")
  CommonAdminService.PageResult listUsers(@RequestParam(required = false) String query,
                                          @RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "20") int size) {
    return service.list("users", query, page, size);
  }

  @PreAuthorize("hasRole('R09')")
  @GetMapping("/admin/organizations")
  CommonAdminService.PageResult listOrganizations(@RequestParam(required = false) String query,
                                                  @RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "20") int size) {
    return service.list("organizations", query, page, size);
  }

  @PreAuthorize("hasRole('R09')")
  @GetMapping("/admin/positions")
  CommonAdminService.PageResult listPositions(@RequestParam(required = false) String query,
                                              @RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "20") int size) {
    return service.list("positions", query, page, size);
  }

  @PreAuthorize("hasRole('R09')")
  @GetMapping("/admin/roles")
  CommonAdminService.PageResult listRoles(@RequestParam(required = false) String query,
                                          @RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "20") int size) {
    return service.list("roles", query, page, size);
  }

  @PreAuthorize("hasRole('R09')")
  @GetMapping("/admin/user-roles")
  CommonAdminService.PageResult listUserRoles(@RequestParam(required = false) String query,
                                               @RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "20") int size) {
    return service.list("user-roles", query, page, size);
  }

  @PreAuthorize("hasRole('R09')")
  @GetMapping("/admin/menu-permissions")
  CommonAdminService.PageResult listMenuPermissions(@RequestParam(required = false) String query,
                                                    @RequestParam(defaultValue = "1") int page,
                                                    @RequestParam(defaultValue = "20") int size) {
    return service.list("menu-permissions", query, page, size);
  }

  @PreAuthorize("hasRole('R09')")
  @GetMapping("/admin/feature-permissions")
  CommonAdminService.PageResult listFeaturePermissions(@RequestParam(required = false) String query,
                                                       @RequestParam(defaultValue = "1") int page,
                                                       @RequestParam(defaultValue = "20") int size) {
    return service.list("feature-permissions", query, page, size);
  }

  @PreAuthorize("hasRole('R09')")
  @GetMapping("/admin/data-scopes")
  CommonAdminService.PageResult listDataScopes(@RequestParam(required = false) String query,
                                               @RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "20") int size) {
    return service.list("data-scopes", query, page, size);
  }

  @PreAuthorize("hasRole('R09')")
  @PostMapping("/admin/users")
  CommonAdminService.SaveResult createUsers(@RequestBody Map<String, Object> data, Authentication authentication) {
    return service.create("users", data, authentication.getName());
  }

  @PreAuthorize("hasRole('R09')")
  @PostMapping("/admin/organizations")
  CommonAdminService.SaveResult createOrganizations(@RequestBody Map<String, Object> data, Authentication authentication) {
    return service.create("organizations", data, authentication.getName());
  }

  @PreAuthorize("hasRole('R09')")
  @PostMapping("/admin/positions")
  CommonAdminService.SaveResult createPositions(@RequestBody Map<String, Object> data, Authentication authentication) {
    return service.create("positions", data, authentication.getName());
  }

  @PreAuthorize("hasRole('R09')")
  @PostMapping("/admin/roles")
  CommonAdminService.SaveResult createRoles(@RequestBody Map<String, Object> data, Authentication authentication) {
    return service.create("roles", data, authentication.getName());
  }

  @PreAuthorize("hasRole('R09')")
  @PostMapping("/admin/user-roles")
  CommonAdminService.SaveResult createUserRoles(@RequestBody Map<String, Object> data, Authentication authentication) {
    return service.create("user-roles", data, authentication.getName());
  }

  @PreAuthorize("hasRole('R09')")
  @PostMapping("/admin/menu-permissions")
  CommonAdminService.SaveResult createMenuPermissions(@RequestBody Map<String, Object> data, Authentication authentication) {
    return service.create("menu-permissions", data, authentication.getName());
  }

  @PreAuthorize("hasRole('R09')")
  @PostMapping("/admin/feature-permissions")
  CommonAdminService.SaveResult createFeaturePermissions(@RequestBody Map<String, Object> data, Authentication authentication) {
    return service.create("feature-permissions", data, authentication.getName());
  }

  @PreAuthorize("hasRole('R09')")
  @PostMapping("/admin/data-scopes")
  CommonAdminService.SaveResult createDataScopes(@RequestBody Map<String, Object> data, Authentication authentication) {
    return service.create("data-scopes", data, authentication.getName());
  }


  @PreAuthorize("hasRole('R09')")
  @GetMapping("/admin/{entity}")
  CommonAdminService.PageResult listGeneric(@PathVariable String entity,
                                            @RequestParam(required = false) String query,
                                            @RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "20") int size) {
    return service.list(entity, query, page, size);
  }

  @PreAuthorize("hasRole('R09')")
  @PostMapping("/admin/{entity}")
  CommonAdminService.SaveResult createGeneric(@PathVariable String entity,
                                              @RequestBody Map<String, Object> data,
                                              Authentication authentication) {
    return service.create(entity, data, authentication.getName());
  }

  @PreAuthorize("hasRole('R09')")
  @PutMapping("/admin/{entity}/{id}")
  CommonAdminService.SaveResult update(@PathVariable String entity, @PathVariable String id,
                                       @RequestBody Map<String, Object> data,
                                       Authentication authentication) {
    return service.update(entity, id, data, authentication.getName());
  }

  public record LoginRequest(@NotBlank String userId, @NotBlank String password) {}
}
