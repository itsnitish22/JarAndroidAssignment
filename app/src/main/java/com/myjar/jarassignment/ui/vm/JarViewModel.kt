package com.myjar.jarassignment.ui.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.myjar.jarassignment.createRetrofit
import com.myjar.jarassignment.data.model.ComputerItem
import com.myjar.jarassignment.data.model.MainPhoneListResponse
import com.myjar.jarassignment.data.repository.JarRepository
import com.myjar.jarassignment.data.repository.JarRepositoryImpl
import com.myjar.jarassignment.utils.SingleEvent
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

    private val _startFetchingFromDB:  MutableLiveData<SingleEvent<Boolean>> = MutableLiveData()
    val startFetchingFromDB: LiveData<SingleEvent<Boolean>>
        get() = _startFetchingFromDB

    private val repository: JarRepository = JarRepositoryImpl(createRetrofit())

    fun fetchData() {
        viewModelScope.launch {
            try {
                val response = repository.fetchResults()
                if (response.isNotEmpty()) {
                    _listStringData.emit(response)
                }
            } catch (e: Exception) {
                _startFetchingFromDB.postValue(SingleEvent(true))
                Log.e("ViewModel", "Something went wrong!")
            }
        }
    }

    suspend fun getQuestionnaireDataFromDB(realmDB: Realm): MainPhoneListResponse? {
        return try {
            val localResponse = realmDB.where(MainPhoneListResponse::class.java).findFirst()
            val jsonString = localResponse?.computerItemData.toString().trim()
            val computerItemList = Gson().fromJson<List<ComputerItem>>(
                jsonString,
                object : TypeToken<List<ComputerItem>>() {}.type
            )
            _listStringData.emit(computerItemList)
            localResponse
        } catch (exc: Exception) {
            Log.e("ViewModelE", exc.message.toString())
            null
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