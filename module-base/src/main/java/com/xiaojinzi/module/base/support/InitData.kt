package com.xiaojinzi.module.base.support

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.xiaojinzi.module.base.bean.toStringItemDTO
import com.xiaojinzi.wp.res.R

object ResData {

    // 所有内置 Icon 下标的集合, 这个集合的顺序一定不能更改
    // 新的资源一定要往下顺序的添加
    val resIconIndexList = listOf(
        R.drawable.res_paper1,
        R.drawable.res_makeup1,
        R.drawable.res_camera1,
        R.drawable.res_camera2,
        R.drawable.res_money1,
        R.drawable.res_washing_machine1,
        R.drawable.res_watch2,
        R.drawable.res_bottle1,
        R.drawable.res_bottle2,
        R.drawable.res_dress1,
        R.drawable.res_pet1,
        R.drawable.res_office1,
        R.drawable.res_decorate1,
        R.drawable.res_eat1,
        R.drawable.res_eat2,
        R.drawable.res_eat3,
        R.drawable.res_drinks1,
        R.drawable.res_lollipop1,
        R.drawable.res_bread1,
        R.drawable.res_restaurant1,
        R.drawable.res_bus1,
        R.drawable.res_cart1,
        R.drawable.res_park1,
        R.drawable.res_travel1,
        R.drawable.res_maintenance1,
        R.drawable.res_plane1,
        R.drawable.res_train1,
        R.drawable.res_oiling1,
        R.drawable.res_movie1,
        R.drawable.res_chess1,
        R.drawable.res_sport1,
        R.drawable.res_drinks2,
        R.drawable.res_poker1,
        R.drawable.res_bath1,
        R.drawable.res_house1,
        R.drawable.res_house2,
        R.drawable.res_bulb1,
        R.drawable.res_fire1,
        R.drawable.res_phone1,
        R.drawable.res_water1,
        R.drawable.res_park2,
        R.drawable.res_clean1,
        R.drawable.res_card1,
        R.drawable.res_card2,
        R.drawable.res_eat4,
        R.drawable.res_traffic1,
        R.drawable.res_game1,
        R.drawable.res_category1,
        R.drawable.res_education1,
        R.drawable.res_gift1,
        R.drawable.res_heart1,
        R.drawable.res_add1,
        R.drawable.res_car1,
        R.drawable.res_massage1,
        R.drawable.res_income1,
        R.drawable.res_income2,
        R.drawable.res_money2,
        R.drawable.res_trophy1,
        R.drawable.res_money3,
        R.drawable.res_transfer1,
        R.drawable.res_telephone1,
        R.drawable.res_electricity1,
        R.drawable.res_water2,
        R.drawable.res_fire2,
        R.drawable.res_house3,
        R.drawable.res_park3,
        R.drawable.res_hat1,
        R.drawable.res_learning1,
        R.drawable.res_book2,
        R.drawable.res_love1,
        R.drawable.res_love2,
        R.drawable.res_lend1,
        R.drawable.res_money4,
        R.drawable.res_money5,
        R.drawable.res_money6,
        R.drawable.res_menu1,
        R.drawable.res_tea1,
        R.drawable.res_tea2,
        R.drawable.res_hospital1,
        R.drawable.res_medicine1,
        R.drawable.res_question_mark1,
        R.drawable.res_setting1,
        R.drawable.res_auto1,
        R.drawable.res_alipay1,
        R.drawable.res_alipay2,
        R.drawable.res_wechat1,
        R.drawable.res_wechat2,
        R.drawable.res_qq1,
        R.drawable.res_qq2,
        R.drawable.res_yunshanfu1,
        R.drawable.res_eleme1,
        R.drawable.res_eleme2,
        R.drawable.res_meituan1,
        R.drawable.res_jd1,
        R.drawable.res_jd2,
        R.drawable.res_jd3,
        R.drawable.res_taobao1,
        R.drawable.res_pinduoduo1,
        R.drawable.res_douyin1,
        R.drawable.res_douyin2,
        R.drawable.res_bank1,
        R.drawable.res_bank2,
        R.drawable.res_bank3,
        R.drawable.res_bank4,
        R.drawable.res_bank5,
        R.drawable.res_bank6,
        R.drawable.res_bank7,
    )

    val resIconCategoryList = listOf(
        R.string.res_str_common_use.toStringItemDTO() to listOf(
            R.drawable.res_douyin1,
            R.drawable.res_douyin2,
            R.drawable.res_pinduoduo1,
            R.drawable.res_taobao1,
            R.drawable.res_jd1,
            R.drawable.res_jd2,
            R.drawable.res_jd3,
            R.drawable.res_meituan1,
            R.drawable.res_eleme1,
            R.drawable.res_eleme2,
            R.drawable.res_yunshanfu1,
            R.drawable.res_qq1,
            R.drawable.res_qq2,
            R.drawable.res_alipay1,
            R.drawable.res_alipay2,
            R.drawable.res_wechat1,
            R.drawable.res_wechat2,
            R.drawable.res_card1,
            R.drawable.res_card2,
            R.drawable.res_transfer1,
            R.drawable.res_money1,
            R.drawable.res_money2,
            R.drawable.res_money3,
            R.drawable.res_money4,
            R.drawable.res_money5,
            R.drawable.res_money6,
            R.drawable.res_house1,
        ),
        R.string.res_str_bank.toStringItemDTO() to listOf(
            R.drawable.res_bank1,
            R.drawable.res_bank2,
            R.drawable.res_bank3,
            R.drawable.res_bank4,
            R.drawable.res_bank5,
            R.drawable.res_bank6,
            R.drawable.res_bank7,
        ),
        R.string.res_str_life.toStringItemDTO() to listOf(
            R.drawable.res_drinks1,
            R.drawable.res_drinks2,
            R.drawable.res_tea1,
            R.drawable.res_tea2,
            R.drawable.res_massage1,
            R.drawable.res_dress1,
            R.drawable.res_pet1,
            R.drawable.res_office1,
            R.drawable.res_eat1,
            R.drawable.res_eat2,
            R.drawable.res_eat3,
            R.drawable.res_drinks1,
            R.drawable.res_lollipop1,
            R.drawable.res_bread1,
            R.drawable.res_restaurant1,
        ),
    )

    // 所有内置 String 下标的集合, 这个集合的顺序一定不能更改
    // 新的资源一定要往下顺序的添加
    val resStringIndexList = listOf(
        R.string.res_str_shopping_consumption,
        R.string.res_str_eat,
        R.string.res_str_travel_traffic,
        R.string.res_str_entertainment,
        R.string.res_str_life,
        R.string.res_str_cultural_education,
        R.string.res_str_gifts_favor,
        R.string.res_str_health_care,
        R.string.res_str_other,
        R.string.res_str_daily_household,
        R.string.res_str_digital_products,
        R.string.res_str_virtual_top_up,
        R.string.res_str_daily_life_electrical_appliances,
        R.string.res_str_accessories,
        R.string.res_str_infant_mom_toys,
        R.string.res_str_beauty_products,
        R.string.res_str_breakfast,
        R.string.res_str_lunch,
        R.string.res_str_dinner,
        R.string.res_str_drinks,
        R.string.res_str_snacks,
        R.string.res_str_fresh_products,
        R.string.res_str_dinners,
        R.string.res_str_daily_necessities,
        R.string.res_str_taxi,
        R.string.res_str_public_transit,
        R.string.res_str_parking,
        R.string.res_str_oiling,
        R.string.res_str_train,
        R.string.res_str_plane,
        R.string.res_str_maintenance,
        R.string.res_str_travel,
        R.string.res_str_movie_sing,
        R.string.res_str_sport_fitness,
        R.string.res_str_massage,
        R.string.res_str_chess_cartagena,
        R.string.res_str_bar,
        R.string.res_str_performance,
        R.string.res_str_income,
        R.string.res_str_salary,
        R.string.res_str_winning,
        R.string.res_str_prize,
        R.string.res_str_financial,
        R.string.res_str_subsidies,
        R.string.res_str_reimburse,
        R.string.res_str_default_bill_book,
        R.string.res_str_transfer,
        R.string.res_str_default_account,
        R.string.res_str_normal_account,
        R.string.res_str_capital_account,
        R.string.res_str_credit_account,
        R.string.res_str_top_up_account,
        R.string.res_str_financial_account,
        R.string.res_str_asset_account,
        R.string.res_str_phone_bill,
        R.string.res_str_electricity_bill,
        R.string.res_str_water_bill,
        R.string.res_str_gas_bill,
        R.string.res_str_property_bill,
        R.string.res_str_parking_space_fee,
        R.string.res_str_housekeeping_cleaning,
        R.string.res_str_tuition_fee,
        R.string.res_str_training,
        R.string.res_str_book,
        R.string.res_str_honor_your_elders,
        R.string.res_str_gifts,
        R.string.res_str_lend,
        R.string.res_str_red_packet,
        R.string.res_str_reward,
        R.string.res_str_health_care1,
        R.string.res_str_hospital,
        R.string.res_str_buy_medicine,
        R.string.res_str_travel_book,
        R.string.res_str_business_book,
        R.string.res_str_baby_book,
        R.string.res_str_auto_bill,
        R.string.res_str_alipay,
        R.string.res_str_wechat,
        R.string.res_str_ysf,
        R.string.res_str_infer_bill_type,
    )

    fun getIconRsdIndex(@DrawableRes rsdId: Int): Int {
        val result = resIconIndexList.indexOf(element = rsdId)
        if (result == -1) {
            error("Not Support")
        }
        return result
    }

    fun getNameRsdIndex(@StringRes rsdId: Int): Int {
        val result = resStringIndexList.indexOf(element = rsdId)
        if (result == -1) {
            error("Not Support")
        }
        return result
    }

    init {

    }

}