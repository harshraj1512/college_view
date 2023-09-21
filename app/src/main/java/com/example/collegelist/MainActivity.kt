
package com.example.collegelist
import UniversityAdapter
import android.os.Bundle
import android.view.Menu
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.collegelist.R
import com.example.collegelist.University
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener  {


    private lateinit var universityRecyclerView: RecyclerView
    private lateinit var adapter: UniversityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        universityRecyclerView = findViewById(R.id.universityRecyclerView)
        setupRecyclerView()
        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(this)
        fetchAllUniversities()
    }

    private fun fetchAllUniversities() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // Make the API request to fetch all universities
                val universities = UniversityApi.service.getAllUniversities()

                withContext(Dispatchers.Main) {
                    adapter.addUniversities(universities)
                }
            } catch (e: Exception) {
                // Handle API request error
                e.printStackTrace()
            }
        }
    }

    private fun setupRecyclerView() {
        universityRecyclerView.layoutManager = LinearLayoutManager(this)

        adapter = UniversityAdapter(mutableListOf())
        universityRecyclerView.adapter = adapter
    }


    override fun onQueryTextSubmit(query: String?): Boolean {
        if (!query.isNullOrEmpty()) {
            val filteredUniversities: MutableList<University> = adapter.list().filter { university ->
                university.name.contains(query, ignoreCase = true)
            }.toMutableList()

            // Update the adapter with the filtered data
            adapter.updateUniversities(filteredUniversities)
        } else {
            // If the query is empty, show the original list
            adapter.updateUniversities(adapter.list())
        }

        // Return true to indicate that the query has been handled.
        return true
    }


    override fun onQueryTextChange(newText: String?): Boolean {
        val filteredUniversities = adapter.list().filter { university ->
            university.name.contains(newText?:" ", ignoreCase = true)
        }.toMutableList()

        // Update the RecyclerView adapter with the filtered data
        adapter.updateUniversities(filteredUniversities)

        return true
    }
}
