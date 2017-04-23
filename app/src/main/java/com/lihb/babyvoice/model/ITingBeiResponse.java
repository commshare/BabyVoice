package com.lihb.babyvoice.model;

import java.util.List;

/**
 * Created by lihb on 2017/4/24.
 */

public class ITingBeiResponse<T> {

    public int endRow;
    public int startRow;
    public int firstPage;
    public boolean hasNextPage;
    public boolean hasPreviousPage;
    public boolean isFirstPage;
    public boolean isLastPage;
    public int lastPage;
    public List<T> list;
    public int navigatePages;
    public List navigatepageNums;
    public int nextPage;
    public int pageNum;
    public int pageSize;
    public int pages;
    public int prePage;
    public int size;
    public int total;




}
