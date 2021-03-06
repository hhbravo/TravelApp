package com.deved.myepxinperu.data.server

import com.deved.data.common.DataResponse
import com.deved.data.source.PlaceDataSource
import com.deved.domain.Department
import com.deved.domain.Places
import com.deved.domain.User
import com.deved.myepxinperu.R
import com.deved.myepxinperu.data.server.mapper.PlacesMapper
import com.deved.myepxinperu.data.server.mapper.UserMapper
import com.deved.myepxinperu.data.server.model.PlacesServer
import com.deved.myepxinperu.data.server.model.UserServer
import com.deved.myepxinperu.ui.common.UiContext
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await

class FirebasePlacesDataSource(
    private val firebaseFirestore: FirebaseFirestore
) : PlaceDataSource {

    override suspend fun fetchAllPlaces(): DataResponse<List<Places>> {
        return try {
            val result = firebaseFirestore.collectionGroup("TouristDestination").get().await()
            val places = arrayListOf<Places>()
            result.forEach {
                places.add(PlacesMapper().mapToEntity(it.toObject(PlacesServer::class.java)))
            }
            DataResponse.Success(places)
        } catch (e: FirebaseFirestoreException) {
            DataResponse.ExceptionError(e)
        } catch (e: Exception) {
            DataResponse.ExceptionError(e)
        }
    }

    override suspend fun fetchAllDepartment(): DataResponse<List<Department>> {
        return try {
            val result = firebaseFirestore.collectionGroup("TouristDestination").get().await()
            val places = arrayListOf<Department>()
            result.forEach {
                val path = it.reference.parent.parent?.id
                places.add(Department(path,PlacesMapper().mapToEntity(it.toObject(PlacesServer::class.java))))
            }
            DataResponse.Success(places)
        } catch (e: FirebaseFirestoreException) {
            DataResponse.ExceptionError(e)
        } catch (e: Exception) {
            DataResponse.ExceptionError(e)
        }
    }

    override suspend fun registerExp(data: Department,userId:String): DataResponse<String> {
        return try {
            val department = hashMapOf<String, Any?>()
            with(data.place!!) {
                department["name"] = name
                department["description"] = description
                department["pictureOne"] = picturesOne
                department["pictureSecond"] = picturesSecond
                department["createAt"] = createAt
                department["userId"] = userId
            }

            firebaseFirestore.document("Department/${data.name!!.toUpperCase()}")
                .collection("TouristDestination")
                .document(data.place!!.name!!).set(department)
            DataResponse.Success(UiContext.getString(R.string.success_registered_shared))
        } catch (e: FirebaseFirestoreException) {
            DataResponse.ExceptionError(e)
        } catch (e: Exception) {
            DataResponse.ExceptionError(e)
        }
    }

    override suspend fun getDetailPlace(departmentName:String,placeName: String): DataResponse<Places> {
        return try {
            val result = firebaseFirestore.collection("Department")
                .document(departmentName).collection("TouristDestination")
                .document(placeName).get().await()
            DataResponse.Success(PlacesMapper().mapToEntity(result.toObject(PlacesServer::class.java)))
        } catch (e: FirebaseFirestoreException) {
            DataResponse.ExceptionError(e)
        } catch (e: Exception) {
            DataResponse.ExceptionError(e)
        }
    }

    override suspend fun getDetailUserPublishedPlace(userId: String?): DataResponse<User> {
        return try {
            val result = firebaseFirestore.collection("Users")
                .document(userId.toString()).get().await()
            DataResponse.Success(UserMapper().mapToEntity(result.toObject(UserServer::class.java)))
        } catch (e: FirebaseFirestoreException) {
            DataResponse.ExceptionError(e)
        } catch (e: Exception) {
            DataResponse.ExceptionError(e)
        }
    }
}