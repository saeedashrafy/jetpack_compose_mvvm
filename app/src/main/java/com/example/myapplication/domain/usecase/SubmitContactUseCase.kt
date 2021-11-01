package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.entity.Contact
import com.example.myapplication.domain.repository.ContactRepository

class SubmitContactUseCase(private val contactRepository: ContactRepository) {
    suspend operator fun invoke(contact: Contact) = contactRepository.submitContact(contact)
}