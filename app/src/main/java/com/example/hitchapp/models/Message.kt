package com.example.hitchapp.models

import com.parse.ParseClassName
import com.parse.ParseObject
import com.parse.ParseUser
import org.parceler.Parcel

@Parcel(analyze = [Message::class])
@ParseClassName("Message")
class Message : ParseObject() {
    var content: String?
        get() = getString(KEY_CONTENT)
        set(content) {
            content?.let { put(KEY_CONTENT, it) }
        }

    var author: ParseUser?
        get() = getParseUser(KEY_AUTHOR)
        set(user) {
            user?.let { put(KEY_AUTHOR, it) }
        }

    var authorId: String?
        get() = getString(KEY_AUTHOR_ID)
        set(authorId) {
            authorId?.let { put(KEY_AUTHOR_ID, it) }
        }

    companion object {
        const val KEY_CONTENT = "content"
        const val KEY_AUTHOR = "author"
        const val KEY_AUTHOR_ID = "authorId"
    }
}