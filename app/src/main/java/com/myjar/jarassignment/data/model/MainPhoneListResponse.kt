package com.myjar.jarassignment.data.model

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class MainPhoneListResponse(
    @SerializedName("itemId")
    @PrimaryKey
    var itemId: Long = 1,
    @SerializedName("computerItemData")
    var computerItemData: String = ""
) : RealmObject()