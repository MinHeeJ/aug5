package kr.ac.knue.common.persistence;

import java.util.*;
import org.apache.ibatis.annotations.*;

@Mapper
public interface CommonMapper {
  @Select("SELECT u.user_id, u.password, u.system_enabled, COALESCE(ur.role_code, 'R09') role_code FROM app_users u LEFT JOIN user_roles ur ON ur.user_id=u.user_id AND ur.valid_to IS NULL WHERE u.user_id=#{id}") Map<String,Object> findUser(String id);
  @SelectProvider(type=SqlProvider.class, method="selectPage") List<Map<String,Object>> selectPage(@Param("entity") String entity, @Param("query") String query, @Param("offset") int offset, @Param("size") int size);
  @SelectProvider(type=SqlProvider.class, method="selectById") Optional<Map<String,Object>> selectById(@Param("entity") String entity, @Param("id") String id);
  @SelectProvider(type=SqlProvider.class, method="count") long count(@Param("entity") String entity, @Param("query") String query);
  @InsertProvider(type=SqlProvider.class, method="insert") void insert(@Param("entity") String entity, @Param("data") Map<String,Object> data);
  @UpdateProvider(type=SqlProvider.class, method="update") void update(@Param("entity") String entity, @Param("id") String id, @Param("data") Map<String,Object> data);
  @Insert("INSERT INTO audit_logs(actor_user_id, action_type, target_type, target_key, before_value, after_value, reason) VALUES(#{actor}, #{action}, #{target}, #{key}, CAST(#{before} AS jsonb), CAST(#{after} AS jsonb), #{reason})") void audit(@Param("actor") String actor, @Param("action") String action, @Param("target") String target, @Param("key") String key, @Param("before") String before, @Param("after") String after, @Param("reason") String reason);

  /**
   * SQL provider with strict entity/column whitelisting.
   *
   * <p>The only strings concatenated into SQL are table and column identifiers selected from immutable
   * server-side maps below. User-controlled values remain MyBatis bind parameters. The generic provider
   * exists only to keep the Batch 1 CRUD surface compact while preventing arbitrary identifiers.
   */
  @SuppressWarnings({"findsecbugs:CUSTOM_INJECTION", "java:S2077"})
  class SqlProvider {
    static final Map<String,String> TABLE=Map.of("users","app_users","organizations","organizations","positions","positions","roles","roles","user-roles","user_roles","menu-permissions","menu_permissions","feature-permissions","feature_permissions","data-scopes","data_scope_permissions");
    static final Map<String,String> ID=Map.of("users","user_id","organizations","organization_code","positions","id","roles","role_code","user-roles","id","menu-permissions","id","feature-permissions","id","data-scopes","id");
    static final Map<String,Set<String>> COLUMNS=Map.of(
        "users", Set.of("user_id","password","employee_no","name","organization_code","position_name","employment_status","system_enabled"),
        "organizations", Set.of("organization_code","organization_name","organization_type","parent_organization_code","effective_from","effective_to","active"),
        "positions", Set.of("user_id","organization_code","role_code","position_name","primary_position","valid_from","valid_to","active"),
        "roles", Set.of("role_code","role_name","purpose","assignment_criteria","default_data_scope","active"),
        "user-roles", Set.of("user_id","organization_code","role_code","valid_from","valid_to"),
        "menu-permissions", Set.of("role_code","subject_type","subject_id","menu_id","screen_id","action_code","scope_type","allowed"),
        "feature-permissions", Set.of("role_code","subject_type","subject_id","menu_id","screen_id","action_code","scope_type","allowed"),
        "data-scopes", Set.of("role_code","subject_type","subject_id","menu_id","screen_id","action_code","organization_code","scope_type","allowed"));
    public static boolean isSupported(String e){ return TABLE.containsKey(e); }
    static String checkedEntity(String e){ if(!TABLE.containsKey(e)||!ID.containsKey(e)) throw new IllegalArgumentException("Unsupported entity"); return e; }
    static String table(String e){ return TABLE.get(checkedEntity(e)); }
    static String checkedColumn(String entity, String column){
      String e=checkedEntity(entity);
      if(column==null || !COLUMNS.get(e).contains(column)) throw new IllegalArgumentException("Unsupported column");
      return column;
    }
    public static String idColumn(String e){ return ID.get(checkedEntity(e)); }
    static String where(String e,String q){
      if(q==null||q.isBlank()) return "";
      String column=switch(e){case "users"->"(user_id ILIKE concat('%',#{query},'%') OR name ILIKE concat('%',#{query},'%'))";case "organizations"->"organization_name ILIKE concat('%',#{query},'%')";case "roles"->"(role_code ILIKE concat('%',#{query},'%') OR role_name ILIKE concat('%',#{query},'%'))";case "positions"->"(position_name ILIKE concat('%',#{query},'%') OR user_id ILIKE concat('%',#{query},'%'))";default->"CAST(id AS text) ILIKE concat('%',#{query},'%')";}; return " WHERE "+column; }
    // nosemgrep: opt.semgrep-rules.gitlab.find_sec_bugs.CUSTOM_INJECTION-2
    public static String selectPage(Map<String,Object> p){ String e=(String)p.get("entity"); return "SELECT * FROM "+table(e)+where(e,(String)p.get("query"))+" ORDER BY created_at DESC LIMIT #{size} OFFSET #{offset}"; }
    // nosemgrep: opt.semgrep-rules.gitlab.find_sec_bugs.CUSTOM_INJECTION-2
    public static String count(Map<String,Object> p){ String e=(String)p.get("entity"); return "SELECT count(*) FROM "+table(e)+where(e,(String)p.get("query")); }
    // nosemgrep: opt.semgrep-rules.gitlab.find_sec_bugs.CUSTOM_INJECTION-2
    public static String selectById(Map<String,Object> p){ String e=(String)p.get("entity"); return "SELECT * FROM "+table(e)+" WHERE "+idColumn(e)+"=#{id}"; }
    // nosemgrep: opt.semgrep-rules.gitlab.find_sec_bugs.CUSTOM_INJECTION-2
    public static String insert(Map<String,Object> p){ String e=(String)p.get("entity"); Map<String,Object>d=(Map<String,Object>)p.get("data"); List<String> keys=new ArrayList<>(d.keySet()); StringJoiner cols=new StringJoiner(","), vals=new StringJoiner(","); for(String k:keys){String column=checkedColumn(e,k);cols.add(column);vals.add("#{data."+column+"}");} return "INSERT INTO "+table(e)+" ("+cols+") VALUES ("+vals+")"; }
    // nosemgrep: opt.semgrep-rules.gitlab.find_sec_bugs.CUSTOM_INJECTION-2
    public static String update(Map<String,Object> p){ String e=(String)p.get("entity"); Map<String,Object>d=(Map<String,Object>)p.get("data"); List<String> keys=new ArrayList<>(d.keySet()); if(keys.isEmpty()) throw new IllegalArgumentException("변경할 값이 없습니다"); StringJoiner sets=new StringJoiner(","); for(String k:keys){String column=checkedColumn(e,k); sets.add(column+"=#{data."+column+"}");} return "UPDATE "+table(e)+" SET "+sets+", updated_at=current_timestamp WHERE "+idColumn(e)+"=#{id}"; }
  }
}
