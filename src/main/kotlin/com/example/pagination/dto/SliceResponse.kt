package com.example.pagination.dto

import org.springframework.data.domain.Slice

data class SliceResponse<T:Any>(
    val content: List<T>,
    val slice: SliceMetadata
) {
    data class SliceMetadata(
        val size: Int,
        val number: Int,
        val hasNext: Boolean,
        val hasPrevious: Boolean
    )

    companion object {
        fun <T:Any> from(slice: Slice<T>): SliceResponse<T> {
            return SliceResponse(
                content = slice.content,
                slice = SliceMetadata(
                    size = slice.size,
                    number = slice.number,
                    hasNext = slice.hasNext(),
                    hasPrevious = slice.hasPrevious()
                )
            )
        }
    }
}

