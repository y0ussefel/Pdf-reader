    package com.example.pdf_reader

    import android.content.Intent
    import android.os.Bundle
    import android.widget.Button
    import androidx.activity.enableEdgeToEdge
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.view.ViewCompat
    import androidx.core.view.WindowInsetsCompat
    import androidx.fragment.app.Fragment
    import com.example.pdf_reader.fragma.AllPdfFragment
    import com.example.pdf_reader.fragma.BrowseFragment
    import com.example.pdf_reader.fragma.FavoriteFragment
    import com.example.pdf_reader.fragma.RecentFragment
    import com.google.android.material.bottomnavigation.BottomNavigationView

    class MainActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            setContentView(R.layout.activity_main)
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }

            val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

            bottomNavigation.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.browse -> {
                        replaceFragment(BrowseFragment())
                        true
                    }R.id.recents -> {
                        replaceFragment(RecentFragment())
                        true
                    }
                    R.id.favorites -> {
                        replaceFragment(FavoriteFragment())
                        true
                    }
                    R.id.device -> {
                        replaceFragment(AllPdfFragment())
                        true
                    }

                    else -> false
                }
            }

        }
        private fun replaceFragment(fragment : Fragment){
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container,fragment)
                .commit()
        }

    }