package com.example.myapplication.domain.repository

import com.example.myapplication.domain.entity.ErrorEntity

interface ErrorHandler {

    fun getError(throwable: Throwable): ErrorEntity
}