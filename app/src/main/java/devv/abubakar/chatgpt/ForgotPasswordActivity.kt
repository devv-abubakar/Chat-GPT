@file:Suppress("DEPRECATION")

package devv.abubakar.chatgpt

import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ProgressBar
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import devv.abubakar.chatgpt.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var handler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnForgotPasswordContinue.setOnClickListener {
            val email = binding.forgotPasswordEditText.text.toString()
            firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    showProgressDialogBox(
                        true,
                        "Check your email!",
                        "Please wait... \n You will be directed to the homepage"
                    )
                }
                .addOnFailureListener {
                    showProgressDialogBox(
                        false,
                        "Reset Password Failed!",
                        "Try again later \n You will be directed back."
                    )
                }
        }

    }

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
            .setDuration(7000)
            .start()

        handler.postDelayed({
            if (signupCheck) {
                val intent = Intent(this, WelcomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, WelcomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 7000)

    }

}
