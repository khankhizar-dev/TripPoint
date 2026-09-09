package com.android.trippoint.core.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class ChecklistRemoteDataSource(
    private val api: TripPointApi
) {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    // 1. Get all checklists for a trip
    suspend fun getChecklists(tripId: String): List<ChecklistDto> {
        val query = """
            query GetChecklists(${'$'}tripId: ID!) {
                checklists(tripId: ${'$'}tripId) {
                    id tripId createdBy name description status 
                    totalItems completedItems progress createdAt updatedAt
                }
            }
        """.trimIndent()
        val variables = mapOf("tripId" to tripId)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("checklists") as? List<*> ?: return emptyList()
        val adapter = moshi.adapter(ChecklistDto::class.java)
        return data.mapNotNull { adapter.fromJsonValue(it) }
    }

    // 2. Get a specific checklist
    suspend fun getChecklist(tripId: String, checklistId: String): ChecklistDto? {
        val query = """
            query GetChecklist(${'$'}tripId: ID!, ${'$'}checklistId: ID!) {
                checklist(tripId: ${'$'}tripId, checklistId: ${'$'}checklistId) {
                    id tripId createdBy name description status totalItems completedItems progress
                    sections {
                        id checklistId name position totalItems completedItems progress
                        items {
                            id sectionId createdBy name category essential completed dueDate position createdAt updatedAt
                        }
                        createdAt updatedAt
                    }
                    createdAt updatedAt
                }
            }
        """.trimIndent()
        val variables = mapOf("tripId" to tripId, "checklistId" to checklistId)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("checklist") ?: return null
        return moshi.adapter(ChecklistDto::class.java).fromJsonValue(data)
    }

    // 3. Create Checklist
    suspend fun createChecklist(tripId: String, input: CreateChecklistInput): ChecklistDto? {
        val query = """
            mutation CreateChecklist(${'$'}tripId: ID!, ${'$'}input: CreateChecklistInput!) {
                createChecklist(tripId: ${'$'}tripId, input: ${'$'}input) {
                    id tripId createdBy name description status totalItems completedItems progress
                    createdAt updatedAt
                }
            }
        """.trimIndent()
        val variables = mapOf("tripId" to tripId, "input" to input)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("createChecklist") ?: return null
        return moshi.adapter(ChecklistDto::class.java).fromJsonValue(data)
    }

    // 4. Update Checklist
    suspend fun updateChecklist(tripId: String, checklistId: String, input: UpdateChecklistInput): ChecklistDto? {
        val query = """
            mutation UpdateChecklist(${'$'}tripId: ID!, ${'$'}checklistId: ID!, ${'$'}input: UpdateChecklistInput!) {
                updateChecklist(tripId: ${'$'}tripId, checklistId: ${'$'}checklistId, input: ${'$'}input) {
                    id name description status totalItems completedItems progress updatedAt
                }
            }
        """.trimIndent()
        val variables = mapOf("tripId" to tripId, "checklistId" to checklistId, "input" to input)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("updateChecklist") ?: return null
        return moshi.adapter(ChecklistDto::class.java).fromJsonValue(data)
    }

    // 5. Archive Checklist
    suspend fun archiveChecklist(tripId: String, checklistId: String): ChecklistDto? {
        val query = """
            mutation ArchiveChecklist(${'$'}tripId: ID!, ${'$'}checklistId: ID!) {
                archiveChecklist(tripId: ${'$'}tripId, checklistId: ${'$'}checklistId) {
                    id name status updatedAt
                }
            }
        """.trimIndent()
        val variables = mapOf("tripId" to tripId, "checklistId" to checklistId)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("archiveChecklist") ?: return null
        return moshi.adapter(ChecklistDto::class.java).fromJsonValue(data)
    }

    // 6. Create Checklist Section
    @Suppress("LongMethod")
    suspend fun createChecklistSection(
        tripId: String, 
        checklistId: String, 
        input: CreateChecklistSectionInput
    ): ChecklistSectionDto? {
        val query = """
            mutation CreateChecklistSection(${'$'}tripId: ID!, ${'$'}checklistId: ID!, ${'$'}input: CreateChecklistSectionInput!) {
                createChecklistSection(tripId: ${'$'}tripId, checklistId: ${'$'}checklistId, input: ${'$'}input) {
                    id checklistId name position totalItems completedItems progress
                    createdAt updatedAt
                }
            }
        """.trimIndent()
        val variables = mapOf("tripId" to tripId, "checklistId" to checklistId, "input" to input)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("createChecklistSection") ?: return null
        return moshi.adapter(ChecklistSectionDto::class.java).fromJsonValue(data)
    }

    // 7. Update Checklist Section
    suspend fun updateChecklistSection(
        tripId: String, 
        checklistId: String, 
        sectionId: String, 
        input: UpdateChecklistSectionInput
    ): ChecklistSectionDto? {
        val query = """
            mutation UpdateChecklistSection(${'$'}tripId: ID!, ${'$'}checklistId: ID!, ${'$'}sectionId: ID!, ${'$'}input: UpdateChecklistSectionInput!) {
                updateChecklistSection(tripId: ${'$'}tripId, checklistId: ${'$'}checklistId, sectionId: ${'$'}sectionId, input: ${'$'}input) {
                    id checklistId name position totalItems completedItems progress
                }
            }
        """.trimIndent()
        val variables = mapOf(
            "tripId" to tripId, 
            "checklistId" to checklistId, 
            "sectionId" to sectionId, 
            "input" to input
        )
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("updateChecklistSection") ?: return null
        return moshi.adapter(ChecklistSectionDto::class.java).fromJsonValue(data)
    }

    // 8. Delete Checklist Section
    suspend fun deleteChecklistSection(tripId: String, checklistId: String, sectionId: String): Boolean {
        val query = """
            mutation DeleteChecklistSection(${'$'}tripId: ID!, ${'$'}checklistId: ID!, ${'$'}sectionId: ID!) {
                deleteChecklistSection(tripId: ${'$'}tripId, checklistId: ${'$'}checklistId, sectionId: ${'$'}sectionId)
            }
        """.trimIndent()
        val variables = mapOf("tripId" to tripId, "checklistId" to checklistId, "sectionId" to sectionId)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        return response.body()?.data?.get("deleteChecklistSection") as? Boolean ?: false
    }

    // 9. Add Checklist Item
    suspend fun addChecklistItem(
        tripId: String, 
        checklistId: String, 
        sectionId: String, 
        input: CreateChecklistItemInput
    ): ChecklistItemDto? {
        val query = """
            mutation AddChecklistItem(${'$'}tripId: ID!, ${'$'}checklistId: ID!, ${'$'}sectionId: ID!, ${'$'}input: CreateChecklistItemInput!) {
                addChecklistItem(tripId: ${'$'}tripId, checklistId: ${'$'}checklistId, sectionId: ${'$'}sectionId, input: ${'$'}input) {
                    id sectionId createdBy name category essential completed dueDate position createdAt updatedAt
                }
            }
        """.trimIndent()
        val variables = mapOf(
            "tripId" to tripId, 
            "checklistId" to checklistId, 
            "sectionId" to sectionId, 
            "input" to input
        )
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("addChecklistItem") ?: return null
        return moshi.adapter(ChecklistItemDto::class.java).fromJsonValue(data)
    }

    // 10. Update Checklist Item
    suspend fun updateChecklistItem(
        tripId: String, 
        checklistId: String, 
        sectionId: String, 
        itemId: String, 
        input: UpdateChecklistItemInput
    ): ChecklistItemDto? {
        val query = """
            mutation UpdateChecklistItem(${'$'}tripId: ID!, ${'$'}checklistId: ID!, ${'$'}sectionId: ID!, ${'$'}itemId: ID!, ${'$'}input: UpdateChecklistItemInput!) {
                updateChecklistItem(tripId: ${'$'}tripId, checklistId: ${'$'}checklistId, sectionId: ${'$'}sectionId, itemId: ${'$'}itemId, input: ${'$'}input) {
                    id sectionId name category essential completed dueDate position updatedAt
                }
            }
        """.trimIndent()
        val variables = mapOf(
            "tripId" to tripId, 
            "checklistId" to checklistId, 
            "sectionId" to sectionId, 
            "itemId" to itemId, 
            "input" to input
        )
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("updateChecklistItem") ?: return null
        return moshi.adapter(ChecklistItemDto::class.java).fromJsonValue(data)
    }
}

data class ChecklistDto(
    val id: String,
    val tripId: String,
    val createdBy: String,
    val name: String,
    val description: String?,
    val status: String,
    val totalItems: Int,
    val completedItems: Int,
    val progress: Int,
    val sections: List<ChecklistSectionDto>? = null,
    val createdAt: String,
    val updatedAt: String
)

data class ChecklistSectionDto(
    val id: String,
    val checklistId: String,
    val name: String,
    val position: Int,
    val totalItems: Int,
    val completedItems: Int,
    val progress: Int,
    val items: List<ChecklistItemDto>? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class ChecklistItemDto(
    val id: String,
    val sectionId: String,
    val createdBy: String? = null,
    val name: String,
    val category: String,
    val essential: Boolean,
    val completed: Boolean,
    val dueDate: String?,
    val position: Int,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class CreateChecklistInput(
    val name: String,
    val description: String? = null
)

data class UpdateChecklistInput(
    val name: String? = null,
    val description: String? = null,
    val status: String? = null
)

data class CreateChecklistSectionInput(
    val name: String,
    val position: Int? = null
)

data class UpdateChecklistSectionInput(
    val name: String? = null,
    val position: Int? = null
)

data class CreateChecklistItemInput(
    val name: String,
    val category: String,
    val essential: Boolean,
    val dueDate: String? = null,
    val position: Int? = null
)

data class UpdateChecklistItemInput(
    val name: String? = null,
    val category: String? = null,
    val essential: Boolean? = null,
    val completed: Boolean? = null,
    val dueDate: String? = null,
    val position: Int? = null
)
