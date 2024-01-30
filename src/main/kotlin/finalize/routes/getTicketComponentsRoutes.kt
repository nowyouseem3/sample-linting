package finalize.routes

import finalize.controllers.TicketController
import finalize.models.GenericResponse
import finalize.models.UserValidation
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getTicketComponentsRoutes(){
    /*--------- 192.168.4.134:3030/helpdesk-ticketing/create-ticket/ticket-components/select end point of components here --- */

    // this route is for getting requester list
    route("/requester"){
        post {
            val accessToken = call.request.headers["Authorization"] ?: return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Success", 404, data = "Unauthorized"))
            val body = call.receive<UserValidation>()
            val userList = TicketController().getRequester()
            try {
                when (TicketController().accessValidation(accessToken, body.accessTokenEnc, body.userId)){
                    0 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 400, "You are not authorized to access the page."))
                    1 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 401, "Invalid Key"))
                    2 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 401, "System key expired, please contact the administrator."))
                    3 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 401, "Invalid Token"))
                    4 -> return@post call.respond(GenericResponse("Success", 200, data = userList))
                }
            } catch (e: Exception){
                call.respond(GenericResponse("Server Error", 500, data = "Message: $e"))
            } finally {
                userList.clear()
            }
        }
    }

    // this route is for getting reported method
    route("/reported-method"){
        post {
            val accessToken = call.request.headers["Authorization"] ?: return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Success", 404, data = "Unauthorized"))
            val body = call.receive<UserValidation>()
            val requestMethod = TicketController().getRequestMethod()
            try {
                when (TicketController().accessValidation(accessToken, body.accessTokenEnc, body.userId)){
                    0 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 400, "You are not authorized to access the page."))
                    1 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 401, "Invalid Key"))
                    2 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 401, "System key expired, please contact the administrator."))
                    3 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 401, "Invalid Token"))
                    4 -> return@post call.respond(GenericResponse("Success", 200, data = requestMethod))
                }
            } catch (e: Exception){
                call.respond(GenericResponse("Server Error", 500, data = "Message: $e"))
            } finally {
                requestMethod.clear()
            }
        }
    }

    // this route is for getting category list
    route("/category"){
        post {
            val accessToken = call.request.headers["Authorization"] ?: return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Success", 404, data = "Unauthorized"))
            val body = call.receive<UserValidation>()
            val categoryList = TicketController().getCategory()
            try {
                when (TicketController().accessValidation(accessToken, body.accessTokenEnc, body.userId)){
                    0 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 400, "You are not authorized to access the page."))
                    1 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 401, "Invalid Key"))
                    2 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 401, "System key expired, please contact the administrator."))
                    3 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 401, "Invalid Token"))
                    4 -> return@post call.respond(GenericResponse("Success", 200, data = categoryList))
                }
            } catch (e: Exception){
                call.respond(GenericResponse("Server Error", 500, data = "Message: $e"))
            }finally {
                categoryList.clear()
            }
        }
    }

    // this route is for getting sub category list
    route("/sub-category"){
        post("{categoryFK?}") {
            val accessToken = call.request.headers["Authorization"] ?: return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Success", 404, data = "Unauthorized"))
            val body = call.receive<UserValidation>()
            val categoryId = call.parameters["categoryFK"] ?: return@post call.respond(GenericResponse("Success", 200, data = "category is empty"))
            val subCategoryList = TicketController().getSubCategory(categoryId.toInt())
            try {
                when (TicketController().accessValidation(accessToken, body.accessTokenEnc, body.userId)){
                    0 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 400, "You are not authorized to access the page."))
                    1 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 401, "Invalid Key"))
                    2 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 401, "System key expired, please contact the administrator."))
                    3 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 401, "Invalid Token"))
                    4 -> return@post call.respond(GenericResponse("Success", 200, data = subCategoryList))
                }
            } catch (e: Exception){
                call.respond(GenericResponse("Server Error", 500, data = "Message: $e"))
            }finally {
                subCategoryList.clear()
            }
        }
    }

    // this route is for getting company area list
    route("/company-area"){
        post {
            val accessToken = call.request.headers["Authorization"] ?: return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Success", 404, data = "Unauthorized"))
            val body = call.receive<UserValidation>()
            val companyArea = TicketController().getCompanyArea()
            try {
                when (TicketController().accessValidation(accessToken, body.accessTokenEnc, body.userId)){
                    0 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 400, "You are not authorized to access the page."))
                    1 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 401, "Invalid Key"))
                    2 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 401, "System key expired, please contact the administrator."))
                    3 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 401, "Invalid Token"))
                    4 -> return@post call.respond(GenericResponse("Success", 200, data = companyArea))
                }
            } catch (e: Exception){
                call.respond(GenericResponse("Server Error", 500, data = "Message: $e"))
            }finally {
                companyArea.clear()
            }
        }
    }

    // this route is for getting company floor list depend on area
    route("/company-floor"){
        post("{floorFK?}") {
            val accessToken = call.request.headers["Authorization"] ?: return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Success", 404, data = "Unauthorized"))
            val body = call.receive<UserValidation>()
            val floorId = call.parameters["floorFK"] ?: return@post call.respond(GenericResponse("Success", 200, data = "floor is empty"))
            val floorArea = TicketController().getCompanyFloor(floorId.toInt())
            try {
                when (TicketController().accessValidation(accessToken, body.accessTokenEnc, body.userId)){
                    0 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 400, "You are not authorized to access the page."))
                    1 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 401, "Invalid Key"))
                    2 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 401, "System key expired, please contact the administrator."))
                    3 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 401, "Invalid Token"))
                    4 -> return@post call.respond(GenericResponse("Success", 200, data = floorArea))
                }
            } catch (e: Exception){
                call.respond(GenericResponse("Server Error", 500, data = "Message: $e"))
            }finally {
                floorArea.clear()
            }
        }
    }

    // this route is for getting support list base on tickets to support
    route("/team-assigned"){
        post() {
            val accessToken = call.request.headers["Authorization"] ?: return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Success", 404, data = "Unauthorized"))
            val body = call.receive<UserValidation>()
            val teamActive = TicketController().getTeamActiveTo(body.userId)
            val teamSelected = TicketController().getAssignedSupport(teamActive)
            try {
                when (TicketController().accessValidation(accessToken, body.accessTokenEnc, body.userId)){
                    0 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 400, "You are not authorized to access the page."))
                    1 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 401, "Invalid Key"))
                    2 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 401, "System key expired, please contact the administrator."))
                    3 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 401, "Invalid Token"))
                    4 -> return@post call.respond(GenericResponse("Success", 200, data = teamSelected))
                }
            } catch (e: Exception){
                call.respond(GenericResponse("Server Error", 500, data = "Message: $e"))
            }finally {
                teamSelected.clear()
            }
        }
    }

    // getting color changer base on ticket status
    route("/ticket-status"){
        post {
            val accessToken = call.request.headers["Authorization"] ?: return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Success", 404, data = "Unauthorized"))
            val body = call.receive<UserValidation>()
            val status = TicketController().getTicketStatus()
            try {
                when (TicketController().accessValidation(accessToken, body.accessTokenEnc, body.userId)){
                    0 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 400, "You are not authorized to access the page."))
                    1 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 401, "Invalid Key"))
                    2 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 401, "System key expired, please contact the administrator."))
                    3 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 401, "Invalid Token"))
                    4 -> return@post call.respond(GenericResponse("Success", 200, data = status))
                }
            } catch (e: Exception){
                call.respond(GenericResponse("Server Error", 500, data = "Message: $e"))
            }finally {
                status.clear()
            }
        }
    }

    // getting all sub category nested in category list
    route("ticket-categoryX-sub"){
        post {
            val accessToken = call.request.headers["Authorization"] ?: return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Success", 404, data = "Unauthorized"))
            val body = call.receive<UserValidation>()
            val subAndCategory = TicketController().getTicketSubAndCategory()
            try {
                when (TicketController().accessValidation(accessToken, body.accessTokenEnc, body.userId)){
                    0 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 400, "You are not authorized to access the page."))
                    1 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 401, "Invalid Key"))
                    2 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 401, "System key expired, please contact the administrator."))
                    3 -> return@post call.respond(HttpStatusCode.Unauthorized, GenericResponse("Failed", 401, "Invalid Token"))
                    4 -> return@post call.respond(GenericResponse("Success", 200, data = subAndCategory))
                }
            } catch (e: Exception){
                call.respond(GenericResponse("Server Error", 500, data = "Message: $e"))
            }finally {
                subAndCategory.clear()
                body.destroy()
            }
        }
    }

    // testing routes
    route("/testing-func"){
        get("{accessToken?}"){

                //val token = call.parameters["accessToken"].toString()
                //val headers = call.request.headers["Authorization"]
                val concat = TicketController().actionTakenValidation(1, null,false)
            try {
                call.respond(GenericResponse("success", 200, data = concat))
            } catch(e: Exception){
                call.respond("error : $e")
            }

        }
    }
}