package com.monolith.compass.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.monolith.compass.MainActivity
import com.monolith.compass.R


class ProfileFragment : Fragment() {


    private lateinit var profileViewModel: ProfileViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        val textView: TextView = root.findViewById(R.id.name_txt)
        val imgCard: ImageView = root.findViewById(R.id.card_img)
        val button: Button =root.findViewById(R.id.button)
        val imgBtn: ImageButton = root.findViewById(R.id.imageButton2)
        val progressBar:ProgressBar =root.findViewById(R.id.progress_bar)


        val ma = activity as MainActivity?

        var value =ma?.SharedValue

        textView.text = value


        imgCard.setImageResource(R.drawable.ic_launcher_background)

        //imgBtn.setBackgroundResource(R.drawable.ic_launcher_background)


        imgCard.setOnClickListener {
            ma?.SharedValue = "こんばんは"
            findNavController().navigate(R.id.action_navigation_profile_to_navigation_profile_edit)
        }

        button.setOnClickListener {
            if(progressBar.progress  == 100){
                progressBar.progress = 0
            }else {
                progressBar.progress += 10
            }
        }


        imgBtn.setOnClickListener{
            //textView.text = testtext
            //test = textView.text.toString()
            findNavController().navigate(R.id.action_navigation_profile_to_navigation_profile_edit)

        }

        profileViewModel.test.observe(viewLifecycleOwner, Observer {
            //textView.text = profileViewModel.getValue()
        })

        return root
    }
}

/*
プログレスバー(役立)
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

livedataに関して(developers)
https://developer.android.com/topic/libraries/architecture/livedata?hl=ja

livedataに関して(参考文献)
https://toronavi.com/jetpack-livedata

databinding
https://toronavi.com/jetpack-databinding

fragment
https://qiita.com/m-coder/items/3a8e66d49f2830b09bf4


activityからfragmentへのデータの受け渡し
https://101010.fun/programming/android-try-fragment.html


データの受け渡し
https://aresei-note.com/1017
https://teratail.com/questions/114570
https://tech.mokelab.com/android/Fragment/argument.html
https://tkm0on.hatenablog.com/entry/2015/05/26/180655




fragment savedinstancestateについて
https://stackoverflow.com/questions/20550016/savedinstancestate-is-always-null-in-fragment

入力領域の空白自動削除
 val text = editText.text.toString().trim()

必要と思われるデータ
開拓ポイント（新旧２種類）
各バッジの獲得に必要なポイント
名刺の背景・フレーム
 */