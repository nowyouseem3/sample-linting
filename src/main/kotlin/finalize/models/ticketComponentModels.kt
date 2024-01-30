package finalize.models

import kotlinx.serialization.Serializable
import javax.security.auth.Destroyable

// storage for list of all users
@Serializable
data class RequesterBanks(
    val requesterId: String,
    val requesterName: String
): Destroyable

// storage for requesting method list
@Serializable
data class RequestMethodBanks(
    val requestMethodName: String,
    val requestMethodId: Int
): Destroyable

// storage for category list
@Serializable
data class CategoryBanks(
    val categoryName: String,
    val categoryId: Int,
    val teamActive: String
): Destroyable

// storage for sub category list
@Serializable
data class SubCategoryBanks(
    val categoryName: String,
    val categoryFK: Int,
    val subCategoryName: String,
    val subCategoryId: Int,
    val teamActive: Int
): Destroyable

// storage for company area list
@Serializable
data class AreaBanks(
    val floorIdFK: Int,
    val areaName: String,
    val areaId: Int
): Destroyable

// storage for company floor list
@Serializable
data class FloorBanks(
    val floorId: Int,
    val floorName: String,
    val buildingArea: String
): Destroyable

// storage for support personnel list
@Serializable
data class AssignedBanks(
    val teamId: Int,
    val userId: String,
    val supportName: String,
    val employeeId: String?,
    val statusCode: Int,
    val statusName: String
): Destroyable

// this used for creating multiple tickets in one request
@Serializable
data class DynamicTickets(
    val tickets : MutableList<CreateTicketBanks>,
): Destroyable
@Serializable
data class CreateTicketBanks(
    val reportedById: String,
    val actualToSupportId: String,
    val subCategory: MutableList<String>,
    val requesterRemarks: String,
    val dateTimeReported: String ?,
    val reportedMethod: Int,
    val companyArea: Int,
    val insertedBy: String,
    val supportAssignedBy: String ?,
    val assignedPersonnelId: String ?,
    val exclusiveToTeam: Int ?,
    val userId: String,
    val accessTokenEnc: String
): Destroyable

fun createTicketBanksValidation() :MutableList<ValidationResponse>{
    val ticketComponent = mutableListOf<CreateTicketBanks>()
    val validationData = mutableListOf<ValidationResponse>()

    ticketComponent.map {
        if (it.reportedById .isEmpty()){
            validationData.add(ValidationResponse("Failed", "Reported ID is empty"))
        }
        if (it.actualToSupportId.isEmpty()){
            validationData.add(ValidationResponse("Failed", "Recipient ID is empty"))
        }
        if (it.subCategory.isEmpty()){
            validationData.add(ValidationResponse("Failed", "SubCategory ID is empty"))
        }
        if (it.requesterRemarks.isEmpty()){
            validationData.add(ValidationResponse("Failed", "Remarks is empty"))
        }
        if (it.dateTimeReported!!.isEmpty()){
            validationData.add(ValidationResponse("Failed", "Date reported is empty"))
        }
        if (it.reportedMethod.toString().isEmpty()){
            validationData.add(ValidationResponse("Failed", "Date reported is empty"))
        }
        if (it.companyArea.toString().isEmpty()){
            validationData.add(ValidationResponse("Failed", "Date reported is empty"))
        }
        if (it.insertedBy.toString().isEmpty()){
            validationData.add(ValidationResponse("Failed", "Date reported is empty"))
        }
    }
    return validationData
}

// storage for getting ticket list
@Serializable
data class GetTicketBanks(
    val recordNum: Int,
    val ticketNum: String,
    val ticketStatusId: Int ?,
    val ticketStatus: String ?,
    val ticketCreated: String,
    val requesterRemarks: String,
    val reportedById: String,
    val reportedByName: String,
    val actualToSupportId: String,
    val actualToSupportName: String,
    val actualToSupportPosition: String,
    val actualToSupportEmail: String,
    val subCategory: String ?,
    val companyArea: String,
    val companyAreaId: Int,
    val companyFloorBanks: String,
    val companyFloorId: Int,
    val categoryName: String,
    val subCategoryName: String,
    val subCategoryRestCount: Int,
    val reportedMethodId: Int,
    val reportedMethod: String,
    val dateTimeReported: String,
    val insertedBy: String,
    val assignedSupportId: String ?,
    val assignedSupportName: String ?,
    val assignedSupportEmail: String ?,
    val coSupportIdList: String ?,
    val supportAssignedBy: String ?,
    val dateSupportAssigned: String ?,
    val lastUpdatedById: String ?,
    val lastUpdatedByName: String ?,
    val lastDateUpdated: String ?,
    val toClose: String ?,
    val exclusiveToTeam: Int,
    val lockedTo: String ?,
    val expirationDate: String ?
): Destroyable

@Serializable
data class GetTicketBanksForQueue(
    val recordNum: Int,
    val ticketNum: String,
    val ticketStatusId: Int ?,
    val ticketStatus: String ?,
    val dateTimeTicketCreated: String,
    val requesterRemarks: String,
    val reportedById: String,
    val reportedByName: String,
    val actualToSupportId: String,
    val actualToSupportName: String,
    val actualToSupportPosition: String,
    val actualToSupportEmail: String,
    val subCategory: String ?,
    val companyArea: String,
    val companyAreaId: Int,
    val companyFloorBanks: String,
    val companyFloorId: Int,
    val categoryName: String,
    val subCategoryName: String,
    val subCategoryRestCount: Int,
    val reportedMethodId: Int,
    val reportedMethod: String,
    val dateTimeReported: String,
    val insertedBy: String,
    val assignedSupportId: String ?,
    val assignedSupportName: String ?,
    val assignedSupportEmail: String ?,
    val coSupportIdList: String ?,
    val supportAssignedBy: String ?,
    val dateSupportAssigned: String ?,
    val lastUpdatedById: String ?,
    val lastUpdatedByName: String ?,
    val dateTimeLastUpdated: String ?,
    val dateTimeToAutoClose: String ?,
    val exclusiveToTeam: Int,
    val lockedTo: String ?,
    val expirationDate: String ?
): Destroyable

@Serializable
data class GetTicketBanksForQueueReports(
    val ticketNum: String,
    val ticketStatus: String ?,
    val dateTimeTicketCreated: String,
    val reportedByName: String,
    val actualToSupportName: String,
    val actualToSupportPosition: String,
    val companyArea: String,
    val companyFloorBanks: String,
    val categoryName: String,
    val subCategoryName: String,
    val reportedMethod: String,
    val dateTimeReported: String,
    val assignedSupportName: String ?,
    val dateSupportAssigned: String ?,
    val lastUpdatedByName: String ?,
    val dateTimeLastUpdated: String ?,
    val dateTimeResolved: String ?,
    val dateTimeToAutoClose: String ?,
    val exclusiveToTeam: Int,
): Destroyable

// model for getting ticket to close
@Serializable
data class GetTicketDetailsClosed(
    val recordNum: Int,
    val ticketNum: String,
    val statusId: Int,
    val ticketStatus: String,
    val subCategory: String ?,
    val coSupport: String,
    val lastDateUpdated: String ?,
    val location: String,
    val description: String,
    val reportedBy: String,
    val recipientBy: String,
    val recipientId: String,
    val dateReported: String,
    val timeReported: String,
    val ticketCreated: String,
    val reportedVia: String,
    val supportedBy: String,
    val supportById: String ?,
    val supportEmail: String
)

@Serializable
data class GetActualTicketBanks(
    val referenceId: String,
    val recordLastUpdatedBy: String ?,
    val recordLastUpdatedDateTime: String ?,
    val ticketStatus: String,
    val ticketStatusId: Int,
    val ticketRemarks: Array<String ?> ?,
    val remarksConcat: String,
    val dateInserted: String,
    val lockedToUser: String ?,
    val lockedToUSerNAme: String ?,
    val lockedToExpiredAt: String ?,
    val isEditable: Boolean
): Destroyable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GetActualTicketBanks

        return ticketRemarks.contentEquals(other.ticketRemarks)
    }

    override fun hashCode(): Int {
        return ticketRemarks.contentHashCode()
    }
}

// updating ticket support personnel model
@Serializable
data class UpdateTicketAssignPersonnel(
    val coSupportIdList: MutableList<String> ,
    val assignedPersonnelId: String,
    val remarks: String ?,
    val lastUpdatedBy: String ?,
    val lastSupportPersonnel: String ?
): Destroyable

// adding action taken logs model and adding co support
@Serializable
data class ActionTakenLogs(
    val coSupportIdList: MutableList<String> ?,
    val actionTaken: String ?,
    val lastUpdatedById: String,
    val lastSupportPersonnel: String ?,
    val toSupportEmail : String ?,
    val toSupportName: String ?,
    val assignedSupport: String ?,
    val ticketNumber : String ?,
    val ticketCreated : String ?,
    val description : String ?,
    val issue : MutableList<CategoryName> ? ,
    val teamActive: Int ?,
    val toSendEmail: Boolean
): Destroyable

@Serializable
data class CloseTicket(
    val actionTaken: String ?,
    val lastUpdatedById: String,
    val lastSupportPersonnel: String ?,
    val toSupportEmail : String ?,
    val toSupportName: String ?,
    val assignedSupport: String ?,
    val ticketNumber : String ?,
    val ticketCreated : String ?,
    val description : String ?,
    val issue : MutableList<CategoryName> ? ,
    val employeeId: String ?,
    val toSendEmail: Boolean
): Destroyable

@Serializable
data class CategoryName(
    val categoryName: String ?,
    val subCategoryName: Array<String> ?
):Destroyable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CategoryName

        if (subCategoryName != null) {
            if (other.subCategoryName == null) return false
            if (!subCategoryName.contentEquals(other.subCategoryName)) return false
        } else if (other.subCategoryName != null) return false

        return true
    }

    override fun hashCode(): Int {
        return subCategoryName?.contentHashCode() ?: 0
    }
}

// storage for counting all issue in every ticket
@Serializable
data class GetCategoryCounts(
    val categoryName: String,
    val subCategoryName: String,
    val restCounts: Int
): Destroyable

// storage for getting ticket status list
@Serializable
data class TicketStatus(
    val ticketStatusId: Int,
    val ticketStatus: String,
    val foregroundColor: String,
    val backgroundColor: String
): Destroyable

// storage for getting sub and category list
@Serializable
data class GetSubAndCategory(
    val category: String,
    val subCategory: MutableList<SubCategoryBanks>
): Destroyable

// get system module
@Serializable
data class SystemModule(
    val moduleId: Int,
    val moduleName: String
)

// model for validating starts here
@Serializable
data class SystemKeyCrew(
    val recordId: Int,
    val systemName: String,
    val systemKey: String,
    val expirationDate: String,
    val active: Int,
    val addedBy: String? = null,
    val dateAdded: String
):Destroyable

@Serializable
data class GetKeys(
    val keyName: String,
    val keyValue: String,
):Destroyable

@Serializable
data class UserValidation(
    val userId: String,
    val accessTokenEnc: String
):Destroyable

// model for validating ends here
@Serializable
data class SystemModulePost(
    val employee: Boolean,
    val partners: Boolean,
    val crew: Boolean
): Destroyable

// all model that used for Generic response and other functions are starts here
@Serializable
data class GenericResponse<out S>(
    val text: String = "",
    val code: Int = 0,
    val data: S
): Destroyable

@Serializable
data class GenericResponseForQueue<out D, out R>(
    val text: String = "",
    val code: Int = 0,
    val data: D,
    val reports: R
): Destroyable

@Serializable
data class GenericResponseHistory<out S, out B, out A>(
    val text: String = "",
    val code: Int = 0,
    val category: S,
    val logs: B,
    val coSupportList: A
): Destroyable

@Serializable
data class GenericResponseAll<out S, out B, out A, out Z>(
    val text: String = "",
    val code: Int = 0,
    val data : Z,
    val category: S,
    val logs: B,
    val coSupportList: A
)

@Serializable
data class ValidationResponse(
    val text: String,
    val validationResult: String
)

//this model is for date and time and the rest
@Serializable
data class DateTimeSeparator(
    val date: String,
    val time: String
): Destroyable

@Serializable
data class DateComponents(
    val year: Int,
    val month: Int,
    val day: Int
): Destroyable

@Serializable
data class TimeComponents(
    val hours: Int,
    val minutes: Int,
    val seconds: Int
): Destroyable