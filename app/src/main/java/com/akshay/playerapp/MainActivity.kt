package com.akshay.playerapp

import android.Manifest.permission.READ_MEDIA_AUDIO
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.akshay.playerapp.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File
import java.lang.Exception
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setTheme(R.style.coolPinkNav)
        setContentView(binding.root)
        //for Nav Drawer
        toggle = ActionBarDrawerToggle(this, binding.root, R.string.open, R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (requestRuntimePermission()){
            folderList = ArrayList()
            videoList = getAllVideos()
            audioList = getAllAudios()
            setFragment(VideoFragment())
        }
        setFragment(AudioFragment())

//        viewPager = findViewById(R.id.viewPager)
//        val adapter = ViewPagerAdapter(supportFragmentManager)
//        viewPager.adapter = adapter
        bottomNavigationView = findViewById(R.id.bottomNav)

        bottomNavigationView.setOnItemSelectedListener {item->
            when(item.itemId){
                R.id.videoView-> setFragment(VideoFragment())
                R.id.folderView-> setFragment(FolderFragment())
                R.id.audioView-> setFragment(AudioFragment())
            }
            return@setOnItemSelectedListener true
        }

        binding.navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.feedbackNav-> Toast.makeText(this,"Feedback",Toast.LENGTH_SHORT).show()
                R.id.ThemeNav-> Toast.makeText(this,"Themes",Toast.LENGTH_SHORT).show()
                R.id.sortOrderNav-> Toast.makeText(this,"Sort Order",Toast.LENGTH_SHORT).show()
                R.id.aboutNav->Toast.makeText(this,"About",Toast.LENGTH_SHORT).show()
                R.id.exitNav-> exitProcess(1)
            }
            return@setNavigationItemSelectedListener true
        }
//        viewPager()
    }
    private fun setFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentFL, fragment)
        transaction.disallowAddToBackStack()
        transaction.commit()
    }

    //for requesting permission
    private fun requestRuntimePermission(): Boolean{
        if(ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
         ActivityCompat.requestPermissions(this, arrayOf(READ_MEDIA_AUDIO, READ_MEDIA_VIDEO), Build.VERSION_CODES.M)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Build.VERSION_CODES.M) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            else
                ActivityCompat.requestPermissions(this, arrayOf(READ_MEDIA_AUDIO, READ_MEDIA_VIDEO), Build.VERSION_CODES.M)
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("Recycle", "Range", "SuspiciousIndentation")
     fun getAllVideos(): ArrayList<Video>{
        val tempList = ArrayList<Video>()
        val tempFolderList = ArrayList<String>()
        val projection = arrayOf(MediaStore.Video.Media.TITLE, MediaStore.Video.Media.SIZE, MediaStore.Video.Media._ID,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DATE_ADDED, MediaStore.Video.Media.DURATION, MediaStore.Video.Media.BUCKET_ID)

        val cursor = this.contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null,
            MediaStore.Video.Media.DATE_ADDED + " DESC")

        if (cursor != null)
            if (cursor.moveToNext())
                do {
                    val titleC = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE))
                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID))
                    val folderC = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME))
                    val folderIdC = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID))
                    val sizeC = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE))
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))
                    val durationC = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION)).toLong()

                    try {
                        val file = File(pathC)
                        val artUriC = Uri.fromFile(file)
                        val video = Video(title = titleC, id = idC, folderName = folderC, size = sizeC, path = pathC, duration = durationC, artUri = artUriC)

                        if (file.exists()) tempList.add(video)
                        //for adding folders
                        if (!tempFolderList.contains(folderC)){
                            tempFolderList.add(folderC)
                            folderList.add(Folder(folderIdC,folderC))
                        }


                    }catch (e: Exception){}
                }while (cursor.moveToNext())
                cursor?.close()
        return tempList
    }

    // for get all Audio List
    @SuppressLint("Recycle", "Range", "SuspiciousIndentation")
    private fun getAllAudios(): ArrayList<Audio>{
        val tempList = ArrayList<Audio>()
        val tempFolderList = ArrayList<String>()
        val projection = arrayOf(MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.SIZE, MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.BUCKET_DISPLAY_NAME, MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DATE_ADDED, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.BUCKET_ID)

        val cursor = this.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null,
            MediaStore.Audio.Media.DATE_ADDED + " DESC")

        if (cursor != null)
            if (cursor.moveToNext())
                do {
                    val titleC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val folderC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.BUCKET_DISPLAY_NAME))
                    val folderIdC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.BUCKET_ID))
                    val sizeC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE))
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val durationC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)).toLong()

                    try {
                        val file = File(pathC)
                        val artUriC = Uri.fromFile(file)
                        val audio = Audio(title = titleC, id = idC, folderName = folderC, size = sizeC, path = pathC, duration = durationC, artUri = artUriC)

                        if (file.exists()) tempList.add(audio)
                        //for adding folders
                        if (!tempFolderList.contains(folderC)){
                            tempFolderList.add(folderC)
                            folderList.add(Folder(id = idC, folderName = folderC))
                        }
                    }catch (e: Exception){}
                }while (cursor.moveToNext())
        cursor?.close()
        return tempList
    }
    companion object{
        lateinit var videoList: ArrayList<Video>
        lateinit var audioList: ArrayList<Audio>
        lateinit var folderList: ArrayList<Folder>
    }
}