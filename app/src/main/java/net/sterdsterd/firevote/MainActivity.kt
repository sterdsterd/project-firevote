package net.sterdsterd.firevote

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.telephony.TelephonyManager
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

import kotlinx.android.synthetic.main.activity_main.*
import android.Manifest.permission
import android.Manifest.permission.READ_CONTACTS
import android.content.pm.PackageManager
import android.graphics.Color
import android.opengl.Visibility
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.TranslateAnimation
import android.widget.LinearLayout
import com.gun0912.tedpermission.TedPermission
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.firebase.FirebaseApp
import com.gun0912.tedpermission.PermissionListener
import kotlinx.android.synthetic.main.content_main.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.FirebaseError
import java.time.Duration


class MainActivity : AppCompatActivity() {

    var did = "null"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(bottomAppBar)

        FirebaseApp.initializeApp(this)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            var tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            did = tm.getDeviceId()
        }


        checkAbility(did, fab)

        fab.setOnClickListener { view ->

            var i = 0
            when {
                vote1.isChecked -> i = 1
                vote2.isChecked -> i = 2
                vote3.isChecked -> i = 3
                vote4.isChecked -> i = 4
                vote5.isChecked -> i = 5
            }
            var snackbar: Snackbar? = null
            if(i != 0) {
                var fb = FirebaseDatabase.getInstance().getReference().push()
                fb.child("ID").setValue(did)
                fb.child("Value").setValue(i)
                snackbar = Snackbar.make(
                    coordinator,
                    "투표하였습니다", Snackbar.LENGTH_LONG
                )
            } else {
                fab.hide()
                snackbar = Snackbar.make(
                    coordinator,
                    "항목을 선택해주세요",
                    Snackbar.LENGTH_LONG
                )
            }

            fab.hide()
            val snackbarView = snackbar.view
            val params = snackbarView.layoutParams as CoordinatorLayout.LayoutParams

            params.setMargins(
                params.leftMargin + 48,
                params.topMargin,
                params.rightMargin + 48,
                params.bottomMargin + 192
            )

            snackbarView.layoutParams = params

            snackbar.view.background = this.getDrawable(R.drawable.bg_snack)

            snackbar.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {

                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    checkAbility(did, fab)
                    super.onDismissed(transientBottomBar, event)
                }
            })
        }
    }

    fun checkAbility(did: String, fab:FloatingActionButton){
        var dbRef = FirebaseDatabase.getInstance().getReference()

        dbRef.orderByChild("ID").equalTo(did).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    fab.hide()
                    radioGroup.visibility = GONE
                    infoText.visibility = VISIBLE
                } else {
                    fab.show()
                    radioGroup.visibility = VISIBLE
                    infoText.visibility = GONE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.action_refresh) checkAbility(did, fab)


        return when (item.itemId) {
            R.id.action_refresh -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
