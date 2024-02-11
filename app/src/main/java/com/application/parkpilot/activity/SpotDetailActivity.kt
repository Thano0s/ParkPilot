package com.application.parkpilot.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.application.parkpilot.R
import com.application.parkpilot.adapter.CarouselRecyclerView
import com.application.parkpilot.module.QRGenerator
import com.application.parkpilot.module.RazorPay
import com.application.parkpilot.view.DialogQRCode
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.razorpay.PaymentResultListener

class SpotDetailActivity: AppCompatActivity(R.layout.spot_detail), PaymentResultListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val recycleView: RecyclerView = findViewById(R.id.recycleView)
        val buttonBookNow: Button = findViewById(R.id.buttonBookNow)

        val arr: ArrayList<Any> = ArrayList()

        arr.add("https://images.pexels.com/photos/1545743/pexels-photo-1545743.jpeg?auto=compress&cs=tinysrgb&w=600")
        arr.add("https://images.pexels.com/photos/116675/pexels-photo-116675.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1")
        arr.add("https://images.pexels.com/photos/707046/pexels-photo-707046.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1")
        arr.add("https://images.pexels.com/photos/164634/pexels-photo-164634.jpeg?auto=compress&cs=tinysrgb&w=600")

        recycleView.layoutManager = CarouselLayoutManager()

        recycleView.adapter = CarouselRecyclerView(this,R.layout.square_carousel,arr)

        buttonBookNow.setOnClickListener {
            // this is temporary
            val razorpay = RazorPay(this)
            razorpay.makePayment("INR",1000,"123")
        }
    }

    override fun onPaymentSuccess(razorpayPaymentID: String) {
        Toast.makeText(this,"Payment successfully completed $razorpayPaymentID", Toast.LENGTH_SHORT).show()

        val QRCode = QRGenerator(this).generate(Firebase.auth.currentUser!!.uid)
        MaterialAlertDialogBuilder(this)
            .setView(DialogQRCode(this, QRCode, "This is a QR Code"))
            .show()
    }

    override fun onPaymentError(code: Int, response: String) {
        Toast.makeText(this,"Payment failed $response",Toast.LENGTH_SHORT).show()
    }
}