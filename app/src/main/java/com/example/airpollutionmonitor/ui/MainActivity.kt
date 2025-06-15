@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.airpollutionmonitor.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.airpollutionmonitor.R
import com.example.airpollutionmonitor.data.Record
import com.example.airpollutionmonitor.repo.DataRepository
import com.example.airpollutionmonitor.viewmodel.ListState
import com.example.airpollutionmonitor.viewmodel.PollutedViewModel
import com.example.airpollutionmonitor.viewmodel.ViewModelFactory
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

class MainActivity : AppCompatActivity() {
    private lateinit var pollutedViewModel: PollutedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pollutedViewModel =
            ViewModelProvider(this, ViewModelFactory(DataRepository))[PollutedViewModel::class.java]
        setContent {
            PollutedScreen(pollutedViewModel)
        }
    }
}

@Composable
fun PollutedScreen(viewModel: PollutedViewModel) {
    val highInfo by viewModel.highInfo.collectAsStateWithLifecycle()
    val lowInfo by viewModel.lowInfo.collectAsStateWithLifecycle()
    val listState by viewModel.listState.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var showSnackbar by remember { mutableStateOf(false) }
    var isSearchMode by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val warningMessage = stringResource(R.string.normal_status_toast_text)

    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            snackbarHostState.showSnackbar(
                message = warningMessage
            )
            showSnackbar = false
        }
    }

    // Handle search state changes
    LaunchedEffect(isSearchMode, searchQuery) {
        if (!isSearchMode || searchQuery.isEmpty()) {
            viewModel.handleFilter(highInfo.size, "")
        } else {
            val filteredCount = highInfo.count { record ->
                record.sitename.contains(searchQuery, ignoreCase = true)
            }
            viewModel.handleFilter(filteredCount, searchQuery)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (isSearchMode) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    placeholder = { Text(stringResource(R.string.search_hint)) },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = {
                            isSearchMode = false
                            searchQuery = ""
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Close Search")
                        }
                    }
                )
            } else {
                TopAppBar(
                    title = { Text("Air Pollution Monitor") },
                    actions = {
                        IconButton(onClick = { isSearchMode = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (isSearchMode) {
                if (searchQuery.isEmpty()) {
                    // Show empty page when search bar is open and no input
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = stringResource(R.string.empty_result_text))
                    }
                } else {
                    // Show search result (found/not found)
                    SwipeRefresh(
                        state = rememberSwipeRefreshState(isRefreshing = listState is ListState.Refreshing),
                        onRefresh = { viewModel.getPollutedInfo() },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        when (listState) {
                            is ListState.Found -> {
                                LazyColumn(
                                    modifier = Modifier.padding(horizontal = 5.dp)
                                ) {
                                    itemsIndexed(highInfo.filter { record ->
                                        record.sitename.contains(searchQuery, ignoreCase = true)
                                    }) { idx, item ->
                                        HighPollutedItem(
                                            index = idx,
                                            record = item,
                                            onWarningClick = { showSnackbar = true }
                                        )
                                        Divider()
                                    }
                                }
                            }
                            is ListState.NotFound -> {
                                val keyword = (listState as? ListState.NotFound)?.keyword ?: ""
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stringResource(R.string.not_found_result_text)
                                            .format(keyword)
                                    )
                                }
                            }
                            is ListState.Refreshing -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                            is ListState.Timeout -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = stringResource(R.string.timeout_result_text))
                                }
                            }
                            is ListState.NoNetwork -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = stringResource(R.string.no_internet_text))
                                }
                            }
                            else -> {}
                        }
                    }
                }
            } else {
                SwipeRefresh(
                    state = rememberSwipeRefreshState(isRefreshing = listState is ListState.Refreshing),
                    onRefresh = { viewModel.getPollutedInfo() },
                    modifier = Modifier.fillMaxSize()
                ) {
                    when (listState) {
                        is ListState.ShowAll -> {
                            Column {
                                LazyRow(
                                    modifier = Modifier.padding(horizontal = 5.dp)
                                ) {
                                    itemsIndexed(lowInfo) { idx, item ->
                                        LowPollutedItem(idx, item)
                                    }
                                }
                                Spacer(Modifier.height(8.dp))
                                LazyColumn(
                                    modifier = Modifier.padding(horizontal = 5.dp)
                                ) {
                                    itemsIndexed(highInfo) { idx, item ->
                                        HighPollutedItem(
                                            index = idx,
                                            record = item,
                                            onWarningClick = { showSnackbar = true }
                                        )
                                        Divider()
                                    }
                                }
                            }
                        }
                        is ListState.Refreshing -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                        is ListState.Timeout -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = stringResource(R.string.timeout_result_text))
                            }
                        }
                        is ListState.NoNetwork -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = stringResource(R.string.no_internet_text))
                            }
                        }
                        else -> Unit // do nothing
                    }
                }
            }
        }
    }
}

@Composable
fun HighPollutedItem(index: Int, record: Record, onWarningClick: () -> Unit) {
    val context = LocalContext.current
    val isStateFine = record.status == stringResource(R.string.good_status)
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Number (ordinal)
        Text(
            text = (index + 1).toString(),
            modifier = Modifier
                .width(40.dp)
                .padding(start = 5.dp),
            textAlign = TextAlign.Center,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )
        
        // Site and county information
        Column(
            modifier = Modifier
                .padding(start = 5.dp, end = 5.dp)
        ) {
            Text(
                text = record.sitename,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = record.county,
                fontSize = 14.sp
            )
        }
        
        // County map image
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(getCountyImageUrl(record.county))
                .crossfade(true)
                .build(),
            contentDescription = "County map",
            modifier = Modifier
                .size(40.dp)
                .padding(vertical = 5.dp)
        )
        
        // PM2.5 value
        Text(
            text = record.pm25,
            modifier = Modifier.padding(start = 8.dp),
            fontSize = 14.sp
        )
        
        // Status text (always align right)
        Text(
            text = if (isStateFine) 
                stringResource(R.string.good_status_hint) 
            else 
                record.status,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            fontSize = 14.sp,
            textAlign = TextAlign.End,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        // Arrow button (show when status is not good)
        if (!isStateFine) {
            IconButton(
                onClick = onWarningClick,
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Warning",
                    tint = Color(0xFF888888),
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

@Composable
fun LowPollutedItem(index: Int, record: Record) {
    val context = LocalContext.current
    
    Card(
        modifier = Modifier
            .width(120.dp)
            .padding(6.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFDDDDDD))
    ) {
        Column(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Number (ordinal)
                Text(
                    text = (index + 1).toString(),
                    fontSize = 14.sp
                )
                
                // County
                Text(
                    text = record.county,
                    fontSize = 12.sp
                )
                
                // PM2.5 value
                Text(
                    text = record.pm25,
                    fontSize = 12.sp
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Site name
                Text(
                    text = record.sitename,
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )
                
                // Status
                Text(
                    text = record.status,
                    fontSize = 12.sp
                )
            }
        }
    }
}

// Copy county image URL mapping from GlideUtils
private fun getCountyImageUrl(county: String): String? {
    val countryUrlMap = hashMapOf(
        "臺北市" to "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c8/Taiwan_ROC_political_division_map_Taipei_City_%282010%29.svg/300px-Taiwan_ROC_political_division_map_Taipei_City_%282010%29.svg.png",
        "新北市" to "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3b/Taiwan_ROC_political_division_map_New_Taipei_City.svg/300px-Taiwan_ROC_political_division_map_New_Taipei_City.svg.png",
        "桃園市" to "https://upload.wikimedia.org/wikipedia/commons/thumb/c/ca/Taiwan_ROC_political_division_map_Taoyuan_County.svg/300px-Taiwan_ROC_political_division_map_Taoyuan_County.svg.png",
        "基隆市" to "https://upload.wikimedia.org/wikipedia/commons/thumb/6/63/Taiwan_ROC_political_division_map_Keelung_City.svg/300px-Taiwan_ROC_political_division_map_Keelung_City.svg.png",
        "新竹市" to "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7f/Taiwan_ROC_political_division_map_Hsinchu_City.svg/300px-Taiwan_ROC_political_division_map_Hsinchu_City.svg.png",
        "新竹縣" to "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d1/Taiwan_ROC_political_division_map_Hsinchu_County.svg/300px-Taiwan_ROC_political_division_map_Hsinchu_County.svg.png",
        "苗栗縣" to "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2a/Taiwan_ROC_political_division_map_Miaoli_County.svg/300px-Taiwan_ROC_political_division_map_Miaoli_County.svg.png",
        "宜蘭縣" to "https://upload.wikimedia.org/wikipedia/commons/thumb/6/69/Taiwan_ROC_political_division_map_Yilan_County.svg/300px-Taiwan_ROC_political_division_map_Yilan_County.svg.png",
        "臺中市" to "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fb/Taiwan_ROC_political_division_map_Taichung_City_%282010%29.svg/450px-Taiwan_ROC_political_division_map_Taichung_City_%282010%29.svg.png",
        "彰化縣" to "https://upload.wikimedia.org/wikipedia/commons/thumb/9/98/Taiwan_ROC_political_division_map_Changhua_County.svg/450px-Taiwan_ROC_political_division_map_Changhua_County.svg.png",
        "雲林縣" to "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6a/Taiwan_ROC_political_division_map_Yunlin_County.svg/450px-Taiwan_ROC_political_division_map_Yunlin_County.svg.png",
        "南投縣" to "https://upload.wikimedia.org/wikipedia/commons/thumb/0/02/Taiwan_ROC_political_division_map_Nantou_County.svg/450px-Taiwan_ROC_political_division_map_Nantou_County.svg.png",
        "花蓮縣" to "https://upload.wikimedia.org/wikipedia/commons/thumb/3/39/Taiwan_ROC_political_division_map_Hualien_County.svg/450px-Taiwan_ROC_political_division_map_Hualien_County.svg.png",
        "嘉義市" to "https://upload.wikimedia.org/wikipedia/commons/thumb/4/45/Taiwan_ROC_political_division_map_Chiayi_City.svg/450px-Taiwan_ROC_political_division_map_Chiayi_City.svg.png",
        "澎湖縣" to "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1a/Taiwan_ROC_political_division_map_Penghu_County.svg/450px-Taiwan_ROC_political_division_map_Penghu_County.svg.png",
        "嘉義縣" to "https://upload.wikimedia.org/wikipedia/commons/thumb/6/67/Taiwan_ROC_political_division_map_Chiayi_County.svg/450px-Taiwan_ROC_political_division_map_Chiayi_County.svg.png",
        "臺南市" to "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c0/Taiwan_ROC_political_division_map_Tainan_City_%282010%29.svg/450px-Taiwan_ROC_political_division_map_Tainan_City_%282010%29.svg.png",
        "高雄市" to "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fd/Taiwan_ROC_political_division_map_Kaohsiung_City_%282010%29.svg/450px-Taiwan_ROC_political_division_map_Kaohsiung_City_%282010%29.svg.png",
        "臺東縣" to "https://upload.wikimedia.org/wikipedia/commons/thumb/1/14/Taiwan_ROC_political_division_map_Taitung_County.svg/450px-Taiwan_ROC_political_division_map_Taitung_County.svg.png",
        "屏東縣" to "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0f/Taiwan_ROC_political_division_map_Pingtung_County.svg/450px-Taiwan_ROC_political_division_map_Pingtung_County.svg.png",
        "連江縣" to "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1b/Taiwan_ROC_political_division_map_Lienchiang_County.svg/450px-Taiwan_ROC_political_division_map_Lienchiang_County.svg.png",
        "金門縣" to "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2e/Taiwan_ROC_political_division_map_Kinmen_County.svg/450px-Taiwan_ROC_political_division_map_Kinmen_County.svg.png"
    )
    return countryUrlMap[county]
}