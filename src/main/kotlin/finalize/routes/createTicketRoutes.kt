package finalize.routes

import finalize.controllers.TicketController
import finalize.models.*
import finalize.plugins.dateTimeRefactor
import finalize.plugins.dateTimeSeparator
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.createTicketRoutes(){
    /*--------- 192.168.4.134:3030/helpdesk-ticketing/create-ticket --- */
    route("/create-ticket"){
        route("/ticket-components"){
            getTicketComponentsRoutes()
        }

        // creating ticket
        post {
            val accessToken = call.request.headers["Authorization"] ?: return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Success", 404, data = "Unauthorized"))
            val tickets = call.receive<DynamicTickets>()
            val ticketComponents = tickets.tickets
            val validation = createTicketBanksValidation()
            if (validation.isNotEmpty()){
                return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Success", 404, data = validation.map { it.validationResult }))
            }
            try {
                ticketComponents.map {
                    when (TicketController().accessValidation(accessToken, it.accessTokenEnc, it.userId)){
                        0 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 400, "You are not authorized to access the page."))
                        1 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 401, "Invalid Key"))
                        2 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 401, "System key expired, please contact the administrator."))
                        3 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 401, "Invalid Token"))
                        else -> {
                            var timeReported = ""
                            var dateReported = ""
                            if (it.dateTimeReported != null){
                                timeReported = dateTimeRefactor(dateTimeSeparator(it.dateTimeReported.toString()).map {timeData -> timeData.time }.toString())
                                dateReported = dateTimeRefactor(dateTimeSeparator(it.dateTimeReported.toString()).map {dateData -> dateData.date }.toString())
                            }
                            TicketController().createTicket(
                                it.reportedById,
                                it.actualToSupportId,
                                it.subCategory,
                                it.requesterRemarks,
                                timeReported,
                                dateReported,
                                it.reportedMethod,
                                it.companyArea,
                                it.insertedBy,
                                it.supportAssignedBy,
                                it.assignedPersonnelId,
                                it.exclusiveToTeam
                            )
                            if (it.assignedPersonnelId != null && it.assignedPersonnelId != ""){
                                TicketController().createTicketLogs(30, "${it.insertedBy}; assigned to ;${it.assignedPersonnelId}; this ticket", it.insertedBy, TicketController().ticketGenerator().toInt() - 1, false)
                                TicketController().updateSupportEngage(it.assignedPersonnelId)
                            }
                        }
                    }
                }
                return@post call.respond(GenericResponse("Success", 200, "Ticket Created"))
            } catch (e : Exception){
                call.respond(GenericResponse("Internal Error", 400, "Creation Error: $e"))
            } finally {
                tickets.destroy()
                ticketComponents.clear()
            }
        }

        put("{recordNumber?}") {
            val accessToken = call.request.headers["Authorization"] ?: return@put call.respond(HttpStatusCode.Unauthorized, GenericResponse("Success", 404, data = "Unauthorized"))
            val recordNum = call.parameters["recordNumber"] ?: return@put call.respond( GenericResponse("Success", 200, "Record Number is Empty"))
            val components = call.receive<CreateTicketBanks>()
            try {
                when (TicketController().accessValidation(accessToken, components.accessTokenEnc, components.userId)){
                    0 -> return@put call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 400, "You are not authorized to access the page."))
                    1 -> return@put call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 401, "Invalid Key"))
                    2 -> return@put call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 401, "System key expired, please contact the administrator."))
                    3 -> return@put call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 401, "Invalid Token"))
                    4 -> {
                        val timeReported = dateTimeRefactor(dateTimeSeparator(components.dateTimeReported.toString()).map {timeData -> timeData.time }.toString())
                        val dateReported = dateTimeRefactor(dateTimeSeparator(components.dateTimeReported.toString()).map {dateData -> dateData.date }.toString())
                        if (components.assignedPersonnelId!!.isNotEmpty()){
                            if (TicketController().updateTicketRecord(components.reportedById, components.actualToSupportId, components.reportedMethod, components.subCategory, components.requesterRemarks, components.companyArea, components.assignedPersonnelId, recordNum.toInt(), dateReported, timeReported, components.insertedBy, 30, components.supportAssignedBy. toString(), components.exclusiveToTeam!!.toInt()) == 1) {
                                TicketController().createTicketLogs(30, "${components.supportAssignedBy}; assigned to ;${components.assignedPersonnelId}; this ticket!", components.supportAssignedBy.toString(), recordNum.toInt(), false)
                            }
                        }else{
                            TicketController().updateTicketRecord(components.reportedById, components.actualToSupportId, components.reportedMethod, components.subCategory, components.requesterRemarks, components.companyArea, components.assignedPersonnelId, recordNum.toInt(), dateReported, timeReported, components.insertedBy, 29, null, components.exclusiveToTeam!!.toInt())
                        }
                        return@put call.respond(GenericResponse("Success", 200, "Ticket Updated"))
                    }
                }
                call.respond( GenericResponse("Success", 200, "Update Failed"))
            } catch (e : Exception){
                call.respond(GenericResponse("Internal Error", 400, "Creation Error: $e"))
            } finally {
                components.destroy()
            }
        }
    }

}