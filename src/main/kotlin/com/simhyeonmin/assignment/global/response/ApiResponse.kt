package com.simhyeonmin.assignment.global.response

// 과제 요건에 따라 필드명만 바꾸면 됩니다. (예: result -> status)
data class ApiResponse<T>(
    val success: Boolean,
    val code: String,
    val message: String? = null,
    val data: T? = null
) {
    companion object {
        fun <T> success(data: T? = null): ApiResponse<T> = 
            ApiResponse(true, "200", "Success", data)
            
        fun error(code: String, message: String): ApiResponse<Nothing> = 
            ApiResponse(false, code, message, null)
    }
}
