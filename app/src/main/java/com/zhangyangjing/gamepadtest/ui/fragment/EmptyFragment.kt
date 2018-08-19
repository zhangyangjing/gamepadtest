package com.zhangyangjing.gamepadtest.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zhangyangjing.gamepadtest.R
import kotlinx.android.synthetic.main.empty_fragment.*


class EmptyFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.empty_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_info.text = arguments?.getString(ARG_MESSAGE)
    }

   companion object {
       const val ARG_MESSAGE = "message"

       fun ins(message: String): EmptyFragment {

           val bundle = Bundle()
           bundle.putString(ARG_MESSAGE, message)

           val fragment = EmptyFragment()
           fragment.arguments = bundle

           return fragment
       }
   }
}
