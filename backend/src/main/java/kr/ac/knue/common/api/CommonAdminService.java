package kr.ac.knue.common.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kr.ac.knue.common.persistence.CommonMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommonAdminService {
  private static final Map<String, List<String>> REQUIRED = Map.of(
      "users", List.of("user_id", "employee_no", "name", "employment_status"),
      "organizations", List.of("organization_code", "organization_name", "organization_type", "effective_from"),
      "positions", List.of("user_id", "valid_from"),
      "roles", List.of("role_code", "role_name", "purpose", "default_data_scope"),
      "user-roles", List.of("user_id", "role_code", "valid_from"),
      "menu-permissions", List.of("role_code"),
      "feature-permissions", List.of("role_code"),
      "data-scopes", List.of("role_code"));

  private final CommonMapper mapper;
  private final ObjectMapper objectMapper;

  public CommonAdminService(CommonMapper mapper, ObjectMapper objectMapper) {
    this.mapper = mapper;
    this.objectMapper = objectMapper;
  }

  public PageResult list(String entity, String query, int page, int size) {
    validateEntity(entity);
    validateSize(size);
    int safePage = Math.max(1, page);
    List<Map<String, Object>> items = mapper.selectPage(entity, query, (safePage - 1) * size, size);
    return new PageResult(items, safePage, size, mapper.count(entity, query));
  }

  @Transactional
  public SaveResult create(String entity, Map<String, Object> input, String actor) {
    validateEntity(entity);
    Map<String, Object> clean = clean(input);
    validateRequired(entity, clean);
    mapper.insert(entity, clean);
    mapper.audit(actor, "CREATE", entity, targetKey(entity, clean), null, json(clean), "관리자 등록");
    return new SaveResult("저장되었습니다", actor);
  }

  @Transactional
  public SaveResult update(String entity, String id, Map<String, Object> input, String actor) {
    validateEntity(entity);
    Map<String, Object> clean = clean(input);
    validateRequired(entity, clean);
    Map<String, Object> before = mapper.selectById(entity, id).orElseThrow(() -> new IllegalArgumentException("대상을 찾을 수 없습니다"));
    mapper.update(entity, id, clean);
    mapper.audit(actor, "UPDATE", entity, id, json(before), json(clean), "관리자 수정");
    return new SaveResult("수정되었습니다", actor);
  }

  private Map<String, Object> clean(Map<String, Object> input) {
    Map<String, Object> clean = new LinkedHashMap<>();
    input.forEach((key, value) -> {
      if (key.matches("[a-z_]+") && !key.equals("id")) clean.put(key, value);
    });
    return clean;
  }

  private String targetKey(String entity, Map<String, Object> data) {
    return String.valueOf(data.getOrDefault(CommonMapper.SqlProvider.idColumn(entity), "new"));
  }

  private String json(Object value) {
    if (value == null) return null;
    try {
      return objectMapper.writeValueAsString(value);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("감사 로그 변환에 실패했습니다", e);
    }
  }

  private void validateRequired(String entity, Map<String, Object> data) {
    for (String required : REQUIRED.getOrDefault(entity, List.of())) {
      if (data.get(required) == null || String.valueOf(data.get(required)).isBlank()) {
        throw new IllegalArgumentException(required + " is required");
      }
    }
  }

  private void validateEntity(String entity) {
    if (!CommonMapper.SqlProvider.isSupported(entity)) throw new IllegalArgumentException("unsupported entity");
  }

  private void validateSize(int size) {
    if (size != 20 && size != 50 && size != 100) throw new IllegalArgumentException("size must be 20, 50, or 100");
  }

  public record PageResult(List<Map<String, Object>> items, int page, int size, long total) {}
  public record SaveResult(String message, String actor) {}
}
