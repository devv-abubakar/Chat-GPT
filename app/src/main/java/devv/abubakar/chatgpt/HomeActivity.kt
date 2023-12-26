@file:Suppress("NAME_SHADOWING", "SameParameterValue")

package devv.abubakar.chatgpt

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class HomeActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    private lateinit var userQuestion: TextInputEditText
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private val userChats = mutableListOf<UserChat>()
    private val userChatAdapter = UserChatAdapter(userChats)


    private val auth = FirebaseAuth.getInstance()

    companion object {
        private const val API_KEY = "Enter your api key here"
        private const val API_URL = "https://api.openai.com/v1/completions"
        private const val MODEL_NAME = "text-davinci-003"
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        userQuestion = findViewById(R.id.userQuestion)

        database = Firebase.database
        myRef = database.getReference("Users")

        showChat()


        userQuestion.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                recyclerView.scrollToPosition(userChats.size - 1)
                userChatAdapter.notifyDataSetChanged()

                val question = userQuestion.text.toString().trim()

                if (question.isNotEmpty()) {

                    val timestamp: Long = System.currentTimeMillis()

                    val user = UserChat("user", question, timestamp.toString())

                    myRef.child(auth.currentUser!!.uid).child("Chats").child("messageId$timestamp")
                        .setValue(user)


                    getResponse(question) { response ->
                        runOnUiThread {
                            val timestamp: Long = System.currentTimeMillis()

                            val ai = UserChat("ai", response, timestamp.toString())

                            myRef.child(auth.currentUser!!.uid).child("Chats")
                                .child("messageId$timestamp")
                                .setValue(ai)
                            // Scroll RecyclerView to the last item
                            recyclerView.scrollToPosition(userChats.size - 1)
                            userChatAdapter.notifyDataSetChanged()
                        }
                    }
                }
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun showChat() {

        recyclerView = findViewById(R.id.chatRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = userChatAdapter


        // Assuming you have a Firebase reference
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        databaseReference.child(auth.currentUser!!.uid).child("Chats")
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    userChats.clear()
                    for (snapshot in dataSnapshot.children) {
                        val userChat = snapshot.getValue(UserChat::class.java)
                        userChats.add(userChat!!)
                    }
                    recyclerView.scrollToPosition(userChats.size - 1)
                    userChatAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors
                }
            })
    }


    private fun getResponse(question: String, callback: (String) -> Unit) {
        userQuestion.setText("")
        hideKeyboard()

        try {
            val requestJson = JSONObject().apply {
                put("model", MODEL_NAME)
                put("prompt", question)
                put("max_tokens", 500)
                put("temperature", 0)
            }

            val body = requestJson.toString().toRequestBody("application/json".toMediaTypeOrNull())

            val request = Request.Builder()
                .url(API_URL)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer $API_KEY")
                .post(body)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("error", "API failed", e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val body = response.body?.string()

                        if (body != null) {
                            Log.v("data", body)

                            val jsonObject = JSONObject(body)

                            if (jsonObject.has("choices")) {
                                val jsonArray: JSONArray = jsonObject.getJSONArray("choices")

                                if (jsonArray.length() > 0) {
                                    val textResult =
                                        jsonArray.getJSONObject(0).optString("text", "")
                                    callback(textResult)

                                } else {
                                    Log.e("JSON Error", "'choices' array is empty.")
                                    errorMessage("Something went wrong. Please try again later.")
                                }
                            } else {
                                Log.e("JSON Error", "No 'choices' key in the JSON object.")
                                errorMessage("Something went wrong. Please try again later.")
                            }
                        } else {
                            Log.v("data", "empty")
                            errorMessage("Something went wrong. Please try again later.")
                        }
                    } catch (e: Exception) {
                        Log.e("Error", "An unexpected error occurred", e)
                    }
                }
            })
        } catch (e: JSONException) {
            Log.e("JSON Error", "Error creating JSON object", e)
            errorMessage("Something went wrong. Please try again later.")
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun errorMessage(s: String) {
        val timestamp: Long = System.currentTimeMillis()

        val ai = UserChat("ai", s, timestamp.toString())

        myRef.child(auth.currentUser!!.uid).child("Chats")
            .child("messageId$timestamp")
            .setValue(ai)
        // Scroll RecyclerView to the last item
        recyclerView.scrollToPosition(userChats.size - 1)
        userChatAdapter.notifyDataSetChanged()
    }

    // Function to hide the keyboard
    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(userQuestion.windowToken, 0)
    }

}
