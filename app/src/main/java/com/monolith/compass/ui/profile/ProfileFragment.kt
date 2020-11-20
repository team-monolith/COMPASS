package com.monolith.compass.ui.profile

import com.monolith.compass.ui.profile.ProfileViewModel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.monolith.compass.R

class ProfileFragment : Fragment() {

    private lateinit var ProfileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ProfileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        val textView: TextView = root.findViewById(R.id.txt_profile)
        val card_img: ImageView = root.findViewById(R.id.card_img)

        card_img.setOnClickListener{

        }


        ProfileViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}


/*
プログレスバー
https://hirauchi-genta.com/kotlin-progressbarhorizontal/

丸ボタン(バッジ用)
https://kk90info.com/%E3%80%90mac%E3%83%BBandroid-studio%E3%80%91%E3%83%9C%E3%82%BF%E3%83%B3%E3%81%AE%E5%BD%A2%E3%82%92%E5%A4%89%E3%81%88%E3%81%A6%E3%81%BF%E3%82%8B/

BMIについて
https://ja.wikipedia.org/wiki/%E3%83%9C%E3%83%87%E3%82%A3%E3%83%9E%E3%82%B9%E6%8C%87%E6%95%B0

データについて
https://akira-watson.com/android/kotlin/internal-storage.html

ファイルのダウンロード(mainActivity)
https://woshidan.hatenablog.com/entry/2016/03/04/083000

起動時処理
https://qiita.com/suke/items/5f443e62810a3d3569b7

アクセス権
https://developer.android.com/training/permissions/requesting?hl=ja
パーミッション1
https://hiropoppo.hatenablog.com/entry/kotlin-recipe/permission#Permission%E3%81%A8%E3%81%AF
パーミッション2
https://symfoware.blog.fc2.com/blog-entry-2033.html



必要と思われるデータ
開拓ポイント（新旧２種類）
各バッジの獲得に必要なポイント
名刺の背景・フレーム

 */