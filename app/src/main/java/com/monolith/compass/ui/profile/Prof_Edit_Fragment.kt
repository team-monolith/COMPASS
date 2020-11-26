package com.monolith.compass.ui.profile

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.CheckResult
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.monolith.compass.R


class Prof_Edit_Fragment : Fragment() {
    private lateinit var profileViewModel: ProfileViewModel




    //////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////

    private final var KEY_NAME="key_name"
    private final var KEY_BACKGROUND="key_background_color"

    @CheckResult
    open fun createInstance(name: String?, @ColorInt color: Int): Prof_Edit_Fragment? {
        // Fragmentを作成して返すメソッド
        // createInstanceメソッドを使用することで、そのクラスを作成する際にどのような値が必要になるか制約を設けることができる
        val fragment = Prof_Edit_Fragment()
        // Fragmentに渡す値はBundleという型でやり取りする
        val args = Bundle()
        // Key/Pairの形で値をセットする
        args.putString(KEY_NAME, name)
        args.putInt(KEY_BACKGROUND, color)
        // Fragmentに値をセットする
        fragment.setArguments(args)
        return fragment
    }
    private var mName = ""
    @ColorInt
    private var mBackgroundColor: Int = Color.TRANSPARENT
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Bundleの値を受け取る際はonCreateメソッド内で行う
        val args = arguments
        // Bundleがセットされていなかった時はNullなのでNullチェックをする
        if (args != null) {
            // String型でNameの値を受け取る
            mName = args.getString(KEY_NAME).toString()
            // int型で背景色を受け取る
            mBackgroundColor = args.getInt(KEY_BACKGROUND, Color.TRANSPARENT)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // ラストに追加
        // 背景色をセットする
        view.setBackgroundColor(mBackgroundColor)
        // onCreateで受け取った値をセットする
        //mTextView.setText(mName)
        val text :TextView = view.findViewById(R.id.name_txt)
        text.setText(mName)
    }
    //////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_profile_edit, container, false)
        val textView: TextView = root.findViewById(R.id.txt_profile)
        val okbtn : Button = root.findViewById(R.id.combtn)
        val name_text :EditText = root.findViewById(R.id.name_txt)




        okbtn.setOnClickListener{
            //profileViewModel.test.value = name_text.text.toString()
            //okbtn.text = profileViewModel.test.value
            //profileViewModel.setValue(name_text.text.toString())
            //okbtn.text = profileViewModel.getValue()
            findNavController().navigate(R.id.action_navigation_profile_edit_to_navigation_profile)
        }
        return root
    }
}