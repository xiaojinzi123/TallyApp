// IBill.aidl
package com.xiaojinzi.tally.base;
import com.xiaojinzi.tally.base.service.datasource.TallyAccountDTO;
import com.xiaojinzi.tally.base.service.datasource.TallyCategoryDTO;

// Declare any non-default types here with import statements

interface IBill {

    /**
     * 添加一个账单
     *
     * @param appSourcceType 账单的来源
     * @param cost           费用
     * @param note           备注
     * @param orderNumber    订单号
     */
    void addBill(
            /**
             * {@link com.xiaojinzi.lib.res.dto.AppSourceType}
             */
            int appSourceType,
            float cost,
            long time,
            String note,
            String orderNumber
    );

    /**
     * 获取默认账户
     */
    TallyAccountDTO getDefaultAccount(int sourceViewType);

    /**
     * 获取默认类别
     */
    TallyCategoryDTO getDefaultCategory(int sourceViewType);

}