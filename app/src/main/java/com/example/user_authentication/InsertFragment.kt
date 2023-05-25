package com.example.user_authentication

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.example.user_authentication.Model.GetUser
import com.example.user_authentication.Server.MasterApplication
import com.example.user_authentication.databinding.ActivityLoginBinding
import com.example.user_authentication.databinding.FragmentInsertBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import kotlin.math.log

class InsertFragment : Fragment() {
    private var _binding: FragmentInsertBinding? = null
    private lateinit var callback : OnBackPressedCallback
    private lateinit var loginActivity: LoginActivity
    private val binding get() = _binding!!
    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginActivity = context as LoginActivity
        callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                goBack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this,callback)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInsertBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.back.setOnClickListener {
            goBack()
        }

        binding.insert.setOnClickListener {
            if (getUserName()==""){
                Toast.makeText(loginActivity,
                    "Enter Id", Toast.LENGTH_SHORT)
                    .show()
            }else if(getUserPw()==""){
                Toast.makeText(loginActivity,
                    "Enter password", Toast.LENGTH_SHORT)
                    .show()
            }else if(getUserRePw()==""){
                Toast.makeText(loginActivity,
                    "Enter your password one more", Toast.LENGTH_SHORT)
                    .show()
            }else if(getUserPw()!=getUserRePw()){
                Toast.makeText(loginActivity,
                    "Password is wrong", Toast.LENGTH_SHORT)
                    .show()
            }else{
                register()
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun register(){
        val masterApp = (loginActivity.application as MasterApplication)
        masterApp.apply {
            createRetrofit(loginActivity)
            service?.let {
                it.registerUser(
                    getUserName(),getUserPw()
                ).enqueue(object : Callback<GetUser> {
                    override fun onResponse(call: Call<GetUser>, response: Response<GetUser>) {
                        if (response.isSuccessful){
                            goBack()
                        }else{
                            fallLogin(response.errorBody()!!.string())
                        }
                    }

                    override fun onFailure(call: Call<GetUser>, t: Throwable) {
                        fallLogin("server error")
                    }

                })
            }
        }

    }

    private fun fallLogin(message:String){
        Toast.makeText(loginActivity,
            message.substring(0,10), Toast.LENGTH_SHORT)
            .show()
    }

    // go activity
    private fun goBack(){
        loginActivity.supportFragmentManager
            .beginTransaction()
            .remove(this@InsertFragment)
            .commit()
    }

    private fun getUserName() : String{
        return binding.userName.text.toString()
    }

    private fun getUserPw() : String{
        return binding.pw.text.toString()
    }

    private fun getUserRePw() : String{
        return binding.rePw.text.toString()
    }

}