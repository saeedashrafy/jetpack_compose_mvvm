package com.example.myapplication.data

import android.util.Log
import com.example.myapplication.core.dispatchers.CoroutineDispatchers
import com.example.myapplication.data.mapper.ContactDomainToBodyMapper
import com.example.myapplication.data.mapper.ContactResponseToDomainMapper
import com.example.myapplication.data.remote.ContactApiService
import com.example.myapplication.data.remote.safeCall
import com.example.myapplication.domain.entity.Contact
import com.example.myapplication.domain.entity.ContactMessage
import com.example.myapplication.domain.entity.DomainResult
import com.example.myapplication.domain.repository.ContactRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ContactRepositoryImpl(
    private val apiService: ContactApiService,
    private val dispatchers: CoroutineDispatchers,
    private val domainToBodyMapper: ContactDomainToBodyMapper,
    private val responseToDomainMapper: ContactResponseToDomainMapper
) : ContactRepository {

    override suspend fun submitContact(contact: Contact): Flow<DomainResult<ContactMessage>> {
        return flow {
            safeCall(dispatcher = dispatchers, responseToDomainMapper) {
                Log.d("TAG",contact.toString())
                apiService.submitContact(domainToBodyMapper(domain = contact))
            }.let {
                emit(it)
            }
        }
    }
}