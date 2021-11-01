package com.example.myapplication.data.mapper

import com.example.myapplication.core.Mapper
import com.example.myapplication.data.remote.model.ContactBody
import com.example.myapplication.domain.entity.Contact
import com.example.myapplication.domain.entity.ContactMessage

class ContactDomainToBodyMapper : Mapper<Contact, ContactBody> {
    override fun invoke(domain: Contact): ContactBody {
        return ContactBody(
            name = domain.name,
            email = domain.email,
            phone = domain.phone,
            subject = domain.subject,
            body = domain.body,
        )
    }
}