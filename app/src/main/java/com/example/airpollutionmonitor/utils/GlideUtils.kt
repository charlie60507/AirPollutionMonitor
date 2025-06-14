package com.example.airpollutionmonitor.utils

import android.content.Context
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import timber.log.Timber

object GlideUtils {
    private val countryUrlMap = hashMapOf(
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

    fun loadIconByCountry(context: Context, imageView: ImageView, country: String) {
        loadIconByUrl(context, imageView, countryUrlMap[country])
    }

    private fun loadIconByUrl(context: Context, imageView: ImageView, url: String?) {
        if (url == null) {
            Timber.d("url is null")
            return
        }
        val loadingIcon = CircularProgressDrawable(context).apply {
            strokeWidth = 5f
            centerRadius = 30f
        }
        loadingIcon.start()
        Glide
            .with(context)
            .load(url)
            .placeholder(loadingIcon)
            .into(imageView)
    }
}