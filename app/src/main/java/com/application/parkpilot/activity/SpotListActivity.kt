package com.application.parkpilot.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.parkpilot.R
import com.application.parkpilot.adapter.SpotListRecyclerView
import com.application.parkpilot.viewModel.SpotListViewModel

class SpotListActivity : AppCompatActivity(R.layout.spot_list) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val viewModel = ViewModelProvider(this)[SpotListViewModel::class.java]
        viewModel.loadStation()
        viewModel.liveDataStationLocation.observe(this) {
            it?.let {
                recyclerView.layoutManager = LinearLayoutManager(this)
                recyclerView.adapter = SpotListRecyclerView(this, R.layout.spot_preview, it)
            }
        }
    }
}