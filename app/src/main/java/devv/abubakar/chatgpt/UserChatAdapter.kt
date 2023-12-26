package devv.abubakar.chatgpt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UserChatAdapter(private val userChats: List<UserChat>) :
    RecyclerView.Adapter<UserChatAdapter.UserChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserChatViewHolder {
        val layoutRes = if (viewType == VIEW_TYPE_USER) {
            R.layout.item_chat_user
        } else {
            R.layout.item_chat_ai
        }

        val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return UserChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserChatViewHolder, position: Int) {
        val userChat = userChats[position]
        holder.contentTextView.text = userChat.content.trim()
        // Convert timestamp to a readable date and time format
        val formattedTimestamp = convertTimestampToReadable(userChat.timestamp.toLongOrNull() ?: 0L)
        holder.timestampTextView.text = formattedTimestamp
        // Set other views accordingly
    }

    override fun getItemCount(): Int {
        return userChats.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (userChats[position].sender == "user") VIEW_TYPE_USER else VIEW_TYPE_OTHER
    }

    class UserChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contentTextView: TextView = itemView.findViewById(R.id.text_content)
        val timestampTextView: TextView = itemView.findViewById(R.id.text_timestamp)
        // Add other views for your chat item
    }

    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_OTHER = 2

        private fun convertTimestampToReadable(timestamp: Long): String {
            val currentDate = Calendar.getInstance()
            val messageDate = Calendar.getInstance().apply {
                timeInMillis = timestamp
            }

            return when {
                isSameDay(currentDate, messageDate) -> {
                    // Today
                    SimpleDateFormat("h:mm a", Locale.getDefault()).format(messageDate.time)
                }

                isYesterday(currentDate, messageDate) -> {
                    // Yesterday
                    "Yesterday"
                }

                else -> {
                    // Older than yesterday, show the full date
                    SimpleDateFormat(
                        "yyyy-MM-dd h:mm a",
                        Locale.getDefault()
                    ).format(messageDate.time)
                }
            }
        }

        private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
        }

        private fun isYesterday(cal1: Calendar, cal2: Calendar): Boolean {
            cal1.add(Calendar.DAY_OF_YEAR, -1)
            return isSameDay(cal1, cal2)
        }

    }
}
