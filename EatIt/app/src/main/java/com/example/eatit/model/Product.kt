package com.example.eatit.model

import android.provider.ContactsContract.DisplayPhoto
import android.text.TextUtils
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

class Product (
    var name: String? = null,
    var description: String? = null,
    var price: String? = null,
    var photo: String? = null
) {

}