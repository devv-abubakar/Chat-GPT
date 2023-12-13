@file:Suppress("OVERRIDE_DEPRECATION")

package devv.abubakar.chatgpt

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import devv.abubakar.chatgpt.databinding.ActivitySignUpBinding

@Suppress("DEPRECATION")
class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var userSignupEmail: String
    private lateinit var userSignupPassword: String
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()


        binding.btnSignupConfirm.setOnClickListener {
            signUp()
        }

        binding.signupLoginText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun signUp() {
        val emailPattern = Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")

        userSignupEmail = binding.signupEmail.text.toString()
        userSignupPassword = binding.signupPassword.text.toString()

        if (!userSignupEmail.matches(emailPattern)) {
            Toast.makeText(this, "Check email and try again", Toast.LENGTH_SHORT).show()
            return
        }
        if (userSignupPassword.length < 8) {
            Toast.makeText(
                this,
                "Password must be at least 8 characters",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (!binding.signupCheckbox.isChecked) {
            Toast.makeText(this, "Please accept the terms", Toast.LENGTH_SHORT).show()
            return
        }
        createUserUsingEmailPassword()
    }

    private fun createUserUsingEmailPassword() {
        binding.signupProgressBar.visibility = View.VISIBLE
        firebaseAuth.createUserWithEmailAndPassword(userSignupEmail, userSignupPassword)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    showProgressDialogBox(
                        true,
                        "Sign up Successful!",
                        "Please wait... \n You will be directed to the homepage"
                    )

                    binding.signupProgressBar.visibility = View.GONE

                } else {
                    binding.signupProgressBar.visibility = View.GONE
                    showProgressDialogBox(
                        false,
                        "Sign up Failed!",
                        "Please wait... \n You will be directed back. Try again later"
                    )
                }
            }
    }

    @SuppressLint("ResourceAsColor")
    private fun showProgressDialogBox(signupCheck: Boolean, title: String, description: String) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.custom_signup_successful_dialoguebox)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        val progressBar = dialog.findViewById<ProgressBar>(R.id.custom_signup_progressBar)

        val dialogTitle = dialog.findViewById<TextView>(R.id.custom_dialog_title)
        val dialogDescription = dialog.findViewById<TextView>(R.id.custom_dialog_description)

        dialogTitle.text = title
        dialogDescription.text = description

        if (!signupCheck) {
            val textColor = Color.parseColor("#FF0000")
            dialogTitle.setTextColor(textColor)
        }

        dialog.show()
        progressBar.max = 100
        ObjectAnimator.ofInt(progressBar, "progress", 100)
            .setDuration(5000)
            .start()

        handler.postDelayed({
            if (signupCheck) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, WelcomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 5000) // 200
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
    }
}
