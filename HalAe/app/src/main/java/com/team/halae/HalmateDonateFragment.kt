package com.team.halae

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Spinner
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.team.halae.R.id.donate_halmate_img
import retrofit2.Response

class HalmateDonateFragment() : Fragment(), AdapterView.OnItemSelectedListener, View.OnClickListener {

    lateinit var donateRecyclerView: RecyclerView
    lateinit var donateList: ArrayList<DonateListItem>
    lateinit var donateAdapter: DonateListAdapter
    lateinit var mGlideRequestManager: RequestManager

    private var contentArry : ArrayList<String> = ArrayList()
    private var startdayArry : ArrayList<String> = ArrayList()
    private var enddayArray : ArrayList<String> = ArrayList()

    var alignspinner: Spinner? = null
    var align: Int? = null
    var halnameandage: String? = null
    private var networkService: NetworkService? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_hamate_donate, container, false)

        networkService = ApplicationController.instance!!.networkService
        donateRecyclerView = v.findViewById(R.id.donate_recyclerview)
        mGlideRequestManager = Glide.with(this)

        //------------------정렬 스피너------------------------
        alignspinner = v.findViewById(R.id.donate_align_spinner)
        alignspinner!!.setSelection(0)
        alignspinner!!.setOnItemSelectedListener(this)

        //------------------------------기부글 recyclerview---------------------------------
        donateList = ArrayList<DonateListItem>()
        donateRecyclerView!!.layoutManager =  LinearLayoutManager(context)
        donateRecyclerView!!.setOnClickListener(this)
        return v


    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

        var alignstr = alignspinner!!.selectedItem.toString()
        Log.v("sylee", alignstr)

        if( alignstr == "마감임박순") align = 0
        else if(alignstr == "최신순") align = 1
        else if(alignstr == "모금액적은순") align = 2
        else if(alignstr == "모금액많은순") align = 3


        var getdonatelist = networkService!!.getdonateList(align!!)

        getdonatelist.enqueue(object : retrofit2.Callback<DonateListResponse> {

            override fun onFailure(call: retrofit2.Call<DonateListResponse>?, t: Throwable?) {

                Log.v("sylee", "donate list onfailure")
                Log.v("sylee", t.toString())
            }


            override fun onResponse(call: retrofit2.Call<DonateListResponse>?, response: Response<DonateListResponse>?) {
                if (response!!.isSuccessful) {
                    if (response.body().message.equals("get donate list Success")) {

                        donateList.clear()
                        Log.v("sylee", response.body().toString())

                        //결과 값 array 에 저장
                        var resultarry = response.body().result
                        var resultlen = resultarry.size


                        for (i in 0..(resultlen-1)) {


                            if (response.body().result[i].hal_gender == 0) {
                                halnameandage = ""
                                halnameandage = response.body().result[i].hal_name + " 할아버지(" + response.body().result[i].hal_age.toString() + ")"
                            } else if (response.body().result[i].hal_gender == 1) {
                                halnameandage = ""
                                halnameandage = response.body().result[i].hal_name + " 할아버지(" + response.body().result[i].hal_age.toString() + ")"
                            }

                            var goalmoney = "모금 목표 금액 : " + response.body().result[i].goal_money.toString() + "원"
                            var nowmoney = response.body().result[i].now_money.toString() + " 원"

                            var nowmoneynum : Float = response.body().result[i].now_money.toFloat()
                            var goalmoneynum : Float = response.body().result[i].goal_money.toFloat()

                            Log.v("sylee", nowmoneynum.toString())
                            Log.v("sylee", goalmoneynum.toString())

                            var percentagenum  : Float = ((nowmoneynum / goalmoneynum) * 100)
                            Log.v("sylee" , (nowmoneynum / goalmoneynum).toString())
                            Log.v("sylee", percentagenum.toString())


                            var percentage = percentagenum.toInt().toString() + "%"
                            Log.v("sylee", percentage)
                            var leftdays = "종료 6일 전"
                            var progressbar : ProgressBar = ProgressBar(context)

                            Log.v("sylee", response.body().result[0].don_text)
                            contentArry.add(response.body().result[i].don_text)
                            startdayArry.add(response.body().result[i].start_date)
                            Log.v("sylee", response.body().result[i].finish_date)
                            enddayArray.add(response.body().result[i].finish_date)


                            var tempDonateItem : DonateListItem
                                    = DonateListItem(response.body().result[i].hal_img, response.body().result[i].don_title,
                                    halnameandage!!, goalmoney, nowmoney, progressbar ,percentage, leftdays)

                            donateList.add(tempDonateItem)
                            donateAdapter = DonateListAdapter(donateList, mGlideRequestManager)
                            donateRecyclerView!!.adapter = donateAdapter
                            donateAdapter!!.setOnItemClickListner(this@HalmateDonateFragment)

                        }

                    }
                }
            }
        })
    }

    override fun onClick(v: View?) {
        Log.v("sylee","들어오니??")
        //클릭한 항목의 인덱스를 저장
        val idx: Int = donateRecyclerView!!.getChildAdapterPosition(v!!)

        val img: String? = donateList[idx].donateimg
        val title: String? = donateList[idx].donatetitle
        val content : String? = contentArry[idx]
        val name: String? = donateList[idx].donatehalmatename
        val goalmoney: String? = donateList[idx].donategoalmoney
        val nowmoney: String? = donateList[idx].donatenowmoney
        val startdate : String? = startdayArry[idx]
        val enddate : String? = enddayArray[idx]
        Log.v("sysy", enddate)
        val percent: String? = donateList[idx].donatepercent
        val leftdays: String? = donateList[idx].donateleftday

        Log.v("sylee", donateList[idx].toString())

        val intent = Intent(activity, DonateDatailActivity::class.java)

        intent.putExtra("img", img)
        intent.putExtra("title", title)
        intent.putExtra("name", name)
        intent.putExtra("content", content)
        intent.putExtra("goalmoney", goalmoney)
        intent.putExtra("nowmoney", nowmoney)
        intent.putExtra("startdate", startdate)
        intent.putExtra("enddaate", enddate)
        intent.putExtra("percent", percent)
        intent.putExtra("leftdays", leftdays)

        startActivity(intent)
    }
}

