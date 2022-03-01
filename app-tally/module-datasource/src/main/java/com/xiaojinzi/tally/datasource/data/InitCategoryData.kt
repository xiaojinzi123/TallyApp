package com.xiaojinzi.tally.datasource.data

import androidx.annotation.Keep
import com.xiaojinzi.tally.base.service.datasource.TallyCategoryGroupTypeDTO
import com.xiaojinzi.tally.datasource.R
import com.xiaojinzi.module.base.support.ResData

@Keep
data class TallyCategoryGroupInitDTO(
    val type: TallyCategoryGroupTypeDTO,
    val iconRsdIndex: Int,
    val nameRsdIndex: Int,
    val items: List<TallyCategoryInitDTO> = emptyList()
)

@Keep
data class TallyCategoryInitDTO(
    val iconIndex: Int,
    val nameIndex: Int,
)

// 类别下标的集合
val categoryGroupInitRsdIndexList = listOf(
    TallyCategoryGroupInitDTO(
        type = TallyCategoryGroupTypeDTO.Income,
        iconRsdIndex = ResData.getIconRsdIndex(
            rsdId = R.drawable.res_income1
        ),
        nameRsdIndex = ResData.getNameRsdIndex(
            rsdId = R.string.res_str_income
        ),
        items = listOf(
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_question_mark1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_other
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_card2
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_salary
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_money2
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_prize
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_trophy1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_winning
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_income2
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_financial
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_heart1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_subsidies
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_money3
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_reimburse
                )
            ),
        )
    ),
    TallyCategoryGroupInitDTO(
        type = TallyCategoryGroupTypeDTO.Spending,
        iconRsdIndex = ResData.getIconRsdIndex(
            rsdId = R.drawable.res_question_mark1
        ),
        nameRsdIndex = ResData.getNameRsdIndex(
            rsdId = R.string.res_str_other
        ),
        items = listOf(
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_question_mark1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_other
                )
            ),
        )
    ),
    TallyCategoryGroupInitDTO(
        type = TallyCategoryGroupTypeDTO.Spending,
        iconRsdIndex = ResData.getIconRsdIndex(
            rsdId = R.drawable.res_card1
        ),
        nameRsdIndex = ResData.getNameRsdIndex(
            rsdId = R.string.res_str_shopping_consumption
        ),
        items = listOf(
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_question_mark1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_other
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_paper1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_daily_household
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_makeup1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_beauty_products
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_camera2
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_digital_products
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_money1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_virtual_top_up
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_washing_machine1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_daily_life_electrical_appliances
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_watch2
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_accessories
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_bottle1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_infant_mom_toys
                )
            ),
        )
    ),
    TallyCategoryGroupInitDTO(
        type = TallyCategoryGroupTypeDTO.Spending,
        iconRsdIndex = ResData.getIconRsdIndex(
            rsdId = R.drawable.res_eat4
        ),
        nameRsdIndex = ResData.getNameRsdIndex(
            rsdId = R.string.res_str_eat
        ),
        items = listOf(
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_question_mark1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_other
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_bottle2
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_daily_necessities
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_eat1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_breakfast
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_eat2
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_lunch
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_eat3
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_dinner
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_drinks1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_drinks
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_lollipop1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_snacks
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_bread1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_fresh_products
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_eat4
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_dinners
                )
            ),
        )
    ),
    TallyCategoryGroupInitDTO(
        type = TallyCategoryGroupTypeDTO.Spending,
        iconRsdIndex = ResData.getIconRsdIndex(
            rsdId = R.drawable.res_traffic1
        ),
        nameRsdIndex = ResData.getNameRsdIndex(
            rsdId = R.string.res_str_travel_traffic
        ),
        items = listOf(
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_question_mark1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_other
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_car1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_taxi
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_bus1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_public_transit
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_park2
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_parking
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_oiling1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_oiling
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_train1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_train
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_plane1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_plane
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_maintenance1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_maintenance
                )
            ),
        )
    ),
    TallyCategoryGroupInitDTO(
        type = TallyCategoryGroupTypeDTO.Spending,
        iconRsdIndex = ResData.getIconRsdIndex(
            rsdId = R.drawable.res_game1
        ),
        nameRsdIndex = ResData.getNameRsdIndex(
            rsdId = R.string.res_str_entertainment
        ),
        items = listOf(
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_question_mark1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_other
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_travel1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_travel
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_movie1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_movie_sing
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_sport1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_sport_fitness
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_massage1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_massage
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_chess1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_chess_cartagena
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_drinks2
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_bar
                )
            ),
        )
    ),
    TallyCategoryGroupInitDTO(
        type = TallyCategoryGroupTypeDTO.Spending,
        iconRsdIndex = ResData.getIconRsdIndex(
            rsdId = R.drawable.res_category1
        ),
        nameRsdIndex = ResData.getNameRsdIndex(
            rsdId = R.string.res_str_life
        ),
        items = listOf(
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_question_mark1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_other
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_telephone1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_phone_bill
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_electricity1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_electricity_bill
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_water2
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_water_bill
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_fire2
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_gas_bill
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_house3
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_property_bill
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_park3
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_parking_space_fee
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_clean1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_housekeeping_cleaning
                )
            ),
        )
    ),
    TallyCategoryGroupInitDTO(
        type = TallyCategoryGroupTypeDTO.Spending,
        iconRsdIndex = ResData.getIconRsdIndex(
            rsdId = R.drawable.res_education1
        ),
        nameRsdIndex = ResData.getNameRsdIndex(
            rsdId = R.string.res_str_cultural_education
        ),
        items = listOf(
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_question_mark1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_other
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_hat1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_tuition_fee
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_learning1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_training
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_book2
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_book
                )
            ),
        )
    ),
    TallyCategoryGroupInitDTO(
        type = TallyCategoryGroupTypeDTO.Spending,
        iconRsdIndex = ResData.getIconRsdIndex(
            rsdId = R.drawable.res_gift1
        ),
        nameRsdIndex = ResData.getNameRsdIndex(
            rsdId = R.string.res_str_gifts_favor
        ),
        items = listOf(
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_question_mark1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_other
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_lend1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_lend
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_love1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_honor_your_elders
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_gift1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_gifts
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_money4
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_red_packet
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_money6
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_reward
                )
            ),
        )
    ),
    TallyCategoryGroupInitDTO(
        type = TallyCategoryGroupTypeDTO.Spending,
        iconRsdIndex = ResData.getIconRsdIndex(
            rsdId = R.drawable.res_heart1
        ),
        nameRsdIndex = ResData.getNameRsdIndex(
            rsdId = R.string.res_str_health_care
        ),
        items = listOf(
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_question_mark1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_other
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_tea1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_health_care1
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_hospital1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_hospital
                )
            ),
            TallyCategoryInitDTO(
                iconIndex = ResData.getIconRsdIndex(
                    rsdId = R.drawable.res_medicine1
                ),
                nameIndex = ResData.getNameRsdIndex(
                    rsdId = R.string.res_str_buy_medicine
                )
            ),
        )
    ),
)
