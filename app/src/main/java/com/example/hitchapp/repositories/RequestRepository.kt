package com.example.hitchapp.repositories

import android.util.Log
import com.example.hitchapp.models.Request
import com.example.hitchapp.models.User
import com.parse.FindCallback
import com.parse.ParseQuery
import com.parse.ParseUser

class RequestRepository{

    private var currentUser: User = ParseUser.getCurrentUser() as User

    // Gets requests
    fun requestsQuery(requests: Int, findCallback: FindCallback<Request>?) {

        currentUser = ParseUser.getCurrentUser() as User
        Log.i(TAG, "hello" + currentUser.username)
        val query = ParseQuery.getQuery(Request::class.java)
        query.include("driver")
        query.whereEqualTo("driver", currentUser)

        // Set a limit
        query.limit = requests

        // Sort by created at
        query.addDescendingOrder("createdAt")

        // Find requests
        query.findInBackground(findCallback)
    }

    companion object {
        private const val TAG = "RequestRepository"
        @JvmStatic
        var instance: RequestRepository? = null
            get() {
                if (field == null) {
                    field = RequestRepository()
                }
                return field
            }
            private set
    }
}
