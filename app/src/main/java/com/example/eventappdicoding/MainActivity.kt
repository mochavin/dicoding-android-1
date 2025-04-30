package com.example.eventappdicoding

import android.os.Bundle
import android.view.Menu
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.navigation.NavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.eventappdicoding.ui.home.HomeFragmentDirections
import com.example.eventappdicoding.ui.list.ActiveEventsFragmentDirections
import com.example.eventappdicoding.ui.list.FinishedEventsFragmentDirections
import com.example.eventappdicoding.databinding.ActivityMainBinding // ViewBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration // Make it a class property

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the custom Toolbar
        setSupportActionBar(binding.toolbar)

        val navView: BottomNavigationView = binding.navView

        // Get the NavHostFragment using the FragmentManager
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as androidx.navigation.fragment.NavHostFragment
        // Retrieve the NavController from the NavHostFragment
        navController = navHostFragment.navController


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_active_events, R.id.navigation_finished_events,
                R.id.navigation_favorites // Add Favorites ID
            )
        )
        // Setup ActionBar (our Toolbar) with NavController and the updated AppBarConfiguration
        setupActionBarWithNavController(navController, appBarConfiguration)
        // Setup BottomNavigationView with NavController
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        // Ensure navController is initialized before using it (good practice)
        // The appBarConfiguration is now a class property, ensure it's initialized too.
        if (!::navController.isInitialized || !::appBarConfiguration.isInitialized) {
            return super.onSupportNavigateUp()
        }
        // Delegate navigation to the NavController, using the appBarConfiguration.
        // Fall back to the default super implementation if NavController doesn't handle it.
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu) // Inflate the menu

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.queryHint = getString(R.string.search_hint)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    // Navigate to Search Results Fragment
                    navigateToSearch(query)
                    searchView.clearFocus() // Hide keyboard
                    searchItem.collapseActionView() // Collapse the search view
                }
                return true // Query handled
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Optional: Implement suggestions or live search here if needed
                return false // Default handling
            }
        })

        return true // Show the menu
    }

    // Navigate to Search Results fragment
    private fun navigateToSearch(query: String) {
        // Use NavController to navigate. Determine the current destination
        // to select the correct action ID from the navigation graph.
        val currentDestinationId = navController.currentDestination?.id
        val action = when(currentDestinationId) {
            R.id.navigation_home -> HomeFragmentDirections.actionHomeFragmentToSearchEventsFragment(query)
            R.id.navigation_active_events -> ActiveEventsFragmentDirections.actionActiveEventsToSearchEventsFragment(query)
            R.id.navigation_finished_events -> FinishedEventsFragmentDirections.actionFinishedEventsToSearchEventsFragment(query)
            // Add case for FavoritesFragment if search should be accessible from there
            // R.id.navigation_favorites -> FavoritesFragmentDirections.actionFavoritesFragmentToSearchEventsFragment(query) // Define this action if needed
            else -> null // Or a global action if defined: MobileNavigationDirections.actionGlobalSearchEventsFragment(query)
        }

        action?.let {
            navController.navigate(it)
        } ?: run {
            android.util.Log.w("MainActivity", "Could not find navigation action for search from destination $currentDestinationId")
            // Maybe try a global action?
            // try { navController.navigate(MobileNavigationDirections.actionGlobalSearchEventsFragment(query)) } catch (e: Exception) { }
        }
    }
}