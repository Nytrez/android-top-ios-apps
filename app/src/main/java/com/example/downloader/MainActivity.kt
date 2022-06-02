package com.example.downloader


import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.downloader.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.URL

class ViewHolder(v: View) {
    val tvName: TextView = v.findViewById(R.id.tvName)
    val tvArtist: TextView = v.findViewById(R.id.tvArtist)
    val tvSummary: TextView = v.findViewById(R.id.tvSummary)
}

class FeedEntry {
    var name: String = ""
    var artist: String = ""


    var releaseDate: String = ""
    var summary: String = ""
    var imageURL: String = ""

    override fun toString(): String {
        return """
            name =$name
            artist =$artist
            releaseDate =$releaseDate
            imageURL =$imageURL
            """.trimIndent()
    }
}

class MainActivity : AppCompatActivity() {
    private val TAG = "Download Data"
    var feedUrl: String = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml"
    // private val downloadURL =\

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        val inst = this
        val parseApplication = ParseApplication()



        fun background() = GlobalScope.launch {

            val download: Job = launch {
                val test =
                    downloader(feedUrl)
                if (test.isEmpty()) {
                    Log.d(TAG, "Error Downloading")
                }

                parseApplication.parse(test)

            }

            download.join()

            launch(Dispatchers.Main) {
                val feedAdapter = FeedAdapter(
                    inst, R.layout.item_res, parseApplication.applications
                )
                binding.xmlListView.adapter = feedAdapter
            }
        }

        background()

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.feeds_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        feedUrl = when (item.itemId) {
            R.id.mnuFree ->
                "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml"
            R.id.mnuPaid ->
                "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=10/xml"
            R.id.mnuSong ->
                "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=10/xml"
            else ->
                return super.onOptionsItemSelected(item)

        }

       // backgr()
        return true
    }


    private fun downloader(urlPath: String?): String {
        return URL(urlPath).readText()
    }


}

