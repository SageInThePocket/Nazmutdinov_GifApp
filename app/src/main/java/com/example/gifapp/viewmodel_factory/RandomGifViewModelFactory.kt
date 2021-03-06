package com.example.gifapp.viewmodel_factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gifapp.repository.Repository
import com.example.gifapp.viewmodel.RandomGifViewModel

/**
 * Фабрика для создания экземпляра [RandomGifViewModel]
 */
class RandomGifViewModelFactory(
    private val repository: Repository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RandomGifViewModel(repository) as T
    }
}