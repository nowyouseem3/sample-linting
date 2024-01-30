package finalize.routes

import finalize.controllers.TicketController
import finalize.models.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.updateTicketRoutes(){
    /*--------- 192.168.4.134:3030/helpdesk-ticketing/update-ticket/select end point here --- */

    // updating ticket to assign support personnel to ticket
    route("/assign-support"){
        put("{ticketNum?}/{exclusiveTo?}/{userId?}") {
            val recordNumber = call.parameters["ticketNum"] ?: return@put call.respond(GenericResponse("Success", 200, data = "Record number is empty"))
            val exclusiveTo = call.parameters["exclusiveTo"] ?: return@put call.respond(GenericResponse("Success", 200, data = "Team is empty"))
            val userId: String ? = call.parameters["userId"]
            val assignedPersonnelData = call.receive<UpdateTicketAssignPersonnel>()
            try {
                val ticketRes = TicketController().getTickets(exclusiveTo.toInt(), userId, 0, null, null, 1, 1).find { it.recordNum == recordNumber.toInt() }
                if (TicketController().updateAssignTickets(assignedPersonnelData.assignedPersonnelId, assignedPersonnelData.lastUpdatedBy.toString(), recordNumber.toInt()) != 1) return@put call.respond(GenericResponse("Success", 200, data = "Assigning personnel failed"))
                when (TicketController().assignValidation(assignedPersonnelData.remarks, ticketRes?.assignedSupportId, assignedPersonnelData.assignedPersonnelId, assignedPersonnelData.lastUpdatedBy.toString())){
                    1 -> TicketController().createTicketLogs(30, "${assignedPersonnelData.assignedPersonnelId}; grab this ticket!${TicketController().toConcatFormatter(assignedPersonnelData.coSupportIdList.toMutableList())}", assignedPersonnelData.assignedPersonnelId, recordNumber.toInt(), false)
                    2 -> TicketController().createTicketLogs(30, "${assignedPersonnelData.lastUpdatedBy}; assigned to ;${assignedPersonnelData.assignedPersonnelId}; this ticket!${TicketController().toConcatFormatter(assignedPersonnelData.coSupportIdList.toMutableList())}", assignedPersonnelData.lastUpdatedBy.toString(), recordNumber.toInt(), false)
                    3 -> TicketController().createTicketLogs(30, "No Remarks!${assignedPersonnelData.lastUpdatedBy}; re-assigned to ;${assignedPersonnelData.assignedPersonnelId}; this ticket!${TicketController().toConcatFormatter(assignedPersonnelData.coSupportIdList.toMutableList())}", assignedPersonnelData.lastUpdatedBy.toString(), recordNumber.toInt(), true)
                    4 -> TicketController().createTicketLogs(30, "${assignedPersonnelData.remarks}!${assignedPersonnelData.lastUpdatedBy}; re-assigned to ;${assignedPersonnelData.assignedPersonnelId}; this ticket!${TicketController().toConcatFormatter(assignedPersonnelData.coSupportIdList.toMutableList())}", assignedPersonnelData.lastUpdatedBy.toString(), recordNumber.toInt(), true)
                }
                if (TicketController().findAvailabilitySupport(assignedPersonnelData.lastSupportPersonnel.toString()) == 0) TicketController().updateSupportAvailable(assignedPersonnelData.lastSupportPersonnel.toString())
                TicketController().updateSupportEngage(assignedPersonnelData.assignedPersonnelId)
                call.respond(GenericResponse("Success", 200, data = "Update Success"))
            } catch (e: Exception){
                call.respond(GenericResponse("Server Error", 500, data = "Message: $e"))
            }finally {
                assignedPersonnelData.destroy()
            }
        }
    }

    // adding logs, adding co support, and resolving tickets
    route("/action-taken"){
        post("{ticketNum?}/{isResolve?}") {
            val recordNumber = call.parameters["ticketNum"] ?: return@post call.respond(GenericResponse("Success", 200, data = "Record number is empty"))
            val isResolvedCheck = call.parameters["isResolve"] ?: return@post call.respond(GenericResponse("Success", 200, data = "Resolve validation is empty"))
            val actionTakenData = call.receive<ActionTakenLogs>()
            try {
                if (isResolvedCheck.toInt() == 1){
                    if (actionTakenData.coSupportIdList!!.isNotEmpty()){
                        TicketController().createTicketLogs(31, "Ticket Resolved!${actionTakenData.actionTaken.toString()}!${actionTakenData.lastUpdatedById}; added his/ her companion!${TicketController().toConcatFormatter(actionTakenData.coSupportIdList)}", actionTakenData.lastUpdatedById, recordNumber.toInt(), false)
                        TicketController().updateCoSupportTickets(actionTakenData.lastUpdatedById, recordNumber.toInt())
                        TicketController().updateResolveTicket(actionTakenData.lastUpdatedById, recordNumber.toInt())
                        if (TicketController().findAvailabilitySupport(actionTakenData.lastSupportPersonnel.toString()) == 0) TicketController().updateSupportAvailable(actionTakenData.lastSupportPersonnel.toString())
                    }else{
                        TicketController().createTicketLogs(31, "Ticket Resolved!${actionTakenData.actionTaken}!${TicketController().toConcatFormatter(actionTakenData.coSupportIdList)}", actionTakenData.lastUpdatedById, recordNumber.toInt(), false)
                        TicketController().updateResolveTicket(actionTakenData.lastUpdatedById, recordNumber.toInt())
                        if (TicketController().findAvailabilitySupport(actionTakenData.lastSupportPersonnel.toString()) == 0) TicketController().updateSupportAvailable(actionTakenData.lastSupportPersonnel.toString())
                    }
                    TicketController().sendEmailResolved(isResolvedCheck.toInt(), actionTakenData.ticketNumber.toString(), recordNumber.toInt(), actionTakenData.ticketCreated.toString(), actionTakenData.toSupportEmail.toString(), actionTakenData.toSupportName.toString(), actionTakenData.assignedSupport.toString(), actionTakenData.description.toString(), actionTakenData.actionTaken.toString(), actionTakenData.issue!!.toMutableList(), actionTakenData.teamActive!!.toInt(), null)
                } else if (actionTakenData.coSupportIdList!!.isNotEmpty()){
                    TicketController().updateReOngoingTicket(recordNumber.toInt(),actionTakenData.lastUpdatedById)
                    TicketController().createTicketLogs(30, "${actionTakenData.actionTaken}!${actionTakenData.lastUpdatedById}; added his/ her companion!${TicketController().toConcatFormatter(actionTakenData.coSupportIdList)}", actionTakenData.lastUpdatedById, recordNumber.toInt(), true)
                    TicketController().updateCoSupportTickets(actionTakenData.lastUpdatedById, recordNumber.toInt())
                    if (actionTakenData.toSendEmail){
                        TicketController().sendEmailResolved(isResolvedCheck.toInt(), actionTakenData.ticketNumber.toString(), recordNumber.toInt(), actionTakenData.ticketCreated.toString(), actionTakenData.toSupportEmail.toString(), actionTakenData.toSupportName.toString(), actionTakenData.assignedSupport.toString(), actionTakenData.description.toString(), actionTakenData.actionTaken.toString(), actionTakenData.issue!!.toMutableList(), actionTakenData.teamActive!!.toInt(), null)
                    }
                } else {
                    TicketController().updateReOngoingTicket(recordNumber.toInt(),actionTakenData.lastUpdatedById)
                    TicketController().createTicketLogs(30,"${actionTakenData.actionTaken}!${TicketController().toConcatFormatter(actionTakenData.coSupportIdList)}" , actionTakenData.lastUpdatedById, recordNumber.toInt(), true)
                    TicketController().updateCoSupportTickets(actionTakenData.lastUpdatedById, recordNumber.toInt())
                    if (actionTakenData.toSendEmail){
                        TicketController().sendEmailResolved(isResolvedCheck.toInt(), actionTakenData.ticketNumber.toString(), recordNumber.toInt(), actionTakenData.ticketCreated.toString(), actionTakenData.toSupportEmail.toString(), actionTakenData.toSupportName.toString(), actionTakenData.assignedSupport.toString(), actionTakenData.description.toString(), actionTakenData.actionTaken.toString(), actionTakenData.issue!!.toMutableList(), actionTakenData.teamActive!!.toInt(), null)
                    }
                }
                call.respond(GenericResponse("Success", 200, data = "Update Success"))
            } catch (e: Exception){
                call.respond(GenericResponse("Server Error", 500, data = "Message: $e"))
            } finally {
                actionTakenData.destroy()
            }
        }

        put("{recordNum?}/{isResolved?}/{referenceId?}") {
            val refNumber = call.parameters["referenceId"] ?: return@put call.respond(GenericResponse("Success", 200, data = "Reference number is empty"))
            val recNumber = call.parameters["recordNum"] ?: return@put call.respond(GenericResponse("Success", 200, data = "Record number is empty"))
            val isResolvedCheck = call.parameters["isResolved"] ?: return@put call.respond(GenericResponse("Success", 200, data = "Resolve validation is empty"))
            val actionTakenData = call.receive<ActionTakenLogs>()
            try {
                if (isResolvedCheck.toInt() == 1){
                    if (actionTakenData.coSupportIdList!!.isNotEmpty()){
                        if (TicketController().updateActualTicket(31,"Ticket Resolved!${actionTakenData.actionTaken.toString()}!${actionTakenData.lastUpdatedById}; added his/ her companion!${TicketController().toConcatFormatter(actionTakenData.coSupportIdList)}", actionTakenData.lastUpdatedById, refNumber.toInt()) == 1) {
                            TicketController().updateCoSupportTickets(actionTakenData.lastUpdatedById, recNumber.toInt())
                            TicketController().updateResolveTicket(actionTakenData.lastUpdatedById, recNumber.toInt())
                            if (TicketController().findAvailabilitySupport(actionTakenData.lastSupportPersonnel.toString()) == 0) TicketController().updateSupportAvailable(actionTakenData.lastSupportPersonnel.toString())
                        } else call.respond(GenericResponse("Success", 200, data = "Update Failed"))
                    } else {
                        if (TicketController().updateActualTicket(31,"Ticket Resolved!${actionTakenData.actionTaken.toString()}!${TicketController().toConcatFormatter(actionTakenData.coSupportIdList)}", actionTakenData.lastUpdatedById, refNumber.toInt()) == 1) {
                            TicketController().updateCoSupportTickets(actionTakenData.lastUpdatedById, recNumber.toInt())
                            TicketController().updateResolveTicket(actionTakenData.lastUpdatedById, recNumber.toInt())
                            if (TicketController().findAvailabilitySupport(actionTakenData.lastSupportPersonnel.toString()) == 0) TicketController().updateSupportAvailable(actionTakenData.lastSupportPersonnel.toString())
                        } else call.respond(GenericResponse("Success", 200, data = "Update Failed"))
                    }
                    TicketController().sendEmailResolved(isResolvedCheck.toInt(), actionTakenData.ticketNumber.toString(), recNumber.toInt(), actionTakenData.ticketCreated.toString(), actionTakenData.toSupportEmail.toString(), actionTakenData.toSupportName.toString(), actionTakenData.assignedSupport.toString(), actionTakenData.description.toString(), actionTakenData.actionTaken.toString(), actionTakenData.issue!!.toMutableList(), actionTakenData.teamActive!!.toInt(), null)
                } else if (actionTakenData.coSupportIdList!!.isNotEmpty()){
                    if (TicketController().updateActualTicket(30, "${actionTakenData.actionTaken}!${actionTakenData.lastUpdatedById}; added his/ her companion!${TicketController().toConcatFormatter(actionTakenData.coSupportIdList)}", actionTakenData.lastUpdatedById, refNumber.toInt()) == 1) {
                        TicketController().updateCoSupportTickets(actionTakenData.lastUpdatedById, recNumber.toInt())
                        if (actionTakenData.toSendEmail){
                            TicketController().sendEmailResolved(isResolvedCheck.toInt(), actionTakenData.ticketNumber.toString(), recNumber.toInt(), actionTakenData.ticketCreated.toString(), actionTakenData.toSupportEmail.toString(), actionTakenData.toSupportName.toString(), actionTakenData.assignedSupport.toString(), actionTakenData.description.toString(), actionTakenData.actionTaken.toString(), actionTakenData.issue!!.toMutableList(), actionTakenData.teamActive!!.toInt(), null)
                        }
                    } else call.respond(GenericResponse("Success", 200, data = "Update Failed"))
                } else {
                    TicketController().updateActualTicket(30, "${actionTakenData.actionTaken}!${TicketController().toConcatFormatter(actionTakenData.coSupportIdList)}", actionTakenData.lastUpdatedById, refNumber.toInt())
                    TicketController().updateCoSupportTickets(actionTakenData.lastUpdatedById, recNumber.toInt())
                    if (actionTakenData.toSendEmail){
                        TicketController().sendEmailResolved(isResolvedCheck.toInt(), actionTakenData.ticketNumber.toString(), recNumber.toInt(), actionTakenData.ticketCreated.toString(), actionTakenData.toSupportEmail.toString(), actionTakenData.toSupportName.toString(), actionTakenData.assignedSupport.toString(), actionTakenData.description.toString(), actionTakenData.actionTaken.toString(), actionTakenData.issue!!.toMutableList(), actionTakenData.teamActive!!.toInt(), null)
                    }
                }
                return@put call.respond(GenericResponse("Success", 200, data = "Update Success"))
            } catch (e: Exception){
                call.respond(GenericResponse("Server Error", 500, data = "Message: $e"))
            } finally {
                actionTakenData.destroy()
            }
        }
    }

    // routes for updating actual ticket when someone's editing
    route("/update-actual-ticket"){
        get("{referenceId?}/{userId?}") {
            val recNumber = call.parameters["referenceId"] ?: return@get call.respond(GenericResponse("Success", 200, data = "Record number is empty"))
            val userUpdating = TicketController().getActualTicketEditing(recNumber.toInt())
            val userUpdatingName = TicketController().getRequester().find { it.requesterId == userUpdating }?.requesterName
            val userID: String ? = call.parameters["userId"]
            try {
                if (userID != null){
                    if (TicketController().getActualTicketEditing(recNumber.toInt()).isNullOrEmpty()) {
                        TicketController().updateActualTicketEditing(userID.toString(), recNumber.toInt())
                        call.respond(GenericResponse("Success", 200, data = "Actual Ticket updated"))
                    } else {
                        call.respond(GenericResponse("Success", 40, data = userUpdatingName))
                    }
                }else {
                    TicketController().updateActualTicketEditing(null, recNumber.toInt())
                    call.respond(GenericResponse("Success", 200, data = "Ticket Open for editing"))
                }
            }catch (e: Exception){
                call.respond(GenericResponse("Server Error", 500, data = "Message: $e"))
            }
        }
    }

    route("/update-ticket-record"){
        get("{referenceId?}/{userId?}") {
            val recNum = call.parameters["referenceId"] ?: return@get call.respond(GenericResponse("Success", 200, data = "Record number is empty"))
            val userUpdating = TicketController().getTicketRecordEditing(recNum.toInt())
            val userUpdatingName = TicketController().getRequester().find { it.requesterId == userUpdating }?.requesterName
            val userID: String ? = call.parameters["userId"]
            try {
                if (userID != null){
                    if (TicketController().getTicketRecordEditing(recNum.toInt()).isNullOrEmpty()) {
                        TicketController().updateTicketRecordEditing(userID.toString(), recNum.toInt())
                        call.respond(GenericResponse("Success", 200, data = "Actual Ticket updated"))
                    } else {
                        call.respond(GenericResponse("Success", 40, data = userUpdatingName))
                    }
                }else {
                    TicketController().updateTicketRecordEditing(null, recNum.toInt())
                    call.respond(GenericResponse("Success", 200, data = "Ticket Open for editing"))
                }
            }catch (e: Exception){
                call.respond(GenericResponse("Server Error", 500, data = "Message: $e"))
            }
        }
    }

    // routes for closing tickets
    route("/close-ticket"){
        post("{recordNum?}/{isClosed?}/{bySystem?}") {
            val recordNumber = call.parameters["recordNum"] ?: return@post call.respond(GenericResponse("Success", 200, data = "Record number is empty"))
            val isClosedSystem  = call.parameters["bySystem"] ?: return@post call.respond(GenericResponse("Success", 200, data = "Close Specification is empty"))
            val toClosedValue = call.parameters["isClosed"] ?: return@post call.respond(GenericResponse("Success", 200, data = "to close Specification is empty"))
            val toClosedValuePortal = call.parameters["isClosed"] ?: return@post call.respond(GenericResponse("Success", 200, data = "to close Specification is empty"))
            val ticketDetails = call.receive<CloseTicket>()
            val ticketList = TicketController().getAllTickets(recordNumber.toInt())
            val tickets = TicketController().getFilteredTicket(ticketList)
            val ticketResult = tickets.find { it.recordNum == recordNumber.toInt() }
            try {
                if (TicketController().getEmployeeID(ticketResult!!.actualToSupportId) != ticketDetails.employeeId!!.toInt()) return@post call.respond(GenericResponse("Success", 210, data = "Invalid Id"))
                when (TicketController().paramEnc(toClosedValue).toBoolean() || toClosedValuePortal.toBoolean()){
                    true -> {
                        var separator : String ? = "!"
                        if (ticketDetails.actionTaken == "" || ticketDetails.actionTaken == null) {
                            separator = ""
                        }
                        if (TicketController().updateCloseTicket(recordNumber.toInt(), ticketDetails.lastUpdatedById) == 1){
                            if (isClosedSystem.toBoolean()){
                                TicketController().createTicketLogs(32, "${ticketDetails.actionTaken?.replace("!","")}${separator}Ticket Closed by System!", "System", recordNumber.toInt(), false)
                                TicketController().sendEmailResolved(2, ticketDetails.ticketNumber.toString(), recordNumber.toInt(), ticketDetails.ticketCreated.toString(), ticketDetails.toSupportEmail.toString(), ticketDetails.toSupportName.toString(), ticketDetails.assignedSupport.toString(), ticketDetails.description.toString(), ticketDetails.actionTaken.toString(), ticketDetails.issue!!.toMutableList(), null, "System")
                            }else{
                                TicketController().createTicketLogs(32, "${ticketDetails.actionTaken?.replace("!","")}${separator}Ticket Closed by Recipient!", ticketDetails.lastUpdatedById, recordNumber.toInt(), false)
                                TicketController().sendEmailResolved(2, ticketDetails.ticketNumber.toString(), recordNumber.toInt(), ticketDetails.ticketCreated.toString(), ticketDetails.toSupportEmail.toString(), ticketDetails.toSupportName.toString(), ticketDetails.assignedSupport.toString(), ticketDetails.description.toString(), ticketDetails.actionTaken.toString(), ticketDetails.issue!!.toMutableList(), null, ticketDetails.toSupportName.toString())
                            }
                            return@post call.respond(GenericResponse("Success", 200, data = "Closing ticket Success"))
                        } else return@post call.respond(GenericResponse("Bad Request", 400, data = "Closing ticket Failed"))
                    }
                    false -> {
                        if (TicketController().updateReOngoingTicket(recordNumber.toInt(), ticketDetails.lastUpdatedById) == 1){
                            TicketController().createTicketLogs(30, "${ticketDetails.actionTaken?.replace("!","")}!Ticket Declined!", ticketDetails.lastUpdatedById, recordNumber.toInt(), false)
                            TicketController().sendEmailResolved(3, ticketDetails.ticketNumber.toString(), recordNumber.toInt(), ticketDetails.ticketCreated.toString(), ticketDetails.toSupportEmail.toString(), ticketDetails.toSupportName.toString(), ticketDetails.assignedSupport.toString(), ticketDetails.description.toString(), ticketDetails.actionTaken.toString(), ticketDetails.issue!!.toMutableList(), null, ticketDetails.toSupportName.toString())
                            TicketController().updateSupportEngage(ticketDetails.lastSupportPersonnel.toString())
                            call.respond(GenericResponse("Success", 200, data = "Re ongoing success"))
                        }
                    }
                }
            } catch (e: Exception){
                call.respond(GenericResponse("Server Error", 500, data = "Message: $e"))
            }finally {
                ticketDetails.destroy()
                ticketList.clear()
                tickets.clear()
                ticketResult!!.destroy()
            }
        }

        get("{recordNum?}/{isClosed?}") {
            val recordNumber = call.parameters["recordNum"]?: return@get call.respond(GenericResponse("Success", 50, data = "Record number is empty"))
            val isToClose = call.parameters["isClosed"] ?: return@get call.respond(GenericResponse("Success", 51, data = "To close value is empty"))
            val ticketResult= TicketController().getTicketClose().find { it.recordNum == recordNumber.split(".")[0].toInt() } ?: return@get call.respond(GenericResponse("Success", 900, data = "Invalid records"))
            if (TicketController().manualEnc(ticketResult.recordNum, ticketResult.ticketCreated).split(".")[1] != recordNumber.split(".")[1]) return@get call.respond(GenericResponse("Success", 900, data = "Invalid records"))
            val ticketCategory = TicketController().getTicketSubCategory(ticketResult.subCategory.toString())
            val coSupportResult = TicketController().getCoSupportList(ticketResult.coSupport)
            val ticketLogs = TicketController().getTicketLogs(ticketResult.recordNum)
            try {
                if (call.request.headers["Authorization"] != "Bearer marlow2022") return@get call.respond(GenericResponse("Success", 200, data = "Record number is empty"))
                else {
                    when (ticketResult.statusId){
                        31 -> {
                            if (TicketController().paramEnc(isToClose) == "true") return@get call.respond(GenericResponseAll("to close ticket", 200, data = ticketResult , category = ticketCategory.sortedBy { it.categoryFK }, logs = ticketLogs.last(), coSupportList = coSupportResult))
                            else if(TicketController().paramEnc(isToClose) == "false") return@get call.respond(GenericResponseAll("to re ongoing ticket", 210, data = ticketResult, category = ticketCategory.sortedBy { it.categoryFK }, logs = ticketLogs.last(), coSupportList = coSupportResult))
                            else return@get call.respond(GenericResponse("Success", 900, data = "Invalid records"))
                        }
                        30 -> return@get call.respond(GenericResponseAll("Ticket set to on-going", 251, data = ticketResult, category = ticketCategory.sortedBy { it.categoryFK }, logs = ticketLogs.last(), coSupportList = coSupportResult))
                        32 -> return@get call.respond(GenericResponseAll("Ticket already closed", 250, data = ticketResult, category = ticketCategory.sortedBy { it.categoryFK }, logs = ticketLogs.takeLast(2), coSupportList = coSupportResult))
                    }
                }
            }catch (e: Exception){
                call.respond(GenericResponse("Server Error", 500, data = "Message: $e"))
            }finally {
                ticketCategory.clear()
                coSupportResult.clear()
            }
        }
    }

}