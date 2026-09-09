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

    // 11. Complete / Uncomplete Checklist Item
    suspend fun completeChecklistItem(
        tripId: String,
        checklistId: String,
        sectionId: String,
        itemId: String,
        completed: Boolean
    ): ChecklistItemDto? {
        val query = """
            mutation CompleteChecklistItem(${'$'}tripId: ID!, ${'$'}checklistId: ID!, ${'$'}sectionId: ID!, ${'$'}itemId: ID!, ${'$'}completed: Boolean!) {
                completeChecklistItem(tripId: ${'$'}tripId, checklistId: ${'$'}checklistId, sectionId: ${'$'}sectionId, itemId: ${'$'}itemId, completed: ${'$'}completed) {
                    id name completed category essential dueDate position updatedAt
                }
            }
        """.trimIndent()
        val variables = mapOf(
            "tripId" to tripId,
            "checklistId" to checklistId,
            "sectionId" to sectionId,
            "itemId" to itemId,
            "completed" to completed
        )
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("completeChecklistItem") ?: return null
        return moshi.adapter(ChecklistItemDto::class.java).fromJsonValue(data)
    }

    // 12. Delete Checklist Item
    suspend fun deleteChecklistItem(tripId: String, checklistId: String, sectionId: String, itemId: String): Boolean {
        val query = """
            mutation DeleteChecklistItem(${'$'}tripId: ID!, ${'$'}checklistId: ID!, ${'$'}sectionId: ID!, ${'$'}itemId: ID!) {
                deleteChecklistItem(tripId: ${'$'}tripId, checklistId: ${'$'}checklistId, sectionId: ${'$'}sectionId, itemId: ${'$'}itemId)
            }
        """.trimIndent()
        val variables = mapOf(
            "tripId" to tripId,
            "checklistId" to checklistId,
            "sectionId" to sectionId,
            "itemId" to itemId
        )
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        return response.body()?.data?.get("deleteChecklistItem") as? Boolean ?: false
    }

    // 14. Get Templates
    suspend fun getChecklistTemplates(): List<ChecklistTemplateDto> {
        val query = """
            query GetChecklistTemplates {
                checklistTemplates {
                    id createdBy name description type status
                    sections {
                        id templateId name position
                        items {
                            id sectionId name category essential position
                        }
                    }
                    createdAt updatedAt
                }
            }
        """.trimIndent()
        val request = GraphQlRequest(query = query)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("checklistTemplates") as? List<*> ?: return emptyList()
        val adapter = moshi.adapter(ChecklistTemplateDto::class.java)
        return data.mapNotNull { adapter.fromJsonValue(it) }
    }

    // 15. Get Specific Template
    suspend fun getChecklistTemplate(templateId: String): ChecklistTemplateDto? {
        val query = """
            query GetChecklistTemplate(${'$'}templateId: ID!) {
                checklistTemplate(templateId: ${'$'}templateId) {
                    id createdBy name description type status
                    sections {
                        id templateId name position
                        items {
                            id sectionId name category essential position
                        }
                        createdAt updatedAt
                    }
                    createdAt updatedAt
                }
            }
        """.trimIndent()
        val variables = mapOf("templateId" to templateId)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("checklistTemplate") ?: return null
        return moshi.adapter(ChecklistTemplateDto::class.java).fromJsonValue(data)
    }

    // 16. Create User Template
    suspend fun createChecklistTemplate(input: CreateChecklistTemplateInput): ChecklistTemplateDto? {
        val query = """
            mutation CreateChecklistTemplate(${'$'}input: CreateChecklistTemplateInput!) {
                createChecklistTemplate(input: ${'$'}input) {
                    id createdBy name description type status
                    createdAt updatedAt
                }
            }
        """.trimIndent()
        val variables = mapOf("input" to input)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("createChecklistTemplate") ?: return null
        return moshi.adapter(ChecklistTemplateDto::class.java).fromJsonValue(data)
    }

    // 17. Update Template
    suspend fun updateChecklistTemplate(
        templateId: String, 
        input: UpdateChecklistTemplateInput
    ): ChecklistTemplateDto? {
        val query = """
            mutation UpdateChecklistTemplate(${'$'}templateId: ID!, ${'$'}input: UpdateChecklistTemplateInput!) {
                updateChecklistTemplate(templateId: ${'$'}templateId, input: ${'$'}input) {
                    id name description type status updatedAt
                }
            }
        """.trimIndent()
        val variables = mapOf("templateId" to templateId, "input" to input)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("updateChecklistTemplate") ?: return null
        return moshi.adapter(ChecklistTemplateDto::class.java).fromJsonValue(data)
    }

    // 18. Archive Template
    suspend fun archiveChecklistTemplate(templateId: String): ChecklistTemplateDto? {
        val query = """
            mutation ArchiveChecklistTemplate(${'$'}templateId: ID!) {
                archiveChecklistTemplate(templateId: ${'$'}templateId) {
                    id name type status updatedAt
                }
            }
        """.trimIndent()
        val variables = mapOf("templateId" to templateId)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("archiveChecklistTemplate") ?: return null
        return moshi.adapter(ChecklistTemplateDto::class.java).fromJsonValue(data)
    }

    // 19. Create Template Section
    suspend fun createChecklistTemplateSection(
        templateId: String, 
        input: CreateChecklistTemplateSectionInput
    ): ChecklistTemplateSectionDto? {
        val query = """
            mutation CreateChecklistTemplateSection(${'$'}templateId: ID!, ${'$'}input: CreateChecklistTemplateSectionInput!) {
                createChecklistTemplateSection(templateId: ${'$'}templateId, input: ${'$'}input) {
                    id templateId name position
                }
            }
        """.trimIndent()
        val variables = mapOf("templateId" to templateId, "input" to input)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("createChecklistTemplateSection") ?: return null
        return moshi.adapter(ChecklistTemplateSectionDto::class.java).fromJsonValue(data)
    }

    // 20. Update Template Section
    suspend fun updateChecklistTemplateSection(
        templateId: String, 
        sectionId: String, 
        input: UpdateChecklistTemplateSectionInput
    ): ChecklistTemplateSectionDto? {
        val query = """
            mutation UpdateChecklistTemplateSection(${'$'}templateId: ID!, ${'$'}sectionId: ID!, ${'$'}input: UpdateChecklistTemplateSectionInput!) {
                updateChecklistTemplateSection(templateId: ${'$'}templateId, sectionId: ${'$'}sectionId, input: ${'$'}input) {
                    id templateId name position
                }
            }
        """.trimIndent()
        val variables = mapOf(
            "templateId" to templateId, 
            "sectionId" to sectionId, 
            "input" to input
        )
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("updateChecklistTemplateSection") ?: return null
        return moshi.adapter(ChecklistTemplateSectionDto::class.java).fromJsonValue(data)
    }

    // 21. Delete Template Section
    suspend fun deleteChecklistTemplateSection(templateId: String, sectionId: String): Boolean {
        val query = """
            mutation DeleteChecklistTemplateSection(${'$'}templateId: ID!, ${'$'}sectionId: ID!) {
                deleteChecklistTemplateSection(templateId: ${'$'}templateId, sectionId: ${'$'}sectionId)
            }
        """.trimIndent()
        val variables = mapOf("templateId" to templateId, "sectionId" to sectionId)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        return response.body()?.data?.get("deleteChecklistTemplateSection") as? Boolean ?: false
    }

    // 22. Add Template Item
    suspend fun addChecklistTemplateItem(
        templateId: String, 
        sectionId: String, 
        input: CreateChecklistTemplateItemInput
    ): ChecklistTemplateItemDto? {
        val query = """
            mutation AddChecklistTemplateItem(${'$'}templateId: ID!, ${'$'}sectionId: ID!, ${'$'}input: CreateChecklistTemplateItemInput!) {
                addChecklistTemplateItem(templateId: ${'$'}templateId, sectionId: ${'$'}sectionId, input: ${'$'}input) {
                    id sectionId name category essential position createdAt updatedAt
                }
            }
        """.trimIndent()
        val variables = mapOf("templateId" to templateId, "sectionId" to sectionId, "input" to input)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("addChecklistTemplateItem") ?: return null
        return moshi.adapter(ChecklistTemplateItemDto::class.java).fromJsonValue(data)
    }

    // 23. Update Template Item
    suspend fun updateChecklistTemplateItem(
        templateId: String, 
        sectionId: String, 
        itemId: String, 
        input: UpdateChecklistTemplateItemInput
    ): ChecklistTemplateItemDto? {
        val query = """
            mutation UpdateChecklistTemplateItem(${'$'}templateId: ID!, ${'$'}sectionId: ID!, ${'$'}itemId: ID!, ${'$'}input: UpdateChecklistTemplateItemInput!) {
                updateChecklistTemplateItem(templateId: ${'$'}templateId, sectionId: ${'$'}sectionId, itemId: ${'$'}itemId, input: ${'$'}input) {
                    id sectionId name category essential position updatedAt
                }
            }
        """.trimIndent()
        val variables = mapOf(
            "templateId" to templateId, 
            "sectionId" to sectionId, 
            "itemId" to itemId, 
            "input" to input
        )
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("updateChecklistTemplateItem") ?: return null
        return moshi.adapter(ChecklistTemplateItemDto::class.java).fromJsonValue(data)
    }

    // 24. Delete Template Item
    suspend fun deleteChecklistTemplateItem(templateId: String, sectionId: String, itemId: String): Boolean {
        val query = """
            mutation DeleteChecklistTemplateItem(${'$'}templateId: ID!, ${'$'}sectionId: ID!, ${'$'}itemId: ID!) {
                deleteChecklistTemplateItem(templateId: ${'$'}templateId, sectionId: ${'$'}sectionId, itemId: ${'$'}itemId)
            }
        """.trimIndent()
        val variables = mapOf("templateId" to templateId, "sectionId" to sectionId, "itemId" to itemId)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        return response.body()?.data?.get("deleteChecklistTemplateItem") as? Boolean ?: false
    }

    // 25. Save Existing Checklist as Template
    suspend fun saveChecklistAsTemplate(
        tripId: String, 
        checklistId: String, 
        input: SaveChecklistAsTemplateInput
    ): ChecklistTemplateDto? {
        val query = """
            mutation SaveChecklistAsTemplate(${'$'}tripId: ID!, ${'$'}checklistId: ID!, ${'$'}input: SaveChecklistAsTemplateInput!) {
                saveChecklistAsTemplate(tripId: ${'$'}tripId, checklistId: ${'$'}checklistId, input: ${'$'}input) {
                    id createdBy name description type status
                    sections {
                        id templateId name position
                        items {
                            id sectionId name category essential position
                        }
                    }
                    createdAt updatedAt
                }
            }
        """.trimIndent()
        val variables = mapOf("tripId" to tripId, "checklistId" to checklistId, "input" to input)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("saveChecklistAsTemplate") ?: return null
        return moshi.adapter(ChecklistTemplateDto::class.java).fromJsonValue(data)
    }

    // 26. Create Checklist From Template
    suspend fun createChecklistFromTemplate(
        tripId: String, 
        templateId: String, 
        input: CreateChecklistFromTemplateInput? = null
    ): ChecklistDto? {
        val query = """
            mutation CreateChecklistFromTemplate(${'$'}tripId: ID!, ${'$'}templateId: ID!, ${'$'}input: CreateChecklistFromTemplateInput) {
                createChecklistFromTemplate(tripId: ${'$'}tripId, templateId: ${'$'}templateId, input: ${'$'}input) {
                    id tripId createdBy name description status totalItems completedItems progress
                    sections {
                        id checklistId name position totalItems completedItems progress
                        items {
                            id sectionId createdBy name category essential completed dueDate position
                        }
                    }
                    createdAt updatedAt
                }
            }
        """.trimIndent()
        val variables = mutableMapOf<String, Any?>("tripId" to tripId, "templateId" to templateId)
        input?.let { variables["input"] = it }
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("createChecklistFromTemplate") ?: return null
        return moshi.adapter(ChecklistDto::class.java).fromJsonValue(data)
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

data class ChecklistTemplateDto(
    val id: String,
    val createdBy: String?,
    val name: String,
    val description: String?,
    val type: String,
    val status: String,
    val sections: List<ChecklistTemplateSectionDto>? = null,
    val createdAt: String,
    val updatedAt: String
)

data class ChecklistTemplateSectionDto(
    val id: String,
    val templateId: String,
    val name: String,
    val position: Int,
    val items: List<ChecklistTemplateItemDto>? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class ChecklistTemplateItemDto(
    val id: String,
    val sectionId: String,
    val name: String,
    val category: String,
    val essential: Boolean,
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

data class CreateChecklistTemplateInput(
    val name: String,
    val description: String? = null
)

data class UpdateChecklistTemplateInput(
    val name: String? = null,
    val description: String? = null,
    val status: String? = null
)

data class CreateChecklistTemplateSectionInput(
    val name: String,
    val position: Int? = null
)

data class UpdateChecklistTemplateSectionInput(
    val name: String? = null,
    val position: Int? = null
)

data class CreateChecklistTemplateItemInput(
    val name: String,
    val category: String,
    val essential: Boolean,
    val position: Int? = null
)

data class UpdateChecklistTemplateItemInput(
    val name: String? = null,
    val category: String? = null,
    val essential: Boolean? = null,
    val position: Int? = null
)

data class SaveChecklistAsTemplateInput(
    val name: String,
    val description: String? = null
)

data class CreateChecklistFromTemplateInput(
    val name: String? = null,
    val description: String? = null
)
