package com.application.mindmate.caregiver

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.adapters.MessagesAdapter
import com.application.common.ActivityUtils
import com.application.enums.UserRole
import com.application.mindmate.R
import com.application.models.MessageModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener

class ChatActivity : AppCompatActivity() {

    private val TAG = "ChatActivity"

    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: ImageView
    private lateinit var adapter: MessagesAdapter
    private var messageList = ArrayList<MessageModel>()

    private lateinit var db: FirebaseFirestore
    private var userRole: String = ""
    private var chatId: String = ""
    private var patientId: String = ""
    private var patientName: String = ""
    private var caregiverId: String = ""
    private var currentUserId: String = ""
    private lateinit var alarmImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Adjust the theme if necessary
        setContentView(R.layout.activity_chat)
        // Get intent extras
        patientId = intent.getStringExtra("PATIENT_ID") ?: ""
        patientName = intent.getStringExtra("PATIENT_NAME") ?: "Patient"
        caregiverId = intent.getStringExtra("CAREGIVER_ID") ?: ""
        userRole = intent.getStringExtra("ROLE") ?: ""
        if (userRole.isEmpty()) {
            userRole = "CAREGIVER"
        }
        alarmImageView = findViewById(R.id.alarm)
        if (userRole == UserRole.CAREGIVER.toString()) {
            alarmImageView.visibility = View.GONE
        }
        ActivityUtils.actionBarSetup(this, UserRole.valueOf(userRole))
        if (patientId.isEmpty() || caregiverId.isEmpty()) {
            Toast.makeText(this, "Invalid chat participants", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (currentUserId.isEmpty()) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Generate chatId (you may want to use a consistent method for generating chat IDs)
        chatId = if (caregiverId < patientId)
            "${caregiverId}_$patientId"
        else
            "${patientId}_$caregiverId"

        // Initialize views
        messagesRecyclerView = findViewById(R.id.messages_recycler_view)
        messageEditText = findViewById(R.id.message_edit_text)
        sendButton = findViewById(R.id.send_button)

        // Set up RecyclerView
        adapter = MessagesAdapter(messageList, currentUserId)
        messagesRecyclerView.layoutManager = LinearLayoutManager(this)
        messagesRecyclerView.adapter = adapter

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        // Load messages from Firestore
        loadMessages()

        // Handle send button click
        sendButton.setOnClickListener {
            sendMessage()
        }

        // Enable send button only if message is not empty
        messageEditText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                val isNotEmpty = s?.toString()?.trim()?.isNotEmpty() == true
                sendButton.isEnabled = isNotEmpty
                sendButton.alpha = if (isNotEmpty) 1.0f else 0.5f
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not needed
            }
        })

        // Set the title to the patient's name
        supportActionBar?.title = patientName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun sendMessage() {
        val messageText = messageEditText.text.toString().trim()
        if (messageText.isEmpty()) {
            return
        }

        val message = hashMapOf(
            "senderId" to currentUserId,
            "messageText" to messageText,
            "timestamp" to FieldValue.serverTimestamp()
        )

        val chatRef = db.collection("chats").document(chatId)
        chatRef.set(
            hashMapOf(
                "participants" to listOf(caregiverId, patientId),
                "chatId" to chatId
            ), SetOptions.merge()
        )

        chatRef.collection("messages")
            .add(message)
            .addOnSuccessListener {
                // Clear the message input
                messageEditText.text.clear()
                messagesRecyclerView.scrollToPosition(messageList.size - 1)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error sending message", e)
                Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadMessages() {
        val chatRef = db.collection("chats").document(chatId)
        chatRef.collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener(EventListener { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@EventListener
                }

                if (snapshots != null) {
                    messageList.clear()
                    for (doc in snapshots.documents) {
                        val senderId = doc.getString("senderId") ?: ""
                        val messageText = doc.getString("messageText") ?: ""
                        val timestamp = doc.getTimestamp("timestamp")
                        val message = MessageModel(
                            senderId,
                            messageText,
                            timestamp?.toDate() ?: java.util.Date()
                        )
                        messageList.add(message)
                    }
                    adapter.notifyDataSetChanged()
                    messagesRecyclerView.scrollToPosition(messageList.size - 1)
                }
            })
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}