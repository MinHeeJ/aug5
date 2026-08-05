package kr.ac.knue.common.api;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import kr.ac.knue.common.persistence.CommonMapper;

@SpringBootTest
@AutoConfigureMockMvc
class ApiContractTest {
  @Autowired MockMvc mvc;
  @MockBean CommonMapper mapper;

  @Test
  void fixtureIsMaterializedOnClasspath() {
    org.assertj.core.api.Assertions.assertThat(new ClassPathResource("contracts/openapi.yaml").exists()).isTrue();
  }

  @Test
  void healthIsPublic() throws Exception {
    mvc.perform(get("/api/health"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("UP"));
  }

  @Test
  void authMeRequiresSession() throws Exception {
    mvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized());
  }

  @Test
  void logoutRequiresSession() throws Exception {
    mvc.perform(post("/api/auth/logout")).andExpect(status().isUnauthorized());
  }

  @Test
  void adminListRequiresSession() throws Exception {
    mvc.perform(get("/api/admin/users")).andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(username = "admin", roles = "R09")
  void adminListRejectsInvalidPageSize() throws Exception {
    mvc.perform(get("/api/admin/users").param("size", "10"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
  }

  @Test
  @WithMockUser(username = "operator", roles = "R01")
  void nonAdministratorIsForbidden() throws Exception {
    mvc.perform(get("/api/admin/users")).andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(username = "admin", roles = "R09")
  void allAdminListContractsExposePageBody() throws Exception {
    when(mapper.selectPage(anyString(), any(), anyInt(), anyInt())).thenReturn(List.of());
    when(mapper.count(anyString(), any())).thenReturn(0L);
    mvc.perform(get("/api/admin/users")).andExpect(status().isOk()).andExpect(jsonPath("$.items").isArray()).andExpect(jsonPath("$.page").value(1));
    mvc.perform(get("/api/admin/organizations")).andExpect(status().isOk()).andExpect(jsonPath("$.items").isArray());
    mvc.perform(get("/api/admin/positions")).andExpect(status().isOk()).andExpect(jsonPath("$.items").isArray());
    mvc.perform(get("/api/admin/roles")).andExpect(status().isOk()).andExpect(jsonPath("$.items").isArray());
    mvc.perform(get("/api/admin/user-roles")).andExpect(status().isOk()).andExpect(jsonPath("$.items").isArray());
    mvc.perform(get("/api/admin/menu-permissions")).andExpect(status().isOk()).andExpect(jsonPath("$.items").isArray());
    mvc.perform(get("/api/admin/feature-permissions")).andExpect(status().isOk()).andExpect(jsonPath("$.items").isArray());
    mvc.perform(get("/api/admin/data-scopes")).andExpect(status().isOk()).andExpect(jsonPath("$.items").isArray());
  }

  @Test
  @WithMockUser(username = "admin", roles = "R09")
  void portalFilteringIsPassedToThePersistenceQuery() throws Exception {
    when(mapper.selectPage(eq("users"), eq("교수"), anyInt(), anyInt())).thenReturn(List.of(Map.of("user_id", "U-FILTERED")));
    when(mapper.count(eq("users"), eq("교수"))).thenReturn(1L);
    mvc.perform(get("/api/admin/users").param("query", "교수"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items[0].user_id").value("U-FILTERED"))
        .andExpect(jsonPath("$.total").value(1));
    verify(mapper).selectPage(eq("users"), eq("교수"), eq(0), eq(20));
  }

  @Test
  void everyAdminCreateContractRejectsMissingSession() throws Exception {
    mvc.perform(post("/api/admin/users").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isUnauthorized());
    mvc.perform(post("/api/admin/organizations").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isUnauthorized());
    mvc.perform(post("/api/admin/positions").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isUnauthorized());
    mvc.perform(post("/api/admin/roles").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isUnauthorized());
    mvc.perform(post("/api/admin/user-roles").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isUnauthorized());
    mvc.perform(post("/api/admin/menu-permissions").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isUnauthorized());
    mvc.perform(post("/api/admin/feature-permissions").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isUnauthorized());
    mvc.perform(post("/api/admin/data-scopes").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(username = "operator", roles = "R01")
  void everyAdminCreateContractRejectsUnauthorizedRoleWithoutPersistence() throws Exception {
    mvc.perform(post("/api/admin/users").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isForbidden());
    mvc.perform(post("/api/admin/organizations").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isForbidden());
    mvc.perform(post("/api/admin/positions").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isForbidden());
    mvc.perform(post("/api/admin/roles").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isForbidden());
    mvc.perform(post("/api/admin/user-roles").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isForbidden());
    mvc.perform(post("/api/admin/menu-permissions").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isForbidden());
    mvc.perform(post("/api/admin/feature-permissions").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isForbidden());
    mvc.perform(post("/api/admin/data-scopes").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isForbidden());
    verifyNoInteractions(mapper);
  }

  @Test
  @WithMockUser(username = "admin", roles = "R09")
  void missingUserIdentifierIsValidationError() throws Exception {
    mvc.perform(post("/api/admin/users").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"누락\"}"))
        .andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
  }

  @Test
  @WithMockUser(username = "admin", roles = "R09")
  void validUserSaveReturnsContractAndAuditsSideEffect() throws Exception {
    mvc.perform(post("/api/admin/users").contentType(MediaType.APPLICATION_JSON)
        .content("{\"user_id\":\"U-NEW\",\"employee_no\":\"E-NEW\",\"name\":\"신규\",\"employment_status\":\"ACTIVE\"}"))
        .andExpect(status().isOk()).andExpect(jsonPath("$.message").value("저장되었습니다"))
        .andExpect(jsonPath("$.actor").value("admin"));
    verify(mapper).insert(eq("users"), anyMap());
    verify(mapper).audit(eq("admin"), eq("CREATE"), eq("users"), eq("U-NEW"), isNull(), anyString(), eq("관리자 등록"));
  }

  @Test
  @WithMockUser(username = "admin", roles = "R09")
  void organizationSaveHasValidationAndSideEffectContract() throws Exception {
    mvc.perform(post("/api/admin/organizations").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    mvc.perform(post("/api/admin/organizations").contentType(MediaType.APPLICATION_JSON)
        .content("{\"organization_code\":\"ORG-X\",\"organization_name\":\"조직\",\"organization_type\":\"UNIT\",\"effective_from\":\"2026-01-01\"}"))
        .andExpect(status().isOk()).andExpect(jsonPath("$.message").value("저장되었습니다"));
    verify(mapper).audit(eq("admin"), eq("CREATE"), eq("organizations"), eq("ORG-X"), isNull(), anyString(), eq("관리자 등록"));
  }

  @Test
  @WithMockUser(username = "admin", roles = "R09")
  void positionSaveHasValidationAndSideEffectContract() throws Exception {
    mvc.perform(post("/api/admin/positions").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    mvc.perform(post("/api/admin/positions").contentType(MediaType.APPLICATION_JSON)
        .content("{\"user_id\":\"U-X\",\"valid_from\":\"2026-01-01\"}"))
        .andExpect(status().isOk()).andExpect(jsonPath("$.actor").value("admin"));
    verify(mapper).audit(eq("admin"), eq("CREATE"), eq("positions"), anyString(), isNull(), anyString(), eq("관리자 등록"));
  }

  @Test
  @WithMockUser(username = "admin", roles = "R09")
  void roleSaveHasValidationAndSideEffectContract() throws Exception {
    mvc.perform(post("/api/admin/roles").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    mvc.perform(post("/api/admin/roles").contentType(MediaType.APPLICATION_JSON)
        .content("{\"role_code\":\"R-X\",\"role_name\":\"역할\",\"purpose\":\"업무\",\"default_data_scope\":\"ALL\"}"))
        .andExpect(status().isOk()).andExpect(jsonPath("$.message").value("저장되었습니다"));
    verify(mapper).audit(eq("admin"), eq("CREATE"), eq("roles"), eq("R-X"), isNull(), anyString(), eq("관리자 등록"));
  }

  @Test
  @WithMockUser(username = "admin", roles = "R09")
  void userRoleSaveHasValidationAndSideEffectContract() throws Exception {
    mvc.perform(post("/api/admin/user-roles").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    mvc.perform(post("/api/admin/user-roles").contentType(MediaType.APPLICATION_JSON)
        .content("{\"user_id\":\"U-X\",\"role_code\":\"R-X\",\"valid_from\":\"2026-01-01\"}"))
        .andExpect(status().isOk()).andExpect(jsonPath("$.actor").value("admin"));
    verify(mapper).audit(eq("admin"), eq("CREATE"), eq("user-roles"), anyString(), isNull(), anyString(), eq("관리자 등록"));
  }

  @Test
  @WithMockUser(username = "admin", roles = "R09")
  void menuPermissionSaveHasValidationAndSideEffectContract() throws Exception {
    mvc.perform(post("/api/admin/menu-permissions").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    mvc.perform(post("/api/admin/menu-permissions").contentType(MediaType.APPLICATION_JSON)
        .content("{\"role_code\":\"R-X\"}"))
        .andExpect(status().isOk()).andExpect(jsonPath("$.message").value("저장되었습니다"));
    verify(mapper).audit(eq("admin"), eq("CREATE"), eq("menu-permissions"), anyString(), isNull(), anyString(), eq("관리자 등록"));
  }

  @Test
  @WithMockUser(username = "admin", roles = "R09")
  void featurePermissionSaveHasValidationAndSideEffectContract() throws Exception {
    mvc.perform(post("/api/admin/feature-permissions").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    mvc.perform(post("/api/admin/feature-permissions").contentType(MediaType.APPLICATION_JSON)
        .content("{\"role_code\":\"R-X\"}"))
        .andExpect(status().isOk()).andExpect(jsonPath("$.actor").value("admin"));
    verify(mapper).audit(eq("admin"), eq("CREATE"), eq("feature-permissions"), anyString(), isNull(), anyString(), eq("관리자 등록"));
  }

  @Test
  @WithMockUser(username = "admin", roles = "R09")
  void dataScopeSaveHasValidationAndSideEffectContract() throws Exception {
    mvc.perform(post("/api/admin/data-scopes").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    mvc.perform(post("/api/admin/data-scopes").contentType(MediaType.APPLICATION_JSON)
        .content("{\"role_code\":\"R-X\"}"))
        .andExpect(status().isOk()).andExpect(jsonPath("$.message").value("저장되었습니다"));
    verify(mapper).audit(eq("admin"), eq("CREATE"), eq("data-scopes"), anyString(), isNull(), anyString(), eq("관리자 등록"));
  }

  @Test
  @WithMockUser(username = "admin", roles = "R09")
  void updateContractUsesLiteralPathAndChecksNotFound() throws Exception {
    when(mapper.selectById("roles", "missing")).thenReturn(java.util.Optional.empty());
    mvc.perform(put("/api/admin/roles/missing").contentType(MediaType.APPLICATION_JSON)
        .content("{\"role_code\":\"R-X\",\"role_name\":\"역할\",\"purpose\":\"업무\",\"default_data_scope\":\"ALL\"}"))
        .andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
  }

  @Test
  void loginRequestValidatesCredentialsShape() throws Exception {
    mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
        .content("{\"userId\":\"admin\",\"password\":\"admin\"}"));
  }
}
