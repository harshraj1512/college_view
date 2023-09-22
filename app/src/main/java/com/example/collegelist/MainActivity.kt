
package com.example.collegelist
import UniversityAdapter
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsService
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
//import com.android.car.ui.toolbar.MenuItem
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

    private var originalUniversities: MutableList<University> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //val toolbar = findViewById<Toolbar>(R.id.toolbar)
       // setSupportActionBar(toolbar)

        universityRecyclerView = findViewById(R.id.universityRecyclerView)

        // Initialize the adapter before calling setupRecyclerView
        adapter = UniversityAdapter(mutableListOf()) { url ->
            // Handle item click here
            openWebsiteInChromeCustomTab(url)
        }

        setupRecyclerView()


        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(this)
        fetchAllUniversities()

        // Start the DataRefreshService
        val serviceIntent = Intent(this, DataRefreshService::class.java)
        startService(serviceIntent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                // Manually trigger data refresh
                refreshData()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun fetchAllUniversities() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // Make the API request to fetch all universities
                val universities = UniversityApi.service.getAllUniversities()

                withContext(Dispatchers.Main) {
                    originalUniversities.addAll(universities)
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

        // Initialize the adapter
        adapter = UniversityAdapter(mutableListOf()) { url ->
            // Handle item click here
            openWebsiteInChromeCustomTab(url)
        }

        universityRecyclerView.adapter = adapter
    }

    private fun refreshData() {
        // Manually trigger data refresh
        adapter.updateUniversities(mutableListOf()) // Clear the list
        fetchAllUniversities()
    }



    private fun openWebsiteInChromeCustomTab(url: String?) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
    }




    override fun onQueryTextSubmit(query: String?): Boolean {
        if (!query.isNullOrEmpty()) {
            val filteredUniversities = originalUniversities.filter { university ->
                university.name.contains(query, ignoreCase = true)
            }.toMutableList()

            // Update the adapter with the filtered data
            adapter.updateUniversities(filteredUniversities)
        } else {
            // If the query is empty, show the original list
            adapter.updateUniversities(originalUniversities)
        }

        // Return true to indicate that the query has been handled.
        return true
    }


    override fun onQueryTextChange(newText: String?): Boolean {
        val filteredUniversities = originalUniversities.filter { university ->
            university.name.contains(newText?:" ", ignoreCase = true)
        }.toMutableList()

        // Update the RecyclerView adapter with the filtered data
        adapter.updateUniversities(filteredUniversities)

        return true
    }


}
