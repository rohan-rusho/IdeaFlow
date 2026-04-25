package com.flowideafmaker.ideaflow

import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.flowideafmaker.ideaflow.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            insets
        }

        val adapter = MainViewPagerAdapter(this)
        binding.viewPager.adapter = adapter

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.bottomNavigation.menu.getItem(position).isChecked = true
                binding.navigationView.menu.getItem(position).isChecked = true
            }
        })

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.dashboardFragment -> binding.viewPager.currentItem = 0
                R.id.ideasFragment -> binding.viewPager.currentItem = 1
                R.id.flowFragment -> binding.viewPager.currentItem = 2
                R.id.settingsFragment -> binding.viewPager.currentItem = 3
            }
            true
        }

        binding.navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.dashboardFragment -> binding.viewPager.currentItem = 0
                R.id.ideasFragment -> binding.viewPager.currentItem = 1
                R.id.flowFragment -> binding.viewPager.currentItem = 2
                R.id.settingsFragment -> binding.viewPager.currentItem = 3
            }
            binding.drawerLayout.close()
            true
        }

        supportFragmentManager.addOnBackStackChangedListener {
            updateHomeButtonIcon()
        }
    }

    private fun updateHomeButtonIcon() {
        val hasBackStack = supportFragmentManager.backStackEntryCount > 0
        // We need to find the btnMenu in the current fragment
        // Since ViewPager2 handles fragments, we can look at the current item
        // But back stack fragments usually replace the content container
    }

    fun openDrawer() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            binding.drawerLayout.open()
        }
    }
}