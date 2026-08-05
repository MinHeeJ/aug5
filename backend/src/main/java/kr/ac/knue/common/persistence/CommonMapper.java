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
    static final Map<String,String> TABLE=Map.ofEntries(
        Map.entry("users","app_users"),Map.entry("organizations","organizations"),
        Map.entry("positions","positions"),Map.entry("roles","roles"),
        Map.entry("user-roles","user_roles"),Map.entry("menu-permissions","menu_permissions"),
        Map.entry("feature-permissions","feature_permissions"),Map.entry("data-scopes","data_scope_permissions"),
        Map.entry("menus","menus"),Map.entry("code-groups","code_groups"),
        Map.entry("codes","detail_codes"),Map.entry("config","system_configs"),
        Map.entry("years","base_years"),Map.entry("file-policies","file_policies"),
        Map.entry("notices","notices"),Map.entry("attachments","attachments"),
        Map.entry("upload-forms","upload_forms"),Map.entry("uploads","excel_uploads"),
        Map.entry("downloads","excel_downloads"),Map.entry("pii","privacy_requests"),
        Map.entry("access","access_events"),Map.entry("audit","audit_logs"),
        Map.entry("batches","batch_definitions"),Map.entry("batch-runs","batch_runs"),
        Map.entry("batch-results","batch_results"));
    static final Map<String,String> ID=Map.ofEntries(
        Map.entry("users","user_id"),Map.entry("organizations","organization_code"),
        Map.entry("positions","id"),Map.entry("roles","role_code"),Map.entry("user-roles","id"),
        Map.entry("menu-permissions","id"),Map.entry("feature-permissions","id"),Map.entry("data-scopes","id"),
        Map.entry("menus","menu_id"),Map.entry("code-groups","group_code"),Map.entry("codes","id"),
        Map.entry("config","config_key"),Map.entry("years","base_year"),Map.entry("file-policies","policy_code"),
        Map.entry("notices","notice_id"),Map.entry("attachments","file_id"),Map.entry("upload-forms","form_code"),
        Map.entry("uploads","upload_id"),Map.entry("downloads","download_id"),Map.entry("pii","request_id"),
        Map.entry("access","access_id"),Map.entry("audit","audit_id"),Map.entry("batches","batch_code"),
        Map.entry("batch-runs","run_id"),Map.entry("batch-results","result_id"));
    static final Map<String,Set<String>> COLUMNS=Map.ofEntries(
        Map.entry("users", Set.of("user_id","password","employee_no","name","organization_code","position_name","employment_status","system_enabled")),
        Map.entry("organizations", Set.of("organization_code","organization_name","organization_type","parent_organization_code","effective_from","effective_to","active")),
        Map.entry("positions", Set.of("user_id","organization_code","role_code","position_name","primary_position","valid_from","valid_to","active")),
        Map.entry("roles", Set.of("role_code","role_name","purpose","assignment_criteria","default_data_scope","active")),
        Map.entry("user-roles", Set.of("user_id","organization_code","role_code","valid_from","valid_to")),
        Map.entry("menu-permissions", Set.of("role_code","subject_type","subject_id","menu_id","screen_id","action_code","scope_type","allowed")),
        Map.entry("feature-permissions", Set.of("role_code","subject_type","subject_id","menu_id","screen_id","action_code","scope_type","allowed")),
        Map.entry("data-scopes", Set.of("role_code","subject_type","subject_id","menu_id","screen_id","action_code","organization_code","scope_type","allowed")),
        Map.entry("menus", Set.of("menu_id","parent_menu_id","menu_name","screen_id","route_path","icon","sort_order","display_order","description","active")),
        Map.entry("code-groups", Set.of("group_code","group_name","description","active")),
        Map.entry("codes", Set.of("group_code","code","code_name","sort_order","active")),
        Map.entry("config", Set.of("config_key","config_value","config_type","description","active")),
        Map.entry("years", Set.of("base_year","status","starts_on","ends_on","active")),
        Map.entry("file-policies", Set.of("policy_code","policy_name","max_size_mb","allowed_extensions","retention_days","active")),
        Map.entry("notices", Set.of("notice_title","notice_body","pinned","published","created_by")),
        Map.entry("attachments", Set.of("file_name","storage_path","content_type","file_size","virus_scan_status","created_by")),
        Map.entry("upload-forms", Set.of("form_code","form_name","template_path","target_table","active")),
        Map.entry("uploads", Set.of("upload_name","form_code","file_id","upload_status","total_rows","success_rows","error_rows","created_by")),
        Map.entry("downloads", Set.of("download_name","target_menu_id","download_status","file_name","created_by")),
        Map.entry("pii", Set.of("subject_user_id","request_type","process_status","masked_field","reason","created_by")),
        Map.entry("access", Set.of("user_id","access_type","ip_address","user_agent","success")),
        Map.entry("audit", Set.of("actor_user_id","action_type","target_type","target_key","before_value","after_value","reason")),
        Map.entry("batches", Set.of("batch_code","batch_name","cron_expression","active")),
        Map.entry("batch-runs", Set.of("batch_code","run_status","started_at","finished_at")),
        Map.entry("batch-results", Set.of("run_id","result_status","message")));
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
