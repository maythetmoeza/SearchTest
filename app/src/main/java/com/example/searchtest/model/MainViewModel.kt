package com.example.searchtest.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

//    val _person = MutableStateFlow(listOf<Person>())
    val _person = MutableStateFlow(allPerson)

    val persons = searchText
//        .debounce(1000)
        .onEach { _isSearching.update { true } }
        .combine(_person){text, persons ->
            if(text.isBlank()){
                persons
            }else{

                delay(1000L)
                persons.filter {
                    it.doesMatchSearchQuery(text)
                }
            }
        }
        .onEach { _isSearching.update { false } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _person.value
        )

    fun onSearchTextChange (text: String){
        _searchText.value = text
    }

}

data class Person(
    val firstName: String,
    val lastName: String
){
    fun doesMatchSearchQuery(query: String): Boolean{
        val matchingCombination = listOf(
            "$firstName$lastName",
            "$firstName $lastName",
            "${firstName.first()} ${lastName.first()}"
        )

        return matchingCombination.any {
            it.contains(query, ignoreCase = true)
        }
    }
}

private val allPerson = listOf(
    Person("May", "Thet"),
    Person("Zar", "Myint"),
    Person("May", "Mee"),
    Person("Mee", "Phyo"),
    Person("Mee", "Kyaw"),
    Person("Thinzar", "Kyaw")
)