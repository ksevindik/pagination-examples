package com.example.pagination.dto

data class CursorResponse<T : Any>(
    val content: List<T>,
    val cursor: CursorMetadata
) {
    data class CursorMetadata(
        val size: Int,
        val nextCursor: String?,
        val hasNext: Boolean
    )

    companion object {
        fun <T : Any> from(
            content: List<T>,
            size: Int,
            nextCursor: String?,
            hasNext: Boolean
        ): CursorResponse<T> {
            return CursorResponse(
                content = content,
                cursor = CursorMetadata(
                    size = size,
                    nextCursor = nextCursor,
                    hasNext = hasNext
                )
            )
        }
    }
}

