package com.example.casaconnect.Domain

import java.io.Serializable

class PropertyDomain : Serializable {
    var adid: String?
    var owndby: String?
    var postdate: String?
    var type: String?
    var title: String?
    var address: String?
    var pickpath: List<String?>
    var description: String?
    var price: Int
    var bed: Int
    var bath: Int
    var size: String?
    var garage: Boolean?
    var score: Double
    var deleteflag: Boolean?= false
    constructor(
        owndby: String?,
        postdate: String?,
        type: String?,
        title: String?,
        address: String?,
        pickpath: List<String?>,
        description: String?,
        price: Int,
        bed: Int,
        bath: Int,
        size: String?,
        garage: Boolean?,
        score: Double,
        adid: String,
        deleteflag: Boolean? = false
    ) {
        this.deleteflag = deleteflag
        this.owndby = owndby
        this.postdate = postdate
        this.type = type
        this.title = title
        this.address = address
        this.pickpath = pickpath
        this.description = description
        this.price = price
        this.bed = bed
        this.bath = bath
        this.size = size
        this.garage = garage
        this.score = score
        this.adid = adid
    }
    constructor(
        type: String?,
        title: String?,
        address: String?,
        pickpath: List<String?>,
        description: String?,
        price: Int,
        bed: Int,
        bath: Int,
        size: String?,
        garage: Boolean?,
        score: Double,
        adid: String
    ) {
        this.owndby = ""
        this.postdate = ""
        this.type = type
        this.title = title
        this.address = address
        this.pickpath = pickpath
        this.description = description
        this.price = price
        this.bed = bed
        this.bath = bath
        this.size = size
        this.garage = garage
        this.score = score
        this.adid = adid
    }

}
