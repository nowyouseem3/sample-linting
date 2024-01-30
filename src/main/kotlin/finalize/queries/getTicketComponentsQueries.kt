package finalize.queries


// queries for getting all components for creating ticket starts here
const val getTicketCountsQueries = "SELECT record_no FROM sh_helpdesk.tblsupport_ticket_record ORDER BY record_no DESC LIMIT 1"
const val getRequesterQueries = "SELECT firstname, lastname, userid FROM sh_usersinfo.tbluser_details"
const val getRequestMethod = "SELECT * FROM sh_system_administration.tblstatus_bank WHERE status_definition_id = 13 AND display = false ORDER BY display_ordering ASC"
const val getCategoryQueries = "SELECT * FROM sh_helpdesk.tblsupport_category WHERE active = 'true'"
const val getSubCategoryQueries = "SELECT tblsupport_category.support_category_name, support_sub_cat_id, tblsupport_sub_category.support_cat_id, support_sub_cat_name, tblsupport_sub_category.team_active_to  FROM sh_helpdesk.tblsupport_sub_category " +
        "LEFT JOIN sh_helpdesk.tblsupport_category ON tblsupport_category.support_cat_id = tblsupport_sub_category.support_cat_id  WHERE tblsupport_sub_category.support_cat_id = ?"
const val getSpecificSubCategoryQueries = "SELECT tblsupport_category.support_category_name, support_sub_cat_id, tblsupport_sub_category.support_cat_id, support_sub_cat_name, tblsupport_sub_category.team_active_to  FROM sh_helpdesk.tblsupport_sub_category " +
        "LEFT JOIN sh_helpdesk.tblsupport_category ON tblsupport_category.support_cat_id = tblsupport_sub_category.support_cat_id  WHERE tblsupport_sub_category.support_sub_cat_id = ?"
const val getAreaQueries = "SELECT * FROM sh_system_administration.tblcompany_areas"
const val getFloorQueries = "SELECT * FROM sh_system_administration.tblcompany_floors WHERE company_floor_id = ?"
const val getTicketStatusQueries = "SELECT status_id, status_name FROM sh_system_administration.tblstatus_bank WHERE status_definition_id = 11 ORDER BY status_id ASC "
const val getTeamActive = "SELECT team_temp_id FROM sh_helpdesk.tblteam_members WHERE userid = ? and active = true"
const val getEmployeeIdValidate = "SELECT employeeid FROM sh_usersinfo.tbluser_details WHERE userid = ?"
const val getTicketResolved = "SELECT date_inserted FROM sh_helpdesk.tblsupport_actual_records where support_record_no = ? and ticket_current_status = 31 ORDER BY reference_id DESC LIMIT 1"
const val getTicketLogsQueries = "" +
        "SELECT reference_id, record_last_updated_by, date_last_updated, ticket_current_status, status_name, updatable_records, remark_entry, date_inserted, tblsupport_actual_records.locked_to, tblsupport_actual_records.lock_exprtn_timestamp, lock_to_name.firstname, lock_to_name.lastname " +
        "FROM sh_helpdesk.tblsupport_actual_records " +
        "LEFT JOIN sh_system_administration.tblstatus_bank " +
        "ON sh_helpdesk.tblsupport_actual_records.ticket_current_status = sh_system_administration.tblstatus_bank.status_id " +
        "LEFT JOIN sh_usersinfo.tbluser_details as lock_to_name " +
        "ON lock_to_name.userid = tblsupport_actual_records.locked_to " +
        "where support_record_no = ? ORDER BY reference_id"
const val getAssignedSupport = "" +
        "SELECT " +
        "team_temp_id, " +
        "sh_helpdesk.tblteam_members.userid, " +
        "sh_usersinfo.tbluser_details.firstname, " +
        "sh_usersinfo.tbluser_details.lastname," +
        "sh_usersinfo.tbluser_details.employeeid," +
        "sh_helpdesk.tblteam_members.team_member_status , " +
        "sh_system_administration.tblstatus_bank.status_name " +
        "FROM " +
        "sh_helpdesk.tblteam_members, " +
        "sh_usersinfo.tbluser_details," +
        "sh_system_administration.tblstatus_bank " +
        "WHERE " +
        "sh_helpdesk.tblteam_members.active = 'true' AND " +
        "team_temp_id = ? AND " +
        "sh_helpdesk.tblteam_members.userid = sh_usersinfo.tbluser_details.userid and " +
        "sh_helpdesk.tblteam_members.team_member_status = sh_system_administration.tblstatus_bank.status_id"
const val getCoAssignedSupport = "" +
        "SELECT " +
        "team_temp_id, " +
        "sh_helpdesk.tblteam_members.userid, " +
        "sh_usersinfo.tbluser_details.firstname, " +
        "sh_usersinfo.tbluser_details.lastname," +
        "sh_helpdesk.tblteam_members.team_member_status , " +
        "sh_system_administration.tblstatus_bank.status_name " +
        "FROM " +
        "sh_helpdesk.tblteam_members, " +
        "sh_usersinfo.tbluser_details," +
        "sh_system_administration.tblstatus_bank " +
        "WHERE " +
        "sh_helpdesk.tblteam_members.active = 'true' AND " +
        "sh_helpdesk.tblteam_members.userid = ? AND " +
        "sh_helpdesk.tblteam_members.userid = sh_usersinfo.tbluser_details.userid and " +
        "sh_helpdesk.tblteam_members.team_member_status = sh_system_administration.tblstatus_bank.status_id"
const val getTicketForSpecificSupport = "SELECT * FROM sh_helpdesk.tblsupport_ticket_record WHERE assigned_support_personnel = ?"
const val getLockToActualTicket = "SELECT locked_to FROM sh_helpdesk.tblsupport_actual_records WHERE reference_id = ? "
const val getLockToTicketRecord = "SELECT locked_to FROM sh_helpdesk.tblsupport_ticket_record WHERE tblsupport_ticket_record.record_no = ? "
const val insertResolveEmail = "INSERT INTO sh_mails_and_sms.tblaccount_mail_records (account_mail_subject, account_mail_body, account_mail_recipient, account_mail_identifier_id) VALUES (?,?,?,?)"
// query for inserting ticket here
const val insertTicketLogs = "INSERT INTO sh_helpdesk.tblsupport_actual_records (support_record_no, ticket_current_status, remark_entry, record_inserted_by, date_inserted, record_last_updated_by, date_last_updated, locked_to, updatable_records) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
const val insertTicketQueries = "" +
        "INSERT INTO sh_helpdesk.tblsupport_ticket_record " +
        "( " +
        "ticket_no, " +
        "company_area_id, " +
        "sub_category_id, " +
        "requestor_remarks, " +
        "reported_by, " +
        "report_method_id, " +
        "date_reported, " +
        "time_reported, " +
        "ticket_current_status, " +
        "inserted_by, " +
        "date_inserted, " +
        "assigned_support_personnel, " +
        "support_assigned_by, " +
        "date_support_assigned, " +
        "actual_supported_employee, " +
        "exclusive_to_team_id " +
        ") " +
        "values " +
        "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
const val getAllTicketQueue = ""+
        "SELECT DISTINCT " +
        "tblsupport_ticket_record.record_no, " +
        "tblsupport_ticket_record.ticket_no, " +
        "tblsupport_ticket_record.ticket_current_status, " +
        "ticket_status.status_name as ticket_status, " +
        "tblsupport_ticket_record.date_inserted, " +
        "tblsupport_ticket_record.reported_by, " +
        "tblsupport_ticket_record.requestor_remarks, " +
        "requester.firstname as reported_by_firstname, " +
        "requester.lastname as reported_by_lastname, " +
        "tblsupport_ticket_record.actual_supported_employee, " +
        "recipient.firstname as recipient_firstname, " +
        "recipient.lastname as recipient_lastname, " +
        "recipient.company_email as recipient_email, " +
        "company_area.company_area_name, " +
        "company_area.company_area_short_name, " +
        "company_area.company_area_id, " +
        "company_area.company_floor_id, " +
        "company_floor.floor_name, " +
        "company_floor.building_area, " +
        "tblsupport_ticket_record.sub_category_id, " +
        "sub_category.support_sub_cat_name, " +
        "category.support_category_name, " +
        "tblsupport_ticket_record.report_method_id, " +
        "reported_bank.status_name as reported_method, " +
        "tblsupport_ticket_record.date_reported, " +
        "tblsupport_ticket_record.time_reported, " +
        "tblsupport_ticket_record.inserted_by, " +
        "tblsupport_ticket_record.assigned_support_personnel, " +
        "support.firstname as support_by_firstname, " +
        "support.lastname as support_by_lastname, " +
        "support.company_email as support_email, " +
        "tblsupport_ticket_record.co_support_personnel, " +
        "tblsupport_ticket_record.support_assigned_by, " +
        "tblsupport_ticket_record.date_support_assigned, " +
        "tblsupport_ticket_record.last_updated_by, " +
        "last_updated_by.firstname as last_updated_by_firstname, " +
        "last_updated_by.lastname as last_updated_by_lastname, " +
        "tblsupport_ticket_record.last_date_updated," +
        "tblsupport_ticket_record.exclusive_to_team_id, " +
        "company_position.position_name as recipient_position " +
        "FROM " +
        "sh_helpdesk.tblsupport_ticket_record " +
        "LEFT JOIN " +
        "sh_usersinfo.tbluser_details as requester " +
        "ON " +
        "tblsupport_ticket_record.reported_by = requester.userid " +
        "LEFT JOIN " +
        "sh_usersinfo.tbluser_details as recipient " +
        "ON " +
        "tblsupport_ticket_record.actual_supported_employee = recipient.userid " +
        "LEFT JOIN " +
        "sh_usersinfo.tbluser_details as support " +
        "ON " +
        "tblsupport_ticket_record.assigned_support_personnel = support.userid " +
        "LEFT JOIN " +
        "sh_system_administration.tblstatus_bank as reported_bank " +
        "ON " +
        "tblsupport_ticket_record.report_method_id = reported_bank.status_id " +
        "LEFT JOIN " +
        "sh_system_administration.tblstatus_bank as ticket_status " +
        "ON " +
        "tblsupport_ticket_record.ticket_current_status = ticket_status.status_id " +
        "LEFT JOIN " +
        "sh_usersinfo.tbluser_details as last_updated_by " +
        "ON " +
        "tblsupport_ticket_record.last_updated_by = last_updated_by.userid " +
        "LEFT JOIN " +
        "sh_system_administration.tblcompany_areas as company_area " +
        "ON " +
        "tblsupport_ticket_record.company_area_id = company_area.company_area_id " +
        "LEFT JOIN  " +
        "sh_system_administration.tblcompany_floors as company_floor " +
        "ON " +
        "company_area.company_floor_id = company_floor.company_floor_id " +
        "LEFT JOIN " +
        "sh_usersinfo.tblemployee_designation_details as company_designation " +
        "ON " +
        "company_designation.userid = recipient.userid " +
        "LEFT JOIN " +
        "sh_system_administration.tblcompany_position_bank as company_position " +
        "ON " +
        "company_position.position_id = company_designation.position_id " +
        "LEFT JOIN " +
        "sh_helpdesk.tblsupport_sub_category as sub_category " +
        "ON " +
        "CONCAT(';',tblsupport_ticket_record.sub_category_id) " +
        "LIKE " +
        "CONCAT('%;',sub_category.support_sub_cat_id,';%') " +
        "LEFT JOIN " +
        "sh_helpdesk.tblsupport_category as category " +
        "ON " +
        "category.support_cat_id = sub_category.support_cat_id " +
        "WHERE " +
        "tblsupport_ticket_record.exclusive_to_team_id = ?" +
        "AND " +
        "Date(tblsupport_ticket_record.date_inserted) BETWEEN ? and ? " +
        "ORDER BY " +
        "ticket_current_status ASC"
const val getAllTicketQueueReports = ""+
        "SELECT DISTINCT " +
        "tblsupport_ticket_record.record_no, " +
        "tblsupport_ticket_record.ticket_no, " +
        "tblsupport_ticket_record.ticket_current_status, " +
        "ticket_status.status_name as ticket_status, " +
        "tblsupport_ticket_record.date_inserted, " +
        "tblsupport_ticket_record.reported_by, " +
        "tblsupport_ticket_record.requestor_remarks, " +
        "requester.firstname as reported_by_firstname, " +
        "requester.lastname as reported_by_lastname, " +
        "tblsupport_ticket_record.actual_supported_employee, " +
        "recipient.firstname as recipient_firstname, " +
        "recipient.lastname as recipient_lastname, " +
        "recipient.company_email as recipient_email, " +
        "company_area.company_area_name, " +
        "company_area.company_area_short_name, " +
        "company_area.company_area_id, " +
        "company_area.company_floor_id, " +
        "company_floor.floor_name, " +
        "company_floor.building_area, " +
        "tblsupport_ticket_record.sub_category_id, " +
        "sub_category.support_sub_cat_name, " +
        "category.support_category_name, " +
        "tblsupport_ticket_record.report_method_id, " +
        "reported_bank.status_name as reported_method, " +
        "tblsupport_ticket_record.date_reported, " +
        "tblsupport_ticket_record.time_reported, " +
        "tblsupport_ticket_record.inserted_by, " +
        "tblsupport_ticket_record.assigned_support_personnel, " +
        "support.firstname as support_by_firstname, " +
        "support.lastname as support_by_lastname, " +
        "support.company_email as support_email, " +
        "tblsupport_ticket_record.co_support_personnel, " +
        "tblsupport_ticket_record.support_assigned_by, " +
        "tblsupport_ticket_record.date_support_assigned, " +
        "tblsupport_ticket_record.last_updated_by, " +
        "last_updated_by.firstname as last_updated_by_firstname, " +
        "last_updated_by.lastname as last_updated_by_lastname, " +
        "tblsupport_ticket_record.last_date_updated," +
        "tblsupport_ticket_record.exclusive_to_team_id, " +
        "company_position.position_name as recipient_position " +
        "FROM " +
        "sh_helpdesk.tblsupport_ticket_record " +
        "LEFT JOIN " +
        "sh_usersinfo.tbluser_details as requester " +
        "ON " +
        "tblsupport_ticket_record.reported_by = requester.userid " +
        "LEFT JOIN " +
        "sh_usersinfo.tbluser_details as recipient " +
        "ON " +
        "tblsupport_ticket_record.actual_supported_employee = recipient.userid " +
        "LEFT JOIN " +
        "sh_usersinfo.tbluser_details as support " +
        "ON " +
        "tblsupport_ticket_record.assigned_support_personnel = support.userid " +
        "LEFT JOIN " +
        "sh_system_administration.tblstatus_bank as reported_bank " +
        "ON " +
        "tblsupport_ticket_record.report_method_id = reported_bank.status_id " +
        "LEFT JOIN " +
        "sh_system_administration.tblstatus_bank as ticket_status " +
        "ON " +
        "tblsupport_ticket_record.ticket_current_status = ticket_status.status_id " +
        "LEFT JOIN " +
        "sh_usersinfo.tbluser_details as last_updated_by " +
        "ON " +
        "tblsupport_ticket_record.last_updated_by = last_updated_by.userid " +
        "LEFT JOIN " +
        "sh_system_administration.tblcompany_areas as company_area " +
        "ON " +
        "tblsupport_ticket_record.company_area_id = company_area.company_area_id " +
        "LEFT JOIN  " +
        "sh_system_administration.tblcompany_floors as company_floor " +
        "ON " +
        "company_area.company_floor_id = company_floor.company_floor_id " +
        "LEFT JOIN " +
        "sh_usersinfo.tblemployee_designation_details as company_designation " +
        "ON " +
        "company_designation.userid = recipient.userid " +
        "LEFT JOIN " +
        "sh_system_administration.tblcompany_position_bank as company_position " +
        "ON " +
        "company_position.position_id = company_designation.position_id " +
        "LEFT JOIN " +
        "sh_helpdesk.tblsupport_sub_category as sub_category " +
        "ON " +
        "CONCAT(';',tblsupport_ticket_record.sub_category_id) " +
        "LIKE " +
        "CONCAT('%;',sub_category.support_sub_cat_id,';%') " +
        "LEFT JOIN " +
        "sh_helpdesk.tblsupport_category as category " +
        "ON " +
        "category.support_cat_id = sub_category.support_cat_id " +
        "WHERE " +
        "tblsupport_ticket_record.exclusive_to_team_id = ? " +
        "AND " +
        "Date(tblsupport_ticket_record.date_inserted) BETWEEN ? and ? " +
        "ORDER BY " +
        "ticket_current_status ASC"
const val getAllTicketsQuery = ""+
        "SELECT DISTINCT " +
        "tblsupport_ticket_record.record_no, " +
        "tblsupport_ticket_record.ticket_no, " +
        "tblsupport_ticket_record.ticket_current_status, " +
        "ticket_status.status_name as ticket_status, " +
        "tblsupport_ticket_record.date_inserted, " +
        "tblsupport_ticket_record.reported_by, " +
        "tblsupport_ticket_record.requestor_remarks, " +
        "requester.firstname as reported_by_firstname, " +
        "requester.lastname as reported_by_lastname, " +
        "tblsupport_ticket_record.actual_supported_employee, " +
        "recipient.firstname as recipient_firstname, " +
        "recipient.lastname as recipient_lastname, " +
        "recipient.company_email as recipient_email, " +
        "company_area.company_area_name, " +
        "company_area.company_area_short_name, " +
        "company_area.company_area_id, " +
        "company_area.company_floor_id, " +
        "company_floor.floor_name, " +
        "company_floor.building_area, " +
        "tblsupport_ticket_record.sub_category_id, " +
        "sub_category.support_sub_cat_name, " +
        "category.support_category_name, " +
        "tblsupport_ticket_record.report_method_id, " +
        "reported_bank.status_name as reported_method, " +
        "tblsupport_ticket_record.date_reported, " +
        "tblsupport_ticket_record.time_reported, " +
        "tblsupport_ticket_record.inserted_by, " +
        "tblsupport_ticket_record.assigned_support_personnel, " +
        "support.firstname as support_by_firstname, " +
        "support.lastname as support_by_lastname, " +
        "support.company_email as support_email, " +
        "tblsupport_ticket_record.co_support_personnel, " +
        "tblsupport_ticket_record.support_assigned_by, " +
        "tblsupport_ticket_record.date_support_assigned, " +
        "tblsupport_ticket_record.last_updated_by, " +
        "last_updated_by.firstname as last_updated_by_firstname, " +
        "last_updated_by.lastname as last_updated_by_lastname, " +
        "tblsupport_ticket_record.last_date_updated," +
        "tblsupport_ticket_record.exclusive_to_team_id, " +
        "company_position.position_name as recipient_position, " +
        "tblsupport_ticket_record.locked_to, " +
        "tblsupport_ticket_record.lock_exprtn_timestamp " +
        "FROM " +
        "sh_helpdesk.tblsupport_ticket_record " +
        "LEFT JOIN " +
        "sh_usersinfo.tbluser_details as requester " +
        "ON " +
        "tblsupport_ticket_record.reported_by = requester.userid " +
        "LEFT JOIN " +
        "sh_usersinfo.tbluser_details as recipient " +
        "ON " +
        "tblsupport_ticket_record.actual_supported_employee = recipient.userid " +
        "LEFT JOIN " +
        "sh_usersinfo.tbluser_details as support " +
        "ON " +
        "tblsupport_ticket_record.assigned_support_personnel = support.userid " +
        "LEFT JOIN " +
        "sh_system_administration.tblstatus_bank as reported_bank " +
        "ON " +
        "tblsupport_ticket_record.report_method_id = reported_bank.status_id " +
        "LEFT JOIN " +
        "sh_system_administration.tblstatus_bank as ticket_status " +
        "ON " +
        "tblsupport_ticket_record.ticket_current_status = ticket_status.status_id " +
        "LEFT JOIN " +
        "sh_usersinfo.tbluser_details as last_updated_by " +
        "ON " +
        "tblsupport_ticket_record.last_updated_by = last_updated_by.userid " +
        "LEFT JOIN " +
        "sh_system_administration.tblcompany_areas as company_area " +
        "ON " +
        "tblsupport_ticket_record.company_area_id = company_area.company_area_id " +
        "LEFT JOIN  " +
        "sh_system_administration.tblcompany_floors as company_floor " +
        "ON " +
        "company_area.company_floor_id = company_floor.company_floor_id " +
        "LEFT JOIN " +
        "sh_usersinfo.tblemployee_designation_details as company_designation " +
        "ON " +
        "company_designation.userid = recipient.userid " +
        "LEFT JOIN " +
        "sh_system_administration.tblcompany_position_bank as company_position " +
        "ON " +
        "company_position.position_id = company_designation.position_id " +
        "LEFT JOIN " +
        "sh_helpdesk.tblsupport_sub_category as sub_category " +
        "ON " +
        "CONCAT(';',tblsupport_ticket_record.sub_category_id) " +
        "LIKE " +
        "CONCAT('%;',sub_category.support_sub_cat_id,';%') " +
        "LEFT JOIN " +
        "sh_helpdesk.tblsupport_category as category " +
        "ON " +
        "category.support_cat_id = sub_category.support_cat_id " +
        "WHERE " +
        "exclusive_to_team_id = ? " +
        "ORDER BY " +
        "ticket_current_status ASC, record_no DESC"
const val getMyTicketsQuery = ""+
        "SELECT DISTINCT " +
        "tblsupport_ticket_record.record_no, " +
        "tblsupport_ticket_record.ticket_no, " +
        "tblsupport_ticket_record.ticket_current_status, " +
        "ticket_status.status_name as ticket_status, " +
        "tblsupport_ticket_record.date_inserted, " +
        "tblsupport_ticket_record.reported_by, " +
        "tblsupport_ticket_record.requestor_remarks, " +
        "requester.firstname as reported_by_firstname, " +
        "requester.lastname as reported_by_lastname, " +
        "tblsupport_ticket_record.actual_supported_employee, " +
        "recipient.firstname as recipient_firstname, " +
        "recipient.lastname as recipient_lastname, " +
        "recipient.company_email as recipient_email, " +
        "company_area.company_area_name, " +
        "company_area.company_area_short_name, " +
        "company_area.company_area_id, " +
        "company_area.company_floor_id, " +
        "company_floor.floor_name, " +
        "company_floor.building_area, " +
        "tblsupport_ticket_record.sub_category_id, " +
        "sub_category.support_sub_cat_name, " +
        "category.support_category_name, " +
        "tblsupport_ticket_record.report_method_id, " +
        "reported_bank.status_name as reported_method, " +
        "tblsupport_ticket_record.date_reported, " +
        "tblsupport_ticket_record.time_reported, " +
        "tblsupport_ticket_record.inserted_by, " +
        "tblsupport_ticket_record.assigned_support_personnel, " +
        "support.firstname as support_by_firstname, " +
        "support.lastname as support_by_lastname, " +
        "support.company_email as support_email, " +
        "tblsupport_ticket_record.co_support_personnel, " +
        "tblsupport_ticket_record.support_assigned_by, " +
        "tblsupport_ticket_record.date_support_assigned, " +
        "tblsupport_ticket_record.last_updated_by, " +
        "last_updated_by.firstname as last_updated_by_firstname, " +
        "last_updated_by.lastname as last_updated_by_lastname, " +
        "tblsupport_ticket_record.last_date_updated," +
        "tblsupport_ticket_record.exclusive_to_team_id, " +
        "company_position.position_name as recipient_position, " +
        "tblsupport_ticket_record.locked_to, " +
        "tblsupport_ticket_record.lock_exprtn_timestamp " +
        "FROM " +
        "sh_helpdesk.tblsupport_ticket_record " +
        "LEFT JOIN " +
        "sh_usersinfo.tbluser_details as requester " +
        "ON " +
        "tblsupport_ticket_record.reported_by = requester.userid " +
        "LEFT JOIN " +
        "sh_usersinfo.tbluser_details as recipient " +
        "ON " +
        "tblsupport_ticket_record.actual_supported_employee = recipient.userid " +
        "LEFT JOIN " +
        "sh_usersinfo.tbluser_details as support " +
        "ON " +
        "tblsupport_ticket_record.assigned_support_personnel = support.userid " +
        "LEFT JOIN " +
        "sh_system_administration.tblstatus_bank as reported_bank " +
        "ON " +
        "tblsupport_ticket_record.report_method_id = reported_bank.status_id " +
        "LEFT JOIN " +
        "sh_system_administration.tblstatus_bank as ticket_status " +
        "ON " +
        "tblsupport_ticket_record.ticket_current_status = ticket_status.status_id " +
        "LEFT JOIN " +
        "sh_usersinfo.tbluser_details as last_updated_by " +
        "ON " +
        "tblsupport_ticket_record.last_updated_by = last_updated_by.userid " +
        "LEFT JOIN " +
        "sh_system_administration.tblcompany_areas as company_area " +
        "ON " +
        "tblsupport_ticket_record.company_area_id = company_area.company_area_id " +
        "LEFT JOIN  " +
        "sh_system_administration.tblcompany_floors as company_floor " +
        "ON " +
        "company_area.company_floor_id = company_floor.company_floor_id " +
        "LEFT JOIN " +
        "sh_usersinfo.tblemployee_designation_details as company_designation " +
        "ON " +
        "company_designation.userid = recipient.userid " +
        "LEFT JOIN " +
        "sh_system_administration.tblcompany_position_bank as company_position " +
        "ON " +
        "company_position.position_id = company_designation.position_id " +
        "LEFT JOIN " +
        "sh_helpdesk.tblsupport_sub_category as sub_category " +
        "ON " +
        "CONCAT(';',tblsupport_ticket_record.sub_category_id) " +
        "LIKE " +
        "CONCAT('%;',sub_category.support_sub_cat_id,';%') " +
        "LEFT JOIN " +
        "sh_helpdesk.tblsupport_category as category " +
        "ON " +
        "category.support_cat_id = sub_category.support_cat_id " +
        "WHERE " +
        "assigned_support_personnel = ? " +
        "OR " +
        "actual_supported_employee = ? " +
        "ORDER BY " +
        "ticket_current_status ASC, record_no DESC"
const val getAllTickets = ""+
        "SELECT DISTINCT " +
        "tblsupport_ticket_record.record_no, " +
        "tblsupport_ticket_record.ticket_no, " +
        "tblsupport_ticket_record.ticket_current_status, " +
        "ticket_status.status_name as ticket_status, " +
        "tblsupport_ticket_record.date_inserted, " +
        "tblsupport_ticket_record.reported_by, " +
        "tblsupport_ticket_record.requestor_remarks, " +
        "requester.firstname as reported_by_firstname, " +
        "requester.lastname as reported_by_lastname, " +
        "tblsupport_ticket_record.actual_supported_employee, " +
        "recipient.firstname as recipient_firstname, " +
        "recipient.lastname as recipient_lastname, " +
        "recipient.company_email as recipient_email, " +
        "company_area.company_area_name, " +
        "company_area.company_area_short_name, " +
        "company_area.company_area_id, " +
        "company_area.company_floor_id, " +
        "company_floor.floor_name, " +
        "company_floor.building_area, " +
        "tblsupport_ticket_record.sub_category_id, " +
        "sub_category.support_sub_cat_name, " +
        "category.support_category_name, " +
        "tblsupport_ticket_record.report_method_id, " +
        "reported_bank.status_name as reported_method, " +
        "tblsupport_ticket_record.date_reported, " +
        "tblsupport_ticket_record.time_reported, " +
        "tblsupport_ticket_record.inserted_by, " +
        "tblsupport_ticket_record.assigned_support_personnel, " +
        "support.firstname as support_by_firstname, " +
        "support.lastname as support_by_lastname, " +
        "support.company_email as support_email, " +
        "tblsupport_ticket_record.co_support_personnel, " +
        "tblsupport_ticket_record.support_assigned_by, " +
        "tblsupport_ticket_record.date_support_assigned, " +
        "tblsupport_ticket_record.last_updated_by, " +
        "last_updated_by.firstname as last_updated_by_firstname, " +
        "last_updated_by.lastname as last_updated_by_lastname, " +
        "tblsupport_ticket_record.last_date_updated," +
        "tblsupport_ticket_record.exclusive_to_team_id, " +
        "company_position.position_name as recipient_position " +
        "FROM " +
        "sh_helpdesk.tblsupport_ticket_record " +
        "LEFT JOIN " +
        "sh_usersinfo.tbluser_details as requester " +
        "ON " +
        "tblsupport_ticket_record.reported_by = requester.userid " +
        "LEFT JOIN " +
        "sh_usersinfo.tbluser_details as recipient " +
        "ON " +
        "tblsupport_ticket_record.actual_supported_employee = recipient.userid " +
        "LEFT JOIN " +
        "sh_usersinfo.tbluser_details as support " +
        "ON " +
        "tblsupport_ticket_record.assigned_support_personnel = support.userid " +
        "LEFT JOIN " +
        "sh_system_administration.tblstatus_bank as reported_bank " +
        "ON " +
        "tblsupport_ticket_record.report_method_id = reported_bank.status_id " +
        "LEFT JOIN " +
        "sh_system_administration.tblstatus_bank as ticket_status " +
        "ON " +
        "tblsupport_ticket_record.ticket_current_status = ticket_status.status_id " +
        "LEFT JOIN " +
        "sh_usersinfo.tbluser_details as last_updated_by " +
        "ON " +
        "tblsupport_ticket_record.last_updated_by = last_updated_by.userid " +
        "LEFT JOIN " +
        "sh_system_administration.tblcompany_areas as company_area " +
        "ON " +
        "tblsupport_ticket_record.company_area_id = company_area.company_area_id " +
        "LEFT JOIN  " +
        "sh_system_administration.tblcompany_floors as company_floor " +
        "ON " +
        "company_area.company_floor_id = company_floor.company_floor_id " +
        "LEFT JOIN " +
        "sh_usersinfo.tblemployee_designation_details as company_designation " +
        "ON " +
        "company_designation.userid = recipient.userid " +
        "LEFT JOIN " +
        "sh_system_administration.tblcompany_position_bank as company_position " +
        "ON " +
        "company_position.position_id = company_designation.position_id " +
        "LEFT JOIN " +
        "sh_helpdesk.tblsupport_sub_category as sub_category " +
        "ON " +
        "CONCAT(';',tblsupport_ticket_record.sub_category_id) " +
        "LIKE " +
        "CONCAT('%;',sub_category.support_sub_cat_id,';%') " +
        "LEFT JOIN " +
        "sh_helpdesk.tblsupport_category as category " +
        "ON " +
        "category.support_cat_id = sub_category.support_cat_id " +
        "WHERE " +
        "tblsupport_ticket_record.record_no = ?" +
        "ORDER BY " +
        "ticket_current_status ASC"
const val getTicketsClose = ""+
        "SELECT DISTINCT " +
        "tblsupport_ticket_record.record_no, " +
        "tblsupport_ticket_record.ticket_no, " +
        "tblsupport_ticket_record.ticket_current_status, " +
        "ticket_status.status_name as ticket_status, " +
        "tblsupport_ticket_record.date_inserted, " +
        "tblsupport_ticket_record.reported_by, " +
        "tblsupport_ticket_record.requestor_remarks, " +
        "requester.firstname as reported_by_firstname, " +
        "requester.lastname as reported_by_lastname, " +
        "tblsupport_ticket_record.actual_supported_employee, " +
        "recipient.firstname as recipient_firstname, " +
        "recipient.lastname as recipient_lastname, " +
        "tblsupport_ticket_record.sub_category_id, " +
        "tblsupport_ticket_record.co_support_personnel, " +
        "company_area.company_area_name, " +
        "company_area.company_area_short_name, " +
        "company_area.company_area_id, " +
        "company_area.company_floor_id, " +
        "company_floor.floor_name, " +
        "company_floor.building_area, " +
        "reported_bank.status_name as reported_method, " +
        "tblsupport_ticket_record.date_reported, " +
        "tblsupport_ticket_record.time_reported, " +
        "support.firstname as support_by_firstname, " +
        "support.lastname as support_by_lastname, " +
        "support.company_email as support_email, " +
        "tblsupport_ticket_record.assigned_support_personnel, " +
        "last_updated_by.firstname as last_updated_by_firstname, " +
        "last_updated_by.lastname as last_updated_by_lastname, " +
        "tblsupport_ticket_record.last_date_updated " +
        "FROM " +
        "sh_helpdesk.tblsupport_ticket_record " +
        "LEFT JOIN " +
        "sh_usersinfo.tbluser_details as requester " +
        "ON " +
        "tblsupport_ticket_record.reported_by = requester.userid " +
        "LEFT JOIN " +
        "sh_usersinfo.tbluser_details as recipient " +
        "ON " +
        "tblsupport_ticket_record.actual_supported_employee = recipient.userid " +
        "LEFT JOIN " +
        "sh_usersinfo.tbluser_details as support " +
        "ON " +
        "tblsupport_ticket_record.assigned_support_personnel = support.userid " +
        "LEFT JOIN " +
        "sh_system_administration.tblstatus_bank as reported_bank " +
        "ON " +
        "tblsupport_ticket_record.report_method_id = reported_bank.status_id " +
        "LEFT JOIN " +
        "sh_system_administration.tblstatus_bank as ticket_status " +
        "ON " +
        "tblsupport_ticket_record.ticket_current_status = ticket_status.status_id " +
        "LEFT JOIN " +
        "sh_usersinfo.tbluser_details as last_updated_by " +
        "ON " +
        "tblsupport_ticket_record.last_updated_by = last_updated_by.userid " +
        "LEFT JOIN " +
        "sh_system_administration.tblcompany_areas as company_area " +
        "ON " +
        "tblsupport_ticket_record.company_area_id = company_area.company_area_id " +
        "LEFT JOIN  " +
        "sh_system_administration.tblcompany_floors as company_floor " +
        "ON " +
        "company_area.company_floor_id = company_floor.company_floor_id " +
        "ORDER BY " +
        "ticket_current_status ASC, record_no DESC"
const val getAllRecipientTicketsQuery = "" +
        "SELECT DISTINCT " +
        "tblsupport_ticket_record.record_no, " +
        "tblsupport_ticket_record.ticket_no, " +
        "tblsupport_ticket_record.ticket_current_status, " +
        "ticket_status.status_name as ticket_status, " +
        "tblsupport_ticket_record.date_inserted, " +
        "tblsupport_ticket_record.reported_by, " +
        "tblsupport_ticket_record.requestor_remarks, " +
        "requester.firstname as reported_by_firstname, " +
        "requester.lastname as reported_by_lastname, " +
        "tblsupport_ticket_record.actual_supported_employee, " +
        "recipient.firstname as recipient_firstname, " +
        "recipient.lastname as recipient_lastname, " +
        "recipient.company_email as recipient_email, " +
        "company_area.company_area_name, " +
        "company_area.company_area_id, " +
        "company_area.company_floor_id, " +
        "company_area.company_area_short_name, " +
        "company_floor.floor_name, " +
        "company_floor.building_area, " +
        "tblsupport_ticket_record.sub_category_id, " +
        "sub_category.support_sub_cat_name, " +
        "category.support_category_name, " +
        "tblsupport_ticket_record.report_method_id, " +
        "reported_bank.status_name as reported_method, " +
        "tblsupport_ticket_record.date_reported, " +
        "tblsupport_ticket_record.time_reported, " +
        "tblsupport_ticket_record.inserted_by, " +
        "tblsupport_ticket_record.assigned_support_personnel, " +
        "support.firstname as support_by_firstname, " +
        "support.lastname as support_by_lastname, " +
        "tblsupport_ticket_record.co_support_personnel, " +
        "tblsupport_ticket_record.support_assigned_by, " +
        "tblsupport_ticket_record.date_support_assigned, " +
        "tblsupport_ticket_record.last_updated_by, " +
        "last_updated_by.firstname as last_updated_by_firstname, " +
        "last_updated_by.lastname as last_updated_by_lastname, " +
        "tblsupport_ticket_record.last_date_updated," +
        "tblsupport_ticket_record.exclusive_to_team_id, " +
        "company_position.position_name as recipient_position " +
        "FROM " +
        "sh_helpdesk.tblsupport_ticket_record " +
        "LEFT JOIN " +
        "sh_usersinfo.tbluser_details as requester " +
        "ON " +
        "tblsupport_ticket_record.reported_by = requester.userid " +
        "LEFT JOIN " +
        "sh_usersinfo.tbluser_details as recipient " +
        "ON " +
        "tblsupport_ticket_record.actual_supported_employee = recipient.userid " +
        "LEFT JOIN " +
        "sh_usersinfo.tbluser_details as support " +
        "ON " +
        "tblsupport_ticket_record.assigned_support_personnel = support.userid " +
        "LEFT JOIN " +
        "sh_system_administration.tblstatus_bank as reported_bank " +
        "ON " +
        "tblsupport_ticket_record.report_method_id = reported_bank.status_id " +
        "LEFT JOIN " +
        "sh_system_administration.tblstatus_bank as ticket_status " +
        "ON " +
        "tblsupport_ticket_record.ticket_current_status = ticket_status.status_id " +
        "LEFT JOIN " +
        "sh_usersinfo.tbluser_details as last_updated_by " +
        "ON " +
        "tblsupport_ticket_record.last_updated_by = last_updated_by.userid " +
        "LEFT JOIN " +
        "sh_system_administration.tblcompany_areas as company_area " +
        "ON " +
        "tblsupport_ticket_record.company_area_id = company_area.company_area_id " +
        "LEFT JOIN  " +
        "sh_system_administration.tblcompany_floors as company_floor " +
        "ON " +
        "company_area.company_floor_id = company_floor.company_floor_id " +
        "LEFT JOIN " +
        "sh_usersinfo.tblemployee_designation_details as company_designation " +
        "ON " +
        "company_designation.userid = recipient.userid " +
        "LEFT JOIN " +
        "sh_system_administration.tblcompany_position_bank as company_position " +
        "ON " +
        "company_position.position_id = company_designation.position_id " +
        "LEFT JOIN " +
        "sh_helpdesk.tblsupport_sub_category as sub_category " +
        "ON " +
        "CONCAT(';',tblsupport_ticket_record.sub_category_id) " +
        "LIKE " +
        "CONCAT('%;',sub_category.support_sub_cat_id,';%') " +
        "LEFT JOIN " +
        "sh_helpdesk.tblsupport_category as category " +
        "ON " +
        "category.support_cat_id = sub_category.support_cat_id " +
        "WHERE " +
        "tblsupport_ticket_record.actual_supported_employee = ? " +
        "ORDER BY " +
        "ticket_current_status ASC, record_no DESC"
// get system key and access token here
const val checkSystemKeyQuery = "SELECT recordid,system_key_name,system_key,expiration_date,active,added_by,date_added FROM sh_system_administration.tblsystem_key ORDER BY recordid ASC"
const val getSecretIVAndSecretKeyQuery = "SELECT system_key_name, system_key FROM sh_system_administration.tblsystem_key WHERE system_key_name IN ('iv-key-16', 'secret-key-32')"
const val getAccessToken = "SELECT access_token FROM sh_system_administration.tblactive_session WHERE userid = ?"
// query for updating tickets starts here
const val updateTicket = "UPDATE sh_helpdesk.tblsupport_ticket_record SET reported_by = ?, actual_supported_employee = ?, report_method_id = ?, sub_category_id = ?, requestor_remarks = ?, company_area_id = ?, assigned_support_personnel = ?, date_reported = ?, time_reported = ?, last_updated_by = ?, last_date_updated = ?, ticket_current_status = ?, support_assigned_by = ?, exclusive_to_team_id = ? WHERE record_no = ?"
const val updateTicketAssign = "UPDATE sh_helpdesk.tblsupport_ticket_record SET assigned_support_personnel = ?, support_assigned_by = ?, last_updated_by = ?, last_date_updated = ?, date_support_assigned = ?, ticket_current_status = 30 WHERE record_no = ?"
const val updateTicketResolve = "UPDATE sh_helpdesk.tblsupport_ticket_record SET  last_updated_by = ?, last_date_updated = ?, ticket_current_status = 31 WHERE record_no = ?"
const val updateTicketClose = "UPDATE sh_helpdesk.tblsupport_ticket_record SET last_updated_by = ?, last_date_updated = ?, ticket_current_status = 32 WHERE record_no = ?"
const val updateTicketReOngoing = "UPDATE sh_helpdesk.tblsupport_ticket_record SET last_updated_by = ?, last_date_updated = ?, ticket_current_status = 30 WHERE record_no = ?"
const val updateTicketCoSupport = "UPDATE sh_helpdesk.tblsupport_ticket_record SET co_support_personnel = ?, support_assigned_by = ?, last_updated_by = ?, last_date_updated = ?, ticket_current_status = 30 WHERE record_no = ?"
const val updateSupportStatusEngage = "UPDATE sh_helpdesk.tblteam_members set team_member_status = 34 WHERE userid = ?"
const val updateSupportStatusAvailable = "UPDATE sh_helpdesk.tblteam_members set team_member_status = 33 WHERE userid = ?"
const val updateActualTicketEditing = "UPDATE sh_helpdesk.tblsupport_actual_records SET locked_to = ?, lock_exprtn_timestamp = ? WHERE reference_id = ?"
const val updateTicketRecordEditing = "UPDATE sh_helpdesk.tblsupport_ticket_record SET locked_to = ?, lock_exprtn_timestamp = ? WHERE record_no = ?"
const val updateActualTicket = "UPDATE sh_helpdesk.tblsupport_actual_records SET remark_entry = ?, ticket_current_status = ?, record_last_updated_by = ?, date_last_updated = ? WHERE reference_id = ?"

//get system module
const val getSystemModule = "" +
        "SELECT * FROM sh_system_administration.tblsystem_modules WHERE applicable_to_employee = ? and applicable_to_crew = ? and applicable_to_partners = ?"

