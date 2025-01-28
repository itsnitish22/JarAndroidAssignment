package com.myjar.jarassignment.ui.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.myjar.jarassignment.createRetrofit
import com.myjar.jarassignment.data.model.ComputerItem
import com.myjar.jarassignment.data.model.MainPhoneListResponse
import com.myjar.jarassignment.data.repository.JarRepository
import com.myjar.jarassignment.data.repository.JarRepositoryImpl
import io.realm.Realm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class JarViewModel : ViewModel() {

    private val _listStringData = MutableStateFlow<List<ComputerItem>>(emptyList())
    val listStringData: StateFlow<List<ComputerItem>>
        get() = _listStringData

    private val _navigateToItem = MutableStateFlow<String?>(null)
    val navigateToItem: StateFlow<String?>
        get() = _navigateToItem

    private val repository: JarRepository = JarRepositoryImpl(createRetrofit())

    fun fetchData() {
        viewModelScope.launch {
            val response = repository.fetchResults()
            if (response.isNotEmpty()) {
                _listStringData.emit(response)
            }
        }
    }

    fun saveListToDB(realmDB: Realm, response: List<ComputerItem>) {
        try {
            realmDB.executeTransaction {
                it.delete(MainPhoneListResponse::class.java)
                it.insertOrUpdate(
                    MainPhoneListResponse(
                        computerItemData = Gson().toJson(
                            response
                        )
                    )
                )
            }
            Log.d("ViewModel", "DBStoredSuccess")
        } catch (e: Exception) {
            Log.e("ViewModel", "${e.message}")
        }
    }

    fun navigateToItemDetail(id: String) {
        viewModelScope.launch {
            _navigateToItem.emit(id)
        }
    }

}