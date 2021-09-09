package com.caocao.cadavimusicplayer.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.caocao.cadavimusicplayer.R
import com.caocao.cadavimusicplayer.databinding.ActivityHomeBinding
import com.caocao.cadavimusicplayer.ui.adapter.TabAdapter
import com.caocao.cadavimusicplayer.ui.view.AllSongsFragment
import com.caocao.cadavimusicplayer.ui.view.MiniPlayerFragment
import com.caocao.cadavimusicplayer.ui.view.MusicPlayerFragment
import com.caocao.cadavimusicplayer.util.ServiceConnectionUtil
import com.caocao.cadavimusicplayer.util.replaceFragmentToActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import org.koin.android.ext.android.inject

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private var isMenuOpened = false
    val miniPlayerFragment : MiniPlayerFragment by inject()
    private val playerFragment : MusicPlayerFragment by inject()
    private lateinit var alertDialog: AlertDialog
    private var serviceConnectionToken: ServiceConnectionUtil.ServiceConnectionToken? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        initDrawerLayout()
        initTabLayout()
        settingSlidingPanel()
        serviceConnectionToken = bindServiceAndGetToken(applicationContext)
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume")
    }

    override fun onStop() {
        super.onStop()
        Log.e(TAG, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        unBindFromService()
        Log.e(TAG, "onDestroy")
    }

    fun openPlayer() {
        binding.slidingPanel.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
    }

    fun closePlayer() {
        binding.slidingPanel.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
    }

    fun showDialogPermission() {
        alertDialog.apply {
            setOnShowListener {
                this.apply {
                    getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                        permissionDenied()
                        openSettingPermission()
                        dismiss()
                    }
                    getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener {
                        permissionDenied()
                        dismiss()
                    }
                }
            }
            show()
        }
    }

    private fun openSettingPermission() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        startActivity(intent)
    }

    private fun permissionAllow() {
        getAllSongFragmentOrNull() ?.run {
            getDataSongs()
        }
    }

    private fun permissionDenied() {
        getAllSongFragmentOrNull() ?.run {
            updateWithPermissionDenied()
        }
    }

    private fun getAllSongFragmentOrNull() = supportFragmentManager.fragments.lastOrNull() as? AllSongsFragment

    private fun bindServiceAndGetToken(context: Context) = ServiceConnectionUtil.bind(context)

    private fun unBindFromService() {
        serviceConnectionToken?.let { ServiceConnectionUtil.unbind(it) }
    }

    private fun init() {
        alertDialog = AlertDialog.Builder(this)
            .setTitle(resources.getString(R.string.title_dialog_permistion))
            .setMessage(resources.getString(R.string.sub_title_dialog_permistion))
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.setting_button_dialog), null)
            .setNegativeButton(resources.getString(R.string.ok_dialog), null)
            .create()

        Dexter.withContext(this)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    Log.d(TAG, "onPermissionGranted")
                    permissionAllow()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    showDialogPermission()
                    permissionDenied()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    showDialogPermission()
                }
            })
            .check()
    }

    private fun initDrawerLayout() {
        toggle = object : ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @SuppressLint("WrongConstant")
            override fun onDrawerStateChanged(newState: Int) {
                super.onDrawerStateChanged(newState)
                if (newState == DrawerLayout.STATE_SETTLING) {
                    if (binding.drawerLayout.isDrawerOpen(Gravity.START)) {
                        isMenuOpened = true
                    }
                }
            }
        }
        binding.drawerLayout.addDrawerListener(toggle)
    }

    private fun initTabLayout() {
        val tabAdapter = TabAdapter(this)
        binding.viewPager.adapter = tabAdapter
        TabLayoutMediator(binding.tabs, binding.viewPager,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                when (position) {
                    0 -> tab.text = resources.getString(R.string.all_song)
                    1 -> tab.text = resources.getString(R.string.favorite)
                    2 -> tab.text = resources.getString(R.string.artist)
                    3 -> tab.text = resources.getString(R.string.playlist)
                    4 -> tab.text = this.resources.getString(R.string.album)
                    else -> tab.text = this.resources.getString(R.string.genres)
                }
            }).attach()
    }

    private fun settingSlidingPanel() {
        replaceFragmentToActivity(miniPlayerFragment, R.id.mini_player_container)
        replaceFragmentToActivity(playerFragment, R.id.player_container)
        binding.slidingPanel.apply {
            panelState = SlidingUpPanelLayout.PanelState.HIDDEN
            addPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener {
                override fun onPanelSlide(panel: View?, slideOffset: Float) {
                    when {
                        slideOffset <= 0.2f -> miniPlayerFragment.setAlpha(1f)
                        slideOffset > 0.2f && slideOffset <= 0.6f -> {
                            miniPlayerFragment.setVisibility(View.VISIBLE)
                            miniPlayerFragment.setAlpha(1 - (slideOffset - 0.2f) / 0.4f)
                        }
                        slideOffset > 0.6f -> miniPlayerFragment.setVisibility(View.INVISIBLE)
                    }
                }

                override fun onPanelStateChanged(
                    panel: View?,
                    previousState: SlidingUpPanelLayout.PanelState?,
                    newState: SlidingUpPanelLayout.PanelState?
                ) {
                    if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                        miniPlayerFragment.setVisibility(View.VISIBLE)
                        miniPlayerFragment.setAlpha(1f)
                    }
                }
            })
        }
    }

    companion object {
        const val TAG = "HomeActivity"
    }
}