package com.example.myapplication.data.mapper

import com.example.myapplication.core.Mapper
import com.example.myapplication.data.remote.model.ContactResponse
import com.example.myapplication.domain.entity.ContactMessage

class ContactResponseToDomainMapper : Mapper<ContactResponse, ContactMessage> {
    override fun invoke(response: ContactResponse): ContactMessage {
        return ContactMessage(response.description)
    }
}