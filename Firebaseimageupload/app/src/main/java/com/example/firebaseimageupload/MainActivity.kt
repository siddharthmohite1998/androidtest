package com.example.firebaseimageupload

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.firebaseimageupload.databinding.ActivityMainBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class MainActivity : AppCompatActivity() {


    private lateinit var upload:Button
    private lateinit var dbRef:DatabaseReference
    private lateinit var storageRef: StorageReference
    private lateinit var submit:Button
    private lateinit var comments:EditText

     val ImageBack=1

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        submit=this.findViewById(R.id.submit)
        comments=this.findViewById(R.id.comments)

        binding.upload.setOnClickListener{
            val intent= Intent(Intent.ACTION_GET_CONTENT)
            intent.setType("image/*")
            startActivityForResult(intent,ImageBack)
        }

        storageRef= FirebaseStorage.getInstance().reference.child("Images")

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        submit.setOnClickListener {

            val comments=comments.text.toString()

            if (requestCode == ImageBack) {
                if (resultCode == RESULT_OK) {
                    val imageData = data!!.getData()
                    val imageName: StorageReference =
                        storageRef.child("images" + imageData?.lastPathSegment)
                    imageName.putFile(imageData!!)
                        .addOnSuccessListener(OnSuccessListener { taskSnapshot ->
                            Log.e("inside", "inside upload image")
                            imageName.getDownloadUrl()
                                .addOnSuccessListener(OnSuccessListener<Uri> { uri ->
                                    Log.e("inside", "inside the downloadurl")
                                    val databaseReference: DatabaseReference =
                                        FirebaseDatabase.getInstance()
                                            .getReferenceFromUrl("https://camera-intent-402d8-default-rtdb.firebaseio.com")
                                            .child("Image")

                                    val hashMap: HashMap<String, String> = HashMap()
                                    val userId =databaseReference.push().key!!
                                    val imageUri= uri.toString()
                                    Log.e("uri", uri.toString())
                                    hashMap.put("imageUrl", uri.toString())
                                    hashMap.put("comments",comments)
                                    databaseReference.child("Posts").child(userId).setValue(hashMap)
                                    Toast.makeText(
                                        this,
                                        "data inserted in realtime database",
                                        Toast.LENGTH_LONG
                                    ).show()

                                })
                        })

                }
            }
        }
    }
}