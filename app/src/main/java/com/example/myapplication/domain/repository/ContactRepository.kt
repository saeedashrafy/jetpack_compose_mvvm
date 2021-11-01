package com.example.myapplication.domain.repository

import com.example.myapplication.domain.entity.Contact
import com.example.myapplication.domain.entity.ContactMessage
import com.example.myapplication.domain.entity.DomainResult
import kotlinx.coroutines.flow.Flow

interface ContactRepository {

    suspend fun submitContact(contact: Contact): Flow<DomainResult<ContactMessage>>
}