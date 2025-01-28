package com.myjar.jarassignment

import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.myjar.jarassignment.data.database.RealmModule
import com.myjar.jarassignment.data.model.ComputerItem
import com.myjar.jarassignment.ui.adapter.ItemAdapter
import com.myjar.jarassignment.ui.vm.JarViewModel
import com.myjar.jarassignment.utils.EventObserver
import io.realm.Realm
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<JarViewModel>()
    private lateinit var realmDB: Realm
    private lateinit var adapter: ListAdapter<ComputerItem, *>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initDB()
        setupUi()
        observeFlows()
    }

    private fun initDB() {
        realmDB = RealmModule.provideRealmInstance(applicationContext)
    }

    private fun observeFlows() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.listStringData.collectLatest {
                    if (it.isNotEmpty())
                        viewModel.saveListToDB(realmDB, it)
                    adapter.submitList(it)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.navigateToItem.filterNotNull().collectLatest {
                    val intent = Intent(this@MainActivity, DetailActivity::class.java)
                    intent.putExtra("itemId", it)
                    startActivity(intent)
                }
            }
        }


        viewModel.startFetchingFromDB.observe(this, EventObserver {
            it?.let { startFetchingFromDB ->
                if (startFetchingFromDB) {
                    lifecycleScope.launch {
                        viewModel.getQuestionnaireDataFromDB(realmDB)
                    }
                }
            }
        })
    }

    private fun setupUi() {
        val recyclerView: RecyclerView = findViewById(R.id.item_list)
        val searchView: SearchView = findViewById(R.id.search_view)

        adapter = ItemAdapter { selectedItem ->
            viewModel.navigateToItemDetail(selectedItem.id)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterItems(newText.orEmpty())
                return true
            }
        })
    }

    private fun filterItems(query: String) {
        lifecycleScope.launch {
            val allItems = viewModel.listStringData.value
            val filteredList = if (query.isNotEmpty()) {
                allItems.filter {
                    it.name.contains(query, ignoreCase = true)
                }
            } else {
                allItems
            }
            adapter.submitList(filteredList)
        }
    }


    override fun onResume() {
        super.onResume()
        viewModel.fetchData()
    }
}