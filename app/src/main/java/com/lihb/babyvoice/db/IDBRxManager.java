package com.lihb.babyvoice.db;

import java.util.List;

import rx.Observable;


/**
 * Created by lihb on 2017/3/17.
 */

public interface IDBRxManager<T> {


    /**
     * 插入单个数据
     * @param t
     * @return
     */
    Observable<Boolean> insertData(final T t);

    /**
     * 批量插入数据
     * @param dataList
     * @return
     */
    Observable<Void> batchInsertData(final List<T> dataList);

    /**
     * 查询所有数据
     * @return
     */
    Observable<List<T>> queryAllData();


    /**
     * 查询一条数据
     * @return
     */
    Observable<Boolean> queryData(final T t);

    /**
     * 删除数据
     * @param t
     * @return
     */
    Observable<Boolean> delData(final T t);

    /**
     * 更新数据
     * @param t
     * @return
     */
    Observable<Boolean> updateData(final T t);




}
