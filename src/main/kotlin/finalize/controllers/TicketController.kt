package finalize.controllers

import finalize.models.*
import finalize.plugins.*
import finalize.queries.*
import get.plugins.DBConfig
import java.sql.Date
import java.sql.PreparedStatement
import java.sql.Time
import java.sql.Timestamp
import java.time.*
import java.util.*

class TicketController {
    // ----------------------get team active to -------------------------------
    fun getTeamActiveTo(userId: String): Int{
        return DBConfig().connection().use { con ->
            var teamActive = 0
            con.prepareStatement(getTeamActive).use {
                it.setString(1, userId)
                it.executeQuery().use { dataResult ->
                    if (dataResult.next()) teamActive = dataResult.getInt("team_temp_id")
                    teamActive
                }
            }
        }
    }
    // ------------------create ticket controller here--------------------------
    fun createTicket(reportedById: String, actualToSupportId: String, subCategory: MutableList<String>, requesterRemarks: String, timeReported: String, dateReported: String, reportedMethodId: Int, companyAreaId: Int, insertedBy: String, supportAssignedBy: String ?, assignedPersonnelId: String ?, exclusiveToTeam: Int ?) : Int {
        var ticketStatus = 29
        var subCategoryValue : String ? = null
        var date: Date ? = null
        var time: Time ? = null
        var assignedPersonnel: String ? = null
        var assignedPersonnelBy: String ? = null
        var timeAssigned: Timestamp ? = null
        // ----validate if not yet assigned personnel----
        if (assignedPersonnelId!!.isNotEmpty()){
            ticketStatus = 30
            assignedPersonnelBy = supportAssignedBy
            assignedPersonnel = assignedPersonnelId
            timeAssigned = Timestamp.valueOf(timeDate())
        }
        // -----validate the ticket reported date and time-----
        if (timeReported.isNotBlank() && dateReported.isNotBlank()){
            val dateBanks = dateSeparator(dateTimeRefactor(dateReported))
            val timeBanks = timeSeparator(timeReported)
            date = Date.valueOf(LocalDate.of(dateBanks[0].year,dateBanks[0].month,dateBanks[0].day))
            time = Time.valueOf(LocalTime.of(timeBanks[0].hours,timeBanks[0].minutes,timeBanks[0].seconds))
        }
        // ------validate if sub category is empty----------
        if (subCategory.isNotEmpty()){
            subCategoryValue = toConcatFormatter(subCategory)
        }
        return DBConfig().connection().use { con ->
            con.prepareStatement(insertTicketQueries).use {
                it.setInt(1, ticketGenerator().toInt())
                it.setInt(2, companyAreaId)
                it.setString(3,subCategoryValue)
                it.setString(4, requesterRemarks)
                it.setString(5, reportedById)
                it.setInt(6, reportedMethodId)
                it.setDate(7, date)
                it.setTime(8, time)
                it.setInt(9, ticketStatus)
                it.setString(10, insertedBy)
                it.setTimestamp(11, Timestamp.valueOf(timeDate()))
                it.setString(12, assignedPersonnel)
                it.setString(13, assignedPersonnelBy)
                it.setTimestamp(14, timeAssigned)
                it.setString(15, actualToSupportId)
                it.setInt(16, exclusiveToTeam!!.toInt())

                it.executeUpdate()
            }
        }
    }

    // -------------------------inserting logs here-----------------------------
    fun createTicketLogs(ticketStatus: Int, remarks: String, recordInsertedBy: String, recordNum: Int, isEditable: Boolean): Int {
        return DBConfig().connection().use { con ->
            con.prepareStatement(insertTicketLogs).use {
                it.setInt(1, recordNum)
                it.setInt(2, ticketStatus)
                it.setString(3, remarks)
                it.setString(4, recordInsertedBy)
                it.setTimestamp(5, Timestamp.valueOf(timeDate()))
                it.setString(6, recordInsertedBy)
                it.setTimestamp(7, Timestamp.valueOf(timeDate()))
                it.setString(8, null)
                it.setBoolean(9, isEditable)

                it.executeUpdate()
            }
        }
    }

    // -----------------------get created ticket here-------------------------
    fun getAllTickets(recNum: Int): MutableList<GetTicketBanks>{
        val getTicketBanksData = mutableListOf<GetTicketBanks>()
        return DBConfig().connection().use { con ->
            con.prepareStatement(getAllTickets).use {
                it.setInt(1, recNum)
                it.executeQuery().use { dataResult ->
                    while (dataResult.next()){
                        val recordNum = dataResult.getInt("record_no")
                        val ticketNum = dataResult.getInt("ticket_no")
                        val ticketStatusId = dataResult.getInt("ticket_current_status")
                        val ticketStatus = dataResult.getString("ticket_status")
                        val dateCreated = dataResult.getString("date_inserted")
                        val reportedById = dataResult.getString("reported_by")
                        val requesterRemarks = dataResult.getString("requestor_remarks")
                        val reportedByFName = dataResult.getString("reported_by_firstname")
                        val reportedByLName = dataResult.getString("reported_by_lastname")
                        val reportedByName = "$reportedByLName, $reportedByFName"
                        val actualToSupport = dataResult.getString("actual_supported_employee")
                        val actualToSupportFName = dataResult.getString("recipient_firstname")
                        val actualToSupportLName = dataResult.getString("recipient_lastname")
                        val actualToSupportName = "$actualToSupportLName, $actualToSupportFName"
                        val actualToSupportPosition = dataResult.getString("recipient_position")
                        val actualToSupportEmail = dataResult.getString("recipient_email")
                        val companyAreaName = dataResult.getString("company_area_name")
                        val companyAreaShortName = dataResult.getString("company_area_short_name")
                        val companyFloorName = dataResult.getString("floor_name")
                        val companyBuildingArea = dataResult.getString("building_area")
                        val companyAreaId = dataResult.getInt("company_area_id")
                        val companyFloorId = dataResult.getInt("company_floor_id")
                        val subCategoryIdList = dataResult.getString("sub_category_id")
                        val subCategoryName = dataResult.getString("support_sub_cat_name")
                        val categoryName = dataResult.getString("support_category_name")
                        val reportedMethodId = dataResult.getInt("report_method_id")
                        val reportedMethod = dataResult.getString("reported_method")
                        val dateReported = dataResult.getString("date_reported")
                        val timeReported = dataResult.getString("time_reported")
                        val insertedBy = dataResult.getString("inserted_by")
                        val assignedSupportId = dataResult.getString("assigned_support_personnel")
                        val assignedSupportFName = dataResult.getString("support_by_firstname")
                        val assignedSupportLName = dataResult.getString("support_by_lastname")
                        var assignedSupportName: String ? = null
                        val assignedSupportEmail: String ? = dataResult?.getString("support_email")
                        if (assignedSupportFName != null && assignedSupportLName != null) assignedSupportName = "$assignedSupportLName, $assignedSupportFName"
                        val coSupportPersonnel = dataResult.getString("co_support_personnel")
                        val supportAssignedBy = dataResult.getString("support_assigned_by")
                        val dateSupportAssigned = dataResult.getString("date_support_assigned")
                        val lastUpdatedById = dataResult.getString("last_updated_by")
                        val lastUpdatedByFName = dataResult.getString("last_updated_by_firstname")
                        val lastUpdatedByLName = dataResult.getString("last_updated_by_lastname")
                        var lastUpdatedByName: String ? = null
                        if (lastUpdatedByFName != null && lastUpdatedByLName != null) lastUpdatedByName = "$lastUpdatedByLName, $lastUpdatedByFName"
                        val lastDateUpdated = dataResult.getString("last_date_updated")
                        val exclusiveToTeam = dataResult.getInt("exclusive_to_team_id")
                        getTicketBanksData.add(GetTicketBanks(
                            recordNum,
                            toTicketGenerator(ticketNum.toString()),
                            ticketStatusId,
                            ticketStatus,
                            dateCreated,
                            requesterRemarks,
                            reportedById,
                            reportedByName,
                            actualToSupport,
                            actualToSupportName,
                            actualToSupportPosition,
                            actualToSupportEmail,
                            subCategoryIdList,
                            "$companyAreaName ($companyAreaShortName)",
                            companyAreaId,
                            "$companyFloorName, $companyBuildingArea",
                            companyFloorId,
                            categoryName,
                            subCategoryName,
                            ticketIssueCounter(concatFormatters(subCategoryIdList)),
                            reportedMethodId,
                            reportedMethod,
                            "$dateReported $timeReported",
                            insertedBy,
                            assignedSupportId,
                            assignedSupportName,
                            assignedSupportEmail,
                            coSupportPersonnel,
                            supportAssignedBy,
                            dateSupportAssigned,
                            lastUpdatedById,
                            lastUpdatedByName,
                            lastDateUpdated,
                            addingFiveDays(lastDateUpdated),
                            exclusiveToTeam,
                            null,
                            null
                        ))
                    }
                    getTicketBanksData
                }
            }
        }
    }
    fun getTickets(exclusiveTo: Int, recipientId : String ?, specification: Int, from: String ?, to: String ?, support: Int, all: Int): MutableList<GetTicketBanks>{
        val getTicketBanksData = mutableListOf<GetTicketBanks>()
        return DBConfig().connection().use { con ->
            if (support == 1) {
                val statement : PreparedStatement = if (all == 1) con.prepareStatement(getAllTicketsQuery) else con.prepareStatement(getMyTicketsQuery)
                statement.use {
                    if (all == 1) {
                        it.setInt(1, exclusiveTo)
                    }
                    else {
                        it.setString(1, recipientId)
                        it.setString(2, recipientId)
                    }
                    it.executeQuery().use { dataResult ->
                        var counter = 0
                        var lastId = ""
                        while (dataResult.next()){
                            val recordNum = dataResult.getInt("record_no")
                            val ticketNum = dataResult.getInt("ticket_no")
                            val ticketStatusId = dataResult.getInt("ticket_current_status")
                            val ticketStatus = dataResult.getString("ticket_status")
                            val dateCreated = dataResult.getString("date_inserted")
                            val reportedById = dataResult.getString("reported_by")
                            val requesterRemarks = dataResult.getString("requestor_remarks")
                            val reportedByFName = dataResult.getString("reported_by_firstname")
                            val reportedByLName = dataResult.getString("reported_by_lastname")
                            val reportedByName = "$reportedByLName, $reportedByFName"
                            val actualToSupport = dataResult.getString("actual_supported_employee")
                            val actualToSupportFName = dataResult.getString("recipient_firstname")
                            val actualToSupportLName = dataResult.getString("recipient_lastname")
                            val actualToSupportName = "$actualToSupportLName, $actualToSupportFName"
                            val actualToSupportPosition = dataResult.getString("recipient_position")
                            val actualToSupportEmail = dataResult.getString("recipient_email")
                            val companyAreaName = dataResult.getString("company_area_name")
                            val companyAreaShortName = dataResult.getString("company_area_short_name")
                            val companyFloorName = dataResult.getString("floor_name")
                            val companyBuildingArea = dataResult.getString("building_area")
                            val companyAreaId = dataResult.getInt("company_area_id")
                            val companyFloorId = dataResult.getInt("company_floor_id")
                            val subCategoryIdList = dataResult.getString("sub_category_id")
                            val subCategoryName = dataResult.getString("support_sub_cat_name")
                            val categoryName = dataResult.getString("support_category_name")
                            val reportedMethodId = dataResult.getInt("report_method_id")
                            val reportedMethod = dataResult.getString("reported_method")
                            val dateReported = dataResult.getString("date_reported")
                            val timeReported = dataResult.getString("time_reported")
                            val insertedBy = dataResult.getString("inserted_by")
                            val assignedSupportId = dataResult.getString("assigned_support_personnel")
                            val assignedSupportFName = dataResult.getString("support_by_firstname")
                            val assignedSupportLName = dataResult.getString("support_by_lastname")
                            var assignedSupportName: String ? = null
                            val assignedSupportEmail: String ? = dataResult?.getString("support_email")
                            if (assignedSupportFName != null && assignedSupportLName != null) assignedSupportName = "$assignedSupportLName, $assignedSupportFName"
                            val coSupportPersonnel = dataResult.getString("co_support_personnel")
                            val supportAssignedBy = dataResult.getString("support_assigned_by")
                            val dateSupportAssigned = dataResult.getString("date_support_assigned")
                            val lastUpdatedById = dataResult.getString("last_updated_by")
                            val lastUpdatedByFName = dataResult.getString("last_updated_by_firstname")
                            val lastUpdatedByLName = dataResult.getString("last_updated_by_lastname")
                            var lastUpdatedByName: String ? = null
                            if (lastUpdatedByFName != null && lastUpdatedByLName != null) lastUpdatedByName = "$lastUpdatedByLName, $lastUpdatedByFName"
                            val lastDateUpdated = dataResult.getString("last_date_updated")
                            val exclusiveToTeam = dataResult.getInt("exclusive_to_team_id")
                            val lockedTo: String ?= dataResult?.getString("locked_to")
                            val lockedToExpiration: String ? = dataResult?.getString("lock_exprtn_timestamp")
                            when (specification){
                                0 -> if (counter > 5) break else if (ticketStatusId == 32 && lastId != ticketNum.toString()) counter += 1 else lastId = ticketNum.toString()
                                29 -> if (ticketStatusId != 29) break
                                30 -> if (ticketStatusId != 30) continue
                                31 -> if (ticketStatusId != 31) continue
                                32 -> {
                                    if (ticketStatusId != 32) continue
                                    else {
                                        val result: Boolean = toDateStamp(dateTimeSeparator(lastDateUpdated)[0].date) >= toDateStamp(from.toString()) && toDateStamp(dateTimeSeparator(lastDateUpdated)[0].date) <= toDateStamp(to.toString())
                                        if (!result) continue
                                    }
                                }
                            }
                            val toClosed : String ? = if (ticketStatusId == 31) addingFiveDays(lastDateUpdated).toString()
                            else null
                            getTicketBanksData.add(GetTicketBanks(
                                recordNum,
                                toTicketGenerator(ticketNum.toString()),
                                ticketStatusId,
                                ticketStatus,
                                dateCreated,
                                requesterRemarks,
                                reportedById,
                                reportedByName,
                                actualToSupport,
                                actualToSupportName,
                                actualToSupportPosition,
                                actualToSupportEmail,
                                subCategoryIdList,
                                "$companyAreaName ($companyAreaShortName)",
                                companyAreaId,
                                "$companyFloorName, $companyBuildingArea",
                                companyFloorId,
                                categoryName,
                                subCategoryName,
                                ticketIssueCounter(concatFormatters(subCategoryIdList)),
                                reportedMethodId,
                                reportedMethod,
                                "$dateReported $timeReported",
                                insertedBy,
                                assignedSupportId,
                                assignedSupportName,
                                assignedSupportEmail,
                                coSupportPersonnel,
                                supportAssignedBy,
                                dateSupportAssigned,
                                lastUpdatedById,
                                lastUpdatedByName,
                                lastDateUpdated,
                                toClosed,
                                exclusiveToTeam,
                                lockedTo,
                                lockedToExpiration
                            ))
                        }
                        getTicketBanksData
                    }
                }
            }else{
                con.prepareStatement(getAllRecipientTicketsQuery).use {
                    it.setString(1, recipientId)
                    it.executeQuery().use { dataResult ->
                        var counter = 0
                        var lastId = ""
                        while (dataResult.next()){
                            val recordNum = dataResult.getInt("record_no")
                            val ticketNum = dataResult.getInt("ticket_no")
                            val ticketStatusId = dataResult.getInt("ticket_current_status")
                            val ticketStatus = dataResult.getString("ticket_status")
                            val dateCreated = dataResult.getString("date_inserted")
                            val reportedById = dataResult.getString("reported_by")
                            val requesterRemarks = dataResult.getString("requestor_remarks")
                            val reportedByFName = dataResult.getString("reported_by_firstname")
                            val reportedByLName = dataResult.getString("reported_by_lastname")
                            val reportedByName = "$reportedByLName, $reportedByFName"
                            val actualToSupport = dataResult.getString("actual_supported_employee")
                            val actualToSupportFName = dataResult.getString("recipient_firstname")
                            val actualToSupportLName = dataResult.getString("recipient_lastname")
                            val actualToSupportName = "$actualToSupportLName, $actualToSupportFName"
                            val actualToSupportPosition = dataResult.getString("recipient_position")
                            val actualToSupportEmail = dataResult.getString("recipient_email")
                            val companyAreaName = dataResult.getString("company_area_name")
                            val companyAreaShortName = dataResult.getString("company_area_short_name")
                            val companyFloorName = dataResult.getString("floor_name")
                            val companyBuildingArea = dataResult.getString("building_area")
                            val companyAreaId = dataResult.getInt("company_area_id")
                            val companyFloorId = dataResult.getInt("company_floor_id")
                            val subCategoryIdList = dataResult.getString("sub_category_id")
                            val subCategoryName = dataResult.getString("support_sub_cat_name")
                            val categoryName = dataResult.getString("support_category_name")
                            val reportedMethodId = dataResult.getInt("report_method_id")
                            val reportedMethod = dataResult.getString("reported_method")
                            val dateReported = dataResult.getString("date_reported")
                            val timeReported = dataResult.getString("time_reported")
                            val insertedBy = dataResult.getString("inserted_by")
                            val assignedSupportId = dataResult.getString("assigned_support_personnel")
                            val assignedSupportFName = dataResult.getString("support_by_firstname")
                            val assignedSupportLName = dataResult.getString("support_by_lastname")
                            var assignedSupportName: String ? = null
                            if (assignedSupportFName != null && assignedSupportLName != null) assignedSupportName = "$assignedSupportLName, $assignedSupportFName"
                            val coSupportPersonnel = dataResult.getString("co_support_personnel")
                            val supportAssignedBy = dataResult.getString("support_assigned_by")
                            val dateSupportAssigned = dataResult.getString("date_support_assigned")
                            val lastUpdatedById = dataResult.getString("last_updated_by")
                            val lastUpdatedByFName = dataResult.getString("last_updated_by_firstname")
                            val lastUpdatedByLName = dataResult.getString("last_updated_by_lastname")
                            var lastUpdatedByName: String ? = null
                            if (lastUpdatedByFName != null && lastUpdatedByLName != null) lastUpdatedByName = "$lastUpdatedByLName, $lastUpdatedByFName"
                            val lastDateUpdated = dataResult.getString("last_date_updated")
                            val exclusiveToTeam = dataResult.getInt("exclusive_to_team_id")
                            when (specification){
                                0 -> if (counter > 5) break else if (ticketStatusId == 32 && lastId != ticketNum.toString()) counter += 1 else lastId = ticketNum.toString()
                                29 -> if (ticketStatusId != 29) break
                                30 -> if (ticketStatusId != 30) continue
                                31 -> if (ticketStatusId != 31) continue
                                32 -> {
                                    if (ticketStatusId != 32) continue
                                    else {
                                        val result: Boolean = toDateStamp(dateTimeSeparator(lastDateUpdated)[0].date) >= toDateStamp(from.toString()) && toDateStamp(dateTimeSeparator(lastDateUpdated)[0].date) <= toDateStamp(to.toString())
                                        if (!result) continue
                                    }
                                }
                            }
                            val toClosed : String ? = if (ticketStatusId == 31) addingFiveDays(lastDateUpdated).toString()
                            else null
                            getTicketBanksData.add(GetTicketBanks(
                                recordNum,
                                toTicketGenerator(ticketNum.toString()),
                                ticketStatusId,
                                ticketStatus,
                                dateCreated,
                                requesterRemarks,
                                reportedById,
                                reportedByName,
                                actualToSupport,
                                actualToSupportName,
                                actualToSupportPosition,
                                actualToSupportEmail,
                                subCategoryIdList,
                                "$companyAreaName ($companyAreaShortName)",
                                companyAreaId,
                                "$companyFloorName, $companyBuildingArea",
                                companyFloorId,
                                categoryName,
                                subCategoryName,
                                ticketIssueCounter(concatFormatters(subCategoryIdList)),
                                reportedMethodId,
                                reportedMethod,
                                "$dateReported $timeReported",
                                insertedBy,
                                assignedSupportId,
                                assignedSupportName,
                                null,
                                coSupportPersonnel,
                                supportAssignedBy,
                                dateSupportAssigned,
                                lastUpdatedById,
                                lastUpdatedByName,
                                lastDateUpdated,
                                toClosed,
                                exclusiveToTeam,
                                null,
                                null
                            ))
                        }
                        getTicketBanksData
                    }
                }
            }
        }
    }
    // ------------------------get filtered tickets disregard duplicates ----------------------
    fun getFilteredTicket(ticket: MutableList<GetTicketBanks>) : MutableList<GetTicketBanks>{
        val item = mutableListOf<GetTicketBanks>()
        for (index in ticket.indices){
            if (ticket.size == 1){
                item.add(
                    GetTicketBanks(
                        ticket[index].recordNum,
                        ticket[index].ticketNum,
                        ticket[index].ticketStatusId,
                        ticket[index].ticketStatus,
                        ticket[index].ticketCreated,
                        ticket[index].requesterRemarks,
                        ticket[index].reportedById,
                        ticket[index].reportedByName,
                        ticket[index].actualToSupportId,
                        ticket[index].actualToSupportName,
                        ticket[index].actualToSupportPosition,
                        ticket[index].actualToSupportEmail,
                        ticket[index].subCategory.toString(),
                        ticket[index].companyArea,
                        ticket[index].companyAreaId,
                        ticket[index].companyFloorBanks,
                        ticket[index].companyFloorId,
                        ticket[index].categoryName,
                        ticket[index].subCategoryName,
                        ticket[index].subCategoryRestCount,
                        ticket[index].reportedMethodId,
                        ticket[index].reportedMethod,
                        ticket[index].dateTimeReported,
                        ticket[index].insertedBy,
                        ticket[index].assignedSupportId,
                        ticket[index].assignedSupportName,
                        ticket[index].assignedSupportEmail,
                        ticket[index].coSupportIdList,
                        ticket[index].supportAssignedBy,
                        ticket[index].dateSupportAssigned,
                        ticket[index].lastUpdatedById,
                        ticket[index].lastUpdatedByName,
                        ticket[index].lastDateUpdated,
                        ticket[index].toClose,
                        ticket[index].exclusiveToTeam,
                        ticket[index].lockedTo,
                        ticket[index].expirationDate
                    )
                )
            } else {
                if (index == ticket.lastIndex-1){
                    if (ticket[index].recordNum != ticket[index  + 1].recordNum){
                        item.add(
                            GetTicketBanks(
                                ticket[index].recordNum,
                                ticket[index].ticketNum,
                                ticket[index].ticketStatusId,
                                ticket[index].ticketStatus,
                                ticket[index].ticketCreated,
                                ticket[index].requesterRemarks,
                                ticket[index].reportedById,
                                ticket[index].reportedByName,
                                ticket[index].actualToSupportId,
                                ticket[index].actualToSupportName,
                                ticket[index].actualToSupportPosition,
                                ticket[index].actualToSupportEmail,
                                ticket[index].subCategory.toString(),
                                ticket[index].companyArea,
                                ticket[index].companyAreaId,
                                ticket[index].companyFloorBanks,
                                ticket[index].companyFloorId,
                                ticket[index].categoryName,
                                ticket[index].subCategoryName,
                                ticket[index].subCategoryRestCount,
                                ticket[index].reportedMethodId,
                                ticket[index].reportedMethod,
                                ticket[index].dateTimeReported,
                                ticket[index].insertedBy,
                                ticket[index].assignedSupportId,
                                ticket[index].assignedSupportName,
                                ticket[index].assignedSupportEmail,
                                ticket[index].coSupportIdList,
                                ticket[index].supportAssignedBy,
                                ticket[index].dateSupportAssigned,
                                ticket[index].lastUpdatedById,
                                ticket[index].lastUpdatedByName,
                                ticket[index].lastDateUpdated,
                                ticket[index].toClose,
                                ticket[index].exclusiveToTeam,
                                ticket[index].lockedTo,
                                ticket[index].expirationDate
                            )
                        )
                        item.add(
                            GetTicketBanks(
                                ticket[ticket.size - 1].recordNum,
                                ticket[ticket.size - 1].ticketNum,
                                ticket[ticket.size - 1].ticketStatusId,
                                ticket[ticket.size - 1].ticketStatus,
                                ticket[ticket.size - 1].ticketCreated,
                                ticket[ticket.size - 1].requesterRemarks,
                                ticket[ticket.size - 1].reportedById,
                                ticket[ticket.size - 1].reportedByName,
                                ticket[ticket.size - 1].actualToSupportId,
                                ticket[ticket.size - 1].actualToSupportName,
                                ticket[ticket.size - 1].actualToSupportPosition,
                                ticket[ticket.size - 1].actualToSupportEmail,
                                ticket[ticket.size - 1].subCategory.toString(),
                                ticket[ticket.size - 1].companyArea,
                                ticket[ticket.size - 1].companyAreaId,
                                ticket[ticket.size - 1].companyFloorBanks,
                                ticket[ticket.size - 1].companyFloorId,
                                ticket[ticket.size - 1].categoryName,
                                ticket[ticket.size - 1].subCategoryName,
                                ticket[ticket.size - 1].subCategoryRestCount,
                                ticket[ticket.size - 1].reportedMethodId,
                                ticket[ticket.size - 1].reportedMethod,
                                ticket[ticket.size - 1].dateTimeReported,
                                ticket[ticket.size - 1].insertedBy,
                                ticket[ticket.size - 1].assignedSupportId,
                                ticket[ticket.size - 1].assignedSupportName,
                                ticket[ticket.size - 1].assignedSupportEmail,
                                ticket[ticket.size - 1].coSupportIdList,
                                ticket[ticket.size - 1].supportAssignedBy,
                                ticket[ticket.size - 1].dateSupportAssigned,
                                ticket[ticket.size - 1].lastUpdatedById,
                                ticket[ticket.size - 1].lastUpdatedByName,
                                ticket[ticket.size - 1].lastDateUpdated,
                                ticket[ticket.size - 1].toClose,
                                ticket[ticket.size - 1].exclusiveToTeam,
                                ticket[ticket.size - 1].lockedTo,
                                ticket[ticket.size - 1].expirationDate
                            )
                        )
                        break
                    } else {
                        item.add(
                            GetTicketBanks(
                                ticket[index + 1].recordNum,
                                ticket[index + 1].ticketNum,
                                ticket[index + 1].ticketStatusId,
                                ticket[index + 1].ticketStatus,
                                ticket[index + 1].ticketCreated,
                                ticket[index + 1].requesterRemarks,
                                ticket[index + 1].reportedById,
                                ticket[index + 1].reportedByName,
                                ticket[index + 1].actualToSupportId,
                                ticket[index + 1].actualToSupportName,
                                ticket[index + 1].actualToSupportPosition,
                                ticket[index + 1].actualToSupportEmail,
                                ticket[index + 1].subCategory.toString(),
                                ticket[index + 1].companyArea,
                                ticket[index + 1].companyAreaId,
                                ticket[index + 1].companyFloorBanks,
                                ticket[index + 1].companyFloorId,
                                ticket[index + 1].categoryName,
                                ticket[index + 1].subCategoryName,
                                ticket[index + 1].subCategoryRestCount,
                                ticket[index + 1].reportedMethodId,
                                ticket[index + 1].reportedMethod,
                                ticket[index + 1].dateTimeReported,
                                ticket[index + 1].insertedBy,
                                ticket[index + 1].assignedSupportId,
                                ticket[index + 1].assignedSupportName,
                                ticket[index + 1].assignedSupportEmail,
                                ticket[index + 1].coSupportIdList,
                                ticket[index + 1].supportAssignedBy,
                                ticket[index + 1].dateSupportAssigned,
                                ticket[index + 1].lastUpdatedById,
                                ticket[index + 1].lastUpdatedByName,
                                ticket[index + 1].lastDateUpdated,
                                ticket[index + 1].toClose,
                                ticket[index + 1].exclusiveToTeam,
                                ticket[index + 1].lockedTo,
                                ticket[index + 1].expirationDate
                            )
                        )
                        break
                    }
                } else if (ticket[index].recordNum != ticket[index + 1].recordNum){
                    item.add(
                        GetTicketBanks(
                            ticket[index].recordNum,
                            ticket[index].ticketNum,
                            ticket[index].ticketStatusId,
                            ticket[index].ticketStatus,
                            ticket[index].ticketCreated,
                            ticket[index].requesterRemarks,
                            ticket[index].reportedById,
                            ticket[index].reportedByName,
                            ticket[index].actualToSupportId,
                            ticket[index].actualToSupportName,
                            ticket[index].actualToSupportPosition,
                            ticket[index].actualToSupportEmail,
                            ticket[index].subCategory.toString(),
                            ticket[index].companyArea,
                            ticket[index].companyAreaId,
                            ticket[index].companyFloorBanks,
                            ticket[index].companyFloorId,
                            ticket[index].categoryName,
                            ticket[index].subCategoryName,
                            ticket[index].subCategoryRestCount,
                            ticket[index].reportedMethodId,
                            ticket[index].reportedMethod,
                            ticket[index].dateTimeReported,
                            ticket[index].insertedBy,
                            ticket[index].assignedSupportId,
                            ticket[index].assignedSupportName,
                            ticket[index].assignedSupportEmail,
                            ticket[index].coSupportIdList,
                            ticket[index].supportAssignedBy,
                            ticket[index].dateSupportAssigned,
                            ticket[index].lastUpdatedById,
                            ticket[index].lastUpdatedByName,
                            ticket[index].lastDateUpdated,
                            ticket[index].toClose,
                            ticket[index].exclusiveToTeam,
                            ticket[index].lockedTo,
                            ticket[index].expirationDate
                        )
                    )
                }
            }

        }
        return item
    }

    // ------------------------get ticket for queuing --------------------
    fun getTicketQueue(teamActive:Int, from: String, to:String): MutableList <GetTicketBanksForQueue> {
        val getTicketBanksData = mutableListOf<GetTicketBanksForQueue>()
        return DBConfig().connection().use { con ->
            con.prepareStatement(getAllTicketQueue).use {
                it.setInt(1, teamActive)
                it.setDate(2, Date.valueOf(from))
                it.setDate(3, Date.valueOf(to))
                it.executeQuery().use { dataResult ->
                    while (dataResult.next()){
                        val recordNum = dataResult.getInt("record_no")
                        val ticketNum = dataResult.getInt("ticket_no")
                        val ticketStatusId = dataResult.getInt("ticket_current_status")
                        val ticketStatus = dataResult.getString("ticket_status")
                        val dateCreated = dataResult.getString("date_inserted")
                        val reportedById = dataResult.getString("reported_by")
                        val requesterRemarks = dataResult.getString("requestor_remarks")
                        val reportedByFName = dataResult.getString("reported_by_firstname")
                        val reportedByLName = dataResult.getString("reported_by_lastname")
                        val reportedByName = "$reportedByLName, $reportedByFName"
                        val actualToSupport = dataResult.getString("actual_supported_employee")
                        val actualToSupportFName = dataResult.getString("recipient_firstname")
                        val actualToSupportLName = dataResult.getString("recipient_lastname")
                        val actualToSupportName = "$actualToSupportLName, $actualToSupportFName"
                        val actualToSupportPosition = dataResult.getString("recipient_position")
                        val actualToSupportEmail = dataResult.getString("recipient_email")
                        val companyAreaName = dataResult.getString("company_area_name")
                        val companyAreaShortName = dataResult.getString("company_area_short_name")
                        val companyFloorName = dataResult.getString("floor_name")
                        val companyBuildingArea = dataResult.getString("building_area")
                        val companyAreaId = dataResult.getInt("company_area_id")
                        val companyFloorId = dataResult.getInt("company_floor_id")
                        val subCategoryIdList = dataResult.getString("sub_category_id")
                        val subCategoryName = dataResult.getString("support_sub_cat_name")
                        val categoryName = dataResult.getString("support_category_name")
                        val reportedMethodId = dataResult.getInt("report_method_id")
                        val reportedMethod = dataResult.getString("reported_method")
                        val dateReported = dataResult.getString("date_reported")
                        val timeReported = dataResult.getString("time_reported")
                        val insertedBy = dataResult.getString("inserted_by")
                        val assignedSupportId = dataResult.getString("assigned_support_personnel")
                        val assignedSupportFName = dataResult.getString("support_by_firstname")
                        val assignedSupportLName = dataResult.getString("support_by_lastname")
                        var assignedSupportName: String ? = null
                        val assignedSupportEmail: String ? = dataResult?.getString("support_email")
                        if (assignedSupportFName != null && assignedSupportLName != null) assignedSupportName = "$assignedSupportLName, $assignedSupportFName"
                        val coSupportPersonnel = dataResult.getString("co_support_personnel")
                        val supportAssignedBy = dataResult.getString("support_assigned_by")
                        val dateSupportAssigned = dataResult.getString("date_support_assigned")
                        val lastUpdatedById = dataResult.getString("last_updated_by")
                        val lastUpdatedByFName = dataResult.getString("last_updated_by_firstname")
                        val lastUpdatedByLName = dataResult.getString("last_updated_by_lastname")
                        var lastUpdatedByName: String ? = null
                        if (lastUpdatedByFName != null && lastUpdatedByLName != null) lastUpdatedByName = "$lastUpdatedByLName, $lastUpdatedByFName"
                        val lastDateUpdated = dataResult.getString("last_date_updated")
                        val exclusiveToTeam = dataResult.getInt("exclusive_to_team_id")
                        val toClosed : String ? = if (ticketStatusId == 31) addingFiveDays(lastDateUpdated).toString()
                        else null
                        getTicketBanksData.add(GetTicketBanksForQueue(
                            recordNum,
                            toTicketGenerator(ticketNum.toString()),
                            ticketStatusId,
                            ticketStatus,
                            dateCreated,
                            requesterRemarks,
                            reportedById,
                            reportedByName,
                            actualToSupport,
                            actualToSupportName,
                            actualToSupportPosition,
                            actualToSupportEmail,
                            subCategoryIdList,
                            "$companyAreaName ($companyAreaShortName)",
                            companyAreaId,
                            "$companyFloorName, $companyBuildingArea",
                            companyFloorId,
                            categoryName,
                            subCategoryName,
                            ticketIssueCounter(concatFormatters(subCategoryIdList)),
                            reportedMethodId,
                            reportedMethod,
                            "$dateReported $timeReported",
                            insertedBy,
                            assignedSupportId,
                            assignedSupportName,
                            assignedSupportEmail,
                            coSupportPersonnel,
                            supportAssignedBy,
                            dateSupportAssigned,
                            lastUpdatedById,
                            lastUpdatedByName,
                            lastDateUpdated,
                            toClosed,
                            exclusiveToTeam,
                            null,
                            null
                        ))
                    }
                    getTicketBanksData
                }
            }
        }
    }
    fun getTicketQueueReports(teamActive:Int, from: String, to: String): MutableList <GetTicketBanksForQueueReports> {
        val getTicketBanksDataSet = mutableListOf<GetTicketBanksForQueueReports>()
        return DBConfig().connection().use { con ->
            con.prepareStatement(getAllTicketQueueReports).use {
                it.setInt(1, teamActive)
                it.setDate(2, Date.valueOf(from))
                it.setDate(3, Date.valueOf(to))
                it.executeQuery().use { dataResult ->
                    while (dataResult.next()){
                        val recordNum = dataResult.getInt("record_no")
                        val ticketNum = dataResult.getInt("ticket_no")
                        val ticketStatus = dataResult.getString("ticket_status")
                        val dateCreated = dataResult.getString("date_inserted")
                        val reportedByFName = dataResult.getString("reported_by_firstname")
                        val reportedByLName = dataResult.getString("reported_by_lastname")
                        val reportedByName = "$reportedByLName, $reportedByFName"
                        val actualToSupportFName = dataResult.getString("recipient_firstname")
                        val actualToSupportLName = dataResult.getString("recipient_lastname")
                        val actualToSupportName = "$actualToSupportLName, $actualToSupportFName"
                        val actualToSupportPosition = dataResult.getString("recipient_position")
                        val companyAreaName = dataResult.getString("company_area_name")
                        val companyAreaShortName = dataResult.getString("company_area_short_name")
                        val companyFloorName = dataResult.getString("floor_name")
                        val companyBuildingArea = dataResult.getString("building_area")
                        val subCategoryName = dataResult.getString("support_sub_cat_name")
                        val categoryName = dataResult.getString("support_category_name")
                        val reportedMethod = dataResult.getString("reported_method")
                        val dateReported = dataResult.getString("date_reported")
                        val timeReported = dataResult.getString("time_reported")
                        val assignedSupportFName = dataResult.getString("support_by_firstname")
                        val assignedSupportLName = dataResult.getString("support_by_lastname")
                        var assignedSupportName: String ? = null
                        if (assignedSupportFName != null && assignedSupportLName != null) assignedSupportName = "$assignedSupportLName, $assignedSupportFName"
                        val dateSupportAssigned = dataResult.getString("date_support_assigned")
                        val lastUpdatedByFName = dataResult.getString("last_updated_by_firstname")
                        val lastUpdatedByLName = dataResult.getString("last_updated_by_lastname")
                        var lastUpdatedByName: String ? = null
                        if (lastUpdatedByFName != null && lastUpdatedByLName != null) lastUpdatedByName = "$lastUpdatedByLName, $lastUpdatedByFName"
                        val lastDateUpdated = dataResult.getString("last_date_updated")
                        val exclusiveToTeam = dataResult.getInt("exclusive_to_team_id")
                        getTicketBanksDataSet.add(GetTicketBanksForQueueReports(
                            toTicketGenerator(ticketNum.toString()),
                            ticketStatus,
                            dateCreated,
                            reportedByName,
                            actualToSupportName,
                            actualToSupportPosition,
                            "$companyAreaName ($companyAreaShortName)",
                            "$companyFloorName, $companyBuildingArea",
                            categoryName,
                            subCategoryName,
                            reportedMethod,
                            "$dateReported $timeReported",
                            assignedSupportName,
                            dateSupportAssigned,
                            lastUpdatedByName,
                            lastDateUpdated,
                            getTicketResolved(recordNum),
                            addingFiveDays(lastDateUpdated),
                            exclusiveToTeam
                        ))
                    }
                    getTicketBanksDataSet
                }
            }
        }
    }
    private fun getTicketResolved(recordNum: Int): String ?{
        return DBConfig().connection().use { con ->
            con.prepareStatement(getTicketResolved).use {
                var dateResolved : String ?
                it.setInt(1, recordNum)
                it.executeQuery().use { resultData ->
                    dateResolved = if (resultData.next()) resultData.getString("date_inserted")
                    else null
                    dateResolved
                }
            }
        }
    }
    // ------------------------get filtered queuing tickets disregard duplicates ----------------------
    fun getFilteredTicketQueue(ticket: MutableList<GetTicketBanksForQueue>) : MutableList<GetTicketBanksForQueue>{
        val item = mutableListOf<GetTicketBanksForQueue>()
        for (index in ticket.indices){
            if (ticket.size == 1){
                item.add(
                    GetTicketBanksForQueue(
                        ticket[index].recordNum,
                        ticket[index].ticketNum,
                        ticket[index].ticketStatusId,
                        ticket[index].ticketStatus,
                        ticket[index].dateTimeTicketCreated,
                        ticket[index].requesterRemarks,
                        ticket[index].reportedById,
                        ticket[index].reportedByName,
                        ticket[index].actualToSupportId,
                        ticket[index].actualToSupportName,
                        ticket[index].actualToSupportPosition,
                        ticket[index].actualToSupportEmail,
                        ticket[index].subCategory.toString(),
                        ticket[index].companyArea,
                        ticket[index].companyAreaId,
                        ticket[index].companyFloorBanks,
                        ticket[index].companyFloorId,
                        ticket[index].categoryName,
                        ticket[index].subCategoryName,
                        ticket[index].subCategoryRestCount,
                        ticket[index].reportedMethodId,
                        ticket[index].reportedMethod,
                        ticket[index].dateTimeReported,
                        ticket[index].insertedBy,
                        ticket[index].assignedSupportId,
                        ticket[index].assignedSupportName,
                        ticket[index].assignedSupportEmail,
                        ticket[index].coSupportIdList,
                        ticket[index].supportAssignedBy,
                        ticket[index].dateSupportAssigned,
                        ticket[index].lastUpdatedById,
                        ticket[index].lastUpdatedByName,
                        ticket[index].dateTimeLastUpdated,
                        ticket[index].dateTimeToAutoClose,
                        ticket[index].exclusiveToTeam,
                        ticket[index].lockedTo,
                        ticket[index].expirationDate
                    )
                )
            } else {
                if (index == ticket.lastIndex-1){
                    if (ticket[index].recordNum != ticket[index  + 1].recordNum){
                        item.add(
                            GetTicketBanksForQueue(
                                ticket[index].recordNum,
                                ticket[index].ticketNum,
                                ticket[index].ticketStatusId,
                                ticket[index].ticketStatus,
                                ticket[index].dateTimeTicketCreated,
                                ticket[index].requesterRemarks,
                                ticket[index].reportedById,
                                ticket[index].reportedByName,
                                ticket[index].actualToSupportId,
                                ticket[index].actualToSupportName,
                                ticket[index].actualToSupportPosition,
                                ticket[index].actualToSupportEmail,
                                ticket[index].subCategory.toString(),
                                ticket[index].companyArea,
                                ticket[index].companyAreaId,
                                ticket[index].companyFloorBanks,
                                ticket[index].companyFloorId,
                                ticket[index].categoryName,
                                ticket[index].subCategoryName,
                                ticket[index].subCategoryRestCount,
                                ticket[index].reportedMethodId,
                                ticket[index].reportedMethod,
                                ticket[index].dateTimeReported,
                                ticket[index].insertedBy,
                                ticket[index].assignedSupportId,
                                ticket[index].assignedSupportName,
                                ticket[index].assignedSupportEmail,
                                ticket[index].coSupportIdList,
                                ticket[index].supportAssignedBy,
                                ticket[index].dateSupportAssigned,
                                ticket[index].lastUpdatedById,
                                ticket[index].lastUpdatedByName,
                                ticket[index].dateTimeLastUpdated,
                                ticket[index].dateTimeToAutoClose,
                                ticket[index].exclusiveToTeam,
                                ticket[index].lockedTo,
                                ticket[index].expirationDate
                            )
                        )
                        item.add(
                            GetTicketBanksForQueue(
                                ticket[ticket.size - 1].recordNum,
                                ticket[ticket.size - 1].ticketNum,
                                ticket[ticket.size - 1].ticketStatusId,
                                ticket[ticket.size - 1].ticketStatus,
                                ticket[ticket.size - 1].dateTimeTicketCreated,
                                ticket[ticket.size - 1].requesterRemarks,
                                ticket[ticket.size - 1].reportedById,
                                ticket[ticket.size - 1].reportedByName,
                                ticket[ticket.size - 1].actualToSupportId,
                                ticket[ticket.size - 1].actualToSupportName,
                                ticket[ticket.size - 1].actualToSupportPosition,
                                ticket[ticket.size - 1].actualToSupportEmail,
                                ticket[ticket.size - 1].subCategory.toString(),
                                ticket[ticket.size - 1].companyArea,
                                ticket[ticket.size - 1].companyAreaId,
                                ticket[ticket.size - 1].companyFloorBanks,
                                ticket[ticket.size - 1].companyFloorId,
                                ticket[ticket.size - 1].categoryName,
                                ticket[ticket.size - 1].subCategoryName,
                                ticket[ticket.size - 1].subCategoryRestCount,
                                ticket[ticket.size - 1].reportedMethodId,
                                ticket[ticket.size - 1].reportedMethod,
                                ticket[ticket.size - 1].dateTimeReported,
                                ticket[ticket.size - 1].insertedBy,
                                ticket[ticket.size - 1].assignedSupportId,
                                ticket[ticket.size - 1].assignedSupportName,
                                ticket[ticket.size - 1].assignedSupportEmail,
                                ticket[ticket.size - 1].coSupportIdList,
                                ticket[ticket.size - 1].supportAssignedBy,
                                ticket[ticket.size - 1].dateSupportAssigned,
                                ticket[ticket.size - 1].lastUpdatedById,
                                ticket[ticket.size - 1].lastUpdatedByName,
                                ticket[ticket.size - 1].dateTimeLastUpdated,
                                ticket[ticket.size - 1].dateTimeToAutoClose,
                                ticket[ticket.size - 1].exclusiveToTeam,
                                ticket[ticket.size - 1].lockedTo,
                                ticket[ticket.size - 1].expirationDate
                            )
                        )
                        break
                    } else {
                        item.add(
                            GetTicketBanksForQueue(
                                ticket[index + 1].recordNum,
                                ticket[index + 1].ticketNum,
                                ticket[index + 1].ticketStatusId,
                                ticket[index + 1].ticketStatus,
                                ticket[index + 1].dateTimeTicketCreated,
                                ticket[index + 1].requesterRemarks,
                                ticket[index + 1].reportedById,
                                ticket[index + 1].reportedByName,
                                ticket[index + 1].actualToSupportId,
                                ticket[index + 1].actualToSupportName,
                                ticket[index + 1].actualToSupportPosition,
                                ticket[index + 1].actualToSupportEmail,
                                ticket[index + 1].subCategory.toString(),
                                ticket[index + 1].companyArea,
                                ticket[index + 1].companyAreaId,
                                ticket[index + 1].companyFloorBanks,
                                ticket[index + 1].companyFloorId,
                                ticket[index + 1].categoryName,
                                ticket[index + 1].subCategoryName,
                                ticket[index + 1].subCategoryRestCount,
                                ticket[index + 1].reportedMethodId,
                                ticket[index + 1].reportedMethod,
                                ticket[index + 1].dateTimeReported,
                                ticket[index + 1].insertedBy,
                                ticket[index + 1].assignedSupportId,
                                ticket[index + 1].assignedSupportName,
                                ticket[index + 1].assignedSupportEmail,
                                ticket[index + 1].coSupportIdList,
                                ticket[index + 1].supportAssignedBy,
                                ticket[index + 1].dateSupportAssigned,
                                ticket[index + 1].lastUpdatedById,
                                ticket[index + 1].lastUpdatedByName,
                                ticket[index + 1].dateTimeLastUpdated,
                                ticket[index + 1].dateTimeToAutoClose,
                                ticket[index + 1].exclusiveToTeam,
                                ticket[index + 1].lockedTo,
                                ticket[index + 1].expirationDate
                            )
                        )
                        break
                    }
                } else if (ticket[index].recordNum != ticket[index + 1].recordNum){
                    item.add(
                        GetTicketBanksForQueue(
                            ticket[index].recordNum,
                            ticket[index].ticketNum,
                            ticket[index].ticketStatusId,
                            ticket[index].ticketStatus,
                            ticket[index].dateTimeTicketCreated,
                            ticket[index].requesterRemarks,
                            ticket[index].reportedById,
                            ticket[index].reportedByName,
                            ticket[index].actualToSupportId,
                            ticket[index].actualToSupportName,
                            ticket[index].actualToSupportPosition,
                            ticket[index].actualToSupportEmail,
                            ticket[index].subCategory.toString(),
                            ticket[index].companyArea,
                            ticket[index].companyAreaId,
                            ticket[index].companyFloorBanks,
                            ticket[index].companyFloorId,
                            ticket[index].categoryName,
                            ticket[index].subCategoryName,
                            ticket[index].subCategoryRestCount,
                            ticket[index].reportedMethodId,
                            ticket[index].reportedMethod,
                            ticket[index].dateTimeReported,
                            ticket[index].insertedBy,
                            ticket[index].assignedSupportId,
                            ticket[index].assignedSupportName,
                            ticket[index].assignedSupportEmail,
                            ticket[index].coSupportIdList,
                            ticket[index].supportAssignedBy,
                            ticket[index].dateSupportAssigned,
                            ticket[index].lastUpdatedById,
                            ticket[index].lastUpdatedByName,
                            ticket[index].dateTimeLastUpdated,
                            ticket[index].dateTimeToAutoClose,
                            ticket[index].exclusiveToTeam,
                            ticket[index].lockedTo,
                            ticket[index].expirationDate
                        )
                    )
                }
            }
        }
        return item
    }

    // -------------------------get team ticket counts---------------------------
    fun getTeamTicketCounts(exclusiveTo: Int) : Int {
        var counts = 0
        return DBConfig().connection().use { con ->
            con.prepareStatement(getAllTicketsQuery).use {
                it.setInt(1, exclusiveTo)
                it.executeQuery().use { dataResult ->
                    while (dataResult.next()) {
                        counts += 1
                    }
                    counts
                }
            }
        }
    }

    // ---------------------------get ticket Sub Category------------------------------
    fun getTicketSubCategory(subCategory: String) : MutableList<SubCategoryBanks>{
        val getSubAndCategorySpecificData = mutableListOf<SubCategoryBanks>()
        concatFormatters(subCategory).map {
            val subCategoryData = getSpecificSubCategory(it.toInt()).toTypedArray()
            getSubAndCategorySpecificData.add(SubCategoryBanks(subCategoryData[0].categoryName, subCategoryData[0].categoryFK, subCategoryData[0].subCategoryName, subCategoryData[0].subCategoryId, subCategoryData[0].teamActive))
        }
        return getSubAndCategorySpecificData
    }

    // ------------------------------get ticket logs---------------------------------
    fun getTicketLogs(recordNum: Int): MutableList<GetActualTicketBanks>{
        val actualTicketsData =  mutableListOf<GetActualTicketBanks>()
        DBConfig().connection().use { con ->
            con.prepareStatement(getTicketLogsQueries).use {
                it.setInt(1, recordNum)
                it.executeQuery().use { dataResult ->
                    while (dataResult.next()){
                        val referenceId = dataResult.getInt("reference_id")
                        val recordLastUpdateBy = dataResult.getString("record_last_updated_by")
                        val recordLastUpdateDateTime = dataResult.getString("date_last_updated")
                        val ticketCurrentStatusId = dataResult.getInt("ticket_current_status")
                        val ticketCurrentStatusName = dataResult.getString("status_name")
                        val remarksEntry = dataResult.getString("remark_entry")
                        val dateInserted = dataResult.getString("date_inserted")
                        val lockedToUser = dataResult.getString("locked_to")
                        val lockedToFirstName = dataResult.getString("firstname")
                        val lockedToLastNAme = dataResult.getString("lastname")
                        val lockedToName = "$lockedToLastNAme, $lockedToFirstName"
                        val lockedToExpireAt = dataResult.getString("lock_exprtn_timestamp")
                        val editable = dataResult.getBoolean("updatable_records")

                        actualTicketsData.add(GetActualTicketBanks(toTicketGenerator(referenceId.toString()), recordLastUpdateBy, recordLastUpdateDateTime, ticketCurrentStatusName, ticketCurrentStatusId, remarksReadable(remarksEntry), remarksEntry, dateInserted, lockedToUser, lockedToName, lockedToExpireAt, editable))
                    }
                    return actualTicketsData
                }
            }
        }
    }

    // ------------------------------get co support list --------------------
    fun getCoSupportList(coSupportList: String): MutableList<AssignedBanks>{
        val coSupport = coSupportIdLister(coSupportList)
        val coSupportListData = mutableListOf<AssignedBanks>()
        coSupport?.map { coSupportUserId ->
            DBConfig().connection().use { con ->
                con.prepareStatement(getCoAssignedSupport).use {
                    it.setString(1, coSupportUserId)
                    it.executeQuery().use { resultData ->
                        while (resultData.next()){
                            val teamID = resultData.getInt("team_temp_id")
                            val userId = resultData.getString("userid")
                            val firstName = resultData.getString("firstname")
                            val lastName = resultData.getString("lastname")
                            val supportName = "$lastName, $firstName"
                            val statusId = resultData.getInt("team_member_status")
                            val statusName = resultData.getString("status_name")

                            coSupportListData.add(AssignedBanks(teamID, userId, supportName, null, statusId, statusName))
                        }
                    }
                }
            }
        }
        return coSupportListData
    }

    // ------------------------get who editing in actual ticket-------------------
    fun getActualTicketEditing(referenceId: Int) : String ?{
        DBConfig().connection().use { con ->
            con.prepareStatement(getLockToActualTicket).use {
                it.setInt(1, referenceId)
                it.executeQuery().use { dataResult ->
                    if(dataResult.next()) {
                        return dataResult.getString("locked_to")
                    }
                return null
                }
            }
        }
    }

    // ------------------------get who editing in ticket records-------------------
    fun getTicketRecordEditing(referenceId: Int) : String ?{
        DBConfig().connection().use { con ->
            con.prepareStatement(getLockToTicketRecord).use {
                it.setInt(1, referenceId)
                it.executeQuery().use { dataResult ->
                    if(dataResult.next()) {
                        return dataResult.getString("locked_to")
                    }
                    return null
                }
            }
        }
    }

    // function for mailing when ticket resolved
    fun sendEmailResolved(isResolved : Int ?, ticketNumber: String, refNum: Int, ticketCreated: String, toSupportEMail: String, toSupportName: String, assignedSupport: String, description: String, logCreated: String, issue: MutableList<CategoryName>, teamActive: Int ?, isSystem: String ?) : Int{
        val regards = if (teamActive == 1){
            "Team IT Infrastructure"
        }else "Team IT Application Dev"
        val eMailBody : String
        val emailIdentifier : String
        when (isResolved){
            1 -> {
                emailIdentifier = "Ticket Resolved"
                eMailBody = "" +
                        "<strong>ACTION REQUIRED</strong>" +
                        "<br/>" +
                        "<br/>" +
                        "<br/>" +
                        "A Pleasant Day, Mr/Ms. ${toSupportName.split(",")[0]}!" +
                        "<br/>" +
                        "<br/>" +
                        "This is to inform you that the ticket <strong style = 'color: red;'>#$ticketNumber</strong> has been Resolved. <br />" +
                        "Please see details below." +
                        "<br/>" +
                        "<br/>" +
                        "<strong>Ticket Number:</strong> #$ticketNumber" +
                        "<br/>" +
                        "<strong>Requested by:</strong>  <strong style = 'color: red;'>$toSupportName</strong> " +
                        "<br/>" +
                        "<strong>Category:<br/></strong>" +
                        "<table style = 'border: 1px solid red'> " +
                        "<tr>" +
                        toReadableIssue(issue).replace('[', ' ').replace(']', ' ').replace(',', '|') +
                        "</tr>" +
                        "</table>" +
                        "<br/>" +
                        "<strong>Description:</strong> <strong style = 'color: red;'>$description</strong> " +
                        "<br/>" +
                        "<strong>Date Requested:</strong> <strong style = 'color: red;'>${timeDateReadable(ticketCreated)}</strong>" +
                        "<br/>" +
                        "---------------------------------------------------------------------------------------------" +
                        "<br/>" +
                        "<strong>Resolved by:</strong> <strong style = 'color: red;'>$assignedSupport</strong>" +
                        "<br/>" +
                        "<strong>Action Taken:</strong> <strong style = 'color: red;'>$logCreated</strong> " +
                        "<br/>" +
                        "<strong>Date Resolved:</strong><strong style = 'color: red;'>${timeDateReadable(timeDate())}</strong>" +
                        "<br />" +
                        "<br />" +
                        "If you accept that your request was successfully fulfilled, please close this ticket through this link: <a href=\"http://192.168.5.127:3000/Helpdesk/TicketResolved/ticketId=${manualEnc(refNum,ticketCreated)}&toClose=${paramEnc("True")}\">Close Ticket</a>" +
                        "<br />" +
                        "<br />" +
                        "Otherwise, you may return this request by clicking this link: <a href=\"http://192.168.5.127:3000/Helpdesk/TicketResolved/ticketId=${manualEnc(refNum,ticketCreated)}&toClose=${paramEnc("False")}\">Reopen Ticket</a>" +
                        "<br />" +
                        "<br />" +
                        "<br />" +
                        "*** If you do not respond after five (5) working days, we'll conclude that your request has been resolved, and this request will be automatically closed. ***\n" +
                        "<br />" +
                        "<br />" +
                        "For inquiries, please send an email to ict@umtc.com.ph or call us at local 2210, 2216, or 2217." +
                        "<br />" +
                        "<br />" +
                        "<br />" +
                        "Best regards," +
                        "<br />" +
                        regards +
                        "<br />" +
                        "<br />" +
                        "<br />" +
                        "<br />" +
                        "<br />" +
                        "<i>This is a system generated message. Please DO NOT REPLY to this email.</i>"
                }
            2 -> {
                emailIdentifier = "Ticket Closed"
                eMailBody = "" +
                        "<strong>TICKET UPDATED</strong>" +
                        "<br/>" +
                        "<br/>" +
                        "<br/>" +
                        "A Pleasant Day, Mr/Ms. ${assignedSupport.split(",")[0]}!" +
                        "<br/>" +
                        "<br/>" +
                        "This is to inform you that the ticket <strong style = 'color: red;'>#${toTicketGenerator(ticketNumber)}</strong> has been mark as Closed. <br />" +
                        "Please see details below." +
                        "<br/>" +
                        "<br/>" +
                        "<strong>Ticket Number:</strong> #${toTicketGenerator(ticketNumber)} " +
                        "<br/>" +
                        "<strong>Requested by:</strong>  <strong style = 'color: red;'>$toSupportName</strong> " +
                        "<br/>" +
                        "<strong>Category:<br/></strong>" +
                        "<table style = 'border: 1px solid red'> " +
                        "<tr>" +
                        toReadableIssue(issue).replace('[', ' ').replace(']', ' ').replace(',', '|') +
                        "</tr>" +
                        "</table>" +
                        "<br/>" +
                        "<strong>Description:</strong> <strong style = 'color: red;'>$description</strong> " +
                        "<br/>" +
                        "<strong>Date Requested:</strong> <strong style = 'color: red;'>${timeDateReadable(ticketCreated)}</strong>" +
                        "<br/>" +
                        "---------------------------------------------------------------------------------------------" +
                        "<br/>" +
                        "<strong>Closed by:</strong> <strong style = 'color: red;'>${isSystem}</strong>" +
                        "<br/>" +
                        "<strong>Remarks: </strong> <strong style = 'color: red;'>$logCreated</strong> " +
                        "<br/>" +
                        "<strong>Date Closed:</strong><strong style = 'color: red;'>${timeDateReadable(timeDate())}</strong>" +
                        "<br />" +
                        "<br />" +
                        "For inquiries, please send an email to ict@umtc.com.ph or call us at local 2210, 2216, or 2217." +
                        "<br />" +
                        "<br />" +
                        "<br />" +
                        "<br />" +
                        "<br />" +
                        "<i>This is a system generated message. Please DO NOT REPLY to this email.</i>"
                }
            3 -> {
                emailIdentifier = "Ticket Re-Ongoing"
                eMailBody = "" +
                        "<strong>TICKET RETURNED</strong>" +
                        "<br/>" +
                        "<br/>" +
                        "<br/>" +
                        "A Pleasant Day, Mr/Ms. ${assignedSupport.split(",")[0]}!" +
                        "<br/>" +
                        "<br/>" +
                        "This is to inform you that the ticket <strong style = 'color: red;'>#$${toTicketGenerator(ticketNumber)}</strong> was returned by the recipient <br />" +
                        "Please see details below." +
                        "<br/>" +
                        "<br/>" +
                        "<strong>Ticket Number: #$${toTicketGenerator(ticketNumber)} " +
                        "<br/>" +
                        "<strong>Requested by: </strong>  <strong style = 'color: red;'>$toSupportName</strong> " +
                        "<br/>" +
                        "<strong>Category:<br/></strong>" +
                        "<table style = 'border: 1px solid red'> " +
                        "<tr>" +
                        toReadableIssue(issue).replace('[', ' ').replace(']', ' ').replace(',', '|') +
                        "</tr>" +
                        "</table>" +
                        "<br/>" +
                        "<strong>Description: </strong> <strong style = 'color: red;'>$description</strong> " +
                        "<br/>" +
                        "<strong>Date Requested: </strong> <strong style = 'color: red;'>${timeDateReadable(ticketCreated)}</strong>" +
                        "<br/>" +
                        "---------------------------------------------------------------------------------------------" +
                        "<br/>" +
                        "<strong>Re-Ongoing by: </strong> <strong style = 'color: red;'>${toSupportName}</strong>" +
                        "<br/>" +
                        "<strong>Remarks: </strong> <strong style = 'color: red;'>$logCreated</strong> " +
                        "<br/>" +
                        "<strong>Date Returned: </strong><strong style = 'color: red;'>${timeDateReadable(timeDate())}</strong>" +
                        "<br />" +
                        "<br />" +
                        "For inquiries, please send an email to ict@umtc.com.ph or call us at local 2210, 2216, or 2217." +
                        "<br />" +
                        "<br />" +
                        "<br />" +
                        "<br />" +
                        "<br />" +
                        "<i>This is a system generated message. Please DO NOT REPLY to this email.</i>"
            }
            else -> {
                emailIdentifier = "Ticket Made Action"
                eMailBody = "" +
                        "<strong>TICKET NOTIFY</strong>" +
                        "<br/>" +
                        "<br/>" +
                        "<br/>" +
                        "A Pleasant Day, Mr/Ms. ${toSupportName.split(",")[0]}!" +
                        "<br/>" +
                        "<br/>" +
                        "This is to inform you that the ticket <strong style = 'color: red;'>#$${toTicketGenerator(ticketNumber)}</strong> had Changes. <br />" +
                        "Please see details below." +
                        "<br/>" +
                        "<br/>" +
                        "<strong>Ticket Number: #$${toTicketGenerator(ticketNumber)} " +
                        "<br/>" +
                        "<strong>Requested by:</strong> <strong style = 'color: red;'>$toSupportName</strong> " +
                        "<br/>" +
                        "<strong>Category:<br/></strong>" +
                        "<table style = 'border: 1px solid red'> " +
                        "<tr>" +
                        toReadableIssue(issue).replace('[', ' ').replace(']', ' ').replace(',', '|') +
                        "</tr>" +
                        "</table>" +
                        "<br/>" +
                        "<strong>Description:</strong> <strong style = 'color: red;'>$description</strong> " +
                        "<br/>" +
                        "<strong>Date Requested:</strong> <strong style = 'color: red;'>${timeDateReadable(ticketCreated)}</strong>" +
                        "<br/>" +
                        "---------------------------------------------------------------------------------------------" +
                        "<br/>" +
                        "<strong>Action Taken:</strong> <strong style = 'color: red;'>$logCreated</strong> " +
                        "<br/>" +
                        "<strong>Date:</strong> <strong style = 'color: red;'>${timeDateReadable(timeDate())}</strong>" +
                        "<br />" +
                        "<br />" +
                        "<br />" +
                        "For inquiries, please send an email to ict@umtc.com.ph or call us at local 2210, 2216, or 2217." +
                        "<br />" +
                        "<br />" +
                        "<br />" +
                        "Best regards," +
                        "<br />" +
                        regards +
                        "<br />" +
                        "<br />" +
                        "<br />" +
                        "<br />" +
                        "<br />" +
                        "<i>This is a system generated message. Please DO NOT REPLY to this email.</i>"
                }
            }
        return DBConfig().connection().use { con ->
            con.prepareStatement(insertResolveEmail).use {
                it.setString(1, emailIdentifier)
                it.setString(2, eMailBody)
                it.setString(3, toSupportEMail)
                it.setInt(4, refNum)

                it.executeUpdate()
            }
        }
    }

    // getting ticket details for closing
    fun getTicketClose(): MutableList <GetTicketDetailsClosed> {
        val getTicketBanksData = mutableListOf<GetTicketDetailsClosed>()
        return DBConfig().connection().use { con ->
            con.prepareStatement(getTicketsClose).use {
                it.executeQuery().use { dataResult ->
                    while (dataResult.next()){
                        val recordNum = dataResult.getInt("record_no")
                        val ticketNum = dataResult.getInt("ticket_no")
                        val ticketStatusId = dataResult.getInt("ticket_current_status")
                        val ticketStatus = dataResult.getString("ticket_status")
                        val requesterRemarks = dataResult.getString("requestor_remarks")
                        val reportedByFName = dataResult.getString("reported_by_firstname")
                        val reportedByLName = dataResult.getString("reported_by_lastname")
                        val reportedByName = "$reportedByLName, $reportedByFName"
                        val actualToSupportFName = dataResult.getString("recipient_firstname")
                        val actualToSupportLName = dataResult.getString("recipient_lastname")
                        val actualToSupportName = "$actualToSupportLName, $actualToSupportFName"
                        val actualToSupportUserId = dataResult.getString("actual_supported_employee")
                        val subCategoryIdList = dataResult.getString("sub_category_id")
                        val companyAreaName = dataResult.getString("company_area_name")
                        val companyAreaShortName = dataResult.getString("company_area_short_name")
                        val companyFloorName = dataResult.getString("floor_name")
                        val companyBuildingArea = dataResult.getString("building_area")
                        val reportedMethod = dataResult.getString("reported_method")
                        val dateReported = dataResult.getString("date_reported")
                        val timeReported = dataResult.getString("time_reported")
                        val ticketCreated = dataResult.getString("date_inserted")
                        val assignedSupportFName = dataResult.getString("support_by_firstname")
                        val assignedSupportLName = dataResult.getString("support_by_lastname")
                        val assignedSupportId = dataResult?.getString("assigned_support_personnel")
                        var assignedSupportName: String ? = null
                        val assignedSupportEmail: String ? = dataResult?.getString("support_email")
                        val coSupportPersonnel = dataResult?.getString("co_support_personnel")
                        if (assignedSupportFName != null && assignedSupportLName != null) assignedSupportName = "$assignedSupportLName, $assignedSupportFName"
                        val lastDateUpdated = dataResult?.getString("last_date_updated")
                        getTicketBanksData.add(GetTicketDetailsClosed(
                            recordNum,
                            toTicketGenerator(ticketNum.toString()),
                            ticketStatusId,
                            ticketStatus,
                            subCategoryIdList,
                            coSupportPersonnel.toString(),
                            lastDateUpdated.toString(),
                            "$companyAreaName, ($companyAreaShortName), $companyFloorName - $companyBuildingArea",
                            requesterRemarks,
                            reportedByName,
                            actualToSupportName,
                            actualToSupportUserId,
                            dateReported,
                            timeReported,
                            ticketCreated,
                            reportedMethod,
                            assignedSupportName.toString(),
                            assignedSupportId.toString(),
                            assignedSupportEmail.toString()
                        ))
                    }
                    getTicketBanksData
                }
            }
        }
    }

    // user validation
    fun accessValidation(authorizationVal : String, accessToken: String, userId: String) : Int{
        val keys = getSecretIVAndSecretKey()
        val iV = keys[0].keyValue.toByteArray()
        val key = keys[1].keyValue.toByteArray()
        val encryptedToken = decryptAes(accessToken, key, iV).split(".")[0]
        if (authorizationVal.isEmpty() || !authorizationVal.startsWith("Bearer ") || (authorizationVal.split(" ").count() != 2)) return 0
        val systemKeyResult = checkSystemKey().find { it.systemKey == authorizationVal.split(" ")[1] }
        if ((systemKeyResult == null) || (systemKeyResult.active != 1)) return 1
        val systemKeyExpiration: Timestamp = Timestamp.valueOf(systemKeyResult.expirationDate)
        if (Timestamp.valueOf(LocalDateTime.now()) >= systemKeyExpiration) return 2
        var compareCount = 0
        getUserAccessToken(userId).map {
            if (it.toString() == encryptedToken) compareCount += 1
        }
        if (compareCount != 0) return 3

        return 4
    }

    // get employee id for validation on closing ticket
    fun getEmployeeID(userID: String) :Int{
        var userId = ""
        return DBConfig().connection().use { con ->
            con.prepareStatement(getEmployeeIdValidate).use {
                it.setString(1, userID)
                it.executeQuery().use { dataResult ->
                    if (dataResult.next()) userId = dataResult.getString("employeeid")
                    userId.toInt()
                }
            }
        }
    }
    // -------------------------updating tickets starts here------------------------
    fun updateTicketRecord(reportedById : String, actualToSupportId: String, reportedMethodId: Int, subCategory: MutableList<String>, requesterRemarks: String, companyAreaId: Int, assignedPersonnelId: String?, recordNum: Int, dateReported: String, timeReported: String, lastUpdatedBy: String, ticketStatus: Int, supportAssignedBy: String ?, exclusiveTo: Int) : Int{
        val dateBanks = dateSeparator(dateTimeRefactor(dateReported))
        val timeBanks = timeSeparator(timeReported)
        val date = Date.valueOf(LocalDate.of(dateBanks[0].year,dateBanks[0].month,dateBanks[0].day))
        val time = Time.valueOf(LocalTime.of(timeBanks[0].hours,timeBanks[0].minutes,timeBanks[0].seconds))
        return DBConfig().connection().use { con ->
            con.prepareStatement(updateTicket).use {
                it.setString(1, reportedById)
                it.setString(2, actualToSupportId)
                it.setInt(3, reportedMethodId)
                it.setString(4, toConcatFormatter(subCategory))
                it.setString(5, requesterRemarks)
                it.setInt(6, companyAreaId)
                it.setString(7, assignedPersonnelId)
                it.setDate(8, date)
                it.setTime(9, time)
                it.setString(10, lastUpdatedBy)
                it.setTimestamp(11, Timestamp.valueOf(timeDate()))
                it.setInt(12, ticketStatus)
                it.setString(13, supportAssignedBy)
                it.setInt(14, exclusiveTo)
                it.setInt(15, recordNum)
                it.executeUpdate()
            }
        }
    }
    fun updateAssignTickets(setAssignPersonnelId: String, lastUpdatedBy: String, recordNum: Int) : Int{
        return DBConfig().connection().use { con ->
            con.prepareStatement(updateTicketAssign).use {
                it.setString(1, setAssignPersonnelId)
                it.setString(2, lastUpdatedBy)
                it.setString(3, lastUpdatedBy)
                it.setTimestamp(4, Timestamp.valueOf(timeDate()))
                it.setTimestamp(5, Timestamp.valueOf(timeDate()))
                it.setInt(6, recordNum)

                it.executeUpdate()
            }
        }
    }
    fun updateResolveTicket(lastUpdatedBy: String, recordNum: Int) : Int {
        return DBConfig().connection().use { con ->
            con.prepareStatement(updateTicketResolve).use {
                it.setString(1, lastUpdatedBy)
                it.setTimestamp(2, Timestamp.valueOf(timeDate()))
                it.setInt(3, recordNum)

                it.executeUpdate()
            }
        }
    }
    fun updateCloseTicket(recordNum: Int, lastUpdatedBy: String) : Int {
        return DBConfig().connection().use { con ->
            con.prepareStatement(updateTicketClose).use {
                it.setString(1, lastUpdatedBy)
                it.setTimestamp(2, Timestamp.valueOf(timeDate()))
                it.setInt(3, recordNum)

                it.executeUpdate()
            }
        }
    }
    fun updateReOngoingTicket(recordNum: Int, lastUpdatedBy: String) : Int {
        return DBConfig().connection().use { con ->
            con.prepareStatement(updateTicketReOngoing).use {
                it.setString(1, lastUpdatedBy)
                it.setTimestamp(2, Timestamp.valueOf(timeDate()))
                it.setInt(3, recordNum)

                it.executeUpdate()
            }
        }
    }
    fun updateCoSupportTickets(lastUpdatedBy: String, recordNum: Int) : Int{
        return DBConfig().connection().use { con ->
            con.prepareStatement(updateTicketCoSupport).use {
                it.setString(1, coSupportChecking(recordNum))
                it.setString(2, lastUpdatedBy)
                it.setString(3, lastUpdatedBy)
                it.setTimestamp(4, Timestamp.valueOf(timeDate()))
                it.setInt(5, recordNum)

                it.executeUpdate()
            }
        }
    }
    fun updateSupportEngage(userId: String) : Int{
        return DBConfig().connection().use { con ->
            con.prepareStatement(updateSupportStatusEngage).use {
                it.setString(1, userId)
                it.executeUpdate()
            }
        }
    }
    fun updateSupportAvailable(userId: String) : Int{
        return DBConfig().connection().use { con ->
            con.prepareStatement(updateSupportStatusAvailable).use {
                it.setString(1, userId)
                it.executeUpdate()
            }
        }
    }
    fun updateActualTicketEditing(userId: String ?, referenceId: Int): Int{
        val timeBanks : Timestamp ? = if (userId != null) Timestamp.valueOf(LocalDateTime.of(LocalDate.now(),LocalTime.of(timeSeparator(addingTenMin(Time.valueOf(LocalTime.now()).toString()).toString())[0].hours,timeSeparator(addingTenMin(Time.valueOf(LocalTime.now()).toString()).toString())[0].minutes, timeSeparator(addingTenMin(Time.valueOf(LocalTime.now()).toString()).toString())[0].seconds)))
        else null
        return DBConfig().connection().use { con ->
            con.prepareStatement(updateActualTicketEditing).use {
                it.setString (1, userId)
                it.setTimestamp(2, timeBanks)
                it.setInt(3, referenceId)
                it.executeUpdate()
            }
        }
    }
    fun updateTicketRecordEditing(userId: String ?, referenceId: Int): Int{
        val timeBanks : Timestamp ? = if (userId != null) Timestamp.valueOf(LocalDateTime.of(LocalDate.now(),LocalTime.of(timeSeparator(addingTenMin(Time.valueOf(LocalTime.now()).toString()).toString())[0].hours,timeSeparator(addingTenMin(Time.valueOf(LocalTime.now()).toString()).toString())[0].minutes, timeSeparator(addingTenMin(Time.valueOf(LocalTime.now()).toString()).toString())[0].seconds)))
        else null
        return DBConfig().connection().use { con ->
            con.prepareStatement(updateTicketRecordEditing).use {
                it.setString (1, userId)
                it.setTimestamp(2, timeBanks)
                it.setInt(3, referenceId)
                it.executeUpdate()
            }
        }
    }
    fun updateActualTicket(ticketStatus: Int, remarks: String, recordInsertedBy: String, recordNum: Int): Int{
        return DBConfig().connection().use { con ->
            con.prepareStatement(updateActualTicket).use {
                it.setString(1, remarks)
                it.setInt(2, ticketStatus)
                it.setString(3, recordInsertedBy)
                it.setTimestamp(4, Timestamp.valueOf(timeDate()))
                it.setInt(5, recordNum)
                it.executeUpdate()
            }
        }
    }

    // -------------------looking for assigned support who still on going tickets--------------
    fun findAvailabilitySupport(userId : String) : Int{
        DBConfig().connection().use { con ->
            con.prepareStatement(getTicketForSpecificSupport).use {
                it.setString(1, userId)
                it.executeQuery().use { dataResult ->
                    while (dataResult.next()){
                        if (dataResult.getInt("ticket_current_status") == 30){
                            return 1
                        }
                    }
                }
            }
        }
        return 0
    }

    // ---------get all ticket components starts here for creating tickets------------
    fun getRequester() : MutableList<RequesterBanks>{
        val requesterData = mutableListOf<RequesterBanks>()
        DBConfig().connection().use { con ->
            con.prepareStatement(getRequesterQueries).use {
                it.executeQuery().use { dataResult ->
                    while (dataResult.next()){
                        val firstName = dataResult.getString("firstname")
                        val lastName = dataResult.getString("lastname")
                        val userFullName = "$lastName, $firstName"
                        val userId = dataResult.getString("userid")

                        requesterData.add(RequesterBanks(userId, userFullName))
                    }
                    return requesterData
                }
            }
        }
    }
    fun getRequestMethod() : MutableList<RequestMethodBanks>{
        val requestMethodBanks = mutableListOf<RequestMethodBanks>()
        DBConfig().connection().use { con ->
            con.prepareStatement(getRequestMethod).use {
                it.executeQuery().use { dataResult ->
                    while (dataResult.next()){
                        val requestMethodName = dataResult.getString("status_name")
                        val requestMethodId = dataResult.getInt("status_id")

                        requestMethodBanks.add(RequestMethodBanks(requestMethodName, requestMethodId))
                    }
                    return requestMethodBanks
                }
            }
        }
    }
    fun getCategory() : MutableList<CategoryBanks>{
        val categoryData = mutableListOf<CategoryBanks>()
        DBConfig().connection().use { con ->
            con.prepareStatement(getCategoryQueries).use {
                it.executeQuery().use { dataResult ->
                    while (dataResult.next()){
                        val categoryName = dataResult.getString("support_category_name")
                        val categoryId = dataResult.getInt("support_cat_id")
                        val teamActiveId = dataResult.getString("team_active_to")

                        categoryData.add(CategoryBanks(categoryName, categoryId, teamActiveId))
                    }
                    return categoryData
                }
            }
        }
    }
    fun getSubCategory(categoryFK: Int) : MutableList<SubCategoryBanks>{
        val subCategoryData = mutableListOf<SubCategoryBanks>()
        DBConfig().connection().use { con ->
            con.prepareStatement(getSubCategoryQueries).use {
                it.setInt(1, categoryFK)
                it.executeQuery().use { dataResult ->
                    while (dataResult.next()){
                        val categoryName = dataResult.getString("support_category_name")
                        val categoryFk = dataResult.getInt("support_cat_id")
                        val subCategoryName = dataResult.getString("support_sub_cat_name")
                        val subCategoryId = dataResult.getInt("support_sub_cat_id")
                        val teamActive = dataResult.getString("team_active_to")
                        subCategoryData.add(SubCategoryBanks(categoryName, categoryFk, subCategoryName, subCategoryId, concatFormatter(teamActive).toInt()))
                    }
                    return subCategoryData
                }
            }
        }
    }
    fun getCompanyArea() : MutableList<AreaBanks>{
        val companyAreaData = mutableListOf<AreaBanks>()
        DBConfig().connection().use { con ->
            con.prepareStatement(getAreaQueries).use{
                it.executeQuery().use { dataResult ->
                    while (dataResult.next()){
                        val floorId = dataResult.getInt("company_floor_id")
                        val areaName = dataResult.getString("company_area_name")
                        val areaId = dataResult.getInt("company_area_id")

                        companyAreaData.add(AreaBanks(floorId, areaName, areaId))
                    }
                    return companyAreaData
                }
            }
        }
    }
    fun getCompanyFloor(areaFK: Int) : MutableList<FloorBanks>{
        val companyFloorData = mutableListOf<FloorBanks>()
        DBConfig().connection().use { con ->
            con.prepareStatement(getFloorQueries).use {
                it.setInt(1, areaFK)
                it.executeQuery().use { dataResult ->
                    while (dataResult.next()){
                        val floorId = dataResult.getInt("company_floor_id")
                        val floorName = dataResult.getString("floor_name")
                        val buildingAreaName = dataResult.getString("building_area")

                        companyFloorData.add(FloorBanks(floorId, floorName, buildingAreaName))
                    }
                    return companyFloorData
                }
            }
        }
    }
    fun getAssignedSupport(teamAssigned: Int) : MutableList<AssignedBanks>{
        val assignedSupportData = mutableListOf<AssignedBanks>()
        DBConfig().connection().use { conn ->
            conn.prepareStatement(getAssignedSupport).use {
                it.setInt(1, teamAssigned)
                it.executeQuery().use { dataResult ->
                    while (dataResult.next()){
                        val teamID = dataResult.getInt("team_temp_id")
                        val userId = dataResult.getString("userid")
                        val firstName = dataResult.getString("firstname")
                        val lastName = dataResult.getString("lastname")
                        val employeeId = dataResult.getString("employeeId")
                        val supportName = "$lastName, $firstName"
                        val statusId = dataResult.getInt("team_member_status")
                        val statusName = dataResult.getString("status_name")

                        assignedSupportData.add(AssignedBanks(teamID, userId, supportName, employeeId, statusId, statusName))
                    }
                }
                return assignedSupportData
            }
        }
    }
    // ------------------get all ticket components end here---------------------

    // assigning validations
    fun assignValidation(remarks: String ?, assignedSupport: String ?, assignedPersonnelId: String?, lastUpdatedBy: String): Int{
        return if (assignedPersonnelId == lastUpdatedBy && assignedSupport == null) 1
        else if (assignedPersonnelId != lastUpdatedBy && assignedSupport == null) 2
        else if (remarks == null || remarks == "") 3
        else 4
    }

    // action taken validation
    fun actionTakenValidation(isResolved: Int?, coSupportList: MutableList<String?>?, toSendEmail: Boolean): Double{
        return if (isResolved == 1 && coSupportList!!.isNotEmpty()) 1.1
        else if (isResolved == 1 && coSupportList == null) 2.1
        else if (coSupportList!!.isEmpty() && toSendEmail) 3.1
        else 4.1
    }

    // ------------------ get ticket status and details----------------
    fun getTicketStatus() : MutableList<TicketStatus>{
        val ticketStatusData = mutableListOf<TicketStatus>()
        return  DBConfig().connection().use { con ->
            con.prepareStatement(getTicketStatusQueries).use {
                it.executeQuery().use { dataResult ->
                    while (dataResult.next()){
                        val ticketStatusId = dataResult.getInt("status_id")
                        val ticketStatusName = dataResult.getString("status_name")
                        when (ticketStatusId){
                            29 -> ticketStatusData.add(TicketStatus(ticketStatusId,ticketStatusName, "#f54885", "#fdd8e5"))
                            30 -> ticketStatusData.add(TicketStatus(ticketStatusId,ticketStatusName, "#e48800", "#ffe1b1"))
                            31 -> ticketStatusData.add(TicketStatus(ticketStatusId,ticketStatusName, "#2eaf67", "#dafbe8"))
                            else -> ticketStatusData.add(TicketStatus(ticketStatusId,ticketStatusName, "#00afe4", "#b1f5ff"))
                        }
                    }
                    ticketStatusData
                }
            }
        }
    }

    // ------------------ get all ticket sub and category ----------------
    fun getTicketSubAndCategory() : MutableList<GetSubAndCategory>{
        val getSubAndCategoryData = mutableListOf<GetSubAndCategory>()
        getCategory().map { itCategory ->
            val subCategoryResult = getSubCategory(itCategory.categoryId)
            getSubAndCategoryData.add(GetSubAndCategory(itCategory.categoryName, subCategoryResult))
        }
        return getSubAndCategoryData
    }

    // get system modules
    fun getSystemModule(employee: Boolean, partner: Boolean, crew: Boolean): MutableList<SystemModule> {
        val systemModuleData = mutableListOf<SystemModule>()
        DBConfig().connection().use { con ->
            con.prepareStatement(getSystemModule).use {
                it.setBoolean(1, employee)
                it.setBoolean(2, partner)
                it.setBoolean(3, crew)

                it.executeQuery().use { dataResult ->
                    while (dataResult.next()){
                        val moduleId = dataResult.getInt("module_id")
                        val moduleName = dataResult.getString("module_name")
                        systemModuleData.add(SystemModule(moduleId, moduleName))
                    }
                    return systemModuleData
                }
            }

        }
    }

    // manual encryption
    fun manualEnc(recordNum: Int, dateCreated: String) :String{
        val dateTime = dateTimeSeparator(dateCreated)
        val date = dateSeparator(dateTime[0].date)
        val time = timeSeparator(dateTime[0].time)
        val fromDateValue = LocalDate.of(1970, 1, 1)
        val dateValue = LocalDate.of(date[0].year,date[0].month,date[0].day)
        val timeValue = LocalTime.of(time[0].hours, time[0].minutes, time[0].seconds)
        val year = dateValue.year - fromDateValue.year
        val month =  dateValue.dayOfMonth - fromDateValue.dayOfMonth
        val day = dateValue.dayOfMonth - fromDateValue.dayOfMonth
        return "$recordNum.${(year * 365)*86400 + (month * 31)*86400 + day * 86400 + timeValue.hour * 3600 + timeValue.minute * 60 + timeValue.second}"
    }
    fun paramEnc(condition: String) :String{
        var encrypted = ""
        if (condition.length > 6){
            condition.split("s").map {
                encrypted += when(it){
                    "Ffk53224!cx" -> 'f'
                    "H23Sww@a24" -> 't'
                    "SD44133" -> 'a'
                    "Ay57S!" -> 'l'
                    "qw523c" -> 'r'
                    "dDay47" -> 'u'
                    "dHh413@" -> 's'
                    "yY861aA" -> 'e'
                    else -> "231312"
                }
            }
        }else{
            condition.map {
                encrypted += when(it){
                    'F','f' -> "Ffk53224!cxs"
                    'T', 't' -> "H23Sww@a24s"
                    'A', 'a' -> "SD44133s"
                    'L', 'l' -> "Ay57S!s"
                    'R', 'r' -> "qw523cs"
                    'U', 'u' -> "dDay47s"
                    'S', 's' -> "dHh413@s"
                    'E', 'e' -> "yY861aA"
                    else -> "642345"
                }
            }
        }
        return encrypted
    }

    // ------------------all function that used in this file was here----------
    private fun getSpecificSubCategory(subCategoryId: Int) : MutableList<SubCategoryBanks>{
        val subCategoryData = mutableListOf<SubCategoryBanks>()
        DBConfig().connection().use { con ->
            con.prepareStatement(getSpecificSubCategoryQueries).use {
                it.setInt(1, subCategoryId)
                it.executeQuery().use { dataResult ->
                    while (dataResult.next()){
                        val categoryName = dataResult.getString("support_category_name")
                        val categoryFk = dataResult.getInt("support_cat_id")
                        val subCategoryName = dataResult.getString("support_sub_cat_name")
                        val subCategoryID= dataResult.getInt("support_sub_cat_id")
                        val teamActive = dataResult.getString("team_active_to")
                        subCategoryData.add(SubCategoryBanks(categoryName, categoryFk, subCategoryName, subCategoryID, concatFormatter(teamActive).toInt()))
                    }
                    return subCategoryData
                }
            }
        }
    }
    private fun ticketIssueCounter(subCategoryIdList: Array<String>) : Int{
        var issueCounter = 0

        for (char in subCategoryIdList.indices){
            issueCounter += 1
        }
        return issueCounter
    }
    private fun coSupportIdLister(coSupportIdList: String ?) : Array<String> ?{
        if (coSupportIdList.isNullOrEmpty()) return null

        val newCoSupportList = coSupportIdList.dropLast(1)
        return newCoSupportList.split(";").toTypedArray()
    }
    private fun remarksReadable(remarks: String): Array<String ?>{
        val remarksResult = mutableListOf<String ?>()
        //print("------------------------------${concatFormatDash(remarks).size}")
        if (concatFormatDash(remarks).size > 1){
            concatFormatDash(remarks).forEachIndexed{ arrayIndex, it ->
                var remarksReadable: String ? = ""
                val remark = concatFormat(it)
                when (arrayIndex) {
                    concatFormatDash(remarks).size - 1 -> {
                        remarksReadable = it
                    }
                    else -> {
                        when (remark.size){
                            1 -> remarksReadable = it
                            else -> {
                                for (index in remark.indices){
                                    remarksReadable += if (index == 0 || index == 2){
                                        getRequester().find { resultData -> resultData.requesterId == remark[index] }!!.requesterName
                                    } else {
                                        remark[index]
                                    }
                                }
                            }
                        }
                    }
                }
                remarksResult.add(remarksReadable.toString())
            }
        } else {
            var remarksReadable: String ? = ""
            val remark = concatFormat(remarks)
            if (remark.size > 1){
                for (index in remark.indices){
                    remarksReadable += if (index == 0 || index == 2){
                        getRequester().find { resultData -> resultData.requesterId == remark[index] }!!.requesterName
                    } else {
                        remark[index]
                    }
                }
            } else  {
                remarksReadable = remarks
            }
            remarksResult.add(remarksReadable.toString())
        }
        return remarksResult.toTypedArray()
    }

    // ---------------------------concat Formatter------------------------------
    private fun concatFormatter (concatFormat: String): String{
        var concatResult = ""
        for (char in concatFormat.indices){
            if (concatFormat[char] != ';') concatResult += concatFormat[char]
        }

        return concatResult
    }
    private fun concatFormatters (concatFormat: String): Array<String>{
        return  concatFormat.dropLast(1).split(";").toTypedArray()
    }
    private fun concatFormat (concatFormat: String): Array<String>{
        return  concatFormat.split(";").toTypedArray()
    }
    private fun concatFormatDash (concatFormat: String): Array<String>{
        return  concatFormat.split("!").toTypedArray()
    }

    // to concat formatter
    fun toConcatFormatter(subCategory: MutableList<String>) : String {
        var subCategoryFormatted = ""
        subCategory.map {
            subCategoryFormatted += "$it;"
        }
        return subCategoryFormatted
    }

    // to readable issue for email
    private fun toReadableIssue(issue: MutableList<CategoryName>) : String{
        var emailBody = ""
        issue.map{
            emailBody += "<br/><tr>${it.categoryName} -  ${it.subCategoryName!!.map { subCat -> " $subCat " }} </tr>"
        }

        return emailBody
    }

    // function for checking co support list for editing actual ticket
    private fun coSupportChecking(recordNum: Int) : String ?{
        val coSupportList = getTicketLogs(recordNum).map { it.ticketRemarks }
        var coSupport : String ? = null
        coSupportList.map {
            if (it!!.size >= 3){
                if (coSupport == null){
                    coSupport = it.last()
                } else {
                    concatFormat(it.last().toString()).map { userId ->
                        var check = false
                        var count = 0
                        concatFormat(coSupport.toString()).map { currentUserId ->
                            if (currentUserId != userId) {
                                check = true
                            } else {
                                count += 1
                            }
                        }
                        if (check && count == 0){
                            coSupport += "$userId;"
                        }
                    }
                }
            }
        }
        return coSupport
    }

    // ---------------------------------------validating user fetching starts here-------------------------------------
    private fun decryptAes(encryptedData: String, key: ByteArray, iv: ByteArray): String {
        val cipher = javax.crypto.Cipher.getInstance("AES/CBC/PKCS5PADDING")
        val secretKey = javax.crypto.spec.SecretKeySpec(key, "AES")
        cipher.init(javax.crypto.Cipher.DECRYPT_MODE, secretKey, javax.crypto.spec.IvParameterSpec(iv))

        val encryptedBytes = Base64.getDecoder().decode(encryptedData)
        val decryptedBytes = cipher.doFinal(encryptedBytes)

        return String(decryptedBytes)
    }
    private fun getUserAccessToken(userId: String): String {
        return DBConfig().connection().use { con ->
            var result = ""
            con.prepareStatement(getAccessToken).use {
                it.setString(1, userId)
                it.executeQuery().use { resultData ->
                    if (resultData.next()) result = resultData.getString("access_token")
                    result
                }
            }
        }
    }
    private fun checkSystemKey(): List<SystemKeyCrew>{
        val systemKeyList = mutableListOf<SystemKeyCrew>()
        return DBConfig().connection().use { con ->
            con.prepareStatement(checkSystemKeyQuery).use {
                it.executeQuery().use { dataResult ->
                    while (dataResult.next()){
                        val recordId = dataResult.getInt("recordid")
                        val systemKeyName = dataResult.getString("system_key_name")
                        val systemKey = dataResult.getString("system_key")
                        val expirationDate = dataResult.getString("expiration_date")
                        val active = dataResult.getInt("active")
                        val addedBy = dataResult.getString("added_by")
                        val dateAdded = dataResult.getString("date_added")

                        systemKeyList.add(SystemKeyCrew(recordId, systemKeyName, systemKey, expirationDate!!, active, addedBy, dateAdded))
                    }
                    systemKeyList
                }
            }
        }
    }
    private fun getSecretIVAndSecretKey() :List<GetKeys>{
        val keys = mutableListOf<GetKeys>()
        return DBConfig().connection().use { con ->
            con.prepareStatement(getSecretIVAndSecretKeyQuery).use {
                it.executeQuery().use { resultData ->
                    while (resultData.next()){
                        val keyName = resultData.getString("system_key_name")
                        val keyValue = resultData.getString("system_key")

                        keys.add(GetKeys(keyName, keyValue))
                    }
                    keys
                }
            }
        }
    }
    // ---------------------------------------validating user fetching ends here --------------------------------------

    // --------------------------ticket id generator----------------------------
    fun ticketGenerator() : String{
        val idCounter = TicketController().getTicketCounts()
        val idFormat = arrayListOf('0','0','0','0','0','0','0')
        var idInserterFormat = idFormat.size - idCounter.length
        var ticketId = ""
        for (counterIndex in idCounter.indices){
            for (formatIndex in idFormat. indices){
                if (formatIndex >= idInserterFormat){
                    val indicesVal = idCounter[counterIndex]
                    idFormat[formatIndex] = indicesVal
                }
            }
            idInserterFormat ++
        }
        for (formatIndex in idFormat. indices){
            ticketId+= idFormat[formatIndex]
        }
        return ticketId
    }
    private fun toTicketGenerator(ticketID: String) : String{
        val idCount: String = ticketID
        val idFormat = arrayListOf('0','0','0','0','0','0','0')
        var idInserterFormat = idFormat.size - idCount.length
        var ticketIDFormat = ""
        for (counterIndex in idCount.indices){
            for (formatIndex in idFormat. indices){
                if (formatIndex >= idInserterFormat){
                    val indicesVal = idCount[counterIndex]
                    idFormat[formatIndex] = indicesVal
                }
            }
            idInserterFormat ++
        }
        for (formatIndex in idFormat. indices){
            ticketIDFormat+= idFormat[formatIndex]
        }
        return ticketIDFormat
    }
    private fun getTicketCounts(): String{
        var totalTicket = 1
        DBConfig().connection().use { con ->
            con.prepareStatement(getTicketCountsQueries).use {
                it.executeQuery().use { dataResult ->
                    if (dataResult.next()) totalTicket += dataResult.getInt("record_no")
                }
                return totalTicket.toString()
            }
        }
    }

}
