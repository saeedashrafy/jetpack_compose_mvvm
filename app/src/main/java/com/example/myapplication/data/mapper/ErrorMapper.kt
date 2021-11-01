package com.example.myapplication.data.mapper


import com.example.myapplication.domain.entity.Failure
import com.example.myapplication.domain.entity.ServerError

object ErrorMapper {

    fun getError(statusCode: Int?): Failure {
        return when (statusCode) {
            400 -> ServerError.AuthError.InvalidCredential
            else -> ServerError.AuthError.TokenExpired
        }
    }
}