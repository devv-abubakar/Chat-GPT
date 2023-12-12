@file:Suppress("OVERRIDE_DEPRECATION")

package devv.abubakar.chatgpt

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import devv.abubakar.chatgpt.databinding.ActivityWelcomeBinding


class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setIconBasedOnTheme()
        btnSignup()
        btnLogin()

    }

    private fun btnLogin() {
        binding.btnWelcomeToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun btnSignup() {
        binding.btnWelcomeToSignup.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setIconBasedOnTheme() {
        val isDarkMode =
            (resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES

        // Apple icon based on the current theme
        if (isDarkMode) {
            binding.welcomeAppleIcon.setImageResource(R.drawable.apple_logo_dark)
        } else {
            binding.welcomeAppleIcon.setImageResource(R.drawable.apple_icon)
        }
    }


    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {

    }

}