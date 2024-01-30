package finalize.routes

import finalize.controllers.TicketController
import finalize.models.*
import finalize.plugins.dateTimeSeparator
import finalize.plugins.toDateStamp
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getTicketRoutes(){
    /*--------- 192.168.4.134:3030/helpdesk-ticketing/select end point here --- */

    route("helpdesk-ticketing"){
        createTicketRoutes()
        route("/update-ticket"){
            updateTicketRoutes()
        }

        post("/teamActive") {
            val body = call.receive<UserValidation>()
            val accessToken = call.request.headers["Authorization"] ?: return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Success", 404, data = "Unauthorized"))
            try {
                when (TicketController().accessValidation(accessToken, body.accessTokenEnc, body.userId)){
                    0 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 400, "You are not authorized to access the page."))
                    1 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 401, "Invalid Key"))
                    2 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 402, "System key expired, please contact the administrator."))
                    3 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 403, "Invalid Token"))
                    4 -> return@post call.respond(GenericResponse("Success", 200, data = TicketController().getTeamActiveTo(body.userId)))
                }
            } catch (e: Exception){
                call.respond(GenericResponse("Server Error", 500, data = "Message: $e"))
            }
        }

        // get all tickets base on team support and user who logged
        post("{specification?}/{from?}/{to?}/{isAll?}") {
            val body = call.receive<UserValidation>()
            val accessToken = call.request.headers["Authorization"] ?: return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Success", 404, data = "Unauthorized"))
            val specification: String = call.parameters["specification"] ?: return@post call.respond(GenericResponse("Success", 200, data = "specification is empty"))
            val exclusiveTo = TicketController().getTeamActiveTo(body.userId)
            val fromDate: String ? = call.parameters["from"] // from date range
            val toDate: String ? = call.parameters["to"] // to date range
            val isAll: String ? = call.parameters["isAll"] // it's either 1 or 0
            val tickets = if (exclusiveTo != 0) TicketController().getTickets(exclusiveTo, body.userId, specification.toInt(), fromDate, toDate, 1, isAll!!.toInt())
            else TicketController().getTickets(exclusiveTo, body.userId, specification.toInt(), fromDate, toDate, 0, isAll!!.toInt())
            try {
                when (TicketController().accessValidation(accessToken, body.accessTokenEnc, body.userId)){
                    0 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 400, "You are not authorized to access the page."))
                    1 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 401, "Invalid Key"))
                    2 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 402, "System key expired, please contact the administrator."))
                    3 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 403, "Invalid Token"))
                    4 -> return@post call.respond(GenericResponse("Success", 200, data = TicketController().getFilteredTicket(tickets)))
                }
            } catch (e: Exception){
                call.respond(GenericResponse("Server Error", 500, data = "Message: $e"))
            }finally {
                tickets.clear()
            }
        }

        // for queuing
        route("/queuing"){
            post("{from?}/{to?}") {
                val body = call.receive<UserValidation>()
                val accessToken = call.request.headers["Authorization"] ?: return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Success", 404, data = "Unauthorized"))
                val fromDate: String = call.parameters["from"] ?: return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Success", 404, data = "From date is empty"))
                val toDate: String = call.parameters["to"] ?: return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Success", 404, data = "To date is empty"))
                val teamActive = TicketController().getTeamActiveTo(body.userId)
                val allTickets = TicketController().getTicketQueue(teamActive, fromDate, toDate)
                val reports = TicketController().getTicketQueueReports(teamActive, fromDate, toDate)
                val tickets = TicketController().getFilteredTicketQueue(allTickets)
                try {
                    when (TicketController().accessValidation(accessToken, body.accessTokenEnc, body.userId)){
                        0 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 400, "You are not authorized to access the page."))
                        1 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 401, "Invalid Key"))
                        2 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 401, "System key expired, please contact the administrator."))
                        3 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 401, "Invalid Token"))
                        4 -> return@post call.respond(GenericResponseForQueue("Success", 200, data = tickets, reports = reports ))
                    }
                } catch (e: Exception){
                    call.respond(GenericResponse("Server Error", 500, data = "Message: $e"))
                }finally {  // remove all data from heap memory
                    tickets.clear()
                    reports.clear()
                    allTickets.clear()
                }
            }
        }

        route("/ticket-counts"){
            get("{/exclusiveTo?}") {
                try {
                    val exclusiveTo = call.parameters["exclusiveTo"] ?: return@get call.respond(GenericResponse("Success", 200, data = "Team is Empty"))
                    val ticketCounts = TicketController().getTeamTicketCounts(exclusiveTo.toInt())
                    call.respond(GenericResponse("Success", 200, data = ticketCounts))
                } catch (e: Exception){
                    call.respond(GenericResponse("Server Error", 500, data = "Message: $e"))
                }
            }
        }

        // routes for getting history, co support, and list of category for specific tickets
        route("/history"){
            post("{recordNum?}/{subCategory?}/{coSupportList?}"){
                val accessToken = call.request.headers["Authorization"] ?: return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Success", 404, data = "Unauthorized"))
                val body = call.receive<UserValidation>()
                val recordNum = call.parameters["recordNum"] ?: return@post call.respond(GenericResponse("Success", 200, data = "Record Number is Empty"))
                val subCategoryList = call.parameters["subCategory"] ?: return@post call.respond(GenericResponse("Success", 200, data = "sub Category is Empty"))
                val coSupportList = call.parameters["coSupportList"]
                val ticketCategory = TicketController().getTicketSubCategory(subCategoryList)
                val coSupportResult = TicketController().getCoSupportList(coSupportList.toString())
                val ticketLogs = TicketController().getTicketLogs(recordNum.toInt())
                try {
                    when (TicketController().accessValidation(accessToken, body.accessTokenEnc, body.userId)){
                        0 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 400, "You are not authorized to access the page."))
                        1 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 401, "Invalid Key"))
                        2 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 401, "System key expired, please contact the administrator."))
                        3 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 401, "Invalid Token"))
                        4 -> return@post call.respond(GenericResponseHistory("Success", 200, category = ticketCategory.sortedBy { it.categoryFK }, logs = ticketLogs.sortedByDescending { it.referenceId }, coSupportList = coSupportResult))
                    }
                } catch (e: Exception){
                    call.respond(GenericResponse("Server Error", 500, data = "Message: $e"))
                }finally {
                    ticketCategory.clear()
                    ticketLogs.clear()
                    coSupportResult.clear()
                }
            }
        }

        route("system-module"){
            post {
                val systemModuleInput = call.receive<SystemModulePost>()
                val systemModuleData = TicketController().getSystemModule(systemModuleInput.employee, systemModuleInput.partners, systemModuleInput.crew)
                try {
                    call.respond(GenericResponse("Success", 200, data = systemModuleData))
                } catch (e: Exception){
                    call.respond(GenericResponse("Server Error", 500, data = "Message: $e"))
                }finally {
                    systemModuleData.clear()
                    systemModuleInput.destroy()
                }
            }
        }
    }
}