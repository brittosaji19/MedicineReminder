package com.brittosaji.medicinereminder

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot



class LoginActivity : AppCompatActivity() {
    private val RC_SIGNIN:Int=123
    private lateinit var mAuth: FirebaseAuth
    lateinit var db:FirebaseDatabase
    lateinit var userRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val googleSignInOptions: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.client_id)).requestEmail().build()
        val googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
        mAuth= FirebaseAuth.getInstance()
        val signInWithGoogleButton = googleSignInButton
        signInWithGoogleButton.setOnClickListener(View.OnClickListener {
            signInWithGoogle(googleSignInClient)
        })
        val signInButton=signInButton
        signInButton.setOnClickListener {
            signInWithEmail(emailEditText.text.toString(),passwordEditText.text.toString())
        }
    }

    private fun signInWithGoogle(signInClient: GoogleSignInClient){
        Toast.makeText(this,"Starting Google SignIn", Toast.LENGTH_SHORT).show()
        val signInIntent=signInClient.signInIntent
        startActivityForResult(signInIntent,RC_SIGNIN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==RC_SIGNIN){
            val task: Task<GoogleSignInAccount>
            task=GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account: GoogleSignInAccount =task.getResult(ApiException::class.java)
                firebaseSignInWithGoogle(account)
                Toast.makeText(this,"Signed In Succesfully",Toast.LENGTH_SHORT).show()
            }
            catch (e:Exception){
                Toast.makeText(this,"SignIn Failed",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseSignInWithGoogle(account:GoogleSignInAccount){
        Toast.makeText(this,"Logging In As "+ account.id,Toast.LENGTH_SHORT).show()
        val credential: AuthCredential = GoogleAuthProvider.getCredential(account.idToken,null)
        val authHandler=mAuth.signInWithCredential(credential)
        authHandler.addOnCompleteListener(this, OnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(this,"Logged In",Toast.LENGTH_SHORT).show()
                signInComplete()
                val user=mAuth.currentUser
            }
            else{
                Toast.makeText(this,"Authentication Failed",Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun signInWithEmail(email:String,pass:String){
        val signInHandler=mAuth.signInWithEmailAndPassword(email,pass)
        signInHandler.addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(this,"Logged in as : " +mAuth.currentUser,Toast.LENGTH_SHORT).show()
                signInComplete()
            }
            else{
                Toast.makeText(this,"Authentication Failure",Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun signInComplete(){
        db= FirebaseDatabase.getInstance()
        userRef=db.getReference()
        val currentUserRef=userRef.child("users").child(mAuth.currentUser?.uid)
        currentUserRef.child("name").setValue(mAuth.currentUser?.displayName)
        currentUserRef.child("id").setValue(mAuth.currentUser?.uid)
        currentUserRef.child("email").setValue(mAuth.currentUser?.email)
        // Read from the database
        currentUserRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //var value:User? = dataSnapshot.getValue(User::class.java)
                Log.d("Output",dataSnapshot.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("Error", "Failed to read value.", error.toException())
            }
        })

        startActivity(Intent(this,MainActivity::class.java))
    }
}
