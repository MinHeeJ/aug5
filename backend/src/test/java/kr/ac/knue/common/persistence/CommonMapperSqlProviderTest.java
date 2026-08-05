package kr.ac.knue.common.persistence;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import org.junit.jupiter.api.Test;

class CommonMapperSqlProviderTest {
  @Test
  void insertRejectsUnknownColumnIdentifiers() {
    assertThatThrownBy(() -> CommonMapper.SqlProvider.insert(Map.of(
        "entity", "users",
        "data", Map.of("name; DROP TABLE app_users; --", "attacker"))))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Unsupported column");
  }

  @Test
  void updateRejectsUnknownColumnIdentifiers() {
    assertThatThrownBy(() -> CommonMapper.SqlProvider.update(Map.of(
        "entity", "users",
        "id", "U-1",
        "data", Map.of("name = password", "attacker"))))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Unsupported column");
  }
}
